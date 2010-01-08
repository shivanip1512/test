package com.cannontech.web.capcontrol;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.cannontech.capcontrol.OrphanCBC;
import com.cannontech.core.dao.CapControlDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class CCEditorController implements Controller {
    private CapControlDao capControlDao;
    
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ModelAndView mav = new ModelAndView();
        
        List<OrphanCBC> orphans = capControlDao.getOrphanedCBCs();
        mav.addObject("orphans", orphans);
        
        mav.setViewName("tier/popupmenu/orphanedCBCPopup.jsp");
        return mav;
    }

    public void setCapControlDao(CapControlDao capControlDao) {
        this.capControlDao = capControlDao;
    }
    
}
