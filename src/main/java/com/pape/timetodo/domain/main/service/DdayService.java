package com.pape.timetodo.domain.main.service;

import com.pape.timetodo.domain.main.model.dday.*;
import com.pape.timetodo.global.constant.SortType;
import com.pape.timetodo.global.constant.StatusType;
import com.pape.timetodo.global.exception.AppException;
import com.pape.timetodo.global.exception.ExceptionCode;
import com.pape.timetodo.global.jpa.entity.DdayEntity;
import com.pape.timetodo.global.jpa.entity.UserPreferencesEntity;
import com.pape.timetodo.global.jpa.entity.UsersEntity;
import com.pape.timetodo.global.jpa.repository.DdayQueryRepository;
import com.pape.timetodo.global.jpa.repository.DdayRepository;
import com.pape.timetodo.global.util.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    @Transactional
    public void deleteDday(Long idx) {
        UsersEntity usersEntity = userUtil.getUsersEntity();

        Optional<DdayEntity> ddayEntityWrapper = ddayRepository.findByIdxAndUsersEntity(idx, usersEntity);

        if(ddayEntityWrapper.isPresent()){

            DdayEntity ddayEntity = ddayEntityWrapper.get();
            ddayEntity.setDeleteDt(LocalDateTime.now());
            ddayEntity.setStatus(StatusType.DELETED.getValue());

            ddayRepository.save(ddayEntity); // JPA 더티체킹을 믿지만, 만일에 대비해서
        }
    }

    /**
     * 내 디데이 목록 조회
     * @return MyDdayRS
     */
    public MyDdayRS getMyDday() {

        UsersEntity usersEntity = userUtil.getUsersEntity();

        UserPreferencesEntity preferencesEntity = usersEntity.getUserPreferences();
        List<SortType> ddaySortTypeList = preferencesEntity.getDdaySortType().stream().toList();

        LocalDate date = LocalDate.now();
        List<DdayEntity> ddayList = ddayQueryRepository.findDdayByUsersEntity(usersEntity, date, ddaySortTypeList);
        MyDdayRS result = new MyDdayRS();
        result.setDdayList(ddayList.stream().map(dday -> {
            MyDdayRS.DdayModel model = new MyDdayRS.DdayModel();
            model.setDdayIdx(dday.getIdx());
            model.setContent(dday.getContent());
            model.setDdayDate(dday.getTargetDt());
            return model;
        })
        .collect(Collectors.toList()));

        return result;
    }

    /**
     * 디데이 단건 상세 조회
     * @param idx Long
     * @return GetDdayDetailRS
     */
    public GetDdayDetailRS detailDday(Long idx) {

        UsersEntity usersEntity = userUtil.getUsersEntity();

        DdayEntity dday = ddayRepository.findByIdxAndUsersEntity(idx, usersEntity)
                .orElseThrow(() -> new AppException(ExceptionCode.DATA_NOT_FIND));

        GetDdayDetailRS result = new GetDdayDetailRS();
        result.setContent(dday.getContent());
        result.setDdayDate(dday.getTargetDt());
        result.setTargetDelYn(dday.getTargetDelYn());

        return result;
    }

    /**
     * 디데이 자동 삭제
     * 매일 자정 직후 실행
     */
    @Scheduled(cron = "3 0 0 * * *") // 매일 자정 3초
    @Transactional
    public void autoCloseDday() {
        List<DdayEntity> ddayList = ddayQueryRepository.findAllToClose(LocalDate.now().minusDays(1)); // 기준 날짜: 어제

        LocalDateTime now = LocalDateTime.now();
        Character deleted = StatusType.DELETED.getValue();
        for(DdayEntity dday : ddayList) {
            dday.setDeleteDt(now);
            dday.setStatus(deleted);
        }
        ddayRepository.saveAll(ddayList);
    }

    /**
     * 디데이 완료 혹은 완료 취소
     * @param idx Long
     * @return UpdateDdayRS
     */
    @Transactional
    public UpdateDdayRS completeDday(Long idx) {

        DdayEntity dday = ddayRepository.findById(idx).orElseThrow(() -> new AppException(ExceptionCode.DATA_NOT_FIND));
        dday.setCompleted(!dday.getCompleted());
        dday.setUpdateDt(LocalDateTime.now());
        ddayRepository.save(dday);

        UpdateDdayRS result = new UpdateDdayRS();
        result.setUpdateDt(dday.getUpdateDt());

        return result;
    }
}
