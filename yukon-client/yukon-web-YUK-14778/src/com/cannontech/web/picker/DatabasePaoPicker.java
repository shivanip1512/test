package com.cannontech.web.picker;

import java.util.Collections;
import java.util.List;

import com.cannontech.common.search.pao.db.UltraLightPaoRowMapper;
import com.cannontech.common.search.result.UltraLightPao;
import com.google.common.collect.Lists;

public abstract class DatabasePaoPicker extends DatabasePicker<UltraLightPao> {
    
    private final static String[] searchColumnNames = new String[] { "paoName" };
    private final static List<OutputColumn> outputColumns;
    static {
        List<OutputColumn> columns = Lists.newArrayList();
        String titleKeyPrefix = "yukon.web.picker.pao.";
        
        columns.add(new OutputColumn("paoName", titleKeyPrefix + "name"));
        columns.add(new OutputColumn("type", titleKeyPrefix + "type"));
        
        outputColumns = Collections.unmodifiableList(columns);
    }
    
    public DatabasePaoPicker() {
        super(new UltraLightPaoRowMapper(), searchColumnNames);
    }
    
    @Override
    public String getIdFieldName() {
        return "paoId";
    }
    
    @Override
    protected String getDatabaseIdFieldName() {
        return "paobjectId";
    }
    
    @Override
    public List<OutputColumn> getOutputColumns() {
        return outputColumns;
    }
    
}