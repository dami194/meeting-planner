package com.canalplus.meetingplanner.controller;

import com.canalplus.meetingplanner.MeetingPlannerApplication;
import com.canalplus.meetingplanner.model.*;
import com.canalplus.meetingplanner.service.RoomBookRCService;
import com.canalplus.meetingplanner.service.RoomBookRSService;
import com.canalplus.meetingplanner.service.RoomBookSPECService;
import com.canalplus.meetingplanner.service.RoomBookVCService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.MOCK, classes = MeetingPlannerApplication.class)
@AutoConfigureMockMvc
class RoomReservationControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private RoomBookRSService roomBookRSService;

    @Autowired
    private RoomBookSPECService roomBookSPECService;

    @Autowired
    private RoomBookVCService roomBookVCService;

    @Autowired
    private RoomBookRCService roomBookRCService;

    @Autowired
    @Qualifier(value = "E2003")
    private Room roomE2003;

    @Test
    void should_getRooms_return_all_rooms() throws Exception {
        MvcResult mvcResult = mvc.perform(get("/rooms")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        List<Room> allRooms = new ObjectMapper().readValue(json, new TypeReference<List<Room>>() {});

        assertThat(allRooms).isNotEmpty().hasSize(12);
        assertThat(allRooms.get(0).getName()).isEqualTo("E1001");
        assertThat(allRooms.get(1).getName()).isEqualTo("E1002");
        assertThat(allRooms.get(2).getName()).isEqualTo("E1003");
        assertThat(allRooms.get(3).getName()).isEqualTo("E1004");
        assertThat(allRooms.get(4).getName()).isEqualTo("E2001");
        assertThat(allRooms.get(5).getName()).isEqualTo("E2002");
        assertThat(allRooms.get(6).getName()).isEqualTo("E2003");
        assertThat(allRooms.get(7).getName()).isEqualTo("E2004");
        assertThat(allRooms.get(8).getName()).isEqualTo("E3001");
        assertThat(allRooms.get(9).getName()).isEqualTo("E3002");
        assertThat(allRooms.get(10).getName()).isEqualTo("E3003");
        assertThat(allRooms.get(11).getName()).isEqualTo("E3004");
    }

    @Test
    void should_getRoom_return_wanted_room() throws Exception {
       assertThat(getRoom("E2003")).isEqualTo(roomE2003);
    }

    @Test
    void should_RS_meeting_be_booked() throws Exception {
        MvcResult mvcResult = mvc.perform(post("/bookRoom")
                .content(new ObjectMapper().writeValueAsString(new Meeting("réunion 1", TimeSlot.EIGHT_NINE, MeetingType.RS, 5)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        String json = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        Meeting meetingResult = new ObjectMapper().readValue(json, Meeting.class);

        assertThat(meetingResult.getBookedRoomResult()).isNotNull();
        assertThat(meetingResult.getBookedRoomResult().getRoomBookStatus()).isEqualTo(RoomBookStatus.SUCCESS);
        assertThat(meetingResult.getBookedRoomResult().getRoom()).isEqualTo(getRoom("E1001"));
    }

    private Room getRoom(String roomName) throws Exception {
        MvcResult mvcResult = mvc.perform(get("/rooms/{roomName}", roomName)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        return new ObjectMapper().readValue(json, Room.class);
    }
}