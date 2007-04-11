package com.cannontech.web.editor;

import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.cannontech.core.dao.CBCDao;
import com.cannontech.database.data.point.CBCPointTimestampParams;

@SuppressWarnings("serial")
public class CCPointTimestampServlet extends AbstractController {
    
    private CBCDao cbcDao;

    
    protected ModelAndView handleRequestInternal(HttpServletRequest req, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("pointDataPopup");
        int cbcID = ServletRequestUtils.getRequiredIntParameter(req, "cbcID");
        List<CBCPointTimestampParams> pointList = cbcDao.getCBCPointTimeStamps(new Integer (cbcID));
        mav.addObject("pointList", pointList);
        return mav;
    }

    public void setCbcDao(CBCDao cbcDao) {
        this.cbcDao = cbcDao;
    }
    
}
