package com.canalplus.meetingplanner.service;

import com.canalplus.meetingplanner.model.AllRooms;
import com.canalplus.meetingplanner.model.Equipment;
import com.canalplus.meetingplanner.model.Room;
import com.canalplus.meetingplanner.model.TimeSlot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoomFinder {

    public List<Room> findUnbookedRoomsAtTimeSlot(List<Room> rooms, TimeSlot timeSlot) {
        return rooms.stream()
                .filter(room -> !room.isBookedFor(timeSlot))
                .collect(Collectors.toList());
    }

    public List<Room> findRoomsWithMinimumCapacity(List<Room> rooms, int minimumCapacity) {
        return rooms.stream()
                .filter(room -> room.getCapacity() >= minimumCapacity)
                .collect(Collectors.toList());
    }

    public List<Room> findRoomsWithSpecifiedEquipments(List<Room> rooms, Set<Equipment> equipments) {
        return rooms.stream()
                .filter(room -> room.getEquipments().containsAll(equipments))
                .collect(Collectors.toList());
    }
}
