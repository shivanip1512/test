package com.cannontech.web.stars.action.admin;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.customer.Contact;
import com.cannontech.database.data.lite.LiteAddress;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.stars.LiteServiceCompany;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.data.stars.report.ServiceCompany;
import com.cannontech.database.db.contact.ContactNotification;
import com.cannontech.database.db.stars.report.ServiceCompanyDesignationCode;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.util.StarsAdminUtil;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.CompanyAddress;
import com.cannontech.stars.xml.serialize.PrimaryContact;
import com.cannontech.stars.xml.serialize.StarsServiceCompany;
import com.cannontech.web.stars.action.StarsAdminActionController;

public class UpdateServiceCompanyController extends StarsAdminActionController {

    @SuppressWarnings("unchecked")
    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response, 
            final HttpSession session, final StarsYukonUser user,
                final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        try {
            int companyID = ServletRequestUtils.getIntParameter(request,"CompanyID");
            boolean newCompany = (companyID == -1);

            ServiceCompany company = new ServiceCompany();

            Contact contact = new Contact();
            com.cannontech.database.db.contact.Contact contactDB = contact.getContact();

            LiteServiceCompany liteCompany = null;
            LiteContact liteContact = null;
            LiteAddress liteAddr = null;

            if (!newCompany) {
                liteCompany = energyCompany.getServiceCompany( companyID );
                StarsLiteFactory.setServiceCompany( company, liteCompany );
                liteContact = this.contactDao.getContact( liteCompany.getPrimaryContactID() );
                StarsLiteFactory.setContact( contact, liteContact );
                liteAddr = energyCompany.getAddress( liteCompany.getAddressID() );
            }

            company.getServiceCompany().setCompanyName( request.getParameter("CompanyName") );
            company.getServiceCompany().setMainPhoneNumber( ServletUtils.formatPhoneNumberForStorage(request.getParameter("PhoneNo")) );
            company.getServiceCompany().setMainFaxNumber( ServletUtils.formatPhoneNumberForStorage(request.getParameter("FaxNo")) );
            company.getServiceCompany().setHIType( request.getParameter("Type") );
            contactDB.setContLastName( request.getParameter("ContactLastName") );
            contactDB.setContFirstName( request.getParameter("ContactFirstName") );
            contactDB.setLogInID( Integer.valueOf(request.getParameter("ContactLogin") ));

            ContactNotification emailNotif = null;
            for (int i = 0; i < contact.getContactNotifVect().size(); i++) {
                ContactNotification notif = contact.getContactNotifVect().get(i);
                if (notif.getNotificationCatID().intValue() == YukonListEntryTypes.YUK_ENTRY_ID_EMAIL) {
                    emailNotif = notif;
                    emailNotif.setOpCode( TransactionType.DELETE );
                    break;
                }
            }

            if (request.getParameter("Email").length() > 0) {
                if (emailNotif != null) {
                    emailNotif.setNotification( request.getParameter("Email") );
                    emailNotif.setOpCode( TransactionType.UPDATE );
                }
                else {
                    emailNotif = new ContactNotification();
                    emailNotif.setNotificationCatID( new Integer(YukonListEntryTypes.YUK_ENTRY_ID_EMAIL) );
                    emailNotif.setDisableFlag( "N" );
                    emailNotif.setNotification( request.getParameter("Email") );
                    emailNotif.setOpCode( TransactionType.INSERT );
                    contact.getContactNotifVect().add( emailNotif );
                }
            }

            if (newCompany) {
                com.cannontech.database.db.customer.Address address = new com.cannontech.database.db.customer.Address();
                StarsServiceCompany scTemp = (StarsServiceCompany) session.getAttribute( StarsAdminUtil.SERVICE_COMPANY_TEMP );
                if (scTemp != null)
                    StarsFactory.setCustomerAddress( address, scTemp.getCompanyAddress() );
                else
                    address.setStateCode( "" );

                company.setAddress( address );
                company.setPrimaryContact( contact );
                company.setEnergyCompanyID( energyCompany.getEnergyCompanyId() );
                company = Transaction.createTransaction( TransactionType.INSERT, company ).execute();

                liteContact = (LiteContact) StarsLiteFactory.createLite( contact );
                ServerUtils.handleDBChange( liteContact, DbChangeType.ADD );

                liteCompany = (LiteServiceCompany) StarsLiteFactory.createLite( company );
                energyCompany.addServiceCompany( liteCompany );

                PrimaryContact starsContact = new PrimaryContact();
                StarsLiteFactory.setStarsCustomerContact(
                                                         starsContact, this.contactDao.getContact(company.getServiceCompany().getPrimaryContactID().intValue()) );

                CompanyAddress starsAddr = new CompanyAddress();
                StarsLiteFactory.setStarsCustomerAddress(
                                                         starsAddr, energyCompany.getAddress(company.getAddress().getAddressID().intValue()) );
            }
            else {
                contact = Transaction.createTransaction( TransactionType.UPDATE, contact ).execute();
                StarsLiteFactory.setLiteContact( liteContact, contact );
                ServerUtils.handleDBChange( liteContact, DbChangeType.UPDATE );

                company.setPrimaryContact(contact);
                company = Transaction.createTransaction( TransactionType.UPDATE, company ).execute();
                StarsLiteFactory.setLiteServiceCompany( liteCompany, company );

                if (request.getParameter("hasCodes") != null && request.getParameter("hasCodes").length() > 0) 
                { 
                    List<ServiceCompanyDesignationCode> oldCodeList = ServiceCompanyDesignationCode.getServiceCompanyDesignationCodes(companyID);

                    for(int j = 0; j < oldCodeList.size(); j++)
                    {
                        ServiceCompanyDesignationCode currentCode = oldCodeList.get(j);
                        String codeToUpdateString = request.getParameter("CodeUpdate_" + currentCode.getDesignationCodeID().toString());
                        if(codeToUpdateString != null && codeToUpdateString.length() > 0)
                        {
                            currentCode.setDesignationCodeValue(codeToUpdateString);
                            currentCode = Transaction.createTransaction( TransactionType.UPDATE, currentCode).execute();
                            ServerUtils.handleDBChange(StarsLiteFactory.createLite(currentCode), DbChangeType.UPDATE);
                        }
                        else if(codeToUpdateString != null && codeToUpdateString.length() <= 0)
                        {
                            currentCode = Transaction.createTransaction(TransactionType.DELETE, currentCode).execute();
                            ServerUtils.handleDBChange(StarsLiteFactory.createLite(currentCode), DbChangeType.DELETE);
                        }
                    }
                }
            }

            session.removeAttribute( StarsAdminUtil.SERVICE_COMPANY_TEMP );
            if (newCompany)
                session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Service company created successfully");
            else
                session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Service company information updated successfully");
        }
        catch (WebClientException we) {
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, we.getMessage());
            String redirect = this.getReferer(request);
            response.sendRedirect(redirect);
            return;
        }
        catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update service company information");
            String redirect = this.getReferer(request);
            response.sendRedirect(redirect);
            return;
        }
        
        String redirect = this.getRedirect(request);
        response.sendRedirect(redirect);
    }

}
