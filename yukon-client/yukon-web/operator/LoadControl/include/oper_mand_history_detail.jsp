<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">

</head>
<%
	int programId = 0;
	int curtailReferenceId = 0;

	String programIdStr = request.getParameter("prog");
	if (programIdStr != null)
		programId = Integer.parseInt(programIdStr);

	String curtailRefIdStr = request.getParameter("ref");
	if (curtailRefIdStr != null)
		curtailReferenceId = Integer.parseInt(curtailRefIdStr);

	com.cannontech.web.history.CurtailHistory history = null;

	try {
		history = new com.cannontech.web.history.CurtailHistory(dbAlias);
		com.cannontech.web.history.HCurtailProgram[] programs = history.getCurtailPrograms();

        com.cannontech.web.history.HCurtailCustomerActivity activity = null;

		for (int i = 0; i < programs.length; i++)
		{            
			if (programs[i].getDeviceId() != programId)
				continue;
	%> 
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
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>"/>">&nbsp;</td>
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
            <div align="center">
              <p class="TitleHeader"><br>NOTIFICATION - PROGRAM HISTORY FOR:<br> <%= programs[i].getProgramName() %><br>
              </p>
            </div>
            <div align="center">
              <table width="600" border="1" cellspacing="0" cellpadding="2">
                <tr> 
                  <td width="25%" class="HeaderCell">Company</td>
                  <td width="16%" class="HeaderCell">Acknowledged</td>
                  <td width="25%" class="HeaderCell">Ack Date/Time</td>
                  <td width="11%" class="HeaderCell">Ack Late</td>
                  <td width="11%" class="HeaderCell">Ack User</td>
                  <td width="12%" class="HeaderCell">Ack Type</td>
                </tr>
                <%
			com.cannontech.web.history.HCurtailCustomer[] customers = programs[i].getCurtailCustomers();

			for (int j = 0; j < customers.length; j++)
			{
				activity = customers[j].getCurtailCustomerActivity(curtailReferenceId);
				if (activity == null) continue;

				String ackDateTimeStr = "-";
				String ackPersonStr = "-";

				if ( activity.getAckStatus().equalsIgnoreCase("Acknowledged") )
				{
					if (activity.getAckDateTime() != null)
						ackDateTimeStr = mandTimeFormat.format( activity.getAckDateTime() ) + " " + dateFormat.format( activity.getAckDateTime() );

					if (activity.getNameOfAckPerson() != null && !activity.getNameOfAckPerson().equalsIgnoreCase("null"))
						ackPersonStr = activity.getNameOfAckPerson();
				}
	%>
                <tr> 
                  <td width="25%" class="TableCell"> <%= customers[j].getCompanyName() %></td>
                  <td width="16%" class="TableCell"> <%= activity.getAckStatus() %></td>
                  <td width="25%" class="TableCell"> <%= ackDateTimeStr %></td>
                  <td width="11%" class="TableCell"> <%= activity.getAckLateFlag() %></td>
                  <td width="11%" class="TableCell"> <%= ackPersonStr %></td>
                  <td width="12%" class="TableCell">
                  <%
                    if( activity.getAckStatus().equalsIgnoreCase("Acknowledged")){
                    %>
                      Web Response 
                    <%}else{%>
                      -
                    <%}%>
                  </td>
                </tr>
                <%
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
              <br>
            </div>
            <p align="center" class="SubtitleHeader"> <a href="<%= referrer %>" class="Link1">Back to History</a><br>
              <br> 
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
