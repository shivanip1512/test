<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.database.data.lite.stars.LiteAddress" %>
<%@ page import="com.cannontech.database.data.lite.stars.LiteCustomerContact" %>
<%@ page import="com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation" %>
<%@ page import="com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany" %>
<%@ page import="com.cannontech.database.data.lite.stars.LiteStarsLMHardware" %>
<%@ page import="com.cannontech.stars.web.servlet.InventoryManager" %>

<%
	LiteStarsEnergyCompany ec = com.cannontech.stars.web.servlet.SOAPServer.getEnergyCompany(user.getEnergyCompanyID());
	LiteStarsLMHardware liteHw = (LiteStarsLMHardware) session.getAttribute(InventoryManager.LM_HARDWARE_TO_CHECK);
/*	
	String action = request.getParameter("action");
	if (action != null && action.equalsIgnoreCase("CheckInventory")) {
		StarsLMHardware hardware = new StarsLMHardware();
		InventoryManager.setStarsLMHardware(hardware, request);
		session.setAttribute(InventoryManager.STARS_LM_HARDWARE_TEMP, hardware);
		
		liteHw = ec.searchBySerialNumber(hardware.getManufactureSerialNumber());
		session.setAttribute(InventoryManager.LM_HARDWARE_TO_CHECK, liteHw);
		
		referer = request.getHeader("referer");
		session.setAttribute(ServletUtils.ATT_REFERRER, referer);
	}*/
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
          <td width="102" height="102" background="InventoryImage.jpg">&nbsp;</td>
          <td valign="bottom" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>"/>">&nbsp;</td>
              </tr>
              <tr> 
                <td width="265" height = "28" class="PageHeader" valign="middle">&nbsp;&nbsp;&nbsp;Hardware 
                  Inventory </td>
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
              <% String header = "CHECK INVENTORY"; %>
			  <%@ include file="include/SearchBar.jsp" %>
              <form name="form1" method="POST" action="<%= request.getContextPath() %>/servlet/InventoryManager">
			    <input type="hidden" name="action" value="ConfirmCheck">
<%
	if (liteHw == null) {
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
<%
	}
	else if (liteHw.getAccountID() == com.cannontech.common.util.CtiUtilities.NONE_ID) {
%>
                <p class="MainText">The hardware for this serial number is currently 
                  in the warehouse. Would you like to select it?</p>
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
<%
	}
	else if (liteHw.getAccountID() != account.getAccountID()) {
		LiteStarsCustAccountInformation liteAcctInfo = ec.getBriefCustAccountInfo(liteHw.getAccountID(), true);
		LiteCustomerContact liteContact = ec.getCustomerContact(liteAcctInfo.getCustomer().getPrimaryContactID());
		LiteAddress liteAddr = ec.getAddress(liteAcctInfo.getAccountSite().getStreetAddressID());
%>
			    <p class="MainText">The hardware for this serial number is currently 
                  assigned to the following account:</p>
                <table width="450" border="0" cellspacing="0" cellpadding="0">
                  <tr> 
                    <td width="100" class="HeaderCell">Account #</td>
                    <td width="120" class="HeaderCell">Name</td>
                    <td width="230" class="HeaderCell">Address</td>
                  </tr>
                  <tr> 
                    <td width="100" class="TableCell"><%= ServerUtils.forceNotNull(liteAcctInfo.getCustomerAccount().getAccountNumber()) %></td>
                    <td width="120" class="TableCell"><%= ServerUtils.getFormattedName(liteContact) %></td>
                    <td width="230" class="TableCell"><%= ServerUtils.getOneLineAddress(liteAddr) %></td>
                  </tr>
                </table>
                <p class="MainText">Would you like to move the hardware to the new account? 
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
	else {
%>
                <p class="MainText">The hardware for this serial number is already 
                  assigned to this account.</p>
                <table width="200" border="0" cellspacing="0" cellpadding="3" bgcolor="#FFFFFF">
                  <tr> 
                    <td align="center"> 
                      <input type="button" name="OK" value="OK" onclick="history.back()">
                    </td>
                  </tr>
                </table>
<%
	}
%>
				<p>&nbsp;</p>
                <p>&nbsp;</p><p class="MainText">&nbsp;</p>
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
