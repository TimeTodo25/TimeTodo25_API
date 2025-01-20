package com.pape.timetodo.domain.main.controller;

import com.pape.timetodo.domain.main.model.dday.RegisterDdayRS;
import com.pape.timetodo.domain.main.model.dday.registerDdayRQ;
import com.pape.timetodo.domain.main.service.DdayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<RegisterDdayRS> registerDday(@Valid @RequestBody registerDdayRQ rq){

        RegisterDdayRS result = ddayService.registerDday(rq);

        return ResponseEntity.ok().body(result);
    }
}
