package com.cannontech.analysis.tablemodel;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.Validate;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.loadcontrol.dao.LoadControlProgramDao;
import com.cannontech.loadcontrol.service.data.ProgramControlHistory;
import com.cannontech.spring.YukonSpringHook;
import com.google.common.collect.Lists;

public class ProgramAndGearControlModel extends BareDatedReportModelBase<ProgramAndGearControlModel.ModelRow> {
    private Set<Integer> programIds;
    private LiteYukonUser liteUser;
    
    private LoadControlProgramDao loadControlProgramDao = 
        (LoadControlProgramDao) YukonSpringHook.getBean("loadControlProgramDao");
    
    private List<ModelRow> data = Collections.emptyList();
    
    public ProgramAndGearControlModel() {
    }
    
    static public class ModelRow {
    	public String programName;
        public String startDate;
        public String gearName;
    }
    
    @Override
    protected ModelRow getRow(int rowIndex) {
        return data.get(rowIndex);
    }
    
    @Override
    protected Class<ModelRow> getRowClass() {
        return ModelRow.class;
    }
    
    public String getTitle() {
        return "Program and Gear Control";
    }

    public int getRowCount() {
        return data.size();
    }

    public void doLoadData() {
        Validate.notNull(getStartDate(), "Start date must not be null");
        Validate.notNull(getStopDate(), "End date must not be null");
        
        List<ProgramControlHistory> allControlHistory = Lists.newArrayList();
            
        if(programIds.size() > 0) {
            for(int programId : programIds) {
                allControlHistory.addAll( 
                         loadControlProgramDao.getProgramControlHistoryByProgramId(programId, 
                                                                                   getStartDate(), 
                                                                                   getStopDate()));
            }
        } else {
            allControlHistory = 
                loadControlProgramDao.getAllProgramControlHistory(getStartDate(), getStopDate());
        }
        
        data = Lists.newArrayListWithCapacity(allControlHistory.size());
        
        for(ProgramControlHistory history : allControlHistory) {
            String programName = history.getProgramName();
            Date startDateTime = history.getStartDateTime();
            String gearName = history.getGearName();
            
            ModelRow row = new ModelRow();
            row.programName = programName;
            row.startDate = startDateTime.toString();
            row.gearName = gearName;
            
            data.add(row);
        }
    }
    
    public Set<Integer> getProgramIds() {
        return programIds;
    }

    public void setProgramIds(Set<Integer> programIds) {
        this.programIds = programIds;
    }
    
    public LiteYukonUser getLiteUser() {
        return liteUser;
    }
    public void setLiteUser(LiteYukonUser liteUser) {
        this.liteUser = liteUser;
    }
    
}
