package com.myswcompany.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myswcompany.demo.models.Speaker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TestSpeakerRestController {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getAllSpeakers() throws Exception
    {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/speakers")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void getSpeakerById() throws Exception
    {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/speakers/{id}", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.speaker_id").value(1));
    }

    @Test
    public void createSpeaker() throws Exception
    {
        Speaker speaker = new Speaker();
        speaker.setFirst_name("Aku");
        speaker.setLast_name("Ankka");
        speaker.setSpeaker_bio("bio");
        speaker.setCompany("Company");
        speaker.setTitle("Suunnittelija");

        var json = new ObjectMapper().writeValueAsString(speaker);

        mockMvc.perform( MockMvcRequestBuilders
                        .post("/api/v1/speakers")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.speaker_id").exists());
    }

    @Test
    public void getSpeakerByIdThrowsNotFoundException() throws Exception
    {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/speakers/{id}", 1111)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void updateSpeakerReturnsSpeakerCreated() throws Exception
    {
        Speaker speaker = new Speaker();
        speaker.setFirst_name("Iines");
        speaker.setLast_name("Ankkuli");
        speaker.setSpeaker_bio("bio");
        speaker.setCompany("Company");
        speaker.setTitle("Suunnittelija");

        String json = new ObjectMapper().writeValueAsString(speaker);

        mockMvc.perform( MockMvcRequestBuilders
                        .put("/api/v1/speakers/{id}", 999)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.first_name").value("Iines"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.last_name").value("Ankkuli"));
    }
}
