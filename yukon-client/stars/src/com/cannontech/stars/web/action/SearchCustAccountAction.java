package com.cannontech.stars.web.action;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.stars.web.StarsOperator;
import com.cannontech.stars.web.util.CommonUtils;
import com.cannontech.stars.xml.StarsCustAccountInfoFactory;
import com.cannontech.stars.xml.serialize.SearchBy;
import com.cannontech.stars.xml.serialize.StarsCustAccountInfo;
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
            	if (attName.startsWith( CommonUtils.TRANSIENT_ATT_LEADING ))
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
                StarsFailure failure = new StarsFailure();
                failure.setStatusCode( StarsConstants.FAILURE_CODE_SESSION_INVALID );
                failure.setDescription("Session invalidated, please login again");
                respOper.setStarsFailure( failure );
                return SOAPUtil.buildSOAPMessage( respOper );
            }

            StarsSearchCustomerAccount searchAccount = reqOper.getStarsSearchCustomerAccount();
            com.cannontech.database.data.stars.customer.CustomerAccount account = null;

            Hashtable selectionLists = com.cannontech.stars.util.CommonUtils.getSelectionListTable(
            		new Integer((int) operator.getEnergyCompanyID()) );
            		
            StarsCustSelectionList searchByList = (StarsCustSelectionList) selectionLists.get( com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_SEARCHBY );
            StarsSelectionListEntry searchByEntry = null;
            for (int i = 0; i < searchByList.getStarsSelectionListEntryCount(); i++) {
            	StarsSelectionListEntry entry = searchByList.getStarsSelectionListEntry(i);
            	if (searchAccount.getSearchBy().getEntryID() == entry.getEntryID()) {
            		searchByEntry = entry;
            		break;
            	}
            }
            
            if (searchByEntry.getYukonDefinition().equalsIgnoreCase( com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_SEARCHBY_ACCTNO )) {
                account = com.cannontech.database.data.stars.customer.CustomerAccount.searchByAccountNumber(
                            new Integer((int) operator.getEnergyCompanyID()), searchAccount.getSearchValue() );
            }

            if (account == null) {
                StarsFailure failure = new StarsFailure();
                failure.setStatusCode( StarsConstants.FAILURE_CODE_SESSION_INVALID );
                failure.setDescription("No customer account matching the specified account number");
                respOper.setStarsFailure( failure );
                return SOAPUtil.buildSOAPMessage( respOper );
            }

        	operator.setAttribute("CUSTOMER_ACCOUNT", account);
            
			StarsCustAccountInfo accountInfo = StarsCustAccountInfoFactory.getStarsCustAccountInfo(
					account, selectionLists, StarsSearchCustomerAccountResponse.class );
            respOper.setStarsSearchCustomerAccountResponse( (StarsSearchCustomerAccountResponse) accountInfo );

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
			
            StarsCustAccountInfo accountInfo = operation.getStarsSearchCustomerAccountResponse();
            if (accountInfo == null) return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;

			StarsOperator operator = (StarsOperator) session.getAttribute("OPERATOR");
            operator.setAttribute(CommonUtils.TRANSIENT_ATT_LEADING + "CUSTOMER_ACCOUNT_INFORMATION", accountInfo);
            
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
    }
}