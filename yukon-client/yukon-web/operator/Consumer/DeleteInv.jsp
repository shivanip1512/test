<%@ include file="include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.database.cache.functions.PAOFuncs" %>
<%@ page import="com.cannontech.database.data.lite.stars.LiteInventoryBase" %>
<%@ page import="com.cannontech.database.data.lite.stars.LiteStarsLMHardware" %>
<%@ page import="com.cannontech.stars.web.servlet.InventoryManager" %>
<% if (accountInfo == null) { response.sendRedirect("../Operations.jsp"); return; } %>
<%
	LiteInventoryBase liteInv = (LiteInventoryBase) session.getAttribute(InventoryManager.INVENTORY_TO_DELETE);
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
<script language="JavaScript">
</script>
</head>
<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="ConsumerImage.jpg">&nbsp;</td>
          <td valign="bottom" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>"/>">&nbsp;</td>
              </tr>
              <tr> 
                <td width="265" height = "28" class="PageHeader" valign="middle">&nbsp;&nbsp;&nbsp;Customer 
                  Account Information&nbsp;&nbsp;</td>
                <td width="253" valign="middle">&nbsp;</td>
                  <td width="58" valign="middle"> 
                    <div align="center"><span class="MainText"><a href="../Operations.jsp" class="Link3">Home</a></span></div>
                  </td>
                  <td width="57" valign="middle"> 
                    <div align="left"><span class="MainText"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log 
                      Off</a>&nbsp;</span></div>
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
          <td  valign="top" width="101">&nbsp;</td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center">
              <% String header = "DELETE HARDWARE"; %>
			  <%@ include file="include/InfoSearchBar.jsp" %>
              <form name="form1" method="POST" action="<%= request.getContextPath() %>/servlet/InventoryManager">
			    <input type="hidden" name="action" value="ConfirmDelete">
                <p class="MainText"> 
                  <%
	if (liteInv instanceof LiteStarsLMHardware) {
		String serialNo = ((LiteStarsLMHardware)liteInv).getManufacturerSerialNumber();
%>
                  The hardware with serial no. <%= serialNo %> will be removed 
                  from the account. 
                  <%
	} else {
		String deviceName = PAOFuncs.getYukonPAOName(liteInv.getDeviceID());
%>
                  The device &quot;<%= deviceName %>&quot; will be removed from 
                  the account. 
                  <%	} %>
                  What would you like to do with it?</p>
				<table width="250" border="0" cellspacing="0" cellpadding="3" class="TableCell">
                  <tr>
                    <td width="25">
                      <input type="radio" name="DeletePerm" value="false" checked>
                    </td>
                    <td width="271">Move it to the warehouse.</td>
                  </tr>
                  <tr>
                    <td width="25">
                      <input type="radio" name="DeletePerm" value="true">
                    </td>
                    <td width="271">Delete it from inventory permanantly.</td>
                  </tr>
                </table>
                <br>
                <table width="200" border="0" cellspacing="0" cellpadding="3" bgcolor="#FFFFFF">
                  <tr> 
                    <td width="100" align="right"> 
                      <input type="submit" name="DeleteHardware" value="OK">
                    </td>
                    <td width="100"> 
                      <input type="button" name="Cancel" value="Cancel" onclick="history.back()">
                    </td>
                  </tr>
                </table>
                <p>&nbsp;</p>
                <p>&nbsp;</p>
                <p>&nbsp;</p>
              </form>
            </div>
            <p>&nbsp;</p>
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
