package com.canalplus.meetingplanner.model;

import java.util.*;

/**
 * Une salle de réunion.
 * Contient le statut de réservation à chaque créneau disponible
 * Aucun créneau n'est réservé au démarrage de l'application
 */
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

    // for deserialization
    private Room() {
        this("default name", 0);
    }

    private void initializeTimeSlotStatus() {
        bookStatusByTimeSlot = new LinkedHashMap<>();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return capacity == room.capacity && name.equals(room.name) && equipments.equals(room.equipments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, capacity, equipments, bookStatusByTimeSlot);
    }
}
