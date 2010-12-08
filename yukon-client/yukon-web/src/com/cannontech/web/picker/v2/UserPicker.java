package com.cannontech.web.picker.v2;

import java.util.Collections;
import java.util.List;

import com.cannontech.common.search.UltraLightYukonUser;
import com.google.common.collect.Lists;

public class UserPicker extends LucenePicker<UltraLightYukonUser> {
    private static List<OutputColumn> outputColumns;
    static {
        List<OutputColumn> columns = Lists.newArrayList();
        String titleKeyPrefix = "yukon.web.picker.user.";

        OutputColumn column = new OutputColumn("userName", titleKeyPrefix + "name");
        column.setMaxCharsDisplayed(18);
        columns.add(column);

        column = new OutputColumn("groupName", titleKeyPrefix + "groupName");
        column.setMaxCharsDisplayed(48);
        columns.add(column);

        columns.add(new OutputColumn("userId", titleKeyPrefix + "userId"));

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
