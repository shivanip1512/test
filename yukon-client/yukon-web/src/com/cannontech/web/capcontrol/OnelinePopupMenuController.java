package com.cannontech.web.capcontrol;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.capcontrol.CapBankOperationalState;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.dao.CommentAction;
import com.cannontech.cbc.service.CapControlCommentService;
import com.cannontech.cbc.util.CBCUtils;
import com.cannontech.cbc.web.CapControlType;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.CapControlDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.CBCPointTimestampParams;
import com.cannontech.database.db.capcontrol.CapBankAdditional;
import com.cannontech.yukon.cbc.CapControlCommand;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.Feeder;
import com.cannontech.yukon.cbc.SubBus;

public class OnelinePopupMenuController extends MultiActionController {
    private static final CapBankOperationalState[] allowedOperationStates;
    private CapControlCache capControlCache;
    private CapControlCommentService capControlCommentService;
    private DataSource dataSource;
    private PaoDao paoDao;
    private CapControlDao cbcDao = null;
    
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
        mav.setViewName("oneline/popupmenu/tagMenu");
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
        
        mav.setViewName("oneline/popupmenu/capInfoMenu");
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
        mav.setViewName("oneline/popupmenu/tagMenu");
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
        mav.setViewName("oneline/popupmenu/tagMenu");
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
        mav.setViewName("oneline/popupmenu/capBankMenu");
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
        
        int openAllFdr = CapControlCommand.SEND_ALL_OPEN;
        mav.addObject("openAllFdr", openAllFdr);
        
        int closeAllFdr = CapControlCommand.SEND_ALL_CLOSE;
        mav.addObject("closeAllFdr", closeAllFdr);
        
        int enableOvUvFdr = CapControlCommand.SEND_ALL_ENABLE_OVUV;
        mav.addObject("enableOvUvFdr", enableOvUvFdr);
        
        int disableOvUvFdr = CapControlCommand.SEND_ALL_DISABLE_OVUV;
        mav.addObject("disableOvUvFdr", disableOvUvFdr);
        
        int sendAll2WayFdr = CapControlCommand.SCAN_2WAY_DEV;
        mav.addObject("sendAll2WayFdr", sendAll2WayFdr);
        
        int sendTimeSyncFdr = CapControlCommand.SEND_TIMESYNC;
        mav.addObject("sendTimeSyncFdr", sendTimeSyncFdr);
        
        mav.addObject("controlType", CapControlType.FEEDER);
        mav.setViewName("oneline/popupmenu/feederMenu");
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
        
        int sendAll2WaySub = CapControlCommand.SCAN_2WAY_DEV;
        mav.addObject("sendAll2WaySub", sendAll2WaySub);
        
        int sendTimeSyncSub = CapControlCommand.SEND_TIMESYNC;
        mav.addObject("sendTimeSyncSub", sendTimeSyncSub);
        
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
        
        mav.addObject("controlType", CapControlType.SUBBUS);
        mav.setViewName("oneline/popupmenu/subMenu");
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
        
        mav.addObject("controlType", CapControlType.CAPBANK);
        mav.setViewName("oneline/popupmenu/capBankMaint");
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
        mav.setViewName("oneline/popupmenu/capBankDBChange");
        return mav;
    }
    
    public ModelAndView pointTimestamp(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        ModelAndView mav = new ModelAndView("cbcPointTimestamps");
        int cbcId = ServletRequestUtils.getRequiredIntParameter(request, "cbcID");
        
        LiteYukonPAObject cbc = paoDao.getLiteYukonPAO(cbcId);
        
        String paoName = cbc.getPaoName();
        mav.addObject("paoName", paoName);
        
        Map<String, List<CBCPointTimestampParams>> pointTimestamps = cbcDao.getSortedCBCPointTimeStamps(cbcId);
        mav.addObject("pointMap", pointTimestamps);
        
        // Hack to change jsp content for oneline popup
        boolean isOneline = ServletRequestUtils.getBooleanParameter(request, "oneline", false);
        mav.addObject("isOneline", isOneline);

        return mav;
    }
    
    public ModelAndView varChangePopup(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ModelAndView mav = new ModelAndView();
        final Integer id = ServletRequestUtils.getRequiredIntParameter(request, "id");
        final String returnUrl = ServletRequestUtils.getRequiredStringParameter(request, "returnUrl"); 
        final CapBankDevice capBank = capControlCache.getCapBankDevice(id);
        
        mav.addObject("paoId", id);
        mav.addObject("returnUrl", returnUrl);
        Map<String,Object> varChangeMap = new LinkedHashMap<String,Object>(3);
        varChangeMap.put("Before:", capBank.getBeforeVars());
        varChangeMap.put("After:", capBank.getAfterVars());
        varChangeMap.put("% Change:", capBank.getPercentChange());
        mav.addObject("varChangeMap", varChangeMap);
        mav.setViewName("oneline/popupmenu/varChangePopup");
        return mav;
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
    
    public void setCbcDao(CapControlDao cbcDao) {
        this.cbcDao = cbcDao;
    }
    
}
