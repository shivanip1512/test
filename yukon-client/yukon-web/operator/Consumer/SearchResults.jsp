<%@ include file="include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.database.cache.functions.ContactFuncs" %>
<%@ page import="com.cannontech.database.data.lite.LiteContact" %>
<%@ page import="com.cannontech.database.data.lite.stars.LiteAddress" %>
<%@ page import="com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation" %>
<%@ page import="com.cannontech.database.data.lite.stars.StarsLiteFactory" %>
<%
	StarsSearchCustomerAccountResponse resp = (StarsSearchCustomerAccountResponse)
			session.getAttribute(ServletUtils.ATT_ACCOUNT_SEARCH_RESULTS);
	
	LiteStarsEnergyCompany liteEC = SOAPServer.getEnergyCompany(user.getEnergyCompanyID());
	
	boolean showEnergyCompany = liteEC.getChildren().size() > 0;
	ArrayList descendants = null;
	if (showEnergyCompany) descendants = com.cannontech.stars.util.ECUtils.getAllDescendants(liteEC);
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
<script language="JavaScript">
function selectAccount(accountID) {
	var form = document.resultForm;
	form.AccountID.value = accountID;
	form.submit();
}

function selectMemberAccount(accountID, contextID) {
	var form = document.resultForm;
	form.AccountID.value = accountID;
	form.SwitchContext.value = contextID;
	form.submit();
}
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
          <td  valign="top" width="101">&nbsp; </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" height="400" valign="top" bgcolor="#FFFFFF">
              
            <div align="center"><% String header = "SEARCH RESULTS"; %><%@ include file="include/InfoSearchBar2.jsp" %>
			<% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %></div>
<%
	if (resp != null) {
		if (resp.getStarsFailure() != null) {
%>
            <div align="center">
              <span class="MainText">No customer accounts matching the search criteria.</span> 
            </div>
<%
		}
		else {
%>
            <div align="center">
              <span class="MainText">The following customer accounts are found:</span> 
            </div>
			<form name="resultForm" method="POST" action="<%= request.getContextPath() %>/servlet/SOAPClient">
		      <input type="hidden" name="action" value="GetCustAccount">
              <input type="hidden" name="AccountID" value="">
			  <input type="hidden" name="REDIRECT" value="<%=request.getContextPath()%>/operator/Consumer/Update.jsp">
			  <input type="hidden" name="REFERRER" value="<%=request.getContextPath()%>/operator/Consumer/SearchResults.jsp">
			  
              <table width="615" border="1" cellspacing="0" cellpadding="3" align="center">
                <tr> 
                  <td width="15%" class="HeaderCell">Account #</td>
                  <td width="18%" class="HeaderCell">Name</td>
                  <td width="17%" class="HeaderCell">Phone#</td>
                  <td class="HeaderCell">Address</td>
<% if (showEnergyCompany) { %>
                  <td width="15%" class="HeaderCell">Energy Company</td>
				  <input type="hidden" name="SwitchContext" value="">
<% } %>
                </tr>
<%
			for (int i = 0; i < resp.getAccountIDCount(); i++) {
				LiteStarsEnergyCompany member = liteEC;
				if (showEnergyCompany) {
					for (int j = 0; j < descendants.size(); j++) {
						LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) descendants.get(j);
						if (company.getBriefCustAccountInfo(resp.getAccountID(i), false) != null) {
							member = company;
							break;
						}
					}
				}
				
				LiteStarsCustAccountInformation liteAcctInfo = member.getBriefCustAccountInfo(resp.getAccountID(i), true);
				LiteContact contact = member.getContact(liteAcctInfo.getCustomer().getPrimaryContactID(), liteAcctInfo);
				LiteAddress addr = member.getAddress(liteAcctInfo.getAccountSite().getStreetAddressID());
				
				String homePhone = ServerUtils.getNotification(
						ContactFuncs.getContactNotification(contact, YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE) );
				String workPhone = ServerUtils.getNotification(
						ContactFuncs.getContactNotification(contact, YukonListEntryTypes.YUK_ENTRY_ID_WORK_PHONE) );
				
				StringBuffer phoneNo = new StringBuffer();
				if (homePhone.length() > 0)
					phoneNo.append( homePhone ).append( "(H)" );
				if (workPhone.length() > 0) {
					if (phoneNo.length() > 0) phoneNo.append( ", " );
					phoneNo.append( workPhone ).append( "(W)" );
				}
				if (phoneNo.length() == 0) phoneNo.append( "(none)" );
				
				StreetAddress starsAddr = new StreetAddress();
				StarsLiteFactory.setStarsCustomerAddress(starsAddr, addr);
				String address = ServletUtils.getOneLineAddress(starsAddr);
				if (address.length() == 0) address = "(none)";
%>
                <tr valign="top"> 
                  <td width="15%" class="TableCell">
<% if (showEnergyCompany) { %>
				    <a href="" class="Link1" onClick="selectMemberAccount(<%= liteAcctInfo.getAccountID() %>, <%= member.getLiteID() %>); return false;"><%= liteAcctInfo.getCustomerAccount().getAccountNumber() %></a> 
<% } else { %>
				    <a href="" class="Link1" onClick="selectAccount(<%= liteAcctInfo.getAccountID() %>); return false;"><%= liteAcctInfo.getCustomerAccount().getAccountNumber() %></a> 
<% } %>
                  </td>
                  <td width="18%" class="TableCell"><%= contact.getContLastName() + ", " + contact.getContFirstName() %> 
                  </td>
                  <td width="17%" class="TableCell"><%= phoneNo.toString() %></td>
                  <td class="TableCell"><%= address %></td>
<% if (showEnergyCompany) { %>
                  <td width="15%" class="TableCell"><%= member.getName() %></td>
<% } %>
                </tr>
<%
			}
%>
              </table>
			</form>
<%
		}
	}
%>
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
