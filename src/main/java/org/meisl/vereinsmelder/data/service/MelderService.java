package org.meisl.vereinsmelder.data.service;

import org.meisl.vereinsmelder.data.entity.Club;
import org.meisl.vereinsmelder.data.entity.Competition;
import org.meisl.vereinsmelder.data.entity.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class MelderService {

    @Autowired
    private TeamService teamService;

    @Autowired
    private CompetitionService competitionService;

    public void melden(Competition competition, Club club) {
        long count = competition.getTeams().stream()
                .filter(t -> t.getClub().equals(club) && t.isEnabled()).count();
        Team newTeam = new Team();
        newTeam.setCompetition(competition);
        newTeam.setClub(club);
        String suffix = "";
        if (count > 0) {
            suffix = " " + (count + 1);
        }
        newTeam.setName(club.getName() + suffix);
        competition.getTeams().add(newTeam);
        teamService.update(newTeam);
    }

    public void abmelden(Team team, Competition competition) {
        Club club = team.getClub();
        List<Team> sorted = competition.getTeams().stream()
                .filter(t -> t.getClub().equals(club) && t.isEnabled())
                .sorted(Comparator.comparing(Team::getRegistered)).toList();
        Team teamToRemove = sorted.get(sorted.size() - 1);
        competition.getTeams().remove(teamToRemove);
        teamService.deactivate(teamToRemove);
    }

}
