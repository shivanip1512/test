package com.cannontech.web.editor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.dao.CapbankDao;
import com.cannontech.cbc.model.Capbank;
import com.cannontech.core.dao.CBCDao;
import com.cannontech.database.data.point.CBCPointTimestampParams;
import com.cannontech.yukon.cbc.CapBankDevice;

@SuppressWarnings("serial")
public class CCPointTimestampServlet extends AbstractController {
    private CBCDao cbcDao;
    private CapbankDao capbankDao;
    private CapControlCache capControlCache;

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest req, HttpServletResponse response) throws Exception {
        final ModelAndView mav = new ModelAndView("pointDataPopup");
        final int cbcID = ServletRequestUtils.getRequiredIntParameter(req, "cbcID");
        
        Capbank capBank = capbankDao.getByControlDeviceId(cbcID);
        CapBankDevice capBankDevice = capControlCache.getCapBankDevice(capBank.getId());
        String capBankName = capBankDevice.getCcName();
        mav.addObject("capBankName", capBankName);
        
        List<CBCPointTimestampParams> pointList = cbcDao.getCBCPointTimeStamps(cbcID);
        mav.addObject("pointList", pointList);

        return mav;
    }

    public void setCbcDao(CBCDao cbcDao) {
        this.cbcDao = cbcDao;
    }

    public void setCapbankDao(CapbankDao capbankDao) {
        this.capbankDao = capbankDao;
    }

    public void setCapControlCache(CapControlCache capControlCache) {
        this.capControlCache = capControlCache;
    }
    
}
