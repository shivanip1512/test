package com.cannontech.web.picker.v2;

import java.util.Collections;
import java.util.List;

import com.cannontech.common.search.UltraLightYukonUser;
import com.google.common.collect.Lists;

public class UserGroupPicker extends LucenePicker<UltraLightYukonUser> {
    private static String titleKeyPrefix = "yukon.web.picker.userGroup.";
    
    private static List<OutputColumn> outputColumns;
    static {
        List<OutputColumn> columns = Lists.newArrayList();
        columns.add(new OutputColumn("userGroupName", titleKeyPrefix + "userGroupName"));
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
