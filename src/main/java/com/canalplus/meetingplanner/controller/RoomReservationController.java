package com.canalplus.meetingplanner.controller;

import com.canalplus.meetingplanner.MeetingPlannerApplication;
import com.canalplus.meetingplanner.model.AllRooms;
import com.canalplus.meetingplanner.model.Room;
import com.canalplus.meetingplanner.model.RoomBookResult;
import com.canalplus.meetingplanner.model.RoomBookStatus;
import com.canalplus.meetingplanner.model.meeting.Meeting;
import com.canalplus.meetingplanner.model.meeting.MeetingType;
import com.canalplus.meetingplanner.service.RoomBookRSService;
import com.canalplus.meetingplanner.service.RoomBookSPECService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

import static com.canalplus.meetingplanner.model.meeting.MeetingType.RS;
import static com.canalplus.meetingplanner.model.meeting.MeetingType.SPEC;

@RestController
public class RoomReservationController {

//    @Autowired
//    private RoomBookService roomBookService;

    @Autowired
    private RoomBookRSService roomBookRSService;

    @Autowired
    private RoomBookSPECService roomBookSPECService;

    @Autowired
    private AllRooms allRooms;

    @PostMapping(value="/reserveRoom")
    public String reserveRoom() {
        return "Room reserved";
    }

    @GetMapping(value="/reservedRooms")
    public String getReservedRooms() {
        return "List of rooms reserved";
    }

    @GetMapping(value="/room/{roomName}")
    public Room getRoom(@PathVariable String roomName) {
        var context = new AnnotationConfigApplicationContext(MeetingPlannerApplication.class);
        return (Room) context.getBean(roomName);
    }

    @GetMapping(value="/rooms")
    public Collection<Room> getRooms() {
        var context = new AnnotationConfigApplicationContext(MeetingPlannerApplication.class);
        return context.getBeansOfType(Room.class).values();
    }

    @PostMapping(value="/bookRoom")
    public Meeting bookARoom(@RequestBody Meeting meeting) {
        MeetingType meetingType = meeting.getType();
        RoomBookResult roomBookResult = null;
        if (RS == meetingType) {
            roomBookResult = roomBookRSService.bookRoomFor(meeting);
        } else if (SPEC == meetingType) {
            roomBookResult = roomBookSPECService.bookRoomFor(meeting);
        }

        if (roomBookResult.getRoomBookStatus() == RoomBookStatus.SUCCESS) {
            meeting.setBookedRoom(roomBookResult.getRoom());
        }

        return meeting;
    }
}
