<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">

</head>

<body class="Background" text="#000000" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="../../WebConfig/yukon/LoadImage.jpg">&nbsp;</td>
          <td valign="bottom" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>" defaultvalue="yukon/DemoHeader.gif"/>">&nbsp;</td>
              </tr>
              <tr> 
                <td width="310" height = "28" class="PageHeader">&nbsp;&nbsp;&nbsp;Load Response</td>
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
                <td height="20" valign="top" align="center"> 
                  <form name="form1" method="get" action="oper_mand.jsp?tab=current">
                    <p><br>
                      <input type="submit" name="tab" value="Current">
                    </p>
                  </form>
                  <form name="form1" method="get" action="oper_mand.jsp?tab=new">
                    <p> 
                      <input type="submit" name="tab" value="New">
                    </p>
                  </form>
                  <form name="form1" method="get" action="oper_mand.jsp?tab=history">
                    <p> 
                      <input type="submit" name="tab" value="History">
                    </p>
                  </form>
                  <form name="form1" method="get" action="oper_mand.jsp?tab=programs">
                    <p> 
                      <input type="submit" name="tab" value="Programs">
                    </p>
                  </form>
                  <p>
                </td>
              </tr>
            </table>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <p align="center" class="TitleHeader"><br>NOTIFICATION - HISTORY</p>
            <p align="center" class="MainText">Click on a program name to view the program details.</p>
      <table width="600" border="1" cellspacing="0" cellpadding="2" align="center">
          <tr> 
            <td width="25%" class="HeaderCell">Program</td>
            <td width="25%" class="HeaderCell">Notification Date/Time</td>
            <td width="25%" class="HeaderCell">Start Date/Time</td>
            <td width="25%" class="HeaderCell">Duration (Hour)</td>
          </tr>
          <%
	com.cannontech.web.history.CurtailHistory history = null;
	 LMProgramCurtailment[] curtailProgs = cache.getEnergyCompanyCurtailmentPrograms(energyCompanyID);
 
	try { 
		history = new com.cannontech.web.history.CurtailHistory(dbAlias); 
		com.cannontech.web.history.HCurtailProgramActivity[] activities = history.getCurtailProgramActivities();
       
		for (int i = 0; i < activities.length; i++)
		{
			for(int j = 0; curtailProgs != null && j < curtailProgs.length; j++ ) 
			{				
				com.cannontech.web.history.HCurtailProgram program = activities[i].getCurtailProgram();				
				if(program.getDeviceId() == curtailProgs[j].getYukonID().longValue()) {
            	String link = "oper_mand.jsp?tab=historydetail&prog=" + program.getDeviceId() + "&ref=" + activities[i].getCurtailReferenceId();                        
	%>  
          <tr> 
            <td width="25%" height="23" class="TableCell"><a href="<%= link %>" class="Link1"><%= program.getProgramName() %> </a></td>
            <td width="25%" height="23" class="TableCell"><%= mandTimeFormat.format( activities[i].getNotificationDateTime() ) + " " + tz.getDisplayName(tz.inDaylightTime(new java.util.Date()), TimeZone.SHORT) + "  " + mandDateFormat.format( activities[i].getNotificationDateTime() ) %></td>
            <td width="25%" height="23" class="TableCell"> <%= mandTimeFormat.format( activities[i].getCurtailmentStartTime() ) + " " + tz.getDisplayName(tz.inDaylightTime(new java.util.Date()), TimeZone.SHORT) + "  " + mandDateFormat.format( activities[i].getCurtailmentStartTime() ) %></td>
            <td width="25%" height="23" class="TableCell"><%= activities[i].getDuration() %></td>
          </tr>
          <%
				}
		   }
		}
	}
	catch (Exception e) {
		e.printStackTrace();
	}
	finally {
		history.gc();
	}
%>
        </table> 
        <p>&nbsp;
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
