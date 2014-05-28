package com.cannontech.analysis.tablemodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.disconnect.model.DisconnectDeviceState;
import com.cannontech.amr.disconnect.model.DisconnectResult;
import com.cannontech.amr.disconnect.service.DisconnectService;
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

public class DisconnectSuccessResultsModel extends BareReportModelBase<DisconnectSuccessResultsModel.ModelRow>
        implements ReportModelMetaInfo {

    @Autowired private PaoDao paoDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private DisconnectService disconnectService;
    @Autowired private DateFormattingService dateFormattingService;
    
    private String resultKey;
    private String state;
    private String title;
    private List<ModelRow> data = new ArrayList<ModelRow>();
    private Map<DisconnectDeviceState, String> states = new HashMap<>();
        
    static public class ModelRow {
        public String deviceName;
        public String state;
        public String timestamp;
    }
    
    @Override
    public void doLoadData() {
        DateTimeFormatter dateTimeFormatter =
                dateFormattingService.getDateTimeFormatter(DateFormatEnum.DATEHM, YukonUserContext.system);
        DisconnectResult result = disconnectService.getResult(getResultKey());
        List<SimpleDevice> devices = null;
        DisconnectDeviceState currentState = DisconnectDeviceState.valueOf(getState());
        if (currentState == DisconnectDeviceState.CONNECTED) {
            devices = result.getConnectedCollection().getDeviceList();
        } else if (currentState == DisconnectDeviceState.DISCONNECTED) {
            devices = result.getDisconnectedCollection().getDeviceList();
        } else if (currentState == DisconnectDeviceState.ARMED) {
            devices = result.getArmedCollection().getDeviceList();
        } else if (currentState == DisconnectDeviceState.UNSUPPORTED) {
            devices = result.getUnsupportedCollection().getDeviceList();
        } else if (currentState == DisconnectDeviceState.NOT_CONFIGURED) {
            devices = result.getNotConfiguredCollection().getDeviceList();
        }else if (currentState == DisconnectDeviceState.CANCELED) {
            devices = result.getCanceledCollection().getDeviceList();
        }
        String state = states.get(currentState);
        Map<PaoIdentifier, LiteYukonPAObject> paoIdentifiers =
                paoDao.getLiteYukonPaosById(Lists.transform(devices, SimpleDevice.TO_PAO_IDENTIFIER));
        
        for (LiteYukonPAObject device : paoIdentifiers.values()) {
            ModelRow row = new ModelRow();
            row.deviceName = device.getPaoName();
            row.state = state;
            Instant timestamp = result.getTimestamp(new SimpleDevice(device.getLiteID(), device.getPaoType()));
            if (timestamp != null) {
                row.timestamp = dateTimeFormatter.print(timestamp);
            }
            data.add(row);
        }
    };
    
    @Override
    public LinkedHashMap<String, String> getMetaInfo(final YukonUserContext userContext) {
        DisconnectResult result =disconnectService.getResult(getResultKey());
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        LinkedHashMap<String, String> info = new LinkedHashMap<String, String>();
        String command = accessor.getMessage(result.getCommand());
        title = accessor.getMessage(baseKey + "disconnect.success.title", command);
        for (DisconnectDeviceState state : DisconnectDeviceState.values()) {
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