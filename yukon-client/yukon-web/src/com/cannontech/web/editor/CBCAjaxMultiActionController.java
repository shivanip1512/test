package com.cannontech.web.editor;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.web.CapControlCommandExecutor;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.LoginController;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.roles.capcontrol.CBCSettingsRole;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.util.ParamUtil;
import com.cannontech.yukon.cbc.CBCCommand;

public class CBCAjaxMultiActionController extends MultiActionController {
    private CapControlCache capControlCache;

    public CBCAjaxMultiActionController() {
    }

    /**
     * creates HTML response to the system command menu
     * @param writer
     */

    public ModelAndView updateSystemCommandMenu(HttpServletRequest req, HttpServletResponse resp) {
        StringBuffer buf = generateHtmlLink(capControlCache);
        String commandLink = buf.toString();
        try {
            PrintWriter writer = resp.getWriter();
            writer.write(commandLink);
            writer.flush();
        } catch (IOException e) {
            CTILogger.error(e);
        }

        return null;
    }

    private StringBuffer generateHtmlLink(CapControlCache cbcCache) {
        StringBuffer buf = new StringBuffer("<a   href='javascript:void(0);' onclick='handleSystemCommand()' ");
        buf.append((cbcCache.getSystemStatusOn()) ? "id='systemOn'>"
                : "id='systemOff'>");
        buf.append((cbcCache.getSystemStatusOn()) ? "Disable System </a>"
                : " Enable System </a>");
        buf.append("<br/>");
        buf.append("<br/>");
        return buf;
    }

    public ModelAndView executeSystemCommand(HttpServletRequest req,
            HttpServletResponse resp) {
        CapControlCache cbcCache = YukonSpringHook.getBean("cbcCache",CapControlCache.class);

        LiteYukonUser user = (LiteYukonUser) req.getSession(false)
                                                .getAttribute(LoginController.YUKON_USER);
        CapControlCommandExecutor executor = new CapControlCommandExecutor(cbcCache, user);
        boolean turnSystemOff = ParamUtil.getBoolean(req, "turnSystemOff");
        int commandID = (turnSystemOff) ? CBCCommand.DISABLE_SYSTEM
                : CBCCommand.ENABLE_SYSTEM;
        if (allowControl(user)) {
            executor.executeCommand(0, commandID);
        } else {
            CTILogger.info("Unable to execute command - " + commandID + ". Check admin settings");
        }
        return null;

    }

    private boolean allowControl(LiteYukonUser user) {
        String allowCtlVal = DaoFactory.getAuthDao()
                                       .getRolePropertyValue(user,
                                                             CBCSettingsRole.ALLOW_CONTROLS);
        boolean allowControl = Boolean.valueOf(allowCtlVal);
        return allowControl;
    }
    
    public void setCapControlCache(CapControlCache capControlCache) {
        this.capControlCache = capControlCache;
    }
}
