package com.canalplus.meetingplanner;

import com.canalplus.meetingplanner.controller.RoomReservationController;
import com.canalplus.meetingplanner.model.Room;
import com.canalplus.meetingplanner.model.TimeSlot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.TestPropertySource;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import static com.canalplus.meetingplanner.model.TimeSlot.*;
import static com.canalplus.meetingplanner.model.TimeSlot.NINETEEN_TWENTY;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MeetingPlannerApplicationTests {

	@Autowired
	private RoomReservationController roomReservationController;

	@Autowired
	@Qualifier(value = "E1001")
	private Room roomE1001;

	@Autowired
	@Qualifier(value = "E3001")
	private Room roomE3001;


	@Test
	public void contextLoads() {
		assertThat(roomReservationController).isNotNull();
		assertThat(roomE1001.getCapacity()).isEqualTo(16);
		assertThat(roomE3001.getCapacity()).isEqualTo(9);
	}

}
