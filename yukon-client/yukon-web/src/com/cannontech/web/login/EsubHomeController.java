package com.cannontech.web.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;

@Controller
public class EsubHomeController {
    @Autowired private RolePropertyDao rolePropertyDao;
    
    @RequestMapping("/esub/home")
    public String home(LiteYukonUser user) throws Exception {
        String esubHomeUrl =
            rolePropertyDao.getPropertyStringValue(YukonRoleProperty.OPERATOR_ESUBSTATION_DRAWINGS_HOME_URL, user);

        return "redirect:" + esubHomeUrl;
    }
}
