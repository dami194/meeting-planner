package com.canalplus.meetingplanner.service;

import com.canalplus.meetingplanner.exceptions.NoAvailableRoomException;
import com.canalplus.meetingplanner.model.Equipment;
import com.canalplus.meetingplanner.model.Room;
import com.canalplus.meetingplanner.model.TimeSlot;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service utilitaire permettant d'effectuer des recherches sur les salles selon plusieurs critères
 */
@Service
public class RoomFinder {

    public List<Room> findAvailableRooms(List<Room> rooms, TimeSlot meetingTimeSlot, int meetingEmployeesNumber) throws NoAvailableRoomException {
        List<Room> unbookedRooms = findUnbookedRoomsAtTimeSlot(rooms, meetingTimeSlot);
        if (unbookedRooms.isEmpty()) {
            throw new NoAvailableRoomException("Toutes les salles au créneau " + meetingTimeSlot + " sont déjà réservées");
        }

        List<Room> unbookedCleanedRooms;
        if (meetingTimeSlot.previousSlot().isPresent()) {
            unbookedCleanedRooms = findUnbookedRoomsAtTimeSlot(unbookedRooms, meetingTimeSlot.previousSlot().get());
            if (unbookedCleanedRooms.isEmpty()) {
                throw new NoAvailableRoomException("Toutes les salles non réservées au créneau " + meetingTimeSlot + " ont déjà réservées au créneau précédent");
            }
        } else {
            unbookedCleanedRooms = unbookedRooms;
        }

        List<Room> availableRooms = findRoomsWithMinimumCapacity(unbookedCleanedRooms, meetingEmployeesNumber);
        if (availableRooms.isEmpty()) {
            throw new NoAvailableRoomException("Les salles ni réservées au créneau " + meetingTimeSlot + " ni réservées au créneau précédent n'ont pas la capacité requise");
        }

        return availableRooms;
    }

    public List<Room> findRoomsWithSpecifiedEquipments(List<Room> rooms, Set<Equipment> equipments) {
        return rooms.stream()
                .filter(room -> room.getEquipments().containsAll(equipments))
                .collect(Collectors.toList());
    }

    private List<Room> findUnbookedRoomsAtTimeSlot(List<Room> rooms, TimeSlot timeSlot) {
        return rooms.stream()
                .filter(room -> !room.isBookedFor(timeSlot))
                .collect(Collectors.toList());
    }

    private List<Room> findRoomsWithMinimumCapacity(List<Room> rooms, int minimumCapacity) {
        return rooms.stream()
                .filter(room -> room.getCapacity() >= minimumCapacity)
                .collect(Collectors.toList());
    }

}
