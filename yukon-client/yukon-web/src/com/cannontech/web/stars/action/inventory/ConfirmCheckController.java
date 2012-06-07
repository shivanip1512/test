package com.cannontech.web.stars.action.inventory;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.database.data.lite.LiteStarsCustAccountInformation;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.data.lite.StarsLiteFactory;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.action.CreateLMHardwareAction;
import com.cannontech.stars.web.action.UpdateLMHardwareAction;
import com.cannontech.stars.web.util.InventoryManagerUtil;
import com.cannontech.stars.xml.serialize.StarsCreateLMHardware;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsDeleteLMHardware;
import com.cannontech.stars.xml.serialize.StarsInventory;
import com.cannontech.stars.xml.serialize.StarsUpdateLMHardware;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.stars.action.StarsInventoryActionController;

public class ConfirmCheckController extends StarsInventoryActionController {

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response,
            final HttpSession session, final StarsYukonUser user, 
                final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        String redirect = null;
        LiteInventoryBase liteInv = (LiteInventoryBase) session.getAttribute( InventoryManagerUtil.INVENTORY_TO_CHECK );
        Integer invNo = (Integer) session.getAttribute( InventoryManagerUtil.STARS_INVENTORY_NO );
        
        String referer = (String) session.getAttribute( ServletUtils.ATT_REFERRER );
        if (referer.indexOf("Wizard") < 0) {
            StarsCustAccountInformation starsAcctInfo = (StarsCustAccountInformation)
                    session.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
            LiteStarsCustAccountInformation liteAcctInfo = 
                starsCustAccountInformationDao.getById(starsAcctInfo.getStarsCustomerAccount().getAccountID(),
                                                       energyCompany.getEnergyCompanyId());
            
            if (invNo == null) {
                StarsCreateLMHardware createHw = new StarsCreateLMHardware();
                if (liteInv != null) {
                    StarsLiteFactory.setStarsInv( createHw, liteInv, energyCompany );
                    createHw.setRemoveDate( null );
                    createHw.setInstallDate( new Date() );
                    createHw.setInstallationNotes( "" );
                }
                else {
                    StarsInventory starsInv = (StarsInventory) session.getAttribute( InventoryManagerUtil.STARS_INVENTORY_TEMP );
                    if (starsInv.getMCT() != null) {
                        // To create a MCT, we need more information
                        String location = (String) session.getAttribute(ServletUtils.ATT_REDIRECT);
                        location = ServletUtil.createSafeRedirectUrl(request, location);
                        response.sendRedirect(location);
                        return;
                    }
                    
                    createHw.setInventoryID( -1 );
                    createHw.setDeviceType( starsInv.getDeviceType() );
                    createHw.setLMHardware( starsInv.getLMHardware() );
                    createHw.setRemoveDate( null );
                    createHw.setInstallDate( new Date() );
                    createHw.setInstallationNotes( "" );
                }
                
                try {
                    liteInv = CreateLMHardwareAction.addInventory( createHw, liteAcctInfo, energyCompany );
                }
                catch (WebClientException e) {
                    CTILogger.error( e.getMessage(), e );
                    session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, e.getMessage() );
                    String location = request.getContextPath() + "/operator/Consumer/SerialNumber.jsp?action=New";
                    response.sendRedirect(location);
                    return;
                }
                
                session.removeAttribute( ServletUtils.ATT_REDIRECT );
                StarsInventory starsInv = StarsLiteFactory.createStarsInventory( liteInv, energyCompany );
                CreateLMHardwareAction.parseResponse( createHw, starsInv, starsAcctInfo, session );

                /* New hardware has been added to the customer.  
                Refreshing the customer account info in the session.
                */
                session.setAttribute(ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO, liteAcctInfo);

                // REDIRECT set in the CreateLMHardwareAction.parseResponse() method above
                redirect = (String) session.getAttribute( ServletUtils.ATT_REDIRECT );
            }
            else {
                StarsUpdateLMHardware updateHw = new StarsUpdateLMHardware();
                if (liteInv != null) {
                    StarsLiteFactory.setStarsInv( updateHw, liteInv, energyCompany );
                    updateHw.setRemoveDate( null );
                    updateHw.setInstallDate( new Date() );
                    updateHw.setInstallationNotes( "" );
                }
                else {
                    StarsInventory starsInv = (StarsInventory) session.getAttribute( InventoryManagerUtil.STARS_INVENTORY_TEMP );
                    if (starsInv.getMCT() != null) {
                        // To create a MCT, we need more information
                        String location = request.getContextPath() + "/operator/Consumer/CreateHardware.jsp?InvNo=" + invNo;
                        response.sendRedirect(location);
                        return;
                    }
                    
                    updateHw.setInventoryID( -1 );
                    updateHw.setDeviceType( starsInv.getDeviceType() );
                    updateHw.setLMHardware( starsInv.getLMHardware() );
                    updateHw.setRemoveDate( null );
                    updateHw.setInstallDate( new Date() );
                    updateHw.setInstallationNotes( "" );
                }
                
                int origInvID = starsAcctInfo.getStarsInventories().getStarsInventory( invNo.intValue() ).getInventoryID();
                StarsDeleteLMHardware deleteHw = new StarsDeleteLMHardware();
                deleteHw.setInventoryID( origInvID );
                
                try {
                    liteInv = UpdateLMHardwareAction.updateInventory( updateHw, deleteHw, liteAcctInfo, user );
                }
                catch (WebClientException e) {
                    CTILogger.error( e.getMessage(), e );
                    session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, e.getMessage() );
                    String location = request.getContextPath() + "/operator/Consumer/SerialNumber.jsp?InvNo="  + invNo;
                    response.sendRedirect(location);
                    return;
                }
                
                session.setAttribute( ServletUtils.ATT_REDIRECT, request.getContextPath() + "/operator/Consumer/Inventory.jsp?InvNo=" + invNo );
                StarsInventory starsInv = StarsLiteFactory.createStarsInventory(liteInv, energyCompany);
                UpdateLMHardwareAction.parseResponse(origInvID, starsInv, starsAcctInfo, session);
                
                /* New hardware has been added to the customer.  
                Refreshing the customer account info in the session.
                */
                session.setAttribute(ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO, liteAcctInfo);

                // REDIRECT set in the UpdateLMHardwareAction.parseResponse() method above
                redirect = (String) session.getAttribute( ServletUtils.ATT_REDIRECT );
            }
        }
        else {
            StarsInventory starsInv = null;
            if (liteInv != null) {
                // This is an existing hardware/device
                starsInv = StarsLiteFactory.createStarsInventory( liteInv, energyCompany );
            }
            else {
                // This is a LM hardware to be added to the inventory
                starsInv = (StarsInventory) session.getAttribute( InventoryManagerUtil.STARS_INVENTORY_TEMP );
            }
            
            starsInv.setRemoveDate( null );
            starsInv.setInstallDate( new Date() );
            starsInv.setInstallationNotes( "" );
            session.setAttribute( InventoryManagerUtil.STARS_INVENTORY_TEMP + invNo, starsInv );
            
            redirect = (String) session.getAttribute(ServletUtils.ATT_REDIRECT);
        }
        
        if (redirect == null) redirect = this.getRedirect(request);
        redirect = ServletUtil.createSafeRedirectUrl(request, redirect);
        response.sendRedirect(redirect);
    }
    
}
