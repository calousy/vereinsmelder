package org.meisl.vereinsmelder.data.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@Getter
@Setter
public class Club extends AbstractEntity {

    private String name;

    @OneToMany(mappedBy = "club")
    @ElementCollection(fetch = FetchType.EAGER)
    private List<Team> teams;

    @OneToMany(mappedBy = "managerOf")
    @ElementCollection(fetch = FetchType.EAGER)
    private List<User> managers;

    @Override
    public String toString() {
        return getName();
    }
}
