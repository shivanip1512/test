package com.cannontech.web.stars.action.inventory;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.util.FilterWrapper;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.bean.InventoryBean;
import com.cannontech.web.stars.action.StarsInventoryActionController;

public class UpdateFiltersController extends StarsInventoryActionController {
    private boolean hwSelection;
    
    public void setHwSelection(final boolean hwSelection) {
        this.hwSelection = hwSelection;
    }
    
    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response,
            HttpSession session, final StarsYukonUser user, 
                final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        String[] selectionIDs = request.getParameterValues("SelectionIDs");
        String[] filterTexts = request.getParameterValues("FilterTexts");
        String[] yukonDefIDs = request.getParameterValues("YukonDefIDs");
        ArrayList<FilterWrapper> filters = new ArrayList<FilterWrapper>();
        InventoryBean iBean = (InventoryBean) session.getAttribute("inventoryBean");
        
        if(filterTexts == null)
        {
            //session.setAttribute("inventoryBean", iBean);
            session.setAttribute( ServletUtils.FILTER_INVEN_LIST, filters );
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "There are no filters defined.");
            String redirect = this.getReferer(request);
            response.sendRedirect(redirect);
            return;
        }
        
        for(int j = 0; j < filterTexts.length; j++)
        {
            FilterWrapper wrapper = new FilterWrapper(yukonDefIDs[j], filterTexts[j], selectionIDs[j] );
            filters.add(wrapper);
        }
        
        iBean.setFilterByList(filters);
        iBean.setViewResults(false);
        iBean.setCheckInvenForAccount(false);
        iBean.setPage(1);
        session.setAttribute("inventoryBean", iBean);
        session.setAttribute( ServletUtils.FILTER_INVEN_LIST, filters );
        
        String redirect = null;
        if (this.hwSelection) {
            redirect = request.getContextPath() + "/operator/Consumer/SelectInv.jsp";
        } else {    
            redirect = request.getContextPath() + "/operator/Hardware/Inventory.jsp";
        }
        response.sendRedirect(redirect);
    }
    
}
