package com.cannontech.analysis.tablemodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.demandreset.model.DemandResetResult;
import com.cannontech.amr.demandreset.service.DemandResetBucket;
import com.cannontech.amr.demandreset.service.DemandResetService;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

public class DemandResetFailureResultsModel extends BareReportModelBase<DemandResetFailureResultsModel.ModelRow>
        implements ReportModelMetaInfo {

    @Autowired private PaoDao paoDao;
    @Autowired private DemandResetService demandResetService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    private String state;
    private String title;
    private String resultKey;
    private final List<ModelRow> data = new ArrayList<ModelRow>();
    private final Map<DemandResetBucket, String> states = new HashMap<>();

    static public class ModelRow {
        public String deviceName;
        public String state;
        public String errorDescription;
        public String errorCode;
    }

    @Override
    public void doLoadData() {
        DemandResetBucket currectState = DemandResetBucket.valueOf(state);
        DemandResetResult result = demandResetService.getResult(getResultKey());
        if (currectState == DemandResetBucket.FAILED) {
            addRows(result.getFailedCollection().getDeviceList(), currectState, result);
        } else if (currectState == DemandResetBucket.UNCONFIRMED) {
            addRows(result.getUnconfirmedCollection().getDeviceList(), currectState, result);
        }
    };
    
    private void addRows(List<SimpleDevice> devices, DemandResetBucket currentState, DemandResetResult result) {
        if (!devices.isEmpty()) {
            Map<PaoIdentifier, LiteYukonPAObject> paoIdentifiers =
                paoDao.getLiteYukonPaosById(Lists.transform(devices, SimpleDevice.TO_PAO_IDENTIFIER));
            for (LiteYukonPAObject pao : paoIdentifiers.values()) {
                SimpleDevice device = new SimpleDevice(pao);
                ModelRow row = new ModelRow();
                row.deviceName = pao.getPaoName();
                row.state = states.get(currentState);
                SpecificDeviceErrorDescription error = result.getError(device);
                if (error != null) {
                    row.errorDescription = error.getDescription();
                    row.errorCode = String.valueOf(error.getErrorCode());
                }
                data.add(row);
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

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public LinkedHashMap<String, String> getMetaInfo(YukonUserContext userContext) {
        DemandResetBucket currectState = DemandResetBucket.valueOf(state);
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String bucket = accessor.getMessage(currectState);
        LinkedHashMap<String, String> info = new LinkedHashMap<String, String>();
        title = accessor.getMessage(baseKey + "demandReset.report.title", bucket);
        for (DemandResetBucket state : DemandResetBucket.values()) {
            states.put(state, accessor.getMessage(state));
        }
        return info;
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