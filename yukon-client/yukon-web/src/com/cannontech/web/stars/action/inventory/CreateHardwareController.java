package com.cannontech.web.stars.action.inventory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.data.lite.LiteStarsLMHardware;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.action.CreateLMHardwareAction;
import com.cannontech.stars.web.action.UpdateLMHardwareConfigAction;
import com.cannontech.stars.web.util.InventoryManagerUtil;
import com.cannontech.stars.xml.serialize.StarsCreateLMHardware;
import com.cannontech.stars.xml.serialize.StarsLMConfiguration;
import com.cannontech.web.stars.action.StarsInventoryActionController;
import com.cannontech.web.stars.service.SwitchContextService;


public class CreateHardwareController extends StarsInventoryActionController {
    private SwitchContextService switchContextService;
    
    public void setSwitchContextService(SwitchContextService switchContextService) {
        this.switchContextService = switchContextService;
    }

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response,
            HttpSession session, final StarsYukonUser user, 
                final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        String memberParam = ServletRequestUtils.getStringParameter(request, "Member");
        LiteStarsEnergyCompany member = (memberParam != null) ? 
                starsDatabaseCache.getEnergyCompany(Integer.valueOf(memberParam)) : energyCompany;
        
        ServletUtils.saveRequest(request, session, new String[] {
            "Member", "DeviceType", "SerialNo", "DeviceLabel", "AltTrackNo", "ReceiveDate", "Voltage", "ServiceCompany", "Notes", "Route", 
            "yukonDeviceDemandRate", "yukonDeviceCreationStyleRadio", "yukonDeviceName", "choosenYukonDeviceId", "choosenYukonDeviceNameField"});
        
        try {
            StarsCreateLMHardware createHw = new StarsCreateLMHardware();
            InventoryManagerUtil.setStarsInv( createHw, request, member );
            
            try {
                LiteInventoryBase existingHw = starsSearchDao.searchLMHardwareBySerialNumber(createHw.getLMHardware().getManufacturerSerialNumber(), energyCompany);
                if (existingHw != null)
                    throw new WebClientException("Cannot create hardware: serial # already exists.");
            }
            catch (ObjectInOtherEnergyCompanyException e) {
                throw new WebClientException("Cannot create hardware: serial # already exists in the inventory list of <i>" + e.getYukonEnergyCompany().getName() + "</i>.");
            }
            
            LiteInventoryBase liteInv = CreateLMHardwareAction.addInventory( createHw, null, member );
            
            if (request.getParameter("UseHardwareAddressing") != null) {
                StarsLMConfiguration starsCfg = new StarsLMConfiguration();
                UpdateLMHardwareConfigAction.setStarsLMConfiguration( starsCfg, request);
                UpdateLMHardwareConfigAction.updateLMConfiguration( starsCfg, (LiteStarsLMHardware)liteInv, member );
            }
            
            if (member.getLiteID() != user.getEnergyCompanyID()) {
                switchContextService.switchContext( user, request, session, member.getLiteID() );
                session = request.getSession( false );
            }
            
            // Append inventory ID to the redirect URL
            String redirect = this.getRedirect(request);
            redirect += String.valueOf( liteInv.getInventoryID() );
            
            // Make the "Back to List" link on the inventory details page default to Inventory.jsp
            session.removeAttribute( ServletUtils.ATT_REFERRER2 );
            response.sendRedirect(redirect);
        }
        catch (WebClientException e) {
            CTILogger.error( e.getMessage(), e );
            session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, e.getMessage() );
            String redirect = this.getReferer(request);
            response.sendRedirect(redirect);
        }
    }
}
