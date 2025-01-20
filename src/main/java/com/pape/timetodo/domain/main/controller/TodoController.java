package com.pape.timetodo.domain.main.controller;

import com.pape.timetodo.domain.main.model.todo.*;
import com.pape.timetodo.domain.main.service.TodoRoutineService;
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

    private final TodoRoutineService todoRoutineService;

    /**
     * Todo_ 등록
     * @return
     */
    @PostMapping("/create")
    @Operation(summary = "투두 등록", description = "투두정보를 등록합니다.")
    public ResponseEntity<CreateTodoRS> createTodo(@Valid @RequestBody CreateTodoRQ rq){

        todoRoutineService.createTodo(rq);

        return ResponseEntity.ok().build();
    }

    /**
     * 홈화면 :: 오늘 TodoList 조회
     */
    @GetMapping("/home")
    @Operation(summary = "홈화면 데이터 조회[투두, 디데이]", description = "홈화면 데이터를 조회합니다.")
    public ResponseEntity<GetHomeTodoRS> getHomeTodo(@Valid GetHomeTodoRQ rq){

        GetHomeTodoRS result = todoRoutineService.getHomeTodo(rq);

        return ResponseEntity.ok().body(result);
    }

    /**
     * Todo_ 삭제
     * @param idx
     */
    @DeleteMapping("/{idx}/delete")
    @Operation(summary = "투두 삭제", description = "투두 데이터를 삭제합니다. [논리삭제]")
    public ResponseEntity<Void> deleteTodo(@PathVariable(name = "idx", required = false) @Parameter(name="idx", description = "삭제할 투두 IDX", in = ParameterIn.PATH, example = "1") Long idx){

        if(idx == null){
            throw new AppException(ExceptionCode.NON_VALID_PARAMETER);
        }

        todoRoutineService.deleteTodo(idx);

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

        todoRoutineService.updateTodo(rq);

        return ResponseEntity.ok().build();
    }

    /**
     * Todo_ 타이머 데이터 등록
     * @param rq RegistTodoTimerRQ
     * @return RegistTodoTimerRS
     */
    @PostMapping("/register/todo/timer")
    @Operation(summary = "투두 타이머 등록", description = "투두 타이머 스톱워치 데이터를 등록합니다.")
    public ResponseEntity<RegistTodoTimerRS> registTodoTimer(@Valid @RequestBody RegistTodoTimerRQ rq){

        RegistTodoTimerRS result = todoRoutineService.registTodoTimer(rq);

        return ResponseEntity.ok().body(result);
    }


    /**
     * Todo_ 단건 상세 조회
     * @param idx
     * @return
     */
    @GetMapping("/detail/{idx}")
    @Operation(summary = "투두 데이터 단건조회", description = "투두 단건데이터를 조회합니다.")
    public ResponseEntity<GetTodoDetailRS> detailTodo(@PathVariable(name = "idx") @Parameter(name="idx", description = "투두 IDX", in = ParameterIn.PATH, example = "1") Long idx){

        GetTodoDetailRS result = todoRoutineService.detailTodo(idx);

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

        GetTodoTimerHistoryRs result = todoRoutineService.detailTodoTimer(idx);

        return ResponseEntity.ok().body(result);
    }

}
