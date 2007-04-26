package com.cannontech.web.editor;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.capcontrol.CapBankAdditional;
import com.cannontech.web.util.CBCDBUtil;

public class CapBankAdditionalServlet extends AbstractController {

    protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("cbcAddInfoPopup");
        int paoID = ServletRequestUtils.getRequiredIntParameter(request,
                                                                "paoID");
        LiteYukonPAObject lite = DaoFactory.getPaoDao().getLiteYukonPAO(paoID);
        CapBankAdditional capBankAdd = new CapBankAdditional();
        capBankAdd.setDeviceID(paoID);
        Connection conn = CBCDBUtil.getConnection();
        capBankAdd.setDbConnection(conn);
        capBankAdd.retrieve();
        CBCDBUtil.closeConnection(conn);

        mav.addObject("capBankAdd", capBankAdd);
        mav.addObject("lite", lite);
        return mav;
    }

}
