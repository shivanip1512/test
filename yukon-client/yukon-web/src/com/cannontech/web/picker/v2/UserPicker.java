package com.cannontech.web.picker.v2;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.search.SearchResult;
import com.cannontech.common.search.UltraLightYukonUser;
import com.cannontech.common.search.UserSearcher;
import com.google.common.collect.Lists;

public class UserPicker extends LucenePicker<UltraLightYukonUser> {
    private static List<OutputColumn> outputColumns;
    static {
        List<OutputColumn> columns = Lists.newArrayList();
        String titleKeyPrefix = "yukon.web.modules.picker.user.";

        OutputColumn column = new OutputColumn("userName", titleKeyPrefix + "name");
        column.setMaxCharsDisplayed(18);
        columns.add(column);

        column = new OutputColumn("groupName", titleKeyPrefix + "groupName");
        column.setMaxCharsDisplayed(48);
        columns.add(column);

        columns.add(new OutputColumn("userId", titleKeyPrefix + "userId"));

        outputColumns = Collections.unmodifiableList(columns);
    }

    private UserSearcher userSearcher;

    @Override
    public String getIdFieldName() {
        return "userId";
    }

    @Override
    public List<OutputColumn> getOutputColumns() {
        return outputColumns;
    }

    @Override
    public SearchResult<UltraLightYukonUser> search(String ss, int start,
            int count) {
        SearchResult<UltraLightYukonUser> hits;
        if (StringUtils.isBlank(ss)) {
            hits = userSearcher.allUsers(criteria, start, count);
        } else {
            hits = userSearcher.search(ss, criteria, start , count);
        }
        return hits;
    }

    @Autowired
    public void setUserSearcher(UserSearcher userSearcher) {
        this.userSearcher = userSearcher;
    }
}
