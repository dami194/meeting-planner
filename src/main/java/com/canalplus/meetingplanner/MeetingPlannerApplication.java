package com.canalplus.meetingplanner;

import com.canalplus.meetingplanner.model.Room;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
public class MeetingPlannerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MeetingPlannerApplication.class, args);
	}

	@Bean(name="E1001")
	public Room defineRoom_E1001(){
		return new Room("E1001", 23);
	}

	@Bean(name="E1002")
	public Room defineRoom_E1002(){
		return new Room("E1002", 10);
	}

	@Bean(name="E1003")
	public Room defineRoom_E1003(){
		return new Room("E1003", 8);
	}

	@Bean(name="E1004")
	public Room defineRoom_E1004(){
		return new Room("E1004", 4);
	}

	@Bean(name="E2001")
	public Room defineRoom_E2001(){
		return new Room("E2001", 4);
	}

	@Bean(name="E2002")
	public Room defineRoom_E2002(){
		return new Room("E2002", 15);
	}

	@Bean(name="E2003")
	public Room defineRoom_E2003(){
		return new Room("E2003", 7);
	}

	@Bean(name="E2004")
	public Room defineRoom_E2004(){
		return new Room("E2004", 9);
	}

	@Bean(name="E3001")
	public Room defineRoom_E3001(){
		return new Room("E3001", 13);
	}

	@Bean(name="E3002")
	public Room defineRoom_E3002(){
		return new Room("E3002", 8);
	}

	@Bean(name="E3003")
	public Room defineRoom_E3003(){
		return new Room("E3003", 9);
	}

	@Bean(name="E3004")
	public Room defineRoom_E3004(){
		return new Room("E3004", 4);
	}



}
