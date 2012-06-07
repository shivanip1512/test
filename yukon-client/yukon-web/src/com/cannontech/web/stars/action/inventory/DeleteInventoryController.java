package com.cannontech.web.stars.action.inventory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.action.DeleteLMHardwareAction;
import com.cannontech.stars.web.util.InventoryManagerUtil;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.navigation.CtiNavObject;
import com.cannontech.web.stars.action.StarsInventoryActionController;

public class DeleteInventoryController extends StarsInventoryActionController {

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response,
            final HttpSession session, final StarsYukonUser user, 
                final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        String redirect = null;
        String att_redir = ServletRequestUtils.getStringParameter(request, ServletUtils.ATT_REDIRECT);
        String refer = ServletRequestUtils.getStringParameter(request, ServletUtils.ATT_REFERRER);
        
        if (att_redir == null) att_redir = request.getContextPath() + "/operator/Consumer/Update.jsp";
        
        if (refer == null) refer = request.getContextPath() + "/operator/Consumer/Update.jsp";
        
        final StringBuilder sb = new StringBuilder();
        sb.append(request.getContextPath() + "/servlet/SOAPClient?action=DeleteLMHardware");
        sb.append("&REDIRECT=" + att_redir);
        sb.append("&REFERRER=" + refer);
        
        String redir = sb.toString();
        session.setAttribute( ServletUtils.ATT_REDIRECT, redir );
        
        int invID = ServletRequestUtils.getIntParameter(request, "InvID");
        LiteInventoryBase liteInv = starsInventoryBaseDao.getByInventoryId(invID);
        
        if (liteInv.getAccountID() == 0) {
            boolean deleteFromYukon = request.getParameter("DeleteAction") != null
                    && request.getParameter("DeleteAction").equalsIgnoreCase("DeleteFromYukon");
            
            try {
                InventoryManagerUtil.deleteInventory( liteInv, energyCompany, deleteFromYukon );
            }
            catch (Exception e) {
                CTILogger.error( e.getMessage(), e );
                if (e instanceof WebClientException) {
                    session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, e.getMessage() );
                } else {
                    session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "Failed to delete the hardware from inventory" );
                }
                String location = this.getReferer(request);
                response.sendRedirect(location);
                return;
            }
        }
        else {
            StarsOperation operation = DeleteLMHardwareAction.getRequestOperation(request);
            session.setAttribute( InventoryManagerUtil.STARS_INVENTORY_OPERATION, operation );
            redirect = (String) session.getAttribute(ServletUtils.ATT_REDIRECT);
        }
        
        /*
         * This is used when inventory is deleted, but was linked to from outside the member's inventory page, i.e 
         * the parent energy company inventory list.  Need to first logout of the member to return
         * to the parent list.
         */
        CtiNavObject nav = (CtiNavObject) session.getAttribute(ServletUtils.NAVIGATE);
        if (nav.isInternalLogin() && ! nav.isMemberECAdmin()) {
            redirect = request.getContextPath() + "/servlet/LoginController?ACTION=LOGOUT";
        }
        
        if (redirect == null) redirect = this.getRedirect(request);
        redirect = ServletUtil.createSafeRedirectUrl(request, redirect);
        response.sendRedirect(redirect);
    }
    
}
