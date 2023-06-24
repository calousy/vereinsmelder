package org.meisl.vereinsmelder.data.entity;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
public class Competition extends AbstractEntity {

    private String name;

    private String category;
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "venue_id")
    private Venue venue;

    @OneToMany(mappedBy = "competition", fetch = FetchType.EAGER)
    private List<Team> teams;

    private LocalDateTime registrationEnd;
}
