<%@ include file="include/StarsHeader.jsp" %>
<% if (accountInfo == null) { response.sendRedirect("../Operations.jsp"); return; } %>
<%
	int invNo = Integer.parseInt(request.getParameter("InvNo"));
	StarsMCT mct = inventories.getStarsMCT(invNo - inventories.getStarsLMHardwareCount());
	// Stacey, here are the fields useful to you:
	int deviceID = mct.getDeviceID();
	String deviceName = mct.getDeviceName();
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
</head>
<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="150" height="102" background="../../WebConfig/MomWide.jpg">&nbsp;</td>
          <td valign="top" height="102"> 
            <table width="609" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>"/>">&nbsp;</td>
              </tr>
              <tr> 
                <td width="265" height = "28" class="PageHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Customer Account Information&nbsp;&nbsp;</td>
                <td width="253" valign="middle">&nbsp;</td>
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
          <td width="150" bgcolor="#000000" height="1"></td>
          <td width="1" bgcolor="#000000" height="1"></td>
          <td width="609" bgcolor="#000000" height="1"></td>
		  <td width="1" bgcolor="#000000" height="1"></td>
        </tr>
        <tr> 
          <td  valign="top" width="101"> 
            <% String pageName = "CommandMeter.jsp?deviceid=" + 16; //FIX ME ccu-710A %>
            <%@ include file="include/nav.jsp" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center">
              <% String header = "METER - CONTROL COMMANDS"; %>
			  <br>
			  <table width="100%" border="0" cellspacing="0" cellpadding="3">
			    <tr>
			      <td align="center" class="TitleHeader"><%= header %></td>
			    </tr>
			  </table>
              
              <% String errorMsg = null;%>
			  <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
			  
			  <form name="invForm" method="POST" action="<%= request.getContextPath() %>/servlet/SOAPClient">
                <input type="hidden" name="action" value="CommandMeter">
                <input type="hidden" name="deviceid" value="<%= 16 %>"> <% //FIX ME %>
				<input type="hidden" name="REDIRECT" value="<%=request.getContextPath()%>/operator/Consumer/CommandMeter.jsp?deviceid=<%= 16%>"><% //FIX ME %>
				<input type="hidden" name="REFERRER" value="<%=request.getContextPath()%>/operator/Consumer/CommandMeter.jsp?deviceid=<%= 16%>"><% //FIX ME %>
				
                <table width="575" border="0" cellspacing="0" cellpadding="3">
                  <tr> 
                    <td width="30%" class="SubtitleHeader" align="right"> Meter Name:</td>
                    <td width="70%" class="TableCell">My MCT Meter</td>
                  </tr>
                  <tr> 
                    <td width="30%" class="SubtitleHeader" align="right">Meter Type:</td>
                    <td width="70%" class="TableCell">My Meter Type</td>
                  </tr>
                  <tr> 
                    <td width="30%" class="SubtitleHeader" align="right">Execute 
                      Command :</td>
                    <td width="70%"> 
                      <select id="collGroup" name="command">
                        <option value="getvalue kwh update" selected>Read Energy 
                        (update)</option>
                        <% if( DeviceTypesFuncs.isDisconnectMCT(37)) %>
                        <option value="getstatus disconnect">Read Disconnect Status</option>
                        <option value="control disconnect">Disconnect Meter</option>
                        <option value="control connect">Connect Meter</option>
                      </select>
                      <input type="submit" name="Submit" value="Execute">
                    </td>
                  </tr>
                  <tr> 
                    <td class="SubtitleHeader" align="right" width="30%" valign="top">Results: 
                    </td>
                    <td class="SubtitleHeader" width="70%"> 
                      <textarea name="textfield" class="TableCell" readonly="readonly" cols="60" rows="5" wrap="OFF"></textarea>
                      <input type="submit" name="Submit2" value="Clear Results">
                    </td>
                  </tr>
                </table>
                <br>
              </form>
            </div>
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
