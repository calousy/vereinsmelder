package org.meisl.vereinsmelder.data.service;

import org.meisl.vereinsmelder.data.entity.Club;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ClubService {

    private final ClubRepository repository;

    @Autowired
    public ClubService(ClubRepository repository) {
        this.repository = repository;
    }

    public Optional<Club> get(UUID id) {
        return repository.findById(id);
    }

    public Club update(Club entity) {
        return repository.save(entity);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public Page<Club> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
