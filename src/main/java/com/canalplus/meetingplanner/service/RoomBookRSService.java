package com.canalplus.meetingplanner.service;

import com.canalplus.meetingplanner.exceptions.NoAvailableRoomException;
import com.canalplus.meetingplanner.model.*;
import com.canalplus.meetingplanner.repository.RoomBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service permettant de réserver une salle pour une réunion simple (type "RS")
 *
 * Une réunion simple doit se faire dans une salle :
 *
 * - disponible au créneau horaire demandé (i.e. non réservée pour ce créneau, non réservée au créneau précédent
 * et assez grande pour accueillir le nombre de personnes conviées à la réunion)
 *
 * Aucun équipement n'est requis pour ce type de réunion.
 */
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
        } catch (NoAvailableRoomException e) {
            return new RoomBookResult(e.getMessage());
        }

        return getRoomBookResultForRSMeeting(meeting.getTimeSlot(), availableRooms);
    }

    private RoomBookResult getRoomBookResultForRSMeeting(TimeSlot meetingTimeSlot, List<Room> availableRooms) {
        Room bookedRoom = getOrderedRoomsByEquipmentsNumber(availableRooms).get(0);
        bookedRoom.markAsBookedFor(meetingTimeSlot);
        return new RoomBookResult(bookedRoom);
    }

    private List<Room> getOrderedRoomsByEquipmentsNumber(List<Room> rooms) {
        return rooms.stream()
                .sorted(Comparator.comparing(room -> room.getEquipments().size()))
                .collect(Collectors.toList());
    }
}
