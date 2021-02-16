package com.canalplus.meetingplanner.service;

import com.canalplus.meetingplanner.model.Equipment;
import com.canalplus.meetingplanner.model.Room;
import com.canalplus.meetingplanner.model.TimeSlot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.canalplus.meetingplanner.model.Equipment.*;
import static com.canalplus.meetingplanner.model.TimeSlot.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RoomFinderTest {

    @InjectMocks
    private RoomFinder roomFinder;

    @Test
    void should_unbooked_rooms_be_found_at_timeSlot() {
        // Setup
        Room room1 = new Room("room1",4);
        Room room2 = new Room("room2",2);
        Room room3 = new Room("room3",14);
        Room room4 = new Room("room4",7);
        room1.markAsBookedFor(EIGHT_NINE);
        room1.markAsBookedFor(FOURTEEN_FIFTEEN);
        room2.markAsBookedFor(FOURTEEN_FIFTEEN);
        room4.markAsBookedFor(EIGHT_NINE);
        room4.markAsBookedFor(TEN_ELEVEN);
        List<Room> rooms = List.of(room1, room2, room3, room4);

        // Test
        List<Room> unbookedRoomsAtTimeSlot = roomFinder.findUnbookedRoomsAtTimeSlot(rooms, FOURTEEN_FIFTEEN);

        // Assert
        assertThat(unbookedRoomsAtTimeSlot).containsExactly(room3, room4);
    }

    @Test
    void should_rooms_with_good_capacity_be_found() {
        // Setup
        Room room1 = new Room("room1",4);
        Room room2 = new Room("room2",2);
        Room room3 = new Room("room3",14);
        Room room4 = new Room("room4",7);
        List<Room> rooms = List.of(room1, room2, room3, room4);

        // Test
        List<Room> roomsWithMinimumCapacity = roomFinder.findRoomsWithMinimumCapacity(rooms, 4);

        // Assert
        assertThat(roomsWithMinimumCapacity).containsExactly(room1, room3, room4);
    }

    @Test
    void should_rooms_with_good_equipments_be_found() {
        // Setup
        Room room1 = new Room("room1",4);
        Room room2 = new Room("room2",2, Set.of(SCREEN));
        Room room3 = new Room("room3",14, Set.of(SCREEN, BOARD));
        Room room4 = new Room("room4",7, Set.of(SCREEN, BOARD, MULTILINE_SPEAKER));
        List<Room> rooms = List.of(room1, room2, room3, room4);

        // Test
        List<Room> roomsWithSpecifiedEquipments = roomFinder.findRoomsWithSpecifiedEquipments(rooms, Set.of(SCREEN, BOARD));
        List<Room> roomsWithSpecifiedEquipments2 = roomFinder.findRoomsWithSpecifiedEquipments(rooms, Set.of());

        // Assert
        assertThat(roomsWithSpecifiedEquipments).containsExactly(room3, room4);
        assertThat(roomsWithSpecifiedEquipments2).containsExactly(room1, room2, room3, room4);
    }
}