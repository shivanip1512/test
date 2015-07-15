package com.cannontech.web.util;

import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.faces.model.SelectItem;

import com.cannontech.capcontrol.service.CbcHelperService;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.Phase;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.capcontrol.CapBank;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.database.data.point.AnalogControlType;
import com.cannontech.database.data.point.PointArchiveType;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.point.StatusControlType;
import com.cannontech.database.db.point.PointAlarming;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Maps;

public class CBCSelectionLists {

    /*
     * Constants for indexing the tabbed pane
     */
    public static final int General = 0;
    public static final int CapControlSubBusSetup = 1;
    public static final int CapControlSubstationSetup = 2;
    public static final int CapControlAreaSetup = 3;
    public static final int CapControlSpecialAreaSetup = 4;
    public static final int CapControlSubSchedSetup = 5;
    public static final int CapControlFeederSetup = 6;
    public static final int CapControlStrategySetup = 7;
    public static final int CapControlChildList = 8;
    public static final int CapBankSetup = 9;
    public static final int CapBankControllerSetup = 10;
    public static final int DualBus = 11;
    public static final int CapBankAdvanced = 12;
    public static final int CapBankAdditionalInfo = 13;
    public static final int AreaSubs = 14;
    public static final int SpecialAreaSubs = 15;

    /* MyFaces 1.10 does not seem to show the correct time with h:outPutText, */
    /* so we only show date for now within our pages */
    private static final String dateOnly = "MM-dd-yyyy";
    private static final String dateTime = "MM-dd-yyyy HH:mm:ss";
    private static final String dateTimeNoSeconds = "MM-dd-yyyy HH:mm";

    private LiteYukonUser yukonUser;
    private AuthDao authDao;
    private CbcHelperService cbcHelperService;
    private YukonUserContextMessageSourceResolver messageSourceResolver;

    private static final SelectItem[] pTypes = {
        new SelectItem(PointTypes.ANALOG_POINT, "Analog"),
        new SelectItem(PointTypes.STATUS_POINT, "Status"),
            /*
             * In case we need to add functionality for demand accumulator to the existing code the following
             * lines need to be un-commented
             */
            /* new SelectItem(new Integer (PointTypes.DEMAND_ACCUMULATOR_POINT), "Demand Accumulator") */
        new SelectItem(PointTypes.PULSE_ACCUMULATOR_POINT, "Accumulator"),
        new SelectItem(PointTypes.CALCULATED_POINT, "Calculated Analog Output"),
        new SelectItem(PointTypes.CALCULATED_STATUS_POINT, "Calculated Status Output")
    };

    private static final SelectItem[] typeList701X = {
        new SelectItem(PaoType.CBC_EXPRESSCOM.getDeviceTypeId(), PaoType.CBC_EXPRESSCOM.getDbString()),
        new SelectItem(PaoType.CAPBANKCONTROLLER.getDeviceTypeId(), PaoType.CAPBANKCONTROLLER.getDbString()),
        new SelectItem(PaoType.CBC_7010.getDeviceTypeId(), PaoType.CBC_7010.getDbString()),
        new SelectItem(PaoType.CBC_7011.getDeviceTypeId(), PaoType.CBC_7011.getDbString()),
        new SelectItem(PaoType.CBC_7012.getDeviceTypeId(), PaoType.CBC_7012.getDbString()),
        new SelectItem(PaoType.CBC_FP_2800.getDeviceTypeId(), PaoType.CBC_FP_2800.getDbString())
    };

    private static final SelectItem[] typeList702X = {
        new SelectItem(PaoType.CBC_7020.getDeviceTypeId(), PaoType.CBC_7020.getDbString()),
        new SelectItem(PaoType.CBC_7022.getDeviceTypeId(), PaoType.CBC_7022.getDbString()),
        new SelectItem(PaoType.CBC_7023.getDeviceTypeId(), PaoType.CBC_7023.getDbString()),
        new SelectItem(PaoType.CBC_7024.getDeviceTypeId(), PaoType.CBC_7024.getDbString())
    };

