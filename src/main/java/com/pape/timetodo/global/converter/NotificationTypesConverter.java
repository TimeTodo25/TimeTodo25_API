package com.pape.timetodo.global.converter;

import com.pape.timetodo.global.constant.NotificationType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

@Converter
public class NotificationTypesConverter implements AttributeConverter<Set<NotificationType>, String> {
    @Override
    public String convertToDatabaseColumn(Set<NotificationType> notificationTypes) {
        if (notificationTypes == null || notificationTypes.isEmpty()) {
            return null;
        }
        return notificationTypes.stream()
                .map(Enum::name)
                .collect(Collectors.joining(",,"));
    }

    @Override
    public Set<NotificationType> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return EnumSet.noneOf(NotificationType.class);
        }
        return Arrays.stream(dbData.split(",,"))
                .map(NotificationType::valueOf)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(NotificationType.class)));
    }
}
