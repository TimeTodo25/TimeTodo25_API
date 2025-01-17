package com.pape.timetodo.domain.main.service;

import com.pape.timetodo.domain.main.model.DdayTodoModel;
import com.pape.timetodo.domain.main.model.GetCategoryModel;
import com.pape.timetodo.domain.main.model.GetTodoModel;
import com.pape.timetodo.domain.main.model.dday.RegisterDayRQ;
import com.pape.timetodo.domain.main.model.routhin.RegisterRoutineRQ;
import com.pape.timetodo.domain.main.model.routhin.RegisterRoutineRS;
import com.pape.timetodo.domain.main.model.routhin.UpdateRoutineRQ;
import com.pape.timetodo.domain.main.model.todo.*;
import com.pape.timetodo.domain.main.model.todo.GetTodoDetailRS.TimerHistory;
import com.pape.timetodo.domain.main.model.todo.RegistTodoTimerRQ.TimeData;
import com.pape.timetodo.global.constant.DayWeekType;
import com.pape.timetodo.global.exception.AppException;
import com.pape.timetodo.global.exception.ExceptionCode;
import com.pape.timetodo.global.jpa.entity.*;
import com.pape.timetodo.global.jpa.entity.RoutineEntity.CycleType;
import com.pape.timetodo.global.jpa.repository.*;
import com.pape.timetodo.global.util.UserUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;

    private final TodoQueryRepository todoQueryRepository;
    
    private final CategoryRepository categoryRepository;

    private final RoutineRepository routineRepository;

    private final TodoTimerHistoryRepository todoTimerHistoryRepository;

    private final DdayRepository ddayRepository;

    private final UserUtil userUtil;
    
    /**
     * Todo 리스트 추가, Category와 Users정보 확인
     * @param rq
     * @return
     */
    @Transactional
    public CreateTodoRS createTodo(@Valid CreateTodoRQ rq) {

        UsersEntity usersEntity = userUtil.getUsersEntity();

        // 카테고리가 있으며 자신이 만든 카테고리인지 확인
        CategoryEntity categoryEntity = categoryRepository.findByIdxAndUsersEntity(rq.getCategoryIdx(), usersEntity)
            .orElseThrow(() -> new AppException(ExceptionCode.NON_VALID_PARAMETER, "잘못된 카테고리 IDX"));

        TodoEntity.TodoEntityBuilder todoEntityBuilder = TodoEntity.builder()
            .content(rq.getContent())
            .categoryEntity(categoryEntity)
            .usersEntity(usersEntity)
            .targetDate(rq.getDate())
            .deleted(false)
            .totalTm(LocalTime.of(0, 0, 0));

        if(rq.getStartTargetTm() != null) todoEntityBuilder.startTargetTm(rq.getStartTargetTm());
        if(rq.getEndTargetTm() != null) todoEntityBuilder.endTargetTm(rq.getEndTargetTm());

        TodoEntity todoEntity = todoEntityBuilder.build();

        todoEntity = todoRepository.save(todoEntity);
        
        CreateTodoRS result = new CreateTodoRS();
        result.setContent(todoEntity.getContent());
        result.setCategoryTitle(todoEntity.getCategoryEntity().getTitle());
        result.setCreateDt(todoEntity.getCreateDt());
        result.setUpdateDt(todoEntity.getUpdateDt());

        return result;
    }

    /**
     * 홈화면 :: Todo데이터 조회 D-day Todo, Category Todo
     * @return
     */
    @Transactional(readOnly = true)
    public GetHomeTodoRS getHomeTodo(GetHomeTodoRQ rq) {

        UsersEntity usersEntity = userUtil.getUsersEntity();

        List<GetCategoryModel> categoryList = this.getMyCategoryList(usersEntity, rq);
        List<DdayTodoModel> intervalDayTodoModels = todoQueryRepository.findDdayTodoByUsersEntity(usersEntity);

        GetHomeTodoRS result = new GetHomeTodoRS();
        result.setCategoryList(categoryList);
        result.setIntervalDayTodoList(intervalDayTodoModels);

        return result;
    }

    /**
     * 루틴 등록
     * @param rq
     * @return
     */
    @Transactional
    public RegisterRoutineRS registerRoutine(RegisterRoutineRQ rq) {

        UsersEntity usersEntity = userUtil.getUsersEntity();

        // 실제로 있는 TodoData인지 그리고 자기 자신 TodoData인지 확인
        TodoEntity todoEntity = todoRepository.findByIdxAndUsersEntity(rq.getTodoIdx(), usersEntity)
            .orElseThrow(() -> new AppException(ExceptionCode.DATA_NOT_FIND));

        // 루틴등록을 중복으로 했는지 확인
        Boolean duplication = routineRepository.findByTodoEntity(todoEntity)
            .isPresent();
            
        if(duplication) throw new AppException(ExceptionCode.DATA_DUPLICATE, "이미 루틴으로 등록되어있습니다.");

        String rm = validationRoutineCycleType(rq.getCycleType(), rq.getCycleValue());

        StringBuilder cycleValue = new StringBuilder();

        // 루틴 사이클 지정일이 있을 시
        if(rq.getCycleValue() != null){
            rq.getCycleValue().forEach(value -> {
                
                switch (rq.getCycleType()) {
                    case EVERY_DAY:
                        break;
                    case EVERY_MONTH:
                        if(!(value >= 1 && 31 >= value)) {
                            throw new AppException(ExceptionCode.NON_VALID_PARAMETER, "일 지정은 1~31까지 숫자를 넣어야 합니다.");
                        } 

                        break;
                    case EVERY_WEEK:
                        DayWeekType dayWeek = DayWeekType.fromValue(value.intValue());
                        if(dayWeek == null){
                            throw new AppException(ExceptionCode.NON_VALID_PARAMETER, "요일은 1~7까지 숫자를 넣어야 합니다.");
                        }
                        break;
                }    
                cycleValue.append(value);
                cycleValue.append(",");
            });
            cycleValue.deleteCharAt(cycleValue.length() - 1);
        }

        RoutineEntity routineEntity = RoutineEntity.builder()
            .cycleType(rq.getCycleType())
            .cycleValue(cycleValue.toString())
            .rm(rm)
            .todoEntity(todoEntity)
            .usersEntity(usersEntity)
            .startDt(rq.getStartDt())
            .endDt(rq.getEndDt())
            .build();

        routineEntity = routineRepository.save(routineEntity);
        
        RegisterRoutineRS result = new RegisterRoutineRS();
        result.setIdx(routineEntity.getIdx());
        result.setCycleType(routineEntity.getCycleType().name());
        result.setRm(routineEntity.getRm());
        result.setCreateDt(routineEntity.getCreateDt());
        result.setUpdateDt(routineEntity.getUpdateDt());

        return result;
    }

    /**
     * 루틴 타입이 요일 지정일 떄, value가 1~7 인지 체크
     * @param cycleType
     * @param cycleValue
     * @return RM 비고 데이터 조회
     */
    private String validationRoutineCycleType(CycleType cycleType, List<Byte> cycleValue){
        StringBuilder rm = new StringBuilder();

        switch(cycleType){
            case EVERY_DAY:
                rm.append("매일 ");
                break;
            case EVERY_MONTH:
                for(Byte value : cycleValue){
                    
                    if(!(value >= 1 && 31 >= value)) {
                        throw new AppException(ExceptionCode.NON_VALID_PARAMETER, "일 지정은 1~31까지 숫자를 넣어야 합니다.");
                    } 

                    rm.append(value);
                    rm.append("일");
                    rm.append(",");
                }
                break;
            case EVERY_WEEK:
                for(Byte value : cycleValue){

                    DayWeekType dayWeek = DayWeekType.fromValue(value.intValue());
    
                    if(dayWeek == null){
                        throw new AppException(ExceptionCode.NON_VALID_PARAMETER, "요일은 1~7까지 숫자를 넣어야 합니다.");
                    }
                    rm.append(dayWeek.getDesc());
                    rm.append(",");
                }
                break;
        }

        rm.deleteCharAt(rm.length() - 1);
        return rm.toString();
    }

    /**
     * 개인 카테고리 및 투두 조회
     * @param usersEntity
     * @return
     */
    private List<GetCategoryModel> getMyCategoryList(UsersEntity usersEntity, GetHomeTodoRQ rq){

        if(usersEntity == null){
            usersEntity = userUtil.getUsersEntity();
        }                

        return categoryRepository.findByUsersEntity(usersEntity).stream()
            .map(entity -> {
                List<GetTodoModel> todoList = todoQueryRepository.findByCategoryAndDate(entity, rq.getDate()).stream()
                    .filter(todo -> {
                        // TodoData가 없는데 Routine만 fasle가 떨어질 수 있음
                        if(todo.getIdx() == null) return false;
                        
                        // 루틴 일 경우 금일의 루틴이 아니면 필터함
                        if(todo.getRoutineYn()){
    
                            CycleType cycleType = todo.getCycleType();
                            List<Byte> clcyeValue = new ArrayList<>();

                            // 사이클 벨류가 없는경우가 있음, 사이클 타입이 매일인경우
                            if(todo.getCycleValue() != null && !todo.getCycleValue().isBlank()){
                                // 사이클 밸류를 , 로 구분자로 지었기 떄문에 , 를 기준으로 List를 만듬
                                clcyeValue.addAll(
                                    Arrays.asList(todo.getCycleValue().split(",")).stream()
                                    .map(Byte::parseByte)
                                    .toList()
                                );
                            }
    
                            validationRoutineCycleType(cycleType, clcyeValue);

                            switch (cycleType) {
                                case EVERY_DAY:
                                    return true;
                                case EVERY_MONTH:
                                    Integer todayValue = LocalDate.now().getDayOfMonth();
                                    return clcyeValue.contains(todayValue.byteValue());
                                case EVERY_WEEK:
                                    Integer todayWeekValue = LocalDate.now().getDayOfWeek().getValue();
                                    return clcyeValue.contains(todayWeekValue.byteValue());
                            }  
                        } 
                        // 루틴이 아니면 Filter X
                        return true;

                    })
                    .toList();

                GetCategoryModel category = new GetCategoryModel();
                category.setIdx(entity.getIdx());
                category.setTitle(entity.getTitle());
                category.setMainColor(entity.getMainColor());
                category.setCreateDt(entity.getCreateDt());
                category.setUpdateDt(entity.getUpdateDt());
                category.setTodoList(todoList);

                return category;
            })
            .filter(model -> !model.getTodoList().isEmpty())
            .toList();
    }

    /**
     * TodoData Delete
     * @param idx
     * @return DB에 데이터가 미존재해도 TRUE
     */
    @Transactional
    public Boolean deleteTodo(Long idx) {

        UsersEntity usersEntity = userUtil.getUsersEntity();

        Optional<TodoEntity> todoEntityWrapper = todoRepository.findByIdxAndUsersEntity(idx, usersEntity);

        if(todoEntityWrapper.isPresent()){

            TodoEntity todoEntity = todoEntityWrapper.get();
            todoEntity.setDeleteDt(LocalDateTime.now());
            todoEntity.setDeleted(true);

            return true;
        } else { // Data가 없을 경우 TRUE 반환

            return true;
        }
    }

    /**
     * TODO데이터 업데이트
     * @param rq
     */
    @Transactional
    public void updateTodo(UpdateTodoRQ rq) {

        TodoEntity todoEntity = this.getMyTodoData(rq.getIdx());

        if(rq.getContent() != null) todoEntity.setContent(rq.getContent());
        if(rq.getTargetDate() != null) todoEntity.setTargetDate(rq.getTargetDate());
        if(rq.getStartTargetTm() != null) todoEntity.setStartTargetTm(rq.getStartTargetTm());
        if(rq.getEndTargetTm() != null) todoEntity.setEndTargetTm(rq.getEndTargetTm());
    }

    /**
     * 투두 시간기록 등록
     * @param rq
     */
    @Transactional
    public void registTodoTimer(RegistTodoTimerRQ rq) {
        TodoEntity todoEntity = this.getMyTodoData(rq.getTodoIdx());
        LocalTime todoTotalTm = todoEntity.getTotalTm();

        // 이미 타이머 기록과 총 시간이 있을 경우 삭제하고 새로 생성 -> Todo 추후 리팩토링 고려
        if(todoTotalTm != LocalTime.of(0, 0, 0)) {
            todoEntity.getTodoTimerHistoryEntities().clear();
            todoTotalTm = LocalTime.of(0, 0, 0);
        }

        List<TodoTimerHistoryEntity> timerHistoryEntities = new ArrayList<>();

        for(TimeData time: rq.getTimeDatas()){

            Duration duration = Duration.between(time.getStartDt(), time.getEndDt());
            long totalSecond = duration.toSeconds();
            todoTotalTm = todoTotalTm.plusSeconds(totalSecond);

            TodoTimerHistoryEntity timerEntity = TodoTimerHistoryEntity.builder()
                .historyStartDt(time.getStartDt())
                .historyEndDt(time.getEndDt())
                .totalTm(LocalTime.ofSecondOfDay(totalSecond))
                .todoEntity(todoEntity)
                .build();

            timerHistoryEntities.add(timerEntity);
        }

        todoEntity.setTotalTm(todoTotalTm);
        todoRepository.save(todoEntity);
        todoTimerHistoryRepository.saveAll(timerHistoryEntities);
    }

    /**
     * 투두 상세 조회
     * @param idx
     * @return
     */
    @Transactional(readOnly = true)
    public GetTodoDetailRS detailTodo(Long idx) {

        TodoEntity todoEntity = this.getMyTodoData(idx);

        List<TimerHistory> timerHistories = new ArrayList<>();

        for(TodoTimerHistoryEntity todoTimerHistoryEntity : todoEntity.getTodoTimerHistoryEntities()){
            TimerHistory node = new TimerHistory();
            node.setStartDt(todoTimerHistoryEntity.getHistoryStartDt());
            node.setEndDt(todoTimerHistoryEntity.getHistoryEndDt());
            node.setTotalTm(todoTimerHistoryEntity.getTotalTm());

            timerHistories.add(node);
        }

        LocalTime totalTm = LocalTime.MIN.plus(
            todoEntity.getTodoTimerHistoryEntities().stream()
                .map(time -> Duration.between(LocalTime.MIN, time.getTotalTm()))
                .reduce(Duration.ZERO, Duration::plus)
            );

        GetTodoDetailRS result = new GetTodoDetailRS();
        result.setIdx(todoEntity.getIdx());
        result.setContent(todoEntity.getContent());
        result.setTotalTm(totalTm);
        result.setTimerHistories(timerHistories);

        return result;
    }

    /**
     * 루틴 수정
     * @param rq
     */
    @Transactional
    public void updateRoutine(UpdateRoutineRQ rq) {

        TodoEntity todoEntity = this.getMyTodoData(rq.getTodoIdx());
        RoutineEntity routineEntity = todoEntity.getRoutineEntity();

        if(routineEntity == null) throw new AppException(ExceptionCode.DATA_NOT_FIND);

        // Cycle 타입 변경
        if(rq.getCycleType() != null) {
            if(rq.getCycleValue() == null) throw new AppException(ExceptionCode.NON_VALID_PARAMETER, "루틴 타입이 변경되면 루틴 지정일도 같이 와야합니다.");

            routineEntity.setCycleType(rq.getCycleType());
        }

        // Cycle 데이터 변경
        if(rq.getCycleValue() != null){

            String rm = this.validationRoutineCycleType(routineEntity.getCycleType(), rq.getCycleValue());

            StringBuilder cycleValue = new StringBuilder();
    
            // 루틴 사이클 지정일이 있을 시
            if(rq.getCycleValue() != null){
                rq.getCycleValue().forEach(value -> {
                    
                    switch (rq.getCycleType()) {
                        case EVERY_DAY:
                            break;
                        case EVERY_MONTH:
                            if(!(value >= 1 && 31 >= value)) {
                                throw new AppException(ExceptionCode.NON_VALID_PARAMETER, "일 지정은 1~31까지 숫자를 넣어야 합니다.");
                            } 
    
                            break;
                        case EVERY_WEEK:
                            DayWeekType dayWeek = DayWeekType.fromValue(value.intValue());
                            if(dayWeek == null){
                                throw new AppException(ExceptionCode.NON_VALID_PARAMETER, "요일은 1~7까지 숫자를 넣어야 합니다.");
                            }
                            break;
                    }    
                    cycleValue.append(value);
                    cycleValue.append(",");
                });
                cycleValue.deleteCharAt(cycleValue.length() - 1);
            }

            routineEntity.setRm(rm);
            routineEntity.setCycleValue(cycleValue.toString());
        }

        if(rq.getStartDt() != null) routineEntity.setStartDt(rq.getStartDt());
        if(rq.getEndDt() != null) routineEntity.setEndDt(rq.getEndDt());
        if(rq.getStartTm() != null) todoEntity.setStartTargetTm(rq.getStartTm());
        if(rq.getEndTm() != null) todoEntity.setEndTargetTm(rq.getEndTm());
    }

    /**
     * 루틴 삭제[물리]
     * @param idx
     */
    @Transactional
    public void deleteRoutine(Long idx) {
        TodoEntity todoEntity = this.getMyTodoData(idx);
        RoutineEntity routineEntity = todoEntity.getRoutineEntity();

        if(routineEntity == null) return;

        routineRepository.delete(routineEntity);
    }

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
        
    private TodoEntity getMyTodoData(Long idx){

        UsersEntity usersEntity = userUtil.getUsersEntity();

        return todoRepository.findByIdxAndUsersEntity(idx, usersEntity)
            .orElseThrow(() -> new AppException(ExceptionCode.DATA_NOT_FIND));
    }

}
