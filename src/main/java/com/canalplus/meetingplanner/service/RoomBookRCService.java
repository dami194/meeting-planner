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

import static com.canalplus.meetingplanner.model.Equipment.*;

/**
 * Service permettant de réserver une salle pour une réunion couplée (type "RC")
 *
 * Une réunion couplée doit se faire dans une salle :
 *
 * - disponible au créneau horaire demandé (i.e. non réservée pour ce créneau, non réservée au créneau précédent
 * et assez grande pour accueillir le nombre de personnes conviées à la réunion)
 *
 * et
 *
 * - comportant un tableau, un écran et une pieuvre, ou qui peut récupérer en tant qu'équipements amovibles un tableau,
 * un écran et une pieuvre
 */
@Service
public class RoomBookRCService {

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

        return getRoomBookResultForRCMeeting(meeting.getTimeSlot(), availableRooms);
    }

    private RoomBookResult getRoomBookResultForRCMeeting(TimeSlot meetingTimeSlot, List<Room> availableRooms) {
        List<Room> availableRoomsWithRCEquipments = roomFinder.findRoomsWithSpecifiedEquipments(availableRooms, Set.of(BOARD, SCREEN, MULTILINE_SPEAKER));

        // On a trouvé une salle avec les équipements déjà présents : on prend la première
        if (!availableRoomsWithRCEquipments.isEmpty()) {
            Room bookedRoom = getOrderedRoomsByEquipmentsNumber(availableRoomsWithRCEquipments).get(0);
            bookedRoom.markAsBookedFor(meetingTimeSlot);
            return new RoomBookResult(bookedRoom);
        }

        // Sinon on va regarder les équipements amovibles disponibles pour ce créneau
        else {
            List<Equipment> availableRemovableEquipments = roomBookRepository.getAvailableRemovableEquipmentsFor(meetingTimeSlot);
            if (!availableRemovableEquipments.contains(BOARD)
                    || !availableRemovableEquipments.contains(SCREEN)
                    || !availableRemovableEquipments.contains(MULTILINE_SPEAKER)) {
                return new RoomBookResult("Les équipements amovibles restants pour le créneau " + meetingTimeSlot + " ne contiennent pas au moins" +
                        " un tableau, un écran et une pieuvre");
            }

            availableRemovableEquipments.remove(BOARD);
            availableRemovableEquipments.remove(SCREEN);
            availableRemovableEquipments.remove(MULTILINE_SPEAKER);
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
