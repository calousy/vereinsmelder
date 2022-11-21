package org.meisl.vereinsmelder.data.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
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

    @OneToMany(mappedBy = "competition", fetch = FetchType.EAGER)
    private List<Team> teams;

    private LocalDateTime registrationEnd;
}
