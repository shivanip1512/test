package com.cannontech.web.stars.action.inventory;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.util.InventoryManagerUtil;
import com.cannontech.web.stars.action.StarsInventoryActionController;

public class SearchInventoryController extends StarsInventoryActionController {

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response,
            final HttpSession session, final StarsYukonUser user, 
                final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        session.removeAttribute( InventoryManagerUtil.INVENTORY_SET );
        
        int searchBy = ServletRequestUtils.getIntParameter(request, "SearchBy");
        String searchValue = request.getParameter( "SearchValue" );
        
        if (searchValue.trim().length() == 0) {
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Search value cannot be empty");
            String redirect = this.getRedirect(request);
            response.sendRedirect(redirect);
            return;
        }
        
        // Remember the last search option
        session.setAttribute( ServletUtils.ATT_LAST_INVENTORY_SEARCH_OPTION, new Integer(searchBy) );
        session.setAttribute( ServletUtils.ATT_LAST_INVENTORY_SEARCH_VALUE, new String(searchValue) );

        boolean searchMembers = rolePropertyDao.checkProperty(YukonRoleProperty.ADMIN_MANAGE_MEMBERS, user.getYukonUser())
                && (energyCompany.hasChildEnergyCompanies());

        List<LiteInventoryBase> invList = null;
        try {
            invList = InventoryManagerUtil.searchInventory( energyCompany, searchBy, searchValue, searchMembers ); 
        }
        catch (WebClientException e) {
            session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, e.getMessage() );
            String redirect = this.getRedirect(request);
            response.sendRedirect(redirect);
            return;
        }
        
        if (invList == null || invList.size() == 0) {
            session.setAttribute(InventoryManagerUtil.INVENTORY_SET_DESC, "<div class='ErrorMsg' align='center'>No hardware found matching the search criteria.</div>");
        }
        else {
            LiteInventoryBase liteInv = null;
            if (invList.size() == 1 && invList.get(0).getEnergyCompanyId() == user.getEnergyCompanyID()) {
            	liteInv = invList.get(0);
            	String redirect;
            	if (liteInv.getAccountID() == 0 ) {
            	    redirect = request.getContextPath() + "/operator/Hardware/InventoryDetail.jsp?InvId=" + liteInv.getInventoryID() + "&src=Search";
            	} else {
            	    redirect = request.getContextPath() + "/spring/stars/operator/hardware/edit?accountId=" + liteInv.getAccountID() + "&inventoryId=" + liteInv.getInventoryID();
            	}

                response.sendRedirect(redirect);
                return;
            }
            
            session.setAttribute(InventoryManagerUtil.INVENTORY_SET, invList);
            session.setAttribute(InventoryManagerUtil.INVENTORY_SET_DESC, "Click on a serial # (device name) to view the hardware details, or click on an account # (if available) to view the account information.");
            session.setAttribute(ServletUtils.ATT_REFERRER, this.getReferer(request));
        }
        
        String redirect = this.getRedirect(request);
        response.sendRedirect(redirect);
    }
    
}
