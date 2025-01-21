package com.pape.timetodo.domain.main.controller;

import com.pape.timetodo.domain.main.model.dday.RegisterDdayRQ;
import com.pape.timetodo.domain.main.model.dday.RegisterDdayRS;
import com.pape.timetodo.domain.main.model.dday.UpdateDdayRQ;
import com.pape.timetodo.domain.main.model.dday.UpdateDdayRS;
import com.pape.timetodo.domain.main.service.DdayService;
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
@RequestMapping("/v1/dday")
@RequiredArgsConstructor
@Tag(name = "디데이", description = "디데이 API")
public class DdayController {

    private final DdayService ddayService;

    /**
     * 디데이 등록
     * @param rq RegisterDayRQ
     * @return RegisterDdayRS
     */
    @PostMapping("/register/d-day")
    @Operation(summary = "디데이 등록", description = "디데이를 등록합니다.")
    public ResponseEntity<RegisterDdayRS> registerDday(@Valid @RequestBody RegisterDdayRQ rq){

        RegisterDdayRS result = ddayService.registerDday(rq);

        return ResponseEntity.ok().body(result);
    }

    /**
     * 디데이 수정
     * @param rq UpdateDdayRQ
     * @return UpdateDdayRS
     */
    @PutMapping("/update")
    @Operation(summary = "디데이 수정", description = "디데이를 수정합니다.")
    public ResponseEntity<UpdateDdayRS> updateDday(@Valid @RequestBody UpdateDdayRQ rq){

        UpdateDdayRS result = ddayService.updateDday(rq);

        return ResponseEntity.ok().body(result);
    }

//    /**
//     * 내 디데이 조회
//     * @return
//     */
//    @GetMapping("/my")
//    @Operation(summary = "내 디데이 조회", description = "내가 만든 디데이를 조회합니다.")
//    public ResponseEntity<MyDdayRS> getMyDday(){
//
//        MyDdayRS result = ddayService.getMyDday();
//
//        return ResponseEntity.ok().body(result);
//    }
//
//    /**
//     * 디데이 단건 상세 조회
//     * @param idx Long
//     * @return GetDdayDetailRS
//     */
//    @GetMapping("/detail/{idx}")
//    @Operation(summary = "디데이 데이터 단건조회", description = "디데이 단건 데이터를 조회합니다.")
//    public ResponseEntity<GetDdayDetailRS> detailDday(@PathVariable(name = "idx") @Parameter(name="idx", description = "디데이 IDX", in = ParameterIn.PATH, example = "1") Long idx){
//
//        GetDdayDetailRS result = ddayService.detailDday(idx);
//
//        return ResponseEntity.ok().body(result);
//    }

    /**
     * 디데이 삭제
     * @param idx Long
     */
    @DeleteMapping("/{idx}/delete")
    @Operation(summary = "디데이 삭제", description = "디데이 데이터를 삭제합니다. [논리삭제]")
    public ResponseEntity<Void> deleteDday(@PathVariable(name = "idx", required = false) @Parameter(name="idx", description = "삭제할 디데이 IDX", in = ParameterIn.PATH, example = "1") Long idx){

        if(idx == null){
            throw new AppException(ExceptionCode.NON_VALID_PARAMETER);
        }

        ddayService.deleteDday(idx);

        return ResponseEntity.ok().build();
    }
}
