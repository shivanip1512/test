package com.cannontech.web.stars.action.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.db.customer.Address;
import com.cannontech.database.db.stars.hardware.Warehouse;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.web.stars.action.StarsAdminActionController;

public class UpdateWarehouseController extends StarsAdminActionController {

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response,
            final HttpSession session, final StarsYukonUser user, 
                final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        try {
            Integer warehouseID = ServletRequestUtils.getIntParameter(request,"WarehouseID");
            Warehouse currentWarehouse = new Warehouse();
            Address currentAddress = new Address();
            boolean isNew = true;
            
            if(warehouseID.intValue() != CtiUtilities.NONE_ZERO_ID) {    
                currentWarehouse.setWarehouseID(warehouseID);
                currentWarehouse = (Warehouse)Transaction.createTransaction(Transaction.RETRIEVE, currentWarehouse).execute();
                currentAddress.setAddressID(currentWarehouse.getAddressID());
                currentAddress = (Address)Transaction.createTransaction(Transaction.RETRIEVE, currentAddress).execute();
                isNew = false;
            }
            
            currentWarehouse.setWarehouseName(request.getParameter( "warehouseName"));
            currentWarehouse.setNotes(request.getParameter( "notes"));
            currentAddress.setLocationAddress1(request.getParameter( "addr1" ));
            currentAddress.setLocationAddress2(request.getParameter( "addr2" ));
            currentAddress.setCityName(request.getParameter( "city" ));
            currentAddress.setStateCode(request.getParameter( "state" ));
            currentAddress.setZipCode(request.getParameter( "zip" ));
            
            if(isNew) {
                
                currentAddress = (Address)Transaction.createTransaction(Transaction.INSERT, currentAddress).execute();
                currentWarehouse.setAddressID(currentAddress.getAddressID());
                currentWarehouse.setEnergyCompanyID(new Integer(user.getEnergyCompanyID()));
                currentWarehouse = (Warehouse)Transaction.createTransaction(Transaction.INSERT, currentWarehouse).execute();
                session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Warehouse successfully created.");
            
            } else {
            
                currentAddress = (Address)Transaction.createTransaction(Transaction.UPDATE, currentAddress).execute();
                currentWarehouse = (Warehouse)Transaction.createTransaction(Transaction.UPDATE, currentWarehouse).execute();
                session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Changes to warehouse completed successfully.");
            
            }
            
        } catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to add new warehouse to the database.");
            String redirect = this.getReferer(request);
            response.sendRedirect(redirect);
            return;
        }
        
        String redirect = this.getRedirect(request);
        response.sendRedirect(redirect);
    }
    
}
