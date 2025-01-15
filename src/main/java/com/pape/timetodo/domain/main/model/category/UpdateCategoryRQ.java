package com.pape.timetodo.domain.main.model.category;

import com.pape.timetodo.global.jpa.entity.CategoryEntity.PublicStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateCategoryRQ {

    @NotNull
    @Schema(description = "수정할 카테고리 IDX", example = "1", implementation = Long.class)
    private Long idx;
    
    @Schema(description = "카테고리 타이틀", example = "일", implementation = String.class)
    private String categoryTitle;
    
    @Schema(description = "공개타입[PRIVATE,PUBLIC,PARTIAL]", example = "PRIVATE", implementation = PublicStatus.class)
    private PublicStatus publicStatus;
    
    @Schema(description = "메인컬러", example = "#000000", implementation = String.class)
    private String mainColor;
}
