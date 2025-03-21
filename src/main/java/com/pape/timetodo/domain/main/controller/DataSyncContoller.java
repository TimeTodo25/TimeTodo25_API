package com.pape.timetodo.domain.main.controller;

import com.pape.timetodo.domain.main.model.LogoutSyncRQ;
import com.pape.timetodo.domain.main.service.DataSyncService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/sync")
@Tag(name = "로그아웃 용 동기화 컨트롤러", description = "동기화 API")
public class DataSyncContoller {

    private final DataSyncService dataSyncService;

    @PostMapping("/all")
    public ResponseEntity<?> syncAllData(@RequestBody LogoutSyncRQ rq) {
        dataSyncService.syncAll(rq);
        return ResponseEntity.ok(Map.of("success", true));
    }
}
