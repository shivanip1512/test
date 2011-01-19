package com.cannontech.web.stars.action.inventory;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.data.stars.hardware.MeterHardwareBase;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.action.CreateLMHardwareAction;
import com.cannontech.stars.web.action.UpdateLMHardwareAction;
import com.cannontech.stars.web.bean.NonYukonMeterBean;
import com.cannontech.stars.xml.serialize.StarsCreateLMHardware;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsInventory;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.stars.action.StarsInventoryActionController;

public class MeterProfileSaveController extends StarsInventoryActionController {

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response,
            HttpSession session, final StarsYukonUser user, 
                final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        String redirect = null;
        
        NonYukonMeterBean mBean = (NonYukonMeterBean) session.getAttribute("meterBean");
        MeterHardwareBase currentMeter = mBean.getCurrentMeter();
        String accountDestination = new String();
        boolean success = false;
        boolean isNew = false;
        
        currentMeter.getMeterHardwareBase().setMeterNumber(request.getParameter("MeterNumber"));
        currentMeter.getMeterHardwareBase().setMeterTypeID(new Integer(request.getParameter("MeterType")));
        currentMeter.getInventoryBase().setAlternateTrackingNumber(request.getParameter("AltTrackNo"));
        currentMeter.getInventoryBase().setDeviceLabel(request.getParameter("DeviceLabel"));
        currentMeter.getInventoryBase().setVoltageID(new Integer(request.getParameter("Voltage")));
        currentMeter.getInventoryBase().setNotes(request.getParameter("Notes"));
        YukonListEntry categoryEntry = mBean.getEnergyCompany().getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_INV_CAT_NON_YUKON_METER);
        if( categoryEntry != null)
            currentMeter.getInventoryBase().setCategoryID(categoryEntry.getEntryID());
        
        try
        {
            //new meter
            if(mBean.getCurrentMeterID() == -1)
            {
                currentMeter.setInventoryID(null);
                currentMeter.setAccountID(mBean.getCurrentAccountID());
                currentMeter.setEnergyCompanyID(mBean.getEnergyCompany().getEnergyCompanyId());
                Transaction.createTransaction(Transaction.INSERT, currentMeter).execute();
                session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "New meter added to inventory and this account.");
                accountDestination = request.getContextPath() + "/operator/Consumer/Update.jsp";
                success = true;
                isNew = true;
            }
            else
            {
                Transaction.createTransaction(Transaction.UPDATE, currentMeter).execute();
                session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Meter successfully updated in the database.");
                accountDestination = request.getContextPath() + "/operator/Consumer/MeterProfile.jsp?MetRef=" + currentMeter.getInventoryBase().getInventoryID().toString();
                success = true;
            }
            
            /**
             * Switch mapping to non-Yukon dumb meters
             */
            String[] assignedSwitchIDs = request.getParameterValues("SwitchIDs");
            
            boolean deleteSuccess = MeterHardwareBase.deleteAssignedSwitches(currentMeter.getInventoryBase().getInventoryID());
                        
            if(assignedSwitchIDs != null && assignedSwitchIDs.length > 0)
            {
                if(deleteSuccess)
                {
                    for(int j = 0; j < assignedSwitchIDs.length; j++)
                    {
                        boolean updateSuccess = MeterHardwareBase.updateAssignedSwitch(Integer.parseInt(assignedSwitchIDs[j]), currentMeter.getInventoryBase().getInventoryID().intValue());
                        
                        //something wrong
                        if(!updateSuccess)
                        {
                            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Switch assignment to this meter failed.");
                            break;
                        }
                    }
                }
                else
                {
                    session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Could not delete previous switch assignment to this meter.  Assignment not updated.");
                }
            }
            
        }
        catch (TransactionException e) 
        {
            CTILogger.error( e.getMessage(), e );
            e.printStackTrace();
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Meter information could not be saved to the database.  Transaction failed.");
        }
        
        if(success)
        {
            /*LINE OF DEATH*/
            LiteInventoryBase liteInv = (LiteInventoryBase)StarsLiteFactory.createLite(currentMeter);
           /* if(liteInv.getDeviceLabel().length() < 1)
                liteInv.setDeviceLabel(current)*/
            
            StarsCustAccountInformation starsAcctInfo = (StarsCustAccountInformation)
                    session.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
            LiteStarsCustAccountInformation liteAcctInfo = 
                starsCustAccountInformationDao.getById(starsAcctInfo.getStarsCustomerAccount().getAccountID(),
                                                       mBean.getEnergyCompany().getEnergyCompanyId());
            
            if (isNew) 
            {
                StarsCreateLMHardware createHw = new StarsCreateLMHardware();
                StarsLiteFactory.setStarsInv( createHw, liteInv, mBean.getEnergyCompany() );
                createHw.setRemoveDate( null );
                createHw.setInstallDate( new Date() );
                createHw.setInstallationNotes( "" );
                        
                try 
                {
                    liteInv = CreateLMHardwareAction.addInventory( createHw, liteAcctInfo, mBean.getEnergyCompany() );
                }
                catch (WebClientException e) 
                {
                    CTILogger.error( e.getMessage(), e );
                    session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, e.getMessage() );
                    String location = request.getContextPath() + "/operator/Consumer/MeterProfile.jsp?MetRef=-1";
                    response.sendRedirect(location);
                    return;
                }
                        
                session.removeAttribute( ServletUtils.ATT_REDIRECT );
                StarsInventory starsInv = StarsLiteFactory.createStarsInventory( liteInv, mBean.getEnergyCompany() );
                starsInv.setMeterNumber(currentMeter.getMeterHardwareBase().getMeterNumber());
                CreateLMHardwareAction.parseResponse( createHw, starsInv, starsAcctInfo, session );
                
                // REDIRECT set in the CreateLMHardwareAction.parseResponse() method above
                redirect = (String) session.getAttribute( ServletUtils.ATT_REDIRECT );
            }
            else 
            {
                session.setAttribute( ServletUtils.ATT_REDIRECT,  request.getContextPath() + "/operator/Hardware/MeterProfile.jsp?MetRef=" + currentMeter.getInventoryBase().getInventoryID().toString() );
                StarsInventory starsInv = StarsLiteFactory.createStarsInventory(liteInv, mBean.getEnergyCompany());
                starsInv.setMeterNumber(currentMeter.getMeterHardwareBase().getMeterNumber());
                UpdateLMHardwareAction.parseResponse(currentMeter.getInventoryBase().getInventoryID(), starsInv, starsAcctInfo, session);
            }

            /*LINE OF DEATH*/
            DBChangeMsg dbChangeMessage = new DBChangeMsg(
                  mBean.getCurrentAccountID(),
                  DBChangeMsg.CHANGE_CUSTOMER_ACCOUNT_DB,
                  DBChangeMsg.CAT_CUSTOMER_ACCOUNT,
                  DBChangeMsg.CAT_CUSTOMER_ACCOUNT,
                  DbChangeType.UPDATE
            );
            dbChangeMessage.setSource("STARS Self-Message");
            ServerUtils.handleDBChangeMsg(dbChangeMessage);
        }
        
        if (this.getReferer(request).contains("Consumer")) {
            redirect = accountDestination;
        } else {
            redirect = request.getContextPath() + "/operator/Hardware/MeterProfile.jsp?MetRef=" + currentMeter.getInventoryBase().getInventoryID().toString();
        }
        redirect = ServletUtil.createSafeRedirectUrl(request, redirect);
        response.sendRedirect(redirect);
    }
    
}
