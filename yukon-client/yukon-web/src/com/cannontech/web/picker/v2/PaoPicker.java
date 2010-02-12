package com.cannontech.web.picker.v2;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.search.PaoTypeSearcher;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.search.UltraLightPao;
import com.google.common.collect.Lists;

public class PaoPicker extends LucenePicker<UltraLightPao> {
    private static List<OutputColumn> outputColumns;
    static {
        List<OutputColumn> columns = Lists.newArrayList();
        String titleKeyPrefix = "yukon.web.modules.picker.pao.";

        columns.add(new OutputColumn("paoName", titleKeyPrefix + "name"));
        columns.add(new OutputColumn("type", titleKeyPrefix + "type"));

        outputColumns = Collections.unmodifiableList(columns);
    }

    private PaoTypeSearcher paoTypeSearcher;

    @Override
    public String getIdFieldName() {
        return "paoId";
    }

    @Override
    public List<OutputColumn> getOutputColumns() {
        return outputColumns;
    }

    @Override
    public SearchResult<UltraLightPao> search(String ss, int start, int count) {
        SearchResult<UltraLightPao> hits;
        if (StringUtils.isBlank(ss)) {
            hits = paoTypeSearcher.allPaos(criteria, start, count);
        } else {
            hits = paoTypeSearcher.search(ss, criteria, start, count);
        }
        return hits;
    }

    @Autowired
    public void setPaoTypeSearcher(PaoTypeSearcher paoTypeSearcher) {
        this.paoTypeSearcher = paoTypeSearcher;
    }
}
