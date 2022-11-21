package org.meisl.vereinsmelder.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Set;
import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;
import org.meisl.vereinsmelder.data.Role;

@Entity
@Table(name = "application_user")
@Getter
@Setter
public class User extends AbstractEntity {

    private String username;
    private String firstname;
    private String lastname;
    private String email;
    @JsonIgnore
    private String hashedPassword;
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Role> roles;
    @Lob
    @Column(length = 1000000)
    private byte[] profilePicture;

    @ManyToOne
    private Club managerOf;

    public String getName() {
        return lastname + ", " + firstname;
    }

}
