package com.cannontech.web.stars.action.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.customer.Contact;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.db.contact.ContactNotification;
import com.cannontech.database.db.customer.Address;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.roles.yukon.EnergyCompanyRole;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.util.StarsAdminUtil;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.StarsEnergyCompany;
import com.cannontech.stars.xml.serialize.StarsEnergyCompanySettings;
import com.cannontech.web.stars.action.StarsAdminActionController;

public class UpdateEnergyCompanyController extends StarsAdminActionController {

    @SuppressWarnings("unchecked")
    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response, 
            final HttpSession session, final StarsYukonUser user,
                final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        try {
            StarsEnergyCompanySettings ecSettings =
                (StarsEnergyCompanySettings) session.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
            StarsEnergyCompany ec = ecSettings.getStarsEnergyCompany();

            // Create the data object from the request parameters
            Contact contact = new Contact();

            boolean newContact = (energyCompany.getPrimaryContactID() == CtiUtilities.NONE_ZERO_ID);
            LiteContact liteContact = null;

            if (newContact) {
                contact.getContact().setContLastName( CtiUtilities.STRING_NONE );
                contact.getContact().setContFirstName( CtiUtilities.STRING_NONE );
            }
            else {
                liteContact = DaoFactory.getContactDao().getContact( energyCompany.getPrimaryContactID() );
                StarsLiteFactory.setContact( contact, liteContact, energyCompany );
            }

            ContactNotification notifPhone = null;
            ContactNotification notifFax = null;
            ContactNotification notifEmail = null;

            for (int i = 0; i < contact.getContactNotifVect().size(); i++) {
                ContactNotification notif = contact.getContactNotifVect().get(i);
                // Set all the opcode to DELETE first, then change it to UPDATE or add INSERT accordingly
                notif.setOpCode( TransactionType.DELETE );

                if (notif.getNotificationCatID().intValue() == YukonListEntryTypes.YUK_ENTRY_ID_PHONE)
                    notifPhone = notif;
                else if (notif.getNotificationCatID().intValue() == YukonListEntryTypes.YUK_ENTRY_ID_FAX)
                    notifFax = notif;
                else if (notif.getNotificationCatID().intValue() == YukonListEntryTypes.YUK_ENTRY_ID_EMAIL)
                    notifEmail = notif;
            }

            if (request.getParameter("PhoneNo").length() > 0) {
                if (notifPhone != null) {
                    notifPhone.setNotification( ServletUtils.formatPhoneNumberForStorage(request.getParameter("PhoneNo")) );
                    notifPhone.setOpCode( TransactionType.UPDATE );
                }
                else {
                    notifPhone = new ContactNotification();
                    notifPhone.setNotificationCatID( new Integer(YukonListEntryTypes.YUK_ENTRY_ID_PHONE) );
                    notifPhone.setDisableFlag( "Y" );
                    notifPhone.setNotification( ServletUtils.formatPhoneNumberForStorage(request.getParameter("PhoneNo")) );
                    notifPhone.setOpCode( TransactionType.INSERT );

                    contact.getContactNotifVect().add( notifPhone );
                }
            }

            if (request.getParameter("FaxNo").length() > 0) {
                if (notifFax != null) {
                    notifFax.setNotification( ServletUtils.formatPhoneNumberForStorage(request.getParameter("FaxNo")) );
                    notifFax.setOpCode( TransactionType.UPDATE );
                }
                else {
                    notifFax = new ContactNotification();
                    notifFax.setNotificationCatID( new Integer(YukonListEntryTypes.YUK_ENTRY_ID_FAX) );
                    notifFax.setDisableFlag( "Y" );
                    notifFax.setNotification( ServletUtils.formatPhoneNumberForStorage(request.getParameter("FaxNo")) );
                    notifFax.setOpCode( TransactionType.INSERT );

                    contact.getContactNotifVect().add( notifFax );
                }
            }

            if (request.getParameter("Email").length() > 0) {
                if (notifEmail != null) {
                    notifEmail.setNotification( request.getParameter("Email") );
                    notifEmail.setOpCode( TransactionType.UPDATE );
                }
                else {
                    notifEmail = new ContactNotification();
                    notifEmail.setNotificationCatID( new Integer(YukonListEntryTypes.YUK_ENTRY_ID_EMAIL) );
                    notifEmail.setDisableFlag( "N" );
                    notifEmail.setNotification( request.getParameter("Email") );
                    notifEmail.setOpCode( TransactionType.INSERT );

                    contact.getContactNotifVect().add( notifEmail );
                }
            }

            if (newContact) {
                Address addr = new Address();
                StarsEnergyCompany ecTemp = (StarsEnergyCompany) session.getAttribute( StarsAdminUtil.ENERGY_COMPANY_TEMP );
                if (ecTemp != null)
                    StarsFactory.setCustomerAddress( addr, ecTemp.getCompanyAddress() );
                else
                    addr.setStateCode( "" );
                contact.setAddress( addr );

                contact = Transaction.createTransaction( TransactionType.INSERT, contact ).execute();
                liteContact = new LiteContact( contact.getContact().getContactID().intValue() );
                StarsLiteFactory.setLiteContact( liteContact, contact );

                ServerUtils.handleDBChange( liteContact, DbChangeType.ADD );
            }
            else if(contact.getContact().getContLastName() != null) {
                contact = Transaction.createTransaction( TransactionType.UPDATE, contact ).execute();
                StarsLiteFactory.setLiteContact( liteContact, contact );

                ServerUtils.handleDBChange( liteContact, DbChangeType.UPDATE );
            }

            String compName = request.getParameter("CompanyName");
            if (newContact || !energyCompany.getName().equals( compName )) {
                energyCompany.setName( compName );
                energyCompany.setPrimaryContactID( contact.getContact().getContactID().intValue() );
                Transaction.createTransaction( TransactionType.UPDATE, StarsLiteFactory.createDBPersistent(energyCompany) ).execute();
            }

            int routeID = Integer.parseInt(request.getParameter("Route"));
            StarsAdminUtil.updateDefaultRoute( energyCompany, routeID, user);

            // Update energy company role DEFAULT_TIME_ZONE if necessary
            LiteYukonGroup adminGroup = energyCompany.getOperatorAdminGroup();
            boolean adminGroupUpdated = false;

            adminGroupUpdated |= DaoFactory.getRoleDao().updateGroupRoleProperty(
            		adminGroup, EnergyCompanyRole.ROLEID, EnergyCompanyRole.DEFAULT_TIME_ZONE, request.getParameter("TimeZone") );

            String[] operGroupNames = request.getParameter("OperatorGroup").split(",");
            String operGroupIDs = "";
            for (int i = 0; i < operGroupNames.length; i++) {
                String groupName = operGroupNames[i].trim();
                if (groupName.equals("")) continue;

                LiteYukonGroup group = DaoFactory.getRoleDao().getGroup( groupName );
                if (group == null)
                    throw new WebClientException( "Operator group '" + groupName + "' doesn't exist");

                if (i == 0)
                    operGroupIDs += String.valueOf( group.getGroupID() );
                else
                    operGroupIDs += "," + String.valueOf( group.getGroupID() );
            }

            if (operGroupIDs.equals(""))
                throw new WebClientException( "You must select at least one operator group" );

            adminGroupUpdated |= DaoFactory.getRoleDao().updateGroupRoleProperty(
                                                                        adminGroup, EnergyCompanyRole.ROLEID, EnergyCompanyRole.OPERATOR_GROUP_IDS, operGroupIDs );

            String[] custGroupNames = request.getParameter("CustomerGroup").split(",");
            String custGroupIDs = "";
            for (int i = 0; i < custGroupNames.length; i++) {
                String groupName = custGroupNames[i].trim();
                if (groupName.equals("")) continue;

                LiteYukonGroup group = DaoFactory.getRoleDao().getGroup( groupName );
                if (group == null)
                    throw new WebClientException( "Customer group '" + groupName + "' doesn't exist");

                if (i == 0)
                    custGroupIDs += String.valueOf( group.getGroupID() );
                else
                    custGroupIDs += "," + String.valueOf( group.getGroupID() );
            }
            
            adminGroupUpdated |= DaoFactory.getRoleDao().updateGroupRoleProperty(adminGroup, 
                                                                                 EnergyCompanyRole.ROLEID, 
                                                                                 EnergyCompanyRole.CUSTOMER_GROUP_IDS, 
                                                                                 custGroupIDs );

            adminGroupUpdated |= DaoFactory.getRoleDao().updateGroupRoleProperty(adminGroup, 
                                                                                 EnergyCompanyRole.ROLEID, 
                                                                                 EnergyCompanyRole.ADMIN_EMAIL_ADDRESS, 
                                                                                 request.getParameter("AdminEmail") );

            adminGroupUpdated |= DaoFactory.getRoleDao().updateGroupRoleProperty(adminGroup, 
                                                                                 EnergyCompanyRole.ROLEID, 
                                                                                 EnergyCompanyRole.OPTOUT_NOTIFICATION_RECIPIENTS, 
                                                                                 request.getParameter("OptOutNotif") );

            if (adminGroupUpdated)
                ServerUtils.handleDBChange( adminGroup, DbChangeType.UPDATE );

            StarsLiteFactory.setStarsEnergyCompany( ec, energyCompany );

            session.removeAttribute( StarsAdminUtil.ENERGY_COMPANY_TEMP );
            session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Energy company information updated successfully");
        }
        catch (Exception e) {
            if (e instanceof WebClientException) {
                session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, e.getMessage());
            }
            else {
                CTILogger.error( e.getMessage(), e );
                session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update energy company information");
            }
            
            String referer = this.getReferer(request);
            response.sendRedirect(referer);
            return;
        }
        
        String redirect = this.getRedirect(request);
        response.sendRedirect(redirect);
    }

}
