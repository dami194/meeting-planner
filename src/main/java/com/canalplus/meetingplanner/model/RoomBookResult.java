package com.canalplus.meetingplanner.model;

import java.util.Set;

public class RoomBookResult {

    private final Room room;
    private final RoomBookStatus roomBookStatus;
    private final String roomBookMessage;
    private final Set<Equipment> removableBorrowedEquipments;

    public RoomBookResult(Room room, Set<Equipment> removableBorrowedEquipments) {
        this.room = room;
        this.roomBookStatus = RoomBookStatus.SUCCESS;
        this.roomBookMessage = "La salle [" + room + "] a été réservée";
        this.removableBorrowedEquipments = removableBorrowedEquipments;
    }

    public RoomBookResult(String bookErrorMessage) {
        this.room = null;
        this.roomBookStatus = RoomBookStatus.FAILURE;
        this.roomBookMessage = bookErrorMessage;
        this.removableBorrowedEquipments = null;
    }

    // for deserialization
    private RoomBookResult() {
        this("default result");
    }

    public Room getRoom() {
        return room;
    }

    public RoomBookStatus getRoomBookStatus() {
        return roomBookStatus;
    }

    public String getRoomBookMessage() {
        return roomBookMessage;
    }

    public Set<Equipment> getRemovableBorrowedEquipments() {
        return removableBorrowedEquipments;
    }
}
