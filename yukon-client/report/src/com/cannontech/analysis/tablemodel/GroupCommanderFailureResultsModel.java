package com.cannontech.analysis.tablemodel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.common.device.commands.GroupCommandExecutor;
import com.cannontech.common.device.commands.GroupCommandResult;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.user.YukonUserContext;

public class GroupCommanderFailureResultsModel extends BareReportModelBase<GroupCommanderFailureResultsModel.ModelRow> implements ReportModelMetaInfo {
    
    // dependencies
    private GroupCommandExecutor groupCommandExecutor;
	private PaoDao paoDao;
	
	
    // inputs
    String resultKey;

    // member variables
    private static String title = "Group Command Failure Results";
    private List<ModelRow> data = new ArrayList<ModelRow>();
    private String command;
    
    static public class ModelRow {
        public String deviceName;
        public String errorDescription;
        public String errorCode;
    }
    
    public void doLoadData() {
        
        
        GroupCommandResult result = groupCommandExecutor.getResult(resultKey);
        
        // command
        this.command = result.getCommand();
        
        // success devices
        Set<SimpleDevice> devices = result.getResultHolder().getFailedDevices();
        Map<SimpleDevice, SpecificDeviceErrorDescription> resultErrorsMap = result.getResultHolder().getErrors();
        
        for (YukonDevice device : devices) {
            
            // device name
            LiteYukonPAObject paoObject = paoDao.getLiteYukonPAO(device.getPaoIdentifier().getPaoId());
            String deviceName = paoObject.getPaoName();
            
            // error
            SpecificDeviceErrorDescription error = resultErrorsMap.get(device);
            
            GroupCommanderFailureResultsModel.ModelRow row = new GroupCommanderFailureResultsModel.ModelRow();
            
            row.deviceName = deviceName;
            row.errorDescription = error.getDescription();
            row.errorCode = error.getErrorCode().toString();
            
            data.add(row);
            
        }
    }
    
    @Override
    public LinkedHashMap<String, String> getMetaInfo(YukonUserContext userContext) {
        
        LinkedHashMap<String, String> info = new LinkedHashMap<String, String>();
        
        info.put("Command", this.command);
        return info;
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
    
    @Autowired
    public void setGroupCommandExecutor(GroupCommandExecutor groupCommandExecutor) {
        this.groupCommandExecutor = groupCommandExecutor;
    }
    
    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
    
    public void setResultKey(String resultKey) {
        this.resultKey = resultKey;
    }
    
    public String getResultKey() {
        return resultKey;
    }
}
