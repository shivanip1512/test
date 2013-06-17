package com.cannontech.web.capcontrol;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.capcontrol.BankOpState;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.dao.CommentAction;
import com.cannontech.cbc.service.CapControlCommentService;
import com.cannontech.cbc.util.CapControlUtils;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.dao.CapControlDao;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.CachingPointFormattingService;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.CapControlType;
import com.cannontech.database.db.capcontrol.CapBankAdditional;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.message.capcontrol.model.CommandType;
import com.cannontech.message.capcontrol.streamable.CapBankDevice;
import com.cannontech.message.capcontrol.streamable.Feeder;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.updater.point.PointUpdateBackingService;

@Controller("/oneline/popupmenu/*")
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class OnelinePopupMenuController {
    
    private static final BankOpState[] allowedOperationStates;
    private CapControlCache cache;
    private CapControlCommentService capControlCommentService;
    private PaoDao paoDao;
    private DBPersistentDao dbPersistentDao;
    private CapControlDao capControlDao = null;
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    private CachingPointFormattingService cachingPointFormattingService = null;
    private PointUpdateBackingService pointUpdateBackingService = null;
    
    static {
        allowedOperationStates  = new BankOpState[] {
                                                                 BankOpState.FIXED,
                                                                 BankOpState.STANDALONE,
                                                                 BankOpState.SWITCHED};
    }

    @RequestMapping
    public String subTagMenu(ModelMap model, int id) {
        CapControlType type = CapControlType.SUBBUS;
        final SubBus subBus = cache.getSubBus(id);
        
        model.addAttribute("paoId", id);
        
        String paoName = subBus.getCcName();
        model.addAttribute("paoName", paoName);
        
        model.addAttribute("isDisabled", subBus.getCcDisableFlag());
        model.addAttribute("disableCommandId", CommandType.DISABLE_SUBSTATION_BUS.getCommandId());
        model.addAttribute("enableCommandId", CommandType.ENABLE_SUBSTATION_BUS.getCommandId());
        String disableReason = capControlCommentService.getReason(id, CommentAction.DISABLED, type);
        model.addAttribute("disableReason", disableReason);
        
        model.addAttribute("isDisabledOvUv", subBus.getOvUvDisabledFlag());
        model.addAttribute("disableOvUvCommandId", CommandType.SEND_DISABLE_OVUV.getCommandId());
        model.addAttribute("enableOvUvCommandId", CommandType.SEND_ENABLE_OVUV.getCommandId());
        String disableOvUvReason = capControlCommentService.getReason(id, CommentAction.DISABLED_OVUV, type);
        model.addAttribute("disableOvUvReason", disableOvUvReason);
        
        List<String> comments = capControlCommentService.getComments(id, 5); 
        model.addAttribute("comments", comments);
        
        model.addAttribute("isCapBank", false);
        model.addAttribute("controlType", type);
        
        return "oneline/popupmenu/tagMenu.jsp";
    }
    
    @RequestMapping
    public String capInfoMenu(ModelMap model, int id) {
        CapBankDevice cap = cache.getCapBankDevice(id);
        model.addAttribute("paoName", cap.getCcName());
        
        LiteYukonPAObject lite = paoDao.getLiteYukonPAO(id);
        CapBankAdditional info = new CapBankAdditional();
        info.setDeviceID(id);
        dbPersistentDao.retrieveDBPersistent(info);
        
        Map<String,Object> infoMap = new LinkedHashMap<String, Object>(19);
        infoMap.put("Maintenance Area ID:", info.getMaintAreaID());
        infoMap.put("Pole Number:", info.getPoleNumber());
        infoMap.put("Latitude:", info.getLatit());
        infoMap.put("Longitude:", info.getLongtit());
        infoMap.put("Cap Bank Config:", info.getCapBankConfig());
        infoMap.put("Comm. Medium", info.getCommMedium());
        infoMap.put("Comm.Strength", info.getCommStrengh());
        infoMap.put("External Antenna?", info.getExtAntenna());
        infoMap.put("Antenna Type:", info.getAntennaType());
        infoMap.put("Last Maintenance Visit:", info.getLastInspVisit());
        infoMap.put("Last Inspection Visit:", info.getLastInspVisit());
        infoMap.put("Op Count Reset Date:", info.getOpCountResetDate());
        infoMap.put("Potential Transformer:", info.getPotentTransformer());
        infoMap.put("Maintenance Request Pending:", info.getMaintReqPending());
        infoMap.put("Other Comments:", info.getOtherComments());
        infoMap.put("Op Team Comments:", info.getOpTeamComments());
        infoMap.put("CBC Install Date:", info.getCbcBattInstallDate());
        infoMap.put("Cap Bank Map Address", lite.getPaoDescription());
        infoMap.put("Driving Directions:", info.getDriveDir()); 
        model.addAttribute("infoMap", infoMap);
        
        return "oneline/popupmenu/capInfoMenu.jsp";
    }
    
    @RequestMapping
    public String feederTagMenu(ModelMap model, int id) {
        CapControlType type = CapControlType.FEEDER;
        Feeder feeder = cache.getFeeder(id);
        
        model.addAttribute("paoId", id);
        
        String paoName = feeder.getCcName();
        model.addAttribute("paoName", paoName);
        
        boolean isDisabled = feeder.getCcDisableFlag();
        model.addAttribute("isDisabled", isDisabled);
        model.addAttribute("disableCommandId", CommandType.DISABLE_FEEDER.getCommandId());
        model.addAttribute("enableCommandId", CommandType.ENABLE_FEEDER.getCommandId());
        String disableReason = capControlCommentService.getReason(id, CommentAction.DISABLED, type);
        model.addAttribute("disableReason", disableReason);
        
        boolean isDisabledOvUv = feeder.getOvUvDisabledFlag();
        model.addAttribute("isDisabledOvUv", isDisabledOvUv);
        model.addAttribute("disableOvUvCommandId", CommandType.SEND_DISABLE_OVUV.getCommandId());
        model.addAttribute("enableOvUvCommandId", CommandType.SEND_ENABLE_OVUV.getCommandId());
        String disableOvUvReason = capControlCommentService.getReason(id, CommentAction.DISABLED_OVUV, type);
        model.addAttribute("disableOvUvReason", disableOvUvReason);
        
        List<String> comments = capControlCommentService.getComments(id, 5);
        model.addAttribute("comments", comments);
        
        model.addAttribute("isCapBank", false);
        model.addAttribute("controlType", type);
        
        return "oneline/popupmenu/tagMenu.jsp";
    }
    
    @RequestMapping
    public String capTagMenu(ModelMap model, int id) {
        CapControlType type = CapControlType.CAPBANK;
        CapBankDevice capBank = cache.getCapBankDevice(id);
        
        model.addAttribute("paoId", id);
        
        String paoName = capBank.getCcName();
        model.addAttribute("paoName", paoName);
        
        boolean isDisabled = capBank.getCcDisableFlag();
        model.addAttribute("isDisabled", isDisabled);
        model.addAttribute("disableCommandId", CommandType.DISABLE_CAPBANK.getCommandId());
        model.addAttribute("enableCommandId", CommandType.ENABLE_CAPBANK.getCommandId());
        String disableReason = capControlCommentService.getReason(id, CommentAction.DISABLED, type);
        model.addAttribute("disableReason", disableReason);
        
        boolean isDisabledOvUv = capBank.getOvUVDisabled();
        model.addAttribute("isDisabledOvUv", isDisabledOvUv);
        model.addAttribute("disableOvUvCommandId", CommandType.SEND_DISABLE_OVUV.getCommandId());
        model.addAttribute("enableOvUvCommandId", CommandType.SEND_ENABLE_OVUV.getCommandId());
        String disableOvUvReason = capControlCommentService.getReason(id, CommentAction.DISABLED_OVUV, type);
        model.addAttribute("disableOvUvReason", disableOvUvReason);

        BankOpState operationalState = BankOpState.getStateByName(capBank.getOperationalState());
        model.addAttribute("operationalState", operationalState);
        model.addAttribute("allowedOperationStates", allowedOperationStates);
        String operationalStateReason = capControlCommentService.getReason(id, CommentAction.STANDALONE_REASON, type);
        model.addAttribute("operationalStateReason", operationalStateReason);
        
        List<String> comments = capControlCommentService.getComments(id, 5);
        model.addAttribute("comments", comments);
        
        model.addAttribute("isCapBank", true);
        model.addAttribute("controlType", type);
        
        if (capBank.isIgnoreFlag()) {
        	model.addAttribute("isIgnoreFlag",true);
        	model.addAttribute("refusedReason",CapBankDevice.getIgnoreReason( capBank.getIgnoreReason()));
        }
        
        return "oneline/popupmenu/tagMenu.jsp";
    }
    
    @RequestMapping
    public String capBankMenu(ModelMap model, int id) {
        model.addAttribute("controlType", CapControlType.CAPBANK);
        
        CapBankDevice capBank = cache.getCapBankDevice(id);
        int cbcDeviceId = capBank.getControlDeviceID();
        LiteYukonPAObject cbcPaoObject = paoDao.getLiteYukonPAO(cbcDeviceId);
        
        model.addAttribute("paoId", id);
        
        String paoName = capBank.getCcName();
        model.addAttribute("paoName", paoName);
        
        int open = CommandType.SEND_OPEN_CAPBANK.getCommandId();
        model.addAttribute("open", open);
        
        int close = CommandType.SEND_CLOSE_CAPBANK.getCommandId();
        model.addAttribute("close", close);
        
        int confirm = CommandType.CONFIRM_OPEN.getCommandId();
        model.addAttribute("confirm", confirm);
        
        boolean isTwoWay = CapControlUtils.isTwoWay(cbcPaoObject);
        String scanOptionDis = Boolean.toString(!isTwoWay);
        String childCapMaintPaoId = "CapBankMaint_" + id + "_" + scanOptionDis;
        model.addAttribute("childCapMaintPaoId", childCapMaintPaoId);
        
        String childCapDBChangePaoId = "CapDBChange_" + id;
        model.addAttribute("childCapDBChangePaoId", childCapDBChangePaoId);
        
        return "oneline/popupmenu/capBankMenu.jsp";
    }
    
    @RequestMapping
    public String feederMenu(ModelMap model, int id) {
        Feeder feeder = cache.getFeeder(id);

        model.addAttribute("paoId", id);
        
        String paoName = feeder.getCcName();
        model.addAttribute("paoName", paoName);
        
        int resetOpCount = CommandType.RESET_DAILY_OPERATIONS.getCommandId();
        model.addAttribute("resetOpCount", resetOpCount);
        
        int confirmFdr = CommandType.CONFIRM_CLOSE.getCommandId();
        model.addAttribute("confirmFdr", confirmFdr);
        
        int openAllFdr = CommandType.SEND_OPEN_CAPBANK.getCommandId();
        model.addAttribute("openAllFdr", openAllFdr);
        
        int closeAllFdr = CommandType.SEND_CLOSE_CAPBANK.getCommandId();
        model.addAttribute("closeAllFdr", closeAllFdr);
        
        int enableOvUvFdr = CommandType.SEND_ENABLE_OVUV.getCommandId();
        model.addAttribute("enableOvUvFdr", enableOvUvFdr);
        
        int disableOvUvFdr = CommandType.SEND_DISABLE_OVUV.getCommandId();
        model.addAttribute("disableOvUvFdr", disableOvUvFdr);
        
        int sendAll2WayFdr = CommandType.SEND_SCAN_2WAY_DEVICE.getCommandId();
        model.addAttribute("sendAll2WayFdr", sendAll2WayFdr);
        
        int sendTimeSyncFdr = CommandType.SEND_TIME_SYNC.getCommandId();
        model.addAttribute("sendTimeSyncFdr", sendTimeSyncFdr);
        
        int syncCapBankStatesFdr = CommandType.SEND_SYNC_CBC_CAPBANK_STATE.getCommandId();
        model.addAttribute("syncCapBankStatesFdr", syncCapBankStatesFdr);
        
        model.addAttribute("controlType", CapControlType.FEEDER);
        
        return "oneline/popupmenu/feederMenu.jsp";
    }
    
    @RequestMapping
    public String subMenu(ModelMap model, int id) {
        SubBus subBus = cache.getSubBus(id);
        
        model.addAttribute("paoId", id);
        
        String paoName = subBus.getCcName();
        model.addAttribute("paoName", paoName);
        
        boolean isV = subBus.getVerificationFlag();
        model.addAttribute("isV", isV);
        
        int resetOpCount = CommandType.RESET_DAILY_OPERATIONS.getCommandId();
        model.addAttribute("resetOpCount", resetOpCount);
        
        int confirmSub = CommandType.CONFIRM_CLOSE.getCommandId();
        model.addAttribute("confirmSub", confirmSub);
        
        int openAllSub = CommandType.SEND_OPEN_CAPBANK.getCommandId();
        model.addAttribute("openAllSub", openAllSub);
        
        int closeAllSub = CommandType.SEND_CLOSE_CAPBANK.getCommandId();
        model.addAttribute("closeAllSub", closeAllSub);
        
        int enableOvUvSub = CommandType.SEND_ENABLE_OVUV.getCommandId();
        model.addAttribute("enableOvUvSub", enableOvUvSub);
        
        int disableOvUvSub = CommandType.SEND_DISABLE_OVUV.getCommandId();
        model.addAttribute("disableOvUvSub", disableOvUvSub);
        
        int sendAll2WaySub = CommandType.SEND_SCAN_2WAY_DEVICE.getCommandId();
        model.addAttribute("sendAll2WaySub", sendAll2WaySub);
        
        int sendTimeSyncSub = CommandType.SEND_TIME_SYNC.getCommandId();
        model.addAttribute("sendTimeSyncSub", sendTimeSyncSub);
        
        int syncCapBankStatesSub = CommandType.SEND_SYNC_CBC_CAPBANK_STATE.getCommandId();
        model.addAttribute("syncCapBankStatesSub", syncCapBankStatesSub);
        
        int verifyAll = CommandType.VERIFY_ALL_BANKS.getCommandId();
        model.addAttribute("verifyAll", verifyAll);
        
        int verifyFQ = CommandType.VERIFY_FQ_BANKS.getCommandId();
        model.addAttribute("verifyFQ", verifyFQ);
        
        int verifyFailed = CommandType.VERIFY_FAILED_BANKS.getCommandId();
        model.addAttribute("verifyFailed", verifyFailed);
        
        int verifyQuestion = CommandType.VERIFY_Q_BANKS.getCommandId();
        model.addAttribute("verifyQuestion", verifyQuestion);
        
        int verifyStandalone = CommandType.VERIFY_SA_BANKS.getCommandId();
        model.addAttribute("verifyStandalone", verifyStandalone);
        
        int verifyStop = CommandType.STOP_VERIFICATION.getCommandId();
        model.addAttribute("verifyStop", verifyStop);
        
        int verifyEmergencyStop = CommandType.EMERGENCY_VERIFICATION_STOP.getCommandId();
        model.addAttribute("verifyEmergencyStop", verifyEmergencyStop);
        
        model.addAttribute("controlType", CapControlType.SUBBUS);
        
        return "oneline/popupmenu/subMenu.jsp";
    }
    
    @RequestMapping
    public String capBankMaint(ModelMap model, int id) {
        CapBankDevice capBank = cache.getCapBankDevice(id);
        
        model.addAttribute("paoId", id);
        
        String paoName = capBank.getCcName();
        model.addAttribute("paoName", paoName);
        
        LiteYukonPAObject lite = paoDao.getLiteYukonPAO(capBank.getControlDeviceID());
        boolean scanDisabled = !CapControlUtils.isTwoWay( lite );
        model.addAttribute("scanDisabled", scanDisabled);
        
        int scanCmdId = CommandType.SEND_SCAN_2WAY_DEVICE.getCommandId();
        model.addAttribute("scanCmdId", scanCmdId);
        
        int enableOvUvCmdId = CommandType.SEND_ENABLE_OVUV.getCommandId();
        model.addAttribute("enableOvUvCmdId", enableOvUvCmdId);
        
        int sendTimeSyncCmdId = CommandType.SEND_TIME_SYNC.getCommandId();
        model.addAttribute("sendTimeSyncCmdId", sendTimeSyncCmdId);
        
        int syncCapBankState = CommandType.SEND_SYNC_CBC_CAPBANK_STATE.getCommandId();
        model.addAttribute("syncCapBankState", syncCapBankState);
        
        model.addAttribute("controlType", CapControlType.CAPBANK);
        return "oneline/popupmenu/capBankMaint.jsp";
    }

    @RequestMapping
    public String capBankDBChange(ModelMap model, int id) {
        CapBankDevice capBank = cache.getCapBankDevice(id);
        
        model.addAttribute("paoId", id);
        
        String paoName = capBank.getCcName();
        model.addAttribute("paoName", paoName);
        
        int resetOpcount = CommandType.RESET_DAILY_OPERATIONS.getCommandId();
        model.addAttribute("resetOpcount", resetOpcount);

        LiteState[] states = CapControlUtils.getCBCStateNames();
        model.addAttribute("states", states);
        
        model.addAttribute("controlType", CapControlType.CAPBANK);
        return "oneline/popupmenu/capBankDBChange.jsp";
    }
    
    @RequestMapping
    public String pointTimestamp(ModelMap model, int cbcID) {
        String paoName = paoDao.getYukonPAOName(cbcID);
        model.addAttribute("paoName", paoName);
        
        Map<String, List<LitePoint>> pointTimestamps = capControlDao.getSortedCBCPointTimeStamps(cbcID);
        model.addAttribute("pointMap", pointTimestamps);
        
        List<LitePoint> pointList = new ArrayList<LitePoint>();
        for(List<LitePoint> list : pointTimestamps.values()) {
        	pointList.addAll(list);
        }
        
        // Get some pre work done to speed things up on the page load.
        cachingPointFormattingService.addLitePointsToCache(pointList);
        pointUpdateBackingService.notifyOfImminentPoints(pointList);
        
        return "oneline/cbcPoints.jsp";
    }
    
    @RequestMapping
    public String varChangePopup(ModelMap model, int id) {
        final CapBankDevice capBank = cache.getCapBankDevice(id);
        model.addAttribute("paoId", id);
        
        String before = capBank.getBeforeVars().trim();
        String beforeRow = "";
        String after = capBank.getAfterVars().trim();
        String afterRow = "";
        String change = capBank.getPercentChange().trim();
        String changeRow = "";
        if(before.contains(":")) {
            StringTokenizer st = new StringTokenizer(before, ":");
            String phaseA = st.nextToken();
            String phaseB = "";
            String phaseC = "";
            String total = "";
            if(st.hasMoreTokens()) {
                phaseB = st.nextToken();
                phaseC = st.nextToken();
                total = st.nextToken();
                beforeRow = "<td nowrap align='left' style='color:white; font-size: 14;'>Before:</td><td nowrap align='left' style='color:white; font-size: 14;'>" 
                    + phaseA + "</td><td nowrap align='left' style='color:white; font-size: 14;'>" 
                    + phaseB + "</td><td nowrap align='left' style='color:white; font-size: 14;'>" 
                    + phaseC + "</td><td nowrap align='left' style='color:white; font-size: 14;'>" 
                    + total + "</td>";
            }else {
                beforeRow = "<td nowrap align='left' style='color:white; font-size: 14;'>Before:</td><td nowrap colspan='4' align='left' style='color:white; font-size: 14;'>" + phaseA + "</td>";
            }
        }else {
            beforeRow = "<td nowrap align='left' style='color:white; font-size: 14;'>Before:</td><td nowrap colspan='4' align='left' style='color:white; font-size: 14;'>" + before + "</td>";
        }
        
        if(after.contains(":")) {
            StringTokenizer st = new StringTokenizer(after, ":");
            String phaseA = st.nextToken();
            String phaseB = "";
            String phaseC = "";
            String total = "";
            if(st.hasMoreTokens()) {
                phaseB = st.nextToken();
                phaseC = st.nextToken();
                total = st.nextToken();
                afterRow = "<td nowrap align='left' style='color:white; font-size: 14;'>After:</td><td nowrap align='left' style='color:white; font-size: 14;'>" 
                    + phaseA + "</td><td nowrap align='left' style='color:white; font-size: 14;'>" 
                    + phaseB + "</td><td nowrap align='left' style='color:white; font-size: 14;'>" 
                    + phaseC + "</td><td nowrap align='left' style='color:white; font-size: 14;'>" 
                    + total + "</td>";
            }else {
                afterRow = "<td nowrap align='left' style='color:white; font-size: 14;'>After:</td><td nowrap colspan='4' align='left' style='color:white; font-size: 14;'>" + phaseA + "</td>";
            }
        }else {
            afterRow = "<td nowrap align='left' style='color:white; font-size: 14;'>After:</td><td nowrap colspan='4' align='left' style='color:white; font-size: 14;'>" + after + "</td>";
        }
        
        if(change.contains(":")) {
            StringTokenizer st = new StringTokenizer(change, ":");
            String phaseA = st.nextToken();
            String phaseB = "";
            String phaseC = "";
            String total = "";
            if(st.hasMoreTokens()) {
                phaseB = st.nextToken();
                phaseC = st.nextToken();
                total = st.nextToken();
                changeRow = "<td nowrap align='left' style='color:white; font-size: 14;'>% Change:</td><td nowrap align='left' style='color:white; font-size: 14;'>" 
                    + phaseA + "</td><td nowrap align='left' style='color:white; font-size: 14;'>" 
                    + phaseB + "</td><td nowrap align='left' style='color:white; font-size: 14;'>" 
                    + phaseC + "</td><td nowrap align='left' style='color:white; font-size: 14;'>" 
                    + total + "</td>";
            }else {
                changeRow = "<td nowrap align='left' style='color:white; font-size: 14;'>% Change:</td><td colspan='4' align='left' style='color:white; font-size: 14;'>" + phaseA + "</td>";
            }
        }else {
            changeRow = "<td nowrap align='left' style='color:white; font-size: 14;'>% Change:</td><td colspan='4' align='left' style='color:white; font-size: 14;'>" + change + "</td>";
        }
        
        model.addAttribute("beforeRow", beforeRow);
        model.addAttribute("afterRow", afterRow);
        model.addAttribute("changeRow", changeRow);
        
        return "oneline/popupmenu/varChangePopup.jsp";
    }
    
    @RequestMapping
    public String moveBankBackPopup(ModelMap model, int id) {
    	int cmdId = CommandType.RETURN_CAP_TO_ORIGINAL_FEEDER.getCommandId();
        model.addAttribute("paoId", id);
        model.addAttribute("cmdId", cmdId);
        return "oneline/popupmenu/moveBankBackPopup.jsp";
    }

    @RequestMapping
    public String warningInfoPopop(ModelMap model, YukonUserContext context, int id) {
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(context);
        
        List<String> infoList;
        
        /* Handle each type for the warning popup. */
        if( cache.isCapBank(id) ) {
        	infoList = capbankWarning(id, messageSourceAccessor);
        } else if(cache.isFeeder(id)) {
        	infoList = feederWarning(id, messageSourceAccessor);
        } else {//subbus
        	infoList = subBusWarning(id, messageSourceAccessor);
        }
        
        model.addAttribute("infoList", infoList);
        
        return "oneline/popupmenu/warningInfoPopup.jsp";
    }

    @RequestMapping
    public String moveBankPopup(ModelMap model, YukonUserContext context, int id) {
        model.addAttribute("paoId", id);
        return "oneline/popupmenu/moveBankPopup.jsp";
    }

    private List<String> subBusWarning(int id, MessageSourceAccessor accessor) {
        final SubBus sub = cache.getSubBus(id);
        
        boolean likeDay = sub.getLikeDayControlFlag();
        boolean voltR = sub.getVoltReductionFlag();
        
        List<String> infoList = new ArrayList<String>();

        if (likeDay) {
        	infoList.add(accessor.getMessage("yukon.web.modules.capcontrol.likeDay"));
        }
        if (voltR) {
        	infoList.add(accessor.getMessage("yukon.web.modules.capcontrol.voltReduction"));
        }

        return infoList;
    }
    
    private List<String> feederWarning(int id, MessageSourceAccessor accessor) {
        final Feeder feeder = cache.getFeeder(id);
        
        boolean likeDay = feeder.getLikeDayControlFlag();
        
        List<String> infoList = new ArrayList<String>();

        if (likeDay) {
        	infoList.add(accessor.getMessage("yukon.web.modules.capcontrol.likeDay"));
        }

        return infoList;
    }
    
    private List<String> capbankWarning(int id, MessageSourceAccessor accessor) {
        final CapBankDevice cap = cache.getCapBankDevice(id);
        
        boolean ovuv = cap.getOvuvSituationFlag();
        boolean maxOp = cap.getMaxDailyOperationHitFlag();
        
        List<String> infoList = new ArrayList<String>();

        if (maxOp) {
        	infoList.add(accessor.getMessage("yukon.web.modules.capcontrol.maxDailyOps"));
        }
        if (ovuv) {
        	infoList.add(accessor.getMessage("yukon.web.modules.capcontrol.ovuvSituation"));
        }
        return infoList;
    }
    
    @Autowired
    public void setCapControlCache(CapControlCache capControlCache) {
        this.cache = capControlCache;
    }

    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }

    @Autowired
    public void setCapControlCommentService(CapControlCommentService capControlCommentService) {
        this.capControlCommentService = capControlCommentService;
    }

    @Autowired
    public void setCapControlDao(CapControlDao capControlDao) {
        this.capControlDao = capControlDao;
    }

	@Autowired
	public void setCachingPointFormattingService(CachingPointFormattingService cachingPointFormattingService) {
		this.cachingPointFormattingService = cachingPointFormattingService;
	}
		
	@Autowired
	public void setPointUpdateBackingService(
			PointUpdateBackingService pointUpdateBackingService) {
		this.pointUpdateBackingService = pointUpdateBackingService;
	}
    
	@Autowired
	public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
	
	@Autowired
	public void setDbPersistentDao(DBPersistentDao dbPersistentDao) {
        this.dbPersistentDao = dbPersistentDao;
    }
	
}