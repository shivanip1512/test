package com.cannontech.web.stars.action.inventory;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.db.stars.purchasing.ScheduleTimePeriod;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.bean.PurchaseBean;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.stars.action.StarsInventoryActionController;

public class TimePeriodChangeController extends StarsInventoryActionController {

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response,
            final HttpSession session, final StarsYukonUser user, 
                final LiteStarsEnergyCompany energyCompany) throws Exception {
    	
    	String redirect = this.getRedirect(request);
    	
    	PurchaseBean pBean = (PurchaseBean) session.getAttribute("purchaseBean");
        ScheduleTimePeriod currentPeriod = pBean.getCurrentTimePeriod();
        
        currentPeriod.setScheduleID(pBean.getCurrentSchedule().getScheduleID());
        currentPeriod.setTimePeriodName(request.getParameter("name"));
        currentPeriod.setQuantity(new Integer(request.getParameter("quantity")));
        Date shipDate = ServletUtil.parseDateStringLiberally( request.getParameter("shipDate"), pBean.getEnergyCompany().getDefaultTimeZone());
        if (shipDate == null)
        {
            session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, null);
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Invalid ship date '" + request.getParameter("ShipDate") + "'");
            redirect = request.getContextPath() + "/operator/Hardware/ScheduleTimePeriod.jsp";
        }
        
        currentPeriod.setPredictedShipDate(shipDate);
        
        try
        {
            /**
             * new time period
             */
            if(currentPeriod.getTimePeriodID() == null)
            {
                currentPeriod.setTimePeriodID(ScheduleTimePeriod.getNextTimePeriodID());
                Transaction.createTransaction(Transaction.INSERT, currentPeriod).execute();
                session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "New time projection added to this delivery schedule.");
            }
            else
            {
                Transaction.createTransaction(Transaction.UPDATE, currentPeriod).execute();
                session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Time projection successfully updated in the database.");
            }
        }
        catch (TransactionException e) 
        {
            CTILogger.error( e.getMessage(), e );
            e.printStackTrace();
            session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, null);
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Time projection could not be saved to the database.  Transaction failed.");
        }
        
        redirect = request.getContextPath() + "/operator/Hardware/DeliverySchedule.jsp";
    	response.sendRedirect(redirect);
    }
	
}
