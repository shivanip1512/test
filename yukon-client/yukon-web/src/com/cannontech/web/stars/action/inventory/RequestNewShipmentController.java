package com.cannontech.web.stars.action.inventory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.db.stars.purchasing.Shipment;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.bean.PurchaseBean;
import com.cannontech.web.stars.action.StarsInventoryActionController;

public class RequestNewShipmentController extends StarsInventoryActionController {

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response,
            final HttpSession session, final StarsYukonUser user, 
                final LiteStarsEnergyCompany energyCompany) throws Exception {
    	
        PurchaseBean pBean = (PurchaseBean) session.getAttribute("purchaseBean");
        pBean.setCurrentShipment(new Shipment());
        
        String redirect = request.getContextPath() + "/operator/Hardware/Shipment.jsp";
    	response.sendRedirect(redirect);
    }
	
}