    private static final SelectItem[] typeList802X = {
        new SelectItem(PaoType.CBC_8020.getDeviceTypeId(), PaoType.CBC_8020.getDbString()),
        new SelectItem(PaoType.CBC_8024.getDeviceTypeId(), PaoType.CBC_8024.getDbString())
    };

    private static final SelectItem[] typeListDNP = {
        new SelectItem(PaoType.CBC_DNP.getDeviceTypeId(), PaoType.CBC_DNP.getDbString()) };

    private static final SelectItem[] wizardCBCTypes = {
        // value, label
        new SelectItem(PaoType.CBC_EXPRESSCOM.getDeviceTypeId(), PaoType.CBC_EXPRESSCOM.getDbString()),
        new SelectItem(PaoType.CAPBANKCONTROLLER.getDeviceTypeId(), PaoType.CAPBANKCONTROLLER.getDbString()),
        new SelectItem(PaoType.CBC_7010.getDeviceTypeId(), PaoType.CBC_7010.getDbString()),
        new SelectItem(PaoType.CBC_7011.getDeviceTypeId(), PaoType.CBC_7011.getDbString()),
        new SelectItem(PaoType.CBC_7012.getDeviceTypeId(), PaoType.CBC_7012.getDbString()),
        new SelectItem(PaoType.CBC_7020.getDeviceTypeId(), PaoType.CBC_7020.getDbString()),
        new SelectItem(PaoType.CBC_7022.getDeviceTypeId(), PaoType.CBC_7022.getDbString()),
        new SelectItem(PaoType.CBC_7023.getDeviceTypeId(), PaoType.CBC_7023.getDbString()),
        new SelectItem(PaoType.CBC_7024.getDeviceTypeId(), PaoType.CBC_7024.getDbString()),
        new SelectItem(PaoType.CBC_8020.getDeviceTypeId(), PaoType.CBC_8020.getDbString()),
        new SelectItem(PaoType.CBC_8024.getDeviceTypeId(), PaoType.CBC_8024.getDbString()),
        new SelectItem(PaoType.CBC_DNP.getDeviceTypeId(), PaoType.CBC_DNP.getDbString()),
        new SelectItem(PaoType.CBC_FP_2800.getDeviceTypeId(), PaoType.CBC_FP_2800.getDbString())
    };

    private static final SelectItem[] wizardCBCPointTypes = {
        // value, label
        new SelectItem(PointTypes.ANALOG_POINT, PointTypes.getType(PointTypes.ANALOG_POINT)),
        new SelectItem(PointTypes.STATUS_POINT, PointTypes.getType(PointTypes.STATUS_POINT)) 
    };

    final private SelectItem[] controllerTypes =
        JSFUtil.convertSelectionListByName(YukonSelectionListDefs.YUK_LIST_ID_CONTROLLER_TYPE);
    final private SelectItem[] switchManufacturers =
        JSFUtil.convertSelectionListByName(YukonSelectionListDefs.YUK_LIST_ID_SWITCH_MANUFACTURER);
    final private SelectItem[] switchTypes =
        JSFUtil.convertSelectionListByName(YukonSelectionListDefs.YUK_LIST_ID_SWITCH_TYPE);

    private static final SelectItem[] scheduleCmds = {
        // value, label
        new SelectItem("(none)", "Please Select A Command"),
        new SelectItem("Verify ALL CapBanks", "Verify ALL CapBanks"),
        new SelectItem("Verify Failed CapBanks", "Verify Failed CapBanks"),
        new SelectItem("Verify Failed and Questionable CapBanks", "Verify Failed and Questionable..."),
        new SelectItem("Verify Standalone CapBanks", "Verify Standalone CapBanks"),
        new SelectItem("Verify Questionable CapBanks", "Verify Questionable CapBanks"),
        new SelectItem("Verify CapBanks that have not operated in 0 min 0 hr 0 day 0 wk", "Verify Cap Banks Not operated in..."), 
        new SelectItem("Confirm Sub", "Confirm Sub"),
        new SelectItem("Send Time Syncs", "Send Time Syncs") };

