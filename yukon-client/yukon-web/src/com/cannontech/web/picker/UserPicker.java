package com.cannontech.web.picker;

import java.util.Collections;
import java.util.List;

import com.cannontech.common.search.result.UltraLightYukonUser;
import com.google.common.collect.Lists;

public class UserPicker extends LucenePicker<UltraLightYukonUser> {
    
    private static List<OutputColumn> outputColumns;
    
    static {
        List<OutputColumn> columns = Lists.newArrayList();
        String titleKeyPrefix = "yukon.web.picker.user.";
        
        OutputColumn column = new OutputColumn("userName", titleKeyPrefix + "name");
        column.setMaxCharsDisplayed(18);
        columns.add(column);
        
        columns.add(new OutputColumn("userGroupName", titleKeyPrefix + "userGroupName"));
        
        outputColumns = Collections.unmodifiableList(columns);
    }
    
    @Override
    public String getIdFieldName() {
        return "userId";
    }
    
    @Override
    protected String getLuceneIdFieldName() {
        return "userid";
    }
    
    @Override
    public List<OutputColumn> getOutputColumns() {
        return outputColumns;
    }
    
}