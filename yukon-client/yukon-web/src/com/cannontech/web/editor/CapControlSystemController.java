package com.cannontech.web.editor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.web.CapControlCommandExecutor;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.util.JsonView;
import com.cannontech.yukon.cbc.CapControlCommand;

@Controller
@RequestMapping("/systemActions/*")
public class CapControlSystemController {
    
    private static final Logger log = YukonLogManager.getLogger(CapControlSystemController.class);
    private RolePropertyDao rolePropertyDao;
    private CapControlCache capControlCache; 

    @RequestMapping()
    public View resetOpCount(HttpServletRequest request, HttpServletResponse response, ModelMap model, LiteYukonUser user) {
        executeSystemCommand(request, model, CapControlCommand.RESET_ALL_OPCOUNTS, user);
        return new JsonView();
    }
    @RequestMapping()
    public View toggleSystemStatus(HttpServletRequest request, HttpServletResponse response, ModelMap model, boolean turnSystemOff, LiteYukonUser user) {
        int commandId = turnSystemOff ? CapControlCommand.DISABLE_SYSTEM : CapControlCommand.ENABLE_SYSTEM;
        executeSystemCommand(request, model, commandId, user);   
        return new JsonView();
    }
    
    private void executeSystemCommand(HttpServletRequest request, ModelMap model, int commandId, LiteYukonUser user) {
        CapControlCommandExecutor executor = new CapControlCommandExecutor(capControlCache, user);
        
        if (rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.SYSTEM_WIDE_CONTROLS, user)) {
            executor.executeCommand(0, commandId);
            model.addAttribute("success", true);
        } else {
            log.info("User not authorized to execute command - " + commandId);
            model.addAttribute("success", false);
        }
    }

    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
    
    @Autowired
    public void setCapControlCache(CapControlCache capControlCache) {
        this.capControlCache = capControlCache;
    }
    
}