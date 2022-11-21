package org.meisl.vereinsmelder.data.service;

import org.meisl.vereinsmelder.data.entity.Competition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CompetitionRepository extends JpaRepository<Competition, UUID> {

}