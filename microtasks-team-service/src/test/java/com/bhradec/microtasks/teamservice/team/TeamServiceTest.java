package com.bhradec.microtasks.teamservice.team;

import com.bhradec.microtasks.teamservice.exception.ConflictException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.*;

@SpringBootTest
public class TeamServiceTest {
    private final String TEAM_NAME = "Team name";
    private final String TEAM_DESCRIPTION = "Team description";

    @MockBean
    private TeamRepository teamRepository;

    @Autowired
    private TeamService teamService;

    private final TeamCommand teamCommand = TeamCommand
            .builder()
            .name(TEAM_NAME)
            .description(TEAM_DESCRIPTION)
            .build();

    private final Team team = Team
            .builder()
            .id(1L)
            .name(TEAM_NAME)
            .description(TEAM_DESCRIPTION)
            .build();

    @Test
    @DirtiesContext
    public void save() throws ConflictException {
        when(teamRepository.save(any())).thenReturn(team);

        TeamDto uniqueTeamDto = teamService.save(teamCommand);

        assertEquals(uniqueTeamDto.getName(), TEAM_NAME);
        assertEquals(uniqueTeamDto.getDescription(), TEAM_DESCRIPTION);

        when(teamRepository.existsByName(TEAM_NAME)).thenReturn(true);
        assertThrows(ConflictException.class, () -> teamService.save(teamCommand));
    }
}
