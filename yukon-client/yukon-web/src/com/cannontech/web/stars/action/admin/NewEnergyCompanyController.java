package com.cannontech.web.stars.action.admin;

import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.Transaction;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompanyFactory;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.roles.operator.AdministratorRole;
import com.cannontech.roles.yukon.EnergyCompanyRole;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.util.StarsAdminUtil;
import com.cannontech.user.UserUtils;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.stars.action.StarsAdminActionController;

public class NewEnergyCompanyController extends StarsAdminActionController {

    @SuppressWarnings("unchecked")
    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response, 
            final HttpSession session, final StarsYukonUser user,
                final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        ServletUtils.saveRequest( request, session, new String[] {
                "CompanyName", "AddMember", "Email", "OperatorGroup", "CustomerGroup", "Username", "Username2", "Route"} );

        final boolean isAddMember = request.getParameter("AddMember") != null;

        try {
            LiteYukonGroup operGroup = null;

            String[] operGroupNames = request.getParameter("OperatorGroup").split(",");
            String operGroupIDs = "";
            for (int i = 0; i < operGroupNames.length; i++) {
                String groupName = operGroupNames[i].trim();
                if (groupName.equals("")) continue;

                LiteYukonGroup group = this.roleDao.getGroup( groupName );
                if (group == null)
                    throw new WebClientException( "Operator group '" + groupName + "' does not exist");

                if (i == 0)
                    operGroupIDs += String.valueOf( group.getGroupID() );
                else
                    operGroupIDs += "," + String.valueOf( group.getGroupID() );
                if (i == 0) operGroup = group;
            }

            if (operGroup == null)
                throw new WebClientException( "You must select at least one operator group" );

            String[] custGroupNames = request.getParameter("CustomerGroup").split(",");
            String custGroupIDs = "";
            for (int i = 0; i < custGroupNames.length; i++) {
                String groupName = custGroupNames[i].trim();
                if (groupName.equals("")) continue;

                LiteYukonGroup group = this.roleDao.getGroup( groupName );
                if (group == null)
                    throw new WebClientException( "Customer group '" + groupName + "' does not exist");

                if (i == 0)
                    custGroupIDs += String.valueOf( group.getGroupID() );
                else
                    custGroupIDs += "," + String.valueOf( group.getGroupID() );
            }

            if (this.yukonUserDao.getLiteYukonUser( request.getParameter("Username") ) != null)
                throw new WebClientException( "Username of default operator login already exists" );

            if (request.getParameter("Username2").length() > 0 &&
                    this.yukonUserDao.getLiteYukonUser( request.getParameter("Username2") ) != null)
                throw new WebClientException( "Username of second operator login already exists" );

            // Create a privilege group with EnergyCompany and Administrator role
            Hashtable rolePropMap = new Hashtable();
            rolePropMap.put( new Integer(EnergyCompanyRole.OPERATOR_GROUP_IDS), operGroupIDs );
            rolePropMap.put( new Integer(EnergyCompanyRole.CUSTOMER_GROUP_IDS), custGroupIDs );
            if (!isAddMember)
                rolePropMap.put( new Integer(AdministratorRole.ADMIN_CONFIG_ENERGY_COMPANY), StarsAdminUtil.FIRST_TIME );
            else
                rolePropMap.put( new Integer(AdministratorRole.ADMIN_CONFIG_ENERGY_COMPANY), CtiUtilities.TRUE_STRING );

            String adminGroupName = request.getParameter("CompanyName") + " Admin Grp";
            LiteYukonGroup liteAdminGrp = StarsAdminUtil.createOperatorAdminGroup( adminGroupName, rolePropMap );

            // Create the default operator login
            LiteYukonGroup[] operGroups = new LiteYukonGroup[] { operGroup, liteAdminGrp };
                    LiteYukonUser liteUser = StarsAdminUtil.createOperatorLogin(
                                                                                request.getParameter("Username"), request.getParameter("Password"), UserUtils.STATUS_ENABLED, operGroups, null );

                    // Create primary contact of the energy company
                    com.cannontech.database.data.customer.Contact contact =
                        new com.cannontech.database.data.customer.Contact();

                    contact.getContact().setContLastName( CtiUtilities.STRING_NONE );
                    contact.getContact().setContFirstName( CtiUtilities.STRING_NONE );

                    if (request.getParameter("Email").length() > 0) {
                        com.cannontech.database.db.contact.ContactNotification notifEmail =
                            new com.cannontech.database.db.contact.ContactNotification();
                        notifEmail.setNotificationCatID( new Integer(YukonListEntryTypes.YUK_ENTRY_ID_EMAIL) );
                        notifEmail.setDisableFlag( "N" );
                        notifEmail.setNotification( request.getParameter("Email") );
                        notifEmail.setOpCode( Transaction.INSERT );

                        contact.getContactNotifVect().add( notifEmail );
                    }

                    com.cannontech.database.db.customer.Address address = new com.cannontech.database.db.customer.Address();
                    address.setStateCode( "" );
                    contact.setAddress( address );

                    contact = Transaction.createTransaction( Transaction.INSERT, contact ).execute();

                    LiteContact liteContact = new LiteContact( contact.getContact().getContactID().intValue() );
                    StarsLiteFactory.setLiteContact( liteContact, contact );
                    ServerUtils.handleDBChange( liteContact, DBChangeMsg.CHANGE_TYPE_ADD );

                    // Create the energy company
                    com.cannontech.database.db.company.EnergyCompany company =
                        new com.cannontech.database.db.company.EnergyCompany();
                    company.setName( request.getParameter("CompanyName") );
                    company.setPrimaryContactID( contact.getContact().getContactID() );
                    company.setUserID( new Integer(liteUser.getUserID()) );
                    company = Transaction.createTransaction(Transaction.INSERT, company).execute();

                    SqlStatement stmt = new SqlStatement(
                                                         "INSERT INTO EnergyCompanyOperatorLoginList VALUES(" +
                                                         company.getEnergyCompanyID() + ", " + liteUser.getUserID() + ")",
                                                         CtiUtilities.getDatabaseAlias()
                    );
                    stmt.execute();

                    LiteStarsEnergyCompanyFactory factory = 
                        YukonSpringHook.getBean("liteStarsEnergyCompanyFactory", LiteStarsEnergyCompanyFactory.class);
                    LiteStarsEnergyCompany newCompany = factory.createEnergyCompany(company);
                    newCompany.setWorkOrdersLoaded( true );

                    StarsDatabaseCache.getInstance().addEnergyCompany( newCompany );
                    ServerUtils.handleDBChange( newCompany, DBChangeMsg.CHANGE_TYPE_ADD );

                    // Create login for the second operator
                    if (request.getParameter("Username2").length() > 0) {
                        operGroups = new LiteYukonGroup[] { operGroup };
                                StarsAdminUtil.createOperatorLogin(
                                                                   request.getParameter("Username2"), request.getParameter("Password2"), UserUtils.STATUS_ENABLED, operGroups, newCompany );
                    }

                    // Assign default route to the energy company
                    int routeID = Integer.parseInt( request.getParameter("Route") );
                    StarsAdminUtil.updateDefaultRoute( newCompany, routeID );

                    // Add the new energy company as a member of the current company
                    if (isAddMember)
                        StarsAdminUtil.addMember( energyCompany, newCompany, liteUser.getUserID() );

                    session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Energy company created successfully");
        }
        catch (WebClientException e) {
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, e.getMessage());
        }
        catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to create the energy company");
        }
        
        boolean hasMemberManagementAccess = authDao.checkRoleProperty(user.getUserID(), AdministratorRole.ADMIN_MANAGE_MEMBERS);
        boolean memberManagementRedirect = isAddMember && hasMemberManagementAccess;

        String redirect = (memberManagementRedirect) ? "/operator/Admin/ManageMembers.jsp" : this.getRedirect(request);
        String location = ServletUtil.createSafeRedirectUrl(request, redirect);
        response.sendRedirect(location);
    }
    
}
