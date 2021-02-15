package com.canalplus.meetingplanner.model;

import java.util.*;

public class Room {
    private final String name;
    private final int capacity;
    private final Set<Equipment> equipments;
    private Map<TimeSlot, Boolean> timeSlotToBookStatus;

    public Room(String name, int capacity, Set<Equipment> equipments) {
        this.name = name;
        this.capacity = capacity;
        this.equipments = equipments;
        initializeTimeSlotStatus();
    }
    public Room(String name, int capacity) {
        this(name, capacity, Set.of());
    }

    private void initializeTimeSlotStatus() {
        timeSlotToBookStatus = new LinkedHashMap<>();
        // aucun créneau n'est réservé au démarrage de l'application
        Arrays.stream(TimeSlot.values()).forEach(timeSlot -> timeSlotToBookStatus.put(timeSlot, false));
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isBookedFor(TimeSlot timeSlot) {
        return timeSlotToBookStatus.get(timeSlot);
    }

    public void markAsBookedFor(TimeSlot timeSlot) {
        timeSlotToBookStatus.put(timeSlot, true);
    }
}
