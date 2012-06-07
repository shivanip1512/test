package com.cannontech.web.stars.action.inventory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.data.lite.LiteStarsLMHardware;
import com.cannontech.stars.database.data.lite.StarsLiteFactory;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.SwitchCommandQueue;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.action.UpdateLMHardwareAction;
import com.cannontech.stars.web.action.UpdateLMHardwareConfigAction;
import com.cannontech.stars.web.action.YukonSwitchCommandAction;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsInventory;
import com.cannontech.stars.xml.serialize.StarsLMConfiguration;
import com.cannontech.web.stars.action.StarsInventoryActionController;

public class ConfigLMHardwareController extends StarsInventoryActionController {

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response,
            final HttpSession session, final StarsYukonUser user, 
                final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        try {
            int invID = ServletRequestUtils.getIntParameter(request, "InvID");
            LiteStarsLMHardware liteHw = (LiteStarsLMHardware) starsInventoryBaseDao.getByInventoryId(invID);
            
            if (request.getParameter("UseHardwareAddressing") != null) {
                StarsLMConfiguration starsCfg = new StarsLMConfiguration();
                UpdateLMHardwareConfigAction.setStarsLMConfiguration( starsCfg, request );
                UpdateLMHardwareConfigAction.updateLMConfiguration( starsCfg, liteHw, energyCompany );
            }
            
            boolean saveConfigOnly = ServletRequestUtils.getBooleanParameter(request, "SaveConfigOnly");
            if (saveConfigOnly) {
                session.setAttribute( ServletUtils.ATT_CONFIRM_MESSAGE, "Hardware configuration saved successfully." );
            }
            else {
                boolean saveToBatch = ServletRequestUtils.getBooleanParameter(request, "SaveToBatch");
                if (saveToBatch) {
                    UpdateLMHardwareConfigAction.saveSwitchCommand( liteHw, SwitchCommandQueue.SWITCH_COMMAND_CONFIGURE, energyCompany );
                    session.setAttribute( ServletUtils.ATT_CONFIRM_MESSAGE, "Hardware configuration saved to batch successfully." );
                }
                else {
                    YukonSwitchCommandAction.sendConfigCommand( energyCompany, liteHw, true, null );
                    
                    if (liteHw.getAccountID() > 0) {
                        StarsCustAccountInformation starsAcctInfo = energyCompany.getStarsCustAccountInformation( liteHw.getAccountID() );
                        if (starsAcctInfo != null) {
                            StarsInventory starsInv = StarsLiteFactory.createStarsInventory( liteHw, energyCompany );
                            UpdateLMHardwareAction.parseResponse( liteHw.getInventoryID(), starsInv, starsAcctInfo, null );
                        }
                    }
                    
                    session.setAttribute( ServletUtils.ATT_CONFIRM_MESSAGE, "Hardware configuration sent out successfully." );
                }
            }
        }
        catch (WebClientException e) {
            CTILogger.error( e.getMessage(), e );
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, e.getMessage());
        }
        
        String redirect = this.getRedirect(request);
        response.sendRedirect(redirect);
    }
    
}
