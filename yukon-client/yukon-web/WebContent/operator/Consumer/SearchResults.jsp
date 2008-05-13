<%@ include file="include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.core.dao.DaoFactory" %>
<%@ page import="com.cannontech.database.data.lite.LiteContact" %>
<%@ page import="com.cannontech.database.data.lite.LiteCustomer" %>
<%@ page import="com.cannontech.database.data.lite.LiteCICustomer" %>
<%@ page import="com.cannontech.database.data.lite.LiteAddress" %>
<%@ page import="com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation" %>
<%@ page import="com.cannontech.database.data.lite.stars.StarsLiteFactory" %>
<%
    String pageName = request.getContextPath() + "/operator/Consumer/SearchResults.jsp";

	StarsSearchCustomerAccountResponse resp = (StarsSearchCustomerAccountResponse)
			session.getAttribute(ServletUtils.ATT_ACCOUNT_SEARCH_RESULTS);
	
	Integer lastSearchOption = (Integer) session.getAttribute(ServletUtils.ATT_LAST_ACCOUNT_SEARCH_OPTION);
	int searchByDefID = YukonListEntryTypes.YUK_DEF_ID_SEARCH_TYPE_ACCT_NO;
	if (lastSearchOption != null)
		searchByDefID = DaoFactory.getYukonListDao().getYukonListEntry(lastSearchOption.intValue()).getYukonDefID();
	
	boolean showEnergyCompany = DaoFactory.getAuthDao().checkRoleProperty(lYukonUser, AdministratorRole.ADMIN_MANAGE_MEMBERS)
			&& (liteEC.getChildren().size() > 0);
	List<LiteStarsEnergyCompany> descendants = null;
	if (showEnergyCompany) descendants = com.cannontech.stars.util.ECUtils.getAllDescendants(liteEC);
%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
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

function selectMemberAccount(accountID, memberID) {
	var form = document.resultForm;
	form.AccountID.value = accountID;
	form.SwitchContext.value = memberID;
	form.submit();
}

function navPage()
{
    location.href="<%=pageName%>?page="+document.getElementById("GoPage").value;
}
</script>
</head>

<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <%@ include file="include/HeaderBar.jspf" %>
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
              
            <div align="center"><% String header = "SEARCH RESULTS"; %><%@ include file="include/InfoSearchBar2.jspf" %>
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
              <span class="MainText">The following customer accounts were found:</span> 
            </div>
			<form name="resultForm" method="POST" action="<%= request.getContextPath() %>/servlet/SOAPClient">
		      <input type="hidden" name="action" value="GetCustAccount">
              <input type="hidden" name="AccountID" value="">
			  <input type="hidden" name="REDIRECT" value="<%=request.getContextPath()%>/operator/Consumer/Update.jsp">
			  <input type="hidden" name="REFERRER" value="<%=request.getContextPath()%>/operator/Consumer/SearchResults.jsp">
<% if (showEnergyCompany) { %>
			  <input type="hidden" name="SwitchContext" value="">
<% } %>
<%
    
    int pageSize = 250;
    int pageIndx = request.getParameter("page") != null ? Integer.valueOf(request.getParameter("page")): 1;
    if (pageIndx < 1) pageIndx = 1;
    int maxPageNo = (int) Math.ceil(resp.getStarsBriefCustAccountInfoCount() * 1.0 / pageSize);
    if (pageIndx > maxPageNo) page = maxPageNo;
    int minOrderNo = (pageIndx- 1) * pageSize + 1;
    int maxOrderNo = Math.min(pageIndx * pageSize, resp.getStarsBriefCustAccountInfoCount());
   

    int maxPageDigit = (int)(Math.log(maxPageNo) / Math.log(10)) + 1;
    String navHTML = String.valueOf(minOrderNo).toString();
    if (maxOrderNo > minOrderNo)
        navHTML += "-" +maxOrderNo;
    navHTML += " of " + resp.getStarsBriefCustAccountInfoCount() + " | ";
    if (pageIndx == 1)
        navHTML += "<font color='#CCCCCC'>First</font>";
    else
        navHTML += "<a class='Link1' href='" + pageName + "?page=1'>First</a>";
    navHTML += " | ";
    if (pageIndx == 1)
        navHTML += "<font color='#CCCCCC'>Previous</font>";
    else
        navHTML += "<a class='Link1' href='" + pageName + "?page=" + (pageIndx-1) + "'>Previous</a>";
    navHTML += " | ";
    if (pageIndx == maxPageNo)
        navHTML += "<font color='#CCCCCC'>Next</font>";
    else
        navHTML += "<a class='Link1' href='" + pageName + "?page="+ (pageIndx+1) + "'>Next</a>";
    navHTML += " | ";
    if (pageIndx == maxPageNo)
        navHTML += "<font color='#CCCCCC'>Last</font>";
    else
        navHTML += "<a class='Link1' href='" + pageName+ "?page=" + maxPageNo +"'>Last</a>";
