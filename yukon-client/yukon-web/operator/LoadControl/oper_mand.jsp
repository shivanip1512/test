<%@ include file="include/oper_header.jsp" %> 

<cti:checkRole roleid="<%=DirectCurtailmentRole.ROLEID%>">
<%
   String tab = request.getParameter("tab");
   tab = request.getParameter("tab");

   String referrer = (String) session.getValue("referrer");
   if( referrer == null )
      referrer = request.getRequestURI() + "?" + request.getQueryString();
		  
    if( tab == null )    
        tab = "";
         
    session.putValue("referrer", request.getRequestURI() + "?" + request.getQueryString());
%>

<%@ page import="com.cannontech.loadcontrol.data.LMProgramCurtailment" %>
<%@ taglib uri="/WEB-INF/cti.tld" prefix="cti" %>
<%@ taglib uri="/WEB-INF/struts.tld" prefix="struts" %>
<jsp:useBean id="checker" scope="session" class="com.cannontech.validate.PageBean"/>

<%
    String[] programNames = null;
    String[] programIds = null;
	String programStr = "";
	String notifyDateStr = "";
	String notifyTimeStr = ""; 
	String curtailDateStr = "";
	String curtailTimeStr = "";
	
	java.text.SimpleDateFormat mandDateFormat = new java.text.SimpleDateFormat("MM/dd/yy");
	java.text.SimpleDateFormat mandTimeFormat = new java.text.SimpleDateFormat("HH:mm");
	
	mandDateFormat.setTimeZone(tz);
	mandTimeFormat.setTimeZone(tz);
	
	if (tab.equalsIgnoreCase(""))
	{
		String pending = request.getParameter("pending");

		if (pending != null)
			out.println("<META HTTP-EQUIV=\"refresh\" CONTENT=\"5;URL=oper_mand.jsp\">");
		else
			out.println("<META HTTP-EQUIV=\"refresh\" CONTENT=\"600\">");
	}
	else if (tab.equalsIgnoreCase("new")) {
        LMProgramCurtailment[] programs = cache.getEnergyCompanyCurtailmentPrograms(energyCompanyID);
        programNames = new String[programs.length];
        programIds = new String[programs.length];
		for (int i = 0; i < programs.length; i++) {
			programNames[i] = programs[i].getYukonName();
			programIds[i] = programs[i].getYukonID().toString();
		}
   
		if (request.getParameter("error") == null) {	// not back from the confirmation page
			if (request.getParameter("submitted") == null) {	// not submitted yet
				checker.clear();
				if( programIds.length > 0)
					checker.set("program", programIds[0]);
				else
					checker.set("program", "-1");//give it some invalid value, gets us past exceptions being tossed
				checker.set("notifydate", mandDateFormat.format(com.cannontech.util.ServletUtil.getToday(tz)));
				checker.set("notifytime", mandTimeFormat.format(new java.util.Date()));
				checker.set("curtaildate", mandDateFormat.format(com.cannontech.util.ServletUtil.getToday(tz)));
				checker.set("curtailtime", mandTimeFormat.format(new java.util.Date()));
				checker.set("duration", "4");
				checker.set("comments", "(none)");
			}
			else {	// submitted, validate and redirect to confirmation page
				java.util.Enumeration enum = request.getParameterNames();
				while (enum.hasMoreElements()) {
					String name = (String)enum.nextElement();
					String[] value = request.getParameterValues(name);
					if (value.length == 1)
						checker.set(name, value[0]);
					else
						checker.set(name, value);
				}

				if (checker.validate(request)) {
					response.sendRedirect("oper_mand.jsp?tab=newconfirm");
					return;
				}
			} 
		}
	}
	else if (tab.equalsIgnoreCase("newconfirm")) {	
		if (request.getParameter("confirmed") == null) {	// not confirmed yet
			programStr = checker.get("program");
			notifyDateStr = mandDateFormat.format( ServletUtil.parseDateStringLiberally(checker.get("notifydate"),tz));
			notifyTimeStr = mandTimeFormat.format( ServletUtil.parseDateStringLiberally(checker.get("notifytime"), tz));
			curtailDateStr = mandDateFormat.format( ServletUtil.parseDateStringLiberally(checker.get("curtaildate"),tz));
			curtailTimeStr = mandTimeFormat.format( ServletUtil.parseDateStringLiberally(checker.get("curtailtime"), tz));
			String commentStr = checker.get("comments");
			if (commentStr == null || commentStr.length() == 0)
				checker.set("comments", "(none)");
		}
		else {	// confirmed
			// make sure there is no open curtailment for the same program
			boolean duplicateError = false;

			try {
				Class[] qTypes = { java.util.Date.class };
				Object[][] result = com.cannontech.util.ServletUtil.executeSQL( dbAlias, "SELECT MAX(CURTAILMENTSTOPTIME) FROM LMCURTAILPROGRAMACTIVITY WHERE DEVICEID = " + request.getParameter("program"), qTypes );

				if (result != null && result.length > 0 )
				{
					java.util.Date stopCurtail = (java.util.Date)result[0][0];

					if (stopCurtail.getTime() > System.currentTimeMillis())
					{
						if (checker == null) {
							response.sendRedirect("oper_mand.jsp");
							return;
						}
						checker.setError("duplicate_error", "An active curtailment program has already existed! New program cannot be created until the current one expires.");
						response.sendRedirect("oper_mand.jsp?tab=new&error=true");
						duplicateError = true;
						return;
					}
				}
			}
			catch( Exception e ) {
				e.printStackTrace();
			}
			
			java.util.GregorianCalendar notifyCal = new java.util.GregorianCalendar();
			java.util.GregorianCalendar startCal = new java.util.GregorianCalendar();
			java.util.GregorianCalendar stopCal = new java.util.GregorianCalendar();
			java.util.GregorianCalendar timeCal = new java.util.GregorianCalendar();
			
			notifyCal.setTime( ServletUtil.parseDateStringLiberally(checker.get("notifydate")) );
			timeCal.setTime( ServletUtil.parseDateStringLiberally(checker.get("notifytime"), tz) );
			notifyCal.set(Calendar.HOUR, timeCal.get(Calendar.HOUR_OF_DAY));
			notifyCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
			
			startCal.setTime( ServletUtil.parseDateStringLiberally(checker.get("curtaildate")) );
			timeCal.setTime( ServletUtil.parseDateStringLiberally(checker.get("curtailtime"), tz) );
			startCal.set(Calendar.HOUR, timeCal.get(Calendar.HOUR_OF_DAY));
			startCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
			
			// check that notify date is before start date
			if ( !notifyCal.before(startCal) ) {
				if (checker == null) {
					response.sendRedirect("oper_mand.jsp");
					return;
				}
				checker.setError("notifytime", "Notify time must be earlier than curtail time");
				response.sendRedirect("oper_mand.jsp?tab=new&error=true");
				return;
			}
			else if (!duplicateError)
			{
				long durationMillis = Integer.parseInt(request.getParameter("duration")) * 60 * 60 * 1000;
				stopCal.setTime( new java.util.Date(startCal.getTime().getTime() + durationMillis) );

				com.cannontech.loadcontrol.LoadControlClientConnection conn = cs.getConnection();
				com.cannontech.loadcontrol.messages.LMManualControlRequest msg = new com.cannontech.loadcontrol.messages.LMManualControlRequest();
				msg.setCommand( com.cannontech.loadcontrol.messages.LMManualControlRequest.SCHEDULED_START );
				msg.setYukonID(Integer.parseInt(request.getParameter("program")));
				msg.setNotifyTime(notifyCal);
				msg.setStartTime(startCal);
				msg.setStopTime(stopCal);
				msg.setAddditionalInfo(request.getParameter("comments"));
				conn.write(msg);

				if (checker != null)
					checker.clear();
				response.sendRedirect("oper_mand.jsp?pending=new");
				return;
			}
		}
	}
