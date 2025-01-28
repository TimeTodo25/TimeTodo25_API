package com.pape.timetodo.domain.main.service;

import com.pape.timetodo.domain.main.model.GetCategoryRoutineModel;
import com.pape.timetodo.domain.main.model.GetRoutineModel;
import com.pape.timetodo.domain.main.model.GetTodoModel;
import com.pape.timetodo.domain.main.model.home.*;
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
import java.util.*;
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

    /**
     * 홈화면 :: Todo데이터 조회 D-day TODO_, Category TODO_
     * @param rq GetHomeRQ
     * @return GetHomeRS
     */
    public GetHomeRS getHomeTodo(GetHomeRQ rq) {

        UsersEntity usersEntity = userUtil.getUsersEntity();

        LocalDate date = rq.getDate();

        List<HomeDdayModel> ddayList = todoQueryRepository.findDdayTodoByUsersEntity(usersEntity, date);

        List<HomeCategoryTodoModel> categoryTodoList = this.getMyCategoryList(usersEntity);

        List<HomeCategoryModel> categoryList = new ArrayList<>();
        List<HomeTimerHistoryModel> timerHistoryList = new ArrayList<>();

        for(HomeCategoryTodoModel categoryTodoModel : categoryTodoList) {
            String mainColor = categoryTodoModel.getMainColor();

            // HomeCategoryModel의 카테고리 정보
            HomeCategoryModel categoryModel = new HomeCategoryModel();
            categoryModel.setIdx(categoryTodoModel.getIdx());
            categoryModel.setTitle(categoryTodoModel.getTitle());
            categoryModel.setMainColor(mainColor);
            categoryModel.setPublicStatus(categoryTodoModel.getPublicStatus());

            // HomeCategoryModel의 투두 배열, 타임히스토리 배열
            List<TodoEntity> todoList = categoryTodoModel.getTodoList();
            List<HomeTodoModel> todoModelList = new ArrayList<>(); // HomeTodoModel 배열 생성

            for(TodoEntity todo : todoList) {

                if(todo.getTargetDate().equals(date) || todo.getTargetDate().equals(date.minusDays(1))) { // 당일, 혹은 그 전날의 todo에 대해...

                    HomeTodoModel todoModel = new HomeTodoModel(); // HomeTodoModel 객체
                    todoModel.setIdx(todo.getIdx()); // HomeTodoModel - (1) idx
                    todoModel.setContent(todo.getContent()); // HomeTodoModel - (2) content
                    LocalTime totalTm = LocalTime.of(0, 0);

                    for(TodoTimerHistoryEntity timer : todo.getTodoTimerHistoryEntities()) {

                        // HomeCategoryModel의 투두 배열에 들어갈 totalTm
                        totalTm = totalTm.plus(Duration.between(LocalTime.MIN, timer.getTotalTm()));

                        // HomeTimerHistoryModel의 타임히스토리 배열
                        HomeTimerHistoryModel timerHistoryModel = new HomeTimerHistoryModel(); // HomeTimerHistoryModel 객체

                        LocalDate startDate = timer.getHistoryStartDt().toLocalDate();
                        LocalDate endDate = timer.getHistoryEndDt().toLocalDate();

                        if(startDate.equals(date) && endDate.equals(date)) { // 1. 기준 날짜 시작 & 끝
                            timerHistoryModel.setStartTm(timer.getHistoryStartDt().toLocalTime());
                            timerHistoryModel.setEndTm(timer.getHistoryEndDt().toLocalTime());
                        }
                        else if(startDate.isBefore(date) && endDate.equals(date)) { // 1. 기준 전날 시작, 기준 날짜 끝
                            timerHistoryModel.setStartTm(LocalTime.MIN); // 시작 - 0시 0분으로 줌
                            timerHistoryModel.setEndTm(timer.getHistoryEndDt().toLocalTime());
                        }
                        else if(startDate.equals(date) && endDate.isAfter(date)) { // 2. 기준 날짜 시작, 기준 다음날 끝
                            timerHistoryModel.setStartTm(timer.getHistoryStartDt().toLocalTime());
                            timerHistoryModel.setEndTm(LocalTime.MAX); // 끝 - 23시 59분으로 줌
                        }
                        else {
                            continue; // 그외에는 이 타이머 히스토리 스킵
                        }

                        // 스킵되지 않았다면 mainColor 정보 추가해서 HomeTimerHistoryModel 배열에 추가
                        timerHistoryModel.setMainColor(mainColor);
                        timerHistoryList.add(timerHistoryModel);

                    }
                    todoModel.setTodoTotalTm(totalTm); // HomeTodoModel - (3) todoTotalTm
                    todoModelList.add(todoModel); // HomeTodoModel 배열 추가

                }

            }

            categoryModel.setTodoList(todoModelList);
            categoryList.add(categoryModel);
        }

        GetHomeRS result = new GetHomeRS();
        result.setDdayList(ddayList);
        result.setCategoryList(categoryList);
        result.setTimerHistoryList(timerHistoryList);

        return result;
    }

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
                .content(rq.getContent())
                .cycleType(rq.getCycleType())
                .cycleValue(cycleValue.toString())
                .rm(rm)
                .todoEntities(todoEntityList)
                .usersEntity(usersEntity)
                .startDt(rq.getStartDt())
                .endDt(rq.getEndDt())
                .startTargetTm(rq.getStartTargetTm())
                .endTargetTm(rq.getEndTargetTm())
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
            .content(rq.getContent())
            .cycleType(rq.getCycleType())
            .cycleValue(cycleValue.toString())
            .rm(rm)
            .todoEntities(todoEntityList)
            .usersEntity(usersEntity)
            .startDt(rq.getStartDt())
            .endDt(rq.getEndDt())
            .startTargetTm(rq.getStartTargetTm())
            .endTargetTm(rq.getEndTargetTm())
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
                todoRepository.delete(oldTodo.get()); // TODO: 이거 물리 삭젠데 ㄱㅊ?
            }
            oldTodo.ifPresent(todoRepository::delete); // 기존 투두는 날짜 겹치든 말든 삭제가 디폴트 TODO: 삭제 안함 옵션이 생길 예정...
        }

        return todoEntityList;
    }

    /**
     * 개인 카테고리 및 TODO_ 조회 
     * @param usersEntity UsersEntity
     * @return HomeCategoryTodoModel
     */
    private List<HomeCategoryTodoModel> getMyCategoryList(UsersEntity usersEntity){

        if(usersEntity == null){
            usersEntity = userUtil.getUsersEntity();
        }

        return categoryRepository.findByUsersEntity(usersEntity).stream()
                .map(category -> {

                    HomeCategoryTodoModel categoryTodoModel = new HomeCategoryTodoModel();
                    categoryTodoModel.setIdx(category.getIdx());
                    categoryTodoModel.setTitle(category.getTitle());
                    categoryTodoModel.setMainColor(category.getMainColor());
                    categoryTodoModel.setPublicStatus(category.getPublicStatus());
                    categoryTodoModel.setTodoList(category.getTodoEntities());

                    return categoryTodoModel;
                })
                .collect(Collectors.toList());

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
        UsersEntity usersEntity = userUtil.getUsersEntity();

        if(rq.getContent() != null) {
            todoEntity.setContent(rq.getContent());
            todoEntity.setRoutineEntity(null);
        }
        if(rq.getCategoryIdx() != null) {
            todoEntity.setCategoryEntity(categoryRepository.findByIdxAndUsersEntity(rq.getCategoryIdx(), usersEntity).orElseThrow(
                    () -> new AppException(ExceptionCode.DATA_NOT_FIND, "카테고리 없음"))
            );
            todoEntity.setRoutineEntity(null);
        }

        boolean isUpdate = false;
        if(rq.getTargetDate() != null) {
            todoEntity.setTargetDate(rq.getTargetDate());
            isUpdate = true;
        }
        if(rq.getStartTargetTm() != null) {
            todoEntity.setStartTargetTm(rq.getStartTargetTm());
            isUpdate = true;
        }
        if(rq.getEndTargetTm() != null) {
            todoEntity.setEndTargetTm(rq.getEndTargetTm());
            isUpdate = true;
        }
        // 위 셋 중 하나라도 수정되었다면
        if(todoEntity.getRoutineEntity() != null && isUpdate) {
            todoEntity.setStatus(StatusType.UPDATED.getValue());
        }

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

        // 시작 / 끝 날짜 변경
        if(rq.getStartDt() != null) {
            List<TodoEntity> beforeTodoList = todoQueryRepository.findTodoListByRoutineAndDate(routineEntity, rq.getStartDt(), true);
            LocalDateTime today = LocalDateTime.now();

            // 1. 논리 삭제 옵션 (그러나 새로운 시작 날짜 이전의 투두지만 완료했다면 루틴과의 연결만 끊음)
            for(TodoEntity bTodo : beforeTodoList) {
                if(Objects.equals(bTodo.getProgressStatus(), TodoEntity.ProgressStatus._100.getValue())) {
                    bTodo.setUpdateDt(today);
                    bTodo.setRoutineEntity(null);
                } else {
                    bTodo.setDeleteDt(today);
                    bTodo.setStatus(StatusType.DELETED.getValue());
                }
            }

            // 2. 루틴 연결 끊기 옵션
//            for(TodoEntity bTodo : beforeTodoList) {
//                bTodo.setUpdateDt(today);
//                bTodo.setRoutineEntity(null);
//            }

            todoRepository.saveAll(beforeTodoList);
            routineEntity.setStartDt(rq.getStartDt());
        }
        if(rq.getEndDt() != null) {
            List<TodoEntity> afterTodoList = todoQueryRepository.findTodoListByRoutineAndDate(routineEntity, rq.getStartDt(), false);
            LocalDateTime today = LocalDateTime.now();

            // 1. 논리 삭제 옵션
            for(TodoEntity bTodo : afterTodoList) {
                bTodo.setDeleteDt(today);
                bTodo.setStatus(StatusType.DELETED.getValue());
            }

            // 2. 루틴 연결 끊기 옵션
//            for(TodoEntity bTodo : afterTodoList) {
//                bTodo.setUpdateDt(today);
//                bTodo.setRoutineEntity(null);
//            }

            todoRepository.saveAll(afterTodoList);
            routineEntity.setEndDt(rq.getEndDt());
        }

        // 내용 변경
        if(rq.getContent() != null) {
            String content = rq.getContent();

            // 루틴에 속한 투두 전체 내용 수정
            List<TodoEntity> todoList = todoQueryRepository.findTodoListByRoutine(routineEntity);
            for(TodoEntity todo : todoList) {
                todo.setContent(content);
            }
            todoRepository.saveAll(todoList);
        }

        // 시간 변경
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


    /**
     * 루틴 목록 조회
     * @return GetMyRoutineRS
     */
    public GetMyRoutineRS getMyRoutineList() {
        // 1. 현재 로그인한 사용자 정보 가져오기
        UsersEntity usersEntity = userUtil.getUsersEntity();

        // 2. 모든 루틴을 한 번에 가져오기 (N+1 문제 방지)
        List<RoutineEntity> allRoutines = routineQueryRepository.findMyRoutinesByUsresEntity(usersEntity);

        // 3. 루틴 데이터를 카테고리별로 그룹화 -> key를 Category로 하기 위해 CategoryEntity의 equals와 hashCode 재정의(오버라이드)함
        Map<CategoryEntity, List<RoutineEntity>> categoryRoutineMap = allRoutines.stream()
                .collect(Collectors.groupingBy(RoutineEntity::getCategoryEntity));

        // 4. 루틴 목록이 하나 이상인 카테고리만 필터링
        List<GetCategoryRoutineModel> categoryRoutineList = categoryRoutineMap.entrySet().stream()
                .filter(entry -> !entry.getValue().isEmpty())
                .map(entry -> {
                    // 카테고리 모델 생성
                    GetCategoryRoutineModel categoryModel = new GetCategoryRoutineModel();
                    categoryModel.setCategoryIdx(entry.getKey().getIdx());
                    categoryModel.setTitle(entry.getKey().getTitle());
                    categoryModel.setMainColor(entry.getKey().getMainColor());
                    categoryModel.setPublicStatus(entry.getKey().getPublicStatus());
                    categoryModel.setRoutineList(
                            entry.getValue().stream()
                                    .map(routine -> {
                                        // 루틴 모델 변환
                                        GetRoutineModel routineModel = new GetRoutineModel();
                                        routineModel.setRoutineIdx(routine.getIdx());
                                        routineModel.setContent(routine.getContent());
                                        routineModel.setCycleType(routine.getCycleType());
                                        routineModel.setCycleValue(routine.getCycleValue());
                                        routineModel.setRm(routine.getRm());
                                        routineModel.setStartDt(routine.getStartDt());
                                        routineModel.setEndDt(routine.getEndDt());
                                        return routineModel;
                                    })
                                    .toList()
                    );
                    return categoryModel;
                })
                .toList();

        // 5. 결과 객체 생성 및 반환
        GetMyRoutineRS result = new GetMyRoutineRS();
        result.setCategoryRoutineList(categoryRoutineList);

        return result;
    }


    /**
     * 루틴 단건 상세 조회
     * @param idx Long
     * @return GetRoutineDetailRS
     */
    public GetRoutineDetailRS detailRoutine(Long idx) {

        RoutineEntity routineEntity = this.getMyRoutineData(idx);

        GetRoutineDetailRS result = new GetRoutineDetailRS();
        result.setIdx(routineEntity.getIdx());
        result.setContent(routineEntity.getContent());
        result.setRm(result.getRm());
        result.setStartDt(routineEntity.getStartDt());
        result.setEndDt(routineEntity.getEndDt());
        result.setStartTm(routineEntity.getStartTargetTm());
        result.setEndTm(routineEntity.getEndTargetTm());
        result.setCycleType(routineEntity.getCycleType());
        result.setCycleValue(result.getCycleValue());

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
