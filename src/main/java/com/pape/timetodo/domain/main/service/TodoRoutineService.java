package com.pape.timetodo.domain.main.service;

import com.pape.timetodo.domain.main.model.GetCategoryModel;
import com.pape.timetodo.domain.main.model.GetRoutineModel;
import com.pape.timetodo.domain.main.model.GetTodoModel;
import com.pape.timetodo.domain.main.model.routine.*;
import com.pape.timetodo.domain.main.model.todo.*;
import com.pape.timetodo.domain.main.model.todo.GetTodoTimerHistoryRs.TimerHistory;
import com.pape.timetodo.domain.main.model.todo.RegistTodoTimerRQ.TimeData;
import com.pape.timetodo.global.constant.DayWeekType;
import com.pape.timetodo.global.constant.StatusType;
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
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TodoRoutineService {

    private final TodoRepository todoRepository;

    private final TodoQueryRepository todoQueryRepository;
    
    private final CategoryRepository categoryRepository;

    private final RoutineRepository routineRepository;

    private final RoutineQueryRepository routineQueryRepository;

    private final TodoTimerHistoryRepository todoTimerHistoryRepository;

    private final UserUtil userUtil;
    
    /**
     * TODO_ 리스트 추가, Category와 Users정보 확인
     * @param rq CreateTodoRQ
     * @return CreateTodoRS
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
            .targetDate(rq.getDate());

        if(rq.getStartTargetTm() != null) todoEntityBuilder.startTargetTm(rq.getStartTargetTm());
        if(rq.getEndTargetTm() != null) todoEntityBuilder.endTargetTm(rq.getEndTargetTm());

        TodoEntity todoEntity = todoEntityBuilder.build();

        todoEntity = todoRepository.save(todoEntity);
        
        CreateTodoRS result = new CreateTodoRS();
        result.setTodoIdx(todoEntity.getIdx());
        result.setUpdateDt(todoEntity.getUpdateDt());

        return result;
    }

    /**
     * TODO_ 추가, Routine으로 인해 생성되는 Todo라 로직이 간소해져서 분리함
     * @param rq CreateTodoRQ
     * @return CreateTodoRS
     */
    // @Transactional // TODO: 같은 클래스의 내부 메서드라 트랜잭션이 적용 안된다고 함. 고민 필요.
    public TodoEntity createTodoByRoutine(@Valid CreateTodoRQ rq, CategoryEntity categoryEntity, UsersEntity usersEntity) {

        TodoEntity.TodoEntityBuilder todoEntityBuilder = TodoEntity.builder()
                .content(rq.getContent())
                .categoryEntity(categoryEntity)
                .usersEntity(usersEntity)
                .targetDate(rq.getDate())
                .status(StatusType.NORMAL.getValue());

        if(rq.getStartTargetTm() != null) todoEntityBuilder.startTargetTm(rq.getStartTargetTm());
        if(rq.getEndTargetTm() != null) todoEntityBuilder.endTargetTm(rq.getEndTargetTm());

        TodoEntity todoEntity = todoEntityBuilder.build();

        todoEntity = todoRepository.save(todoEntity);

        return todoEntity;
    }

//    /**
//     * 홈화면 :: Todo데이터 조회 D-day TODO_, Category TODO_ // TODO: 삭제 보류
//     * @return
//     */
//    @Transactional(readOnly = true)
//    public GetHomeTodoRS getHomeTodo(GetHomeTodoRQ rq) {
//
//        UsersEntity usersEntity = userUtil.getUsersEntity();
//
//        List<GetCategoryModel> categoryList = this.getMyCategoryList(usersEntity, rq);
//        List<DdayTodoModel> intervalDayTodoModels = todoQueryRepository.findDdayTodoByUsersEntity(usersEntity);
//
//        GetHomeTodoRS result = new GetHomeTodoRS();
//        result.setCategoryList(categoryList);
//        result.setIntervalDayTodoList(intervalDayTodoModels);
//
//        return result;
//    }

    /**
     * 새로운 루틴 등록 (투두 목록 생성)
     * @param rq RegisterRoutineRQ
     * @return RegisterRoutineRS
     */
    @Transactional
    public RegisterRoutineRS createRoutine(RegisterRoutineRQ rq) {

        UsersEntity usersEntity = userUtil.getUsersEntity();

        CategoryEntity categoryEntity = categoryRepository.findByIdxAndUsersEntity(rq.getCategoryIdx(), usersEntity)
                .orElseThrow(() -> new AppException(ExceptionCode.NON_VALID_PARAMETER, "잘못된 카테고리 IDX"));

        String rm = validationRoutineCycleType(rq.getCycleType(), rq.getCycleValue()); // TODO: 이게 왜 필요하지..?

        StringBuilder cycleValue = getCycleValueAsStr(rq.getCycleType(), rq.getCycleValue());

        List<TodoEntity> todoEntityList = createTodoList(rq, usersEntity, categoryEntity);

        RoutineEntity routineEntity = RoutineEntity.builder()
                .cycleType(rq.getCycleType())
                .cycleValue(cycleValue.toString())
                .rm(rm)
                .todoEntities(todoEntityList)
                .usersEntity(usersEntity)
                .startDt(rq.getStartDt())
                .endDt(rq.getEndDt())
                .build();

        routineEntity = routineRepository.save(routineEntity);

        List<TodoEntity> todoList = routineEntity.getTodoEntities();

        RegisterRoutineRS result = new RegisterRoutineRS();
        result.setIdx(routineEntity.getIdx());
        result.setUpdateDt(routineEntity.getUpdateDt());
        result.setTodoList(todoList.stream().map(todo -> {
            GetTodoModel model = new GetTodoModel();
            model.setIdx(todo.getIdx());
            model.setContent(todo.getContent());
            model.setTargetDate(todo.getTargetDate());
            model.setStartTargetTm(todo.getStartTargetTm());
            model.setEndTargetTm(todo.getEndTargetTm());
            return model;
        }).collect(Collectors.toList()));

        return result;
    }

    /**
     * 기존 투두 -> 루틴 등록 (투두 복사 목록화)
     * @param rq RegisterRoutineRQ
     * @return RegisterRoutineRS
     */
    @Transactional
    public RegisterRoutineRS registerRoutine(RegisterRoutineRQ rq) {

        UsersEntity usersEntity = userUtil.getUsersEntity();

        CategoryEntity categoryEntity = categoryRepository.findByIdxAndUsersEntity(rq.getCategoryIdx(), usersEntity)
                .orElseThrow(() -> new AppException(ExceptionCode.NON_VALID_PARAMETER, "잘못된 카테고리 IDX"));

        // 실제로 있는 TodoData인지 그리고 자기 자신 TodoData인지 확인
        TodoEntity todoEntity = todoRepository.findByIdxAndUsersEntity(rq.getTodoIdx(), usersEntity)
            .orElseThrow(() -> new AppException(ExceptionCode.DATA_NOT_FIND));

        // 루틴등록을 중복으로 했는지 확인
        if(todoEntity.getRoutineEntity() != null) throw new AppException(ExceptionCode.DATA_DUPLICATE, "이미 루틴으로 등록되어있습니다.");

        String rm = validationRoutineCycleType(rq.getCycleType(), rq.getCycleValue());

        StringBuilder cycleValue = getCycleValueAsStr(rq.getCycleType(), rq.getCycleValue());

        List<TodoEntity> todoEntityList = createTodoList(rq, usersEntity, categoryEntity);

        RoutineEntity routineEntity = RoutineEntity.builder()
            .cycleType(rq.getCycleType())
            .cycleValue(cycleValue.toString())
            .rm(rm)
            .todoEntities(todoEntityList)
            .usersEntity(usersEntity)
            .startDt(rq.getStartDt())
            .endDt(rq.getEndDt())
            .build();

        routineEntity = routineRepository.save(routineEntity);

        List<TodoEntity> todoList = routineEntity.getTodoEntities();
        
        RegisterRoutineRS result = new RegisterRoutineRS();
        result.setIdx(routineEntity.getIdx());
        result.setUpdateDt(routineEntity.getUpdateDt());
        result.setTodoList(todoList.stream().map(todo -> {
            GetTodoModel model = new GetTodoModel();
            model.setIdx(todo.getIdx());
            model.setContent(todo.getContent());
            model.setTargetDate(todo.getTargetDate());
            model.setStartTargetTm(todo.getStartTargetTm());
            model.setEndTargetTm(todo.getEndTargetTm());
            return model;
        }).collect(Collectors.toList()));

        return result;
    }

    /**
     * 루틴 타입이 요일 지정일 떄, value가 1~7 인지 체크
     * @param cycleType CycleType
     * @param cycleValue List<Byte>
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
     * 루틴 사이클 stringbuilder로 저장
     * @param cycleType CycleType
     * @param cycleValueList List<Byte>
     * @return StringBuilder
     */
    private StringBuilder getCycleValueAsStr(CycleType cycleType, List<Byte> cycleValueList) {

        StringBuilder cycleValue = new StringBuilder();
        // 루틴 사이클 지정일이 있을 시 (everyday는 value 없음)
        if(cycleValueList != null){
            cycleValueList.forEach(value -> {

                switch (cycleType) {
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
        return  cycleValue;
    }

    /**
     * 루틴 생성 및 등록으로 인한 복수의 투두 생성
     * @param rq RegisterRoutineRQ
     * @param usersEntity UsersEntity
     * @param categoryEntity CategoryEntity
     * @return List<TodoEntity>
     */
    private List<TodoEntity> createTodoList(RegisterRoutineRQ rq, UsersEntity usersEntity, CategoryEntity categoryEntity) {
        List<TodoEntity> todoEntityList = new ArrayList<>();

        // getter 잦은 호출 피하기 위해 각 변수 선언 및 할당
        String content = rq.getContent();
        Long categoryIdx = rq.getCategoryIdx();
        LocalTime startTargetTm = rq.getStartTargetTm();
        LocalTime endTargetTm = rq.getEndTargetTm();

        // 날짜 마다 Todo_ 데이터 추가
        for(LocalDate date = rq.getStartDt(); !date.isAfter(rq.getEndDt()); date = date.plusDays(1)) {

            CreateTodoRQ newTodo = new CreateTodoRQ();
            newTodo.setContent(content);
            newTodo.setCategoryIdx(categoryIdx);
            newTodo.setDate(date);
            newTodo.setStartTargetTm(startTargetTm);
            newTodo.setEndTargetTm(endTargetTm);

            TodoEntity todo = createTodoByRoutine(newTodo, categoryEntity, usersEntity);
            todoEntityList.add(todo);
        }

        // 날짜 겹치는 기존의 TodoEntity 삭제
        if(rq.getTodoIdx() != null) {
            Optional<TodoEntity> oldTodo = todoRepository.findById(rq.getTodoIdx());
            if(oldTodo.isPresent() && (oldTodo.get().getTargetDate().isEqual(rq.getStartDt()) || oldTodo.get().getTargetDate().isAfter(rq.getStartDt())) &&
            (oldTodo.get().getTargetDate().isEqual(rq.getEndDt()) || oldTodo.get().getTargetDate().isBefore(rq.getEndDt()))) {
                todoRepository.delete(oldTodo.get());
            }
            // _todo.ifPresent(todoRepository::delete); // TODO: 그냥 기존 투두는 날짜 겹치든 말든 없애는 게 낫지 않나 논의 필요
        }

        return todoEntityList;
    }

    /**
     * 개인 카테고리 및 TODO_ 조회 
     * @param usersEntity
     * @return
     */
    private List<GetCategoryModel> getMyCategoryList(UsersEntity usersEntity, GetHomeTodoRQ rq){

        if(usersEntity == null){
            usersEntity = userUtil.getUsersEntity();
        }

        // TODO: 홈화면이라 지금 안쓰고 있지만 쓰려면 고쳐야 할 것 - RoutineYn boolean 필드인데 불필요함. routine 필드 null인지 체크하게 해
        // GetTodoModel과 todoQueryRepository도 수정이 필요함
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
     * TODO_ 데이터 삭제
     * @param idx
     */
    @Transactional
    public void deleteTodo(Long idx) {

        UsersEntity usersEntity = userUtil.getUsersEntity();

        Optional<TodoEntity> todoEntityWrapper = todoRepository.findByIdxAndUsersEntity(idx, usersEntity);

        if(todoEntityWrapper.isPresent()){

            TodoEntity todoEntity = todoEntityWrapper.get();
            todoEntity.setDeleteDt(LocalDateTime.now());
            todoEntity.setStatus(StatusType.DELETED.getValue());
        }
    }

    /**
     * TODO_ 데이터 업데이트
     * @param rq UpdateTodoRQ
     * @return UpdateTodoRS
     */
    @Transactional
    public UpdateTodoRS updateTodo(UpdateTodoRQ rq) {

        TodoEntity todoEntity = this.getMyTodoData(rq.getIdx());

        if(rq.getContent() != null) todoEntity.setContent(rq.getContent());
        if(rq.getTargetDate() != null) todoEntity.setTargetDate(rq.getTargetDate());
        if(rq.getStartTargetTm() != null) todoEntity.setStartTargetTm(rq.getStartTargetTm());
        if(rq.getEndTargetTm() != null) todoEntity.setEndTargetTm(rq.getEndTargetTm());

        // 투두 개별 수정하면 기존 루틴에서 제외됨 // TODO: 카테고리 수정이나, 상태 변경의 경우에는 속한 루틴 값 유지해야 함
        if(todoEntity.getRoutineEntity() != null) todoEntity.setRoutineEntity(null);

        todoEntity.setUpdateDt(LocalDateTime.now());
        todoRepository.save(todoEntity);

        UpdateTodoRS result = new UpdateTodoRS();
        result.setUpdateDt(todoEntity.getUpdateDt());

        return result;
    }

    /**
     * TODO_ 시간기록 등록
     * @param rq RegistTodoTimerRQ
     * @return RegistTodoTimerRS
     */
    @Transactional
    public RegistTodoTimerRS registTodoTimer(RegistTodoTimerRQ rq) {
        TodoEntity todoEntity = this.getMyTodoData(rq.getTodoIdx());

        List<TodoTimerHistoryEntity> timerHistoryEntities = new ArrayList<>();

        for(TimeData time: rq.getTimeDatas()){

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

        todoTimerHistoryRepository.saveAll(timerHistoryEntities);

        List<Long> timerIdxList = timerHistoryEntities.stream()
                .map(TodoTimerHistoryEntity::getIdx)
                .collect(Collectors.toList());

        RegistTodoTimerRS result = new RegistTodoTimerRS();
        result.setTimerIdxList(timerIdxList);
        result.setUpdateDt(LocalDateTime.now());

        return result;
    }

    /**
     * TODO_ 상세 조회
     * @param idx
     * @return
     */
    @Transactional(readOnly = true)
    public GetTodoDetailRS detailTodo(Long idx) {

        TodoEntity todoEntity = this.getMyTodoData(idx);

        LocalTime totalTm = LocalTime.MIN.plus(
            todoEntity.getTodoTimerHistoryEntities().stream()
                .map(time -> Duration.between(LocalTime.MIN, time.getTotalTm()))
                .reduce(Duration.ZERO, Duration::plus)
            );

        GetTodoDetailRS result = new GetTodoDetailRS();
        result.setIdx(todoEntity.getIdx());
        result.setContent(todoEntity.getContent());
        result.setTotalTm(totalTm);

        return result;
    }

    /**
     * TODO_ 타이머 기록 상세 조회
     * @param idx
     * @return
     */
    @Transactional(readOnly = true)
    public GetTodoTimerHistoryRs detailTodoTimer(Long idx) {

        TodoEntity todoEntity = this.getMyTodoData(idx);

        List<TimerHistory> timerHistories = new ArrayList<>();

        for(TodoTimerHistoryEntity todoTimerHistoryEntity : todoEntity.getTodoTimerHistoryEntities()){
            TimerHistory node = new TimerHistory();
            node.setStartDt(todoTimerHistoryEntity.getHistoryStartDt());
            node.setEndDt(todoTimerHistoryEntity.getHistoryEndDt());
            node.setTotalTm(todoTimerHistoryEntity.getTotalTm());

            timerHistories.add(node);
        }

        GetTodoTimerHistoryRs result = new GetTodoTimerHistoryRs();
        result.setTimerHistories(timerHistories);

        return result;
    }

    /**
     * 루틴 수정
     * @param rq UpdateRoutineRQ
     * @return UpdateRoutineRS
     */
    @Transactional
    public UpdateRoutineRS updateRoutine(UpdateRoutineRQ rq) {

        RoutineEntity routineEntity = this.getMyRoutineData(rq.getRoutineIdx());

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

        // TODO: 있던 todo 없애야..하는...?
        if(rq.getStartDt() != null) routineEntity.setStartDt(rq.getStartDt());
        if(rq.getEndDt() != null) routineEntity.setEndDt(rq.getEndDt());

        // 속한 Todo_ 데이터도 시간 변경
        LocalDateTime now = LocalDateTime.now();
        if(rq.getStartTm() != null || rq.getEndTm() != null) {
            LocalTime newStartTm = rq.getStartTm();
            LocalTime newEndTm = rq.getEndTm();
            for(TodoEntity todoEntity : routineEntity.getTodoEntities()) {
                todoEntity.setStartTargetTm(newStartTm);
                todoEntity.setEndTargetTm(newEndTm);
                todoEntity.setUpdateDt(now);
                todoRepository.save(todoEntity);
            }
        }

        UpdateRoutineRS result = new UpdateRoutineRS();
        result.setUpdateDt(now);
        return result;
    }

    public GetMyRoutineRS getMyRoutineList() {
        UsersEntity usersEntity = userUtil.getUsersEntity();

        List<GetRoutineModel> routineList = routineQueryRepository.findMyRoutineByUsresEntity(usersEntity).stream()
                .map(entity -> {
                    GetRoutineModel result = new GetRoutineModel();
                    result.setIdx(entity.getIdx());
                    result.setCycleType(entity.getCycleType());
                    result.setCycleValue(entity.getCycleValue());
                    result.setRm(entity.getRm());
                    result.setStartDt(entity.getStartDt());
                    result.setEndDt(entity.getEndDt());

                    return result;
                })
                .toList();

        GetMyRoutineRS result = new GetMyRoutineRS();
        result.setRoutineList(routineList);

        return result;
    }

    public GetRoutineDetailRS detailRoutine(Long idx) {

        RoutineEntity routineEntity = this.getMyRoutineData(idx);

        GetRoutineDetailRS result = new GetRoutineDetailRS();
        result.setIdx(routineEntity.getIdx());
        result.setCycleType(routineEntity.getCycleType());
        result.setCycleValue(result.getCycleValue());
        result.setRm(result.getRm());
        result.setStartDt(routineEntity.getStartDt());
        result.setEndDt(routineEntity.getEndDt());
        // Todo: 한 루틴에 속한 투두의 content는 모두 같지 않나? 시작, 끝 시간도 그런 것 같은데...? 차라리 todo 개수를 반환하는 게...
        result.setTodoList(routineEntity.getTodoEntities().stream().map(todo -> {
            GetTodoModel model = new GetTodoModel();
            model.setIdx(todo.getIdx());
            model.setContent(todo.getContent());
            model.setTargetDate(todo.getTargetDate());
            // TODO: totalTime이 필요한가? 실행 전의 투두는 어차피 0인데
            return model;
        }).collect(Collectors.toList()));

        return result;
    }

    /**
     * 루틴 삭제 [논리 삭제]
     * @param idx Long
     */
    @Transactional
    public void deleteRoutine(Long idx) {
        RoutineEntity routineEntity = getMyRoutineData(idx);

        if(routineEntity == null) return;

        LocalDateTime now = LocalDateTime.now();
        Character d = StatusType.DELETED.getValue();

        for(TodoEntity todo : routineEntity.getTodoEntities()) {
            if(todo.getTargetDate().isBefore(LocalDate.now())) {
                // 이미 타겟 날짜가 지났으면 루틴에서 제외
                todo.setRoutineEntity(null);
            } else {
                // 그외에는 논리 삭제
                todo.setDeleteDt(now);
                todo.setStatus(d);
            }
        }

        routineEntity.setDeleteDt(now);
        routineEntity.setStatus(d);
        routineRepository.save(routineEntity);
    }
        
    private TodoEntity getMyTodoData(Long idx){

        UsersEntity usersEntity = userUtil.getUsersEntity();

        return todoRepository.findByIdxAndUsersEntity(idx, usersEntity)
            .orElseThrow(() -> new AppException(ExceptionCode.DATA_NOT_FIND));
    }

    private RoutineEntity getMyRoutineData(Long idx){

        UsersEntity usersEntity = userUtil.getUsersEntity();

        return routineRepository.findByIdxAndUsersEntity(idx, usersEntity)
                .orElseThrow(() -> new AppException(ExceptionCode.DATA_NOT_FIND));
    }

}
