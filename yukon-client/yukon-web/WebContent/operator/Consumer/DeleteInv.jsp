<%@ include file="include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.database.data.lite.stars.LiteInventoryBase" %>
<%@ page import="com.cannontech.database.data.lite.stars.LiteStarsLMHardware" %>
<% if (accountInfo == null) { response.sendRedirect("../Operations.jsp"); return; } %>
<%
	int invID = Integer.parseInt(request.getParameter("InvID"));
	LiteInventoryBase liteInv = liteEC.getInventory(invID, true);
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
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
          <td  valign="top" width="101">&nbsp;</td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center">
              <% String header = "DELETE HARDWARE"; %>
			  <%@ include file="include/InfoSearchBar.jsp" %>
              <form name="form1" method="POST" action="<%= request.getContextPath() %>/servlet/SOAPClient">
			    <input type="hidden" name="action" value="DeleteLMHardware">
			    <input type="hidden" name="InvID" value="<%= invID %>">
			    <input type="hidden" name="REDIRECT" value="<%= request.getParameter(ServletUtils.ATT_REDIRECT) %>">
			    <input type="hidden" name="REFERRER" value="<%= request.getParameter(ServletUtils.ATT_REFERRER) %>">
                <table width="80%" border="0" cellspacing="0" cellpadding="0" class="MainText">
                  <tr>
                    <td align="center"> 
                      <%
	if (liteInv instanceof LiteStarsLMHardware) {
		String serialNo = ((LiteStarsLMHardware)liteInv).getManufacturerSerialNumber();
%>
                      The hardware with serial number <%= serialNo %> will be 
                      removed from the account. 
                      <%
	} else {
		String deviceName = PAOFuncs.getYukonPAOName(liteInv.getDeviceID());
%>
                      The device &quot;<%= deviceName %>&quot; will be removed 
                      from the account. 
                      <%
	}
%>
                      What would you like to do with it?</td>
                  </tr>
                </table>
                <br>
                <table border="0" cellspacing="0" cellpadding="3" class="TableCell">
                  <tr>
                    <td width="25">
                      <input type="radio" name="DeleteAction" value="MoveToWarehouse" checked>
                    </td>
                    <td nowrap>Move it to the warehouse.</td>
                  </tr>
                  <tr>
                    <td width="25">
                      <input type="radio" name="DeleteAction" value="DeleteFromInventory">
                    </td>
                    <td nowrap> 
                      <% if (liteInv instanceof LiteStarsLMHardware) { %>
                      Delete it from inventory permanently. 
                      <% } else { %>
                      Delete it from inventory, but leave it in the Yukon database. 
                      <% } %>
                    </td>
                  </tr>
<% if (!(liteInv instanceof LiteStarsLMHardware)) { %>
                  <tr>
                    <td width="25">
                      <input type="radio" name="DeleteAction" value="DeleteFromYukon">
                    </td>
                    <td nowrap>Delete it from both inventory and Yukon database.</td>
                  </tr>
<% } %>
                </table>
                <br>
                <table width="200" border="0" cellspacing="0" cellpadding="3" bgcolor="#FFFFFF">
                  <tr> 
                    <td width="78" align="right"> 
                      <input type="submit" name="Submit" value=" OK ">
                    </td>
                    <td width="110"> 
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
        <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
    </tr>
      </table>
    </td>
	</tr>
</table>
<br>
</body>
</html>
