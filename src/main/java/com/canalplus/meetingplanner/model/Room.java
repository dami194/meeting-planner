package com.canalplus.meetingplanner.model;

import java.util.*;

public class Room {
    private final String name;
    private final int capacity;
    private final Set<Equipment> equipments;
    private Map<TimeSlot, Boolean> bookStatusByTimeSlot;

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
        bookStatusByTimeSlot = new LinkedHashMap<>();
        // aucun créneau n'est réservé au démarrage de l'application
        Arrays.stream(TimeSlot.values()).forEach(timeSlot -> bookStatusByTimeSlot.put(timeSlot, false));
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    public Set<Equipment> getEquipments() {
        return equipments;
    }

    public boolean isBookedFor(TimeSlot timeSlot) {
        return bookStatusByTimeSlot.get(timeSlot);
    }

    public void markAsBookedFor(TimeSlot timeSlot) {
        bookStatusByTimeSlot.put(timeSlot, true);
    }

    @Override
    public String toString() {
        return "Room{" +
                "name='" + name + '\'' +
                ", capacity=" + capacity +
                ", equipments=" + equipments +
                '}';
    }
}
