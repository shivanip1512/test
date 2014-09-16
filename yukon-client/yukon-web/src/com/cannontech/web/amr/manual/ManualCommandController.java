package com.cannontech.web.amr.manual;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.core.dao.CommandDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteCommand;
import com.cannontech.database.data.lite.LiteDeviceTypeCommand;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.yc.bean.YCBean;

/**
 * Spring controller class
 */
@CheckRoleProperty(YukonRoleProperty.ENABLE_WEB_COMMANDER)
public class ManualCommandController extends MultiActionController {

    @Autowired private PaoDao paoDao;
    @Autowired private MeterDao meterDao;
    @Autowired private CommandDao commandDao;
    
    public ModelAndView home(HttpServletRequest request, HttpServletResponse response) throws ServletException {

        ModelAndView mav = new ModelAndView("manualCommand.jsp");

        int deviceId = ServletRequestUtils.getRequiredIntParameter(request,
                "deviceId");
        YukonMeter meter = meterDao.getForId(deviceId);
        mav.addObject("deviceId", deviceId);
        mav.addObject("deviceName", meter.getName());

        // Get or create the YCBean and put it into the session
        YCBean ycBean = (YCBean) request.getSession().getAttribute("YC_BEAN");
        if (ycBean == null) {
            ycBean = new YCBean();
        }
        request.getSession().setAttribute("YC_BEAN", ycBean);

        LiteYukonUser user = ServletUtil.getYukonUser(request);
        ycBean.setUserID(user.getUserID());
        ycBean.setLiteYukonPao(deviceId);
        ycBean.setDeviceType(deviceId);
        
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        ycBean.setUserContext(userContext);

        LiteYukonPAObject device = paoDao.getLiteYukonPAO(deviceId);
        mav.addObject("device", device);
        mav.addObject("deviceType", ycBean.getDeviceType());

        // Get the list of commands
        List<LiteDeviceTypeCommand> commands = ycBean.getLiteDeviceTypeCommands();
        List<LiteCommand> commandList = new ArrayList<LiteCommand>();
        for (LiteDeviceTypeCommand ldtc : commands) {
            LiteCommand command = commandDao.getCommand(ldtc.getCommandId());
            if (ldtc.isVisible() && ycBean.isAllowCommand(command.getCommand(), user, device)) {
                commandList.add(command);
            }
        }
        mav.addObject("commandList", commandList);
        
        String errorMsg = ServletRequestUtils.getStringParameter(request, "errorMsg");
        if (errorMsg == null && ycBean.getErrorMsg() != null) {
            errorMsg = ycBean.getErrorMsg();
            ycBean.setErrorMsg("");
        }
        
        mav.addObject("errorMsg", errorMsg);
        
        return mav;
    }
    
}