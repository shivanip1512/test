<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.database.data.lite.stars.LiteCustomerAccount" %>
<%@ page import="com.cannontech.database.data.lite.stars.LiteStarsLMHardware" %>
<%@ page import="com.cannontech.stars.util.OptOutEventQueue" %>
<%
	boolean showEnergyCompany = liteEC.getChildren().size() > 0 && AuthFuncs.checkRoleProperty(lYukonUser, AdministratorRole.ADMIN_MANAGE_MEMBERS);
	
	int memberID = -1;
	LiteStarsEnergyCompany member = null;
	if (request.getParameter("Member") != null) {
		memberID = Integer.parseInt(request.getParameter("Member"));
		if (memberID >= 0)
			member = StarsDatabaseCache.getInstance().getEnergyCompany(memberID);
	}
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
<script language="JavaScript">
function changeMember(form) {
	form.attributes["action"].value = "";
	form.submit();
}

function selectAccount(accountID, memberID) {
	var form = document.custForm;
	form.AccountID.value = accountID;
	form.SwitchContext.value = memberID;
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
          <td  valign="top" width="101">&nbsp;</td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" height="400" valign="top" bgcolor="#FFFFFF">
            <div align="center"> <br>
              <span class="TitleHeader">ADMINISTRATION - SCHEDULED <cti:getProperty propertyid="<%= ConsumerInfoRole.WEB_TEXT_OPT_OUT_NOUN %>" defaultvalue="opt out" format="upper"/> 
              EVENTS</span><br>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              <br>
<% if (showEnergyCompany) { %>
              <form method="post" action="">
              <table width="400" border="0" cellspacing="0" cellpadding="1" align="center">
                <tr>
                  <td align="center" class="MainText">Member: 
                    <select name="Member" onchange="this.form.submit()">
                      <option value="-1">All</option>
                      <%
	ArrayList descendants = ECUtils.getAllDescendants(liteEC);
	for (int i = 0; i < descendants.size(); i++) {
		LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) descendants.get(i);
		String selected = (member != null && company.equals(member))? "selected" : "";
%>
                      <option value="<%= company.getLiteID() %>" <%= selected %>><%= company.getName() %></option>
                      <%
	}
%>
                    </select>
                  </td>
                </tr>
              </table>
              </form>
<% } %>
              <table width="450" border="1" cellspacing="0" cellpadding="1" align="center">
                <tr> 
                  <td class="HeaderCell" width="22%">Start Date/Time</td>
                  <td class="HeaderCell" width="17%">Duration</td>
                  <td class="HeaderCell" width="22%">Account #</td>
                  <td class="HeaderCell" width="22%">Serial #</td>
<% if (showEnergyCompany) { %>
                  <td class="HeaderCell" width="17%">Member</td>
<% } %>
                </tr>
<%
	ArrayList descendants = ECUtils.getAllDescendants(liteEC);
	for (int i = 0; i < descendants.size(); i++) {
		LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) descendants.get(i);
		if (member != null && !company.equals(member)) continue;
		
		OptOutEventQueue.OptOutEvent[] events = OptOutEventQueue.getInstance().getOptOutEvents(company.getLiteID());
		for (int j = 0; j < events.length; j++) {
			LiteCustomerAccount liteAccount = company.getBriefCustAccountInfo(events[j].getAccountID(), true).getCustomerAccount();
			String serialNo = "----";
			if (events[j].getInventoryID() > 0)
				serialNo = ((LiteStarsLMHardware) company.getInventoryBrief(events[j].getInventoryID(), true)).getManufacturerSerialNumber();
%>
                <tr> 
                  <td width="22%" class="TableCell"><%= dateTimeFormat.format(new Date(events[j].getStartDateTime())) %></td>
                  <td width="17%" class="TableCell"><%= ServletUtils.getDurationFromHours(events[j].getPeriod()) %></td>
                  <td width="22%" class="TableCell">
				    <a href="" class="Link1" onclick="selectAccount(<%= liteAccount.getAccountID() %>, <%= company.getLiteID() %>); return false;"><%= liteAccount.getAccountNumber() %></a>
				  </td>
                  <td width="22%" class="TableCell"><%= serialNo %></td>
<% if (showEnergyCompany) { %>
                  <td width="17%" class="TableCell"><%= company.getName() %></td>
<% } %>
                </tr>
<%
		}
	}
%>
              </table>
            </div>
			<form name="custForm" method="POST" action="<%= request.getContextPath() %>/servlet/SOAPClient">
		      <input type="hidden" name="action" value="GetCustAccount">
              <input type="hidden" name="AccountID" value="">
              <input type="hidden" name="SwitchContext" value="">
			  <input type="hidden" name="REDIRECT" value="<%=request.getContextPath()%>/operator/Consumer/Update.jsp">
			  <input type="hidden" name="REFERRER" value="<%=request.getContextPath()%>/operator/Admin/OptOutEvents.jsp">
			</form>
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
