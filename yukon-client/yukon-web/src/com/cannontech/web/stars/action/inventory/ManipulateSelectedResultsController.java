package com.cannontech.web.stars.action.inventory;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.bean.InventoryBean;
import com.cannontech.web.stars.action.StarsInventoryActionController;

public class ManipulateSelectedResultsController extends StarsInventoryActionController {
    private ManipulateInventoryResultsController manipulateInventoryResults;
    
    public void setManipulateInventoryResults(final ManipulateInventoryResultsController manipulateInventoryResults) {
        this.manipulateInventoryResults = manipulateInventoryResults;
    }
    
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
        
        InventoryBean iBean = (InventoryBean) session.getAttribute("inventoryBean");
        List<LiteInventoryBase> inventoryList = new ArrayList<LiteInventoryBase>(); 
        for ( int i = 0; i < iBean.getInventoryList().size(); i ++)
        {
            LiteInventoryBase liteInvBase = iBean.getInventoryList().get(i);

            if (liteInvBase != null) {
                for (int j = 0; j < selectionIDs.length; j++)
                {
                    if( liteInvBase.getInventoryID() == selectionIDs[j])
                    {
                        inventoryList.add(iBean.getInventoryList().get(i));
                        break;
                    }
                }   
            }
        }

        iBean.setInventoryList(inventoryList);
        iBean.setNumberOfRecords(String.valueOf((iBean.getInventoryList().size())));
        //session.setAttribute("inventoryBean", iBean);

        this.manipulateInventoryResults.doAction(request, response, session, user, energyCompany);
    }
    
}
