<%@ include file="include/StarsHeader.jsp" %>
<% if (accountInfo == null) { response.sendRedirect("../Operations.jsp"); return; } %>
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
          <td width="102" height="102" background="../../WebConfig/yukon/ConsumerImage.jpg">&nbsp;</td>
          <td valign="top" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>"/>">&nbsp;</td>
              </tr>
              <tr> 
				  
                <td width="265" height="28" valign="middle" class="PageHeader">&nbsp;&nbsp;&nbsp;Customer Account Information&nbsp;&nbsp;</td>
                  <td width="253" valign="middle">&nbsp;</td>
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
		  <% String pageName = "Timeout.jsp"; %>
          <%@ include file="include/Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          
		  <td width="657" valign="top" bgcolor="#FFFFFF"> 
              
            <div align="center"> 
              <% String header = "THERMOSTAT - SCHEDULE<BR>TIMEOUT WARNING"; %>
              <%@ include file="include/InfoSearchBar.jsp" %>
                <table width="80%" border="1" cellspacing = "0" cellpadding = "2">
                <tr> 
                    <td align = "center"> 
                      
                    <table width="478" border="0">
                      <tr> 
                        <td class = "MainText" width="100%" ><span class="ErrorMsg">Your 
                          thermostat gateway has not reported back for more than 
                          <%= ServletUtils.GATEWAY_TIMEOUT_HOURS %> hours. The 
                          last thermostat settings received from the gateway may 
                          not be current, and you probably will not be able to 
                          send settings to gateway successfully.</span><br>
                          <br>
                          Please check the LEDs on your gateway to make sure it's 
                          working properly. If you find any problem, please contact 
                          your utility company for support.<br>
                          <br>
                          If you want to view the thermostat settings anyway, 
                          please <a href="<%= session.getAttribute(ServletUtils.ATT_REFERRER) %>&OmitTimeout=true">click 
                          here</a>.</td>
                      </tr>
                    </table>
                    </td>
                </tr>
              </table>
              <br>
			  <input type="button" name="Back" value="Back" onclick="history.back()">
              <p align="center" class="MainText"><font face="Arial, Helvetica, sans-serif" size="1">Copyright 
                &copy; 2003, Cannon Technologies, Inc. All rights reserved.</font> 
              </p>
              <p align="center" class="MainText">&nbsp; </p>
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
