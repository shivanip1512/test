<%
	/**
	 * Main portal for operators
	 **/
%>

<%@ page import="com.cannontech.roles.application.WebClientRole" %>

<%@ page import="com.cannontech.roles.operator.AdministratorRole" %>
<%@ page import="com.cannontech.roles.operator.CommercialMeteringRole" %>
<%@ page import="com.cannontech.roles.operator.ConsumerInfoRole" %>
<%@ page import="com.cannontech.roles.operator.DirectCurtailmentRole" %>
<%@ page import="com.cannontech.roles.operator.DirectLoadcontrolRole" %>
<%@ page import="com.cannontech.roles.operator.EnergyBuybackRole" %>
<%@ page import="com.cannontech.roles.operator.OddsForControlRole" %>
<%@ page import="com.cannontech.roles.operator.InventoryRole" %>
<%@ page import="com.cannontech.roles.operator.WorkOrderRole" %>

<%@ taglib uri="/WEB-INF/cti.tld" prefix="cti" %>
<%@ include file="Consumer/include/StarsHeader.jsp" %>

<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
<script language="JavaScript">
function confirmDelete() {
	if (confirm("Are you sure you want to delete the energy company and all customer account information belongs to it?")
		&& confirm("Are you really sure you want to delete the energy company?"))
		document.DeleteForm.submit();
}
</script>
</head>

<body class="Background" text="#000000" leftmargin="0" topmargin="0" link="#000000" vlink="#000000" alink="#000000">
<table width="658" border="0" cellspacing="0" height="102" cellpadding="0">
  <tr>
    <td width="657"valign="bottom">
      <table width="657" border="0" cellspacing="0" cellpadding="3" height="102"> 
        <tr> 
          <td id="Header" background="../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>"/>" height="77" >&nbsp;</td>
        </tr>
        <tr>
         	<td>
               <div align="right"><span class="MainText"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log Off</a>&nbsp;</span></div>
            </td>
        </tr>
      </table>
    </td>
    <td width="1" bgcolor="#000000"><img src="../Images/Icons/VerticalRule.gif"></td>
  </tr>
</table>
<table width="658" border="0" cellspacing="0" cellpadding="0" align="left">
  <tr> 
    <td width="102" bgcolor="#000000" height="1"><img src="../Images/Icons/VerticalRule.gif"></td>
    <td width="555" bgcolor="#000000" height="1"><img src="../Images/Icons/VerticalRule.gif"></td>
    <td width="1" background="../Images/Icons/VerticalRule.gif" height="1"></td>
  </tr>
  
<cti:checkRole roleid="<%= ConsumerInfoRole.ROLEID %>">
  <tr> 
    <td width="102" background="Consumer/ConsumerImage.jpg" height="102">&nbsp;</td>
    <td width="555" bgcolor="#FFFFFF" height="102" valign="top"><img src="ConsumerHeader.gif" width="229" height="15" border="0"><br>
      <table width="525" border="0" cellspacing="0" cellpadding="3" align="center">
        <tr> 
          <td width="25%"><font face="Arial, Helvetica, sans-serif" size="2"> 
            </font></td>
          <td width="25%">&nbsp;</td>
          <td width="43%"><font face="Arial, Helvetica, sans-serif" size="1">Search 
            for existing customer:</font></td>
          <td width="7%">&nbsp;</td>
        </tr>
        <tr> 
          <td width="25%" class="MainText"> 
            <div align = "center" style = "border:solid 1px #666999;">
              <a href = 'Consumer/New.jsp<cti:checkProperty propertyid="<%= ConsumerInfoRole.NEW_ACCOUNT_WIZARD %>">?Wizard=true</cti:checkProperty>' class = "Link1" style = "text-decoration:none;">New Account</a>
            </div>
          </td>
          <td class="MainText" width="25%">
