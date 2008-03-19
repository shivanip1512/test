package com.cannontech.web.capcontrol;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.cannontech.capcontrol.OrphanCBC;
import com.cannontech.core.dao.CapControlDao;

public class CCEditorController implements Controller {
    private CapControlDao cbcDao;
    
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ModelAndView mav = new ModelAndView();
        
        List<OrphanCBC> orphans = cbcDao.getOrphanedCBCs();
        mav.addObject("orphans", orphans);
        
        mav.setViewName("tier/popupmenu/orphanedCBCPopup");
        return mav;
    }

    public void setCBCDao(CapControlDao cbcDao) {
        this.cbcDao = cbcDao;
    }
    
}
