package com.canalplus.meetingplanner;

import com.canalplus.meetingplanner.model.Equipment;
import com.canalplus.meetingplanner.model.Room;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Set;

import static com.canalplus.meetingplanner.model.Equipment.*;

@SpringBootApplication
public class MeetingPlannerApplication {

	@Value( "${covid.roomCapacityLimitation:1.0}" )
	private double roomCapacityLimitation;

	public static void main(String[] args) {
		SpringApplication.run(MeetingPlannerApplication.class, args);
	}

	@Bean(name="E1001")
	public Room defineRoom_E1001(){
		System.out.println("Pour cause de Covid, la capacité des salles sera réduite à " + (roomCapacityLimitation*100) + " % de sa capacité initiale");
		return new Room("E1001", computeRealRoomCapacity(23));
	}

	@Bean(name="E1002")
	public Room defineRoom_E1002(){
		return new Room("E1002", computeRealRoomCapacity(10), Set.of(SCREEN));
	}

	@Bean(name="E1003")
	public Room defineRoom_E1003(){
		return new Room("E1003", computeRealRoomCapacity(8), Set.of(MULTILINE_SPEAKER));
	}

	@Bean(name="E1004")
	public Room defineRoom_E1004(){
		return new Room("E1004", computeRealRoomCapacity(4), Set.of(BOARD));
	}

	@Bean(name="E2001")
	public Room defineRoom_E2001(){
		return new Room("E2001", computeRealRoomCapacity(4));
	}

	@Bean(name="E2002")
	public Room defineRoom_E2002(){
		return new Room("E2002", computeRealRoomCapacity(15), Set.of(SCREEN, WEBCAM));
	}

	@Bean(name="E2003")
	public Room defineRoom_E2003(){
		return new Room("E2003", computeRealRoomCapacity(7));
	}

	@Bean(name="E2004")
	public Room defineRoom_E2004(){
		return new Room("E2004", computeRealRoomCapacity(9), Set.of(BOARD));
	}

	@Bean(name="E3001")
	public Room defineRoom_E3001(){
		return new Room("E3001", computeRealRoomCapacity(13), Set.of(SCREEN,WEBCAM,MULTILINE_SPEAKER));
	}

	@Bean(name="E3002")
	public Room defineRoom_E3002(){
		return new Room("E3002", computeRealRoomCapacity(8));
	}

	@Bean(name="E3003")
	public Room defineRoom_E3003(){
		return new Room("E3003", computeRealRoomCapacity(9), Set.of(SCREEN, MULTILINE_SPEAKER));
	}

	@Bean(name="E3004")
	public Room defineRoom_E3004(){
		return new Room("E3004", computeRealRoomCapacity(4));
	}

	private int computeRealRoomCapacity(int initialCapacity) {
		return (int) (roomCapacityLimitation * initialCapacity);
	}

}
