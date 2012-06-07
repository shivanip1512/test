package com.cannontech.web.stars.action.inventory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.data.purchasing.PurchasingMultiDelete;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.bean.PurchaseBean;
import com.cannontech.web.stars.action.StarsInventoryActionController;

public class DeletePurchasePlanConfirmedController extends StarsInventoryActionController {

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response,
            final HttpSession session, final StarsYukonUser user, 
                final LiteStarsEnergyCompany energyCompany) throws Exception {
    	
        PurchaseBean pBean = (PurchaseBean) session.getAttribute("purchaseBean");
        PurchasingMultiDelete plannedDeath = new PurchasingMultiDelete();
        plannedDeath.setPurchasePlan(pBean.getCurrentPlan());
        plannedDeath.setSchedules(pBean.getAvailableSchedules());
        plannedDeath.setShipments(pBean.getAvailableShipments());
        plannedDeath.setInvoices(pBean.getAvailableInvoices());
        
        try
        {
            Transaction.createTransaction(Transaction.DELETE, plannedDeath).execute();
            session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Plan " + pBean.getCurrentPlan().getPlanName() + " was successfully deleted.");
        }
        catch (TransactionException e) 
        {
            CTILogger.error( e.getMessage(), e );
            e.printStackTrace();
            session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, null);
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Plan deletion did NOT complete.  Transaction failed.");
        }
        
        /*
         * Set current plan as the next in the list (next most recently created)
         */
        pBean.setCurrentPlan(pBean.getAvailablePlans().get(0));
        String redirect = request.getContextPath() + "/operator/Hardware/PurchaseTrack.jsp";
        response.sendRedirect(redirect);
    }
	
}
