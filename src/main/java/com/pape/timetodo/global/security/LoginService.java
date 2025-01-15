package com.pape.timetodo.global.security;

import java.util.Optional;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.pape.timetodo.global.jpa.entity.UsersEntity;
import com.pape.timetodo.global.jpa.repository.UsersRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoginService implements UserDetailsService{

    private final UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        Optional<UsersEntity> usersWrap = usersRepository.findById(username);

        if(!usersWrap.isPresent()){
            throw new UsernameNotFoundException(FailType.USER_NOT_FOUNT.getValue());
        } 

        UsersEntity users = usersWrap.get();

        if(!users.isEnabled()){
            throw new BadCredentialsException(FailType.ACOUNT_DISABLE.getValue());
        }
       
        if(!users.isAccountNonExpired()){
            throw new BadCredentialsException(FailType.ACOUNT_EXPRIED.getValue());
        }

        if(!users.isAccountNonLocked()){
            throw new BadCredentialsException(FailType.ACOUNT_LOCK.getValue());
        }

        if(users.getDeleteDt() != null){
            throw new BadCredentialsException(FailType.USER_DELETE.getValue());
        }

        users.getAuthorities();

        return users;
    }
    

}
