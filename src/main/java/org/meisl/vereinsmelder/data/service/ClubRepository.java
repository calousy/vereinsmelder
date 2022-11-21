package org.meisl.vereinsmelder.data.service;

import org.meisl.vereinsmelder.data.entity.Club;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClubRepository extends JpaRepository<Club, UUID> {

    Club findByName(String name);
}