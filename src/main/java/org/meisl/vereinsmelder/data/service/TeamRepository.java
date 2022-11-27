package org.meisl.vereinsmelder.data.service;

import org.meisl.vereinsmelder.data.entity.Competition;
import org.meisl.vereinsmelder.data.entity.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TeamRepository extends JpaRepository<Team, UUID> {

    Page<Team> findByCompetition(Competition competition, Pageable pageable);

    List<Team> findByCompetition(Competition competition);

    Page<Team> findByCompetitionAndEnabledTrue(Competition competition, Pageable pageable);

    List<Team> findByCompetitionAndEnabledTrue(Competition competition);

}