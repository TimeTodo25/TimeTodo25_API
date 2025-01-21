package com.pape.timetodo.domain.main.service;

import com.pape.timetodo.domain.main.model.dday.RegisterDdayRQ;
import com.pape.timetodo.domain.main.model.dday.RegisterDdayRS;
import com.pape.timetodo.domain.main.model.dday.UpdateDdayRQ;
import com.pape.timetodo.domain.main.model.dday.UpdateDdayRS;
import com.pape.timetodo.global.constant.StatusType;
import com.pape.timetodo.global.exception.AppException;
import com.pape.timetodo.global.exception.ExceptionCode;
import com.pape.timetodo.global.jpa.entity.DdayEntity;
import com.pape.timetodo.global.jpa.entity.UsersEntity;
import com.pape.timetodo.global.jpa.repository.DdayQueryRepository;
import com.pape.timetodo.global.jpa.repository.DdayRepository;
import com.pape.timetodo.global.util.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DdayService {

    private final UserUtil userUtil;

    private final DdayRepository ddayRepository;

    private final DdayQueryRepository ddayQueryRepository;

    /**
     * 디데이 등록
     * @param rq RegisterDayRQ
     * @return RegisterDdayRS
     */
    @Transactional
    public RegisterDdayRS registerDday(RegisterDdayRQ rq) {

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


    /**
     * 디데이 수정
     * @param rq UpdateDdayRQ
     * @return UpdateDdayRS
     */
    @Transactional
    public UpdateDdayRS updateDday(UpdateDdayRQ rq) {

        DdayEntity ddayEntity = ddayQueryRepository.findByIdAndUsersEntity(rq.getIdx(), userUtil.getUsersEntity());

        if(ddayEntity == null) throw new AppException(ExceptionCode.DATA_NOT_FIND);

        if(rq.getContent() != null) ddayEntity.setContent(rq.getContent());
        if(rq.getTargetDt() != null) ddayEntity.setTargetDt(rq.getTargetDt());

        ddayEntity.setUpdateDt(LocalDateTime.now());
        ddayRepository.save(ddayEntity);

        UpdateDdayRS result = new UpdateDdayRS();
        result.setUpdateDt(ddayEntity.getUpdateDt());

        return result;
    }


    /**
     * 디데이 삭제 [논리 삭제]
     * @param idx Long
     */
    public void deleteDday(Long idx) {
        UsersEntity usersEntity = userUtil.getUsersEntity();

        Optional<DdayEntity> ddayEntityWrapper = ddayRepository.findByIdxAndUsersEntity(idx, usersEntity);

        if(ddayEntityWrapper.isPresent()){

            DdayEntity ddayEntity = ddayEntityWrapper.get();
            ddayEntity.setDeleteDt(LocalDateTime.now());
            ddayEntity.setStatus(StatusType.DELETED.getValue());
        }
    }
    
}
