package com.cannontech.stars.web.action;

import java.util.*;
import javax.servlet.http.*;
import com.cannontech.database.Transaction;
import com.cannontech.stars.xml.serialize.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class UpdateCustAccountAction extends ActionBase {

    public UpdateCustAccountAction() {
        super();
    }

    public StarsOperation build(HttpServletRequest req, HttpSession session) {
        StarsCustomerAccountInformation accountInfo = (StarsCustomerAccountInformation) session.getAttribute("CUSTOMER_ACCOUNT_INFORMATION");
        StarsCustomerAccount account = accountInfo.getStarsCustomerAccount();

        account.setAccountNumber( req.getParameter("AcctNo") );
        account.setIsCommercial( Boolean.valueOf(req.getParameter("Commercial")).booleanValue() );
        account.setCompany( req.getParameter("Company") );
        account.setAccountNotes( req.getParameter("AcctNotes") );

        account.setPropertyNumber( req.getParameter("PropNo") );
        account.setPropertyNotes( req.getParameter("PropNotes") );

        StreetAddress propAddr = new StreetAddress();
        propAddr.setStreetAddr1( req.getParameter("SAddr1") );
        propAddr.setStreetAddr2( req.getParameter("SAddr2") );
        propAddr.setCity( req.getParameter("SCity") );
        propAddr.setState( req.getParameter("SState") );
        propAddr.setZip( req.getParameter("SZip") );
        account.setStreetAddress( propAddr );

        StarsSiteInformation siteInfo = new StarsSiteInformation();
        siteInfo.setSubstationName( req.getParameter("Substation") );
        siteInfo.setFeeder( req.getParameter("Feeder") );
        siteInfo.setPole( req.getParameter("Pole") );
        siteInfo.setTransformerSize( req.getParameter("TranSize") );
        siteInfo.setServiceVoltage( req.getParameter("ServVolt") );

        BillingAddress billAddr = new BillingAddress();
        billAddr.setStreetAddr1( req.getParameter("BAddr1") );
        billAddr.setStreetAddr2( req.getParameter("BAddr2") );
        billAddr.setCity( req.getParameter("BCity") );
        billAddr.setState( req.getParameter("BState") );
        billAddr.setZip( req.getParameter("BZip") );
        account.setBillingAddress( billAddr );

        PrimaryContact primContact = new PrimaryContact();
        primContact.setLastName( req.getParameter("LastName") );
        primContact.setFirstName( req.getParameter("FirstName") );
        primContact.setHomePhone( req.getParameter("HomePhone") );
        primContact.setWorkPhone( req.getParameter("WorkPhone") );
        account.setPrimaryContact( primContact );

        StarsUpdateCustomerAccount updateAccount = (StarsUpdateCustomerAccount)
                StarsCustAccountFactory.newStarsCustAccount(account, StarsUpdateCustomerAccount.class );

        StarsOperation operation = new StarsOperation();
        operation.setStarsUpdateCustomerAccount( updateAccount );

        return operation;
    }

    public StarsOperation process(StarsOperation operation, HttpSession session) {
        StarsOperation respOper = new StarsOperation();
        StarsUpdateCustomerAccount starsAccount = operation.getStarsUpdateCustomerAccount();
/*
        com.cannontech.database.data.starscustomer.CustomerAccount account =
                (com.cannontech.database.data.starscustomer.CustomerAccount) session.getAttribute("CUSTOMER_ACCOUNT");
        if (account == null) {
            StarsFailure failure = new StarsFailure();
            failure.setContent("Session invalidated, please login again");
            respOper.setStarsFailure( failure );
            return respOper;
        }

        com.cannontech.database.data.starscustomer.CustomerBase customer = account.getCustomerBase();

        com.cannontech.database.db.customer.CustomerContact primContact = customer.getPrimaryContact();
        StarsCustomerContactFactory.setCustomerContact(primContact, starsAccount.getPrimaryContact());

        Vector contactVct = new Vector();
        for (int i = 0; i < starsAccount.getAdditionalContactCount(); i++) {
            //com.cannontech.database.db.customer.CustomerContact.
        }

        Transaction transaction = Transaction.createTransaction(Transaction.RETRIEVE, energyCompany);
        transaction.execute();

        com.cannontech.database.data.starscustomer.CustomerAccount account =
                    new  com.cannontech.database.data.starscustomer.CustomerAccount();

        com.cannontech.database.db.customer.CustomerContact primContact = new com.cannontech.database.db.customer.CustomerContact();
        com.cannontech.database.db.starscustomer.CustomerBase customer = new com.cannontech.database.db.starscustomer.CustomerBase();
*/
        StarsSuccess success = new StarsSuccess();
        respOper.setStarsSuccess( success );

        return respOper;
    }

    public boolean parse(StarsOperation operation, HttpSession session) {
        return (operation.getStarsSuccess() != null);
    }
}