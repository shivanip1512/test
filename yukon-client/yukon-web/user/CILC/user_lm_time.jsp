<%@ include file="../include/user_header.jsp" %>
<html>
<head>
<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
</head>

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
			  <td width="265" height = "28" class="PageHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;User Control</td> 
                <td width="253" valign="middle">&nbsp;</td>
                <td width="58" valign="middle">&nbsp;</td>
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
          <td width="150" bgcolor="#000000" height="1"></td>
          <td width="1" bgcolor="#000000" height="1"></td>
          <td width="609" bgcolor="#000000" height="1"></td>
		  <td width="1" bgcolor="#000000" height="1"></td>
        </tr>
        <tr> 
          <td  valign="top" width="150"> 
		  <% String pageName = "user_lm_time.jsp"; %>
          <%@ include file="include/nav.jsp" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
		  <td width="609" valign="top" bgcolor="#FFFFFF"> 
              <div align="center"><br>
                <span class="TitleHeader">TIME-BASED CONTROL</span> 
                <p class="MainText">Please select a schedule from the following list.</p>
                <table width="600" border="1" cellspacing="0" bgcolor="white" cellpadding="2" align="center">
                  <tr valign="top"> 
                    <td width="104" class="HeaderCell">Schedule Name</td>
                    <td width="100" class="HeaderCell">Status</td>
                    <td width="95" class="HeaderCell">Control Rate</td>
                    <td width="101" class="HeaderCell">Last Start Time</td>
                    <td width="104" class="HeaderCell">Last Stop Time</td>
                    <td width="58" class="HeaderCell">Approx. Reduction</td>
                  </tr>
                  <tr> 
                    <td height="23" class="TableCell" width="104"><a href="user_lm_schedule.jsp">Rooftop Air </a></td>
                    <td height="23" class="TableCell" width="100"><font color="#FF0000">Inactive</font></td>
                    <td height="23" class="TableCell" width="95">25% Off Cycle</td>
                    <td height="23" class="TableCell" width="101">06/23/01 12:00</td>
                    <td height="23" class="TableCell" width="104">06/23/01 15:45</td>
                    <td height="23" class="TableCell" width="58">400.0</td>
                  </tr>
                  <tr> 
                    <td height="23" class="TableCell" width="104">Large Motors</td>
                    <td height="23" class="TableCell" width="100">Disabled</td>
                    <td height="23" class="TableCell" width="95">&nbsp;</td>
                    <td height="23" class="TableCell" width="101">06/23/01 14:00</td>
                    <td height="23" class="TableCell" width="104">06/23/01 16:00</td>
                    <td height="23" class="TableCell" width="58">425.0</td>
                  </tr>
                  <tr> 
                    <td height="23" class="TableCell" width="104">Irrigation Site 1</td>
                    <td height="23" class="TableCell" width="100"><font color="#00CC00">Running</font></td>
                    <td height="23" class="TableCell" width="95">4 Hours</td>
                    <td height="23" class="TableCell" width="101">07/23/01 12:03</td>
                    <td height="23" class="TableCell" width="104">07/23/01 16:03</td>
                    <td height="23" class="TableCell" width="58">50.0</td>
                  </tr>
                  <tr> 
                    <td height="23" class="TableCell" width="104">Irrigation Site 
                      2</td>
                    <td height="23" class="TableCell" width="100"><font color="#0000FF">Stopped</font></td>
                    <td height="23" class="TableCell" width="95">&nbsp;</td>
                    <td height="23" class="TableCell" width="101">07/20/01 13:15</td>
                    <td height="23" class="TableCell" width="104">07/20/01 15:15</td>
                    <td height="23" class="TableCell" width="58">50.0</td>
                  </tr>
                </table>
                <p align="center">&nbsp;</p>
                <p align="center" class="MainText">&nbsp;</p>
              <p>&nbsp;</p></div>
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
