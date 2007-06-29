package com.cannontech.web.widget;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.account.dao.AccountInfoDao;
import com.cannontech.amr.account.model.AccountInfo;
import com.cannontech.amr.account.model.Address;
import com.cannontech.amr.account.model.ServiceLocation;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

public class AccountInformationWidget extends WidgetControllerBase{
    private AccountInfoDao accountInfoDao;
    
    @Override
    public ModelAndView render(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ModelAndView mav = new ModelAndView();
        final int deviceId = WidgetParameterHelper.getIntParameter(request, "deviceId");
        
        try {
            final AccountInfo info = accountInfoDao.getAccount(deviceId);
            final ServiceLocation serviceInfo = accountInfoDao.getServiceLocation(deviceId);
            
            mav.addObject("deviceId", deviceId);
            mav.addObject("info", info);
            mav.addObject("serviceInfo", serviceInfo);
            mav.addObject("infoMapURL", getMapURL(info.getAddress()));
            mav.addObject("serviceInfoMapURL", getMapURL(serviceInfo.getAddress()));
            mav.setViewName("accountInformationWidget/accountInfo.jsp");
            return mav;
        } catch (Exception e) {
            mav.addObject("error", e.getMessage());
            mav.setViewName("accountInformationWidget/mspError.jsp");
            return mav;
        }
    }
    
    private String getMapURL(Address address) {
        if (address == null ||
                address.getLocationAddress1() == null ||
                address.getCityName() == null ||
                address.getStateCode() == null ||
                address.getZipCode() == null) {
            return null;
        }

        String[] split = StringUtils.split(address.getLocationAddress1(), ' ');
        String join = StringUtils.join(split, '+');

        final StringBuilder sb = new StringBuilder();
        sb.append("http://maps.google.com/maps?f=q&hl=en&q=");
        sb.append(join);
        sb.append("+");
        sb.append(address.getCityName());
        sb.append(",+");
        sb.append(address.getStateCode());
        sb.append("+");
        sb.append(address.getZipCode());
        sb.append("&ie=UTF8&om=1&z=16&iwloc=addr&t=h");
        return sb.toString();
    }
    
    public void setAccountInfoDao(final AccountInfoDao accountInfoDao) {
        this.accountInfoDao = accountInfoDao;
    }

}
