package org.meisl.vereinsmelder.data.entity;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
public class Club extends AbstractEntity {

    private String name;

    @OneToMany(mappedBy = "club", fetch = FetchType.EAGER)
    private List<Team> teams;

    @OneToMany(mappedBy = "managerOf", fetch = FetchType.EAGER)
    private Set<User> managers;

    @Override
    public String toString() {
        return getName();
    }
}
