package com.canalplus.meetingplanner.service;

import com.canalplus.meetingplanner.exceptions.NoAvailableRoomException;
import com.canalplus.meetingplanner.model.Equipment;
import com.canalplus.meetingplanner.model.Room;
import com.canalplus.meetingplanner.model.TimeSlot;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service utilitaire permettant d'effectuer des recherches sur les salles selon plusieurs critères
 */
@Service
public class RoomFinder {

    /**
     * Cherche des salles disponibles
     *
     * Une salle est disponible si elle est :
     * - non réservée pour le créneau recherché
     * - non réservée pour le créneau précédent au créneau recherché
     * - d'une capacité supérieure à 'minimumCapacity'
     *
     * @param rooms les salles sur lesquelles effectuer la recherche
     * @param meetingTimeSlot le créneau sur lequel effectuer la recherche
     * @param minimumCapacity la capacité minimum des salles à trouver
     * @return La liste des salles qui sont disponibles
     * @throws NoAvailableRoomException si aucune salle de respecte les conditions demandées
     */
    public List<Room> findAvailableRooms(List<Room> rooms, TimeSlot meetingTimeSlot, int minimumCapacity) throws NoAvailableRoomException {
        List<Room> unbookedRooms = findUnbookedRoomsAtTimeSlot(rooms, meetingTimeSlot);
        if (unbookedRooms.isEmpty()) {
            throw new NoAvailableRoomException("Toutes les salles au créneau " + meetingTimeSlot + " sont déjà réservées");
        }

        List<Room> unbookedCleanedRooms;
        if (meetingTimeSlot.previousSlot().isPresent()) {
            unbookedCleanedRooms = findUnbookedRoomsAtTimeSlot(unbookedRooms, meetingTimeSlot.previousSlot().get());
            if (unbookedCleanedRooms.isEmpty()) {
                throw new NoAvailableRoomException("Toutes les salles restantes au créneau " + meetingTimeSlot + " ont déjà réservées au créneau précédent");
            }
        } else {
            unbookedCleanedRooms = unbookedRooms;
        }

        List<Room> availableRooms = findRoomsWithMinimumCapacity(unbookedCleanedRooms, minimumCapacity);
        if (availableRooms.isEmpty()) {
            throw new NoAvailableRoomException("Toutes les salles restantes au créneau " + meetingTimeSlot + " n'ont pas la capacité requise");
        }

        return availableRooms;
    }

    /**
     * Cherche des salles disposant d'un certain nombre d'équipements
     *
     * @param rooms les salles sur lesquelles effectuer la recherche
     * @param equipments les équipements requis dans les salles recherchées
     * @return La liste des salles qui disposent des équipements précisés en entrée
     */
    public List<Room> findRoomsWithSpecifiedEquipments(List<Room> rooms, Set<Equipment> equipments) {
        return rooms.stream()
                .filter(room -> room.getEquipments().containsAll(equipments))
                .collect(Collectors.toList());
    }

    /**
     * Cherche la meilleure salle parmi celles disponibles
     *
     * La salle recherchée doit disposer des équipements présents dans le paramètre 'someEquipments'
     * Les équipements amovibles disponibles doivent comporter au moins un équipement de type 'otherEquipment'
     *
     * Si les deux conditions précédentes sont réunies, la salle renvoyée est celle parmi les salles disponibles
     * qui a le moins d'équipement
     *
     * @param availableRooms les salles sur lesquelles effectuer la recherche
     * @param someEquipments les équipements requis dans les salles recherchées
     * @param otherEquipment l'équipement requis dans la liste des équipements amovibles disponibles
     * @param availableRemovableEquipments les équipements amovibles disponibles
     * @return la meilleure salle selon la règle évoquée ci-dessus
     */
    public Optional<Room> findRoom_with_someEquipment(List<Room> availableRooms, Set<Equipment> someEquipments,
                                                      Equipment otherEquipment, List<Equipment> availableRemovableEquipments) {
        List<Room> roomsWithSomeEquipments = findRoomsWithSpecifiedEquipments(availableRooms, someEquipments);

        if (!roomsWithSomeEquipments.isEmpty() && availableRemovableEquipments.contains(otherEquipment)) {
            return roomsWithSomeEquipments
                    .stream()
                    .min(Comparator.comparing(room -> room.getEquipments().size()));
        }

        return Optional.empty();
    }

    /**
     * Cherche la meilleure salle parmi celles disponibles
     *
     * La salle recherchée doit disposer de l'équipement présent dans le paramètre 'equipment'
     * Les équipements amovibles disponibles doivent comporter tous les équipements présents dans 'otherEquipments'
     *
     * Si les deux conditions précédentes sont réunies, la salle renvoyée est celle parmi les salles disponibles
     * qui a le moins d'équipement
     *
     * @param availableRooms les salles sur lesquelles effectuer la recherche
     * @param equipment l'équipement requis dans les salles recherchées
     * @param otherEquipments les équipements requis dans la liste des équipements amovibles disponibles
     * @param availableRemovableEquipments les équipements amovibles disponibles
     * @return la meilleure salle selon la règle évoquée ci-dessus
     */
    public Optional<Room> findRoom_with_oneEquipment(List<Room> availableRooms, Equipment equipment,
                                                     Set<Equipment> otherEquipments, List<Equipment> availableRemovableEquipments) {
        List<Room> roomsWithAtLeastOneRCEquipment = findRoomsWithSpecifiedEquipments(availableRooms, Set.of(equipment));

        if (!roomsWithAtLeastOneRCEquipment.isEmpty() && availableRemovableEquipments.containsAll(otherEquipments)) {

            return roomsWithAtLeastOneRCEquipment
                    .stream()
                    .min(Comparator.comparing(room -> room.getEquipments().size()));
        }

        return Optional.empty();
    }


    private List<Room> findUnbookedRoomsAtTimeSlot(List<Room> rooms, TimeSlot timeSlot) {
        return rooms.stream()
                .filter(room -> !room.isBookedFor(timeSlot))
                .collect(Collectors.toList());
    }

    private List<Room> findRoomsWithMinimumCapacity(List<Room> rooms, int minimumCapacity) {
        return rooms.stream()
                .filter(room -> room.getCapacity() >= minimumCapacity)
                .collect(Collectors.toList());
    }

}