<cti:checkProperty propertyid="<%= ConsumerInfoRole.IMPORT_CUSTOMER_ACCOUNT %>">
            <div align = "center" style = "border:solid 1px #666999;">
			  <a href="Consumer/ImportAccount.jsp" class = "Link1" style = "text-decoration:none;">Import Accounts</a>
			</div>
</cti:checkProperty>
		  </td>
          <form name = "custSearchForm" method="POST" action="<%=request.getContextPath()%>/servlet/SOAPClient">
            <input type="hidden" name="action" value="SearchCustAccount">
            <td class = "MainText" width="43%" align = "right" nowrap> 
              <select name="SearchBy">
<%
	Integer lastOption = (Integer) user.getAttribute(ServletUtils.ATT_LAST_SEARCH_OPTION);
	StarsCustSelectionList searchByList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_SEARCH_TYPE );
	for (int i = 0; i < searchByList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = searchByList.getStarsSelectionListEntry(i);
		String selectedStr = (lastOption != null && entry.getEntryID() == lastOption.intValue()) ? "selected" : "";
%>
                <option value="<%= entry.getEntryID() %>" <%= selectedStr %>><%= entry.getContent() %></option>
<%
	}
%>
              </select>
              &nbsp; 
              <input type="text" name="SearchValue" size="15">
              &nbsp; </td>
            <td class = "MainText" width="7%" valign = "top"><img class="Clickable" src="GoButton.gif" width="23" height="20" onClick = "Javascript:document.custSearchForm.submit();"> 
            </td>
          </form>
        </tr>
      </table>
    </td>
    <td width="1" background="../Images/Icons/VerticalRule.gif" height="102"></td>
  </tr>
</cti:checkRole>

<cti:checkRole roleid="<%= CommercialMeteringRole.ROLEID %>">
  <tr> 
    <td width="102" bgcolor="#000000" height="1"><img src="../Images/Icons/VerticalRule.gif"></td>
    <td width="555" bgcolor="#000000" height="1"><img src="../Images/Icons/VerticalRule.gif"></td>
    <td width="1" background="../Images/Icons/VerticalRule.gif" height="1"></td>
  </tr>
  <tr> 
    <td width="102" height="102" background="Metering/MeterImage.jpg">&nbsp;</td>
    <td width="555" bgcolor="#FFFFFF" height="102" valign="top"><img src="MeteringHeader.gif" width="161" height="15"><br>
      <table width="525" border="0" cellspacing="0" cellpadding="3" align="center">
        <tr> 
          <td width="25%">&nbsp;</td>
          <td width="25%">&nbsp;</td>
          <td >&nbsp;</td>
        </tr>
        <tr> 
          <form method="post" action="Metering/Billing.jsp">
            <td width="25%" class = "MainText"> 
              <div align = "center" style = "border:solid 1px #666999;"><a href = "Metering/Billing.jsp" class = "Link1" style = "text-decoration:none;">Billing</a></div>
            </td>
            <td width="25%" class = "MainText"> 
              <div align = "center" style = "border:solid 1px #666999;"><a href = "Metering/Metering.jsp" class = "Link1" style = "text-decoration:none;">All Trends</a></div>
            </td>
          </form>
          <form method="post" action="Metering/Metering.jsp">
            <td class = "MainText"> </td>
          </form>
          <form method="get" action="Metering/Metering.jsp">
          </form>
        </tr>
      </table>
    </td>
    <td width="1" background="../Images/Icons/VerticalRule.gif" height="102"></td>
  </tr>
</cti:checkRole>

