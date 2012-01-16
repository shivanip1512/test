package com.cannontech.analysis.tablemodel;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.commands.GroupCommandExecutor;
import com.cannontech.common.device.commands.GroupCommandResult;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;

public class GroupCommanderSuccessResultsModel extends BareReportModelBase<GroupCommanderSuccessResultsModel.ModelRow> implements ReportModelMetaInfo {
    
    private GroupCommandExecutor groupCommandExecutor;
	private PaoDao paoDao;
	private PointDao pointDao;
    private String resultKey;
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    private static String title;
    private List<ModelRow> data = new ArrayList<ModelRow>();
    private String command;
    
    static public class ModelRow {
        public String deviceName;
        public String pointName;
        public String pointType;
        public PointValueHolder pointValue;
        public Date pointTimeStamp;
        public String lastResult;
    }
    
    public void doLoadData() {
        
        
        GroupCommandResult result = groupCommandExecutor.getResult(resultKey);
        
        // command
        this.command = result.getCommand();
        
        // success devices
        Set<SimpleDevice> devices = result.getResultHolder().getSuccessfulDevices();
        Map<SimpleDevice, String> resultStringsMap = result.getResultHolder().getResultStrings();
        Map<SimpleDevice, List<PointValueHolder>> pointValuesMap = result.getResultHolder().getValues();
        
        for (YukonDevice device : devices) {
            
            // device name
            LiteYukonPAObject paoObject = paoDao.getLiteYukonPAO(device.getPaoIdentifier().getPaoId());
            String deviceName = paoObject.getPaoName();
            
            // last result
            String lastResult = resultStringsMap.get(device);
            
            // row per point value
            List<PointValueHolder> pointValues = pointValuesMap.get(device);
            
            if (pointValues != null) {
                
                for (PointValueHolder pvh : pointValues) {
                    
                    GroupCommanderSuccessResultsModel.ModelRow row = new GroupCommanderSuccessResultsModel.ModelRow();
                    
                    int pointId = pvh.getId();
                    
                    row.deviceName = deviceName;
                    row.pointName = pointDao.getPointName(pointId);
                    row.pointValue = pvh;
                    row.pointType = PointTypes.getType(pointDao.getLitePoint(pointId).getPointType());
                    row.pointTimeStamp = pvh.getPointDataTimeStamp();
                    row.lastResult = lastResult;
                    
                    data.add(row);
                }
            }
            else {
                
                GroupCommanderSuccessResultsModel.ModelRow row = new GroupCommanderSuccessResultsModel.ModelRow();
                row.deviceName = deviceName;
                row.pointName = null;
                row.pointValue = null;
                row.pointType = null;
                row.pointTimeStamp = null;
                row.lastResult = lastResult;
                
                data.add(row);
            }
        }
    }
    
    @Override
    public LinkedHashMap<String, String> getMetaInfo(YukonUserContext context) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        LinkedHashMap<String, String> info = new LinkedHashMap<String, String>();
        info.put(accessor.getMessage(baseKey + "command"), this.command);
        title = accessor.getMessage(baseKey + "groupCommandSuccess.title");
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
    
    public void setResultKey(String resultKey) {
        this.resultKey = resultKey;
    }
    
    public String getResultKey() {
        return resultKey;
    }
    
    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
    
    @Autowired
    public void setGroupCommandExecutor(GroupCommandExecutor groupCommandExecutor) {
        this.groupCommandExecutor = groupCommandExecutor;
    }
    
    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
    
    @Autowired
    public void setPointDao(PointDao pointDao) {
        this.pointDao = pointDao;
    }
}