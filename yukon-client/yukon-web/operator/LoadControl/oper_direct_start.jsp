<%@ include file="include/oper_header.jsp" %>
<%@ include file="include/oper_trendingheader.jsp" %>
<%@ page import="com.cannontech.loadcontrol.data.LMProgramDirect" %>
<%@ page import="com.cannontech.loadcontrol.data.LMProgramDirectGear" %>
<%@ page import="java.util.Calendar" %>
<%@ taglib uri="/WEB-INF/struts.tld" prefix="struts" %>
<%@ taglib uri="/WEB-INF/cti.tld" prefix="cti" %>
<jsp:useBean id="checker" scope="session" class="com.cannontech.validate.PageBean"/>
<%
/* Parameters:
   id - the id of the program e to start
   
   Attempts to get a reference to the program with programid == id
   If the ok button is clicked a message is sent to the load control server
   If the cancel button is clicked the user is directed back to program summary page
*/
   java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("HH:mm");
	timeFormat.setTimeZone(tz);
	
   LMProgramDirect program = null;
   java.util.Vector gears = null; //will contain LMProgramDirectGear
   java.util.Date now = new java.util.Date();
      
    //What program are we dealing with?
    int programID = -1;

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
		   checker.set( "STARTRADIO", "now" );
		   checker.set( "STOPRADIO", "now" );
		   checker.set( "STARTTIME", timeFormat.format(now) );
		   checker.set( "STOPTIME", timeFormat.format( new java.util.Date( now.getTime() + 14400000)) );
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
			response.sendRedirect( "oper_direct_start.jsp?id=" + programID + "&error=true" );
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
			response.sendRedirect( "oper_direct_start.jsp?id=" + programID + "&error=true" );
		}

        String gearName = request.getParameter("gear");
        int gearIndex = -1;

        LMProgramDirect[] allPrograms = cache.getDirectPrograms();

        for( int i = 0; i < allPrograms.length; i++ )
        {
            if( allPrograms[i].getYukonID().intValue() == programID )
            {
                java.util.Vector gVec = allPrograms[i].getDirectGearVector();

                for( int j = 0; j < gVec.size(); j++ )
                {
                    LMProgramDirectGear g = (LMProgramDirectGear) gVec.get(j);
                    if( g.getGearName().equalsIgnoreCase(gearName) )
                    {
                        gearIndex = j+1;
                        break;
                    }
                }
            }

            if( gearIndex >= 0 )
                break;
        }
                
		if (valid)
		{		
			//Send the message to loadmanagement	           
            com.cannontech.loadcontrol.messages.LMManualControlMsg msg = new com.cannontech.loadcontrol.messages.LMManualControlMsg();
        	msg.setCommand(com.cannontech.loadcontrol.messages.LMManualControlMsg.SCHEDULED_START);
	        msg.setYukonID(programID);
	        msg.setStartGear(gearIndex);
                       
            java.util.Date current = new java.util.Date();
            java.util.GregorianCalendar currentCal = new java.util.GregorianCalendar();
            currentCal.setTime(current);            

            startCal.set( Calendar.YEAR, currentCal.get( Calendar.YEAR ) );
            startCal.set( Calendar.DAY_OF_YEAR, currentCal.get( Calendar.DAY_OF_YEAR ) );

            stopCal.set( Calendar.YEAR, currentCal.get( Calendar.YEAR ) );
            stopCal.set( Calendar.DAY_OF_YEAR, currentCal.get( Calendar.DAY_OF_YEAR ) );

            msg.setStartTime(startCal);
            msg.setStopTime(stopCal);
	        
            com.cannontech.loadcontrol.LoadControlClientConnection conn = cs.getConnection();
            conn.write(msg);
                           
            checker.clear();
            
             response.sendRedirect( "oper_direct.jsp?pending=true");
		}
	}

    LMProgramDirect[] allPrograms = cache.getDirectPrograms();

    for( int i = 0; i < allPrograms.length; i++ )
    {
        if( allPrograms[i].getYukonID().intValue() == programID )
        {
            program = allPrograms[i];

            // Find all the gears for this program
            gears = program.getDirectGearVector();
            break;
        }
    }
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

