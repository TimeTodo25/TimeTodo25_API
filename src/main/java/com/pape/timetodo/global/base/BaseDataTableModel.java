package com.pape.timetodo.global.base;

import java.util.List;

public abstract class BaseDataTableModel<P> {

    public abstract List<P> getData();

    public abstract Integer getLength();
    
}
