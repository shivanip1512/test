package com.cannontech.web.capcontrol;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.capcontrol.CapBankOperationalState;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.dao.CommentAction;
import com.cannontech.cbc.service.CapControlCommentService;
import com.cannontech.cbc.util.CBCDisplay;
import com.cannontech.cbc.util.CBCUtils;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.CapControlDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.CachingPointFormattingService;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.CapControlType;
import com.cannontech.database.db.capcontrol.CapBankAdditional;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.updater.point.PointUpdateBackingService;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.CapControlCommand;
import com.cannontech.yukon.cbc.Feeder;
import com.cannontech.yukon.cbc.SubBus;

@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class OnelinePopupMenuController extends MultiActionController {
    private static final CapBankOperationalState[] allowedOperationStates;
    private CapControlCache capControlCache;
    private CapControlCommentService capControlCommentService;
    private DataSource dataSource;
    private PaoDao paoDao;
    private CapControlDao capControlDao = null;
    
    private CachingPointFormattingService cachingPointFormattingService = null;
    private PointUpdateBackingService pointUpdateBackingService = null;
    
    static {
        allowedOperationStates  = new CapBankOperationalState[] {
                                                                 CapBankOperationalState.Fixed,
                                                                 CapBankOperationalState.StandAlone,
                                                                 CapBankOperationalState.Switched};
    }

    public ModelAndView subTagMenu(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ModelAndView mav = new ModelAndView();
        final Integer id = ServletRequestUtils.getRequiredIntParameter(request, "id");
        final String returnUrl = ServletRequestUtils.getRequiredStringParameter(request, "returnUrl"); 
        final SubBus subBus = capControlCache.getSubBus(id);
        
        mav.addObject("paoId", id);
        mav.addObject("returnUrl", returnUrl);
        
        String paoName = subBus.getCcName();
        mav.addObject("paoName", paoName);
        
        boolean isDisabled = subBus.getCcDisableFlag();
        mav.addObject("isDisabled", isDisabled);
        
        boolean isDisabledOVUV = subBus.getOvUvDisabledFlag();
        mav.addObject("isDisabledOVUV", isDisabledOVUV);
        
        CapControlType type = CapControlType.SUBBUS;

        String disableReason = capControlCommentService.getReason(id, CommentAction.DISABLED, type);
        mav.addObject("disableReason", disableReason);
        
        String disableOVUVReason = capControlCommentService.getReason(id, CommentAction.DISABLED_OVUV, type);
        mav.addObject("disableOVUVReason", disableOVUVReason);
        
        String operationalStateReason = capControlCommentService.getReason(id, CommentAction.STANDALONE_REASON, type);
        mav.addObject("operationalStateReason", operationalStateReason);
        
        List<String> comments = capControlCommentService.getComments(id, 5); 
        mav.addObject("comments", comments);
        
        mav.addObject("isCapBank", false);
        mav.addObject("controlType", type);
        mav.setViewName("oneline/popupmenu/tagMenu.jsp");
        return mav;
    }
    
    public ModelAndView capInfoMenu(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ModelAndView mav = new ModelAndView();
        final Integer id = ServletRequestUtils.getRequiredIntParameter(request, "id");
        final CapBankDevice cap = capControlCache.getCapBankDevice(id);

        String paoName = cap.getCcName();
        mav.addObject("paoName", paoName);
        
        final LiteYukonPAObject lite = paoDao.getLiteYukonPAO(id);
        final CapBankAdditional info = new CapBankAdditional();
        info.setDeviceID(id);
        
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            info.setDbConnection(connection);
            info.retrieve();
        } catch (SQLException e) {
            CTILogger.error(e);
        } finally {
            if (connection != null) connection.close();
        }
        
        Map<String,Object> infoMap = new LinkedHashMap<String,Object>(19);
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
        mav.addObject("infoMap", infoMap);
        
        mav.setViewName("oneline/popupmenu/capInfoMenu.jsp");
        return mav;
    }
    
    public ModelAndView feederTagMenu(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ModelAndView mav = new ModelAndView();
        final Integer id = ServletRequestUtils.getRequiredIntParameter(request, "id");
        final String returnUrl = ServletRequestUtils.getRequiredStringParameter(request, "returnUrl"); 
        final Feeder feeder = capControlCache.getFeeder(id);
        
        mav.addObject("paoId", id);
        mav.addObject("returnUrl", returnUrl);
        
        String paoName = feeder.getCcName();
        mav.addObject("paoName", paoName);
        
        boolean isDisabled = feeder.getCcDisableFlag();
        mav.addObject("isDisabled", isDisabled);
        
        boolean isDisabledOVUV = feeder.getOvUvDisabledFlag();
        mav.addObject("isDisabledOVUV", isDisabledOVUV);
        
        CapControlType type = CapControlType.FEEDER;

        String disableReason = capControlCommentService.getReason(id, CommentAction.DISABLED, type);
        mav.addObject("disableReason", disableReason);
        
        String disableOVUVReason = capControlCommentService.getReason(id, CommentAction.DISABLED_OVUV, type);
        mav.addObject("disableOVUVReason", disableOVUVReason);
        
        List<String> comments = capControlCommentService.getComments(id, 5);
        mav.addObject("comments", comments);
        
        mav.addObject("isCapBank", false);
        mav.addObject("controlType", type);
        mav.setViewName("oneline/popupmenu/tagMenu.jsp");
        return mav;
    }
    
    public ModelAndView capTagMenu(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ModelAndView mav = new ModelAndView();
        final Integer id = ServletRequestUtils.getRequiredIntParameter(request, "id");
        final String returnUrl = ServletRequestUtils.getRequiredStringParameter(request, "returnUrl"); 
        final CapBankDevice capBank = capControlCache.getCapBankDevice(id);
        
        mav.addObject("paoId", id);
        mav.addObject("returnUrl", returnUrl);
        
        String paoName = capBank.getCcName();
        mav.addObject("paoName", paoName);
        
        boolean isDisabled = capBank.getCcDisableFlag();
        mav.addObject("isDisabled", isDisabled);
        
        boolean isDisabledOVUV = capBank.getOvUVDisabled();
        mav.addObject("isDisabledOVUV", isDisabledOVUV);

        CapBankOperationalState operationalState = CapBankOperationalState.valueOf(capBank.getOperationalState());
        mav.addObject("operationalState", operationalState);
        mav.addObject("allowedOperationStates", allowedOperationStates);

        CapControlType type = CapControlType.CAPBANK;
        
        String disableReason = capControlCommentService.getReason(id, CommentAction.DISABLED, type);
        mav.addObject("disableReason", disableReason);
        
        String disableOVUVReason = capControlCommentService.getReason(id, CommentAction.DISABLED_OVUV, type);
        mav.addObject("disableOVUVReason", disableOVUVReason);
        
        String operationalStateReason = capControlCommentService.getReason(id, CommentAction.STANDALONE_REASON, type);
        mav.addObject("operationalStateReason", operationalStateReason);
        
        List<String> comments = capControlCommentService.getComments(id, 5);
        mav.addObject("comments", comments);
        
        mav.addObject("isCapBank", true);
        mav.addObject("controlType", type);
        
        if (capBank.isIgnoreFlag()) {
        	mav.addObject("isIgnoreFlag",true);
        	mav.addObject("refusedReason",CapBankDevice.getIgnoreReason( capBank.getIgnoreReason()));
        }
        
        mav.setViewName("oneline/popupmenu/tagMenu.jsp");
        return mav;
    }
    
    public ModelAndView capBankMenu(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ModelAndView mav = new ModelAndView();
        final Integer id = ServletRequestUtils.getRequiredIntParameter(request, "id");
        
        final CapBankDevice capBank = capControlCache.getCapBankDevice(id);
        final int cbcDeviceId = capBank.getControlDeviceID();
        final LiteYukonPAObject cbcPaoObject = paoDao.getLiteYukonPAO(cbcDeviceId);
        
        mav.addObject("paoId", id);
        
        String paoName = capBank.getCcName();
        mav.addObject("paoName", paoName);
        
        int open = CapControlCommand.OPEN_CAPBANK;
        mav.addObject("open", open);
        
        int close = CapControlCommand.CLOSE_CAPBANK;
        mav.addObject("close", close);
        
        int confirm = CapControlCommand.CONFIRM_OPEN;
        mav.addObject("confirm", confirm);
        
        boolean isTwoWay = CBCUtils.isTwoWay(cbcPaoObject);
        String scanOptionDis = Boolean.toString(!isTwoWay);
        String childCapMaintPaoId = "CapBankMaint_" + id + "_" + scanOptionDis;
        mav.addObject("childCapMaintPaoId", childCapMaintPaoId);
        
        String childCapDBChangePaoId = "CapDBChange_" + id;
        mav.addObject("childCapDBChangePaoId", childCapDBChangePaoId);
        
        mav.addObject("controlType", CapControlType.CAPBANK);
        mav.setViewName("oneline/popupmenu/capBankMenu.jsp");
        return mav;
    }
    
    public ModelAndView feederMenu(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ModelAndView mav = new ModelAndView();
        final Integer id = ServletRequestUtils.getRequiredIntParameter(request, "id");
        final Feeder feeder = capControlCache.getFeeder(id);

        mav.addObject("paoId", id);
        
        String paoName = feeder.getCcName();
        mav.addObject("paoName", paoName);
        
        int resetOpCount = CapControlCommand.RESET_OPCOUNT;
        mav.addObject("resetOpCount", resetOpCount);
        
        int confirmFdr = CapControlCommand.CONFIRM_CLOSE;
        mav.addObject("confirmFdr", confirmFdr);
        
        int openAllFdr = CapControlCommand.SEND_ALL_OPEN;
        mav.addObject("openAllFdr", openAllFdr);
        
        int closeAllFdr = CapControlCommand.SEND_ALL_CLOSE;
        mav.addObject("closeAllFdr", closeAllFdr);
        
        int enableOvUvFdr = CapControlCommand.SEND_ALL_ENABLE_OVUV;
        mav.addObject("enableOvUvFdr", enableOvUvFdr);
        
        int disableOvUvFdr = CapControlCommand.SEND_ALL_DISABLE_OVUV;
        mav.addObject("disableOvUvFdr", disableOvUvFdr);
        
        int sendAll2WayFdr = CapControlCommand.SEND_ALL_SCAN_2WAY;
        mav.addObject("sendAll2WayFdr", sendAll2WayFdr);
        
        int sendTimeSyncFdr = CapControlCommand.SEND_TIMESYNC;
        mav.addObject("sendTimeSyncFdr", sendTimeSyncFdr);
        
        int syncCapBankStatesFdr = CapControlCommand.SYNC_ALL_CAPBANK_STATES;
        mav.addObject("syncCapBankStatesFdr", syncCapBankStatesFdr);
        
        mav.addObject("controlType", CapControlType.FEEDER);
        mav.setViewName("oneline/popupmenu/feederMenu.jsp");
        return mav;
    }
    
    public ModelAndView subMenu(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ModelAndView mav = new ModelAndView();
        final Integer id = ServletRequestUtils.getRequiredIntParameter(request, "id");
        final SubBus subBus = capControlCache.getSubBus(id);
        
        mav.addObject("paoId", id);
        
        String paoName = subBus.getCcName();
        mav.addObject("paoName", paoName);
        
        boolean isV = subBus.getVerificationFlag();
        mav.addObject("isV", isV);
        
        int resetOpCount = CapControlCommand.RESET_OPCOUNT;
        mav.addObject("resetOpCount", resetOpCount);
        
        int confirmSub = CapControlCommand.CONFIRM_CLOSE;
        mav.addObject("confirmSub", confirmSub);
        
        int openAllSub = CapControlCommand.SEND_ALL_OPEN;
        mav.addObject("openAllSub", openAllSub);
        
        int closeAllSub = CapControlCommand.SEND_ALL_CLOSE;
        mav.addObject("closeAllSub", closeAllSub);
        
        int enableOvUvSub = CapControlCommand.SEND_ALL_ENABLE_OVUV;
        mav.addObject("enableOvUvSub", enableOvUvSub);
        
        int disableOvUvSub = CapControlCommand.SEND_ALL_DISABLE_OVUV;
        mav.addObject("disableOvUvSub", disableOvUvSub);
        
        int sendAll2WaySub = CapControlCommand.SEND_ALL_SCAN_2WAY;
        mav.addObject("sendAll2WaySub", sendAll2WaySub);
        
        int sendTimeSyncSub = CapControlCommand.SEND_TIMESYNC;
        mav.addObject("sendTimeSyncSub", sendTimeSyncSub);
        
        int syncCapBankStatesSub = CapControlCommand.SYNC_ALL_CAPBANK_STATES;
        mav.addObject("syncCapBankStatesSub", syncCapBankStatesSub);
        
        int verifyAll = CapControlCommand.CMD_ALL_BANKS;
        mav.addObject("verifyAll", verifyAll);
        
        int verifyFQ = CapControlCommand.CMD_FQ_BANKS;
        mav.addObject("verifyFQ", verifyFQ);
        
        int verifyFailed = CapControlCommand.CMD_FAILED_BANKS;
        mav.addObject("verifyFailed", verifyFailed);
        
        int verifyQuestion = CapControlCommand.CMD_QUESTIONABLE_BANKS;
        mav.addObject("verifyQuestion", verifyQuestion);
        
        int verifyStandalone = CapControlCommand.CMD_STANDALONE_VERIFY;
        mav.addObject("verifyStandalone", verifyStandalone);
        
        int verifyStop = CapControlCommand.CMD_DISABLE_VERIFY;
        mav.addObject("verifyStop", verifyStop);
        
        int verifyEmergencyStop = CapControlCommand.CMD_EMERGENCY_DISABLE_VERIFY;
        mav.addObject("verifyEmergencyStop", verifyEmergencyStop);
        
        mav.addObject("controlType", CapControlType.SUBBUS);
        mav.setViewName("oneline/popupmenu/subMenu.jsp");
        return mav;
    }
    
    public ModelAndView capBankMaint(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ModelAndView mav = new ModelAndView();
        final Integer id = ServletRequestUtils.getRequiredIntParameter(request, "id");
        final CapBankDevice capBank = capControlCache.getCapBankDevice(id);
        
        mav.addObject("paoId", id);
        
        String paoName = capBank.getCcName();
        mav.addObject("paoName", paoName);
        
        LiteYukonPAObject lite = paoDao.getLiteYukonPAO(capBank.getControlDeviceID());
        boolean scanDisabled = !CBCUtils.isTwoWay( lite );
        mav.addObject("scanDisabled", scanDisabled);
        
        int scanCmdId = CapControlCommand.SCAN_2WAY_DEV;
        mav.addObject("scanCmdId", scanCmdId);
        
        int enableOVUVCmdId = CapControlCommand.BANK_ENABLE_OVUV;
        mav.addObject("enableOVUVCmdId", enableOVUVCmdId);
        
        int sendTimeSyncCmdId = CapControlCommand.SEND_TIMESYNC;
        mav.addObject("sendTimeSyncCmdId", sendTimeSyncCmdId);
        
        int syncCapBankState = CapControlCommand.SYNC_CBC_CAPBANK_STATE;
        mav.addObject("syncCapBankState", syncCapBankState);
        
        mav.addObject("controlType", CapControlType.CAPBANK);
        mav.setViewName("oneline/popupmenu/capBankMaint.jsp");
        return mav;
    }

    public ModelAndView capBankDBChange(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ModelAndView mav = new ModelAndView();
        final Integer id = ServletRequestUtils.getRequiredIntParameter(request, "id");
        final CapBankDevice capBank = capControlCache.getCapBankDevice(id);
        
        mav.addObject("paoId", id);
        
        String paoName = capBank.getCcName();
        mav.addObject("paoName", paoName);
        
        int resetOpcount = CapControlCommand.RESET_OPCOUNT;
        mav.addObject("resetOpcount", resetOpcount);

        LiteState[] states = CBCUtils.getCBCStateNames();
        mav.addObject("states", states);
        
        mav.addObject("controlType", CapControlType.CAPBANK);
        mav.setViewName("oneline/popupmenu/capBankDBChange.jsp");
        return mav;
    }
    
    public ModelAndView pointTimestamp(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        ModelAndView mav = new ModelAndView("cbcPointTimestamps.jsp");
        int cbcId = ServletRequestUtils.getRequiredIntParameter(request, "cbcID");
        
        String paoName = paoDao.getYukonPAOName(cbcId);
        mav.addObject("paoName", paoName);
        
        Map<String, List<LitePoint>> pointTimestamps = capControlDao.getSortedCBCPointTimeStamps(cbcId);
        mav.addObject("pointMap", pointTimestamps);
        
        List<LitePoint> pointList = new ArrayList<LitePoint>();
        for(List<LitePoint> list : pointTimestamps.values()) {
        	pointList.addAll(list);
        }
        
        // Get some pre work done to speed things up on the page load.
        cachingPointFormattingService.addLitePointsToCache(pointList);
        pointUpdateBackingService.notifyOfImminentPoints(pointList);
        
        return mav;
    }
    
    public ModelAndView varChangePopup(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ModelAndView mav = new ModelAndView();
        final Integer id = ServletRequestUtils.getRequiredIntParameter(request, "id");
        final String returnUrl = ServletRequestUtils.getRequiredStringParameter(request, "returnUrl"); 
        final CapBankDevice capBank = capControlCache.getCapBankDevice(id);
        
        mav.addObject("paoId", id);
        mav.addObject("returnUrl", returnUrl);
        
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
        
        mav.addObject("beforeRow", beforeRow);
        mav.addObject("afterRow", afterRow);
        mav.addObject("changeRow", changeRow);
        mav.setViewName("oneline/popupmenu/varChangePopup.jsp");
        return mav;
    }
    
    public ModelAndView moveBankBackPopup(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ModelAndView mav = new ModelAndView();
        final Integer id = ServletRequestUtils.getRequiredIntParameter(request, "id");
        final String returnUrl = ServletRequestUtils.getRequiredStringParameter(request, "returnUrl"); 

    	int cmdId = CapControlCommand.RETURN_BANK_TO_FEEDER;
        
        mav.addObject("paoId", id);
        mav.addObject("cmdId", cmdId);
        mav.addObject("returnUrl", returnUrl);
        mav.setViewName("oneline/popupmenu/moveBankBackPopup.jsp");
        return mav;
    }

    public ModelAndView warningInfoPopop(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ModelAndView mav = new ModelAndView();
        final Integer id = ServletRequestUtils.getRequiredIntParameter(request, "id");
        
        List<String> infoList;
        
        /* Handle each type for the warning popup. */
        if( capControlCache.isCapBank(id) ) {
        	infoList = capbankWarning(id);
        } else if(capControlCache.isFeeder(id)) {
        	infoList = feederWarning(id);
        } else {//subbus
        	infoList = subBusWarning(id);
        }
        
        mav.addObject("infoList", infoList);
        
        mav.setViewName("oneline/popupmenu/warningInfoPopup.jsp");
        return mav;
    }

    public ModelAndView moveBankPopup(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ModelAndView mav = new ModelAndView();
        final Integer id = ServletRequestUtils.getRequiredIntParameter(request, "id");       
        mav.addObject("paoId", id);
        mav.setViewName("oneline/popupmenu/moveBankPopup.jsp");
        return mav;
    }

    private List<String> subBusWarning(int id) {
        final SubBus sub = capControlCache.getSubBus(id);
        
        boolean likeDay = sub.getLikeDayControlFlag();
        boolean voltR = sub.getVoltReductionFlag();
        
        List<String> infoList = new ArrayList<String>();

        if (likeDay) {
        	infoList.add(CBCDisplay.WARNING_LIKE_DAY);
        }
        if (voltR) {
        	infoList.add(CBCDisplay.WARNING_VOLT_REDUCTION);
        }

        return infoList;
    }
    
    private List<String> feederWarning(int id) {
        final Feeder feeder = capControlCache.getFeeder(id);
        
        boolean likeDay = feeder.getLikeDayControlFlag();
        
        List<String> infoList = new ArrayList<String>();

        if (likeDay) {
        	infoList.add(CBCDisplay.WARNING_LIKE_DAY);
        }

        return infoList;
    }
    
    private List<String> capbankWarning(int id) {
        final CapBankDevice cap = capControlCache.getCapBankDevice(id);
        
        boolean ovuv = cap.getOvuvSituationFlag();
        boolean maxOp = cap.getMaxDailyOperationHitFlag();
        
        List<String> infoList = new ArrayList<String>();

        if (maxOp) {
        	infoList.add(CBCDisplay.WARNING_MAX_DAILY_OPS);
        }
        if (ovuv) {
        	infoList.add(CBCDisplay.WARNING_OVUV_SITUATION);
        }
        return infoList;
    }
    
    public void setCapControlCache(CapControlCache capControlCache) {
        this.capControlCache = capControlCache;
    }

    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }

    public void setCapControlCommentService(
            CapControlCommentService capControlCommentService) {
        this.capControlCommentService = capControlCommentService;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public void setCapControlDao(CapControlDao capControlDao) {
        this.capControlDao = capControlDao;
    }

	@Autowired
	public void setCachingPointFormattingService(
			CachingPointFormattingService cachingPointFormattingService) {
		this.cachingPointFormattingService = cachingPointFormattingService;
	}
		
	@Autowired
	public void setPointUpdateBackingService(
			PointUpdateBackingService pointUpdateBackingService) {
		this.pointUpdateBackingService = pointUpdateBackingService;
	}
    
}
