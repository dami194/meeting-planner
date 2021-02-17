package com.canalplus.meetingplanner.model;

public class RoomBookResult {

    private final Room room;
    private final RoomBookStatus roomBookStatus;
    private final String roomBookMessage;


    public RoomBookResult(Room room) {
        this.room = room;
        this.roomBookStatus = RoomBookStatus.SUCCESS;
        this.roomBookMessage = "La salle [" + room + "] a été réservée";
    }

    public RoomBookResult(String bookErrorMessage) {
        this.room = null;
        this.roomBookStatus = RoomBookStatus.FAILURE;
        this.roomBookMessage = bookErrorMessage;
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
}
