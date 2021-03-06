package com.canalplus.meetingplanner.repository;

import com.canalplus.meetingplanner.model.Equipment;
import com.canalplus.meetingplanner.model.Room;
import com.canalplus.meetingplanner.model.TimeSlot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.*;

import static com.canalplus.meetingplanner.model.Equipment.*;

/**
 * Service qui permet de récupérer toutes les salles de réunion
 * Gère également les équipements amovibles disponibles à chaque créneau
 * Par défaut, tous les équipements amovibles sont disponibles à chaque créneau.
 */
@Repository
public class RoomBookRepository {

    @Autowired(required = false)
    private List<Room> rooms;

    public List<Room> getRooms() {
        return rooms;
    }

    private Map<TimeSlot, List<Equipment>> availableRemovableEquipmentsByTimeSlot = new HashMap<>();

    @PostConstruct
    public void initRemovableEquipments() {
        Arrays.stream(TimeSlot.values()).forEach(timeSlot -> availableRemovableEquipmentsByTimeSlot.put(timeSlot, getRemovableEquipments()));
    }

    private List<Equipment> getRemovableEquipments() {
        return new ArrayList<>(Arrays.asList(MULTILINE_SPEAKER, MULTILINE_SPEAKER, MULTILINE_SPEAKER, MULTILINE_SPEAKER,
                SCREEN, SCREEN, SCREEN, SCREEN, SCREEN, WEBCAM, WEBCAM, WEBCAM, WEBCAM, BOARD, BOARD));
    }

    public List<Equipment> getAvailableRemovableEquipmentsFor(TimeSlot timeSlot) {
        return availableRemovableEquipmentsByTimeSlot.get(timeSlot);
    }
}
