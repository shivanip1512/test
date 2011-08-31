package com.cannontech.web.capcontrol;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.capcontrol.dao.CapbankDao;
import com.cannontech.capcontrol.model.CapbankAdditional;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
@RequestMapping("/capAddInfo/*")
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class CapBankAdditionalController {

	private CapbankDao capbankDao;
	
    @RequestMapping
    public String view(HttpServletRequest request, ModelMap model, int paoId) throws SQLException {
        CapbankAdditional capBankAdd = capbankDao.getCapbankAdditional(paoId);

        model.addAttribute("capBankAdd", capBankAdd);
        
        return "capBankAddInfoPopup.jsp";
    }
    
    @Autowired
    public void setCapbankDao(CapbankDao capbankDao) {
		this.capbankDao = capbankDao;
	}

}