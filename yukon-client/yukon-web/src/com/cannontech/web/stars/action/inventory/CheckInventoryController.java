package com.cannontech.web.stars.action.inventory;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.roles.operator.ConsumerInfoRole;
import com.cannontech.stars.util.FilterWrapper;
import com.cannontech.stars.util.InventoryUtils;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.action.CreateLMHardwareAction;
import com.cannontech.stars.web.action.UpdateLMHardwareAction;
import com.cannontech.stars.web.bean.InventoryBean;
import com.cannontech.stars.web.util.InventoryManagerUtil;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.DeviceType;
import com.cannontech.stars.xml.serialize.LMHardware;
import com.cannontech.stars.xml.serialize.MCT;
import com.cannontech.stars.xml.serialize.StarsCreateLMHardware;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsDeleteLMHardware;
import com.cannontech.stars.xml.serialize.StarsInventory;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.stars.action.StarsInventoryActionController;

public class CheckInventoryController extends StarsInventoryActionController {

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response,
            final HttpSession session, final StarsYukonUser user, 
                final LiteStarsEnergyCompany energyCompany) throws Exception {
        final String action = ServletRequestUtils.getStringParameter(request, "action");

        if (action.equalsIgnoreCase("CheckInventory")) {
            session.setAttribute( ServletUtils.ATT_REDIRECT, this.getRedirect(request));
        }
        
        boolean invChecking = this.authDao.checkRoleProperty(user.getYukonUser(), ConsumerInfoRole.INVENTORY_CHECKING);
        InventoryBean iBean = (InventoryBean) session.getAttribute("inventoryBean");
        if(iBean == null)
        {
            session.setAttribute("inventoryBean", new InventoryBean());
            iBean = (InventoryBean) session.getAttribute("inventoryBean");
        }
        iBean.setCheckInvenForAccount(false);

        int devTypeID = ServletRequestUtils.getIntParameter(request, "DeviceType", 1);
        String serialNo = ServletRequestUtils.getStringParameter(request, "SerialNo");
        String deviceName = ServletRequestUtils.getStringParameter(request, "DeviceName");

        boolean deviceTypeUnknown = devTypeID == -1;
        
        if(deviceTypeUnknown) {
            /*
             * TODO: We will need a new way to find MCTs now that we removed the device type pulldown for Xcel
             * Will using the inventory filters by serial number actually return a Yukon MCT?  Will not since
             * the filters assume lmhardware.
             * UPDATED: This section never returns hardware and I do not like this approach anyway.
             * Instead I am rolling back the Xcel changes and adding the device type back in so we can find Yukon
             * MCTs.
             */
            ArrayList<FilterWrapper> tempList = new ArrayList<FilterWrapper>();
            tempList.add(new FilterWrapper(String.valueOf(YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_MEMBER), energyCompany.getName(), String.valueOf(energyCompany.getEnergyCompanyId())));
            tempList.add(new FilterWrapper(String.valueOf(YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_SERIAL_RANGE_MIN), serialNo, serialNo));
            tempList.add(new FilterWrapper(String.valueOf(YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_SERIAL_RANGE_MAX), serialNo, serialNo));
            //session.setAttribute( ServletUtils.FILTER_INVEN_LIST, tempList );
            iBean.setFilterByList(tempList);
            iBean.setCheckInvenForAccount(true);
            String redirect = request.getContextPath() + "/operator/Consumer/SelectInv.jsp";
            response.sendRedirect(redirect);
            return;
        }    
        else {
            int categoryID = InventoryUtils.getInventoryCategoryID( devTypeID, energyCompany );
            boolean isMCT = false;

            if (invChecking && categoryID > 0) {
                // Save the request parameters
                StarsInventory starsInv = (StarsInventory) StarsFactory.newStarsInv(StarsInventory.class);
                starsInv.setDeviceType( (DeviceType)StarsFactory.newStarsCustListEntry(
                                                                                       this.yukonListDao.getYukonListEntry(devTypeID), DeviceType.class) );
    
                if (InventoryUtils.isLMHardware(categoryID)) {
                    LMHardware hw = new LMHardware();
                    hw.setManufacturerSerialNumber( serialNo );
                    starsInv.setLMHardware( hw );
                }
                else if (InventoryUtils.isMCT(categoryID)) {
                    MCT mct = new MCT();
                    mct.setDeviceName( deviceName );
                    starsInv.setMCT( mct );
                    isMCT = true;
                }
    
                session.setAttribute( InventoryManagerUtil.STARS_INVENTORY_TEMP, starsInv );
            }
    
            LiteInventoryBase liteInv = null;
    
            try {
                if (categoryID > 0 && InventoryUtils.isLMHardware( categoryID )) {
                	liteInv = starsSearchDao.searchLMHardwareBySerialNumber(serialNo, energyCompany);
                    session.setAttribute( InventoryManagerUtil.INVENTORY_TO_CHECK, liteInv );
                }
                else if (categoryID > 0) {
                    liteInv = starsSearchDao.searchForDevice(categoryID, deviceName, energyCompany);
                    session.setAttribute( InventoryManagerUtil.INVENTORY_TO_CHECK, liteInv );
                }
            }
            catch (ObjectInOtherEnergyCompanyException e) {
                String redirect;
    
                if (action.equalsIgnoreCase("CreateLMHardware") ||
                        action.equalsIgnoreCase("UpdateLMHardware"))
                {
                    session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE,
                                          "The hardware or device already exists in the inventory list of <i>" + e.getYukonEnergyCompany().getName() + "</i>." );
                    redirect = this.getReferer(request);
                }
                else {
                    session.setAttribute( InventoryManagerUtil.INVENTORY_TO_CHECK, e );
                    redirect = request.getContextPath() + "/operator/Consumer/CheckInv.jsp";
                }
    
                response.sendRedirect(redirect);
                return;
            }

            try {
                if (action.equalsIgnoreCase("CreateLMHardware")) {
                    // Request from CreateHardware.jsp, no inventory checking
                    ServletUtils.saveRequest(request, session, new String[]
                                                                          {"DeviceType", "SerialNo", 
                            "DeviceLabel", "AltTrackNo", "fieldReceiveDate", "FieldRemoveDate", "Voltage", "Notes", "InstallDate", "ServiceCompany", "InstallNotes", "Route"} );
    
                    StarsCreateLMHardware createHw = new StarsCreateLMHardware();
                    InventoryManagerUtil.setStarsInv( createHw, request, energyCompany );

                    if (isMCT) {
                        if (liteInv != null) {
                            if (request.getParameter("CreateMCT") != null) {
                                session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "The specified device name already exists");
                                String redirect = this.getReferer(request);
                                response.sendRedirect(redirect);
                                return;
                            }
        
                            createHw.setInventoryID( liteInv.getInventoryID() );
                            createHw.setDeviceID( liteInv.getDeviceID() );
                        }
                        else if (request.getParameter("CreateMCT") == null) {
                            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "The device name is not found. To create a new device, check the \"Create new device\".");
                            String redirect = this.getReferer(request);
                            response.sendRedirect(redirect);
                            return;
                        }
                    }
    
                    StarsCustAccountInformation starsAcctInfo = (StarsCustAccountInformation)
                    session.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
                    LiteStarsCustAccountInformation liteAcctInfo =
                        starsCustAccountInformationDao.getById(starsAcctInfo.getStarsCustomerAccount().getAccountID(),
                                                               energyCompany.getEnergyCompanyId());
    
                    try {
                        liteInv = CreateLMHardwareAction.addInventory( createHw, liteAcctInfo, energyCompany );
                    }
                    catch (WebClientException e) {
                        CTILogger.error( e.getMessage(), e );
                        session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, e.getMessage() );
                        String redirect = this.getReferer(request);
                        response.sendRedirect(redirect);
                        return;
                    }
    
                    session.removeAttribute( ServletUtils.ATT_REDIRECT );
                    StarsInventory starsInv = StarsLiteFactory.createStarsInventory( liteInv, energyCompany );
                    CreateLMHardwareAction.parseResponse( createHw, starsInv, starsAcctInfo, session );
    
                    // REDIRECT set in the CreateLMHardwareAction.parseResponse() method above
                    String redirect = (String) session.getAttribute( ServletUtils.ATT_REDIRECT );
                    redirect = ServletUtil.createSafeRedirectUrl(request, redirect);
                    response.sendRedirect(redirect);
                    return;
                }
                else if (action.equalsIgnoreCase("UpdateLMHardware")) {
                    // Request from Inventory.jsp, device type or serial # must have been changed
                    StarsOperation operation = UpdateLMHardwareAction.getRequestOperation(request, energyCompany );
                    StarsDeleteLMHardware deleteHw = new StarsDeleteLMHardware();
                    deleteHw.setInventoryID(ServletRequestUtils.getIntParameter(request, "OrigInvID"));
                    operation.setStarsDeleteLMHardware( deleteHw );
                    session.setAttribute( InventoryManagerUtil.STARS_INVENTORY_OPERATION, operation );
    
                    if (liteInv != null) {
                        operation.getStarsUpdateLMHardware().setInventoryID( liteInv.getInventoryID() );
                        operation.getStarsUpdateLMHardware().setDeviceID( liteInv.getDeviceID() );
                    }
    
                    // Forward to DeleteInv.jsp to confirm removal of the old hardware
                    LiteInventoryBase liteInvOld = starsInventoryBaseDao.getByInventoryId(deleteHw.getInventoryID());
                    session.setAttribute( InventoryManagerUtil.INVENTORY_TO_DELETE, liteInvOld );
                    String redirect = request.getContextPath() + "/operator/Consumer/DeleteInv.jsp";
                    response.sendRedirect(redirect);
                    return;
                }
                else {
                    String redirect = request.getContextPath() + "/operator/Consumer/CheckInv.jsp";
                    response.sendRedirect(redirect);
                    return;
                }
            }
            catch (WebClientException e) {
                session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, e.getMessage() );
                String redirect = this.getReferer(request);
                response.sendRedirect(redirect);
                return;
            }
        }
    }

}