    private static SelectItem[] capBankOpStates = { new SelectItem(CapBank.FIXED_OPSTATE, CapBank.FIXED_OPSTATE),
        new SelectItem(CapBank.STANDALONE_OPSTATE, CapBank.STANDALONE_OPSTATE),
        new SelectItem(CapBank.SWITCHED_OPSTATE, CapBank.SWITCHED_OPSTATE),
        new SelectItem(CapBank.UNINSTALLED_OPSTATE, CapBank.UNINSTALLED_OPSTATE) };

    private static final SelectItem[] capBankSizes = { 
        new SelectItem(50, "50 kVar"),
        new SelectItem(100, "100 kVar"),
        new SelectItem(150, "120 kVar"),
        new SelectItem(200, "200 kVar"),
        new SelectItem(275, "275 kVar"),
        new SelectItem(300, "300 kVar"),
        new SelectItem(450, "450 kVar"),
        new SelectItem(550, "550 kVar"),
        new SelectItem(600, "600 kVar"),
        new SelectItem(825, "825 kVar"),
        new SelectItem(900, "900 kVar"),
        new SelectItem(1100, "1100 kVar"),
        new SelectItem(1200, "1200 kVar")
    };

    public static final Map<Integer, String> intervalGroupDisplayValues = Maps.newHashMap();
    static {
        intervalGroupDisplayValues.put(0, "Default");
        intervalGroupDisplayValues.put(1, "First");
        intervalGroupDisplayValues.put(2, "Second");
    }

    public static final Map<Integer, String> timeIntervalDisplayValues = Maps.newHashMap();
    static {
        timeIntervalDisplayValues.put(1, "1 second");
        timeIntervalDisplayValues.put(2, "2 seconds");
        timeIntervalDisplayValues.put(5, "5 seconds");
        timeIntervalDisplayValues.put(10, "10 seconds");
        timeIntervalDisplayValues.put(15, "15 seconds");
        timeIntervalDisplayValues.put(30, "30 seconds");
        timeIntervalDisplayValues.put(60, "1 minute");
        timeIntervalDisplayValues.put(120, "2 minutes");
        timeIntervalDisplayValues.put(180, "3 minutes");
        timeIntervalDisplayValues.put(240, "4 minutes");
        timeIntervalDisplayValues.put(300, "5 minutes");
        timeIntervalDisplayValues.put(420, "7 minutes");
        timeIntervalDisplayValues.put(600, "10 minutes");
        timeIntervalDisplayValues.put(720, "12 minutes");
        timeIntervalDisplayValues.put(900, "15 minutes");
        timeIntervalDisplayValues.put(1200, "20 minutes");
        timeIntervalDisplayValues.put(1500, "25 minutes");
        timeIntervalDisplayValues.put(1800, "30 minutes");
        timeIntervalDisplayValues.put(3600, "1 hour");
        timeIntervalDisplayValues.put(7200, "2 hours");
        timeIntervalDisplayValues.put(21600, "6 hours");
        timeIntervalDisplayValues.put(43200, "12 hours");
        timeIntervalDisplayValues.put(86400, "1 day");
    }

    // generic time list in seconds for a many fields
    public static final SelectItem[] TIME_INTERVAL = {
        // value, label
        new SelectItem(1, "1 second"),
        new SelectItem(2, "2 seconds"),
        new SelectItem(5, "5 seconds"),
        new SelectItem(10, "10 seconds"),
        new SelectItem(15, "15 seconds"),
        new SelectItem(30, "30 seconds"),
        new SelectItem(60, "1 minute"),
        new SelectItem(120, "2 minutes"),
        new SelectItem(180, "3 minutes"),
        new SelectItem(240, "4 minutes"),
        new SelectItem(300, "5 minutes"),
        new SelectItem(420, "7 minutes"),
        new SelectItem(600, "10 minutes"),
        new SelectItem(720, "12 minutes"),
        new SelectItem(900, "15 minutes"),
        new SelectItem(1200, "20 minutes"),
        new SelectItem(1500, "25 minutes"),
        new SelectItem(1800, "30 minutes"),
        new SelectItem(3600, "1 hour"),
        new SelectItem(7200, "2 hours"),
        new SelectItem(21600, "6 hours"),
        new SelectItem(43200, "12 hours"),
        new SelectItem(86400, "1 day") };