%>            
    <table width='615' border='0' cellspacing='0' cellpadding='3' class='TableCell' align='center'>
      <tr>
        <td><%=navHTML%></td>
        <td align='right'>Page(1-<%=maxPageNo%>)
          <input type='text' id='GoPage' style='border:1px solid #0066CC; font:11px' size='<%=maxPageDigit%>' value='<%=pageIndx%>' onkeypress='if (event.keyCode == 13) {navPage();return false;}'>
          <input type='button' id='Go' style='font:11px; margin-bottom:-1px' value='Go' onclick='location.href="<%=pageName%>?page="+document.getElementById("GoPage").value;'>
        </td>
      </tr>
    </table>
              <table width="615" border="1" cellspacing="0" cellpadding="3" align="center">                
                <tr> 
                  <td width="15%" class="HeaderCell">Account #</td>
                  <td width="18%" class="HeaderCell">Name</td>
                  <td width="17%" class="HeaderCell">
<% if (searchByDefID == YukonListEntryTypes.YUK_DEF_ID_SEARCH_TYPE_MAP_NO) { %>
                    Map#
<% } else { %>
                    Phone#
<% } %>
                  </td>
                  <td class="HeaderCell">Address</td>
<% if (showEnergyCompany) { %>
                  <td width="15%" class="HeaderCell">Energy Company</td>
<% } %>
                </tr>
<%
            LiteStarsEnergyCompany member = liteEC;
            for (int i = minOrderNo-1; i < maxOrderNo; i++) {            
//            for (int i = 0; i < resp.getStarsBriefCustAccountInfoCount(); i++) {
                if (resp.getStarsBriefCustAccountInfo(i).hasEnergyCompanyID())
                {
                    if( member.getEnergyCompanyID() != resp.getStarsBriefCustAccountInfo(i).getEnergyCompanyID())
                    {   //only load this if needed
                        member = StarsDatabaseCache.getInstance().getEnergyCompany(resp.getStarsBriefCustAccountInfo(i).getEnergyCompanyID());
                    }
                }
                
                LiteStarsCustAccountInformation liteAcctInfo = member.getBriefCustAccountInfo(resp.getStarsBriefCustAccountInfo(i).getAccountID(), true);
				if (liteAcctInfo == null) continue;
				
				LiteCustomer customer = liteAcctInfo.getCustomer();
				LiteContact contact = DaoFactory.getContactDao().getContact(customer.getPrimaryContactID());
				LiteAddress addr = member.getAddress(liteAcctInfo.getAccountSite().getStreetAddressID());
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
<%                
                if (searchByDefID == YukonListEntryTypes.YUK_DEF_ID_SEARCH_TYPE_COMPANY_NAME) {  
%>
                  <td width="18%" class="TableCell"><%= StringEscapeUtils.escapeHtml(LiteStarsCustAccountInformation.getCompanyName(customer.getLiteID())) %>  
                  </td>
<% } else if (customer instanceof LiteCICustomer) { %>                 
                  <td width="30%" class="TableCell"><%= StringEscapeUtils.escapeHtml(contact.getContLastName()) + ", " + StringEscapeUtils.escapeHtml(contact.getContFirstName()) + " (" + StringEscapeUtils.escapeHtml(((LiteCICustomer)customer).getCompanyName()) + ")"%>    
                  </td>
<% } else { %>                 
                  <td width="18%" class="TableCell"><%= StringEscapeUtils.escapeHtml(contact.getContLastName()) + ", " + StringEscapeUtils.escapeHtml(contact.getContFirstName()) %>  
                  </td>
<% } %>
                  <td width="17%" class="TableCell">
<%
				if (searchByDefID == YukonListEntryTypes.YUK_DEF_ID_SEARCH_TYPE_MAP_NO) {
%>
                    <%= StarsUtils.forceNotNull(liteAcctInfo.getAccountSite().getSiteNumber()) %>
<%
				}
				else {
					String homePhone = StarsUtils.getNotification(
							DaoFactory.getContactDao().getContactNotification(contact, YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE) );
					String workPhone = StarsUtils.getNotification(
							DaoFactory.getContactDao().getContactNotification(contact, YukonListEntryTypes.YUK_ENTRY_ID_WORK_PHONE) );
					
					StringBuffer phoneNo = new StringBuffer();
					if (homePhone.length() > 0)
						phoneNo.append( ServletUtils.formatPhoneNumberForDisplay(homePhone) ).append( "(H)" );
					if (workPhone.length() > 0) {
						if (phoneNo.length() > 0) phoneNo.append( ", " );
						phoneNo.append( ServletUtils.formatPhoneNumberForDisplay(workPhone) ).append( "(W)" );
					}
					if (phoneNo.length() == 0) phoneNo.append( "(none)" );
%>
                    <%= phoneNo.toString() %>
<%
				}
%>
                  </td>
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
