package com.cannontech.web.stars.action.inventory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.db.purchasing.Shipment;
import com.cannontech.stars.service.EnergyCompanyService;
import com.cannontech.stars.util.FilterWrapper;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.bean.InventoryBean;
import com.cannontech.stars.web.bean.PurchaseBean;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.stars.action.StarsInventoryActionController;

public class ShipmentChangeController extends StarsInventoryActionController {

    @Autowired private EnergyCompanyService ecService;
    
    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response,
            final HttpSession session, final StarsYukonUser user, 
                final LiteStarsEnergyCompany energyCompany) throws Exception {
    	
    	String redirect = this.getRedirect(request);
    	
    	PurchaseBean pBean = (PurchaseBean) session.getAttribute("purchaseBean");
        Shipment currentShipment = pBean.getCurrentShipment();
        boolean shipmentFailed = false;
         
        currentShipment.setShipmentNumber(request.getParameter("name"));
        String warehouse = request.getParameter("warehouse");
        currentShipment.setWarehouseID(warehouse == null ? 0 : Integer.valueOf(warehouse));
        TimeZone systemTimeZone = ecService.getDefaultTimeZone(pBean.getEnergyCompany().getEnergyCompanyId());
        Date orderedDate = ServletUtil.parseDateStringLiberally(request.getParameter("orderingDate"), systemTimeZone);
        Date shipDate = ServletUtil.parseDateStringLiberally(request.getParameter("shipDate"), systemTimeZone);
        Date receivedDate = ServletUtil.parseDateStringLiberally(request.getParameter("receivingDate"), systemTimeZone);
        if (shipDate == null)
        {
            session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, null);
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Invalid start date '" + request.getParameter("StartDate") + "'");
            redirect = request.getContextPath() + "/operator/Hardware/ScheduleTimePeriod.jsp";
        }
        
        currentShipment.setShipDate(shipDate);
        currentShipment.setReceivedDate(receivedDate);
        currentShipment.setOrderedDate(orderedDate);
        
        currentShipment.setActualPricePerUnit(new Double(request.getParameter("pricePerUnit")));
        currentShipment.setSalesTax(new Double(request.getParameter("salesTax")));
        currentShipment.setShippingCharges(new Double(request.getParameter("shippingCharges")));
        currentShipment.setOtherCharges(new Double(request.getParameter("otherCharges")));
        currentShipment.setSalesTotal(new Double(request.getParameter("total")));
        currentShipment.setAmountPaid(new Double(request.getParameter("amountPaid")));
        
        String serialStart = (request.getParameter("serialStart"));
        String serialEnd = (request.getParameter("serialEnd"));
        if(serialStart != null) {
            currentShipment.setSerialNumberStart(serialStart);
        }
        if(serialEnd != null) {
            currentShipment.setSerialNumberEnd(serialEnd);
        }
        
        if(serialStart != null && serialEnd != null)
        {
            long snFrom = 0, snTo = 0;
            try {
                snFrom = Long.parseLong( serialStart );
                if (serialEnd.length() > 0) {
                    snTo = Long.parseLong( serialEnd );
                } else {
                    snTo = snFrom;
                }
            }
            catch (NumberFormatException nfe) {
                session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Invalid number format in the Serial range");
                redirect = request.getContextPath() + "/operator/Hardware/Shipment.jsp";
                response.sendRedirect(redirect);
                return;
            }
            if (snFrom > snTo) {
                session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "The Serial range 'from' value cannot be greater than the 'to' value");
                redirect = request.getContextPath() + "/operator/Hardware/Shipment.jsp";
                response.sendRedirect(redirect);
                return;
            }
        }
        
        try
        {
            /**
             * new time period
             */
            if(currentShipment.getShipmentID() == null)
            {
                currentShipment.setShipmentID(Shipment.getNextShipmentID());
                currentShipment.setScheduleID(pBean.getCurrentSchedule().getScheduleID());
                Transaction.createTransaction(Transaction.INSERT, currentShipment).execute();
                session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "New shipment added to this delivery schedule.");
            }
            else
            {
                Transaction.createTransaction(Transaction.UPDATE, currentShipment).execute();
                session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Shipment successfully updated in the database.");
             }
        }
        catch (TransactionException e) 
        {
            CTILogger.error( e.getMessage(), e );
            e.printStackTrace();
            session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, null);
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Shipment could not be saved to the database.  Transaction failed.");
            shipmentFailed = true;
        }
        
        redirect = request.getContextPath() + "/operator/Hardware/DeliverySchedule.jsp";
        
        if(!shipmentFailed)
        {
            /*
             * Going to do some automation here
             * A: Need to check for existence of serial numbers already in the system
             * B: Create new with currentState = Ordered in appropriate warehouse
             * C: Probably will need to spin off a process task so we can time it
             * */
            if(serialStart != null && serialEnd != null)
            {
                InventoryBean iBean = (InventoryBean) session.getAttribute("inventoryBean");
                if(iBean == null)
                {
                    session.setAttribute("inventoryBean", new InventoryBean());
                    iBean = (InventoryBean) session.getAttribute("inventoryBean");
                }
                
                /*
                 * Let's cheat a little and use the inventoryBean filtering to look for serial range.
                 */
                ArrayList<FilterWrapper> tempList = new ArrayList<FilterWrapper>();
                String devType = pBean.getCurrentSchedule().getModelID().toString();
                tempList.add(new FilterWrapper(String.valueOf(YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_DEV_TYPE), devType, devType));
                tempList.add(new FilterWrapper(String.valueOf(YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_SERIAL_RANGE_MAX), serialEnd, serialEnd));
                tempList.add(new FilterWrapper(String.valueOf(YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_SERIAL_RANGE_MIN), serialStart, serialStart));
                iBean.setFilterByList(tempList);
                List<LiteInventoryBase> found = null;
                String errMsg = null;
                try {
                    found = iBean.getInventoryList(request);
                } catch (PersistenceException e){
                    errMsg = e.getMessage();
                }
                if(found == null)
                {
                    if (errMsg == null) {
                        errMsg = "Inventory was unable to determine if serial range is pre-existing.  It is unsafe to create this range.";
                    }
                    pBean.setCurrentSerialNumberError(true);
                    session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, errMsg);
                    redirect = request.getContextPath() + "/operator/Hardware/Shipment.jsp";
                }
                //found some, better not create this range
                else if(found.size() > 0)
                {
                    pBean.setCurrentSerialNumberError(true);
                    session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "The specified serial range includes switches already in inventory.  Unable to create new range.");
                    redirect = request.getContextPath() + "/operator/Hardware/Shipment.jsp";
                }
                else
                {
                    session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Actual serial range has not yet been created in inventory.  Please verify the following information and click submit.");
                    redirect = request.getContextPath() + "/operator/Hardware/ShipmentSNRangeAdd.jsp";
                }
                    
            } else {
                redirect = request.getContextPath() + "/operator/Hardware/DeliverySchedule.jsp";
            }
        }
        
        response.sendRedirect(redirect);
    }
	
}
