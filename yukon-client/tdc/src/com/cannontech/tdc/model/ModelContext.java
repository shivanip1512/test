package com.cannontech.tdc.model;

import java.util.List;

import com.cannontech.tdc.template.TemplateDisplayModel;

public interface ModelContext {

    public static String[] ALL_CTXTS = { TemplateDisplayModel.class.getName() };

    void initModelContextList();

    List<String> getModelContextList();

}
