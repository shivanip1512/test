package com.cannontech.web.stars.action.inventory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.db.purchasing.ScheduleTimePeriod;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.bean.PurchaseBean;
import com.cannontech.web.stars.action.StarsInventoryActionController;

public class RequestNewTimePeriodController extends StarsInventoryActionController {

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response,
            final HttpSession session, final StarsYukonUser user, 
                final LiteStarsEnergyCompany energyCompany) throws Exception {
    	
        PurchaseBean pBean = (PurchaseBean) session.getAttribute("purchaseBean");
        pBean.setCurrentTimePeriod(new ScheduleTimePeriod());
        pBean.getCurrentTimePeriod().setScheduleID(pBean.getCurrentSchedule().getScheduleID());
        
        String redirect = request.getContextPath() + "/operator/Hardware/ScheduleTimePeriod.jsp";
    	response.sendRedirect(redirect);
    }
	
}
