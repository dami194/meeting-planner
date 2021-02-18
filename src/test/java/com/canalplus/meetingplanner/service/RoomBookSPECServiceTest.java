package com.canalplus.meetingplanner.service;

import com.canalplus.meetingplanner.exceptions.NoAvailableRoomException;
import com.canalplus.meetingplanner.model.*;
import com.canalplus.meetingplanner.repository.RoomBookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static com.canalplus.meetingplanner.model.Equipment.*;
import static com.canalplus.meetingplanner.model.TimeSlot.EIGHT_NINE;
import static com.canalplus.meetingplanner.model.TimeSlot.NINE_TEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoomBookSPECServiceTest {

    @InjectMocks
    private RoomBookSPECService roomBookSPECService;

    @Spy
    private RoomBookRepository roomBookRepository;

    @Mock
    private RoomFinder roomFinder;

    private List<Room> rooms;

    @BeforeEach
    public void setup() {
        roomBookRepository.initRemovableEquipments();
    }

    @Test
    void cannot_book_room_for_SPEC_meeting_if_no_available_room() {
        // Setup
        TimeSlot meetingTimeSlot = EIGHT_NINE;
        int meetingEmployeesNumber = 5;
        when(roomBookRepository.getRooms()).thenReturn(rooms);
        doThrow(new NoAvailableRoomException("Pas de salle disponible")).when(roomFinder).findAvailableRooms(rooms, meetingTimeSlot, meetingEmployeesNumber);

        // Test
        Meeting meeting = new Meeting("réunion", meetingTimeSlot, MeetingType.RS, meetingEmployeesNumber);
        RoomBookResult roomBookResult = roomBookSPECService.bookRoomFor(meeting);

        // Assert
        assertThat(roomBookResult.getRoomBookStatus()).isEqualTo(RoomBookStatus.FAILURE);
        assertThat(roomBookResult.getRoom()).isNull();
    }

    @Test
    void room_booked_for_SPEC_meeting_when_a_room_already_contains_BOARD() {
        // Setup
        TimeSlot meetingTimeSlot = NINE_TEN;
        int meetingEmployeesNumber = 5;
        Room room1 = new Room("room1",6, Set.of(BOARD, MULTILINE_SPEAKER));
        Room room2 = new Room("room2",7);
        Room room3 = new Room("room3",8);
        Room room4 = new Room("room4",9, Set.of(BOARD));
        rooms = List.of(room1, room2, room3, room4);
        when(roomBookRepository.getRooms()).thenReturn(rooms);
        when(roomFinder.findAvailableRooms(rooms, meetingTimeSlot, meetingEmployeesNumber)).thenReturn(rooms);
        when(roomFinder.findRoomsWithSpecifiedEquipments(rooms, Set.of(BOARD))).thenReturn(new ArrayList<>(Arrays.asList(room1, room4)));

        // Test
        Meeting meeting = new Meeting("réunion", meetingTimeSlot, MeetingType.SPEC, meetingEmployeesNumber);
        RoomBookResult roomBookResult = roomBookSPECService.bookRoomFor(meeting);

        // Assert
        assertThat(roomBookResult.getRoomBookStatus()).isEqualTo(RoomBookStatus.SUCCESS);
        assertThat(roomBookResult.getRoom()).isEqualTo(room4);
        assertThat(room4.isBookedFor(meetingTimeSlot)).isTrue();
    }

    @Test
    void cannot_book_room_for_SPEC_meeting_when_no_room_with_BOARD_and_no_available_removable_BOARD() {
        // Setup
        TimeSlot meetingTimeSlot = NINE_TEN;
        int meetingEmployeesNumber = 5;
        Room room1 = new Room("room1",6, Set.of(MULTILINE_SPEAKER));
        Room room2 = new Room("room2",7);
        Room room3 = new Room("room3",8);
        Room room4 = new Room("room4",9, Set.of(WEBCAM));
        rooms = List.of(room1, room2, room3, room4);
        when(roomBookRepository.getRooms()).thenReturn(rooms);
        when(roomFinder.findAvailableRooms(rooms, meetingTimeSlot, meetingEmployeesNumber)).thenReturn(rooms);
        when(roomFinder.findRoomsWithSpecifiedEquipments(rooms, Set.of(BOARD))).thenReturn(Collections.emptyList());
        roomBookRepository.getAvailableRemovableEquipmentsFor(meetingTimeSlot).removeIf(equipment -> equipment == BOARD);

        // Test
        Meeting meeting = new Meeting("réunion", meetingTimeSlot, MeetingType.SPEC, meetingEmployeesNumber);
        RoomBookResult roomBookResult = roomBookSPECService.bookRoomFor(meeting);

        // Assert
        assertThat(roomBookResult.getRoomBookStatus()).isEqualTo(RoomBookStatus.FAILURE);
        assertThat(roomBookResult.getRoom()).isNull();
    }

    @Test
    void room_booked_for_SPEC_meeting_when_no_room_with_BOARD_and_available_removable_BOARD() {
        // Setup
        TimeSlot meetingTimeSlot = NINE_TEN;
        int meetingEmployeesNumber = 5;
        Room room1 = new Room("room1",6, Set.of(MULTILINE_SPEAKER));
        Room room2 = new Room("room2",7);
        Room room3 = new Room("room3",8);
        Room room4 = new Room("room4",9, Set.of(WEBCAM));
        rooms = List.of(room1, room2, room3, room4);
        when(roomBookRepository.getRooms()).thenReturn(rooms);
        when(roomFinder.findAvailableRooms(rooms, meetingTimeSlot, meetingEmployeesNumber)).thenReturn(rooms);
        when(roomFinder.findRoomsWithSpecifiedEquipments(rooms, Set.of(BOARD))).thenReturn(Collections.emptyList());

        // Test
        Meeting meeting = new Meeting("réunion", meetingTimeSlot, MeetingType.SPEC, meetingEmployeesNumber);
        RoomBookResult roomBookResult = roomBookSPECService.bookRoomFor(meeting);

        // Assert
        assertThat(roomBookResult.getRoomBookStatus()).isEqualTo(RoomBookStatus.SUCCESS);
        assertThat(roomBookResult.getRoom()).isEqualTo(room2);
        assertThat(room2.isBookedFor(meetingTimeSlot)).isTrue();
        assertThat(roomBookRepository.getAvailableRemovableEquipmentsFor(meetingTimeSlot)).hasSize(14);
        assertThat(roomBookResult.getRemovableBorrowedEquipments()).isNotEmpty().containsExactly(BOARD);
    }
}