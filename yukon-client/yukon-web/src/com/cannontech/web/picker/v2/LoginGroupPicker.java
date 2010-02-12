package com.cannontech.web.picker.v2;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.search.LoginGroupSearcher;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.search.UltraLightLoginGroup;
import com.google.common.collect.Lists;

public class LoginGroupPicker extends LucenePicker<UltraLightLoginGroup> {
    private static List<OutputColumn> outputColumns;
    static {
        List<OutputColumn> columns = Lists.newArrayList();
        String titleKeyPrefix = "yukon.web.modules.picker.loginGroup.";
        columns.add(new OutputColumn("groupName", titleKeyPrefix + "groupName"));
        columns.add(new OutputColumn("groupId", titleKeyPrefix + "groupId"));
        outputColumns = Collections.unmodifiableList(columns);
    }

    private LoginGroupSearcher loginGroupSearcher;

    @Override
    public String getIdFieldName() {
        return "groupId";
    }

    @Override
    public List<OutputColumn> getOutputColumns() {
        return outputColumns;
    }

    @Override
    public SearchResult<UltraLightLoginGroup> search(String ss, int start,
            int count) {
        SearchResult<UltraLightLoginGroup> hits;
        if (StringUtils.isBlank(ss)) {
            hits = loginGroupSearcher.allLoginGroups(criteria, start, count);
        } else {
            hits = loginGroupSearcher.search(ss, criteria, start , count);
        }
        return hits;
    }

    @Autowired
    public void setLoginGroupSearcher(LoginGroupSearcher loginGroupSearcher) {
        this.loginGroupSearcher = loginGroupSearcher;
    }
}
