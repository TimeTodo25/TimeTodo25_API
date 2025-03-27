package com.pape.timetodo.domain.main.service;

import com.pape.timetodo.domain.main.model.LogoutSyncRQ;
import com.pape.timetodo.global.constant.DayWeekType;
import com.pape.timetodo.global.constant.StatusType;
import com.pape.timetodo.global.exception.AppException;
import com.pape.timetodo.global.exception.ExceptionCode;
import com.pape.timetodo.global.jpa.entity.*;
import com.pape.timetodo.global.jpa.repository.*;
import com.pape.timetodo.global.util.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataSyncService {

    private final UserUtil userUtil;

    private final CategoryRepository categoryRepository;

    private final RoutineRepository routineRepository;

    private final TodoRepository todoRepository;

    private final TodoTimerHistoryRepository timerRepository;

    private final DdayRepository ddayRepository;

    // local_idx = db_idx : 하위 엔티티와의 연결을 위한 일시적 맵핑
    private final ThreadLocal<Map<Long, Long>> categoryMap = ThreadLocal.withInitial(HashMap::new);
    private final ThreadLocal<Map<Long, Long>> routineMap = ThreadLocal.withInitial(HashMap::new);
    private final ThreadLocal<Map<Long, Long>> todoMap = ThreadLocal.withInitial(HashMap::new);

    @Transactional
    public void syncAll(LogoutSyncRQ rq) {

        try {
            UsersEntity user = userUtil.getUsersEntity();

            // 메모리 누수 방지 + 스레드 안전 격리를 위한 맵 초기화
            categoryMap.set(new HashMap<>());
            routineMap.set(new HashMap<>());
            todoMap.set(new HashMap<>());

            syncDday(rq.getDdays(), user);
            syncCategory(rq.getCategories(), user);
            syncRoutine(rq.getRoutines(), user);
            syncTodo(rq.getTodos(), user);

            for(LogoutSyncRQ.TodoTimerHistoryDTO dto : rq.getTimerHistories()) {
                syncTimer(dto);
            }

            // 투두 타이머 없는데 Status가 P면 삭제, 아니면 Status U로 수정 (P: 루틴에서 벗어남 + 진행도 0)
            List<TodoEntity> todosPreDelete = todoRepository.findByUsersEntityAndStatus(user, StatusType.PRE_DELETED.getValue());
            for(TodoEntity todo : todosPreDelete) {
                if(timerRepository.existsByTodoEntity(todo)) {
                    todo.setStatus(StatusType.UPDATED.getValue());
                    todoRepository.save(todo);
                } else {
                    todoRepository.delete(todo);
                }
            }

        } catch (Exception e) {
            log.error("동기화 중 오류 발생", e);
            throw new AppException(ExceptionCode.SYNC_ERROR);

        } finally {
            // 사용 끝난 후 메모리 해제
            categoryMap.remove();
            routineMap.remove();
        }
    }

    /**
     * 디데이
     */
    private void syncDday(List<LogoutSyncRQ.DdayDTO> ddayDtoList, UsersEntity user) {

        if(ddayDtoList == null || ddayDtoList.isEmpty()) {
            return;
        }

        for(LogoutSyncRQ.DdayDTO dto : ddayDtoList) {
            DdayEntity entity;

            if(dto.getDdayIdx() == null) { // 기존 idx 없으면 생성
                entity = new DdayEntity();
            } else { // 기존 idx 있으면 찾기 -> 수정
                entity = ddayRepository.findById(dto.getDdayIdx()).orElse(new DdayEntity());
            }

            entity.setUsersEntity(user);
            entity.setContent(dto.getContent());
            entity.setTargetDt(dto.getTargetDt());
            entity.setTargetDelYn(dto.getTargetDelYn());

            ddayRepository.save(entity);
        }
        // GenerationType.IDENTITY 사용중이라, saveAll과 복수의 save 호출 사이에 성능 차이가 크지 않다고 하여,
        // 트랜잭션 보호 겸 각각 save 호출하는 것으로 결정

    }

    /**
     * 카테고리
     */
    private void syncCategory(List<LogoutSyncRQ.CategoryDTO> categoryDtoList, UsersEntity user) {

        if(categoryDtoList == null || categoryDtoList.isEmpty()) {
            return;
        }

        for(LogoutSyncRQ.CategoryDTO dto : categoryDtoList) {
            CategoryEntity entity;

            if(dto.getCategoryIdx() == null) { // 기존 idx 없으면 생성
                entity = new CategoryEntity();
            } else { // 기존 idx 있으면 찾기 -> 수정
                entity = categoryRepository.findById(dto.getCategoryIdx()).orElse(new CategoryEntity());
            }

            entity.setUsersEntity(user);
            entity.setTitle(dto.getCategoryTitle());
            entity.setPublicStatus(dto.getPublicStatus());
            entity.setMainColor(dto.getMainColor());

            categoryRepository.save(entity);

            // 로컬Idx = DBIdx 키-값으로 저장
            categoryMap.get().put(dto.getCategoryLocalIdx(), entity.getIdx());

        }
    }

    /**
     * 루틴
     */
    private void syncRoutine(List<LogoutSyncRQ.RoutineDTO> routineDtoList, UsersEntity user) {

        if(routineDtoList == null || routineDtoList.isEmpty()) {
            return;
        }

        for(LogoutSyncRQ.RoutineDTO dto : routineDtoList) {
            RoutineEntity entity;

            if (dto.getRoutineIdx() == null) { // 기존 idx 없으면 생성
                entity = new RoutineEntity();
            } else { // 기존 idx 있으면 찾기 -> 수정
                entity = routineRepository.findById(dto.getRoutineIdx()).orElse(new RoutineEntity());
                if(dto.getCycleValue() != null) { // 기존 루틴 수정인데, 반복 타입을 바꿨으면 기존 투두 중 수행 안 한 건 다 삭제예정 처리 -> todo_, timer 업뎃 후 삭제
                    List<TodoEntity> todosToDelete = todoRepository.findByRoutineEntityAndProgressStatus(entity, 0);
                    for(TodoEntity todo : todosToDelete) {
                        todo.setStatus(StatusType.PRE_DELETED.getValue()); // **
                    }
                }
            }

            entity.setUsersEntity(user);

            // 카테고리 로컬 인덱스로 해당하는 카테고리 DB 인덱스 찾아, 루틴의 카테고리 설정
            // 예외처리는 Optional이라서 해두지만, map에 데이터가 있다면 반드시 idx가 있어야 함
            Long categoryIdx = categoryMap.get().get(dto.getCategoryLocalIdx());
            CategoryEntity category = categoryRepository.findById(categoryIdx).orElseThrow(() -> new AppException(ExceptionCode.DATA_NOT_FIND));
            entity.setCategoryEntity(category);

            entity.setContent(dto.getContent());
            entity.setCycleType(dto.getCycleType());

            StringBuilder cycleValue = new StringBuilder();
            if (dto.getCycleValue() != null) {
                dto.getCycleValue().forEach(value -> {
                    switch (dto.getCycleType()) {
                        case EVERY_DAY:
                            break;
                        case EVERY_MONTH:
                            if (!(value >= 1 && 31 >= value)) {
                                throw new AppException(ExceptionCode.NON_VALID_PARAMETER, "일 지정은 1~31까지 숫자를 넣어야 합니다.");
                            }
                            break;
                        case EVERY_WEEK:
                            DayWeekType dayWeek = DayWeekType.fromValue(value.intValue());
                            if (dayWeek == null) {
                                throw new AppException(ExceptionCode.NON_VALID_PARAMETER, "요일은 1~7까지 숫자를 넣어야 합니다.");
                            }
                            break;
                    }
                    cycleValue.append(value);
                    cycleValue.append(",");
                });
                cycleValue.deleteCharAt(cycleValue.length() - 1);
            }
            entity.setCycleValue(cycleValue.toString());

            entity.setStartDt(dto.getStartDt());
            entity.setEndDt(dto.getEndDt());
            if(dto.getStartTargetTm() != null) entity.setStartTargetTm(dto.getStartTargetTm());
            if(dto.getEndTargetTm() != null) entity.setEndTargetTm(dto.getStartTargetTm());

            routineRepository.save(entity);

            // 로컬Idx = DBIdx 키-값으로 저장
            routineMap.get().put(dto.getRoutineLocalIdx(), entity.getIdx());
        }
    }

    /**
     * 투두
     */
    private void syncTodo(List<LogoutSyncRQ.TodoDTO> todoDtoList, UsersEntity user) {

        if(todoDtoList == null || todoDtoList.isEmpty()) {
            return;
        }

        for(LogoutSyncRQ.TodoDTO dto : todoDtoList) {
            TodoEntity entity;

            if (dto.getTodoIdx() == null) {
                entity = new TodoEntity();
            } else {
                entity = todoRepository.findById(dto.getTodoIdx()).orElse(new TodoEntity());
            }

            entity.setUsersEntity(user);

            Long categoryIdx = categoryMap.get().get(dto.getCategoryLocalIdx());
            CategoryEntity category = categoryRepository.findById(categoryIdx).orElseThrow(() -> new AppException(ExceptionCode.DATA_NOT_FIND));
            entity.setCategoryEntity(category);

            if(dto.getRoutineLocalIdx() != null) {
                Long routineIdx = routineMap.get().get(dto.getRoutineLocalIdx());
                RoutineEntity routine = routineRepository.findById(routineIdx).orElseThrow(() -> new AppException(ExceptionCode.DATA_NOT_FIND));
                entity.setRoutineEntity(routine);
            }

            entity.setContent(dto.getContent());
            entity.setTargetDate(dto.getDate());
            if(dto.getStartTargetTm() != null) entity.setStartTargetTm(dto.getStartTargetTm());
            if(dto.getEndTargetTm() != null) entity.setEndTargetTm(dto.getEndTargetTm());

            if(dto.getProgressStatus() != null) entity.setProgressStatus(dto.getProgressStatus());
            else entity.setProgressStatus(0);

            todoRepository.save(entity);

            // 로컬Idx = DBIdx 키-값으로 저장
            todoMap.get().put(dto.getTodoLocalIdx(), entity.getIdx());
        }
    }

    /**
     * 투두 타이머
     */
    private void syncTimer(LogoutSyncRQ.TodoTimerHistoryDTO dto) {

        Long todoIdx;
        if(dto.getTodoIdx() == null) {
            if(dto.getTodoLocalIdx() == null) throw new AppException(ExceptionCode.SYNC_ERROR_LOCAL_IDX);
            todoIdx = todoMap.get().get(dto.getTodoLocalIdx());
        } else {
            todoIdx = dto.getTodoIdx();
        }
        TodoEntity todoEntity = todoRepository.findById(todoIdx).orElseThrow(() -> new AppException(ExceptionCode.DATA_NOT_FIND));

        List<TodoTimerHistoryEntity> timerHistoryEntities = new ArrayList<>();

        for(LogoutSyncRQ.TodoTimerHistoryDTO.TimeData time: dto.getTimeDatas()){

            Duration duration = Duration.between(time.getStartDt(), time.getEndDt());
            long totalSecond = duration.toSeconds();

            TodoTimerHistoryEntity timerEntity = TodoTimerHistoryEntity.builder()
                    .historyStartDt(time.getStartDt())
                    .historyEndDt(time.getEndDt())
                    .totalTm(LocalTime.ofSecondOfDay(totalSecond))
                    .todoEntity(todoEntity)
                    .build();

            timerHistoryEntities.add(timerEntity);
        }

        timerRepository.saveAll(timerHistoryEntities);

    }

}
