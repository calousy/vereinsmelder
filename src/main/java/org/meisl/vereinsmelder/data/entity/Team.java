package org.meisl.vereinsmelder.data.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Team extends AbstractEntity {

    private String name;

    @ManyToOne
    private Club club;

    @ManyToOne
    private Competition competition;

    private LocalDateTime registered = LocalDateTime.now();
}
