<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
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
                <td width="253" height = "28" class="PageHeader">&nbsp;&nbsp;&nbsp;Load 
                  Response </td>
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
<table width="657" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td width="650" valign="top"> 
      <p align="center" class="TitleHeader"><br>NOTIFICATION - CURRENT</p>
      <p align="center" class="MainText">Click on a program name to view the program details.</p>
      <p align="center" class="SubtitleHeader">Today's Notification Summary - <%= datePart.format(today) %>
      <center>
        <table width="600" border="1" cellspacing="0" cellpadding="2" align="center">
          <tr> 
            <td width="25%" class="HeaderCell">Program</td>
            <td width="25%" class="HeaderCell">Notification Date/Time</td>
            <td width="25%" class="HeaderCell">Start Date/Time</td>
            <td width="25%" class="HeaderCell">Duration (Hour)</td>
          </tr>
          <%
                        {
                        
                                LMProgramCurtailment[] curtailProgs = cache.getEnergyCompanyCurtailmentPrograms(energyCompanyID);

                                for( int i = 0; i < curtailProgs.length; i++ )
                                {
                                    // Check if it is today                                   
                                    GregorianCalendar nowCal = new GregorianCalendar();
                                    GregorianCalendar progCal = new GregorianCalendar();

                                    nowCal.setTime(new Date());
                                    progCal.setTime(curtailProgs[i].getCurtailmentStartTime().getTime());

                                    if( nowCal.get( Calendar.YEAR ) == progCal.get(Calendar.YEAR) &&
                                        nowCal.get( Calendar.DAY_OF_YEAR ) == progCal.get(Calendar.DAY_OF_YEAR) )
                                    {                                    
                          %>
          <tr> 
           
            <td width="25%" class="TableCell"><a href="oper_mand.jsp?tab=current&prog=<%= curtailProgs[i].getYukonID() %>" class="Link1"><%= curtailProgs[i].getYukonName() %></a></td>
            <td width="25%" class="TableCell"><%= timePart.format(curtailProgs[i].getNotificationDateTime().getTime()) + " " + tz.getDisplayName(tz.inDaylightTime(new java.util.Date()), TimeZone.SHORT) + "  " + datePart.format(curtailProgs[i].getNotificationDateTime().getTime()) %></td>
            <td width="25%" class="TableCell"><%= timePart.format(curtailProgs[i].getCurtailmentStartTime().getTime()) + " " + tz.getDisplayName(tz.inDaylightTime(new java.util.Date()), TimeZone.SHORT) + "  " + datePart.format(curtailProgs[i].getCurtailmentStartTime().getTime()) %></td>
            <td width="25%" class="TableCell"><%= curtailProgs[i].getDuration() / 60 %></td>
          </tr>
          <%
                                    }
                                }
                        }
                          %>
        </table>
      </center>
      <p>&nbsp; 
                  <p align="center" class="SubtitleHeader">Tomorrow's Notification 
                    Summary - <%= datePart.format(tomorrow) %> 
                  <table width="600" border="1" cellspacing="0" cellpadding="2" align="center">
        <tr> 
          <td width="25%" class="HeaderCell">Program</td>
          <td width="25%" class="HeaderCell">Notification Date/Time</td>
          <td width="25%" class="HeaderCell">Start Date/Time</td>
          <td width="25%" class="HeaderCell">Duration (Hour)</td>
        </tr>
        <%  
                        {                        
                                LMProgramCurtailment[] curtailProgs = cache.getEnergyCompanyCurtailmentPrograms(energyCompanyID);
								
                                for( int i = 0; i < curtailProgs.length; i++ )
                                {
                                	LMProgramCurtailment prog = curtailProgs[i];
                                 	
                                    // Check if it is tomorrow                                   
                                    GregorianCalendar nowCal = new GregorianCalendar();
                                    GregorianCalendar progCal = new GregorianCalendar();

                                    nowCal.setTime(new Date());
                                    progCal.setTime(curtailProgs[i].getCurtailmentStartTime().getTime());
  
                                    if( nowCal.get( Calendar.YEAR ) == progCal.get(Calendar.YEAR) &&
                                        nowCal.get( Calendar.DAY_OF_YEAR )+1 == progCal.get(Calendar.DAY_OF_YEAR) )
                                    {                                     
                          %>
        <tr> 
         
          <td width="25%" height="23" class="TableCell"><a href="oper_mand.jsp?tab=current&prog=<%= curtailProgs[i].getYukonID() %>" class="Link1"><%= curtailProgs[i].getYukonName() %></a></td>
          <td width="25%" height="23" class="TableCell"><%= timePart.format(curtailProgs[i].getNotificationDateTime().getTime()) + " " + tz.getDisplayName(tz.inDaylightTime(new java.util.Date()), TimeZone.SHORT) + "  " + datePart.format(curtailProgs[i].getNotificationDateTime().getTime()) %></td>
          <td width="25%" height="23" class="TableCell"><%= timePart.format(curtailProgs[i].getCurtailmentStartTime().getTime()) + " " + tz.getDisplayName(tz.inDaylightTime(new java.util.Date()), TimeZone.SHORT) + "  " + datePart.format(curtailProgs[i].getCurtailmentStartTime().getTime()) %></td>
          <td width="25%" height="23" class="TableCell"><%= curtailProgs[i].getDuration() / 60 %></td>
        </tr>
        <%
                                    }
                                }
                        }
                          %>
      </table>
    <p>&nbsp;</p></td>
  </tr>
</table>
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
