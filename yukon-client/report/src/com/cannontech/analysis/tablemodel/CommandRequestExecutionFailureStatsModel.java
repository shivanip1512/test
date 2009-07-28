package com.cannontech.analysis.tablemodel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultDao;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultsFilterType;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionResult;

public class CommandRequestExecutionFailureStatsModel extends BareReportModelBase<CommandRequestExecutionFailureStatsModel.ModelRow> {
    
    // dependencies
    private CommandRequestExecutionResultDao commandRequestExecutionResultDao;
    private DeviceErrorTranslatorDao deviceErrorTranslatorDao;
    
    // inputs
    int commandRequestExecutionId;
    
    // member variables
    private static String title = "Command Request Exectuion Failure Stats Report";
    private List<ModelRow> data = new ArrayList<ModelRow>();
    
    
    static public class ModelRow {
        
        public String status;
        public int count;
        public String percentage;
    }
    
    public void doLoadData() {
    	
    	List<CommandRequestExecutionResult> results = commandRequestExecutionResultDao.getResultsByExecutionId(this.commandRequestExecutionId, CommandRequestExecutionResultsFilterType.FAIL);
    	
    	// organize
    	Map<Integer, CreStat> erroCodeCreStatMap = new HashMap<Integer, CreStat>();
    	for (CommandRequestExecutionResult result : results) {
    		
    		int errorCode = result.getErrorCode();
    		
    		CreStat creStat = erroCodeCreStatMap.get(errorCode);
    		if (creStat == null) {
    			creStat = new CreStat(errorCode);
    			erroCodeCreStatMap.put(errorCode, creStat);
    		}
    		creStat.addToCount();
    	}
    	
    	// sort
    	List<CreStat> creStats = new ArrayList<CreStat>(erroCodeCreStatMap.values());
    	Collections.sort(creStats);
    	
    	// total
    	int totalFailures = 0;
    	for (CreStat creStat : creStats) {
    		totalFailures += creStat.getCount();
    	}
    	
    	// rows
    	for (CreStat creStat : creStats) {
    		
    		CommandRequestExecutionFailureStatsModel.ModelRow row = new CommandRequestExecutionFailureStatsModel.ModelRow();
    		
    		DeviceErrorDescription deviceErrorDescription = deviceErrorTranslatorDao.translateErrorCode(creStat.getErrorCode());
    		row.status = deviceErrorDescription.getDescription();
    		
    		row.count = creStat.getCount();
    		
    		DecimalFormat df = new DecimalFormat("0.#");
    		double p = (creStat.getCount() / (float)totalFailures) * 100.0;
    		row.percentage = df.format(p);
    		
    		data.add(row);
    	}
    	
    	
    }
    
    private class CreStat implements Comparable<CreStat> {
    	
    	int errorCode = 0;
    	int count = 0;
    	
    	public CreStat(int errorCode) {
    		this.errorCode = errorCode;
    	}
    	
    	public void addToCount() {
    		this.count += 1;
    	}
    	
    	public int getCount() {
			return count;
		}
    	
    	public int getErrorCode() {
			return errorCode;
		}
    	
    	public int compareTo(CreStat o) {
    		
    		if (this.count < o.count) {
    			return 1;
    		} else if (this.count > o.count) {
    			return -1;
    		} else {
    			return 0;
    		}
    	}
    }
    
    @Override
    protected ModelRow getRow(int rowIndex) {
        return data.get(rowIndex);
    }

    @Override
    protected Class<ModelRow> getRowClass() {
        return ModelRow.class;
    }

    public int getRowCount() {
        return data.size();
    }

    public String getTitle() {
        return title;
    }
    
    public int getCommandRequestExecutionId() {
		return commandRequestExecutionId;
	}
    public void setCommandRequestExecutionId(int commandRequestExecutionId) {
		this.commandRequestExecutionId = commandRequestExecutionId;
	}
    
    @Autowired
    public void setCommandRequestExecutionResultDao(
			CommandRequestExecutionResultDao commandRequestExecutionResultDao) {
		this.commandRequestExecutionResultDao = commandRequestExecutionResultDao;
	}
    
    @Autowired
    public void setDeviceErrorTranslatorDao(DeviceErrorTranslatorDao deviceErrorTranslatorDao) {
		this.deviceErrorTranslatorDao = deviceErrorTranslatorDao;
	}
}
