<%@ page import="java.util.*" %>
<%@ page import="com.cannontech.stars.xml.serialize.*" %>
<%@ page import="com.cannontech.stars.web.StarsOperator" %>
<%@ page import="com.cannontech.stars.util.ServletUtils" %>
<%@ page import="com.cannontech.graph.model.TrendModelType" %>

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
	
	StarsCustAccountInformation accountInfo = null;
	StarsCustomerAccount account = null;
    StreetAddress propAddr = null;
    StarsSiteInformation siteInfo = null;
    BillingAddress billAddr = null;
    PrimaryContact primContact = null;
	
	StarsAppliances appliances = null;
	StarsInventories inventories = null;
	StarsLMPrograms programs = null;
	StarsCallReportHistory callHist = null;
	StarsServiceRequestHistory serviceHist = null;
	StarsGetEnrollmentProgramsResponse categories = null;
	
	accountInfo = (StarsCustAccountInformation) operator.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + "CUSTOMER_ACCOUNT_INFORMATION");
	if (accountInfo != null) {
		account = accountInfo.getStarsCustomerAccount();
		propAddr = account.getStreetAddress();
		siteInfo = account.getStarsSiteInformation();
		billAddr = account.getBillingAddress();
		primContact = account.getPrimaryContact();
		
		appliances = accountInfo.getStarsAppliances();
		inventories = accountInfo.getStarsInventories();
		programs = accountInfo.getStarsLMPrograms();
		callHist = accountInfo.getStarsCallReportHistory();
		serviceHist = accountInfo.getStarsServiceRequestHistory();
		categories = (StarsGetEnrollmentProgramsResponse) operator.getAttribute( "ENROLLMENT_PROGRAMS" );
	}
%>
	<jsp:useBean id="graphBean" class="com.cannontech.graph.GraphBean" scope="session">
		<%-- this body is executed only if the bean is created --%>
	<jsp:setProperty name="graphBean" property="viewType" value="<%=TrendModelType.LINE_VIEW%>"/>
	<jsp:setProperty name="graphBean" property="startStr" value="<%=datePart.format(com.cannontech.util.ServletUtil.getToday())%>"/>
	<jsp:setProperty name="graphBean" property="tab" value="graph"/>
	<jsp:setProperty name="graphBean" property="period" value="<%=com.cannontech.util.ServletUtil.historicalPeriods[0]%>"/>
	<jsp:setProperty name="graphBean" property="gdefid" value="-1"/>	
	    <%-- intialize bean properties --%>
	</jsp:useBean>

