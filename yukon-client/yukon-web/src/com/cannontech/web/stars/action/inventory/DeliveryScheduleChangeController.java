package com.cannontech.web.stars.action.inventory;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.db.purchasing.DeliverySchedule;
import com.cannontech.stars.database.db.purchasing.ScheduleTimePeriod;
import com.cannontech.stars.database.db.purchasing.Shipment;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.bean.PurchaseBean;
import com.cannontech.web.stars.action.StarsInventoryActionController;

public class DeliveryScheduleChangeController extends StarsInventoryActionController {

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response,
            final HttpSession session, final StarsYukonUser user, 
                final LiteStarsEnergyCompany energyCompany) throws Exception {
    	
    	PurchaseBean pBean = (PurchaseBean) session.getAttribute("purchaseBean");
        DeliverySchedule currentSchedule = pBean.getCurrentSchedule();
        
        currentSchedule.setScheduleName(request.getParameter("name"));
        currentSchedule.setModelID(new Integer(request.getParameter("modelType")));
        currentSchedule.setStyleNumber(request.getParameter("styleNumber"));
        currentSchedule.setOrderNumber(request.getParameter("orderNumber"));
        currentSchedule.setQuotedPricePerUnit(new Double(request.getParameter("pricePerUnit")));
        
        try
        {
            //new schedule
            if(currentSchedule.getScheduleID() == null)
            {
                currentSchedule.setScheduleID(DeliverySchedule.getNextScheduleID());
                currentSchedule.setPurchasePlanID(pBean.getCurrentPlan().getPurchaseID());
                Transaction.createTransaction(Transaction.INSERT, currentSchedule).execute();
                session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "New delivery schedule added to this purchase plan.");
            }
            else
            {
                Transaction.createTransaction(Transaction.UPDATE, currentSchedule).execute();
                session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Delivery schedule successfully updated in the database.");
            }
            
            /**
             * Time periods --
             * Only really have to worry about deletes: updates and news have already been added
             */
            String[] timeIDs = request.getParameterValues("times");
            List<ScheduleTimePeriod> oldList = ScheduleTimePeriod.getAllTimePeriodsForDeliverySchedule(currentSchedule.getScheduleID());
          
            for(int i = 0; i < oldList.size(); i++)
            {
                boolean isFound = false;
                
                if(timeIDs != null)
                {    
                    for(int j = 0; j < timeIDs.length; j++)
                    {
                        if(timeIDs[j].compareTo(oldList.get(i).getTimePeriodID().toString()) == 0)
                        {
                            isFound = true;
                            break;
                        }
                    }
                }
                /*
                 * In old list only, must have been removed
                 */
                if(!isFound)
                {
                    Transaction.createTransaction(Transaction.DELETE, oldList.get(i)).execute();
                }
            }
            
            /**
             * Shipments --
             * Only really have to worry about deletes: updates and news have already been added
             */
            String[] shipmentIDs = request.getParameterValues("shipments");
            List<Shipment> oldShipList = Shipment.getAllShipmentsForDeliverySchedule(currentSchedule.getScheduleID());
            
            for(int i = 0; i < oldShipList.size(); i++)
            {
                boolean isFound = false;
                
                if(shipmentIDs != null)
                {    
                    for(int j = 0; j < shipmentIDs.length; j++)
                    {
                        if(shipmentIDs[j].compareTo(oldShipList.get(i).getShipmentID().toString()) == 0)
                        {
                            isFound = true;
                            break;
                        }
                    }
                }
                /*
                 * In old list only, must have been removed
                 */
                if(!isFound)
                {
                    Transaction.createTransaction(Transaction.DELETE, oldShipList.get(i)).execute();
                }
            }
        }
        catch (TransactionException e) 
        {
            CTILogger.error( e.getMessage(), e );
            e.printStackTrace();
            session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, null);
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Delivery schedule could not be saved to the database.  Transaction failed.");
        }
        
        String redirect = request.getContextPath() + "/operator/Hardware/DeliverySchedule.jsp";
        response.sendRedirect(redirect);
    }
	
}
