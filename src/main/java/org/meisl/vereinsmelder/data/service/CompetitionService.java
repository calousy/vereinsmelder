package org.meisl.vereinsmelder.data.service;

import org.meisl.vereinsmelder.data.entity.Competition;
import org.meisl.vereinsmelder.data.filter.CompetitionFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CompetitionService {

    private final CompetitionRepository repository;

    @Autowired
    public CompetitionService(CompetitionRepository repository) {
        this.repository = repository;
    }

    public Optional<Competition> get(UUID id) {
        return repository.findById(id);
    }

    public Competition update(Competition entity) {
        return repository.save(entity);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public List<Competition> listInFuture() {
        List<Competition> all = repository.findAll();
        return all.stream().filter(competition -> competition.getDate()
                .isAfter(LocalDate.now())).toList();
    }

    public Page<Competition> list(CompetitionFilter filter, Pageable pageable) {
        if (filter.showAll) {
            return repository.findAll(pageable);
        }
        return new PageImpl<>(listInFuture());
    }

    public int count(CompetitionFilter filter) {
        if (filter.showAll) {
            return (int) repository.count();
        }
        return listInFuture().size();
    }

}
