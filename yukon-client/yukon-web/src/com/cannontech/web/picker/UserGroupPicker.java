package com.cannontech.web.picker;

import java.util.Collections;
import java.util.List;

import com.cannontech.common.search.result.UltraLightYukonUser;
import com.google.common.collect.Lists;

public class UserGroupPicker extends LucenePicker<UltraLightYukonUser> {
    private static String titleKeyPrefix = "yukon.web.picker.userGroup.";
    
    private static List<OutputColumn> outputColumns;
    static {
        List<OutputColumn> columns = Lists.newArrayList();
        columns.add(new OutputColumn("userGroupName", titleKeyPrefix + "name"));
        columns.add(new OutputColumn("userGroupId", titleKeyPrefix + "userGroupId"));
        outputColumns = Collections.unmodifiableList(columns);
    }

    @Override
    public String getIdFieldName() {
        return "userGroupId";
    }

    @Override
    protected String getLuceneIdFieldName() {
        return "userGroupId";
    }

    @Override
    public List<OutputColumn> getOutputColumns() {
        return outputColumns;
    }
}
