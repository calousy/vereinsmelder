package org.meisl.vereinsmelder.data.service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.meisl.vereinsmelder.data.Role;
import org.meisl.vereinsmelder.data.entity.Club;
import org.meisl.vereinsmelder.data.entity.User;
import org.meisl.vereinsmelder.data.service.exception.EmailExistsException;
import org.meisl.vereinsmelder.data.service.exception.UsernameExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository repository;

    public PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> get(UUID id) {
        return repository.findById(id);
    }

    public User update(User entity) {
        return repository.save(entity);
    }

    public User register(User entity, String password)
            throws EmailExistsException, UsernameExistsException {
        entity.setHashedPassword(passwordEncoder.encode(password));
        Optional<User> byUsername = repository.findByUsername(entity.getUsername());
        if (byUsername.isPresent()) {
            throw new UsernameExistsException(entity.getUsername());
        }
        Optional<User> byEmail = repository.findByEmail(entity.getEmail());
        if (byEmail.isPresent()) {
            throw new EmailExistsException(entity.getEmail());
        }
        return update(entity);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public Page<User> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public int count() {
        return (int) repository.count();
    }

    public Page<User> findByClub(Club club, Pageable pageable) {
        return repository.findByManagerOf(club, pageable);
    }

    public User toggleRole(User entity, Role role) {
        Set<Role> roles = entity.getRoles();
        boolean hasRole = roles.contains(role);
        if (hasRole) {
            roles.remove(role);
        } else {
            roles.add(role);
        }
        return update(entity);
    }

}
