package com.canalplus.meetingplanner.service;

import com.canalplus.meetingplanner.model.*;
import com.canalplus.meetingplanner.model.meeting.Meeting;
import com.canalplus.meetingplanner.repository.RoomBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.canalplus.meetingplanner.model.Equipment.*;

/**
 * Service permettant de réserver une salle pour une visioconférence (type "VC")
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
        } catch (IllegalStateException e) {
            return new RoomBookResult(RoomBookStatus.FAILURE);
        }

        // VC : nécessite en plus un écran, une pieuvre et une webcam
        return getRoomBookResultForVCMeeting(meeting.getTimeSlot(), availableRooms);
    }

    private RoomBookResult getRoomBookResultForVCMeeting(TimeSlot meetingTimeSlot, List<Room> availableRooms) {
        List<Room> availableRoomsWithVCEquipments = roomFinder.findRoomsWithSpecifiedEquipments(availableRooms, Set.of(SCREEN, MULTILINE_SPEAKER, WEBCAM));

        // On a trouvé une salle avec les équipements déjà présents : on prend la première
        if (!availableRoomsWithVCEquipments.isEmpty()) {
            Room bookedRoom = getOrderedRoomsByEquipmentsNumber(availableRoomsWithVCEquipments).get(0);
            bookedRoom.markAsBookedFor(meetingTimeSlot);
            return new RoomBookResult(bookedRoom, RoomBookStatus.SUCCESS);
        }

        // Sinon cela veut dire qu'aucune salle disponible ne contient écran + pieuvre + webcam
        // ==> on va regarder les équipements amovibles disponibles pour ce créneau
        else {
            List<Equipment> availableRemovableEquipments = roomBookRepository.getAvailableRemovableEquipmentsFor(meetingTimeSlot);
            if (!availableRemovableEquipments.contains(SCREEN)
                    || !availableRemovableEquipments.contains(MULTILINE_SPEAKER)
                    || !availableRemovableEquipments.contains(WEBCAM)) {
                return new RoomBookResult(RoomBookStatus.FAILURE);
            }

            availableRemovableEquipments.remove(SCREEN);
            availableRemovableEquipments.remove(MULTILINE_SPEAKER);
            availableRemovableEquipments.remove(WEBCAM);
            Room bookedRoom = getOrderedRoomsByEquipmentsNumber(availableRooms).get(0);
            bookedRoom.markAsBookedFor(meetingTimeSlot);
            return new RoomBookResult(bookedRoom, RoomBookStatus.SUCCESS);
        }
    }

    private List<Room> getOrderedRoomsByEquipmentsNumber(List<Room> rooms) {
        return rooms.stream()
                .sorted(Comparator.comparing(room -> room.getEquipments().size()))
                .collect(Collectors.toList());
    }
}
