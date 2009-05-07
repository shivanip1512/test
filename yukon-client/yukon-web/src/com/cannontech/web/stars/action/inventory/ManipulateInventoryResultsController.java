package com.cannontech.web.stars.action.inventory;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.bean.InventoryBean;
import com.cannontech.web.stars.action.StarsInventoryActionController;

public class ManipulateInventoryResultsController extends StarsInventoryActionController {

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response,
            final HttpSession session, final StarsYukonUser user, 
                final LiteStarsEnergyCompany energyCompany) throws Exception {

        InventoryBean iBean = (InventoryBean) session.getAttribute("inventoryBean");
        List<LiteInventoryBase> iBeanInventoryList = iBean.getInventoryList(request);
        List<LiteInventoryBase> inventoryList = Collections.unmodifiableList(iBeanInventoryList);

        iBean.setInventoryList(inventoryList);
        iBean.setNumberOfRecords(String.valueOf((iBeanInventoryList.size())));

        String redirect = request.getContextPath() + "/operator/Hardware/ChangeInventory.jsp";
        response.sendRedirect(redirect);
    }
    
}
