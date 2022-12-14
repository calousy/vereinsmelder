package org.meisl.vereinsmelder.data.service;

import org.meisl.vereinsmelder.data.entity.Competition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.UUID;

public interface CompetitionRepository extends JpaRepository<Competition, UUID> {

    Page<Competition> findByDateAfter(LocalDate date, Pageable pageable);
}