<%@ include file="include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.database.data.lite.LiteContact" %>
<%@ page import="com.cannontech.database.data.lite.stars.LiteAddress" %>
<%@ page import="com.cannontech.database.data.lite.stars.LiteInventoryBase" %>
<%@ page import="com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation" %>
<%@ page import="com.cannontech.database.data.lite.stars.LiteStarsLMHardware" %>
<%@ page import="com.cannontech.database.data.lite.stars.StarsLiteFactory" %>
<%@ page import="com.cannontech.stars.web.servlet.InventoryManager" %>
<%
	LiteStarsEnergyCompany ec = com.cannontech.stars.web.servlet.SOAPServer.getEnergyCompany(user.getEnergyCompanyID());
	LiteInventoryBase liteInv = (LiteInventoryBase) session.getAttribute(InventoryManager.INVENTORY_TO_CHECK);
	
	boolean inWizard = ((String) session.getAttribute(ServletUtils.ATT_REFERRER)).indexOf("Wizard=true") >= 0;
	if (!inWizard && accountInfo == null) {
		response.sendRedirect("../Operations.jsp");
		return;
	}
%>

<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
<script language="JavaScript">
</script>
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
              <% String header = "CHECK INVENTORY"; %>
<% if (!inWizard) { %>
              <%@ include file="include/InfoSearchBar.jsp" %>
<% } else { %>
              <%@ include file="include/InfoSearchBar2.jsp" %>
<% } %>

              <form name="form1" method="POST" action="<%= request.getContextPath() %>/servlet/InventoryManager">
			    <input type="hidden" name="action" value="ConfirmCheck">
<%	if (request.getParameter("InOther") != null) { %>
                <p class="ErrorMsg">The hardware or device is found in another 
                  energy company. Please contact <%= ec.getParent().getName() %> 
                  for more information.</p>
                <table width="200" border="0" cellspacing="0" cellpadding="3" bgcolor="#FFFFFF">
                  <tr> 
                    <td align="center"> 
                      <input type="button" name="OK2" value="OK" onClick="history.back()">
                    </td>
                  </tr>
                </table>
<%	}
	else if (liteInv == null) {
%>
                <p class="MainText">This serial number is not found in inventory. 
                  Would you like to add it now?</p>
                <table width="200" border="0" cellspacing="0" cellpadding="3" bgcolor="#FFFFFF">
                  <tr> 
                    <td width="100" align="right">
                      <input type="submit" name="NewHardware" value="Yes">
                    </td>
                    <td width="100"> 
                      <input type="button" name="No" value="No" onclick="history.back()">
                    </td>
                  </tr>
                </table>
<%	}
	else if (liteInv.getInventoryID() < 0) {
%>
                <p class="MainText">The device name is found but it's not in inventory 
                  yet. Would you like to add it?</p>
                <table width="200" border="0" cellspacing="0" cellpadding="3" bgcolor="#FFFFFF">
                  <tr> 
                    <td width="100" align="right"> 
                      <input type="submit" name="NewDevice" value="Yes">
                    </td>
                    <td width="100"> 
                      <input type="button" name="No" value="No" onclick="history.back()">
                    </td>
                  </tr>
                </table>
<%	}
	else if (liteInv.getAccountID() == com.cannontech.common.util.CtiUtilities.NONE_ID) {
%>
                <p class="MainText">The hardware or device is currently in the 
                  warehouse. Would you like to select it?</p>
                <table width="200" border="0" cellspacing="0" cellpadding="3" bgcolor="#FFFFFF">
                  <tr> 
                    <td width="100" align="right"> 
                      <input type="submit" name="SelectHardware" value="Yes">
                    </td>
                    <td width="100"> 
                      <input type="button" name="No" value="No" onclick="history.back()">
                    </td>
                  </tr>
                </table>
<%	}
	else if (account != null && liteInv.getAccountID() == account.getAccountID()) {
%>
                <p class="ErrorMsg">The hardware or device is already assigned 
                  to this account.</p>
                <table width="200" border="0" cellspacing="0" cellpadding="3" bgcolor="#FFFFFF">
                  <tr> 
                    <td align="center"> 
                      <input type="button" name="OK" value="OK" onclick="history.back()">
                    </td>
                  </tr>
                </table>
<%	}
	else {
		LiteStarsCustAccountInformation liteAcctInfo = ec.getBriefCustAccountInfo(liteInv.getAccountID(), true);
		LiteContact liteContact = ec.getContact(liteAcctInfo.getCustomer().getPrimaryContactID(), liteAcctInfo);
		LiteAddress liteAddr = ec.getAddress(liteAcctInfo.getAccountSite().getStreetAddressID());
		
		String name = ServerUtils.formatName(liteContact);
		if (name.length() == 0) name = "(none)";
		
		StreetAddress starsAddr = new StreetAddress();
		StarsLiteFactory.setStarsCustomerAddress(starsAddr, liteAddr);
		String address = ServletUtils.getOneLineAddress(starsAddr);
		if (address.length() == 0) address = "(none)";
%>
			    <p class="MainText">The hardware or device is currently assigned 
                  to the following account:</p>
                <table width="450" border="0" cellspacing="0" cellpadding="0">
                  <tr> 
                    <td width="100" class="HeaderCell">Account #</td>
                    <td width="120" class="HeaderCell">Name</td>
                    <td width="230" class="HeaderCell">Address</td>
                  </tr>
                  <tr> 
                    <td width="100" class="TableCell"><%= ServerUtils.forceNotNull(liteAcctInfo.getCustomerAccount().getAccountNumber()) %></td>
                    <td width="120" class="TableCell"><%= name %></td>
                    <td width="230" class="TableCell"><%= address %></td>
                  </tr>
                </table>
                <p class="MainText">Would you like to move it to the new account? 
                </p>
                <table width="200" border="0" cellspacing="0" cellpadding="3" bgcolor="#FFFFFF">
                  <tr> 
                    <td width="100" align="right"> 
                      <input type="submit" name="MoveHardware" value="Yes">
                    </td>
                    <td width="100"> 
                      <input type="button" name="No" value="No" onclick="history.back()">
                    </td>
                  </tr>
                </table>
<%
	}
%>
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
