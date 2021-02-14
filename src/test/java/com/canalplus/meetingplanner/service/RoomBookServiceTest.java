package com.canalplus.meetingplanner.service;

import com.canalplus.meetingplanner.model.RoomBookStatus;
import com.canalplus.meetingplanner.model.Room;
import com.canalplus.meetingplanner.model.RoomBookResult;
import com.canalplus.meetingplanner.model.TimeSlot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.canalplus.meetingplanner.model.TimeSlot.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class RoomBookServiceTest {

    @InjectMocks
    private RoomBookService roomBookService;

    @Test
    void cannot_room_be_booked_if_already_booked() {
        // Setup
        Room room = new Room("E1001", 10);
        givenRoomBookedFor(room, TEN_ELEVEN);

        // Test
        RoomBookResult roomBookResult = roomBookService.bookRoomFor(room, TEN_ELEVEN);

        // Assert
        assertThat(roomBookResult.getRoomBookStatus()).isEqualTo(RoomBookStatus.FAILURE);
    }

    @Test
    void can_room_be_booked_for_EIGHT_NINE_if_not_already_booked() {
        // Setup
        Room room = new Room("E1001", 10);

        // Test
        RoomBookResult roomBookResult = roomBookService.bookRoomFor(room, EIGHT_NINE);

        // Assert
        assertThat(roomBookResult.getRoomBookStatus()).isEqualTo(RoomBookStatus.SUCCESS);
        assertThat(roomBookResult.getRoom().isBookedFor(EIGHT_NINE)).isTrue();
    }

    @Test
    void cannot_room_be_booked_if_previous_slot_was_already_booked() {
        // Setup
        Room room = new Room("E1001", 10);
        givenRoomBookedFor(room, TEN_ELEVEN);

        // Test
        RoomBookResult roomBookResult = roomBookService.bookRoomFor(room, ELEVEN_TWELVE);

        // Assert
        assertThat(roomBookResult.getRoomBookStatus()).isEqualTo(RoomBookStatus.FAILURE);
        assertThat(roomBookResult.getRoom().isBookedFor(ELEVEN_TWELVE)).isFalse();
    }

    @Test
    void can_be_room_booked_if_previous_slot_was_not_already_booked() {
        // Setup
        Room room = new Room("E1001", 10);

        // Test
        RoomBookResult roomBookResult = roomBookService.bookRoomFor(room, SIXTEEN_SEVENTEEN);

        // Assert
        assertThat(roomBookResult.getRoomBookStatus()).isEqualTo(RoomBookStatus.SUCCESS);
        assertThat(roomBookResult.getRoom().isBookedFor(FIFTEEN_SIXTEEN)).isFalse();
        assertThat(roomBookResult.getRoom().isBookedFor(SIXTEEN_SEVENTEEN)).isTrue();
    }

    private void givenRoomBookedFor(Room room, TimeSlot timeSlot) {
        room.markAsBookedFor(timeSlot);
    }


}