package com.cannontech.web.widget;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PaoGroupsWrapper;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

/**
 * Widget used to display basic device information
 */
public class MeterInformationWidget extends WidgetControllerBase {

    private PaoDao paoDao = null;
    private PaoGroupsWrapper paoGroupsWrapper = null;

    @Required
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }

    @Required
    public void setPaoGroupsWrapper(PaoGroupsWrapper paoGroupsWrapper) {
        this.paoGroupsWrapper = paoGroupsWrapper;
    }

    @Override
    public ModelAndView render(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        ModelAndView mav = new ModelAndView();

        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        LiteYukonPAObject pao = paoDao.getLiteYukonPAO(deviceId);

        String type = paoGroupsWrapper.getPAOTypeString(pao.getType());
        
        mav.addObject("device", pao);
        mav.addObject("deviceType", type);

        return mav;
    }

}
