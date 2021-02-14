package com.canalplus.meetingplanner.controller;

import com.canalplus.meetingplanner.MeetingPlannerApplication;
import com.canalplus.meetingplanner.model.Room;
import com.fasterxml.jackson.annotation.JacksonInject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
public class RoomReservationController {

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
}
