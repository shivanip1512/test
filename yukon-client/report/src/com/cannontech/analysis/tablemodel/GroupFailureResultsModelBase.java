package com.cannontech.analysis.tablemodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.common.device.commands.GroupActionResult;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonPAObject;

public abstract class GroupFailureResultsModelBase extends BareReportModelBase<GroupFailureResultsModelBase.ModelRow> implements ReportModelMetaInfo {

    protected String resultKey;
    protected List<ModelRow> data = new ArrayList<ModelRow>();
    static public class ModelRow {
        public String deviceName;
        public String errorDescription;
        public String errorCode;
    }
    
    protected void doLoadData(GroupActionResult result) {
        
        Set<SimpleDevice> devices = result.getResultHolder().getFailedDevices();
        Map<SimpleDevice, SpecificDeviceErrorDescription> resultErrorsMap = result.getResultHolder().getErrors();

        for (YukonDevice device : devices) {

            // device name
            LiteYukonPAObject paoObject = DefaultDatabaseCache.getInstance().getAllPaosMap().get(device.getPaoIdentifier().getPaoId());
            String deviceName = paoObject.getPaoName();

            // error
            SpecificDeviceErrorDescription error = resultErrorsMap.get(device);

            ModelRow row = new ModelRow();

            row.deviceName = deviceName;
            row.errorDescription = error.getDescription();
            row.errorCode = error.getErrorCode().toString();

            data.add(row);

        }
    };
    
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
    
    public void setResultKey(String resultKey) {
        this.resultKey = resultKey;
    }
    
    public String getResultKey() {
        return resultKey;
    }
}