package com.pape.timetodo.domain.main.service;

import com.pape.timetodo.domain.main.model.GetCategoryModel;
import com.pape.timetodo.domain.main.model.category.*;
import com.pape.timetodo.global.exception.AppException;
import com.pape.timetodo.global.exception.ExceptionCode;
import com.pape.timetodo.global.jpa.entity.CategoryEntity;
import com.pape.timetodo.global.jpa.entity.UsersEntity;
import com.pape.timetodo.global.jpa.repository.CategoryQueryRepository;
import com.pape.timetodo.global.jpa.repository.CategoryRepository;
import com.pape.timetodo.global.util.UserUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryService {
    
    private final CategoryRepository categoryRepository;

    private final CategoryQueryRepository categoryQueryRepository;

    private final UserUtil userUtil;
    
    /**
     * 카테고리 추가
     * @param rq
     * @return
     */
    @Transactional
    public CreateCategoryRS createCategory(@Valid CreateCategoryRQ rq) {

        UsersEntity usersEntity = userUtil.getUsersEntity();

        if(categoryRepository.findByUsersEntityAndTitle(usersEntity, rq.getCategoryTitle()).isPresent()){
            throw new AppException(ExceptionCode.DATA_DUPLICATE);
        }

        CategoryEntity categoryEntity = CategoryEntity.builder()
            .title(rq.getCategoryTitle())
            .mainColor(rq.getMainColor())
            .usersEntity(usersEntity)
            .publicStatus(rq.getPublicStatus())
            .build();
        
        categoryEntity = categoryRepository.save(categoryEntity);

        CreateCategoryRS result = new CreateCategoryRS();
        result.setCategoryIdx(categoryEntity.getIdx());
        result.setCategoryTitle(categoryEntity.getTitle());
        result.setPublicStatus(categoryEntity.getPublicStatus());
        result.setMainColor(categoryEntity.getMainColor());
        result.setCreateDt(categoryEntity.getCreateDt());
        result.setUpdateDt(categoryEntity.getUpdateDt());

        return result;
    }

    /**
     * 내 카테고리 조회
     * @return
     */
    public MyCategoryRS getMyCategory() {

        UsersEntity usersEntity = userUtil.getUsersEntity();

        List<GetCategoryModel> categoryList = categoryQueryRepository.findMyCategoryByUsresEntity(usersEntity).stream()
            .map(entity -> {
                GetCategoryModel result = new GetCategoryModel();
                result.setIdx(entity.getIdx());
                result.setTitle(entity.getTitle());
                result.setMainColor(entity.getMainColor());
                result.setPublicStatus(entity.getPublicStatus());
                result.setCreateDt(entity.getCreateDt());
                result.setUpdateDt(entity.getUpdateDt());

                return result;
            })
            .toList();

        MyCategoryRS result = new MyCategoryRS();
        result.setCategoryList(categoryList);

        return result;
    }

    @Transactional
    public UpdateCategoryRS updateCategory(UpdateCategoryRQ rq) {

        CategoryEntity categoryEntity = categoryQueryRepository.findByIdAndUsersEntity(rq.getIdx(), userUtil.getUsersEntity());
            
        if(categoryEntity == null) throw new AppException(ExceptionCode.DATA_NOT_FIND);

        if(rq.getCategoryTitle() != null) categoryEntity.setTitle(rq.getCategoryTitle());
        if(rq.getPublicStatus() != null) categoryEntity.setPublicStatus(rq.getPublicStatus());
        if(rq.getMainColor() != null) categoryEntity.setMainColor(rq.getMainColor());

        categoryEntity.setUpdateDt(LocalDateTime.now());
        
        UpdateCategoryRS result = new UpdateCategoryRS();
        result.setIdx(categoryEntity.getIdx());
        result.setCategoryTitle(categoryEntity.getTitle());
        result.setMainColor(categoryEntity.getMainColor());
        result.setPublicStatus(categoryEntity.getPublicStatus());
        result.setCreateDt(categoryEntity.getCreateDt());
        result.setUpdateDt(categoryEntity.getUpdateDt());

        return result;
    }

}
