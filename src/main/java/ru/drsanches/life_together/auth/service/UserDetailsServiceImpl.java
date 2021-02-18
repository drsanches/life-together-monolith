package ru.drsanches.life_together.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.drsanches.life_together.auth.data.user.UserAuthRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserAuthRepository userAuthRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userAuthRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }
}