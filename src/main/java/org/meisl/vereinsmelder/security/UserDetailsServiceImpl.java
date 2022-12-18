package org.meisl.vereinsmelder.security;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.meisl.vereinsmelder.data.entity.User;
import org.meisl.vereinsmelder.data.service.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> maybeUser = userRepository.findByUsername(username);
        if (maybeUser.isEmpty()) {
            maybeUser = userRepository.findByEmail(username);
            if (maybeUser.isEmpty()) {
                throw new UsernameNotFoundException("No user present with username/email: " + username);
            }
        }
        User user = maybeUser.get();
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getHashedPassword(),
                getAuthorities(user));
    }

    private static List<GrantedAuthority> getAuthorities(User user) {
        return user.getRoles().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());

    }

}
