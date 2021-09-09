package com.bhradec.microtasks.teamservice.team;

import com.bhradec.microtasks.teamservice.exception.ConflictException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TeamControllerTest {
    private final String TEAM_NAME = "Team name";
    private final String TEAM_DESCRIPTION = "Team description";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TeamService teamService;

    private final TeamCommand teamCommand = TeamCommand
            .builder()
            .name(TEAM_NAME)
            .description(TEAM_DESCRIPTION)
            .build();

    private final TeamDto teamDto = TeamDto
            .builder()
            .id(1L)
            .name(TEAM_NAME)
            .description(TEAM_DESCRIPTION)
            .build();

    @Test
    @DirtiesContext
    public void save() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        when(teamService.save(any())).thenReturn(teamDto);

        mockMvc.perform(
                post("/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamCommand))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(TEAM_NAME))
                .andExpect(jsonPath("$.description").value(TEAM_DESCRIPTION));

        when(teamService.save(any())).thenThrow(ConflictException.class);

        mockMvc.perform(
                post("/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamCommand))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$").doesNotExist());
    }
}
