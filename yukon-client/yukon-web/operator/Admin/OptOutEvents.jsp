<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.database.data.lite.stars.LiteCustomerAccount" %>
<%@ page import="com.cannontech.database.data.lite.stars.LiteStarsLMHardware" %>
<%@ page import="com.cannontech.stars.util.OptOutEventQueue" %>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
<script language="JavaScript">
function selectAccount(accountID) {
	var form = document.custForm;
	form.AccountID.value = accountID;
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
              <span class="TitleHeader">ADMINISTRATION - OPT OUT EVENTS</span><br>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              <br>
              <table width="450" border="1" cellspacing="0" cellpadding="1" align="center">
                <tr> 
                  <td class="HeaderCell" width="115">Start Date/Time</td>
                  <td class="HeaderCell" width="87">Duration</td>
                  <td class="HeaderCell" width="115">Account #</td>
                  <td class="HeaderCell" width="115">Serial #</td>
                </tr>
<%
	OptOutEventQueue.OptOutEvent[] events = liteEC.getOptOutEventQueue().getOptOutEvents(liteEC.getLiteID());
	for (int i = 0; i < events.length; i++) {
		LiteCustomerAccount liteAccount = liteEC.getBriefCustAccountInfo(events[i].getAccountID(), true).getCustomerAccount();
		String serialNo = "----";
		if (events[i].getInventoryID() > 0)
			serialNo = ((LiteStarsLMHardware) liteEC.getInventoryBrief(events[i].getInventoryID(), true)).getManufacturerSerialNumber();
%>
                <tr> 
                  <td width="115" class="TableCell"><%= dateTimeFormat.format(new Date(events[i].getStartDateTime())) %></td>
                  <td width="87" class="TableCell"><%= ServletUtils.getDurationFromHours(events[i].getPeriod()) %></td>
                  <td width="115" class="TableCell">
				    <a href="" class="Link1" onclick="selectAccount(<%= liteAccount.getAccountID() %>); return false;"><%= liteAccount.getAccountNumber() %></a>
				  </td>
                  <td width="115" class="TableCell"><%= serialNo %></td>
                </tr>
<%
	}
%>
              </table>
              <br>
              <table width="600" border="0" cellspacing="0" cellpadding="5" align="center">
                <tr> 
                  <td align="center"> 
                    <input type="button" name="Back" value="Back" onClick="location.href='AdminTest.jsp'">
                  </td>
                </tr>
              </table>
            </div>
			<form name="custForm" method="POST" action="<%= request.getContextPath() %>/servlet/SOAPClient">
		      <input type="hidden" name="action" value="GetCustAccount">
              <input type="hidden" name="AccountID" value="">
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
