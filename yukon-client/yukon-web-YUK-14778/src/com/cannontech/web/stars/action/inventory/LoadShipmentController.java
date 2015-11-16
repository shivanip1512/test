package com.cannontech.web.stars.action.inventory;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.db.purchasing.Shipment;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.bean.PurchaseBean;
import com.cannontech.web.stars.action.StarsInventoryActionController;

public class LoadShipmentController extends StarsInventoryActionController {

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response,
            final HttpSession session, final StarsYukonUser user, 
                final LiteStarsEnergyCompany energyCompany) throws Exception {
    	
        PurchaseBean pBean = (PurchaseBean) session.getAttribute("purchaseBean");
        pBean.setCurrentSerialNumberError(false);
        List<Shipment> shipments = pBean.getAvailableShipments();
        Integer idToLoad = ServletRequestUtils.getIntParameter(request, "shipments");
        
        for (final Shipment shipment : shipments) {
        	if (shipment.getShipmentID().equals(idToLoad)) pBean.setCurrentShipment(shipment);
        }
        
        String redirect = request.getContextPath() + "/operator/Hardware/Shipment.jsp";
    	response.sendRedirect(redirect);
    }
	
}
