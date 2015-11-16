package com.cannontech.web.stars.action.inventory;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.db.purchasing.PurchasePlan;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.bean.PurchaseBean;
import com.cannontech.web.stars.action.StarsInventoryActionController;

public class DeletePurchasePlanController extends StarsInventoryActionController {

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response,
            final HttpSession session, final StarsYukonUser user, 
                final LiteStarsEnergyCompany energyCompany) throws Exception {
    	
        PurchaseBean pBean = (PurchaseBean) session.getAttribute("purchaseBean");
        List<PurchasePlan> purchasePlans = pBean.getAvailablePlans();
        Integer idToLoad = ServletRequestUtils.getIntParameter(request, "plans");
        
        for (final PurchasePlan plan : purchasePlans) {
        	if (plan.getPurchaseID().equals(idToLoad)) pBean.setCurrentPlan(plan); 
        }
        
        session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "WARNING: You will be deleting all delivery schedules, shipments, and invoices under the specified purchase plan.  Continue to delete?");
        String redirect = request.getContextPath() + "/operator/Hardware/DeletePurchaseTrack.jsp";
        response.sendRedirect(redirect);
    }
	
}
