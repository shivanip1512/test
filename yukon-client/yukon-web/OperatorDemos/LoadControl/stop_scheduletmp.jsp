<%@ include file="oper_header.jsp" %>
<%@ include file="oper_trendingheader.jsp" %>
<%@ page import="com.cannontech.message.macs.message.Schedule" %>
<%@ taglib uri="/WEB-INF/struts.tld" prefix="struts" %>
<%@ taglib uri="/WEB-INF/cti.tld" prefix="cti" %>
<jsp:useBean id="checker" scope="session" class="com.cannontech.validate.PageBean"/>
<%
/* Parameters:
   id - the id of the schedule to stop
   
   Attempts to get a reference to the schedule with scheduleid == id
   If the ok button is clicked a request is sent to the ScheduleController servlet
   to stop the schedule.
   If the cancel button is clicked the user is directed back to ScheduleSummary.jsp
*/
   java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("HH:mm");

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
		   checker.set("STOPRADIO", "now");
		   checker.set("STOPTIME", timeFormat.format(now) );
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
			response.sendRedirect( "stop_schedule.jsp?id=" + scheduleID + "&error=true" );
		}

		if (valid)
		{
			response.sendRedirect( "/servlet/ScheduleController?ID=" + scheduleID + "&ACTION=" + request.getParameter("ACTION") +
								   "&STARTAT=" + checker.get("STARTAT") + "&STOPAT=" + checker.get("STOPAT") + "&URL=" + request.getParameter("URL") );
			checker.clear();
		}
	}

	com.cannontech.servlet.MACSConnectionServlet connContainer = (com.cannontech.servlet.MACSConnectionServlet)
	   application.getAttribute(com.cannontech.servlet.MACSConnectionServlet.SERVLET_CONTEXT_ID);

	com.cannontech.macs.MACSClientConnection conn = null;

	if( connContainer != null )
	{
		conn = connContainer.getConnection();
	}

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
   // !Check for null schedule!

%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../demostyle.css" type="text/css">
<SCRIPT LANGUAGE="JAVASCRIPT" TYPE="TEXT/JAVASCRIPT">
  <!-- Hide the script from older browsers
  
  function validForm(form)
  {    
     form.STOPAT.value = form.STOPTIME.value;

     if( form.STOPRADIO[0].checked)
        form.STOPAT.value = 0;
          
      return true;
  }
  function goBack() {
  location = "/OperatorDemos/LoadControl/oper_direct.jsp"
  }

  //End hiding script -->
  </SCRIPT>
</head>
<body class="Background" text="#FFFFFF" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td> 
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="LoadImage.jpg">&nbsp;</td>
          <td valign="bottom" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../Header.gif">&nbsp;</td>
              </tr>
              <tr> 
                <td width="253" height = "28" class="Header3">&nbsp;&nbsp;&nbsp;Load 
                  Control</td>
                <td width="235" valign="middle">&nbsp;</td>
                  <td width="58" valign="middle"> 
                    <div align="center"><span class="Main"><a href="../Operations.jsp" class="Link3">Home</a></span></div>
                  </td>
                  <td width="57" valign="middle"> 
                    <div align="left"><span class="Main"><a href="../../login.jsp" class="Link3">Log 
                      Off</a>&nbsp;</span></div>
                  </td>
              </tr>
            </table>
          </td>
          <td width="1" height="102" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
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
          <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <table width="657" border="0" cellspacing="0" cellpadding="0">
              <struts:form name="checker" type="com.cannontech.validate.PageBean" action="stop_schedule.jsp" onSubmit="return validForm(this)"> 
              <tr> 
                <td width="650" class="Main"> 
                  <p align="center"><b><br>
                    STOP SCHEDULE</b> 
                  <p align="center"> 
                  <center>
                    <table width="225" border="1" cellspacing="0" cellpadding="5">
                      <tr> 
                        <td  class="TableCell" width="100%"> 
                          <p>&nbsp;<b>Stop:</b><br>
                          <table border="0" cellspacing="0" cellpadding="5">
                            <tr> 
                              <td width="16%"> <span class="TableCell"><struts:radio property="STOPRADIO" value="now"/> 
                                </span></td>
                              <td width="25%"> <span class="TableCell"> Now:</span></td>
                              <td width="59%">&nbsp; </td>
                            </tr>
                            <tr> 
                              <td width="16%"> <span class="TableCell"><struts:radio property="STOPRADIO" value="time"/> 
                                </span></td>
                              <td width="25%"> <span class="TableCell"> Time:</span></td>
                              <td width="59%"> <span class="TableCell"><struts:text property="STOPTIME" size="10" pattern="@time"/> 
                                </span></td>
                            </tr>
                            <tr> <cti:errormsg colSpan="2"> <%= checker.getError("STOPTIME") %> 
                              </cti:errormsg> </tr>
                          </table>
                        </td>
                      </tr>
                    </table>
                    <table width="235" border="0" cellspacing="5" cellpadding="0">
                      <tr> 
                        <td width="73" align = "left"> 
                          <input type="submit" value="   OK   " border="0" name="image">
                        </td>
                        <td width="147" valign = "bottom"> 
                      <input type = "button" value="Cancel" name = "cancel" onclick = "goBack()">
                        </td>
                      </tr>
                    </table>
                  </center>
                  <p align=RIGHT> <br>
                    <br clear="ALL">
                </td>
              </tr>
              <input name="SUBMITTED" type="hidden" value="true">
              <input name="ID" type="hidden" value="<%= scheduleID %>">
              <input name="ACTION" type="hidden" value="STOP">
              <struts:hidden property="STARTAT" value="-1"/> <struts:hidden property="STOPAT" value="0"/> 
              <input name="URL" type="hidden" value="/OperatorDemos/LoadControl/oper_direct.jsp?pending=true">
              </struts:form> 
            </table>
          </td>
          <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
        </tr>
      </table>
    </td>
  </tr>
</table>
<br>
</body>
</html>