<cti:checkMultiRole roleid="<%= Integer.toString(DirectLoadcontrolRole.ROLEID) + ',' + Integer.toString(DirectCurtailmentRole.ROLEID) + ',' + Integer.toString(EnergyBuybackRole.ROLEID) %>">
  <tr> 
    <td width="102" bgcolor="#000000" height="1"><img src="../Images/Icons/VerticalRule.gif"></td>
    <td width="555" bgcolor="#000000" height="1"><img src="../Images/Icons/VerticalRule.gif"></td>
    <td width="1" background="../Images/Icons/VerticalRule.gif" height="1"></td>
  </tr>
  <tr> 
    <td width="102" height="102" background="LoadControl/LoadImage.jpg">&nbsp;</td>
    <td width="555" bgcolor="#FFFFFF" height="102" valign="top">
      <div align="left"><img src="LoadHeader.gif"><br>
      </div>
      <div align="left"></div>
      <table width="525" border="0" cellspacing="0" cellpadding="3" align="center">
        <tr> 
          <td width="25%" >&nbsp;</td>
          <td width="25%" >&nbsp;</td>
          <td width="25%" >&nbsp;</td>
          <td width="25%">&nbsp;</td>
          </tr>
        <tr> 
          <form method="post" action="LoadControl/oper_direct.jsp">
            <td width="25%" class = "MainText"> <cti:checkRole roleid="<%= DirectLoadcontrolRole.ROLEID %>"> 
              <div align = "center" style = "border:solid 1px #666999;"> <a href = "LoadControl/oper_direct.jsp" class = "Link1" style = "text-decoration:none;">Direct</a></div>
</cti:checkRole>
            </td>
          </form>
          <form method="post" action="LoadControl/oper_mand.jsp">
            <td width="25%" class = "MainText"> <cti:checkRole roleid="<%= DirectCurtailmentRole.ROLEID %>"> 
              <div align = "center" style = "border:solid 1px #666999;"> <a href = "LoadControl/oper_mand.jsp" class = "Link1" style = "text-decoration:none;"><cti:getProperty propertyid="<%= DirectCurtailmentRole.CURTAILMENT_LABEL%>"/></a></div>
</cti:checkRole>
			</td>
          </form>
          <form method="post" action="LoadControl/oper_ee.jsp">
            <td width = "25%" class = "MainText"> <cti:checkRole roleid="<%= EnergyBuybackRole.ROLEID %>"> 
              <div align = "center" style = "border:solid 1px #666999;"> <a href = "LoadControl/oper_ee.jsp" class = "Link1" style = "text-decoration:none;"><cti:getProperty propertyid="<%= EnergyBuybackRole.ENERGY_BUYBACK_LABEL%>"/></a></div>
</cti:checkRole>
			</td>
		  </form>
          <form method="post" action="Consumer/Odds.jsp">
            <td width = "25%" class = "MainText"> <cti:checkRole roleid="<%= OddsForControlRole.ROLEID %>"> 
              <div align = "center" style = "border:solid 1px #666999;"> <a href = "Consumer/Odds.jsp" class = "Link1" style = "text-decoration:none;">Odds 
                for Control</a></div>
</cti:checkRole>
			</td>
		  </form>
          </tr>
        <tr> 
          <td width="25%" class = "MainText">&nbsp;</td>
          <td width="25%" class = "MainText">&nbsp;</td>
          <td width = "25%" class = "MainText">&nbsp;</td>
          <td width="25%">&nbsp;</td>
        </tr>
      </table>
    </td>
    <td width="1" background="../Images/Icons/VerticalRule.gif" height="102"></td>
  </tr>
</cti:checkMultiRole>

