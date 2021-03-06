package com.canalplus.meetingplanner.controller;

import com.canalplus.meetingplanner.model.Room;
import com.canalplus.meetingplanner.model.RoomBookResult;
import com.canalplus.meetingplanner.model.RoomBookStatus;
import com.canalplus.meetingplanner.model.Meeting;
import com.canalplus.meetingplanner.model.MeetingType;
import com.canalplus.meetingplanner.repository.RoomBookRepository;
import com.canalplus.meetingplanner.service.RoomBookRCService;
import com.canalplus.meetingplanner.service.RoomBookRSService;
import com.canalplus.meetingplanner.service.RoomBookSPECService;
import com.canalplus.meetingplanner.service.RoomBookVCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.canalplus.meetingplanner.model.MeetingType.*;

/**
 * Point d'entrée de l'API pour réserver une salle de réunion et consulter les salles
 */
@RestController
public class RoomReservationController {

    @Autowired
    private RoomBookRSService roomBookRSService;

    @Autowired
    private RoomBookSPECService roomBookSPECService;

    @Autowired
    private RoomBookVCService roomBookVCService;

    @Autowired
    private RoomBookRCService roomBookRCService;

    @Autowired
    private RoomBookRepository roomBookRepository;

    @GetMapping(value="/rooms")
    public List<Room> getRooms() {
        return roomBookRepository.getRooms();
    }

    @PostMapping(value="/bookRoom")
    public Meeting bookARoom(@RequestBody Meeting meeting) {
        MeetingType meetingType = meeting.getType();

        RoomBookResult roomBookResult = null;

        if (RS == meetingType) {
            roomBookResult = roomBookRSService.bookRoomFor(meeting);
        } else if (SPEC == meetingType) {
            roomBookResult = roomBookSPECService.bookRoomFor(meeting);
        } else if (VC == meetingType) {
            roomBookResult = roomBookVCService.bookRoomFor(meeting);
        } else if (RC == meetingType) {
            roomBookResult = roomBookRCService.bookRoomFor(meeting);
        }

        meeting.setBookedRoomResult(roomBookResult);

        return meeting;
    }

    @PostMapping(value="/bookRooms")
    public List<Meeting> bookSeveralRooms(@RequestBody List<Meeting> meetings) {
        var meetingsWithResult = new ArrayList<Meeting>();

        meetings.forEach(meeting -> meetingsWithResult.add(bookARoom(meeting)));

        return meetingsWithResult;
    }
}
