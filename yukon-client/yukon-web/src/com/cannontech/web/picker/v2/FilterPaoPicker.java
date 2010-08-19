package com.cannontech.web.picker.v2;

import java.util.Collections;
import java.util.List;

import com.cannontech.common.search.UltraLightPao;
import com.cannontech.common.search.UltraLightPaoAdapter;
import com.cannontech.common.search.pao.db.UltraLightPaoRowMapper;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class FilterPaoPicker extends DatabasePicker<UltraLightPao, UltraLightPaoAdapter> {
    private final static String[] searchColumnNames = new String[] {"paoName"};
    private final static List<OutputColumn> outputColumns;
    static {
        List<OutputColumn> columns = Lists.newArrayList();
        String titleKeyPrefix = "yukon.web.picker.pao.";

        columns.add(new OutputColumn("paoName", titleKeyPrefix + "name"));
        columns.add(new OutputColumn("type", titleKeyPrefix + "type"));

        outputColumns = Collections.unmodifiableList(columns);
    }

    public FilterPaoPicker() {
        super(new UltraLightPaoRowMapper(), searchColumnNames);
    }

    @Override
    protected Function<UltraLightPaoAdapter, UltraLightPao> getTypeTranslator() {
        return new Function<UltraLightPaoAdapter, UltraLightPao>() {
            @Override
            public UltraLightPao apply(final UltraLightPaoAdapter from) {
                return new UltraLightPao() {
                    @Override
                    public int getPaoId() {
                        return from.getPaoId();
                    }

                    @Override
                    public String getPaoName() {
                        return from.getPaoName();
                    }

                    @Override
                    public String getType() {
                        return from.getType();
                    }};
            }};
    }

    @Override
    public String getIdFieldName() {
        return "paoId";
    }

    @Override
    public List<OutputColumn> getOutputColumns() {
        return outputColumns;
    }
}
