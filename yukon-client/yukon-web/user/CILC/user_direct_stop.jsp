<%@ include file="../include/user_header.jsp" %>
<html>
<head>
<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
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
  location = "user_direct.jsp"
  }

  //End hiding script -->
  </SCRIPT>
</head>
<%@ page import="com.cannontech.loadcontrol.data.LMProgramDirect" %>
<%@ page import="com.cannontech.servlet.LCConnectionServlet" %>
<%@ page import="com.cannontech.web.loadcontrol.LoadcontrolCache" %>
<%@ page import="java.util.Calendar" %>
<%@ taglib uri="/WEB-INF/struts.tld" prefix="struts" %>
<%@ taglib uri="/WEB-INF/cti.tld" prefix="cti" %>
<jsp:useBean id="checker" scope="session" class="com.cannontech.validate.PageBean"/>
<%
/* Parameters:
   id - the id of the program to stop
   
   Attempts to get a reference to the program with programid == id
   Send a msg to the load control server to stop the program
*/
   java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("HH:mm");

   LMProgramDirect program = null;
   java.util.Date now = new java.util.Date();
      
    //What program are we dealing with?
    int programID = -1;

    LCConnectionServlet cs = (LCConnectionServlet) application.getAttribute(LCConnectionServlet.SERVLET_CONTEXT_ID);
    LoadcontrolCache cache = cs.getCache();

	if (request.getParameter("SUBMITTED") == null)
	{
		try
		{
			String programIDStr = request.getParameter("id");
			if( programIDStr != null )
				programID = Integer.parseInt(programIDStr);
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
			String programIDStr = request.getParameter("ID");
			if( programIDStr != null )
				programID = Integer.parseInt(programIDStr);
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
			response.sendRedirect( "user_direct_stop.jsp?id=" + programID + "&error=true" );
		}

		if (valid)
		{
						            
		java.util.Date stopTime = null;

		try {			
			stopTime = com.cannontech.validate.PageBean.parseTime( checker.get("STOPAT") );            
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	   		    

            com.cannontech.loadcontrol.messages.LMManualControlMsg msg = new com.cannontech.loadcontrol.messages.LMManualControlMsg();
        	msg.setCommand(com.cannontech.loadcontrol.messages.LMManualControlMsg.SCHEDULED_STOP);
	        msg.setYukonID(programID);
	        msg.setStartGear(1);
                        
            java.util.Date current = new java.util.Date();
            java.util.GregorianCalendar currentCal = new java.util.GregorianCalendar();
            currentCal.setTime(current);
           
            java.util.GregorianCalendar stopCal = new java.util.GregorianCalendar();		    

            if( stopTime != null )
		        stopCal.setTime(stopTime);
            else
                stopCal.setTime(current);

            stopCal.set( Calendar.YEAR, currentCal.get( Calendar.YEAR ) );
            stopCal.set( Calendar.DAY_OF_YEAR, currentCal.get( Calendar.DAY_OF_YEAR ) );
          
            msg.setStopTime(stopCal);
	        
            com.cannontech.loadcontrol.LoadControlClientConnection conn = cs.getConnection();
            conn.write(msg);
                       
            checker.clear();
            
            response.sendRedirect( "user_direct.jsp?pending=true");
		}
	}

    LMProgramDirect[] allPrograms = cache.getDirectPrograms();

    for( int i = 0; i < allPrograms.length; i++ )
    {
        if( allPrograms[i].getYukonID().intValue() == programID )
        {
            program = allPrograms[i];
            break;
        }
    }

%>
<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="150" height="102" background="../../WebConfig/yukon/MomWide.jpg">&nbsp;</td>
          <td valign="top" height="102"> 
            <table width="609" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>" defaultvalue="yukon/DemoHeader.gif"/>">&nbsp;</td>
              </tr>
              <tr>
			  <td width="265" height = "28" class="PageHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;User 
                  Control</td> 
                <td width="253" valign="middle">&nbsp;</td>
                <td width="58" valign="middle">&nbsp;</td>
                <td width="57" valign="middle"> 
                  <div align="left"><span class="MainText"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log 
                    Off</a>&nbsp;</span></div>
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
          <td width="150" bgcolor="#000000" height="1"></td>
          <td width="1" bgcolor="#000000" height="1"></td>
          <td width="609" bgcolor="#000000" height="1"></td>
		  <td width="1" bgcolor="#000000" height="1"></td>
        </tr>
        <tr> 
          <td  valign="top" width="150"> 
		  <% String pageName = "user_direct.jsp"; %>
          <%@ include file="include/nav.jsp" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          
		  <td width="609" valign="top" bgcolor="#FFFFFF"> 
<table width="609" border="0" cellspacing="0" cellpadding="0">
<struts:form name="checker" type="com.cannontech.validate.PageBean" action="user_direct_stop.jsp" onSubmit="return validForm(this)"> 
  <tr> 
                <td width="600"> 
                  <p><br>
                  </p>
                  <table width="200" border="1" cellpadding="5" cellspacing="0" align="center">
                    <tr>
                      <td><span class="TableCell"><b>Stop</b></span>
                        <table width="95%" border="0" cellpadding="0">
                          <tr>
                            <td width="15%"><struts:radio property="STOPRADIO" value="now"/></td>
                            <td width="24%" class="TableCell">Now:</td>
                            <td width="61%">&nbsp;</td>
                          </tr>
                          <tr>
                            <td width="15%"><struts:radio property="STOPRADIO" value="time"/></td>
                            <td width="24%" class="TableCell">Time:</td>
                            <td width="61%" class="TableCell"><struts:text property="STOPTIME" size="10" pattern="@time"/></td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                  <br>
                  <table width="210" border="0" cellspacing="5" cellpadding="0" align="center">
        <tr>
          <td> 
            <div align="right">
              <input type="submit" value="   OK   " name="OK">
            </div>
          </td>
		  <td height="25">
          <input type = "button" value="Cancel" name = "cancel" onClick = "goBack()">
          </td>
        </tr>
      </table>
     
      <p>&nbsp;</p></td>
  </tr>
</table>
<P>

<INPUT NAME="SUBMITTED" TYPE="hidden" VALUE="true">
<INPUT NAME="ID" TYPE="hidden" VALUE="<%= programID %>">
<INPUT NAME="ACTION" TYPE="hidden" VALUE="STOP">
<struts:hidden property="STARTAT" value="-1"/> <struts:hidden property="STOPAT" value="0"/> 
<INPUT NAME="URL" TYPE="hidden" VALUE="<%=request.getContextPath()%>/user/CILC/user_direct.jsp?pending=true">
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
