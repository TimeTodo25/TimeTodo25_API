package com.pape.timetodo.global.converter;

import com.pape.timetodo.global.constant.SortType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

@Converter
public class SortTypesConverter implements AttributeConverter<Set<SortType>, String> {
    @Override
    public String convertToDatabaseColumn(Set<SortType> sortTypes) {
        if (sortTypes == null || sortTypes.isEmpty()) {
            return null;
        }
        return sortTypes.stream()
                .map(Enum::name)
                .collect(Collectors.joining(",,"));
    }

    @Override
    public Set<SortType> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return EnumSet.noneOf(SortType.class);
        }
        return Arrays.stream(dbData.split(",,"))
                .map(SortType::valueOf)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(SortType.class)));
    }
}
