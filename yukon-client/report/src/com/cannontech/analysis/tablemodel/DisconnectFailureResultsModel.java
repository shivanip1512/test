package com.cannontech.analysis.tablemodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.disconnect.model.DisconnectDeviceState;
import com.cannontech.amr.disconnect.model.DisconnectResult;
import com.cannontech.amr.disconnect.service.DisconnectService;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;

public class DisconnectFailureResultsModel extends BareReportModelBase<DisconnectFailureResultsModel.ModelRow>
        implements ReportModelMetaInfo {

    @Autowired private MeterDao meterDao;
    @Autowired private DisconnectService disconnectService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    private String state;
    private String title;
    private String resultKey;
    private List<ModelRow> data = new ArrayList<ModelRow>();
    private Map<DisconnectDeviceState, String> states = new HashMap<>();

    static public class ModelRow {
        public String deviceName;
        public String errorDescription;
        public String errorCode;
    }

    @Override
    public void doLoadData() {
        DisconnectResult result = disconnectService.getResult(resultKey);
        Map<SimpleDevice, SpecificDeviceErrorDescription> resultErrors = result.getErrors();
        Iterable<YukonMeter> yukonMeters = meterDao.getMetersForYukonPaos(resultErrors.keySet());
        for (YukonMeter yukonMeter : yukonMeters) {
            SpecificDeviceErrorDescription error =
                resultErrors.get(new SimpleDevice(yukonMeter.getDeviceId(), yukonMeter.getPaoType()));
            ModelRow row = new ModelRow();
            row.deviceName = yukonMeter.getName();
            row.errorDescription = error.getDescription();
            row.errorCode = String.valueOf(error.getErrorCode());
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
        DisconnectResult result =disconnectService.getResult(getResultKey());
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        LinkedHashMap<String, String> info = new LinkedHashMap<String, String>();
        String command = accessor.getMessage(result.getCommand());
        title = accessor.getMessage(baseKey + "disconnect.failure.title", command);
        for (DisconnectDeviceState state : DisconnectDeviceState.values()) {
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