<body class="Background" text="#FFFFFF" leftmargin="0" topmargin="0">
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
                <td width="253" height = "28" class="PageHeader">&nbsp;&nbsp;&nbsp;Load 
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
		  <td width="1" height="102" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
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
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF">
		   <table width="600" border="0" cellspacing="0" cellpadding="0">
<struts:form name="checker" type="com.cannontech.validate.PageBean" action="oper_direct_start.jsp" onSubmit="return validForm(this)"> 
  <tr> 
                <td width="650" class="TitleHeader"> 
                  <div align="center"><br>DIRECT CONTROL - START PROGRAM<br><br>
                  </div>
                  <table width="225" border="1" cellspacing="0" cellpadding="5" align="center" bgcolor="#FFFFFF">
        <tr> 
        
          <td> 
            <p>&nbsp;<span class="TableCell"><b>Start:</b></span></p>
            <p> 
			<table border="0" cellspacing="0" cellpadding="3" >
                  <tr>
                    <td>
                        <span class="TableCell">Gear:</span>
                    </td>
                    <td>  
                        <select name="gear">
                        <%
                            for( int i = 0; i < gears.size(); i++ ) 
                            {
                                out.print("<option>" + ((LMProgramDirectGear) gears.get(i)).getGearName());                       
                            }
                        %>                  
                        </select>     
                    </td>
                 </tr>
                 </table>
            <table border="0" cellspacing="0" cellpadding="5">
              <tr> 
                <td width="16%"> <span class="TableCell"><struts:radio property="STARTRADIO" value="now"/> 
                  </span></td>
                <td width="25%"> <span class="TableCell">Now:</span></td>
                <td width="59%">&nbsp; </td>
              </tr>
              <tr> 
                <td width="16%"> <span class="TableCell"><struts:radio property="STARTRADIO" value="time"/> 
                  </span></td>
                <td> <span class="TableCell">Time:</span></td>
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
                  <table width="225" border="1" cellspacing="0" cellpadding="5" align="center">
        <tr> 
          <td width="100%" bgcolor="#ffffff"> 
            <p><span class="TableCell">&nbsp;<b>Stop:</b></span></p>
            <p> 
            <table width="93%" border="0" cellspacing="0" cellpadding="5">
              <tr> 
                <td width="16%"> <span class="TableCell"><struts:radio property="STOPRADIO" value="now"/> 
                  </span></td>
                <td> <span class="TableCell">Time:</span></td>
                <td> <span class="TableCell"><struts:text property="STOPTIME" size="10" pattern="@time"/> 
                  </span></td>
                <td class="TableCell"><%= tz.getDisplayName(tz.inDaylightTime(new java.util.Date()), TimeZone.SHORT) %>
                </td>
              </tr>
              <tr> <cti:errormsg colSpan="3"><span class = "TableCell"> <%= checker.getError("STOPTIME") %></span>
                </cti:errormsg> </tr>
            </table>
          </td>
        </tr>
      </table>
                  <table width="235" border="0" cellspacing="5" cellpadding="0" align="center">
                    <tr> 
                      <td height="25" align = "right" width ="110"> 
                        <input type="submit" value="    OK    " border="0" name="image">
                      </td>
                      <td height="25" align = "left" width = "110"> 
                        <input type = "button" value="Cancel" name = "cancel" onclick = "goBack()">
                      </td>
                    </tr>
                  </table>
      <p>&nbsp;</p>
    </td>
  </tr>
</table>
<P>

<INPUT NAME="SUBMITTED" TYPE="hidden" VALUE="true">
<INPUT NAME="ID" TYPE="hidden" VALUE="<%= programID %>"> 
<INPUT NAME="ACTION" TYPE="hidden" VALUE="STARTSTOP">
<struts:hidden property="STARTAT" value="0"/>
<struts:hidden property="STOPAT" value="0"/>
<INPUT NAME="URL" TYPE="hidden" VALUE="<%=request.getContextPath()%>/operator/LoadControl/oper_direct.jsp?pending=true">
</struts:form>
          </td>
        <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
    </tr>
      </table>
    </td>
	</tr>
</table>
<br>
</body>

</html>
