package com.pape.timetodo.domain.main.controller;

import com.pape.timetodo.domain.main.model.category.*;
import com.pape.timetodo.domain.main.service.CategoryService;
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
@RequiredArgsConstructor
@RequestMapping("/v1/category")
@Tag(name = "카테고리", description = "카테고리 API")
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 카테고리 추가
     * @param rq CreateCategoryRQ
     * @return CreateCategoryRS
     */
    @PostMapping("/create")
    @Operation(summary = "카테고리 추가", description = "카테고리를 추가합니다.")
    public ResponseEntity<CreateCategoryRS> createCategory(@Valid @RequestBody CreateCategoryRQ rq){

        CreateCategoryRS result = categoryService.createCategory(rq);

        return ResponseEntity.ok().body(result);
    }

    /**
     * 카테고리 수정
     * @param rq UpdateCategoryRQ
     * @return UpdateCategoryRS
     */
    @PutMapping("/update")
    @Operation(summary = "카테고리 수정", description = "카테고리를 수정합니다.")
    public ResponseEntity<UpdateCategoryRS> updateCategory(@Valid @RequestBody UpdateCategoryRQ rq){

        UpdateCategoryRS result = categoryService.updateCategory(rq);

        return ResponseEntity.ok().body(result);
    }

    /**
     * 내 카테고리 조회
     * @return
     */
    @GetMapping("/my")
    @Operation(summary = "내 카테고리 조회", description = "내가 만든 카테고리를 조회합니다.")
    public ResponseEntity<MyCategoryRS> getMyCategory(){

        MyCategoryRS result = categoryService.getMyCategory();

        return ResponseEntity.ok().body(result);
    }

    /**
     * 카테고리 단건 상세 조회
     * @param idx Long
     * @return GetCategoryDetailRS
     */
    @GetMapping("/detail/{idx}")
    @Operation(summary = "카테고리 데이터 단건조회", description = "카테고리 단건 데이터를 조회합니다.")
    public ResponseEntity<GetCategoryDetailRS> detailCategory(@PathVariable(name = "idx") @Parameter(name="idx", description = "카테고리 IDX", in = ParameterIn.PATH, example = "1") Long idx){

        GetCategoryDetailRS result = categoryService.detailCategory(idx);

        return ResponseEntity.ok().body(result);
    }

}
