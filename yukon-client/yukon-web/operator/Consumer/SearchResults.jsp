<%@ include file="include/StarsHeader.jsp" %>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">

<script language="JavaScript">
<!--
function MM_reloadPage(init) {  //reloads the window if Nav4 resized
  if (init==true) with (navigator) {if ((appName=="Netscape")&&(parseInt(appVersion)==4)) {
    document.MM_pgW=innerWidth; document.MM_pgH=innerHeight; onresize=MM_reloadPage; }}
  else if (innerWidth!=document.MM_pgW || innerHeight!=document.MM_pgH) location.reload();
}
MM_reloadPage(true);
// -->

function selectAccount(accountID) {
	var form = document.resultForm;
	form.AccountID.value = accountID;
	form.submit();
}
</script>
</head>

<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="../../WebConfig/yukon/ConsumerImage.jpg">&nbsp;</td>
          <td valign="bottom" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>"/>">&nbsp;</td>
              </tr>
              <tr> 
                  <td width="265" height = "28" class="PageHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Customer 
                    Account Information&nbsp;&nbsp;</td>
                  
                <td width="253" valign="middle">&nbsp;</td>
                  <td width="58" valign="middle"> 
                    <div align="center"><span class="MainText"><a href="../Operations.jsp" class="Link3">Home</a></span></div>
                  </td>
                  <td width="57" valign="middle"> 
                    <div align="left"><span class="MainText"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log Off</a>&nbsp;</span></div>
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
          <td  valign="top" width="101">&nbsp; </td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" height="400" valign="top" bgcolor="#FFFFFF">
              
            <div align="center"><% String header = "SEARCH RESULTS"; %><%@ include file="include/InfoSearchBar2.jsp" %>
			<% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %></div>
<%
	StarsSearchCustomerAccountResponse resp = (StarsSearchCustomerAccountResponse) user.getAttribute(ServletUtils.ATT_ACCOUNT_SEARCH_RESULTS);
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
                <td width="187" class="HeaderCell">Name</td>
                <td width="290" class="HeaderCell">Address</td>
                <td width="112" class="HeaderCell">Phone#</td>
              </tr>
<%
			for (int i = 0; i < resp.getStarsCustAccountBriefCount(); i++) {
				StarsCustAccountBrief acctBrief = resp.getStarsCustAccountBrief(i);
%>
              <tr valign="top"> 
                <td width="187" class="TableCell">
				  <a href="" class="Link1" onclick="selectAccount(<%= acctBrief.getAccountID() %>); return false;"><%= acctBrief.getContactName() %></a>
				</td>
                <td width="290" class="TableCell"><%= acctBrief.getStreetAddress() %></td>
                <td width="112" class="TableCell"><%= acctBrief.getContPhoneNumber() %></td>
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
        <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
    </tr>
      </table>
    </td>
	</tr>
</table>
<br>
</body>
</html>
