
<%@ page import="java.util.Date" %>
<%@ page import="java.util.GregorianCalendar" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="com.cannontech.servlet.LCConnectionServlet" %>
<%@ page import="com.cannontech.loadcontrol.data.LMProgramCurtailment" %>
<%@ page import="com.cannontech.common.util.TimeUtil" %>
<%
	String pending = request.getParameter("pending");
	
    // Check if a particular program should be shown,
    // if not then just display the summary for today/tommorrow
    Object progParam = request.getParameter("prog");

    Integer programID = null;

    if( progParam != null )    
        programID = new Integer( progParam.toString() );    
           
    java.util.Date today = com.cannontech.util.ServletUtil.getToday();
    java.util.Date tomorrow = com.cannontech.util.ServletUtil.getTommorow();

    java.text.SimpleDateFormat mandDatePart = new java.text.SimpleDateFormat("MM/dd/yyyy");	  
    java.text.SimpleDateFormat mandTimePart = new java.text.SimpleDateFormat("HH:mm");
%>
  
  <%
    //Decide which content should appear
    if (pending != null)
	{
		out.println("<CENTER><FONT FACE=\"Arial, Helvetica\" COLOR=red>A new ERS Notification has been sent, please wait...</CENTER><BR>");
	}
    else
    if( progParam != null )
    {
%>

  <%@ include file="oper_mand_detail.jsp" %>
  <%    
    }
    else
    {
        // Just display the summary
%>
  <%@ include file="oper_mand_summary.jsp" %>
  <%
    }
%>
