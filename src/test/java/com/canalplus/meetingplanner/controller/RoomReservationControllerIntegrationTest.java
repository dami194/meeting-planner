package com.canalplus.meetingplanner.controller;

import com.canalplus.meetingplanner.MeetingPlannerApplication;
import com.canalplus.meetingplanner.model.Room;
import com.canalplus.meetingplanner.repository.RoomBookRepository;
import com.canalplus.meetingplanner.service.RoomBookRCService;
import com.canalplus.meetingplanner.service.RoomBookRSService;
import com.canalplus.meetingplanner.service.RoomBookSPECService;
import com.canalplus.meetingplanner.service.RoomBookVCService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
    private RoomBookRepository roomBookRepository;

    @Test
    void should_getRooms_return_all_rooms() throws Exception {
        MvcResult mvcResult = mvc.perform(get("/rooms")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        List<Room> allRooms = new ObjectMapper().readValue(json, new TypeReference<List<Room>>() {});

        assertThat(allRooms).isNotEmpty().hasSize(12);
    }

//    @Test
//    void should_getRoom_return_the_wanted_room() throws Exception {
//        MvcResult mvcResult = mvc.perform(get("/rooms/E2001")
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk()).andReturn();
//
//        String json = mvcResult.getResponse().getContentAsString();
//        Room wantedRoom = (Room) new ObjectMapper().readValue(json, Room.class);
//
//        assertThat(wantedRoom).isNotNull().isEqualTo()
//    }
}