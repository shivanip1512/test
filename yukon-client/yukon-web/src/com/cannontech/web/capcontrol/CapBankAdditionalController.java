package com.cannontech.web.capcontrol;

import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.capcontrol.CapBankAdditional;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.util.CBCDBUtil;

@Controller
@RequestMapping("/capAddInfo/*")
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class CapBankAdditionalController {

    @RequestMapping
    public String view(HttpServletRequest request, ModelMap model, int paoId) throws SQLException {
        LiteYukonPAObject lite = DaoFactory.getPaoDao().getLiteYukonPAO(paoId);
        CapBankAdditional capBankAdd = new CapBankAdditional();
        capBankAdd.setDeviceID(paoId);
        Connection conn = CBCDBUtil.getConnection();
        capBankAdd.setDbConnection(conn);
        capBankAdd.retrieve();
        CBCDBUtil.closeConnection(conn);

        model.addAttribute("capBankAdd", capBankAdd);
        model.addAttribute("lite", lite);
        
        return "capBankAddInfoPopup.jsp";
    }

}