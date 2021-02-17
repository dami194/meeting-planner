package com.canalplus.meetingplanner.repository;

import com.canalplus.meetingplanner.model.Equipment;
import com.canalplus.meetingplanner.model.Room;
import com.canalplus.meetingplanner.model.TimeSlot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static com.canalplus.meetingplanner.model.Equipment.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class RoomBookRepositoryTest {

    @InjectMocks
    private RoomBookRepository roomBookRepository;

    private List<Room> rooms;

    @BeforeEach
    public void setup() {
        roomBookRepository.initRemovableEquipments();
    }

    @Test
    void should_removable_equipments_be_all_available_when_no_meeting_has_been_booked() {
        // Assert
        var removableEquipments = Arrays.asList(MULTILINE_SPEAKER, MULTILINE_SPEAKER, MULTILINE_SPEAKER, MULTILINE_SPEAKER,
                SCREEN, SCREEN, SCREEN, SCREEN, SCREEN, WEBCAM, WEBCAM, WEBCAM, WEBCAM, BOARD, BOARD);
        Arrays.stream(TimeSlot.values())
                .forEach(timeSlot ->
                    assertThat(roomBookRepository.getAvailableRemovableEquipmentsFor(timeSlot)).containsExactlyElementsOf(removableEquipments)
                );
    }
}