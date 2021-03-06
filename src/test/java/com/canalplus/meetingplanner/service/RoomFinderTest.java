package com.canalplus.meetingplanner.service;

import com.canalplus.meetingplanner.model.Equipment;
import com.canalplus.meetingplanner.model.Room;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static com.canalplus.meetingplanner.model.Equipment.*;
import static com.canalplus.meetingplanner.model.TimeSlot.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class RoomFinderTest {

    @InjectMocks
    private RoomFinder roomFinder;

    @Test
    void should_findAvailableRooms_throw_exception_when_no_unbooked_room_at_timeSlot() {
        // Setup
        Room room1 = new Room("room1",4);
        Room room2 = new Room("room2",2);
        Room room3 = new Room("room3",14);
        Room room4 = new Room("room4",7);
        room1.markAsBookedFor(FOURTEEN_FIFTEEN);
        room2.markAsBookedFor(FOURTEEN_FIFTEEN);
        room3.markAsBookedFor(FOURTEEN_FIFTEEN);
        room4.markAsBookedFor(FOURTEEN_FIFTEEN);
        List<Room> rooms = List.of(room1, room2, room3, room4);

        // Test & Assert
        assertThrows(IllegalStateException.class, () -> roomFinder.findAvailableRooms(rooms, FOURTEEN_FIFTEEN, 5));
    }

    @Test
    void should_findAvailableRooms_throw_exception_when_no_unbooked_room_at_previous_timeSlot() {
        // Setup
        Room room1 = new Room("room1",4);
        Room room2 = new Room("room2",2);
        Room room3 = new Room("room3",14);
        Room room4 = new Room("room4",7);
        room1.markAsBookedFor(FOURTEEN_FIFTEEN);
        room2.markAsBookedFor(FOURTEEN_FIFTEEN);
        room3.markAsBookedFor(FOURTEEN_FIFTEEN);
        room4.markAsBookedFor(FOURTEEN_FIFTEEN);
        List<Room> rooms = List.of(room1, room2, room3, room4);

        // Test & Assert
        assertThrows(IllegalStateException.class, () -> roomFinder.findAvailableRooms(rooms, FIFTEEN_SIXTEEN, 5));
    }

    @Test
    void should_findAvailableRooms_throw_exception_when_no_room_with_sufficient_capacity() {
        // Setup
        Room room1 = new Room("room1",4);
        Room room2 = new Room("room2",2);
        Room room3 = new Room("room3",14);
        Room room4 = new Room("room4",7);
        List<Room> rooms = List.of(room1, room2, room3, room4);

        // Test & Assert
        assertThrows(IllegalStateException.class, () -> roomFinder.findAvailableRooms(rooms, EIGHT_NINE, 20));
    }

    @Test
    void should_findAvailableRooms_return_unbooked_cleaned_and_sufficient_capacity_rooms() {
        // Setup
        Room room1 = new Room("room1",4);
        Room room2 = new Room("room2",2);
        Room room3 = new Room("room3",14);
        Room room4 = new Room("room4",7);
        Room room5 = new Room("room5",12);
        Room room6 = new Room("room6",3);
        room1.markAsBookedFor(NINE_TEN);
        room3.markAsBookedFor(EIGHT_NINE);
        List<Room> rooms = List.of(room1, room2, room3, room4, room5, room6);

        // Test
        List<Room> availableRooms = roomFinder.findAvailableRooms(rooms, NINE_TEN, 5);

        // Assert
        assertThat(availableRooms).containsExactly(room4, room5);
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
        List<Room> roomsWithAtLeastScreenAndBoard = roomFinder.findRoomsWithSpecifiedEquipments(rooms, Set.of(SCREEN, BOARD));
        List<Room> roomsWithNoSpecificEquipment = roomFinder.findRoomsWithSpecifiedEquipments(rooms, Set.of());

        // Assert
        assertThat(roomsWithAtLeastScreenAndBoard).containsExactly(room3, room4);
        assertThat(roomsWithNoSpecificEquipment).containsExactly(room1, room2, room3, room4);
    }

    @Test
    void should_rooms_with_some_equipments_be_found() {
        // Setup
        Room room1 = new Room("room1",4);
        Room room2 = new Room("room2",2, Set.of(SCREEN));
        Room room3 = new Room("room3",14, Set.of(SCREEN, BOARD));
        Room room4 = new Room("room4",7, Set.of(SCREEN, BOARD, MULTILINE_SPEAKER));
        List<Room> rooms = List.of(room1, room2, room3, room4);
        List<Equipment> availableRemovableEquipments = Arrays.asList(WEBCAM, WEBCAM, BOARD, BOARD, BOARD, MULTILINE_SPEAKER);

        // Test
        Optional<Room> roomWithSomeEquipment = roomFinder.findRoom_with_someEquipment(rooms, Set.of(SCREEN, BOARD), WEBCAM, availableRemovableEquipments);

        // Assert
        assertThat(roomWithSomeEquipment).isPresent().hasValue(room3);
    }

    @Test
    void should_rooms_with_some_equipments_not_be_found() {
        // Setup
        Room room1 = new Room("room1",4);
        Room room2 = new Room("room2",2, Set.of(SCREEN));
        Room room3 = new Room("room3",14, Set.of(SCREEN, BOARD));
        Room room4 = new Room("room4",7, Set.of(SCREEN, BOARD, MULTILINE_SPEAKER));
        List<Room> rooms = List.of(room1, room2, room3, room4);

        // Test
        Optional<Room> roomWithSomeEquipment = roomFinder.findRoom_with_someEquipment(rooms, Set.of(SCREEN, BOARD), WEBCAM, Collections.emptyList());

        // Assert
        assertThat(roomWithSomeEquipment).isNotPresent();

        // Test
        roomWithSomeEquipment = roomFinder.findRoom_with_someEquipment(rooms, Set.of(SCREEN, BOARD, WEBCAM), MULTILINE_SPEAKER, Collections.emptyList());

        // Assert
        assertThat(roomWithSomeEquipment).isNotPresent();
    }

    @Test
    void should_rooms_with_one_equipment_be_found() {
        // Setup
        Room room1 = new Room("room1",4);
        Room room2 = new Room("room2",2, Set.of(SCREEN));
        Room room3 = new Room("room3",14, Set.of(SCREEN, BOARD));
        Room room4 = new Room("room4",7, Set.of(SCREEN, BOARD, MULTILINE_SPEAKER));
        List<Room> rooms = List.of(room1, room2, room3, room4);
        List<Equipment> availableRemovableEquipments = Arrays.asList(WEBCAM, WEBCAM, BOARD, BOARD, BOARD, SCREEN);

        // Test
        Optional<Room> roomWithSomeEquipment = roomFinder.findRoom_with_oneEquipment(rooms, MULTILINE_SPEAKER, Set.of(SCREEN, BOARD), availableRemovableEquipments);

        // Assert
        assertThat(roomWithSomeEquipment).isPresent().hasValue(room4);
    }

    @Test
    void should_rooms_with_one_equipment_not_be_found() {
        // Setup
        Room room1 = new Room("room1",4);
        Room room2 = new Room("room2",2, Set.of(SCREEN));
        Room room3 = new Room("room3",14, Set.of(SCREEN, BOARD));
        Room room4 = new Room("room4",7, Set.of(SCREEN, BOARD, MULTILINE_SPEAKER));
        List<Room> rooms = List.of(room1, room2, room3, room4);

        // Test
        Optional<Room> roomWithOneEquipment = roomFinder.findRoom_with_oneEquipment(rooms, WEBCAM, Set.of(BOARD, SCREEN), Collections.emptyList());

        // Assert
        assertThat(roomWithOneEquipment).isNotPresent();

        // Test
        roomWithOneEquipment = roomFinder.findRoom_with_oneEquipment(rooms, SCREEN, Set.of(BOARD, SCREEN), Collections.emptyList());

        // Assert
        assertThat(roomWithOneEquipment).isNotPresent();
    }
}