package com.canalplus.meetingplanner.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AllRooms {

    @Autowired(required = false)
    private List<Room> rooms;

    public List<Room> getRooms() {
        return rooms;
    }
}
