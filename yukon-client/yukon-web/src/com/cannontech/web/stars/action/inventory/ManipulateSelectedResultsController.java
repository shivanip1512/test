package com.cannontech.web.stars.action.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.bean.InventoryBean;
import com.cannontech.web.stars.action.StarsInventoryActionController;

public class ManipulateSelectedResultsController extends StarsInventoryActionController {
    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response,
            final HttpSession session, final StarsYukonUser user, 
                final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        String[] selections = request.getParameterValues("checkMultiInven");
        if( selections == null)//none selected
        {
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "There are no individual Inventories checked to manipulate.");
            String redirect = this.getReferer(request);
            response.sendRedirect(redirect);
            return;
        }
        int [] selectionIDs = new int[selections.length];
        for ( int i = 0; i < selections.length; i++)
            selectionIDs[i] = Integer.valueOf(selections[i]).intValue();

        InventoryBean inventoryBean = (InventoryBean) session.getAttribute("inventoryBean");
        List<LiteInventoryBase> inventoryList = inventoryBean.getInventoryList(request);
        Map<Integer, LiteInventoryBase> inventoryIdMap = toInventoryIdMap(inventoryList);

        List<LiteInventoryBase> selectedInventoryList = new ArrayList<LiteInventoryBase>(inventoryIdMap.size());

        for (final Integer selectionId : selectionIDs) {
            LiteInventoryBase inventory = inventoryIdMap.get(selectionId);
            if (inventory != null) selectedInventoryList.add(inventory);
        }

        inventoryBean.setInventoryList(selectedInventoryList);
        inventoryBean.setNumberOfRecords(inventoryList.size());

        String redirect = request.getContextPath() + "/operator/Hardware/ChangeInventory.jsp";
        response.sendRedirect(redirect);
    }
    
    private Map<Integer, LiteInventoryBase> toInventoryIdMap(List<LiteInventoryBase> list) {
        Map<Integer, LiteInventoryBase> resultMap = new HashMap<Integer, LiteInventoryBase>(list.size());

        for (final LiteInventoryBase value : list) {
            Integer key = value.getInventoryID();
            resultMap.put(key, value);
        }
        
        return resultMap;
    }
}