<cti:checkRole roleid="<%= InventoryRole.ROLEID %>">
  <tr> 
    <td width="102" bgcolor="#000000" height="1"><img src="../Images/Icons/VerticalRule.gif"></td>
    <td width="555" bgcolor="#000000" height="1"><img src="../Images/Icons/VerticalRule.gif"></td>
    <td width="1" background="../Images/Icons/VerticalRule.gif" height="1"></td>
  </tr>
  <tr> 
    <td width="102" height="102" background="Hardware/InventoryImage.jpg">&nbsp;</td>
    <td width="555" bgcolor="#FFFFFF" height="102" valign="top"><img src="InventoryHeader.gif" width="148" height="15"><br>
      <table width="525" border="0" cellspacing="0" cellpadding="3" align="center">
        <tr> 
          <td width="25%" height="30" valign="bottom">&nbsp;</td>
          <td width="25%" height="30" valign="bottom">&nbsp;</td>
          <td width="43%" height="30" valign="bottom"><font face="Arial, Helvetica, sans-serif" size="1">Search 
            for existing hardware:</font></td>
          <td height="30" valign="bottom" width="7%">&nbsp;</td>
        </tr>
        <tr> 
          <td width="25%" class="MainText"> <cti:checkProperty propertyid="<%= InventoryRole.INVENTORY_SHOW_ALL %>"> 
            <div align = "center" style = "border:solid 1px #666999;"><a href = "Hardware/Inventory.jsp" class = "Link1" style = "text-decoration:none;">Inventory</a></div>
            </cti:checkProperty></td>
          <td width="25%" class="MainText">&nbsp;</td>
          <form name = "invSearchForm" method="post" action="<%= request.getContextPath() %>/servlet/InventoryManager">
		    <input type="hidden" name="action" value="SearchInventory">
			<input type="hidden" name="REDIRECT" value="<%= request.getContextPath() %>/operator/Hardware/ResultSet.jsp">
            <td width="43%" class="MainText" align="right" nowrap> 
              <select name="SearchBy">
<%
	StarsCustSelectionList searchByList = (StarsCustSelectionList) selectionListTable.get(YukonSelectionListDefs.YUK_LIST_NAME_INV_SEARCH_BY);
	for (int i = 0; i < searchByList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = searchByList.getStarsSelectionListEntry(i);
%>
			    <option value="<%= entry.getYukonDefID() %>"><%= entry.getContent() %></option>
<%
	}
%>
			  </select>
			  &nbsp; 
              <input type="text" name="SearchValue" size="15">
              &nbsp; </td>
            <td valign = "top" class="MainText" width="7%"> <img src="GoButton.gif" width="23" height="20" onclick = "Javascript:document.invSearchForm.submit();" > 
            </td>
          </form>
        </tr>
      </table>
    </td>
    <td width="1" background="../Images/Icons/VerticalRule.gif" height="102"></td>
  </tr>
</cti:checkRole>

<cti:checkRole roleid="<%= WorkOrderRole.ROLEID %>">
  <tr> 
    <td width="102" bgcolor="#000000" height="1"><img src="../Images/Icons/VerticalRule.gif"></td>
    <td width="555" bgcolor="#000000" height="1"><img src="../Images/Icons/VerticalRule.gif"></td>
    <td width="1" background="../Images/Icons/VerticalRule.gif" height="1"></td>
  </tr>
  <tr> 
    <td width="102" height="102" background="WorkOrder/WorkImage.jpg">&nbsp;</td>
    <td width="555" bgcolor="#FFFFFF" height="102" valign="top"><img src="WorkHeader.gif" width="104" height="15"><br>
      <table width="525" border="0" cellspacing="0" cellpadding="3" align="center">
        <tr valign="bottom"> 
          <td width="25%">&nbsp;</td>
          <td width="25%">&nbsp;</td>
          <td width="43%"><font face="Arial, Helvetica, sans-serif" size="1">Search 
            for existing serivce order:</font></td>
          <td width="7%">&nbsp;</td>
        </tr>
        <tr> 
          <td width="25%" class = "MainText"> <cti:checkProperty propertyid="<%= WorkOrderRole.WORK_ORDER_SHOW_ALL %>"> 
            <div align = "center" style = "border:solid 1px #666999;"><a href = "WorkOrder/SOList.jsp" class = "Link1" style = "text-decoration:none;">Service Order List</a></div>
