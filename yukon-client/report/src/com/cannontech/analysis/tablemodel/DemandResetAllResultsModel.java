package com.cannontech.analysis.tablemodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.demandreset.model.DemandResetResult;
import com.cannontech.amr.demandreset.service.DemandResetBucket;
import com.cannontech.amr.demandreset.service.DemandResetService;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

public class DemandResetAllResultsModel extends BareReportModelBase<DemandResetAllResultsModel.ModelRow>
        implements ReportModelMetaInfo {

    @Autowired private PaoDao paoDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private DemandResetService demandResetService;
    @Autowired private DateFormattingService dateFormattingService;
    
    private DateTimeFormatter dateTimeFormatter;
    private String resultKey;
    private String title;
    private String state;
    private final List<ModelRow> data = new ArrayList<ModelRow>();
    private final Map<DemandResetBucket, String> states = new HashMap<>();
        
    static public class ModelRow {
        public String deviceName;
        public String state;
        public String timestamp;
        public String errorDescription;
        public String errorCode;
    }
    
    @Override
    public void doLoadData() {
        DemandResetResult result = demandResetService.getResult(getResultKey());        
        addRows(result.getConfirmedCollection().getDeviceList(), DemandResetBucket.CONFIRMED, result);
        addRows(result.getUnconfirmedCollection().getDeviceList(), DemandResetBucket.UNCONFIRMED, result);
        addRows(result.getUnsupportedCollection().getDeviceList(), DemandResetBucket.UNSUPPORTED, result);
        addRows(result.getCanceledCollection().getDeviceList(), DemandResetBucket.CANCELED, result);
        addRows(result.getFailedCollection().getDeviceList(), DemandResetBucket.FAILED, result);
    };
    
    private void addRows(List<SimpleDevice> devices, DemandResetBucket currentState,  DemandResetResult result){
        if (!devices.isEmpty()) {
            Map<PaoIdentifier, LiteYukonPAObject> paoIdentifiers =
                paoDao.getLiteYukonPaosById(Lists.transform(devices,SimpleDevice.TO_PAO_IDENTIFIER));
            
            for (LiteYukonPAObject pao : paoIdentifiers.values()) {
                SimpleDevice device = new SimpleDevice(pao);
                ModelRow row = new ModelRow();
                row.deviceName = pao.getPaoName();
                row.state = states.get(currentState);
                Instant timestamp = result.getTimestamp(device);
                if (timestamp != null) {
                    row.timestamp = dateTimeFormatter.print(timestamp);
                }
                if (currentState == DemandResetBucket.FAILED || currentState == DemandResetBucket.UNCONFIRMED) {
                    SpecificDeviceErrorDescription error = result.getError(device);
                    if (error != null) {
                        row.errorDescription = error.getDescription();
                        row.errorCode = String.valueOf(error.getErrorCode());
                    } else {
                        row.errorDescription =  result.getCustomError(device);
                    }
                }
                data.add(row);
            }
        }
    }
    
    @Override
    public LinkedHashMap<String, String> getMetaInfo(final YukonUserContext userContext) {
        dateTimeFormatter = dateFormattingService.getDateTimeFormatter(DateFormatEnum.DATEHM, YukonUserContext.system);
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        LinkedHashMap<String, String> info = new LinkedHashMap<String, String>();
        title = accessor.getMessage(baseKey + "demandReset.all.title");
        for (DemandResetBucket state : DemandResetBucket.values()) {
            states.put(state, accessor.getMessage(state));
        }
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

    public String getResultKey() {
        return resultKey;
    }
    
    public void setResultKey(String resultKey) {
        this.resultKey = resultKey;
    }
    
    public String getState() {
        return state;
    }
    
    public void setState(String state) {
        this.state = state;
    }
}