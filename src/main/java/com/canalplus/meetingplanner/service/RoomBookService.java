package com.canalplus.meetingplanner.service;

import com.canalplus.meetingplanner.model.*;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.canalplus.meetingplanner.model.Equipment.*;
import static com.canalplus.meetingplanner.model.TimeSlot.EIGHT_NINE;

@Service
public class RoomBookService {

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
