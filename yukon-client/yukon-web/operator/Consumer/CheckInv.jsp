<%@ include file="include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.database.cache.functions.ContactFuncs" %>
<%@ page import="com.cannontech.database.data.lite.LiteContact" %>
<%@ page import="com.cannontech.database.data.lite.stars.LiteAddress" %>
<%@ page import="com.cannontech.database.data.lite.stars.LiteInventoryBase" %>
<%@ page import="com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation" %>
<%@ page import="com.cannontech.database.data.lite.stars.LiteStarsLMHardware" %>
<%@ page import="com.cannontech.database.data.lite.stars.StarsLiteFactory" %>
<%@ page import="com.cannontech.stars.util.ObjectInOtherEnergyCompanyException" %>
<%
	Object obj = session.getAttribute(InventoryManagerUtil.INVENTORY_TO_CHECK);
	boolean inOther = (obj instanceof ObjectInOtherEnergyCompanyException);
	LiteInventoryBase liteInv = (LiteInventoryBase) (inOther? ((ObjectInOtherEnergyCompanyException)obj).getObject() : obj);
	
	StarsInventory starsInv = (StarsInventory) session.getAttribute(InventoryManagerUtil.STARS_INVENTORY_TEMP);
	
	boolean inWizard = ((String) session.getAttribute(ServletUtils.ATT_REFERRER)).indexOf("Wizard") >= 0;
	if (!inWizard && accountInfo == null) {
		response.sendRedirect("../Operations.jsp");
		return;
	}
	
	String referer = request.getHeader("referer");
%>

<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
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
<%
	if (inOther) {
		LiteStarsEnergyCompany company = ((ObjectInOtherEnergyCompanyException)obj).getEnergyCompany();
%>
                <p class="ErrorMsg">The hardware or device was found in the inventory 
                  list of <i><%= company.getName() %></i>.</p>
                <table width="200" border="0" cellspacing="0" cellpadding="3" bgcolor="#FFFFFF">
                  <tr> 
                    <td align="center"> 
                      <input type="button" name="OK2" value="OK" onclick="location.href='<%= referer %>'">
                    </td>
                  </tr>
                </table>
<%
	}
	else if (liteInv == null) {
		if (starsInv.getLMHardware() != null) {
%>
                <p class="MainText">The serial number was not found in the inventory. 
                  Would you like to add it now?</p>
                <table width="200" border="0" cellspacing="0" cellpadding="3" bgcolor="#FFFFFF">
                  <tr> 
                    <td width="100" align="right">
                      <input type="submit" name="NewHardware" value="Yes">
                    </td>
                    <td width="100"> 
                      <input type="button" name="No" value="No" onclick="location.href='<%= referer %>'">
                    </td>
                  </tr>
                </table>
<%
		}
		else {
%>
                <p class="MainText">The device name was not found in Yukon. Would 
                  you like to create a new device?</p>
                <table width="200" border="0" cellspacing="0" cellpadding="3" bgcolor="#FFFFFF">
                  <tr> 
                    <td width="100" align="right">
                      <input type="submit" name="NewHardware" value="Yes">
                    </td>
                    <td width="100"> 
                      <input type="button" name="No" value="No" onclick="location.href='<%= referer %>'">
                    </td>
                  </tr>
                </table>
<%
		}
	}
	else if (liteInv.getInventoryID() < 0) {
%>
                <p class="MainText">The device name was found in Yukon, but it 
                  has not been added to the inventory yet. Would you like to add 
                  it now?</p>
                <table width="200" border="0" cellspacing="0" cellpadding="3" bgcolor="#FFFFFF">
                  <tr> 
                    <td width="100" align="right"> 
                      <input type="submit" name="NewDevice" value="Yes">
                    </td>
                    <td width="100"> 
                      <input type="button" name="No" value="No" onclick="location.href='<%= referer %>'">
                    </td>
                  </tr>
                </table>
<%
	}
	else if (liteInv.getAccountID() == com.cannontech.common.util.CtiUtilities.NONE_ZERO_ID) {
%>
                <p class="MainText">The hardware or device is currently in the 
                  warehouse. Would you like to select it?</p>
                <table width="200" border="0" cellspacing="0" cellpadding="3" bgcolor="#FFFFFF">
                  <tr> 
                    <td width="100" align="right"> 
                      <input type="submit" name="SelectHardware" value="Yes">
                    </td>
                    <td width="100"> 
                      <input type="button" name="No" value="No" onclick="location.href='<%= referer %>'">
                    </td>
                  </tr>
                </table>
<%
	}
	else if (account != null && liteInv.getAccountID() == account.getAccountID()) {
%>
                <p class="ErrorMsg">The hardware or device has already been assigned 
                  to this account.</p>
                <table width="200" border="0" cellspacing="0" cellpadding="3" bgcolor="#FFFFFF">
                  <tr> 
                    <td align="center"> 
                      <input type="button" name="OK" value="OK" onclick="location.href='<%= referer %>'">
                    </td>
                  </tr>
                </table>
<%
	}
	else {
		LiteStarsCustAccountInformation liteAcctInfo = liteEC.getBriefCustAccountInfo(liteInv.getAccountID(), true);
		LiteContact liteContact = com.cannontech.database.cache.functions.ContactFuncs.getContact(liteAcctInfo.getCustomer().getPrimaryContactID());
		LiteAddress liteAddr = liteEC.getAddress(liteAcctInfo.getAccountSite().getStreetAddressID());
		
		String name = StarsUtils.formatName(liteContact);
		if (name.length() == 0) name = "(none)";
		
		StreetAddress starsAddr = new StreetAddress();
		StarsLiteFactory.setStarsCustomerAddress(starsAddr, liteAddr);
		String address = ServletUtils.getOneLineAddress(starsAddr);
		if (address.length() == 0) address = "(none)";
%>
			    <p class="MainText">The hardware or device is currently assigned 
                  to another account as shown below:</p>
                <table width="450" border="0" cellspacing="0" cellpadding="0">
                  <tr> 
                    <td width="100" class="HeaderCell">Account #</td>
                    <td width="120" class="HeaderCell">Name</td>
                    <td width="230" class="HeaderCell">Address</td>
                  </tr>
                  <tr> 
                    <td width="100" class="TableCell"><%= StarsUtils.forceNotNull(liteAcctInfo.getCustomerAccount().getAccountNumber()) %></td>
                    <td width="120" class="TableCell"><%= name %></td>
                    <td width="230" class="TableCell"><%= address %></td>
                  </tr>
                </table>
                <p class="MainText">Would you like to move it to this account?</p>
                <table width="200" border="0" cellspacing="0" cellpadding="3" bgcolor="#FFFFFF">
                  <tr> 
                    <td width="100" align="right"> 
                      <input type="submit" name="MoveHardware" value="Yes">
                    </td>
                    <td width="100"> 
                      <input type="button" name="No" value="No" onclick="location.href='<%= referer %>'">
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
