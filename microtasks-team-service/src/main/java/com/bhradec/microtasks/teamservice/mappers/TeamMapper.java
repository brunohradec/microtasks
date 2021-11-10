package com.bhradec.microtasks.teamservice.mappers;

import com.bhradec.microtasks.teamservice.domain.Team;
import com.bhradec.microtasks.teamservice.dtos.TeamCommandDto;
import com.bhradec.microtasks.teamservice.dtos.TeamDto;
import org.springframework.stereotype.Component;

@Component
public class TeamMapper {
    public Team mapCommandToTeam(TeamCommandDto teamCommandDto) {
        return Team
                .builder()
                .name(teamCommandDto.getName())
                .description(teamCommandDto.getDescription())
                .build();
    }

    public TeamDto mapTeamToDto(Team team) {
        return TeamDto
                .builder()
                .id(team.getId())
                .name(team.getName())
                .description(team.getDescription())
                .build();
    }
}
