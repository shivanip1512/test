package com.cannontech.web.capcontrol;

import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.View;

import com.cannontech.core.dao.StrategyDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.DBEditorTypes;
import com.cannontech.database.db.capcontrol.CapControlStrategy;
import com.cannontech.web.editor.CapControlForm;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.util.JsonView;


@Controller
@RequestMapping("/strategy/*")
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class StrategyController {
    
    private RolePropertyDao rolePropertyDao = null;
    private StrategyDao strategyDao = null;
    
    @RequestMapping
    public String scheduleAssignments(HttpServletRequest request, LiteYukonUser user, ModelMap mav) {
        List<CapControlStrategy> strategies = strategyDao.getAllStrategies();
        mav.addAttribute("strategies", strategies);
        
        boolean hasEditingRole = rolePropertyDao.checkProperty(YukonRoleProperty.CBC_DATABASE_EDIT, user);
        mav.addAttribute("hasEditingRole", hasEditingRole);
        
        return "strategy/strategies.jsp";
    }
    
    @RequestMapping(method=RequestMethod.POST)
    public View deleteStrategy(HttpServletRequest request, Integer strategyId, ModelMap map) throws ServletException, Exception {
        boolean success = true;
        String resultString = "Strategy deleted successfully.";
        if( strategyId == null) {
            success = false;
            resultString = "Delete failed, strategyId was NULL";
        } else {
            CapControlForm capControlForm = (CapControlForm) request.getSession().getAttribute("capControlForm");
            capControlForm.initItem(strategyId, DBEditorTypes.EDITOR_STRATEGY);
            success = capControlForm.deleteStrategy();
            if (!success) {
                resultString = "The strategy was not in the database. Please refresh this page.";
            }
        }
        map.addAttribute("success", success);
        map.addAttribute("resultText" , resultString);
        return new JsonView();
    }
    
    @Autowired
    public void setStrategyDao(StrategyDao strategyDao) {
        this.strategyDao = strategyDao;
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
}