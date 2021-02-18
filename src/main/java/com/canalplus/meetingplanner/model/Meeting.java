package com.canalplus.meetingplanner.model;

public class Meeting {
    private final String name;
    private final TimeSlot timeSlot;
    private final MeetingType type;
    private final int employeesNumber;
    private RoomBookResult bookedRoomResult;

    public Meeting(String name, TimeSlot timeSlot, MeetingType type, int employeesNumber) {
        this.name = name;
        this.timeSlot = timeSlot;
        this.type = type;
        this.employeesNumber = employeesNumber;
    }

    // for deserialization
    private Meeting() {
        this("default meeting name", null, null, 0);
    }

    public String getName() {
        return name;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public MeetingType getType() {
        return type;
    }

    public int getEmployeesNumber() {
        return employeesNumber;
    }

    public RoomBookResult getBookedRoomResult() {
        return bookedRoomResult;
    }

    public void setBookedRoomResult(RoomBookResult bookedRoomResult) {
        this.bookedRoomResult = bookedRoomResult;
    }
}
