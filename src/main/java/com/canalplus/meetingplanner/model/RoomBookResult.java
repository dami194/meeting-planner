package com.canalplus.meetingplanner.model;

public class RoomBookResult {

    private final Room room;
    private final RoomBookStatus roomBookStatus;

    public RoomBookResult(Room room, RoomBookStatus roomBookStatus) {
        this.room = room;
        this.roomBookStatus = roomBookStatus;
    }

    public RoomBookResult(RoomBookStatus roomBookStatus) {
        this(null, roomBookStatus);
    }

    public Room getRoom() {
        return room;
    }

    public RoomBookStatus getRoomBookStatus() {
        return roomBookStatus;
    }

}
