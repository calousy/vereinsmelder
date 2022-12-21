package org.meisl.vereinsmelder.data.service;

import java.util.Optional;
import java.util.UUID;

import org.meisl.vereinsmelder.data.entity.Club;
import org.meisl.vereinsmelder.data.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    Page<User> findByManagerOf(Club club, Pageable pageable);
}