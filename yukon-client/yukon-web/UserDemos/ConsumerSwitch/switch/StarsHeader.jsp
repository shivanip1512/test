<%@ page import="java.util.*" %>
<%@ page import="com.cannontech.stars.xml.serialize.*" %>
<%@ page import="com.cannontech.stars.web.StarsUser" %>
<%@ page import="com.cannontech.stars.web.util.CommonUtils" %>

<%
	java.text.SimpleDateFormat histDateFormat = new java.text.SimpleDateFormat("MM/dd/yy HH:mm");
	java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MM/dd/yyyy");
    java.text.SimpleDateFormat dateTimeFormat = new java.text.SimpleDateFormat("MM:dd:yyyy:HH:mm:ss");
	
	StarsUser user = null;
	try {
		user = (StarsUser) session.getAttribute("USER");
	}
	catch (IllegalStateException ise) {}
	if (user == null) {
		response.sendRedirect("/login.jsp"); return;
	}
	
    String dbAlias = user.getDatabaseAlias();	
	
	StarsCustAccountInfo accountInfo = null;
	StarsCustomerAccount account = null;
    StreetAddress propAddr = null;
    StarsSiteInformation siteInfo = null;
    BillingAddress billAddr = null;
    PrimaryContact primContact = null;
	
	StarsAppliances appliances = null;
	StarsInventories inventories = null;
	StarsLMPrograms programs = null;
	StarsGetEnrollmentProgramsResponse categories = null;
	
	accountInfo = (StarsCustAccountInfo) user.getAttribute(CommonUtils.TRANSIENT_ATT_LEADING + "CUSTOMER_ACCOUNT_INFORMATION");
	if (accountInfo != null) {
		account = accountInfo.getStarsCustomerAccount();
		propAddr = account.getStreetAddress();
		siteInfo = account.getStarsSiteInformation();
		billAddr = account.getBillingAddress();
		primContact = account.getPrimaryContact();
		
		appliances = accountInfo.getStarsAppliances();
		inventories = accountInfo.getStarsInventories();
		programs = accountInfo.getStarsLMPrograms();
		categories = (StarsGetEnrollmentProgramsResponse) session.getAttribute( "ENROLLMENT_PROGRAMS" );
	}
%>