    private static final SelectItem[] ptArchiveType = {
        new SelectItem(PointArchiveType.NONE.getPointArchiveTypeName(), PointArchiveType.NONE.getDisplayName()),
        new SelectItem(PointArchiveType.ON_CHANGE.getPointArchiveTypeName(), PointArchiveType.ON_CHANGE.getDisplayName()),
        new SelectItem(PointArchiveType.ON_TIMER.getPointArchiveTypeName(), PointArchiveType.ON_TIMER.getDisplayName()),
        new SelectItem(PointArchiveType.ON_UPDATE.getPointArchiveTypeName(), PointArchiveType.ON_UPDATE.getDisplayName()),
        new SelectItem(PointArchiveType.ON_TIMER_OR_UPDATE.getPointArchiveTypeName(), PointArchiveType.ON_TIMER_OR_UPDATE.getDisplayName())
    };

    private static final SelectItem[] ptUpdateType = {
        new SelectItem(PointTypes.UPDATE_ALL_CHANGE, PointTypes.UPDATE_ALL_CHANGE),
        new SelectItem(PointTypes.UPDATE_FIRST_CHANGE, PointTypes.UPDATE_FIRST_CHANGE),
        new SelectItem(PointTypes.UPDATE_HISTORICAL, PointTypes.UPDATE_HISTORICAL),
        new SelectItem(PointTypes.UPDATE_TIMER, PointTypes.UPDATE_TIMER),
        new SelectItem(PointTypes.UPDATE_TIMER_CHANGE, PointTypes.UPDATE_TIMER_CHANGE)
    };

    private static final SelectItem[] ptAlarmNotification = {
        new SelectItem(PointAlarming.NONE_VALUE_STRING, PointAlarming.NONE_VALUE_STRING),
        new SelectItem(PointAlarming.EXCLUDE_NOTIFY_VALUE_STRING, PointAlarming.EXCLUDE_NOTIFY_VALUE_STRING),
        new SelectItem(PointAlarming.AUTO_ACK_VALUE_STRING, PointAlarming.AUTO_ACK_VALUE_STRING),
        new SelectItem(PointAlarming.BOTH_OPTIONS_VALUE_STRING, PointAlarming.BOTH_OPTIONS_VALUE_STRING)
    };

    private static final SelectItem[] ptStatusControlTypes = {
        // value, label
        new SelectItem(StatusControlType.NONE.getControlName(), StatusControlType.NONE.getControlName()),
        new SelectItem(StatusControlType.LATCH.getControlName(), StatusControlType.LATCH.getControlName()),
        new SelectItem(StatusControlType.NORMAL.getControlName(), StatusControlType.NORMAL.getControlName()),
        new SelectItem(StatusControlType.PSEUDO.getControlName(), StatusControlType.PSEUDO.getControlName()),
        new SelectItem(StatusControlType.SBOLATCH.getControlName(), StatusControlType.SBOLATCH.getControlName()),
        new SelectItem(StatusControlType.SBOPULSE.getControlName(), StatusControlType.SBOPULSE.getControlName())
    };

    private static final SelectItem[] ptAnalogControlTypes = {
        // value, label
        new SelectItem(AnalogControlType.NONE.getControlName(), AnalogControlType.NONE.getControlName()),
        new SelectItem(AnalogControlType.NORMAL.getControlName(), AnalogControlType.NORMAL.getControlName())
    };

    private static YukonUserContext userContext;

