package com.cannontech.analysis.tablemodel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultDao;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultsFilterType;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionResult;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;

public class CommandRequestExecutionResultsModel extends BareReportModelBase<CommandRequestExecutionResultsModel.ModelRow> {
    
    // dependencies
    private CommandRequestExecutionResultDao commandRequestExecutionResultDao;
    private DeviceErrorTranslatorDao deviceErrorTranslatorDao;
    private DeviceDao deviceDao;
    private PaoDao paoDao;
    
    // inputs
    int commandRequestExecutionId;
    String resultsFilterType;
    
    // member variables
    private static String title = "Command Request Exectuion Results";
    private List<ModelRow> data = new ArrayList<ModelRow>();
    
    
    static public class ModelRow {
        
        public String command;
        public String status;
        public String deviceName;
        public String routeName;
        public Date completeTime;
    }
    
    public void doLoadData() {
    	
    	CommandRequestExecutionResultsFilterType reportFilterType = CommandRequestExecutionResultsFilterType.valueOf(this.resultsFilterType);
        
    	List<CommandRequestExecutionResult> results = commandRequestExecutionResultDao.getResultsByExecutionId(this.commandRequestExecutionId, reportFilterType);
    	
    	for (CommandRequestExecutionResult result : results) {
    		
    		
    		CommandRequestExecutionResultsModel.ModelRow row = new CommandRequestExecutionResultsModel.ModelRow();
    		row.command = result.getCommand();
    		
    		DeviceErrorDescription deviceErrorDescription = deviceErrorTranslatorDao.translateErrorCode(result.getErrorCode());
    		row.status = deviceErrorDescription.getDescription();
    		
    		row.deviceName = null;
    		Integer deviceId = result.getDeviceId();
    		if (deviceId != null) {
    			row.deviceName = deviceDao.getFormattedName(deviceId);
    		}
    		
    		row.routeName = null;
    		Integer routeId = result.getRouteId();
    		if (routeId != null) {
    			LiteYukonPAObject routePao = paoDao.getLiteYukonPAO(deviceId);
    			row.routeName = routePao.getPaoName();
    		}
    		
    		row.completeTime = null;
    		Date completeTime = result.getCompleteTime();
    		if (completeTime != null) {
    			row.completeTime = completeTime;
    		}
    		
    		data.add(row);
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
    
    public String getResultsFilterType() {
		return resultsFilterType;
	}
    public void setResultsFilterType(String resultsFilterType) {
		this.resultsFilterType = resultsFilterType;
	}
    
    @Autowired
    public void setCommandRequestExecutionResultDao(
			CommandRequestExecutionResultDao commandRequestExecutionResultDao) {
		this.commandRequestExecutionResultDao = commandRequestExecutionResultDao;
	}
    
    @Autowired
    public void setDeviceErrorTranslatorDao(
			DeviceErrorTranslatorDao deviceErrorTranslatorDao) {
		this.deviceErrorTranslatorDao = deviceErrorTranslatorDao;
	}
    
    @Autowired
    public void setDeviceDao(DeviceDao deviceDao) {
		this.deviceDao = deviceDao;
	}
    
    @Autowired
    public void setPaoDao(PaoDao paoDao) {
		this.paoDao = paoDao;
	}

}
