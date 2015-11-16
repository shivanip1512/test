package com.cannontech.web.stars.action.inventory;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.db.purchasing.Invoice;
import com.cannontech.stars.database.db.purchasing.Shipment;
import com.cannontech.stars.service.EnergyCompanyService;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.bean.PurchaseBean;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.stars.action.StarsInventoryActionController;

public class InvoiceChangeController extends StarsInventoryActionController {
    @Autowired private EnergyCompanyService ecService;

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response,
            final HttpSession session, final StarsYukonUser user, 
                final LiteStarsEnergyCompany energyCompany) throws Exception {
    	
    	String redirect = this.getRedirect(request);
    	
    	PurchaseBean pBean = (PurchaseBean) session.getAttribute("purchaseBean");
        Invoice currentInvoice = pBean.getCurrentInvoice();
        
        currentInvoice.setInvoiceDesignation(request.getParameter("name"));
        TimeZone ecTimeZone = ecService.getDefaultTimeZone(pBean.getEnergyCompany().getEnergyCompanyId());
        Date dateSubmitted = ServletUtil.parseDateStringLiberally(request.getParameter("dateSubmitted"), ecTimeZone);
        currentInvoice.setDateSubmitted(dateSubmitted);
        currentInvoice.setDatePaid(ServletUtil.parseDateStringLiberally(request.getParameter("datePaid"), ecTimeZone));
        currentInvoice.setAuthorized(request.getParameter("authorized") == null ? "N" : "Y");
        currentInvoice.setHasPaid(request.getParameter("hasPaid") == null ? "N" : "Y");
        currentInvoice.setAuthorizedBy(request.getParameter("authorizedBy"));
        currentInvoice.setTotalQuantity(new Integer(request.getParameter("quantity")));
        currentInvoice.setAuthorizedNumber(request.getParameter("authorizedNum"));

        try
        {
            //new invoice
            if(currentInvoice.getInvoiceID() == null)
            {
                currentInvoice.setInvoiceID(Invoice.getNextInvoiceID());
                currentInvoice.setPurchasePlanID(pBean.getCurrentPlan().getPurchaseID());
                Transaction.createTransaction(Transaction.INSERT, currentInvoice).execute();
                session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "New invoice added to this purchase plan.");
                redirect = request.getContextPath() + "/operator/Hardware/Invoice.jsp";
            }
            else
            {
                Transaction.createTransaction(Transaction.UPDATE, currentInvoice).execute();
                session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Invoice successfully updated in the database.");
                redirect = request.getContextPath() + "/operator/Hardware/PurchaseTrack.jsp";
            }
            
            /**
             * Shipments --
             * For invoices, we need to worry about new and deleted ones.
             */
            String[] shipmentIDs = request.getParameterValues("shipments");
            List<Shipment> oldShipList = Shipment.getAllShipmentsForInvoice(currentInvoice.getInvoiceID());
            
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
                 * Just don't delete the whole shipment, just the mapping!!!!
                 */
                if(!isFound)
                {
                    Transaction.createTransaction(Transaction.DELETE_PARTIAL, oldShipList.get(i)).execute();
                }
            }
            
            if(shipmentIDs != null)
            {
                for(int j = 0; j < shipmentIDs.length; j++)
                {
                    boolean isFound = false;
                    
                    for(int i = 0; i < oldShipList.size(); i++)
                    {
                        if(shipmentIDs[j].compareTo(oldShipList.get(i).getShipmentID().toString()) == 0)
                        {
                            isFound = true;
                            break;
                        }
                    }
                    
                    /*
                     * In new list only, must have been added
                     */
                    if(!isFound)
                    {
                        currentInvoice.setShipmentID(new Integer(shipmentIDs[j]));
                        Transaction.createTransaction(Transaction.ADD_PARTIAL, currentInvoice).execute();
                    }
                }
            }
        }
        catch (TransactionException e) 
        {
            CTILogger.error( e.getMessage(), e );
            e.printStackTrace();
            session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, null);
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Invoice could not be saved to the database.  Transaction failed.");
        }
    	
        response.sendRedirect(redirect);
    }
	
}
