<%@ taglib uri="/WEB-INF/cti.tld" prefix="cti" %>
<%@ page import="java.io.StringWriter" %>
<%@ page import="com.cannontech.database.cache.StarsDatabaseCache" %>
<%@ page import="com.cannontech.database.data.lite.stars.*" %>
<%@ page import="com.cannontech.stars.util.ECUtils" %>
<%@ page import="com.cannontech.stars.util.ServletUtils" %>
<%@ page import="com.cannontech.stars.web.StarsYukonUser" %>
<%@ page import="com.cannontech.stars.xml.serialize.*" %>
<cti:checklogin/>
<%
	StarsYukonUser user = (StarsYukonUser) session.getAttribute(ServletUtils.ATT_STARS_YUKON_USER);
	if (!ECUtils.isOperator(user)) {
		response.sendRedirect(request.getContextPath() + "/login.jsp");
		return;
	}
	
	LiteStarsEnergyCompany liteEC = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
	
	String cache = "";
	
	if (request.getParameter("ShowEC") != null) {
		StarsEnergyCompanySettings ecSettings = (StarsEnergyCompanySettings) session.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
		if (ecSettings == null) {
			cache = "<font color='red'>Energy company settings not found!</font>";
		}
		else {
			StringWriter sw = new StringWriter();
			ecSettings.marshal(sw);
			cache = sw.toString();
		}
	}
	else if (request.getParameter("ShowAcct") != null) {
		String acctNo = request.getParameter("AcctNo");
		LiteStarsCustAccountInformation liteAcctInfo = liteEC.searchAccountByAccountNo( acctNo );
		if (liteAcctInfo == null) {
			cache = "<font color='red'>No customer account found for account #" + acctNo + "!</font>";
		}
		else {
			StarsCustAccountInformation starsAcctInfo = liteEC.getStarsCustAccountInformation( liteAcctInfo );
			StringWriter sw = new StringWriter();
			starsAcctInfo.marshal(sw);
			cache = sw.toString();
		}
	}
	else if (request.getParameter("ReloadAcct") != null) {
		String acctNo = request.getParameter("AcctNo");
		LiteStarsCustAccountInformation liteAcctInfo = liteEC.searchByAccountNo( acctNo );
		if (liteAcctInfo == null) {
			cache = "<font color='red'>No customer account found for account #" + acctNo + "!</font>";
		}
		else {
			liteEC.reloadCustAccountInformation(liteAcctInfo);
			liteEC.deleteStarsCustAccountInformation(liteAcctInfo.getAccountID());
			cache = "Account #" + acctNo + " has been reloaded";
		}
	}
	else if (request.getParameter("ReloadInv") != null) {
		liteEC.setInventoryLoaded(false);
		liteEC.loadAllInventory(false);
		cache = "Inventory is being reloaded...";
	}
%>
<html>
<head>
<title>Untitled Document</title>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
</head>

<body bgcolor="#FFFFFF" text="#000000">
<form name="form1" method="post" action="">
  <table width="400" border="0" cellspacing="0" cellpadding="0">
    <tr> 
      <td width="230">Energy Company Settings:</td>
      <td width="170"> 
        <input type="submit" name="ShowEC" value="Show">
      </td>
    </tr>
    <tr> 
      <td width="230">Acct #: 
        <input type="text" name="AcctNo">
      </td>
      <td width="170"> 
        <input type="submit" name="ShowAcct" value="Show" onclick="if (this.form.AcctNo.value == '') { alert('Account # cannot be empty!'); return false; }">
        <input type="submit" name="ReloadAcct" value="Reload" onclick="if (this.form.AcctNo.value == '') { alert('Account # cannot be empty!'); return false; }">
      </td>
    </tr>
    <tr> 
      <td width="230">Inventory:</td>
      <td width="170"> 
        <input type="submit" name="ReloadInv" value="Reload">
      </td>
    </tr>
  </table>
</form>
<hr>
<%= cache.replaceAll("<", "&lt;").replaceAll(">", "&gt;") %>
</body>
</html>
