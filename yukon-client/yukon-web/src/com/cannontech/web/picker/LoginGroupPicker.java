package com.cannontech.web.picker;

import java.util.Collections;
import java.util.List;

import com.cannontech.common.search.result.UltraLightLoginGroup;
import com.google.common.collect.Lists;

public class LoginGroupPicker extends LucenePicker<UltraLightLoginGroup> {
    
    private static List<OutputColumn> outputColumns;
    
    static {
        List<OutputColumn> columns = Lists.newArrayList();
        String titleKeyPrefix = "yukon.web.picker.loginGroup.";
        columns.add(new OutputColumn("groupName", titleKeyPrefix + "groupName"));
        columns.add(new OutputColumn("description", titleKeyPrefix + "description"));
        outputColumns = Collections.unmodifiableList(columns);
    }
    
    @Override
    public String getIdFieldName() {
        return "groupId";
    }
    
    @Override
    protected String getLuceneIdFieldName() {
        return "groupid";
    }
    
    @Override
    public List<OutputColumn> getOutputColumns() {
        return outputColumns;
    }
    
}