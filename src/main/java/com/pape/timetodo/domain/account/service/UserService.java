package com.pape.timetodo.domain.account.service;

import com.pape.timetodo.domain.account.model.user.NickCheckRQ;
import com.pape.timetodo.domain.account.model.user.SnsLoginRQ;
import com.pape.timetodo.domain.account.model.user.SnsLoginRS;
import com.pape.timetodo.domain.account.model.user.UserRegisterRQ;
import com.pape.timetodo.global.constant.NotificationType;
import com.pape.timetodo.global.constant.SortType;
import com.pape.timetodo.global.exception.AppException;
import com.pape.timetodo.global.exception.ExceptionCode;
import com.pape.timetodo.global.jpa.entity.AuthoritiesEntity;
import com.pape.timetodo.global.jpa.entity.AuthoritiesEntity.AuthorityId;
import com.pape.timetodo.global.jpa.entity.MailEntity;
import com.pape.timetodo.global.jpa.entity.MailEntity.MailType;
import com.pape.timetodo.global.jpa.entity.UserPreferencesEntity;
import com.pape.timetodo.global.jpa.entity.UsersEntity;
import com.pape.timetodo.global.jpa.repository.MailQueryRepository;
import com.pape.timetodo.global.jpa.repository.UserPreferencesRepository;
import com.pape.timetodo.global.jpa.repository.UsersRepository;
import com.pape.timetodo.global.security.JwtTokenProvider;
import com.pape.timetodo.global.security.model.TokenModel;
import com.pape.timetodo.global.security.model.UserType;
import com.pape.timetodo.global.util.UserUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UsersRepository usersRepository;

    private final UserPreferencesRepository preferencesRepository;

    private final MailQueryRepository mailQueryRepository;

    private final UserUtil userUtil;

    private final JwtTokenProvider jwtTokenProvider;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Boolean userRegister(@Valid UserRegisterRQ rq) {

        // 아이디가 중복인지 확인
        if(usersRepository.findById(rq.getId()).isPresent()){
            throw new AppException(ExceptionCode.DATA_DUPLICATE, "ID 중복");
        }

        // 이메일이 중복인지 확인
        if(usersRepository.findByEmail(rq.getEmail()).isPresent()){
            throw new AppException(ExceptionCode.DATA_DUPLICATE, "EMAIL 중복");
        }

        Optional<MailEntity> mailEntityWrapper = mailQueryRepository.findByTop1EmailAndMailType(rq.getEmail(), MailType.REGISTER_CERT);

        // 메일인증이 되어있는지 확인
        if(!mailEntityWrapper.isPresent() || !mailEntityWrapper.get().getCertYn()) {
            throw new AppException(ExceptionCode.MAIL_NOT_AUTHENTICATION);
        }

        UsersEntity usersEntity = UsersEntity.builder()
            .username(rq.getId())
            .password(passwordEncoder.encode(rq.getPassword()))
            .email(rq.getEmail())
            .nickname(rq.getNickname())
            .enabled(true)
            .accountNonExpired(true)
            .accountNonLock(true)
            .passFailCount(0)
            .build();

        AuthorityId authorityId = AuthorityId.builder()
            .username(rq.getId())
            .authority(UserType.USER.getValue())
            .build();

        AuthoritiesEntity authoritiesEntity = AuthoritiesEntity.builder()
            .id(authorityId)
            .build();

        UserPreferencesEntity preferencesEntity = UserPreferencesEntity.builder()
                .username(rq.getId())
                .user(usersEntity)
                .todoSortTypes(EnumSet.noneOf(SortType.class))  // 정렬 기본값 설정 - none
                .notificationTypes(EnumSet.noneOf(NotificationType.class))  // 알림 기본값 설정 - none
                .build();

        usersEntity.setAuthorities(authoritiesEntity);
        usersEntity.setPreferences(preferencesEntity);

        usersRepository.save(usersEntity); // casecade.ALL 되어있긴 한데, 내가 볼 때 이게 편해서 일단 둠
        preferencesRepository.save(preferencesEntity);

        return true;
    }

    @Transactional
    public SnsLoginRS snsLogin(SnsLoginRQ rq) {

        String platformUsername = rq.getPlatformType().name() + "_" + rq.getProviderId();

        Optional<UsersEntity> userWrapper = usersRepository.findById(platformUsername);

        TokenModel tokenModel = null;

        if(userWrapper.isPresent()){ // 기존 회원이였을 시 바로 토큰발급
            UsersEntity existingUser = userWrapper.get(); 

            Authentication authentication = new UsernamePasswordAuthenticationToken(existingUser.getUsername(), existingUser.getPassword(), existingUser.getAuthorities());
            tokenModel = jwtTokenProvider.createToken(authentication);
        } else { // 회원이 아니였을 시 회원가입후 바로 토큰발급

            if(usersRepository.findByEmail(rq.getEmail()).isPresent()){
                throw new AppException(ExceptionCode.DATA_DUPLICATE, "EMAIL 중복");
            }

            UsersEntity newUser = UsersEntity.builder()
                .username(platformUsername)
                .password(passwordEncoder.encode(System.currentTimeMillis() + rq.getPlatformType().name()))
                .email(rq.getEmail())
                .nickname(rq.getNickname())
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLock(true)
                .passFailCount(0)
                .build();

            AuthorityId authorityId = AuthorityId.builder()
                .username(platformUsername)
                .authority(UserType.USER.getValue())
                .build();

            AuthoritiesEntity authoritiesEntity = AuthoritiesEntity.builder()
                .id(authorityId)
                .build();

            UserPreferencesEntity preferencesEntity = UserPreferencesEntity.builder()
                    .username(platformUsername)
                    .user(newUser)
                    .todoSortTypes(EnumSet.noneOf(SortType.class))  // 정렬 기본값 설정 - none
                    .notificationTypes(EnumSet.noneOf(NotificationType.class))  // 알림 기본값 설정 - none
                    .build();

            newUser.setAuthorities(authoritiesEntity);
            newUser.setPreferences(preferencesEntity);

            usersRepository.save(newUser);
            preferencesRepository.save(preferencesEntity);

            Authentication authentication = new UsernamePasswordAuthenticationToken(newUser.getUsername(), newUser.getPassword(), newUser.getAuthorities());
            tokenModel = jwtTokenProvider.createToken(authentication);
        }
        
        SnsLoginRS result = new SnsLoginRS();
        result.setTokenModel(tokenModel);

        return result;
    }

    public boolean isDuplicated(NickCheckRQ rq) {
        return usersRepository.findById(rq.getNickname()).isPresent();
    }

}
