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
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.service.PaoLoadingService;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class CommandRequestExecutionResultsModel extends BareReportModelBase<CommandRequestExecutionResultsModel.ModelRow> {
    
    // dependencies
    private CommandRequestExecutionResultDao commandRequestExecutionResultDao;
    private DeviceErrorTranslatorDao deviceErrorTranslatorDao;
    private PaoLoadingService paoLoadingService;
    private PaoDao paoDao;
    
    // inputs
    int commandRequestExecutionId;
    String resultsFilterType;
    
    // member variables
    private static String title = "Command Request Execution Results";
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
    	
    	// build PaoIdentifiers list
    	List<Integer> paoIds = new ArrayList<Integer>();
    	for (CommandRequestExecutionResult result : results) {
    	    
    	    Integer deviceId = result.getDeviceId();
    	    if (deviceId != null) {
    	    	paoIds.add(deviceId);
    	    }
    	    
    	    Integer routeId = result.getRouteId();
            if (routeId != null) {
            	paoIds.add(routeId);
            }
    	}
    	
    	List<PaoIdentifier> paoIdentifiers = paoDao.getPaoIdentifiersForPaoIds(paoIds);
    	
    	// load DisplayablePaos
    	List<DisplayablePao> displayableDevices = paoLoadingService.getDisplayableDevices(paoIdentifiers);
    	
    	// make map
    	ImmutableMap<Integer, DisplayablePao> deviceLookup = Maps.uniqueIndex(displayableDevices, new Function<DisplayablePao, Integer>() {
            @Override
            public Integer apply(DisplayablePao displayablePao) {
                return displayablePao.getPaoIdentifier().getPaoId();
            }
        });
    	
    	
    	for (CommandRequestExecutionResult result : results) {
    		
    		
    		CommandRequestExecutionResultsModel.ModelRow row = new CommandRequestExecutionResultsModel.ModelRow();
    		row.command = result.getCommand();
    		
    		int errorCode = result.getErrorCode();
    		if (errorCode != 0) {
    		    // This is not right! We don't want the deviceErrorTranslatorDao to get us the description
    		    // because those descriptions are the default ones!
	    		DeviceErrorDescription deviceErrorDescription = deviceErrorTranslatorDao.translateErrorCode(result.getErrorCode());
	    		row.status = "(Err: " + errorCode + ") " + deviceErrorDescription.getPorter();
    		} else {
    			row.status = "Successful";
    		}
    		
    		DisplayablePao device = deviceLookup.get(result.getDeviceId());
    		row.deviceName = device != null ? device.getName() : null;
    		
    		DisplayablePao route = deviceLookup.get(result.getRouteId());
    		row.routeName = route != null ? route.getName() : "(auto)";
    		
    		row.completeTime = result.getCompleteTime();
    		
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
    public void setPaoLoadingService(PaoLoadingService paoLoadingService) {
        this.paoLoadingService = paoLoadingService;
    }
    
    @Autowired
    public void setPaoDao(PaoDao paoDao) {
		this.paoDao = paoDao;
	}
}