</cti:checkProperty>
          </td>
          <td width="25%" class = "MainText">&nbsp;</td>
          <form name = "soSearchForm" method="post" action="<%= request.getContextPath() %>/servlet/WorkOrderManager">
		    <input type="hidden" name="action" value="SearchWorkOrder">
			<input type="hidden" name="REDIRECT" value="<%= request.getContextPath() %>/operator/WorkOrder/SearchResults.jsp">
            <td width="43%" align="right" class = "MainText" nowrap> 
              <select name="SearchBy">
              <%
	StarsCustSelectionList searchByList = (StarsCustSelectionList) selectionListTable.get(YukonSelectionListDefs.YUK_LIST_NAME_SO_SEARCH_BY);
	for (int i = 0; i < searchByList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = searchByList.getStarsSelectionListEntry(i);
%>
              <option value="<%= entry.getYukonDefID() %>"><%= entry.getContent() %></option>
              <%
	}
%>
            </select>
            &nbsp; 
            <input type="text" name="SearchValue" size="15">
            &nbsp; </td>
            <td width="7%" valign="top" class="MainText"><img src="GoButton.gif" width="23" height="20" onClick = "Javascript:document.soSearchForm.submit();" ></td>
		  </form>
        </tr>
      </table>
    </td>
    <td width="1" background="../Images/Icons/VerticalRule.gif" height="102"></td>
  </tr>
</cti:checkRole> 

<cti:checkRole roleid="<%= AdministratorRole.ROLEID %>">
  <tr> 
    <td width="102" bgcolor="#000000" height="1"><img src="../Images/Icons/VerticalRule.gif"></td>
    <td width="555" bgcolor="#000000" height="1"><img src="../Images/Icons/VerticalRule.gif"></td>
    <td width="1" background="../Images/Icons/VerticalRule.gif" height="1"></td>
  </tr>
  <tr>
    <td width="102" bgcolor="#000000" height="102" background="Admin/AdminImage.jpg">&nbsp;</td>
    <td bgcolor="#FFFFFF" height="102" valign="top"><img src="AdminHeader.gif" width="129" height="15"><br>
      <table width="525" border="0" cellspacing="0" cellpadding="3" align="center">
        <tr> 
          <td width="25%">&nbsp;</td>
          <td width="25%">&nbsp;</td>
          <td width="25%">&nbsp;</td>
          <td width="25%">&nbsp;</td>
        </tr>
        <tr> 
          <td align = "center" class = "MainText" width="25%"> <cti:checkProperty propertyid="<%= AdministratorRole.ADMIN_CONFIG_ENERGY_COMPANY %>"> 
            <div align = "center" style = "border:solid 1px #666999;"><a href = "Admin/AdminTest.jsp" class = "Link1" style = "text-decoration:none;">Config 
              Energy Company</a></div>
            </cti:checkProperty> </td>
		  <form name="DeleteForm" method="post" action="<%= request.getContextPath() %>/servlet/StarsAdmin">
		    <input type="hidden" name="action" value="DeleteEnergyCompany">
            <td align = "center" class="MainText" width="25%"> <cti:checkProperty propertyid="<%= AdministratorRole.ADMIN_DELETE_ENERGY_COMPANY %>"> 
              <div align = "center" style = "border:solid 1px #666999;"><span class="Clickable" style="text-decoration:none;" onclick="confirmDelete()">Delete 
                Energy Company</span></div>
              </cti:checkProperty> </td>
		  </form>
          <td align = "center" class = "MainText" width="25%"> <cti:checkProperty propertyid="<%= AdministratorRole.ADMIN_CREATE_ENERGY_COMPANY %>"> 
            <div align = "center" style = "border:solid 1px #666999;"><a href = "Consumer/NewEnergyCompany.jsp" class = "Link1" style = "text-decoration:none;">New 
              Energy Company</a></div>
            </cti:checkProperty> </td>
          <td align = "center" class = "MainText" width="25%">&nbsp;</td>
        </tr>
      </table>
    </td>
    <td width="1" background="../Images/Icons/VerticalRule.gif" height="16"></td>
  </tr>
</cti:checkRole>
  </table>
<div align="center"></div>
</body>

</html>
