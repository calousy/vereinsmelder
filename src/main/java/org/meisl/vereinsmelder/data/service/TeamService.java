package org.meisl.vereinsmelder.data.service;

import org.meisl.vereinsmelder.data.entity.Competition;
import org.meisl.vereinsmelder.data.entity.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class TeamService {

    private final TeamRepository repository;

    @Autowired
    public TeamService(TeamRepository repository) {
        this.repository = repository;
    }

    public Optional<Team> get(UUID id) {
        return repository.findById(id);
    }

    public Team update(Team entity) {
        return repository.save(entity);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public Page<Team> listByCompetition(Competition competition, Pageable pageable) {
        return repository.findByCompetition(competition, pageable);
    }

    public int listByCompetitionCount(Competition competition) {
        return repository.findByCompetition(competition).size();
    }

    public Page<Team> listByCompetitionWhereEnabledIsTrue(Competition competition, Pageable pageable) {
        return repository.findByCompetitionAndEnabledTrue(competition, pageable);
    }

    public int listByCompetitionWhereEnabledIsTrueCount(Competition competition) {
        return repository.findByCompetitionAndEnabledTrue(competition).size();
    }

    public Page<Team> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
