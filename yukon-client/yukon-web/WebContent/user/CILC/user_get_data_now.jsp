<html>
<!-- Java script needed for the Calender Function--->
<%@ include file="../include/user_header.jsp" %>
<head>
<title>Consumer Energy Services</title>
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
<META NAME="robots" CONTENT="noindex, nofollow">
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

<SCRIPT language="JavaScript">
function disableButton(x)
{
	x.disabled = true;
	document.getDataNowForm.submit();
}
</SCRIPT>
<!--
<script language="JavaScript">
function MM_reloadPage(init) {  //reloads the window if Nav4 resized
  if (init==true) with (navigator) {if ((appName=="Netscape")&&(parseInt(appVersion)==4)) {
    document.MM_pgW=innerWidth; document.MM_pgH=innerHeight; onresize=MM_reloadPage; }}
  else if (innerWidth!=document.MM_pgW || innerHeight!=document.MM_pgH) location.reload();
}
MM_reloadPage(true);
</script>
// -->
</head>
<body class="Background" leftmargin="0" topmargin="0" >
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
			    <td width="265" height = "28" class="PageHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Get Data Now</td>
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
            <% String pageName = "user_get_data_now.jsp"; %> 
            <%@ include file="include/nav.jsp" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF" >
          <div class = "MainText" align="center">
              <% String header = "METERING - GET DATA NOW"; %>
			  <% if (errorMsg != null)
			  		out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>");
				 if (confirmMsg != null)
              		out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>");%>
              <form name = "getDataNowForm" method="POST" action="<%=request.getContextPath()%>/servlet/GraphGenerator?">
                <input type="hidden" name="action" value="GetDataNow">
                <input type="hidden" name="REDIRECT" value="<%= request.getRequestURI() %>">
				<input type="hidden" name="REFERRER" value="<%= request.getRequestURI() %>">
                <table width="350" border="0" height="179" cellspacing = "0">
                  <tr>
                    <td>
                      <table class = "TableCell" width="100%" border="0" cellspacing = "0" cellpadding = "1">
                        <tr> 
                          <td colspan = "2"><span class="SubtitleHeader">AVAILABLE METERS</span> 
                            <hr>
                          </td>
                        </tr>
                        <%
					  for (int i = 0; i < custDevices.size(); i++)
					  {
					    DeviceCustomerList dcl = (DeviceCustomerList) custDevices.get(i);
					    %>
						<tr>
                          <td width="35"> 
                            <input type="checkbox" name="custDevices" value="<%=dcl.getDeviceID().intValue()%>">
                          </td>
  						  <td width="300"><%=com.cannontech.database.cache.functions.PAOFuncs.getYukonPAOName(dcl.getDeviceID().intValue())%></td>
						</tr>
					<%} %>
                      </table>
                    </td>
                  </tr>
                </table>
                <br>
                <table width="150" border="0">
                  <tr>
                    <td align = "center" width = "50%"> 
                      <input type="submit" name="GetDataNow" value="Get Data Now" onclick="disableButton(this)">
                    </td>
                    <td width = "50%"> 
                      <input type="reset" name="Cancel" value="Cancel" >
                    </td>
                  </tr>
                </table>
                <br>
              </form>
          </div>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>
</body>
</html>