    private static YukonUserContext getYukonUserContext() {
        if (userContext == null) {
            userContext = JSFUtil.getYukonUserContext();
        }
        return userContext;
    }

    /**
     * Returns all possible Comm Channels
     */
    public SelectItem[] getCommChannels() {

        SelectItem[] selItems = new SelectItem[0];

        IDatabaseCache cache = DefaultDatabaseCache.getInstance();
        synchronized (cache) {
            List<LiteYukonPAObject> ports = cache.getAllPorts();

            selItems = new SelectItem[ports.size()];
            for (int i = 0; i < ports.size(); i++) {
                LiteYukonPAObject litePort = ports.get(i);
                selItems[i] = new SelectItem(new Integer(litePort.getYukonID()), litePort.getPaoName());
            }

        }

        return selItems;
    }

    /**
     * Returns the valid start of time for Yukon
     * 
     * @return
     */
    public long getStartOfTime() {

        return CtiUtilities.get1990GregCalendar().getTime().getTime();
    }

    /**
     * Returns all possible Routes
     */
    public SelectItem[] getRoutes() {

        SelectItem[] selItems = new SelectItem[0];

        IDatabaseCache cache = DefaultDatabaseCache.getInstance();
        synchronized (cache) {
            List<LiteYukonPAObject> routes = cache.getAllRoutes();

            selItems = new SelectItem[routes.size()];
            for (int i = 0; i < routes.size(); i++) {
                LiteYukonPAObject liteRoute = routes.get(i);
                selItems[i] = new SelectItem(new Integer(liteRoute.getYukonID()), liteRoute.getPaoName());
            }
        }

        return selItems;
    }

    public SelectItem[] getCBCTypes() {
        return wizardCBCTypes;
    }

    public SelectItem[] getCBCPointTypes() {
        return wizardCBCPointTypes;
    }

    public SelectItem[] getCapBankOpStates() {
        String fixedText = cbcHelperService.getFixedText(yukonUser);
        capBankOpStates[0] = new SelectItem(fixedText, fixedText);
        return capBankOpStates;
    }

    public SelectItem[] getCapBankSizes() {
        return capBankSizes;
    }

