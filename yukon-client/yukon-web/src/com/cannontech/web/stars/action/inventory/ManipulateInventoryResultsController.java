package com.cannontech.web.stars.action.inventory;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.bean.InventoryBean;
import com.cannontech.web.stars.action.StarsInventoryActionController;

public class ManipulateInventoryResultsController extends StarsInventoryActionController {

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response,
            final HttpSession session, final StarsYukonUser user, 
                final LiteStarsEnergyCompany energyCompany) throws Exception {

        InventoryBean inventoryBean = (InventoryBean) session.getAttribute("inventoryBean");
        List<LiteInventoryBase> inventoryList = inventoryBean.getInventoryList(request);
        List<LiteInventoryBase> selectedInventoryList = Collections.unmodifiableList(inventoryList);

        inventoryBean.setInventoryList(selectedInventoryList);
        inventoryBean.setNumberOfRecords(inventoryList.size());

        String redirect = request.getContextPath() + "/operator/Hardware/ChangeInventory.jsp";
        response.sendRedirect(redirect);
    }
}
