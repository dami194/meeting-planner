package com.canalplus.meetingplanner.controller;

import org.springframework.web.bind.annotation.*;

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
}
