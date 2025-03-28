package com.pape.timetodo.domain.main.controller;

import com.pape.timetodo.domain.main.model.AllSyncRQ;
import com.pape.timetodo.domain.main.service.DataSyncService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/sync")
@Tag(name = "동기화 컨트롤러", description = "동기화 API")
public class DataSyncContoller {

    private final DataSyncService dataSyncService;

    /**
     * 동기화 - 생성, 수정
     * @param rq LogoutSyncRQ
     * @return boolean
     */
    @PostMapping("/all")
    public ResponseEntity<?> syncAllData(@RequestBody AllSyncRQ rq) {
        dataSyncService.syncAll(rq);
        return ResponseEntity.ok(Map.of("success", true));
    }
}
