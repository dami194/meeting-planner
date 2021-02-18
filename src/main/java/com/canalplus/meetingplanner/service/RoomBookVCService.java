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

import static com.canalplus.meetingplanner.model.Equipment.*;

/**
 * Service permettant de réserver une salle pour une visioconférence (type "VC")
 *
 * Une visioconférence doit se faire dans une salle :
 *
 * - disponible au créneau horaire demandé (i.e. non réservée pour ce créneau, non réservée au créneau précédent
 * et assez grande pour accueillir le nombre de personnes conviées à la réunion)
 *
 * et
 *
 * - comportant un écran, une pieuvre et une webcam, ou qui peut récupérer en tant qu'équipements amovibles un écran,
 * une pieuvre et une webcam
 */
@Service
public class RoomBookVCService {

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

        return getRoomBookResultForVCMeeting(meeting.getTimeSlot(), availableRooms);
    }

    private RoomBookResult getRoomBookResultForVCMeeting(TimeSlot meetingTimeSlot, List<Room> availableRooms) {
        // On récupère les équipements amovibles disponibles pour le créneau demandé
        List<Equipment> availableRemovableEquipments = roomBookRepository.getAvailableRemovableEquipmentsFor(meetingTimeSlot);
        Optional<Room> potentiallyBookedRoom;

        // On cherche d'abord une salle ayant tous les équipements requis
        potentiallyBookedRoom = room_found_with_all_VC_equipments(availableRooms);
        if (potentiallyBookedRoom.isPresent()) {
            Room bookedRoom = potentiallyBookedRoom.get();
            bookedRoom.markAsBookedFor(meetingTimeSlot);
            return new RoomBookResult(bookedRoom, Set.of());
        }

        // On cherche ensuite une salle ayant au moins un écran et une pieuvre
        potentiallyBookedRoom = roomFinder.findRoom_with_someEquipment(availableRooms, Set.of(SCREEN, MULTILINE_SPEAKER), WEBCAM, availableRemovableEquipments);
        if (potentiallyBookedRoom.isPresent()) {
            Room bookedRoom = potentiallyBookedRoom.get();
            bookedRoom.markAsBookedFor(meetingTimeSlot);
            availableRemovableEquipments.remove(WEBCAM);
            return new RoomBookResult(bookedRoom, Set.of(WEBCAM));
        }

        // On cherche ensuite une salle ayant au moins un écran et une webcam
        potentiallyBookedRoom = roomFinder.findRoom_with_someEquipment(availableRooms, Set.of(SCREEN, WEBCAM), MULTILINE_SPEAKER, availableRemovableEquipments);
        if (potentiallyBookedRoom.isPresent()) {
            Room bookedRoom = potentiallyBookedRoom.get();
            bookedRoom.markAsBookedFor(meetingTimeSlot);
            availableRemovableEquipments.remove(MULTILINE_SPEAKER);
            return new RoomBookResult(bookedRoom, Set.of(MULTILINE_SPEAKER));
        }

        // On cherche ensuite une salle ayant au moins une pieuvre et une webcam
        potentiallyBookedRoom = roomFinder.findRoom_with_someEquipment(availableRooms, Set.of(MULTILINE_SPEAKER, WEBCAM), SCREEN, availableRemovableEquipments);
        if (potentiallyBookedRoom.isPresent()) {
            Room bookedRoom = potentiallyBookedRoom.get();
            bookedRoom.markAsBookedFor(meetingTimeSlot);
            availableRemovableEquipments.remove(SCREEN);
            return new RoomBookResult(bookedRoom, Set.of(SCREEN));
        }

        // On cherche ensuite une salle ayant au moins un écran
        potentiallyBookedRoom = roomFinder.findRoom_with_oneEquipment(availableRooms, SCREEN, Set.of(MULTILINE_SPEAKER, WEBCAM), availableRemovableEquipments);
        if (potentiallyBookedRoom.isPresent()) {
            Room bookedRoom = potentiallyBookedRoom.get();
            bookedRoom.markAsBookedFor(meetingTimeSlot);
            availableRemovableEquipments.remove(MULTILINE_SPEAKER);
            availableRemovableEquipments.remove(WEBCAM);
            return new RoomBookResult(bookedRoom, Set.of(MULTILINE_SPEAKER, WEBCAM));
        }

        // On cherche ensuite une salle ayant au moins une pieuvre
        potentiallyBookedRoom = roomFinder.findRoom_with_oneEquipment(availableRooms, MULTILINE_SPEAKER, Set.of(SCREEN, WEBCAM), availableRemovableEquipments);
        if (potentiallyBookedRoom.isPresent()) {
            Room bookedRoom = potentiallyBookedRoom.get();
            bookedRoom.markAsBookedFor(meetingTimeSlot);
            availableRemovableEquipments.remove(SCREEN);
            availableRemovableEquipments.remove(WEBCAM);
            return new RoomBookResult(bookedRoom, Set.of(SCREEN, WEBCAM));
        }

        // On cherche ensuite une salle ayant au moins une webcam
        potentiallyBookedRoom = roomFinder.findRoom_with_oneEquipment(availableRooms, WEBCAM, Set.of(SCREEN, MULTILINE_SPEAKER), availableRemovableEquipments);
        if (potentiallyBookedRoom.isPresent()) {
            Room bookedRoom = potentiallyBookedRoom.get();
            bookedRoom.markAsBookedFor(meetingTimeSlot);
            availableRemovableEquipments.remove(SCREEN);
            availableRemovableEquipments.remove(MULTILINE_SPEAKER);
            return new RoomBookResult(bookedRoom, Set.of(SCREEN, MULTILINE_SPEAKER));
        }

        // Sinon il ne reste que des salles sans aucun des trois équipements nécessaires à une réunion VC
        // On va regarder les équipements amovibles disponibles pour ce créneau
        if (!availableRemovableEquipments.contains(SCREEN)
                || !availableRemovableEquipments.contains(MULTILINE_SPEAKER)
                || !availableRemovableEquipments.contains(WEBCAM)) {

            return new RoomBookResult("Aucune salle restante à ce créneau ne contient tous les équipements requis" +
                    ", et les équipements amovibles restants pour ce créneau ne contiennent pas au moins" +
                    " un écran, une pieuvre et une webcam");
        }

        availableRemovableEquipments.remove(SCREEN);
        availableRemovableEquipments.remove(MULTILINE_SPEAKER);
        availableRemovableEquipments.remove(WEBCAM);
        Room bookedRoom = availableRooms
                .stream()
                .min(Comparator.comparing(room -> room.getEquipments().size()))
                .get();

        bookedRoom.markAsBookedFor(meetingTimeSlot);
        return new RoomBookResult(bookedRoom, Set.of(SCREEN, MULTILINE_SPEAKER, WEBCAM));
    }

    private Optional<Room> room_found_with_all_VC_equipments(List<Room> availableRooms) {
        return roomFinder
                .findRoomsWithSpecifiedEquipments(availableRooms, Set.of(SCREEN, MULTILINE_SPEAKER, WEBCAM))
                .stream()
                .min(Comparator.comparing(room -> room.getEquipments().size()));
    }
}
