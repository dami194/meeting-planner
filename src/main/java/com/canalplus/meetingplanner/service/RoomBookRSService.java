package com.canalplus.meetingplanner.service;

import com.canalplus.meetingplanner.model.*;
import com.canalplus.meetingplanner.model.meeting.Meeting;
import com.canalplus.meetingplanner.model.meeting.MeetingType;
import com.canalplus.meetingplanner.repository.RoomBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.canalplus.meetingplanner.model.Equipment.BOARD;

@Service
public class RoomBookRSService {

    @Autowired
    private RoomBookRepository roomBookRepository;

    @Autowired
    private RoomFinder roomFinder;

    public RoomBookResult bookRoomFor(Meeting meeting) {
        List<Room> rooms = roomBookRepository.getRooms();

        List<Room> availableRooms;
        try {
            availableRooms = roomFinder.findAvailableRooms(rooms, meeting.getTimeSlot(), meeting.getEmployeesNumber());
        } catch (IllegalStateException e) {
            return new RoomBookResult(RoomBookStatus.FAILURE);
        }

        // RS : ne nécessite rien de plus (donc arrivé ici on renvoie toujours une salle)
        return getRoomBookResultForRSMeeting(meeting.getTimeSlot(), availableRooms);
    }

    private RoomBookResult getRoomBookResultForRSMeeting(TimeSlot meetingTimeSlot, List<Room> availableRooms) {
        // Ci-dessous je voyais deux choix pour déterminer la salle à réserver
        // 1) prendre en priorité la salle avec la plus petite capacité
        // 2) prendre en priorité la salle avec le moins d'équipement
        // j'ai choisi la solution 2 : en effet les autres types de réunion nécessitent des équipements donc autant leur laisser ces salles-là !
        Room bookedRoom = getOrderedRoomsByEquipmentsNumber(availableRooms).get(0);
        bookedRoom.markAsBookedFor(meetingTimeSlot);
        return new RoomBookResult(bookedRoom, RoomBookStatus.SUCCESS);
    }

    private List<Room> getOrderedRoomsByEquipmentsNumber(List<Room> rooms) {
        return rooms.stream()
                .sorted(Comparator.comparing(room -> room.getEquipments().size()))
                .collect(Collectors.toList());
    }
}
