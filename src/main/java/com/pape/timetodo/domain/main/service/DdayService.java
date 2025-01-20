package com.pape.timetodo.domain.main.service;

import com.pape.timetodo.domain.main.model.dday.RegisterDdayRS;
import com.pape.timetodo.domain.main.model.dday.registerDdayRQ;
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

    /**
     * 디데이 등록
     * @param rq RegisterDayRQ
     * @return RegisterDdayRS
     */
    @Transactional
    public RegisterDdayRS registerDday(registerDdayRQ rq) {

        UsersEntity usersEntity = userUtil.getUsersEntity();

        DdayEntity ddayEntity = DdayEntity.builder()
                .content(rq.getContent())
                .targetDt(rq.getTargetDt())
                .targetDelYn(rq.getTargetDelYn())
                .usersEntity(usersEntity)
                .build();

        ddayRepository.save(ddayEntity);

        RegisterDdayRS result = new RegisterDdayRS();
        result.setDdayIdx(ddayEntity.getIdx());
        result.setUpdateDt(ddayEntity.getUpdateDt());

        return result;
    }
}
