package com.cannontech.web.capcontrol;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.core.dao.CapControlDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
@RequestMapping("/tier/cceditorpopup/*")
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class CCEditorController {
    private CapControlDao capControlDao;
    
    @RequestMapping("orphans")
    public String orphans(ModelMap model) {
        model.addAttribute("orphans", capControlDao.getOrphanedCBCs());
        return "tier/popupmenu/orphanedCBCPopup.jsp";
    }

    @Autowired
    public void setCapControlDao(CapControlDao capControlDao) {
        this.capControlDao = capControlDao;
    }
    
}