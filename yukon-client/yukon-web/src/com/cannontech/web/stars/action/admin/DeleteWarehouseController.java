package com.cannontech.web.stars.action.admin;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.db.stars.hardware.Warehouse;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.web.stars.action.StarsAdminActionController;

public class DeleteWarehouseController extends StarsAdminActionController {

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response,
            final HttpSession session, final StarsYukonUser user, 
                final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        try {
            final Integer warehouseId = ServletRequestUtils.getRequiredIntParameter(request, "WarehouseID");
            
            if (!warehouseId.equals(-1)) {
                Warehouse warehouse = new Warehouse();
                warehouse.setWarehouseID(warehouseId);
                Transaction.createTransaction(Transaction.DELETE, warehouse).execute();
            } else {
                final List<Warehouse> wareHouseList = Warehouse.getAllWarehousesForEnergyCompany(user.getEnergyCompanyID());
                for (final Warehouse warehouse : wareHouseList) {
                    Transaction.createTransaction(Transaction.DELETE, warehouse).execute();
                }
            }    
            session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Warehouse(s) successfully deleted.");
        } catch (Exception e) {
            CTILogger.error(e.getMessage(), e);
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to delete warehouse(s) from the database.");
            String redirect = this.getReferer(request);
            response.sendRedirect(redirect);
            return;
        }
        
        String redirect = this.getRedirect(request);
        response.sendRedirect(redirect);
    }
    
}
