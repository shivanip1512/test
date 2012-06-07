package com.cannontech.web.stars.action.inventory;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.db.purchasing.PurchasePlan;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.bean.PurchaseBean;
import com.cannontech.web.stars.action.StarsInventoryActionController;

public class LoadPurchasePlanController extends StarsInventoryActionController {

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
        
        String redirect = request.getContextPath() + "/operator/Hardware/PurchaseTrack.jsp";
    	response.sendRedirect(redirect);
    }
	
}
