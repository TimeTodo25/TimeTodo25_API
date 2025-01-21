package com.pape.timetodo.domain.main.controller;

import com.pape.timetodo.domain.main.model.routine.*;
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
@RequestMapping("/v1/routine")
@RequiredArgsConstructor
@Tag(name = "루틴", description = "루틴 API")
public class RoutineController {

    private final TodoRoutineService todoRoutineService;

    /**
     * 루틴 등록
     * @param rq RegisterRoutineRQ
     * @return RegisterRoutineRS
     */
    @PostMapping("/routine/register")
    @Operation(summary = "루틴등록", description = "투두 루틴 등록합니다.")
    public ResponseEntity<RegisterRoutineRS> registerRoutine(@Valid @RequestBody RegisterRoutineRQ rq){

        RegisterRoutineRS result;

        if(rq.getTodoIdx() == null) {
            result = todoRoutineService.createRoutine(rq);
        } else {
            result = todoRoutineService.registerRoutine(rq);
        }

        return ResponseEntity.ok().body(result);
    }


    /**
     * 내 루틴 목록 조회
     * @return GetMyRoutineRS
     */
    @GetMapping("/my")
    @Operation(summary = "내 루틴 조회", description = "내가 만든 루틴 목록을 조회합니다.")
    public ResponseEntity<GetMyRoutineRS> getMyCategory(){

        GetMyRoutineRS result = todoRoutineService.getMyRoutineList();

        return ResponseEntity.ok().body(result);
    }


    /**
     * 루틴 단건 상세 조회
     * @param idx Long
     * @return GetCategoryDetailRS
     */
    @GetMapping("/detail/{idx}")
    @Operation(summary = "루틴 데이터 단건조회", description = "루틴 단건 데이터를 조회합니다.")
    public ResponseEntity<GetRoutineDetailRS> detailCategory(@PathVariable(name = "idx") @Parameter(name="idx", description = "루틴 IDX", in = ParameterIn.PATH, example = "1") Long idx){

        GetRoutineDetailRS result = todoRoutineService.detailRoutine(idx);

        return ResponseEntity.ok().body(result);
    }


    /**
     * 루틴 수정
     * @param rq UpdateRoutineRQ
     * @return UpdateRoutineRS
     */
    @PutMapping("/routine/update")
    @Operation(summary = "루틴수정", description = "투두 루틴 수정합니다.")
    public ResponseEntity<UpdateRoutineRS> updateRoutine(@Valid @RequestBody UpdateRoutineRQ rq){

        UpdateRoutineRS result = todoRoutineService.updateRoutine(rq);

        return ResponseEntity.ok().body(result);
    }


    /**
     * 루틴 삭제
     * @param idx 루틴 삭제할 Todo번호
     */
    @DeleteMapping("/routine/delete/{idx}")
    @Operation(summary = "루틴삭제", description = "투두 루틴 삭제합니다.")
    public ResponseEntity<Void> deleteRoutine(@PathVariable @Parameter(name="idx", description = "투두 IDX", in = ParameterIn.PATH, example = "1") Long idx){

        if(idx == null) throw new AppException(ExceptionCode.NON_VALID_PARAMETER);

        todoRoutineService.deleteRoutine(idx);

        return ResponseEntity.ok().build();
    }

}
