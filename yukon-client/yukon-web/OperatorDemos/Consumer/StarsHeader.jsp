<%@ page import="java.util.*" %>
<%@ page import="com.cannontech.stars.xml.serialize.*" %>
<%@ page import="com.cannontech.stars.web.StarsOperator" %>
<%@ page import="com.cannontech.stars.web.util.CommonUtils" %>

<%
    java.text.SimpleDateFormat datePart = new java.text.SimpleDateFormat("MM/dd/yyyy");	  
    java.text.SimpleDateFormat timePart = new java.text.SimpleDateFormat("HH:mm");
    java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MM:dd:yyyy:HH:mm:ss");
	java.text.SimpleDateFormat histDateFormat = new java.text.SimpleDateFormat("MM/dd/yy HH:mm");
	
	StarsOperator operator = null;
	try {
		operator = (StarsOperator) session.getAttribute("OPERATOR");
	}
	catch (IllegalStateException ise) {}
	if (operator == null) {
		response.sendRedirect("/login.jsp"); return;
	}
	
    String dbAlias = operator.getDatabaseAlias();	
	
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
	
	accountInfo = (StarsCustAccountInfo) operator.getAttribute(CommonUtils.TRANSIENT_ATT_LEADING + "CUSTOMER_ACCOUNT_INFORMATION");
	if (accountInfo != null) {
		account = accountInfo.getStarsCustomerAccount();
		propAddr = account.getStreetAddress();
		siteInfo = account.getStarsSiteInformation();
		billAddr = account.getBillingAddress();
		primContact = account.getPrimaryContact();
		
		appliances = accountInfo.getStarsAppliances();
		inventories = accountInfo.getStarsInventories();
		programs = accountInfo.getStarsLMPrograms();
		categories = (StarsGetEnrollmentProgramsResponse) operator.getAttribute( "ENROLLMENT_PROGRAMS" );
	}
%>
