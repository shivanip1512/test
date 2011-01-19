package com.cannontech.web.stars.action.inventory;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.action.CreateLMHardwareAction;
import com.cannontech.stars.web.action.MultiAction;
import com.cannontech.stars.web.action.UpdateLMHardwareAction;
import com.cannontech.stars.web.util.InventoryManagerUtil;
import com.cannontech.stars.xml.serialize.StarsCreateLMHardware;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsDeleteLMHardware;
import com.cannontech.stars.xml.serialize.StarsInventory;
import com.cannontech.stars.xml.serialize.StarsUpdateLMHardware;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.stars.action.StarsInventoryActionController;

public class InsertInventoryController extends StarsInventoryActionController {

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response,
            final HttpSession session, final StarsYukonUser user,
                final LiteStarsEnergyCompany energyCompany) throws Exception {
    
        String redirect = null;
        
        int invID = ServletRequestUtils.getIntParameter(request, "InvID");
        LiteInventoryBase liteInv = starsInventoryBaseDao.getByInventoryId(invID);
        Integer invNo = (Integer) session.getAttribute( InventoryManagerUtil.STARS_INVENTORY_NO );
        
        if (liteInv.getAccountID() == CtiUtilities.NONE_ZERO_ID) {
            //quick fix for the Xcel demo...need to find a more stable wizard check solution for the long term
            MultiAction wizardly = (MultiAction) session.getAttribute( ServletUtils.ATT_NEW_ACCOUNT_WIZARD );
            //if (referer.indexOf("Wizard") < 0) {
            
            if (wizardly == null) {
                // If we're trying to create/update a hardware and it's not in the wizard,
                // create/update the hardware immediately after this step. Let the user
                // update the hardware information later.
                StarsCustAccountInformation starsAcctInfo = (StarsCustAccountInformation)
                        session.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
                LiteStarsCustAccountInformation liteAcctInfo =
                    starsCustAccountInformationDao.getById(starsAcctInfo.getStarsCustomerAccount().getAccountID(),
                                                           energyCompany.getEnergyCompanyId());
                
                if (invNo == null) {
                    StarsCreateLMHardware createHw = new StarsCreateLMHardware();
                    StarsLiteFactory.setStarsInv( createHw, liteInv, energyCompany );
                    createHw.setRemoveDate( null );
                    createHw.setInstallDate( new Date() );
                    createHw.setInstallationNotes( "" );
                    
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
                    
                    // REDIRECT set in the CreateLMHardwareAction.parseResponse() method above
                    redirect = (String) session.getAttribute( ServletUtils.ATT_REDIRECT );
                }
                else {
                    StarsUpdateLMHardware updateHw = new StarsUpdateLMHardware();
                    StarsLiteFactory.setStarsInv( updateHw, liteInv, energyCompany );
                    updateHw.setRemoveDate( null );
                    updateHw.setInstallDate( new Date() );
                    updateHw.setInstallationNotes( "" );
                    
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
                    
                    // REDIRECT set in the UpdateLMHardwareAction.parseResponse() method above
                    redirect = (String) session.getAttribute( ServletUtils.ATT_REDIRECT );
                }
            }
            else {  // Inside new account wizard
                StarsInventory starsInv = StarsLiteFactory.createStarsInventory( liteInv, energyCompany );
                starsInv.setRemoveDate( null );
                starsInv.setInstallDate( new Date() );
                starsInv.setInstallationNotes( "" );
                
                session.setAttribute( InventoryManagerUtil.STARS_INVENTORY_TEMP, starsInv );
                redirect = (String) session.getAttribute( ServletUtils.ATT_REDIRECT );
            }
        }
        else {
            // The hardware is installed with another account, go to CheckInv.jsp to let the user confirm the action
            session.setAttribute(InventoryManagerUtil.INVENTORY_TO_CHECK, liteInv);
            redirect = request.getContextPath() + "/operator/Consumer/CheckInv.jsp";
        }
        
        if (redirect == null) redirect = this.getRedirect(request);
        redirect = ServletUtil.createSafeRedirectUrl(request, redirect);
        response.sendRedirect(redirect);
    }

}
