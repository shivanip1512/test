package com.cannontech.web.stars.action.inventory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.util.InventoryManagerUtil;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.stars.action.StarsInventoryActionController;

public class SelectDeviceController extends StarsInventoryActionController {

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response,
            HttpSession session, final StarsYukonUser user, 
                final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        int deviceID = ServletRequestUtils.getIntParameter(request, "DeviceID");
        LiteYukonPAObject litePao = this.paoDao.getLiteYukonPAO( deviceID );
        session.setAttribute(InventoryManagerUtil.DEVICE_SELECTED, litePao);
        
        String redirect = (String) session.getAttribute( ServletUtils.ATT_REDIRECT );
        redirect = ServletUtil.createSafeRedirectUrl(request, redirect);
        response.sendRedirect(redirect);
    }
    
}
