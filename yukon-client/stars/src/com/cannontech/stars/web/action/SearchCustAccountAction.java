package com.cannontech.stars.web.action;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.database.data.lite.stars.*;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsOperator;
import com.cannontech.stars.xml.StarsCustAccountInformationFactory;
import com.cannontech.stars.xml.StarsCustListEntryFactory;
import com.cannontech.stars.xml.StarsFailureFactory;
import com.cannontech.stars.xml.serialize.SearchBy;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsSearchCustomerAccount;
import com.cannontech.stars.xml.serialize.StarsSearchCustomerAccountResponse;
import com.cannontech.stars.xml.serialize.StarsCustSelectionList;
import com.cannontech.stars.xml.serialize.StarsSelectionListEntry;
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.stars.xml.util.StarsConstants;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class SearchCustAccountAction implements ActionBase {

    public SearchCustAccountAction() {
        super();
    }

    public SOAPMessage build(HttpServletRequest req, HttpSession session) {
        try {
            StarsOperator operator = (StarsOperator) session.getAttribute("OPERATOR");
            if (operator == null) return null;
            
            // Remove the "transient"(account-related) attributes
            Enumeration enum = (operator == null) ? session.getAttributeNames() : operator.getAttributeNames();
            while (enum.hasMoreElements()) {
            	String attName = (String) enum.nextElement();
            	if (attName.startsWith( ServletUtils.TRANSIENT_ATT_LEADING ))
        			operator.removeAttribute(attName);
            }

            StarsSearchCustomerAccount searchAccount = new StarsSearchCustomerAccount();
            SearchBy searchBy = new SearchBy();
            searchBy.setEntryID( Integer.parseInt(req.getParameter("SearchBy")) );
            searchAccount.setSearchBy( searchBy );
            searchAccount.setSearchValue( req.getParameter("SearchValue") );

            StarsOperation operation = new StarsOperation();
            operation.setStarsSearchCustomerAccount( searchAccount );

            return SOAPUtil.buildSOAPMessage( operation );
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public SOAPMessage process(SOAPMessage reqMsg, HttpSession session) {
        try {
            StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );
            StarsOperation respOper = new StarsOperation();

            StarsOperator operator = (StarsOperator) session.getAttribute("OPERATOR");
            if (operator == null) {
                respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
                		StarsConstants.FAILURE_CODE_SESSION_INVALID, "Session invalidated, please login again") );
                return SOAPUtil.buildSOAPMessage( respOper );
            }

            StarsSearchCustomerAccount searchAccount = reqOper.getStarsSearchCustomerAccount();
            LiteStarsCustAccountInformation liteAcctInfo = null;
            
            Integer energyCompanyID = new Integer( (int) operator.getEnergyCompanyID() );
            Hashtable selectionLists = com.cannontech.stars.web.servlet.SOAPServer.getAllSelectionLists( energyCompanyID );
            		
            if (searchAccount.getSearchBy().getEntryID() == StarsCustListEntryFactory.getStarsCustListEntry(
            		(LiteCustomerSelectionList) selectionLists.get(com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_SEARCHBY),
            		com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_SEARCHBY_ACCTNO ).getEntryID()) {
            	// Search by account number
            	liteAcctInfo = com.cannontech.stars.web.servlet.SOAPServer.getCustAccountInformation(
            			new Integer((int) operator.getEnergyCompanyID()), searchAccount.getSearchValue() );
            }

            if (liteAcctInfo == null) {
                respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
                		StarsConstants.FAILURE_CODE_SESSION_INVALID, "No customer account matching the specified account number") );
                return SOAPUtil.buildSOAPMessage( respOper );
            }
            
            operator.setAttribute( "CUSTOMER_ACCOUNT_INFORMATION", liteAcctInfo );
            StarsCustAccountInformation starsAcctInfo = StarsLiteFactory.createStarsCustAccountInformation( liteAcctInfo, energyCompanyID );
            
			StarsSearchCustomerAccountResponse resp = new StarsSearchCustomerAccountResponse();
			resp.setStarsCustAccountInformation( starsAcctInfo );
			
            respOper.setStarsSearchCustomerAccountResponse( resp );
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public int parse(SOAPMessage reqMsg, SOAPMessage respMsg, HttpSession session) {
        try {
            StarsOperation operation = SOAPUtil.parseSOAPMsgForOperation( respMsg );

			StarsFailure failure = operation.getStarsFailure();
			if (failure != null) return failure.getStatusCode();
			
			StarsSearchCustomerAccountResponse resp = operation.getStarsSearchCustomerAccountResponse();
            if (resp == null) return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;

			StarsOperator operator = (StarsOperator) session.getAttribute("OPERATOR");
            operator.setAttribute(ServletUtils.TRANSIENT_ATT_LEADING + "CUSTOMER_ACCOUNT_INFORMATION", resp.getStarsCustAccountInformation());
            
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
    }
}