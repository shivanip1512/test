package com.cannontech.analysis.tablemodel;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.deviceread.dao.PlcDeviceAttributeReadService;
import com.cannontech.amr.deviceread.service.GroupMeterReadResult;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;

public class GroupMeterReadSuccessResultsModel extends BareReportModelBase<GroupMeterReadSuccessResultsModel.ModelRow> implements ReportModelMetaInfo {
    
    private PlcDeviceAttributeReadService plcDeviceAttributeReadService;
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    private static String title;
    private PaoDao paoDao;
    private PointDao pointDao;
    private String resultKey;
    private List<ModelRow> data = new ArrayList<ModelRow>();
    private Set<? extends Attribute> attributes;
    @Autowired private ObjectFormattingService objectFormattingService;

    static public class ModelRow {
        public String deviceName;
        public String pointName;
        public String pointType;
        public PointValueHolder pointValue;
        public Date pointTimeStamp;
        public String lastResult;
    }
    
    @Override
    public void doLoadData() {
        
        GroupMeterReadResult result = plcDeviceAttributeReadService.getResult(resultKey);
        
        // attributes
        this.attributes = result.getAttributes();
        
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
                    
                    GroupMeterReadSuccessResultsModel.ModelRow row = new GroupMeterReadSuccessResultsModel.ModelRow();
                    
                    int pointId = pvh.getId();
                    
                    row.deviceName = deviceName;
                    row.pointName = pointDao.getPointName(pointId);
                    row.pointValue = pvh;
                    row.pointType = PointTypes.getType(pointDao.getLitePoint(pointId).getPointType());
                    row.pointTimeStamp = pvh.getPointDataTimeStamp();
                    row.lastResult = lastResult;
                    
                    data.add(row);
                }
            } else {
                
                GroupMeterReadSuccessResultsModel.ModelRow row = new GroupMeterReadSuccessResultsModel.ModelRow();
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
    public LinkedHashMap<String, String> getMetaInfo(final YukonUserContext context) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        LinkedHashMap<String, String> info = new LinkedHashMap<String, String>();
        Function<Attribute, String> attrDesc = new Function<Attribute, String>() {
            @Override
            public String apply(Attribute from) {
                return objectFormattingService.formatObjectAsString(from.getMessage(), context);
            }
        };
        
        if (attributes == null && StringUtils.isNotBlank(resultKey)) {
            GroupMeterReadResult result = plcDeviceAttributeReadService.getResult(resultKey);
            // attributes
            this.attributes = result.getAttributes();
            String prettyText = StringUtils.join(Iterables.transform(attributes, attrDesc).iterator(), ", ");
            info.put(accessor.getMessage(baseKey + "attributes"), prettyText);
        }
        
        title = accessor.getMessage(baseKey + "groupMeterReadSuccess.title");
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

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
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
    public void setPlcDeviceAttributeReadService(PlcDeviceAttributeReadService plcDeviceAttributeReadService) {
        this.plcDeviceAttributeReadService = plcDeviceAttributeReadService;
    }
    
    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
    
    @Autowired
    public void setPointDao(PointDao pointDao) {
        this.pointDao = pointDao;
    }
    
    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
    
}