package com.canalplus.meetingplanner.service;

import com.canalplus.meetingplanner.MeetingPlannerApplication;
import com.canalplus.meetingplanner.model.*;
import com.canalplus.meetingplanner.model.meeting.Meeting;
import com.canalplus.meetingplanner.model.meeting.MeetingType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

import static com.canalplus.meetingplanner.model.Equipment.*;
import static com.canalplus.meetingplanner.model.TimeSlot.EIGHT_NINE;

@Service
public class RoomBookService {

    @Autowired
    private AllRooms allRooms;

    @Autowired
    private RoomFinder roomFinder;
    
    private Map<TimeSlot, List<Equipment>> availableRemovableEquipmentsByTimeSlot = new HashMap<>();

    @PostConstruct
    public void initRemovableEquipments() {
        // Les équipements amovibles sont tous disponibles pour tous les créneaux au départ
        // (autrement dit, un équipement amovible utilisé pour un créneau redevient disponible pour le créneau suivant)
        Arrays.stream(TimeSlot.values()).forEach(timeSlot -> availableRemovableEquipmentsByTimeSlot.put(timeSlot, getRemovableEquipments()));
    }

    private List<Equipment> getRemovableEquipments() {
        return new ArrayList<>(Arrays.asList(MULTILINE_SPEAKER, MULTILINE_SPEAKER, MULTILINE_SPEAKER, MULTILINE_SPEAKER,
                SCREEN, SCREEN, SCREEN, SCREEN, SCREEN, WEBCAM, WEBCAM, WEBCAM, WEBCAM, BOARD, BOARD));
    }

    public List<Equipment> getAvailableRemovableEquipmentsFor(TimeSlot timeSlot) {
        return availableRemovableEquipmentsByTimeSlot.get(timeSlot);
    }

    public RoomBookResult bookRoomFor(Meeting meeting) {
        List<Room> rooms = allRooms.getRooms();

        // On récupère d'abord les salles pas encore réservées à cette heure
        // Si plus aucune salle n'est disponible à cette heure : ECHEC
        TimeSlot meetingTimeSlot = meeting.getTimeSlot();
        List<Room> unbookedRooms = roomFinder.findUnbookedRoomsAtTimeSlot(rooms, meetingTimeSlot);
        if (unbookedRooms.isEmpty()) {
            return new RoomBookResult(RoomBookStatus.FAILURE);
        }

        // On filtre ensuite les salles qui ont été réservées au créneau précédent celui demandé
        // sauf pour le premier créneau (pas de précédent)
        if (meetingTimeSlot.previousSlot().isPresent()) {
            unbookedRooms = roomFinder.findUnbookedRoomsAtTimeSlot(unbookedRooms, meetingTimeSlot.previousSlot().get());
            if (unbookedRooms.isEmpty()) {
                return new RoomBookResult(RoomBookStatus.FAILURE);
            }
        }

        // On filtre ensuite les salles qui n'ont pas la capacité demandée
        // Si plus aucune salle n'est disponible avec la bonne capacité : ECHEC
        int employeesNumber = meeting.getEmployeesNumber();
        List<Room> unbookedRoomsWithGoodCapacity = roomFinder.findRoomsWithMinimumCapacity(unbookedRooms, employeesNumber);
        if (unbookedRoomsWithGoodCapacity.isEmpty()) {
            return new RoomBookResult(RoomBookStatus.FAILURE);
        }

        MeetingType meetingType = meeting.getType();

        // RS : ne nécessite rien de plus (donc arrivé ici on renvoie toujours une salle)
        if (MeetingType.RS == meetingType) {
            return getRoomBookResultForRSMeeting(meetingTimeSlot, unbookedRoomsWithGoodCapacity);
        }

        // SPEC : nécessite en plus un tableau
        else if (MeetingType.SPEC == meetingType) {
            return getRoomBookResultForSPECMeeting(meetingTimeSlot, unbookedRoomsWithGoodCapacity);
        }

        return new RoomBookResult(RoomBookStatus.FAILURE);
    }

    private RoomBookResult getRoomBookResultForRSMeeting(TimeSlot meetingTimeSlot, List<Room> availableRooms) {
        // Ci-dessous je voyais deux choix pour déterminer la salle à réserver
        // 1) prendre en priorité la salle avec la plus petite capacité
        // 2) prendre en priorité la salle avec le moins d'équipement
        // j'ai choisi la solution 2 : en effet les autres types de réunion nécessitent des équipements donc autant leur laisser ces salles-là !
        List<Room> availableRoomsOrdered = getOrderedRoomsByEquipmentsNumber(availableRooms);
        Room bookedRoom = availableRoomsOrdered.get(0);
        bookedRoom.markAsBookedFor(meetingTimeSlot);
        return new RoomBookResult(bookedRoom, RoomBookStatus.SUCCESS);
    }

    private RoomBookResult getRoomBookResultForSPECMeeting(TimeSlot meetingTimeSlot, List<Room> availableRooms) {
        List<Room> availableRoomsWithBoard = roomFinder.findRoomsWithSpecifiedEquipments(availableRooms, Set.of(BOARD));

        // On a trouvé une salle avec les équipements déjà présents : on prend la première
        if (!availableRoomsWithBoard.isEmpty()) {
            Room bookedRoom = getOrderedRoomsByEquipmentsNumber(availableRoomsWithBoard).get(0);
            bookedRoom.markAsBookedFor(meetingTimeSlot);
            return new RoomBookResult(bookedRoom, RoomBookStatus.SUCCESS);
        }

        // Sinon cela veut dire qu'aucune salle disponible ne contient un tableau
        // ==> on va regarder les équipements amovibles disponibles pour ce créneau
        else {
            List<Equipment> removableAvailableEquipments = getAvailableRemovableEquipmentsFor(meetingTimeSlot);
            if (removableAvailableEquipments.isEmpty() || !removableAvailableEquipments.contains(BOARD)) {
                return new RoomBookResult(RoomBookStatus.FAILURE);
            }

            removableAvailableEquipments.remove(BOARD);
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
