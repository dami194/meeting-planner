package com.canalplus.meetingplanner.service;

import com.canalplus.meetingplanner.exceptions.NoAvailableRoomException;
import com.canalplus.meetingplanner.model.*;
import com.canalplus.meetingplanner.repository.RoomBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.canalplus.meetingplanner.model.Equipment.BOARD;

/**
 * Service permettant de réserver une salle pour une séance de partage et d'études de cas (type "SPEC")
 *
 * Une séance de partage et d'études de cas doit se faire dans une salle :
 *
 * - disponible au créneau horaire demandé (i.e. non réservée pour ce créneau, non réservée au créneau précédent
 * et assez grande pour accueillir le nombre de personnes conviées à la réunion)
 *
 * et
 *
 * - comportant un tableau, ou qui peut récupérer en tant qu'équipement amovible un tableau
 */
@Service
public class RoomBookSPECService {

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

        return getRoomBookResultForSPECMeeting(meeting.getTimeSlot(), availableRooms);
    }

    private RoomBookResult getRoomBookResultForSPECMeeting(TimeSlot meetingTimeSlot, List<Room> availableRooms) {
        List<Room> availableRoomsWithBoard = roomFinder.findRoomsWithSpecifiedEquipments(availableRooms, Set.of(BOARD));

        // On a trouvé une salle avec les équipements déjà présents : on prend la première
        if (!availableRoomsWithBoard.isEmpty()) {
            Room bookedRoom = getOrderedRoomsByEquipmentsNumber(availableRoomsWithBoard).get(0);
            bookedRoom.markAsBookedFor(meetingTimeSlot);
            return new RoomBookResult(bookedRoom);
        }

        // Sinon on va regarder les équipements amovibles disponibles pour ce créneau
        else {
            List<Equipment> availableRemovableEquipments = roomBookRepository.getAvailableRemovableEquipmentsFor(meetingTimeSlot);
            if (!availableRemovableEquipments.contains(BOARD)) {
                return new RoomBookResult("Les équipements amovibles restants pour le créneau " + meetingTimeSlot + " ne contiennent pas de tableau");
            }

            availableRemovableEquipments.remove(BOARD);
            Room bookedRoom = getOrderedRoomsByEquipmentsNumber(availableRooms).get(0);
            bookedRoom.markAsBookedFor(meetingTimeSlot);
            return new RoomBookResult(bookedRoom);
        }
    }

    private List<Room> getOrderedRoomsByEquipmentsNumber(List<Room> rooms) {
        return rooms.stream()
                .sorted(Comparator.comparing(room -> room.getEquipments().size()))
                .collect(Collectors.toList());
    }
}
