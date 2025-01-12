package com.pape.timetodo.global.util;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

// import com.pape.timetodo.global.exception.AppException;
// import com.pape.timetodo.global.exception.ExceptionCode;
import com.pape.timetodo.global.jpa.entity.UsersEntity;
import com.pape.timetodo.global.jpa.repository.UsersRepository;
import com.pape.timetodo.global.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserUtil {

    private final UsersRepository usersRepository;

    private final JwtTokenProvider jwtTokenProvider;

    private final String ANONYMOUS_USER = "SYSTEM_ANONYMOUS_USER";

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Transactional
    public UsersEntity getUsersEntity(){
        
        return usersRepository.findById(jwtTokenProvider.getSubject(jwtTokenProvider.getHeaderToken()))
            // .orElseThrow(() -> new AppException(ExceptionCode.NOT_FOUND_USER)) // 추후 다시 해제
            .orElse(this.anonymousUser());
    }

    private UsersEntity anonymousUser(){

        Optional<UsersEntity> usersWrapper = usersRepository.findById(this.ANONYMOUS_USER);
    
        if(usersWrapper.isPresent()){
            return usersWrapper.get();
        } else {
            return usersRepository.save(
                UsersEntity.builder()
                    .username(this.ANONYMOUS_USER)
                    .password(System.currentTimeMillis() + "/" + LocalDateTime.now().toString())
                    .nickname("ANONYMOUS_USER")
                    .email("timetodo24@gmail.com")
                    .enabled(true)
                    .accountNonExpired(true)
                    .accountNonLock(true)
                    .passFailCount(0)
                    .build()
                );
        }

    }
    
}