    public SelectItem[] getScheduleCmds() {
        return scheduleCmds;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getDateTimeNoSeconds() {
        return dateTimeNoSeconds;
    }

    public String getDateOnly() {
        return dateOnly;
    }

    /**
     * Returns a sublist of Time Interval SelectItem[]
     */
    public static SelectItem[] getTimeSubList(int startSecs, int endSecs) {

        if (startSecs >= endSecs)
            return CBCSelectionLists.TIME_INTERVAL;

        int startIndx = 0, endIndx = CBCSelectionLists.TIME_INTERVAL.length;

        for (int i = 0; i < CBCSelectionLists.TIME_INTERVAL.length; i++) {

            int secsVal = ((Integer) CBCSelectionLists.TIME_INTERVAL[i].getValue()).intValue();

            if (secsVal >= startSecs && startIndx <= 0)
                startIndx = i;

            if (secsVal >= endSecs) {
                endIndx = i;
                break;
            }
        }

        SelectItem[] items = new SelectItem[endIndx - startIndx];
        System.arraycopy(CBCSelectionLists.TIME_INTERVAL, startIndx, items, 0, items.length);
        return items;
    }

    /**
     * Returns a sublist of Time Interval SelectItem[].
     * Starts at the given startSecs value and returns the entire upper list
     */
    public static SelectItem[] getTimeSubList(int startSecs) {
        return getTimeSubList(startSecs, Integer.MAX_VALUE);
    }

    public SelectItem[] getPtArchiveType() {
        return ptArchiveType;
    }

    public SelectItem[] getPtUpdateType() {
        return ptUpdateType;
    }

    public SelectItem[] getPtAlarmNotification() {
        return ptAlarmNotification;
    }

    public SelectItem[] getPtStatusControlTypes() {
        return ptStatusControlTypes;
    }

    public SelectItem[] getPtAnalogControlTypes() {
        return ptAnalogControlTypes;
    }

    public SelectItem[] getPointTypes() {
        return pTypes;
    }

    public SelectItem[] getTypeList701X() {
        return typeList701X;
    }

    public SelectItem[] getTypeList702X() {
        return typeList702X;
    }

    public SelectItem[] getTypeList802X() {
        return typeList802X;
    }

    public SelectItem[] getTypeListDNP() {
        return typeListDNP;
    }

    public SelectItem[] getTimeInterval() {

        return TIME_INTERVAL;
    }

    public Integer getSubstationType() {
        return PaoType.CAP_CONTROL_SUBSTATION.getDeviceTypeId();
    }

    public Integer getSubstationBusType() {
        return PaoType.CAP_CONTROL_SUBBUS.getDeviceTypeId();
    }

    public Integer getFeederType() {
        return PaoType.CAP_CONTROL_FEEDER.getDeviceTypeId();
    }

    public Integer getCapType() {
        return DeviceTypes.CAPBANK;
    }

    public SelectItem[] getCapBankConfigs() {
        return new SelectItem[] {
            new SelectItem("None"),
            new SelectItem("Wye"),
            new SelectItem("Delta"),
            new SelectItem("Serial") 
        };
    }

    public SelectItem[] getCapBankCommMedium() {
        return new SelectItem[] {
            new SelectItem("None"),
            new SelectItem("Paging"),
            new SelectItem("DLC"),
            new SelectItem("VHF"),
            new SelectItem("1XRTT"),
            new SelectItem("GPRS"),
            new SelectItem("SSRadio") 
        };
    }

    public SelectItem[] getCapBankAntennaType() {
        return new SelectItem[] {
            new SelectItem("None"),
            new SelectItem("Yagi"),
            new SelectItem("Omni")
        };
    }

    public SelectItem[] getPotentialTransformer() {
        return new SelectItem[] {
            new SelectItem("None"),
            new SelectItem("Dedicated"),
            new SelectItem("Dedicated-A"),
            new SelectItem("Dedicated-B"),
            new SelectItem("Dedicated-C"),
            new SelectItem("Shared")
        };
    }

    public SelectItem[] getAddCapBankSizes() {
        return new SelectItem[] {
            new SelectItem(100, "100"),
            new SelectItem(200, "200"),
            new SelectItem(300, "300"),
            new SelectItem(600, "600"),
            new SelectItem(900, "900"),
            new SelectItem(1200, "1200"),
            new SelectItem(1800, "1800"),
            new SelectItem(2400, "2400")
        };
    }

    public SelectItem[] getControllerTypes() {
        return controllerTypes;
    }

    public SelectItem[] getSwitchManufacturers() {
        return switchManufacturers;
    }

    public SelectItem[] getPhases() {
        
        YukonUserContext yukonUserContext = getYukonUserContext();
        
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(yukonUserContext);
        String phaseAString = messageSourceAccessor.getMessage(Phase.A);
        String phaseBString = messageSourceAccessor.getMessage(Phase.B);
        String phaseCString = messageSourceAccessor.getMessage(Phase.C);
        SelectItem[] phases = {
            new SelectItem(Phase.A.name(), phaseAString),
            new SelectItem(Phase.B.name(), phaseBString),
            new SelectItem(Phase.C.name(), phaseCString)
        };
        return phases;
    }

    public SelectItem[] getSwitchTypes() {
        return switchTypes;
    }

    public TimeZone getTimeZone() {
        return authDao.getUserTimeZone(getYukonUser());
    }

    public LiteYukonUser getYukonUser() {
        return yukonUser;
    }

    public void setYukonUser(LiteYukonUser yukonUser) {
        this.yukonUser = yukonUser;
    }

    public void setAuthDao(AuthDao authDao) {
        this.authDao = authDao;
    }

    public void setCbcHelperService(CbcHelperService cbcHelperService) {
        this.cbcHelperService = cbcHelperService;
    }

    public void setYukonUserContextMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
}