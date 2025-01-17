package com.pape.timetodo.domain.main.controller;

import com.pape.timetodo.domain.main.model.dday.RegisterDayRQ;
import com.pape.timetodo.domain.main.model.routhin.RegisterRoutineRQ;
import com.pape.timetodo.domain.main.model.routhin.RegisterRoutineRS;
import com.pape.timetodo.domain.main.model.routhin.UpdateRoutineRQ;
import com.pape.timetodo.domain.main.model.todo.*;
import com.pape.timetodo.domain.main.service.TodoService;
import com.pape.timetodo.global.exception.AppException;
import com.pape.timetodo.global.exception.ExceptionCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/v1/todo")
@RequiredArgsConstructor
@Tag(name = "투두", description = "투두 API")
public class TodoController {

    private final TodoService todoService;

    /**
     * Todo_ 등록
     * @return
     */
    @PostMapping("/create")
    @Operation(summary = "투두 등록", description = "투두정보를 등록합니다.")
    public ResponseEntity<CreateTodoRS> createTodo(@Valid @RequestBody CreateTodoRQ rq){

        todoService.createTodo(rq);

        return ResponseEntity.ok().build();
    }

    /**
     * 홈화면 :: 오늘 TodoList 조회
     */
    @GetMapping("/home")
    @Operation(summary = "홈화면 데이터 조회[투두, 디데이]", description = "홈화면 데이터를 조회합니다.")
    public ResponseEntity<GetHomeTodoRS> getHomeTodo(@Valid GetHomeTodoRQ rq){

        GetHomeTodoRS result = todoService.getHomeTodo(rq);

        return ResponseEntity.ok().body(result);
    }

    /**
     * Todo_ 삭제
     * @param idx
     * @return
     */
    @DeleteMapping("/{idx}/delete")
    @Operation(summary = "투두 삭제", description = "투두 데이터를 삭제합니다. [논리삭제]")
    public ResponseEntity<Boolean> deleteTodo(@PathVariable(name = "idx", required = false) @Parameter(name="idx", description = "삭제할 투두 IDX", in = ParameterIn.PATH, example = "1") Long idx){

        if(idx == null){
            throw new AppException(ExceptionCode.NON_VALID_PARAMETER);
        }

        todoService.deleteTodo(idx);

        return ResponseEntity.ok().build();
    }

    /**
     * Todo_ 수정
     * @param rq
     * @return
     */
    @PutMapping("/update")
    @Operation(summary = "투두 수정", description = "투두 데이터를 수정합니다.")
    public ResponseEntity<Void> updateTodo(@Valid @RequestBody UpdateTodoRQ rq){

        todoService.updateTodo(rq);

        return ResponseEntity.ok().build();
    }

    /**
     * Todo_ 타이머 데이터 등록
     * @param rq
     * @return
     */
    @PostMapping("/register/todo/timer")
    @Operation(summary = "투두 타이머 등록", description = "투두 타이머 스톱워치 데이터를 등록합니다.")
    public ResponseEntity<Void> registTodoTimer(@Valid @RequestBody RegistTodoTimerRQ rq){

        todoService.registTodoTimer(rq);

        return ResponseEntity.ok().build();
    }


    /**
     * Todo_ 단건 상세 조회
     * @param idx
     * @return
     */
    @GetMapping("/detail/{idx}")
    @Operation(summary = "투두 데이터 단건조회", description = "투두 단건데이터를 조회합니다.")
    public ResponseEntity<GetTodoDetailRS> detailTodo(@PathVariable(name = "idx") @Parameter(name="idx", description = "투두 IDX", in = ParameterIn.PATH, example = "1") Long idx){

        GetTodoDetailRS result = todoService.detailTodo(idx);

        return ResponseEntity.ok().body(result);
    }


    /**
     * Todo_ Timer History 데이터 조회
     * @param idx
     * @return
     */
    @GetMapping("/detail/{idx}/timer")
    @Operation(summary = "투두 타이머 데이터 조회", description = "투두 타이머 데이터를 조회합니다.")
    public ResponseEntity<GetTodoTimerHistoryRs> detailTodoTimer(@PathVariable(name = "idx") @Parameter(name="idx", description = "투두 IDX", in = ParameterIn.PATH, example = "1") Long idx){

        GetTodoTimerHistoryRs result = todoService.detailTodoTimer(idx);

        return ResponseEntity.ok().body(result);
    }


    /**
     * 루틴 등록
     * @param rq
     * @return
     */
    @PostMapping("/routine/register")
    @Operation(summary = "루틴등록", description = "투두 루틴 등록합니다.")
    public ResponseEntity<RegisterRoutineRS> registerRoutine(@Valid @RequestBody RegisterRoutineRQ rq){

        RegisterRoutineRS result = todoService.registerRoutine(rq);

        return ResponseEntity.ok().body(result);
    }

    /**
     * 루틴 수정
     * @param rq
     * @return
     */
    @PutMapping("/routine/update")
    @Operation(summary = "루틴수정", description = "투두 루틴 수정합니다.")
    public ResponseEntity<Void> updateRoutine(@Valid @RequestBody UpdateRoutineRQ rq){

        todoService.updateRoutine(rq);

        return ResponseEntity.ok().build();
    }


    /**
     * 루틴 삭제
     * @param idx 루틴 삭제할 Todo번호
     * @return
     */
    @DeleteMapping("/routine/delete/{idx}")
    @Operation(summary = "루틴삭제", description = "투두 루틴 삭제합니다.")
    public ResponseEntity<Void> deleteRoutine(@PathVariable @Parameter(name="idx", description = "투두 IDX", in = ParameterIn.PATH, example = "1") Long idx){
        
        if(idx == null) throw new AppException(ExceptionCode.NON_VALID_PARAMETER);

        todoService.deleteRoutine(idx); // TODO: 논리 삭제만 진행

        return ResponseEntity.ok().build();
    }

    /**
     * 디데이 등록
     * @param rq
     * @return
     */
    @PostMapping("/register/d-day")
    @Operation(summary = "디데이 등록", description = "디데이를 등록합니다.")
    public ResponseEntity<Void> registerDday(@Valid @RequestBody RegisterDayRQ rq){

        todoService.registerDday(rq);

        return ResponseEntity.ok().build();
    }
}
