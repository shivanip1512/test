package com.cannontech.web.stars.action.inventory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.action.CreateLMHardwareAction;
import com.cannontech.stars.web.util.InventoryManagerUtil;
import com.cannontech.stars.xml.serialize.StarsCreateLMHardware;
import com.cannontech.web.stars.action.StarsInventoryActionController;
import com.cannontech.web.stars.service.SwitchContextService;

public class CreateMCTController extends StarsInventoryActionController {
    private SwitchContextService switchContextService;
    
    public void setSwitchContextService(final SwitchContextService switchContextService) {
        this.switchContextService = switchContextService;
    }
    
    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response,
            HttpSession session, final StarsYukonUser user, 
                final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        LiteStarsEnergyCompany member = null;
        if (request.getParameter("Member") != null)
            member = this.starsDatabaseCache.getEnergyCompany( Integer.parseInt(request.getParameter("Member")) );
        else
            member = this.starsDatabaseCache.getEnergyCompany( user.getEnergyCompanyID() );
        
        ServletUtils.saveRequest(request, session, new String[] {
            "Member", "MCTType", "DeviceName", "PhysicalAddr", "MeterNumber", "MCTRoute", "DeviceLabel", "AltTrackNo", "fieldReceiveDate", "Voltage", "ServiceCompany", "Notes"});
        
        try {
            StarsCreateLMHardware createHw = new StarsCreateLMHardware();
            InventoryManagerUtil.setStarsInv( createHw, request, member );
            
            LiteInventoryBase liteInv = CreateLMHardwareAction.addInventory( createHw, null, member );
            
            if (member.getLiteID() != user.getEnergyCompanyID()) {
                this.switchContextService.switchContext(user, request, session, member.getLiteID());
                session = request.getSession( false );
            }

            // Make the "Back to List" link on the inventory details page default to Inventory.jsp
            session.removeAttribute( ServletUtils.ATT_REFERRER2 );
            
            String redirect = this.getRedirect(request) + String.valueOf( liteInv.getInventoryID() );
            response.sendRedirect(redirect);
            return;
        }
        catch (WebClientException e) {
            CTILogger.error( e.getMessage(), e );
            session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, e.getMessage() );
            String redirect = this.getReferer(request);
            response.sendRedirect(redirect);
            return;
        }
        
    }
    
}
