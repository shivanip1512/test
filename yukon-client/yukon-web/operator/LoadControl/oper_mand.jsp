<%@ include file="oper_header.jsp" %> 
<%@ include file="oper_trendingheader.jsp" %>

<%
    //Duplicate decl in oper_trendingheader.jsp :P
    //String tab = request.getParameter("tab");
    tab = request.getParameter("tab");

    if( tab == null )
        tab = "";
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
				checker.set("program", programIds[0]);
				checker.set("notifydate", mandDateFormat.format(com.cannontech.util.ServletUtil.getToday()));
				checker.set("notifytime", mandTimeFormat.format(new java.util.Date()));
				checker.set("curtaildate", mandDateFormat.format(com.cannontech.util.ServletUtil.getToday()));
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
				}
			}
		}
	}
	else if (tab.equalsIgnoreCase("newconfirm")) {	
		if (request.getParameter("confirmed") == null) {	// not confirmed yet
			programStr = checker.get("program");
			notifyDateStr = mandDateFormat.format( com.cannontech.validate.PageBean.parseDate(checker.get("notifydate")) );
			notifyTimeStr = mandTimeFormat.format( com.cannontech.validate.PageBean.parseTime(checker.get("notifytime")) );
			curtailDateStr = mandDateFormat.format( com.cannontech.validate.PageBean.parseDate(checker.get("curtaildate")) );
			curtailTimeStr = mandTimeFormat.format( com.cannontech.validate.PageBean.parseTime(checker.get("curtailtime")) );
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

				if (result != null && result[0] != null && result[0][0] != null)
				{
					java.util.Date stopCurtail = (java.util.Date)result[0][0];

					if (stopCurtail.getTime() > System.currentTimeMillis())
					{
						if (checker == null)
							response.sendRedirect("oper_mand.jsp");
						checker.setError("duplicate_error", "An active curtailment program has already existed! New program cannot be created until the current one expires.");
						response.sendRedirect("oper_mand.jsp?tab=new&error=true");
						duplicateError = true;
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
			
			notifyCal.setTime( com.cannontech.validate.PageBean.parseDate(request.getParameter("notifydate")) );
			timeCal.setTime( com.cannontech.validate.PageBean.parseTime(request.getParameter("notifytime")) );
			notifyCal.set(Calendar.HOUR, timeCal.get(Calendar.HOUR_OF_DAY));
			notifyCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
			
			startCal.setTime( com.cannontech.validate.PageBean.parseDate(request.getParameter("startdate")) );
			timeCal.setTime( com.cannontech.validate.PageBean.parseTime(request.getParameter("starttime")) );
			startCal.set(Calendar.HOUR, timeCal.get(Calendar.HOUR_OF_DAY));
			startCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
			
			// check that notify date is before start date
			if ( !notifyCal.before(startCal) ) {
				if (checker == null)
					response.sendRedirect("oper_mand.jsp");
				checker.setError("notifytime", "Notify time must be earlier than curtail time");
				response.sendRedirect("oper_mand.jsp?tab=new&error=true");
			}
			else if (!duplicateError)
			{
				long durationMillis = Integer.parseInt(request.getParameter("duration")) * 60 * 60 * 1000;
				stopCal.setTime( new java.util.Date(startCal.getTime().getTime() + durationMillis) );

				com.cannontech.loadcontrol.LoadControlClientConnection conn = cs.getConnection();
				com.cannontech.loadcontrol.messages.LMManualControlMsg msg = new com.cannontech.loadcontrol.messages.LMManualControlMsg();
				msg.setCommand( com.cannontech.loadcontrol.messages.LMManualControlMsg.SCHEDULED_START );
				msg.setYukonID(Integer.parseInt(request.getParameter("program")));
				msg.setNotifyTime(notifyCal);
				msg.setStartTime(startCal);
				msg.setStopTime(stopCal);
				msg.setAddditionalInfo(request.getParameter("comments"));
				conn.write(msg);

				if (checker != null)
					checker.clear();
				response.sendRedirect("oper_mand.jsp?pending=new");
			}
		}
	}
%>

<html>
<head>
<META NAME="robots" CONTENT="noindex, nofollow">
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
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
        <%@ include file="oper_mand_history.jsp" %>
        <%
        }
        else
        if( tab.equalsIgnoreCase("historydetail") )
        {
        %>
        <%@ include file="oper_mand_history_detail.jsp" %>
        <%
        }
        else
        if( tab.equalsIgnoreCase("programs") )
        {
        %>
        <%@ include file="oper_mand_programs.jsp" %>
        <%
        }
        else
        if( tab.equalsIgnoreCase("new") )
        {
        %>
        <%@ include file="oper_mand_new.jsp" %>
        <%
        }
		else
		if( tab.equalsIgnoreCase("newconfirm") )
		{
		%>
        <%@ include file="oper_mand_new_confirm.jsp" %>
        <%
		}
        else
        if( tab.equalsIgnoreCase("profile") )
        {
        %>
        <%@ include file="customer_mand_profile.jsp" %>
        <%
        }
        else               
        if( tab.equalsIgnoreCase("current") || true)
        {
        %>
        <%@ include file="oper_mand_current.jsp" %>
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
