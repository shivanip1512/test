<%@ page import="java.util.*" %>
<%@ page import="com.cannontech.stars.xml.serialize.*" %>
<%@ page import="com.cannontech.stars.web.StarsOperator" %>
<%@ page import="com.cannontech.stars.web.util.CommonUtils" %>

<%
	java.text.SimpleDateFormat histDateFormat = new java.text.SimpleDateFormat("MM/dd/yy HH:mm");
	java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MM/dd/yyyy");
    java.text.SimpleDateFormat dateTimeFormat = new java.text.SimpleDateFormat("MM:dd:yyyy:HH:mm:ss");
	
	StarsOperator operator = null;
	try {
		operator = (StarsOperator) session.getAttribute("OPERATOR");
	}
	catch (IllegalStateException ise) {}
	if (operator == null) {
		response.sendRedirect("/login.jsp"); return;
	}
	
	StarsCustAccountInfo accountInfo = (StarsCustAccountInfo) operator.getAttribute("CUSTOMER_ACCOUNT_INFORMATION");
	
	StarsCustomerAccount account = accountInfo.getStarsCustomerAccount();
    StreetAddress propAddr = account.getStreetAddress();
    StarsSiteInformation siteInfo = account.getStarsSiteInformation();
    BillingAddress billAddr = account.getBillingAddress();
    PrimaryContact primContact = account.getPrimaryContact();
	
	StarsAppliances appliances = accountInfo.getStarsAppliances();
	StarsInventories inventories = accountInfo.getStarsInventories();
	StarsLMPrograms programs = accountInfo.getStarsLMPrograms();
	StarsGetEnrollmentProgramsResponse categories = (StarsGetEnrollmentProgramsResponse) operator.getAttribute( "ENROLLMENT_PROGRAMS" );
	
    String dbAlias = operator.getDatabaseAlias();
%>
