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
    
    private Map<TimeSlot, List<Equipment>> availableRemovableEquipmentsByTimeSlot = new HashMap<>();

    @PostConstruct
    public void initRemovableEquipments() {
        // Les équipements amovibles sont tous disponibles pour tous les créneaux au départ
        // (autrement dit, un équipement amovible utilisé pour un créneau redevient disponible pour le créneau suivant)
        Arrays.stream(TimeSlot.values()).forEach(timeSlot -> availableRemovableEquipmentsByTimeSlot.put(timeSlot, getRemovableEquipments()));
    }

    private List<Equipment> getRemovableEquipments() {
        return Arrays.asList(MULTILINE_SPEAKER, MULTILINE_SPEAKER, MULTILINE_SPEAKER, MULTILINE_SPEAKER,
                SCREEN, SCREEN, SCREEN, SCREEN, SCREEN, WEBCAM, WEBCAM, WEBCAM, WEBCAM, BOARD, BOARD);
    }

    public List<Equipment> getAvailableRemovableEquipmentsFor(TimeSlot timeSlot) {
        return availableRemovableEquipmentsByTimeSlot.get(timeSlot);
    }

    public RoomBookResult bookRoomFor(Meeting meeting) {
        List<Room> rooms = allRooms.getRooms();

        // Si plus aucune salle n'est disponible à cette heure : ECHEC
        TimeSlot meetingTimeSlot = meeting.getTimeSlot();
        List<Room> unbookedRooms = allRooms.getRooms().stream().filter(room -> !room.isBookedFor(meetingTimeSlot)).collect(Collectors.toList());
        if (unbookedRooms.isEmpty()) {
            return new RoomBookResult(RoomBookStatus.FAILURE);
        }

        // Si plus aucune salle n'est disponible avec la bonne capacité : ECHEC
        int employeesNumber = meeting.getEmployeesNumber();
        List<Room> unbookedRoomsWithGoodCapacity = unbookedRooms.stream().filter(unbookedRoom -> unbookedRoom.getCapacity() >= employeesNumber).collect(Collectors.toList());
        if (unbookedRoomsWithGoodCapacity.isEmpty()) {
            return new RoomBookResult(RoomBookStatus.FAILURE);
        }

        MeetingType meetingType = meeting.getType();

        // RS : nécessite juste une salle au-delà de 3 collaborateurs
        if (MeetingType.RS == meetingType) {
            return getRoomBookResultForRSMeeting(meetingTimeSlot, unbookedRoomsWithGoodCapacity);
        }

        return new RoomBookResult(RoomBookStatus.FAILURE);
    }

    private RoomBookResult getRoomBookResultForRSMeeting(TimeSlot meetingTimeSlot, List<Room> unbookedRoomsWithGoodCapacity) {
        List<Room> unbookedRoomsWithGoodCapacityWithAtLeast3Places = unbookedRoomsWithGoodCapacity.stream()
                .filter(room -> room.getCapacity() > 3)
                .collect(Collectors.toList());
        if (unbookedRoomsWithGoodCapacityWithAtLeast3Places.isEmpty()) {
            return new RoomBookResult(RoomBookStatus.FAILURE);
        }
        // Ci-dessous je voyais deux choix pour déterminer la salle à réserver
        // 1) prendre en priorité la salle avec la plus petite capacité
        // 2) prendre en priorité la salle avec le moins d'équipement
        // j'ai choisi la solution 2 : en effet les autres types de réunion nécessitent des équipements donc autant leur laisser ces salles-là !
        List<Room> unbookedRoomsWithGoodCapacityWithAtLeast3PlacesAndOrdered = unbookedRoomsWithGoodCapacityWithAtLeast3Places.stream()
                .sorted(Comparator.comparing(room -> room.getEquipments().size()))
                .collect(Collectors.toList());
        Room bookedRoom = unbookedRoomsWithGoodCapacityWithAtLeast3PlacesAndOrdered.get(0);
        bookedRoom.markAsBookedFor(meetingTimeSlot);
        return new RoomBookResult(bookedRoom, RoomBookStatus.SUCCESS);
    }

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
