package com.cannontech.web.cbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
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
import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.CBCDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.CapControlTypes;
import com.cannontech.database.data.point.CBCPointTimestampParams;
import com.cannontech.database.db.capcontrol.CapBankAdditional;
import com.cannontech.yukon.cbc.CBCCommand;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.CapControlConst;
import com.cannontech.yukon.cbc.Feeder;
import com.cannontech.yukon.cbc.SubBus;

public class OnelinePopupMenuController extends MultiActionController {
    private static final CapBankOperationalState[] allowedOperationStates;
    private CapControlCache capControlCache;
    private CapControlCommentService capControlCommentService;
    private DataSource dataSource;
    private PaoDao paoDao;
    private CBCDao cbcDao = null;
    
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

        String disableReason = capControlCommentService.getReason(id, CommentAction.DISABLED, CapControlTypes.CAP_CONTROL_SUBBUS);
        mav.addObject("disableReason", disableReason);
        
        String disableOVUVReason = capControlCommentService.getReason(id, CommentAction.DISABLED_OVUV, CapControlTypes.CAP_CONTROL_SUBBUS);
        mav.addObject("disableOVUVReason", disableOVUVReason);
        
        String operationalStateReason = capControlCommentService.getReason(id, CommentAction.STANDALONE_REASON, CapControlTypes.CAP_CONTROL_SUBBUS);
        mav.addObject("operationalStateReason", operationalStateReason);
        
        List<String> comments = capControlCommentService.getComments(id, 5); 
        mav.addObject("comments", comments);
        
        mav.addObject("isCapBank", false);
        mav.addObject("controlType", "SUB_TYPE");
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
        
        Map<String,Object> infoMap = new HashMap<String,Object>();
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

        String disableReason = capControlCommentService.getReason(id, CommentAction.DISABLED, CapControlTypes.CAP_CONTROL_FEEDER);
        mav.addObject("disableReason", disableReason);
        
        String disableOVUVReason = capControlCommentService.getReason(id, CommentAction.DISABLED_OVUV, CapControlTypes.CAP_CONTROL_FEEDER);
        mav.addObject("disableOVUVReason", disableOVUVReason);
        
        List<String> comments = capControlCommentService.getComments(id, 5);
        mav.addObject("comments", comments);
        
        mav.addObject("isCapBank", false);
        mav.addObject("controlType", "FEEDER_TYPE");
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

        String disableReason = capControlCommentService.getReason(id, CommentAction.DISABLED, CapControlTypes.CAP_CONTROL_CAPBANK);
        mav.addObject("disableReason", disableReason);
        
        String disableOVUVReason = capControlCommentService.getReason(id, CommentAction.DISABLED_OVUV, CapControlTypes.CAP_CONTROL_CAPBANK);
        mav.addObject("disableOVUVReason", disableOVUVReason);
        
        String operationalStateReason = capControlCommentService.getReason(id, CommentAction.STANDALONE_REASON, CapControlTypes.CAP_CONTROL_CAPBANK);
        mav.addObject("operationalStateReason", operationalStateReason);
        
        List<String> comments = capControlCommentService.getComments(id, 5);
        mav.addObject("comments", comments);
        
