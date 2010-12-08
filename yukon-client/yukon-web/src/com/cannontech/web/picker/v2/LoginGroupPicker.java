package com.cannontech.web.picker.v2;

import java.util.Collections;
import java.util.List;

import com.cannontech.common.search.UltraLightLoginGroup;
import com.google.common.collect.Lists;

public class LoginGroupPicker extends LucenePicker<UltraLightLoginGroup> {
    private static List<OutputColumn> outputColumns;
    static {
        List<OutputColumn> columns = Lists.newArrayList();
        String titleKeyPrefix = "yukon.web.picker.loginGroup.";
        columns.add(new OutputColumn("groupName", titleKeyPrefix + "groupName"));
        columns.add(new OutputColumn("groupId", titleKeyPrefix + "groupId"));
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
