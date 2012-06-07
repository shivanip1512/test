package com.cannontech.web.stars.action.inventory;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.db.purchasing.DeliverySchedule;
import com.cannontech.stars.database.db.purchasing.Invoice;
import com.cannontech.stars.database.db.purchasing.PurchasePlan;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.bean.PurchaseBean;
import com.cannontech.web.stars.action.StarsInventoryActionController;

public class PurchaseChangeController extends StarsInventoryActionController {

    private SimpleJdbcOperations jdbcTemplate;
    
    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response,
            final HttpSession session, final StarsYukonUser user, 
                final LiteStarsEnergyCompany energyCompany) throws Exception {
    	
    	PurchaseBean pBean = (PurchaseBean) session.getAttribute("purchaseBean");
        PurchasePlan currentPlan = pBean.getCurrentPlan();
        
        currentPlan.setPlanName(request.getParameter("name"));
        currentPlan.setPoDesignation(request.getParameter("poNumber"));
        currentPlan.setAccountingCode(request.getParameter("accountingCode"));
        
        try
        {
            //new purchase plan
            if(currentPlan.getPurchaseID() == null) {
                currentPlan.setPurchaseID(PurchasePlan.getNextPurchaseID());
                currentPlan.setEnergyCompanyID(pBean.getEnergyCompany().getEnergyCompanyId());
                Transaction.createTransaction(Transaction.INSERT, currentPlan).execute();
                session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "New purchase plan added to database.");
            } else {
                Transaction.createTransaction(Transaction.UPDATE, currentPlan).execute();
                session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Purchase plan successfully updated in the database.");
            }
            
            /**
             * Delivery Schedules --
             * Only really have to worry about deletes: updates and news have already been added
             **/
            String[] scheduleIDs = request.getParameterValues("schedules");
            List<DeliverySchedule> oldDeliveryScheduleList = DeliverySchedule.getAllDeliverySchedulesForAPlan(currentPlan.getPurchaseID());
            
            for(int i = 0; i < oldDeliveryScheduleList.size(); i++)
            {
                boolean isFound = false;
                if(scheduleIDs != null)
                {
                    for(int j = 0; j < scheduleIDs.length; j++)
                    {
                        if(scheduleIDs[j].compareTo(oldDeliveryScheduleList.get(i).getScheduleID().toString()) == 0)
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
                    /* Delete the entry out of the schedule shipment mapping table and 
                     * out of the schedule table */
                    String sql = "DELETE FROM ScheduleShipmentMapping WHERE ScheduleID = ?";
                    jdbcTemplate.update(sql, oldDeliveryScheduleList.get(i).getScheduleID());
                    Transaction.createTransaction(Transaction.DELETE, oldDeliveryScheduleList.get(i)).execute();
                }
            }
            
            /**
             * Invoices --
             * Only really have to worry about deletes: updates and news have already been added
             **/
            String[] invoiceIDs = request.getParameterValues("invoices");
            List<Invoice> oldInvoiceList = Invoice.getAllInvoicesForPurchasePlan(currentPlan.getPurchaseID());
            
            for(int i = 0; i < oldInvoiceList.size(); i++)
            {
                boolean isFound = false;
                if(invoiceIDs != null)
                {
                    for(int j = 0; j < invoiceIDs.length; j++)
                    {
                        if(invoiceIDs[j].compareTo(oldInvoiceList.get(i).getInvoiceID().toString()) == 0)
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
                    /* Delete the entry out of the invoice shipment mapping table and 
                     * out of the invoice table */
                    String sql = "DELETE FROM InvoiceShipmentMapping WHERE InvoiceID = ?";
                    jdbcTemplate.update(sql, oldInvoiceList.get(i).getInvoiceID());
                    Transaction.createTransaction(Transaction.DELETE, oldInvoiceList.get(i)).execute();
                }
            }
        }
        catch (TransactionException e) 
        {
            CTILogger.error( e.getMessage(), e );
            e.printStackTrace();
            session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, null);
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Purchase plan could not be saved to the database.  Transaction failed.");
        }
        
        String redirect = request.getContextPath() + "/operator/Hardware/PurchaseTrack.jsp";
    	response.sendRedirect(redirect);
    }

    public void setJdbcTemplate(SimpleJdbcOperations jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
	
    
}
