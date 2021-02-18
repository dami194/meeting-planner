package com.canalplus.meetingplanner.service;

import com.canalplus.meetingplanner.exceptions.NoAvailableRoomException;
import com.canalplus.meetingplanner.model.*;
import com.canalplus.meetingplanner.repository.RoomBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
        Optional<Room> potentiallyBookedRoom;

        // On cherche d'abord des salles ayant un tableau
        potentiallyBookedRoom = roomFoundWithAll_SPEC_Equipments(availableRooms);
        if (potentiallyBookedRoom.isPresent()) {
            Room bookedRoom = potentiallyBookedRoom.get();
            bookedRoom.markAsBookedFor(meetingTimeSlot);
            return new RoomBookResult(bookedRoom, Set.of());
        }

        // Sinon il ne reste que des salles sans aucun tableau (seul équipement nécessaire à une réunion RC)
        // On va regarder les équipements amovibles disponibles pour ce créneau
        else {
            List<Equipment> availableRemovableEquipments = roomBookRepository.getAvailableRemovableEquipmentsFor(meetingTimeSlot);
            if (!availableRemovableEquipments.contains(BOARD)) {
                return new RoomBookResult("Aucune salle restante à ce créneau ne contient de tableau" +
                        ", et les équipements amovibles restants pour ce créneau ne contiennent pas non plus de tableau");
            }

            availableRemovableEquipments.remove(BOARD);
            Room bookedRoom = availableRooms
                    .stream()
                    .min(Comparator.comparing(room -> room.getEquipments().size()))
                    .get();
            bookedRoom.markAsBookedFor(meetingTimeSlot);
            return new RoomBookResult(bookedRoom, Set.of(BOARD));
        }
    }

    private Optional<Room> roomFoundWithAll_SPEC_Equipments(List<Room> availableRooms) {
        return roomFinder
                .findRoomsWithSpecifiedEquipments(availableRooms, Set.of(BOARD))
                .stream()
                .min(Comparator.comparing(room -> room.getEquipments().size()));
    }
}
