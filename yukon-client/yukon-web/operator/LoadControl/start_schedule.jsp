<%@ include file="include/oper_header.jsp" %>

<%@ page import="com.cannontech.message.macs.message.Schedule" %>
<%@ page import="com.cannontech.yukon.concrete.ResourceFactory" %>
<%@ page import="com.cannontech.yukon.IMACSConnection" %>

<%@ taglib uri="/WEB-INF/struts.tld" prefix="struts" %>
<%@ taglib uri="/WEB-INF/cti.tld" prefix="cti" %>
<jsp:useBean id="checker" scope="session" class="com.cannontech.validate.PageBean"/>
<%
/* Parameters:
   id - the id of the schedule to start
   
   Attempts to get a reference to the schedule with scheduleid == id
   If the ok button is clicked a request is sent to the ScheduleController servlet
   to start the schedule.
   If the cancel button is clicked the user is directed back to ScheduleSummary.jsp
*/
   java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("HH:mm");
   timeFormat.setTimeZone(tz);
   
   Schedule schedule = null;  
   java.util.Date now = new java.util.Date();
      
    //What program are we dealing with?
    int scheduleID = -1;

	if (request.getParameter("SUBMITTED") == null)
	{
		try
		{
			String scheduleIDStr = request.getParameter("id");
			if( scheduleIDStr != null )
				scheduleID = Integer.parseInt(scheduleIDStr);
		}
		catch( NumberFormatException nfe )
		{
			nfe.printStackTrace();
		}

		if (request.getParameter("error") == null)
		{
		   checker.clear();
		   checker.set( "STARTRADIO", "now" );
		   checker.set( "STOPRADIO", "now" );
		   checker.set( "STARTTIME", timeFormat.format(now) );
		   checker.set( "STOPTIME", timeFormat.format( new java.util.Date( now.getTime() + 14400000)) );
		}
	}
	else {
		try
		{
			String scheduleIDStr = request.getParameter("ID");
			if( scheduleIDStr != null )
				scheduleID = Integer.parseInt(scheduleIDStr);
		}
		catch( NumberFormatException nfe )
		{
			nfe.printStackTrace();
		}

		java.util.Enumeration enum = request.getParameterNames();
		while (enum.hasMoreElements()) {
			String name = (String)enum.nextElement();
			String[] value = request.getParameterValues(name);
			if (value.length == 1)
				checker.set(name, value[0]);
			else
				checker.set(name, value);
		}

		boolean valid = true;

		if (!checker.validate()) {
			valid = false;
			response.sendRedirect( "start_schedule.jsp?id=" + scheduleID + "&error=true" );
		}

		java.util.Date startTime = null;
		java.util.Date stopTime = null;

		try {
			if ( ((String)checker.get("STARTAT")).equals("0") )
				startTime = new java.util.Date();
			else
				startTime = ServletUtil.parseDateStringLiberally(checker.get("STARTAT"), tz);

			stopTime = ServletUtil.parseDateStringLiberally(checker.get("STOPAT"), tz);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		java.util.GregorianCalendar startCal = new java.util.GregorianCalendar();
		java.util.GregorianCalendar stopCal = new java.util.GregorianCalendar();
		startCal.setTime(startTime);
		stopCal.setTime(stopTime);

		if ( startCal.get(java.util.Calendar.HOUR_OF_DAY) > stopCal.get(java.util.Calendar.HOUR_OF_DAY) ||
			 startCal.get(java.util.Calendar.HOUR_OF_DAY) == stopCal.get(java.util.Calendar.HOUR_OF_DAY) &&
			 startCal.get(java.util.Calendar.MINUTE) > stopCal.get(java.util.Calendar.MINUTE) )
		{
			valid = false;
			checker.setError("STOPTIME", "Stop time must be later than start time");
			response.sendRedirect( "start_schedule.jsp?id=" + scheduleID + "&error=true" );
		}

		if (valid)
		{
			// The server expects time to be in his format, don't set this timezone to the users
			java.text.SimpleDateFormat serverTimeFormat = new java.text.SimpleDateFormat("HH:mm");
			response.sendRedirect( "/servlet/ScheduleController?ID=" + scheduleID + "&ACTION=" + request.getParameter("ACTION") +
								   "&STARTAT=" + serverTimeFormat.format(startTime) + "&STOPAT=" + serverTimeFormat.format(stopTime) + "&URL=" + request.getParameter("URL") );
			checker.clear();
		}
	}

	IMACSConnection conn = ResourceFactory.getIYukon().getMACSConnection();

	if( conn != null )
	{
		Schedule[] allSchedules = conn.retrieveSchedules();       
		for( int i = 0; i < allSchedules.length; i++ )
		{
			if( allSchedules[i].getId() == scheduleID )
			{
				schedule = allSchedules[i];
				break;
			}
		}
	}

   // !Should check for null schedule!

%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
 <SCRIPT LANGUAGE="JAVASCRIPT" TYPE="TEXT/JAVASCRIPT">
  <!-- Hide the script from older browsers
  
  function validForm(form)
  {    
     form.STARTAT.value = form.STARTTIME.value;
     form.STOPAT.value = form.STOPTIME.value;

     if( form.STARTRADIO[0].checked)
        form.STARTAT.value = 0;

     return true;
  }
    function goBack() {
  location = "<%=request.getContextPath()%>/operator/LoadControl/oper_direct.jsp"
  }

  //End hiding script -->
  </SCRIPT>
</head>

<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="../../WebConfig/yukon/LoadImage.jpg">&nbsp;</td>
          <td valign="bottom" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>"/>">&nbsp;</td>
              </tr>
              <tr> 
                <td width="310" height = "28" class="PageHeader">&nbsp;&nbsp;&nbsp;Load 
                  Response</td>
                <td width="235" valign="middle">&nbsp;</td>
                  <td width="58" valign="middle"> 
                    <div align="center"><span class="MainText"><a href="../Operations.jsp" class="Link3">Home</a></span></div>
                  </td>
                  <td width="57" valign="middle"> 
                    <div align="left"><span class="MainText"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log Off</a>&nbsp;</span></div>
                  </td>
              </tr>
            </table>
          </td>
		  <td width="1" height="102" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center" bordercolor="0">
        <tr> 
          <td width="101" bgcolor="#000000" height="1"></td>
          <td width="1" bgcolor="#000000" height="1"></td>
          <td width="657" bgcolor="#000000" height="1"></td>
		  <td width="1" bgcolor="#000000" height="1"></td>
        </tr>
        <tr> 
          <td  valign="top" width="101"> 
            <table width="101" border="0" cellspacing="0" cellpadding="6" height="200">
              <tr> 
                <td height="20" valign="top"> 
                  <div align="center">
                    <p></p>
                    </div>
                </td>
              </tr>
            </table>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF">
            <table width="657" border="0" cellspacing="0" cellpadding="0">
              <struts:form name="checker" type="com.cannontech.validate.PageBean" action="start_schedule.jsp" onSubmit="return validForm(this)"> 
              <tr> 
                <td width="650" class="TitleHeader"> 
                  <p align="center"><br>DIRECT CONTROL - START SCHEDULE
                  <p align="center"> 
                  <center>
                    <table width="225" border="1" cellspacing="0" cellpadding="5">
                      <tr> 
                        <td class="TableCell" width="100%"> 
                          <p>&nbsp;<span class="TableCell"><b>Start:</b></span></p>
                          <p> 
                          <table width="93%" border="0" cellspacing="0" cellpadding="5">
                            <tr> 
                              <td width="16%"> <span class="TableCell"><struts:radio property="STARTRADIO" value="now"/> 
                                </span></td>
                              <td width="25%" class="TableCell"> Now:</td>
                              <td width="59%">&nbsp; </td>
                            </tr>
                            <tr> 
                              <td width="16%"> <span class="TableCell"><struts:radio property="STARTRADIO" value="time"/> 
                                </span></td>
                              <td class="TableCell"> Time:</td>
                              <td> <span class="TableCell"><struts:text property="STARTTIME" size="10" pattern="@time"/> 
                                </span></td>
                              <td class="TableCell"><%= tz.getDisplayName(tz.inDaylightTime(new java.util.Date()), TimeZone.SHORT) %>
                              </td>
                            </tr>
                            <tr> <cti:errormsg colSpan="3"> <span class = "TableCell"><%= checker.getError("STARTTIME") %></span>
                              </cti:errormsg> </tr>
                          </table>
                        </td>
                      </tr>
                    </table>
                    
                    <br>
                  </center>
                  <center>
                    <table width="225" border="1" cellspacing="0" cellpadding="5">
                      <tr> 
                        <td  class="TableCell" width="100%"> 
                          <p>&nbsp;<b>Stop:</b></p>
                          <p> 
                          <table width="93%" border="0" cellspacing="0" cellpadding="5">
                            <tr> 
                              <td width="16%"> <span class="TableCell"><struts:radio property="STOPRADIO" value="now"/> 
                                </span></td>
                              <td class="TableCell"> Time:</td>
                              <td> <span class="TableCell"><struts:text property="STOPTIME" size="10" pattern="@time"/> 
                                </span></td>
                              <td class="TableCell"><%= tz.getDisplayName(tz.inDaylightTime(new java.util.Date()), TimeZone.SHORT) %>
                              </td>
                            </tr>
                            <tr> <cti:errormsg colSpan="3"> <span class = "TableCell"><%= checker.getError("STOPTIME") %></span> 
                              </cti:errormsg> </tr>
                          </table>
                        </td>
                      </tr>
                    </table>
                    <table width="235" border="0" cellspacing="5" cellpadding="0">
                      <tr> 
                        <td> 
                          <div align="right">
                            <input type="submit" value="   OK   " border="0" name="image">
                          </div>
                        </td>
						  <input name="SUBMITTED" type="hidden" value="true">
                          
                        <td> 
                          <div align="left">
                            <input type = "button" value="Cancel" name = "cancel" onclick = "goBack()">
                          </div>
                        </td>
                        
                      </tr>
                    </table>
                    <p>&nbsp;</p>
                  </center>
                  <p align=RIGHT> <br>
                </td>
              </tr>
            </table>
			<input name="ID" type="hidden" value="<%= scheduleID %>">
              <input name="ACTION" type="hidden" value="STARTSTOP">
              <struts:hidden property="STARTAT" value="0"/> <struts:hidden property="STOPAT" value="0"/> 
              <input name="URL" type="hidden" value="<%= java.net.URLEncoder.encode(request.getContextPath() + "/operator/LoadControl/oper_direct.jsp?pending=true") %>">
              </struts:form> 
          </td>
        <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
    </tr>
      </table>
    </td>
	</tr>
</table>
<br>
</body>

</html>
