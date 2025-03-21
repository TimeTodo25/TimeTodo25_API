package com.pape.timetodo.domain.main.service;

import com.pape.timetodo.domain.main.model.LogoutSyncRQ;
import com.pape.timetodo.domain.main.model.todo.CreateTodoRQ;
import com.pape.timetodo.domain.main.model.todo.RegistTodoTimerRQ;
import com.pape.timetodo.global.constant.DayWeekType;
import com.pape.timetodo.global.constant.StatusType;
import com.pape.timetodo.global.exception.AppException;
import com.pape.timetodo.global.exception.ExceptionCode;
import com.pape.timetodo.global.jpa.entity.*;
import com.pape.timetodo.global.jpa.repository.*;
import com.pape.timetodo.global.util.UserUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

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


    private <T, D> void syncEntityData(List<D> dtos,
                                       JpaRepository<T, ?> repository,
                                       Function<D, T> dtoToEntityConverter) {
        if(dtos == null || dtos.isEmpty()) {
            return;
        }

        for (D dto : dtos) {
            T entity = dtoToEntityConverter.apply(dto);
            repository.save(entity);
        }
    }

    @Transactional
    public void syncAll(LogoutSyncRQ rq) {
        UsersEntity user = userUtil.getUsersEntity();

        syncEntityData(rq.getDdays(), ddayRepository,
                dto -> convertToDday((LogoutSyncRQ.DdayDTO) dto, user));

        syncEntityData(rq.getCategories(), categoryRepository,
                dto -> convertToCategory((LogoutSyncRQ.CategoryDTO) dto, user));

        syncEntityData(rq.getRoutines(), routineRepository,
                dto -> convertToRoutine((LogoutSyncRQ.RoutineDTO) dto, user));

        syncEntityData(rq.getTodos(), todoRepository,
                dto -> convertToTodo((LogoutSyncRQ.TodoDTO) dto, user));

        for(LogoutSyncRQ.TodoTimerHistoryDTO dto : rq.getTimerHistories()) {
            syncTimer(dto);
        }
    }

    /**
     * 디데이
     */
    private DdayEntity convertToDday(LogoutSyncRQ.DdayDTO dto, UsersEntity user) {
        DdayEntity entity;
        if (dto.getIdx() == null) {
            entity = new DdayEntity();
            entity.setUsersEntity(user);
        } else {
            entity = ddayRepository.findById(dto.getIdx())
                    .orElse(new DdayEntity());
            entity.setUsersEntity(user);
        }

        entity.setContent(dto.getContent());
        entity.setTargetDt(dto.getTargetDt());
        entity.setTargetDelYn(dto.getTargetDelYn());

        return entity;
    }

    /**
     * 카테고리
     */
    private CategoryEntity convertToCategory(LogoutSyncRQ.CategoryDTO dto, UsersEntity user) {
        CategoryEntity entity;
        if (dto.getIdx() == null) {
            entity = new CategoryEntity();
            entity.setUsersEntity(user);
        } else {
            entity = categoryRepository.findById(dto.getIdx())
                    .orElse(new CategoryEntity());
            entity.setUsersEntity(user);
        }

        entity.setTitle(dto.getCategoryTitle());
        entity.setPublicStatus(dto.getPublicStatus());
        entity.setMainColor(dto.getMainColor());

        return entity;
    }

    /**
     * 루틴
     */
    private RoutineEntity convertToRoutine(LogoutSyncRQ.RoutineDTO dto, UsersEntity user) {
        RoutineEntity entity;
        if (dto.getRoutineIdx() == null) {
            entity = new RoutineEntity();
            entity.setUsersEntity(user);
        } else {
            entity = routineRepository.findById(dto.getRoutineIdx())
                    .orElse(new RoutineEntity());
            entity.setUsersEntity(user);
        }

        entity.setContent(dto.getContent());
        CategoryEntity category = categoryRepository.findById(dto.getCategoryIdx()).orElseThrow(() -> new AppException(ExceptionCode.DATA_NOT_FIND));
        entity.setCategoryEntity(category);
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
        entity.setStartTargetTm(dto.getStartTargetTm());
        entity.setEndTargetTm(dto.getStartTargetTm());

        List<TodoEntity> todoEntityList = createTodoList(dto, user, category, entity);
        entity.setTodoEntities(todoEntityList);

        return entity;
    }

    /**
     * 투두
     */
    private TodoEntity convertToTodo(LogoutSyncRQ.TodoDTO dto, UsersEntity user) {
        TodoEntity entity;
        if (dto.getIdx() == null) {
            entity = new TodoEntity();
            entity.setUsersEntity(user);
        } else {
            entity = todoRepository.findById(dto.getIdx())
                    .orElse(new TodoEntity());
            entity.setUsersEntity(user);
        }

        entity.setContent(dto.getContent());
        entity.setCategoryEntity(categoryRepository.findById(dto.getCategoryIdx()).orElseThrow(() -> new AppException(ExceptionCode.DATA_NOT_FIND)));
        entity.setTargetDate(dto.getDate());
        entity.setStartTargetTm(dto.getStartTargetTm());
        entity.setEndTargetTm(dto.getEndTargetTm());
        if(dto.getProgressStatus() != null) entity.setProgressStatus(dto.getProgressStatus());
        else entity.setProgressStatus(0);

        return entity;
    }

    /**
     * 투두 타이머
     */
    private void syncTimer(LogoutSyncRQ.TodoTimerHistoryDTO dto) {

        TodoEntity todoEntity = todoRepository.findById(dto.getTodoIdx()).orElseThrow(() -> new AppException(ExceptionCode.DATA_NOT_FIND));

        List<TodoTimerHistoryEntity> timerHistoryEntities = new ArrayList<>();

        for(RegistTodoTimerRQ.TimeData time: dto.getTimeDatas()){

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


    private List<TodoEntity> createTodoList(LogoutSyncRQ.RoutineDTO rq, UsersEntity usersEntity, CategoryEntity categoryEntity, RoutineEntity routineEntity) {
        List<TodoEntity> todoEntityList = new ArrayList<>();

        // getter 잦은 호출 피하기 위해 각 변수 선언 및 할당
        String content = rq.getContent();
        Long categoryIdx = rq.getCategoryIdx();
        LocalTime startTargetTm = rq.getStartTargetTm();
        LocalTime endTargetTm = rq.getEndTargetTm();

        // 매일
        if (rq.getCycleType().equals(RoutineEntity.CycleType.EVERY_DAY)) {
            // 날짜 마다 Todo_ 데이터 추가
            for (LocalDate date = rq.getStartDt(); !date.isAfter(rq.getEndDt()); date = date.plusDays(1)) {

                CreateTodoRQ newTodo = new CreateTodoRQ();
                newTodo.setContent(content);
                newTodo.setCategoryIdx(categoryIdx);
                newTodo.setDate(date);
                newTodo.setStartTargetTm(startTargetTm);
                newTodo.setEndTargetTm(endTargetTm);

                TodoEntity todo = createTodoByRoutine(newTodo, categoryEntity, usersEntity, routineEntity);
                todoEntityList.add(todo);
            }
        }
        // 매주
        else if (rq.getCycleType().equals(RoutineEntity.CycleType.EVERY_WEEK)) {
            List<Byte> cycleValues = rq.getCycleValue();
            List<DayWeekType> selectedDays = cycleValues.stream()
                    .map(value -> DayWeekType.fromValue(value.intValue()))
                    .filter(Objects::nonNull)
                    .toList();

            for (LocalDate date = rq.getStartDt(); !date.isAfter(rq.getEndDt()); date = date.plusDays(1)) {
                // 현재 날짜의 요일을 DayWeekType으로 변환
                DayWeekType currentDayType = DayWeekType.fromValue(date.getDayOfWeek().getValue());

                // 선택된 요일인 경우에만 Todo_ 생성
                if (selectedDays.contains(currentDayType)) {
                    CreateTodoRQ newTodo = new CreateTodoRQ();
                    newTodo.setContent(content);
                    newTodo.setCategoryIdx(categoryIdx);
                    newTodo.setDate(date);
                    newTodo.setStartTargetTm(startTargetTm);
                    newTodo.setEndTargetTm(endTargetTm);

                    TodoEntity todo = createTodoByRoutine(newTodo, categoryEntity, usersEntity, routineEntity);
                    todoEntityList.add(todo);
                }
            }
        }
        // 매달
        else if (rq.getCycleType().equals(RoutineEntity.CycleType.EVERY_MONTH)) {
            List<Byte> cycleValues = rq.getCycleValue();
            List<Integer> selectedDates = cycleValues.stream()
                    .map(value -> (int) value)
                    .toList();

            for (LocalDate date = rq.getStartDt(); !date.isAfter(rq.getEndDt()); date = date.plusDays(1)) {
                // 현재 날짜의 일자를 가져옴 (1-31)
                int dayOfMonth = date.getDayOfMonth();

                // 선택된 날짜인 경우에만 Todo_ 생성
                if (selectedDates.contains(dayOfMonth)) {
                    CreateTodoRQ newTodo = new CreateTodoRQ();
                    newTodo.setContent(content);
                    newTodo.setCategoryIdx(categoryIdx);
                    newTodo.setDate(date);
                    newTodo.setStartTargetTm(startTargetTm);
                    newTodo.setEndTargetTm(endTargetTm);

                    TodoEntity todo = createTodoByRoutine(newTodo, categoryEntity, usersEntity, routineEntity);
                    todoEntityList.add(todo);
                }
            }
        }

        // 날짜 겹치는 기존의 TodoEntity 삭제
        if (rq.getTodoIdx() != null) {
            Optional<TodoEntity> oldTodo = todoRepository.findById(rq.getTodoIdx());
            if (oldTodo.isPresent() && (oldTodo.get().getTargetDate().isEqual(rq.getStartDt()) || oldTodo.get().getTargetDate().isAfter(rq.getStartDt())) &&
                    (oldTodo.get().getTargetDate().isEqual(rq.getEndDt()) || oldTodo.get().getTargetDate().isBefore(rq.getEndDt()))) {
                todoRepository.delete(oldTodo.get());
            }
            oldTodo.ifPresent(todoRepository::delete); // 기존 투두는 날짜 겹치든 말든 삭제가 디폴트 TODO: 삭제 안함 옵션이 생길 예정...
        }

        return todoEntityList;
    }

    public TodoEntity createTodoByRoutine(@Valid CreateTodoRQ rq, CategoryEntity categoryEntity, UsersEntity usersEntity, RoutineEntity routineEntity) {

        TodoEntity.TodoEntityBuilder todoEntityBuilder = TodoEntity.builder()
                .content(rq.getContent())
                .categoryEntity(categoryEntity)
                .routineEntity(routineEntity)
                .usersEntity(usersEntity)
                .targetDate(rq.getDate())
                .status(StatusType.NORMAL.getValue());

        if (rq.getStartTargetTm() != null) todoEntityBuilder.startTargetTm(rq.getStartTargetTm());
        if (rq.getEndTargetTm() != null) todoEntityBuilder.endTargetTm(rq.getEndTargetTm());

        TodoEntity todoEntity = todoEntityBuilder.build();

        todoEntity = todoRepository.save(todoEntity);

        return todoEntity;
    }

}
