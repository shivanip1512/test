<%@ page import="java.util.Date" %>
<%@ page import="java.util.GregorianCalendar" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="com.cannontech.servlet.LCConnectionServlet" %>
<%@ page import="com.cannontech.loadcontrol.data.LMProgramEnergyExchange" %>
<%@ page import="com.cannontech.loadcontrol.data.LMEnergyExchangeOffer" %>
<%@ page import="com.cannontech.loadcontrol.data.LMEnergyExchangeOfferRevision" %>
<%@ page import="com.cannontech.loadcontrol.data.LMEnergyExchangeHourlyOffer" %>
<%@ page import="com.cannontech.loadcontrol.data.LMEnergyExchangeCustomer" %>
<%@ page import="com.cannontech.loadcontrol.data.LMEnergyExchangeCustomerReply" %>
<%@ page import="com.cannontech.loadcontrol.data.LMEnergyExchangeHourlyCustomer" %>
<%@ page import="com.cannontech.common.util.TimeUtil" %>
<%
	String pending = request.getParameter("pending");

	// Check if a particular program should be shown,
	// if not then just display the summary for today/tommorrow
	Object progParam = request.getParameter("prog");
	Object offerParam = request.getParameter("offer");
	Object revisionParam = request.getParameter("rev");
	Object customerParam = request.getParameter("cust");

	Integer programID = null;
	Integer offerID = null;
	Integer revisionNumber = null;
	Integer customerID = null;

	if( progParam != null )    
		programID = new Integer( progParam.toString() );    

	if( offerParam != null )
		offerID = new Integer( offerParam.toString() );

	if( revisionParam != null ) 
		revisionNumber = new Integer( revisionParam.toString() );

	if( customerParam != null )
		customerID = new Integer( customerParam.toString() );

	java.util.Date today = com.cannontech.util.ServletUtil.getToday();
	java.util.Date tomorrow = com.cannontech.util.ServletUtil.getTommorow();

	//java.text.SimpleDateFormat eeDatePart = new java.text.SimpleDateFormat("MM/dd/yyyy");	  
	//java.text.SimpleDateFormat eeTimePart = new java.text.SimpleDateFormat("HH:mm");
%>


<%
    if (pending != null)
	{
		if (pending.equalsIgnoreCase("new"))
			out.println("<CENTER><FONT FACE=\"Arial, Helvetica\" COLOR=red>A new offer has been sent, please wait...</CENTER><BR>");
		else if (pending.equalsIgnoreCase("revise"))
			out.println("<CENTER><FONT FACE=\"Arial, Helvetica\" COLOR=red>A revised offer has been sent, please wait...</CENTER><BR>");
		else if (pending.equalsIgnoreCase("close"))
			out.println("<CENTER><FONT FACE=\"Arial, Helvetica\" COLOR=red>A close offer request has been sent, please wait...</CENTER><BR>");
	}
    else
	//Decide which content should appear
	if( customerID != null )
	{
%>
	<%@ include file="oper_ee_response.jsp" %>
<%
	}
	else
	if( progParam != null )
	{
%>
	<%@ include file="oper_ee_detail.jsp" %>        
<%    
	}
	else
	{
		// Just display the summary
%>
	<%@ include file="oper_ee_summary.jsp" %>
<%
	}
%>


