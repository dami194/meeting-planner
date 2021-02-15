package com.canalplus.meetingplanner.model.meeting;

import com.canalplus.meetingplanner.model.Room;
import com.canalplus.meetingplanner.model.TimeSlot;

public class Meeting {
    private final String name;
    private final TimeSlot timeSlot;
    private final MeetingType type;
    private final int employeesNumber;
    private Room bookedRoom;

    public Meeting(String name, TimeSlot timeSlot, MeetingType type, int employeesNumber) {
        this.name = name;
        this.timeSlot = timeSlot;
        this.type = type;
        this.employeesNumber = employeesNumber;
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

    public void setBookedRoom(Room bookedRoom) {
        this.bookedRoom = bookedRoom;
    }

    public Room getBookedRoom() {
        return bookedRoom;
    }
}
