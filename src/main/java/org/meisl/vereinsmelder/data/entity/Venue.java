package org.meisl.vereinsmelder.data.entity;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
public class Venue extends AbstractEntity {
    private String name;
    private String toponym;
    private String street;
    private int streetNumber;
    private String addition;
    private int zip;
}
