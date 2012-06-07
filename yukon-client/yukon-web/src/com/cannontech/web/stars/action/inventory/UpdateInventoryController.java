package com.cannontech.web.stars.action.inventory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.data.lite.LiteStarsLMHardware;
import com.cannontech.stars.database.data.lite.StarsLiteFactory;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.action.UpdateLMHardwareAction;
import com.cannontech.stars.web.action.UpdateLMHardwareConfigAction;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsInventory;
import com.cannontech.stars.xml.serialize.StarsLMConfiguration;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.web.stars.action.StarsInventoryActionController;

public class UpdateInventoryController extends StarsInventoryActionController {

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response,
            final HttpSession session, final StarsYukonUser user, 
                final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        try {
            int invID = ServletRequestUtils.getIntParameter(request, "InvID");
            LiteInventoryBase liteInv = starsInventoryBaseDao.getByInventoryId(invID);
            
            StarsOperation operation = UpdateLMHardwareAction.getRequestOperation(request, energyCompany);
            UpdateLMHardwareAction.updateInventory( operation.getStarsUpdateLMHardware(), liteInv, energyCompany );
            
            if (request.getParameter("UseHardwareAddressing") != null) {
                StarsLMConfiguration starsCfg = new StarsLMConfiguration();
                UpdateLMHardwareConfigAction.setStarsLMConfiguration( starsCfg, request );
                UpdateLMHardwareConfigAction.updateLMConfiguration( starsCfg, (LiteStarsLMHardware)liteInv, energyCompany );
            }
            
            if (liteInv.getAccountID() > 0) {
                StarsCustAccountInformation starsAcctInfo = energyCompany.getStarsCustAccountInformation( liteInv.getAccountID() );
                if (starsAcctInfo != null) {
                    StarsInventory starsInv = StarsLiteFactory.createStarsInventory( liteInv, energyCompany );
                    UpdateLMHardwareAction.parseResponse( liteInv.getInventoryID(), starsInv, starsAcctInfo, null );
                }
            }
            
            session.setAttribute( ServletUtils.ATT_CONFIRM_MESSAGE, "Hardware information updated successfully" );
        }
        catch (WebClientException e) {
            CTILogger.error( e.getMessage(), e );
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, e.getMessage());
        }
        catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update hardware information");
        }

        String redirect = this.getRedirect(request);
        response.sendRedirect(redirect);
    }
    
}
