package com.cannontech.stars.web.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.stars.web.StarsOperator;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.xml.serialize.AdditionalContact;
import com.cannontech.stars.xml.serialize.PrimaryContact;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsCustomerAccount;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class UpdateContacts extends HttpServlet {

    private static final String loginURL = "/login.jsp";
    private static final String homeURL = "/OperatorDemos/Operations.jsp";
    private static final String nextURL = "/OperatorDemos/Consumer/Update.jsp";

    public UpdateContacts() {
    }

    public void service(HttpServletRequest req, HttpServletResponse resp) throws javax.servlet.ServletException, java.io.IOException {
        HttpSession session = req.getSession(false);
        if (session == null) resp.sendRedirect(loginURL);

		StarsOperator operator = (StarsOperator) session.getAttribute( "OPERATOR" );
		if (operator == null) {
			resp.sendRedirect(loginURL); return;
		}
		
        StarsCustAccountInformation accountInfo = (StarsCustAccountInformation) operator.getAttribute(
        		ServletUtils.TRANSIENT_ATT_LEADING + "CUSTOMER_ACCOUNT_INFORMATION" );
        StarsCustomerAccount account = accountInfo.getStarsCustomerAccount();
        if (account == null) {
        	resp.sendRedirect(homeURL); return;
        }

        String lastName = req.getParameter("LastName");
        String firstName = req.getParameter("FirstName");

        if (lastName != null && lastName.trim().length() > 0
            && firstName != null && firstName.trim().length() > 0) {
            PrimaryContact primContact = new PrimaryContact();
            primContact.setLastName( lastName );
            primContact.setFirstName( firstName );
            primContact.setHomePhone( ServletUtils.formatPhoneNumber(req.getParameter("HomePhone")) );
            primContact.setWorkPhone( ServletUtils.formatPhoneNumber(req.getParameter("WorkPhone")) );
            account.setPrimaryContact( primContact );
        }

        account.removeAllAdditionalContact();

        for (int i = 2; i <= 4; i++) {
            lastName = req.getParameter("LastName" + i);
            firstName = req.getParameter("FirstName" + i);

            if (lastName != null && lastName.trim().length() > 0
                && firstName != null && firstName.trim().length() > 0) {
                AdditionalContact contact = new AdditionalContact();
                contact.setContactID( Integer.parseInt(req.getParameter("ContactID" + i)) );
                contact.setLastName( lastName );
                contact.setFirstName( firstName );
                contact.setHomePhone( ServletUtils.formatPhoneNumber(req.getParameter("HomePhone" + i)) );
                contact.setWorkPhone( ServletUtils.formatPhoneNumber(req.getParameter("WorkPhone" + i)) );
                account.addAdditionalContact( contact );
            }
        }

        resp.sendRedirect( nextURL );
    }
}