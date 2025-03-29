package com.pape.timetodo.domain.main.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DeleteSyncRQ {

    private List<Long> ddays;
    private List<Long> categories;
    private List<Long> routines;
    private List<Long> todos;

}
