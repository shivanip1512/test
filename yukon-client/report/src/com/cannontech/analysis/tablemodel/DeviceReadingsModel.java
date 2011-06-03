package com.cannontech.analysis.tablemodel;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Clusivity;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.service.CachingPointFormattingService;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.core.service.PointFormattingService;
import com.cannontech.core.service.PointFormattingService.Format;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.ListMultimap;

public class DeviceReadingsModel extends FilteredReportModelBase<DeviceReadingsModel.ModelRow> 
                                   implements UserContextModelAttributes {

    private Logger log = YukonLogManager.getLogger(DeviceReadingsModel.class);

    // member variables
    private List<ModelRow> data = new ArrayList<ModelRow>();
    private Attribute attribute;
    private boolean getAll = true;
    private boolean excludeDisabledDevices = false;

    private PointFormattingService pointFormattingService;
    private PaoLoadingService paoLoadingService;
    private RawPointHistoryDao rawPointHistoryDao;

    private YukonUserContext userContext;

    static public class ModelRow {
        public String deviceName;
        public String type;
        public String date;
        public String value;
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
    public String getTitle() {
        return "Device Readings Report";
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public void doLoadData() {
        Iterable<? extends YukonPao> devices = getYukonPaoList();
        List<DisplayablePao> displayableDevices = paoLoadingService.getDisplayableDevices(devices);
        
        ListMultimap<PaoIdentifier, PointValueQualityHolder> intermediateResults;
        
        if (getAll) {
            intermediateResults = rawPointHistoryDao.getAttributeData(displayableDevices, attribute, getStartDate(), getStopDate(), excludeDisabledDevices, Clusivity.EXCLUSIVE_INCLUSIVE, Order.REVERSE);
        } else {
            intermediateResults = rawPointHistoryDao.getLimitedAttributeData(displayableDevices, attribute, null, null, 1, excludeDisabledDevices, Clusivity.EXCLUSIVE_INCLUSIVE, Order.REVERSE);
        }
        CachingPointFormattingService cachingPointFormattingService = pointFormattingService.getCachedInstance();            
        for (DisplayablePao displayablePao : displayableDevices) {
            List<PointValueQualityHolder> values = intermediateResults.get(displayablePao.getPaoIdentifier());
            for (PointValueQualityHolder pointValueHolder : values) {
        
                DeviceReadingsModel.ModelRow row = new DeviceReadingsModel.ModelRow();
                row.deviceName = displayablePao.getName();
                row.type = displayablePao.getPaoIdentifier().getPaoType().getPaoTypeName();
                row.date = cachingPointFormattingService.getValueString(pointValueHolder,
                                                                        Format.DATE,
                                                                        userContext);
                row.value = cachingPointFormattingService.getValueString(pointValueHolder,
                                                                         Format.SHORT,
                                                                         userContext);
                data.add(row);
            }
        }

        log.info("Report Records Collected from Database: " + data.size());
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public void setRetrieveAll(boolean all) {
        this.getAll = all;
    }
    
    @Override
    public void setUserContext(YukonUserContext userContext){
        this.userContext = userContext;
    }
    
    public YukonUserContext getUserContext() {
        return userContext;
    }

    public void setExcludeDisabledDevices(boolean excludeDisabledDevices) {
        this.excludeDisabledDevices = excludeDisabledDevices;
    }
    
    @Autowired
    public void setPointFormattingService(PointFormattingService pointFormattingService){
        this.pointFormattingService = pointFormattingService;
    }
    
    @Autowired
    public void setPaoLoadingService(PaoLoadingService paoLoadingService) {
        this.paoLoadingService = paoLoadingService;
    }
    
    @Autowired
    public void setRawPointHistoryDao(RawPointHistoryDao rawPointHistoryDao) {
        this.rawPointHistoryDao = rawPointHistoryDao;
    }
}