        mav.addObject("isCapBank", true);
        mav.addObject("controlType", CapControlConst.CMD_TYPE_CAPBANK);
        mav.setViewName("oneline/popupmenu/tagMenu");
        return mav;
    }
    
    public ModelAndView capBankMenu(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ModelAndView mav = new ModelAndView();
        final Integer id = ServletRequestUtils.getRequiredIntParameter(request, "id");
        final CapBankDevice capBank = capControlCache.getCapBankDevice(id);
        
        mav.addObject("paoId", id);
        
        String paoName = capBank.getCcName();
        mav.addObject("paoName", paoName);
        
        int open = CBCCommand.OPEN_CAPBANK;
        mav.addObject("open", open);
        
        int close = CBCCommand.CLOSE_CAPBANK;
        mav.addObject("close", close);
        
        int confirm = CBCCommand.CONFIRM_OPEN;
        mav.addObject("confirm", confirm);
        
        LiteYukonPAObject lite = paoDao.getLiteYukonPAO(id);
        boolean isTwoWay = CBCUtils.isTwoWay(lite);
        String scanOptionDis = Boolean.toString(!isTwoWay);
        String childCapMaintPaoId = "CapBankMaint_" + id + "_" + scanOptionDis;
        mav.addObject("childCapMaintPaoId", childCapMaintPaoId);
        
        String childCapDBChangePaoId = "CapDBChange_" + id;
        mav.addObject("childCapDBChangePaoId", childCapDBChangePaoId);
        
        mav.addObject("controlType", CapControlConst.CMD_TYPE_CAPBANK);
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
        
        int resetOpCount = CBCCommand.RESET_OPCOUNT;
        mav.addObject("resetOpCount", resetOpCount);
        
        int openAllFdr = CBCCommand.SEND_ALL_OPEN;
        mav.addObject("openAllFdr", openAllFdr);
        
        int closeAllFdr = CBCCommand.SEND_ALL_CLOSE;
        mav.addObject("closeAllFdr", closeAllFdr);
        
        int enableOvUvFdr = CBCCommand.SEND_ALL_ENABLE_OVUV;
        mav.addObject("enableOvUvFdr", enableOvUvFdr);
        
        int disableOvUvFdr = CBCCommand.SEND_ALL_DISABLE_OVUV;
        mav.addObject("disableOvUvFdr", disableOvUvFdr);
        
        int sendAll2WayFdr = CBCCommand.SCAN_2WAY_DEV;
        mav.addObject("sendAll2WayFdr", sendAll2WayFdr);
        
        int sendTimeSyncFdr = CBCCommand.SEND_TIMESYNC;
        mav.addObject("sendTimeSyncFdr", sendTimeSyncFdr);
        
        mav.addObject("controlType", CapControlConst.CMD_TYPE_FEEDER);
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
        
        int resetOpCount = CBCCommand.RESET_OPCOUNT;
        mav.addObject("resetOpCount", resetOpCount);
        
        int confirmSub = CBCCommand.CONFIRM_CLOSE;
        mav.addObject("confirmSub", confirmSub);
        
        int openAllSub = CBCCommand.SEND_ALL_OPEN;
        mav.addObject("openAllSub", openAllSub);
        
        int closeAllSub = CBCCommand.SEND_ALL_CLOSE;
        mav.addObject("closeAllSub", closeAllSub);
        
        int enableOvUvSub = CBCCommand.SEND_ALL_ENABLE_OVUV;
        mav.addObject("enableOvUvSub", enableOvUvSub);
        
        int disableOvUvSub = CBCCommand.SEND_ALL_DISABLE_OVUV;
        mav.addObject("disableOvUvSub", disableOvUvSub);
        
        int sendAll2WaySub = CBCCommand.SCAN_2WAY_DEV;
        mav.addObject("sendAll2WaySub", sendAll2WaySub);
        
        int sendTimeSyncSub = CBCCommand.SEND_TIMESYNC;
        mav.addObject("sendTimeSyncSub", sendTimeSyncSub);
        
        int verifyAll = CBCCommand.CMD_ALL_BANKS;
        mav.addObject("verifyAll", verifyAll);
        
        int verifyFQ = CBCCommand.CMD_FQ_BANKS;
        mav.addObject("verifyFQ", verifyFQ);
        
        int verifyFailed = CBCCommand.CMD_FAILED_BANKS;
        mav.addObject("verifyFailed", verifyFailed);
        
        int verifyQuestion = CBCCommand.CMD_QUESTIONABLE_BANKS;
        mav.addObject("verifyQuestion", verifyQuestion);
        
        int verifyStandalone = CBCCommand.CMD_STANDALONE_VERIFY;
        mav.addObject("verifyStandalone", verifyStandalone);
        
        int verifyStop = CBCCommand.CMD_DISABLE_VERIFY;
        mav.addObject("verifyStop", verifyStop);
        
        mav.addObject("controlType", CapControlConst.CMD_TYPE_SUB);
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
        
        int scanCmdId = CBCCommand.SCAN_2WAY_DEV;
        mav.addObject("scanCmdId", scanCmdId);
        
        int enableOVUVCmdId = CBCCommand.BANK_ENABLE_OVUV;
        mav.addObject("enableOVUVCmdId", enableOVUVCmdId);
        
        int sendTimeSyncCmdId = CBCCommand.SEND_TIMESYNC;
        mav.addObject("sendTimeSyncCmdId", sendTimeSyncCmdId);
        
        mav.addObject("controlType", CapControlConst.CMD_TYPE_CAPBANK);
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
        
        int resetOpcount = CBCCommand.RESET_OPCOUNT;
        mav.addObject("resetOpcount", resetOpcount);
        
        LiteState[] states = CBCUtils.getCBCStateNames();
        mav.addObject("states", states);
        
        mav.addObject("controlType", CapControlConst.CMD_TYPE_CAPBANK);
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
    
    public void setCbcDao(CBCDao cbcDao) {
        this.cbcDao = cbcDao;
    }
    
}
