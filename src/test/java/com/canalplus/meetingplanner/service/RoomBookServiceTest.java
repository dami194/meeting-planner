package com.canalplus.meetingplanner.service;

import com.canalplus.meetingplanner.model.*;
import com.canalplus.meetingplanner.model.meeting.Meeting;
import com.canalplus.meetingplanner.model.meeting.MeetingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.*;

import static com.canalplus.meetingplanner.model.Equipment.*;
import static com.canalplus.meetingplanner.model.Equipment.BOARD;
import static com.canalplus.meetingplanner.model.TimeSlot.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoomBookServiceTest {

    @InjectMocks
    private RoomBookService roomBookService;

    @Mock
    private AllRooms allRooms;

    @Mock
    private RoomFinder roomFinder;

    private List<Room> rooms;

    @BeforeEach
    public void setup() {
        roomBookService.initRemovableEquipments();
    }

    @Test
    void should_removable_equipments_be_all_available_when_no_meeting_has_been_booked() {
        // Assert
        List<Equipment> removableEquipments = Arrays.asList(MULTILINE_SPEAKER, MULTILINE_SPEAKER, MULTILINE_SPEAKER, MULTILINE_SPEAKER,
                SCREEN, SCREEN, SCREEN, SCREEN, SCREEN, WEBCAM, WEBCAM, WEBCAM, WEBCAM, BOARD, BOARD);
        Arrays.stream(TimeSlot.values())
                .forEach(timeSlot ->
                    assertThat(roomBookService.getAvailableRemovableEquipmentsFor(timeSlot)).containsExactlyElementsOf(removableEquipments)
                );
    }

    @Test
    void cannot_book_room_for_meeting_when_no_available_room_for_timeSlot() {
        // Setup
        Room room1 = new Room("room1",4);
        Room room2 = new Room("room2",2);
        Room room3 = new Room("room3",14);
        Room room4 = new Room("room4",7);
        rooms = List.of(room1, room2, room3, room4);
        when(allRooms.getRooms()).thenReturn(rooms);
        when(roomFinder.findUnbookedRoomsAtTimeSlot(rooms, EIGHT_NINE)).thenReturn(Collections.emptyList());

        // Test
        Meeting meeting = new Meeting("réunion", EIGHT_NINE, MeetingType.VC, 5);
        RoomBookResult roomBookResult = roomBookService.bookRoomFor(meeting);

        // Assert
        assertThat(roomBookResult.getRoomBookStatus()).isEqualTo(RoomBookStatus.FAILURE);
        assertThat(roomBookResult.getRoom()).isNull();
    }

    @Test
    void cannot_book_room_for_meeting_when_all_rooms_have_been_booked_for_previous_slot() {
        // Setup
        Room room1 = new Room("room1",4);
        Room room2 = new Room("room2",2);
        Room room3 = new Room("room3",14);
        Room room4 = new Room("room4",7);
        rooms = List.of(room1, room2, room3, room4);
        when(allRooms.getRooms()).thenReturn(rooms);
        when(roomFinder.findUnbookedRoomsAtTimeSlot(rooms, NINE_TEN)).thenReturn(rooms);
        when(roomFinder.findUnbookedRoomsAtTimeSlot(rooms, EIGHT_NINE)).thenReturn(Collections.emptyList());

        // Test
        Meeting meeting = new Meeting("réunion", NINE_TEN, MeetingType.VC, 5);
        RoomBookResult roomBookResult = roomBookService.bookRoomFor(meeting);

        // Assert
        assertThat(roomBookResult.getRoomBookStatus()).isEqualTo(RoomBookStatus.FAILURE);
        assertThat(roomBookResult.getRoom()).isNull();
    }

    @Test
    void cannot_book_room_for_meeting_when_no_available_room_has_sufficient_capacity() {
        // Setup
        Room room1 = new Room("room1",4);
        Room room2 = new Room("room2",2);
        Room room3 = new Room("room3",14);
        Room room4 = new Room("room4",7);
        rooms = List.of(room1, room2, room3, room4);
        when(allRooms.getRooms()).thenReturn(rooms);
        when(roomFinder.findUnbookedRoomsAtTimeSlot(rooms, NINE_TEN)).thenReturn(rooms);
        when(roomFinder.findUnbookedRoomsAtTimeSlot(rooms, EIGHT_NINE)).thenReturn(rooms);
        when(roomFinder.findRoomsWithMinimumCapacity(rooms, 15)).thenReturn(Collections.emptyList());

        // Test
        Meeting meeting = new Meeting("réunion", NINE_TEN, MeetingType.VC, 15);
        RoomBookResult roomBookResult = roomBookService.bookRoomFor(meeting);

        // Assert
        assertThat(roomBookResult.getRoomBookStatus()).isEqualTo(RoomBookStatus.FAILURE);
        assertThat(roomBookResult.getRoom()).isNull();
    }

    @Test
    void room_booked_for_RS_meeting_should_be_the_first_available_with_the_least_equipments() {
        // Setup
        Room room1 = new Room("room1",6, Set.of(BOARD));
        Room room2 = new Room("room2",7);
        Room room3 = new Room("room3",8);
        Room room4 = new Room("room4",9, Set.of(BOARD, MULTILINE_SPEAKER));
        rooms = List.of(room1, room2, room3, room4);
        when(allRooms.getRooms()).thenReturn(rooms);
        when(roomFinder.findUnbookedRoomsAtTimeSlot(rooms, NINE_TEN)).thenReturn(rooms);
        when(roomFinder.findUnbookedRoomsAtTimeSlot(rooms, EIGHT_NINE)).thenReturn(rooms);
        when(roomFinder.findRoomsWithMinimumCapacity(rooms, 5)).thenReturn(rooms);

        // Test
        Meeting meeting = new Meeting("réunion", NINE_TEN, MeetingType.RS, 5);
        RoomBookResult roomBookResult = roomBookService.bookRoomFor(meeting);

        // Assert
        assertThat(roomBookResult.getRoomBookStatus()).isEqualTo(RoomBookStatus.SUCCESS);
        assertThat(roomBookResult.getRoom()).isEqualTo(room2);
        assertThat(room2.isBookedFor(NINE_TEN)).isTrue();
    }

    @Test
    void room_booked_for_SPEC_meeting_when_a_room_already_contains_BOARD() {
        // Setup
        Room room1 = new Room("room1",6, Set.of(BOARD, MULTILINE_SPEAKER));
        Room room2 = new Room("room2",7);
        Room room3 = new Room("room3",8);
        Room room4 = new Room("room4",9, Set.of(BOARD));
        rooms = List.of(room1, room2, room3, room4);
        when(allRooms.getRooms()).thenReturn(rooms);
        when(roomFinder.findUnbookedRoomsAtTimeSlot(rooms, NINE_TEN)).thenReturn(rooms);
        when(roomFinder.findUnbookedRoomsAtTimeSlot(rooms, EIGHT_NINE)).thenReturn(rooms);
        when(roomFinder.findRoomsWithMinimumCapacity(rooms, 5)).thenReturn(rooms);
        when(roomFinder.findRoomsWithSpecifiedEquipments(rooms, Set.of(BOARD))).thenReturn(new ArrayList<>(Arrays.asList(room1, room4)));

        // Test
        Meeting meeting = new Meeting("réunion", NINE_TEN, MeetingType.SPEC, 5);
        RoomBookResult roomBookResult = roomBookService.bookRoomFor(meeting);

        // Assert
        assertThat(roomBookResult.getRoomBookStatus()).isEqualTo(RoomBookStatus.SUCCESS);
        assertThat(roomBookResult.getRoom()).isEqualTo(room4);
        assertThat(room4.isBookedFor(NINE_TEN)).isTrue();
    }

    @Test
    void cannot_book_room_for_SPEC_meeting_when_no_room_with_BOARD_and_no_removable_available_equipment() {
        // Setup
        Room room1 = new Room("room1",6, Set.of(MULTILINE_SPEAKER));
        Room room2 = new Room("room2",7);
        Room room3 = new Room("room3",8);
        Room room4 = new Room("room4",9, Set.of(WEBCAM));
        rooms = List.of(room1, room2, room3, room4);
        when(allRooms.getRooms()).thenReturn(rooms);
        when(roomFinder.findUnbookedRoomsAtTimeSlot(rooms, NINE_TEN)).thenReturn(rooms);
        when(roomFinder.findUnbookedRoomsAtTimeSlot(rooms, EIGHT_NINE)).thenReturn(rooms);
        when(roomFinder.findRoomsWithMinimumCapacity(rooms, 5)).thenReturn(rooms);
        when(roomFinder.findRoomsWithSpecifiedEquipments(rooms, Set.of(BOARD))).thenReturn(Collections.emptyList());
        roomBookService.getAvailableRemovableEquipmentsFor(NINE_TEN).clear();

        // Test
        Meeting meeting = new Meeting("réunion", NINE_TEN, MeetingType.SPEC, 5);
        RoomBookResult roomBookResult = roomBookService.bookRoomFor(meeting);

        // Assert
        assertThat(roomBookResult.getRoomBookStatus()).isEqualTo(RoomBookStatus.FAILURE);
        assertThat(roomBookResult.getRoom()).isNull();
    }

    @Test
    void cannot_book_room_for_SPEC_meeting_when_no_room_with_BOARD_and_no_removable_available_BOARD() {
        // Setup
        Room room1 = new Room("room1",6, Set.of(MULTILINE_SPEAKER));
        Room room2 = new Room("room2",7);
        Room room3 = new Room("room3",8);
        Room room4 = new Room("room4",9, Set.of(WEBCAM));
        rooms = List.of(room1, room2, room3, room4);
        when(allRooms.getRooms()).thenReturn(rooms);
        when(roomFinder.findUnbookedRoomsAtTimeSlot(rooms, NINE_TEN)).thenReturn(rooms);
        when(roomFinder.findUnbookedRoomsAtTimeSlot(rooms, EIGHT_NINE)).thenReturn(rooms);
        when(roomFinder.findRoomsWithMinimumCapacity(rooms, 5)).thenReturn(rooms);
        when(roomFinder.findRoomsWithSpecifiedEquipments(rooms, Set.of(BOARD))).thenReturn(Collections.emptyList());
        roomBookService.getAvailableRemovableEquipmentsFor(NINE_TEN).removeIf(equipment -> equipment == BOARD);

        // Test
        Meeting meeting = new Meeting("réunion", NINE_TEN, MeetingType.SPEC, 5);
        RoomBookResult roomBookResult = roomBookService.bookRoomFor(meeting);

        // Assert
        assertThat(roomBookResult.getRoomBookStatus()).isEqualTo(RoomBookStatus.FAILURE);
        assertThat(roomBookResult.getRoom()).isNull();
    }

    @Test
    void room_booked_for_SPEC_meeting_when_no_room_with_BOARD_but_have_removable_available_BOARD() {
        // Setup
        Room room1 = new Room("room1",6, Set.of(MULTILINE_SPEAKER));
        Room room2 = new Room("room2",7);
        Room room3 = new Room("room3",8);
        Room room4 = new Room("room4",9, Set.of(WEBCAM));
        rooms = List.of(room1, room2, room3, room4);
        when(allRooms.getRooms()).thenReturn(rooms);
        when(roomFinder.findUnbookedRoomsAtTimeSlot(rooms, NINE_TEN)).thenReturn(rooms);
        when(roomFinder.findUnbookedRoomsAtTimeSlot(rooms, EIGHT_NINE)).thenReturn(rooms);
        when(roomFinder.findRoomsWithMinimumCapacity(rooms, 5)).thenReturn(rooms);
        when(roomFinder.findRoomsWithSpecifiedEquipments(rooms, Set.of(BOARD))).thenReturn(Collections.emptyList());

        // Test
        Meeting meeting = new Meeting("réunion", NINE_TEN, MeetingType.SPEC, 5);
        RoomBookResult roomBookResult = roomBookService.bookRoomFor(meeting);

        // Assert
        assertThat(roomBookResult.getRoomBookStatus()).isEqualTo(RoomBookStatus.SUCCESS);
        assertThat(roomBookResult.getRoom()).isEqualTo(room2);
        assertThat(room2.isBookedFor(NINE_TEN)).isTrue();
        assertThat(roomBookService.getAvailableRemovableEquipmentsFor(NINE_TEN)).hasSize(14);
    }


    private void givenRoomBookedFor(Room room, TimeSlot timeSlot) {
        room.markAsBookedFor(timeSlot);
    }


}