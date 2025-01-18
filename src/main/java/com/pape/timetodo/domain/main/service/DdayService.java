package com.pape.timetodo.domain.main.service;

import com.pape.timetodo.domain.main.model.dday.RegisterDayRQ;
import com.pape.timetodo.global.jpa.entity.DdayEntity;
import com.pape.timetodo.global.jpa.entity.UsersEntity;
import com.pape.timetodo.global.jpa.repository.DdayRepository;
import com.pape.timetodo.global.util.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DdayService {

    private final DdayRepository ddayRepository;

    private final UserUtil userUtil;

    @Transactional
    public void registerDday(RegisterDayRQ rq) {

        UsersEntity usersEntity = userUtil.getUsersEntity();

        DdayEntity ddayEntity = DdayEntity.builder()
                .content(rq.getContent())
                .targetDt(rq.getTargetDt())
                .targetDelYn(rq.getTargetDelYn())
                .usersEntity(usersEntity)
                .build();

        ddayRepository.save(ddayEntity);
    }
}
