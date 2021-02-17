package com.canalplus.meetingplanner.service;

import com.canalplus.meetingplanner.model.Room;
import com.canalplus.meetingplanner.model.RoomBookResult;
import com.canalplus.meetingplanner.model.RoomBookStatus;
import com.canalplus.meetingplanner.model.TimeSlot;
import com.canalplus.meetingplanner.model.Meeting;
import com.canalplus.meetingplanner.model.MeetingType;
import com.canalplus.meetingplanner.repository.RoomBookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static com.canalplus.meetingplanner.model.Equipment.BOARD;
import static com.canalplus.meetingplanner.model.Equipment.MULTILINE_SPEAKER;
import static com.canalplus.meetingplanner.model.TimeSlot.EIGHT_NINE;
import static com.canalplus.meetingplanner.model.TimeSlot.NINE_TEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoomBookRSServiceTest {

    @InjectMocks
    private RoomBookRSService roomBookRSService;

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
    void cannot_book_room_for_RS_meeting_if_no_available_room() {
        // Setup
        TimeSlot meetingTimeSlot = EIGHT_NINE;
        int meetingEmployeesNumber = 5;
        when(roomBookRepository.getRooms()).thenReturn(rooms);
        doThrow(new IllegalStateException()).when(roomFinder).findAvailableRooms(rooms, meetingTimeSlot, meetingEmployeesNumber);

        // Test
        Meeting meeting = new Meeting("réunion", meetingTimeSlot, MeetingType.RS, meetingEmployeesNumber);
        RoomBookResult roomBookResult = roomBookRSService.bookRoomFor(meeting);

        // Assert
        assertThat(roomBookResult.getRoomBookStatus()).isEqualTo(RoomBookStatus.FAILURE);
        assertThat(roomBookResult.getRoom()).isNull();
    }

    @Test
    void room_booked_for_RS_meeting_should_be_the_first_available_with_the_least_equipments() {
        // Setup
        TimeSlot meetingTimeSlot = NINE_TEN;
        int meetingEmployeesNumber = 5;
        Room room1 = new Room("room1",6, Set.of(BOARD));
        Room room2 = new Room("room2",7);
        Room room3 = new Room("room3",8);
        Room room4 = new Room("room4",9, Set.of(BOARD, MULTILINE_SPEAKER));
        rooms = List.of(room1, room2, room3, room4);
        when(roomBookRepository.getRooms()).thenReturn(rooms);
        when(roomFinder.findAvailableRooms(rooms, meetingTimeSlot, meetingEmployeesNumber)).thenReturn(rooms);

        // Test
        Meeting meeting = new Meeting("réunion", meetingTimeSlot, MeetingType.RS, meetingEmployeesNumber);
        RoomBookResult roomBookResult = roomBookRSService.bookRoomFor(meeting);

        // Assert
        assertThat(roomBookResult.getRoomBookStatus()).isEqualTo(RoomBookStatus.SUCCESS);
        assertThat(roomBookResult.getRoom()).isEqualTo(room2);
        assertThat(room2.isBookedFor(meetingTimeSlot)).isTrue();
    }
}