%>

<html>
<head>
<META NAME="robots" CONTENT="noindex, nofollow">
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">

</head>

<body text="#000000" link="#000000" vlink="#000000" alink="#000000">
<table width="800" border="0" cellspacing="0" cellpadding="0">
  <tr> 
   
    <td width="650" valign="top"> 
     
      <p>   
        <%
        if( tab.equalsIgnoreCase("history") )
        {   
        %>
        <%@ include file="include/oper_mand_history.jsp" %>
        <%
        }
        else
        if( tab.equalsIgnoreCase("historydetail") )
        {
        %>
        <%@ include file="include/oper_mand_history_detail.jsp" %>
        <%
        }
        else
        if( tab.equalsIgnoreCase("programs") )
        {
        %>
        <%@ include file="include/oper_mand_programs.jsp" %>
        <%
        }
        else
        if( tab.equalsIgnoreCase("new") )
        {
        %>
        <%@ include file="include/oper_mand_new.jsp" %>
        <%
        }
		else    
		if( tab.equalsIgnoreCase("newconfirm") )
		{
		%>
        <%@ include file="include/oper_mand_new_confirm.jsp" %>
        <%
		}
        else
        if( tab.equalsIgnoreCase("profile") )
        {
        %>
        <%@ include file="include/customer_mand_profile.jsp" %>
        <%
        }
        else               
        if( tab.equalsIgnoreCase("current") || true)
        {
        %>
        <%@ include file="include/oper_mand_current.jsp" %>
        <%
        }
        
      %>
      </p>
    </td>
  </tr>
</table>
<!-- Add menus -->   
</body>
</html>
</cti:checkRole>