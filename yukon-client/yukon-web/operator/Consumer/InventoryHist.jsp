<%@ include file="include/StarsHeader.jsp" %>
<% if (accountInfo == null) { response.sendRedirect("../Operations.jsp"); return; } %>
<%
	int invNo = Integer.parseInt(request.getParameter("InvNo"));
	StarsInventory inventory = inventories.getStarsInventory(invNo);
%>

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
      <%@ include file="include/HeaderBar.jsp" %>
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
            <% String pageName = "Inventory.jsp?InvNo=" + invNo; %>
            <%@ include file="include/Nav.jsp" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center">
              <% String header = "HARDWARE HISTORY"; %>
              <%@ include file="include/InfoSearchBar.jsp" %>

            <table width="610" border="0" cellspacing="0" cellpadding="10" align="center" height="66">
              <tr> 
                 <td valign="top" bgcolor="#FFFFFF" height="65"> 
                    <div align="center"> <span class="Subtext">Hardware History</span> 
                      <br>
                      <table width="250" border="1" cellspacing="0" cellpadding="3" align="center">
                        <tr> 
                          <td width="104" class="HeaderCell">Date</td>
                          <td width="100" class="HeaderCell">Action</td>
                        </tr>
                        <%
	StarsLMHardwareHistory hwHist = inventory.getStarsLMHardwareHistory();
	for (int i = hwHist.getStarsLMHardwareEventCount() - 1; i >= 0; i--) {
		StarsLMHardwareEvent event = hwHist.getStarsLMHardwareEvent(i);
%>
                        <tr valign="top"> 
                          <td width="104" class="TableCell" bgcolor="#FFFFFF"><%= ServletUtils.formatDate(event.getEventDateTime(), datePart, "----") %></td>
                          <td width="100" class="TableCell" bgcolor="#FFFFFF"><%= event.getEventAction() %></td>
                        </tr>
<%
	}
%>
                      </table>
                    </div>
                  
                </td>
              </tr>
            </table>
            </div>
            <table width="400" border="0" cellspacing="0" cellpadding="3" align="center" bgcolor="#FFFFFF">
              <tr> 
                <td width="43%"> 
                  <div align="center">
                    <input type="button" name="Back" value="Back" onclick="location='Inventory.jsp?InvNo=<%= invNo %>'">
                  </div>
                </td>
              </tr>
            </table>
            <div align="center"><br>
            </div>
            <p>&nbsp;</p>
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
