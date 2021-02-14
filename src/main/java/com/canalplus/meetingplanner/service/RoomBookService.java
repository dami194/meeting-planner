package com.canalplus.meetingplanner.service;

import com.canalplus.meetingplanner.model.RoomBookStatus;
import com.canalplus.meetingplanner.model.Room;
import com.canalplus.meetingplanner.model.RoomBookResult;
import com.canalplus.meetingplanner.model.TimeSlot;
import org.springframework.stereotype.Service;

import static com.canalplus.meetingplanner.model.TimeSlot.EIGHT_NINE;

@Service
public class RoomBookService {

    public RoomBookResult bookRoomFor(Room room, TimeSlot timeSlot) {
        if (canRoomBeBookedFor(room, timeSlot)) {
            room.markAsBookedFor(timeSlot);
            return new RoomBookResult(room, RoomBookStatus.SUCCESS);
        }
        return new RoomBookResult(room, RoomBookStatus.FAILURE);
    }

    private boolean canRoomBeBookedFor(Room room, TimeSlot timeSlot) {
        // si elle est déjà réservée : on sort
        if (room.isBookedFor(timeSlot)) {
            return false;
        }

        // si c'est la première heure de la journée : c'est bon
        if (EIGHT_NINE == timeSlot) {
            return true;
        }

        // si le créneau précédent était réservé :
        if (room.isBookedFor(timeSlot.previousSlot().get())) {
            return false;
        }

        return true;
    }

}
