<%
	/**
	 * Main portal for operators
	 **/
%>

<%@ page import="com.cannontech.roles.application.ReportingRole" %>
<%@ page import="com.cannontech.roles.application.CommanderRole" %>
<%@ page import="com.cannontech.roles.application.BillingRole" %>
<%@ page import="com.cannontech.roles.operator.DirectCurtailmentRole" %>
<%@ page import="com.cannontech.roles.loadcontrol.DirectLoadcontrolRole" %>
<%@ page import="com.cannontech.roles.operator.EnergyBuybackRole" %>
<%@ page import="com.cannontech.roles.operator.OddsForControlRole" %>
<%@ page import="com.cannontech.roles.operator.CICurtailmentRole" %>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ include file="Consumer/include/StarsHeader.jsp" %>

<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
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
          <td id="Header" background="../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>" defaultvalue="yukon/DemoHeader.gif"/>" height="77" >&nbsp;</td>
        </tr>
        <tr>
          <td align="right">
<% if (session.getAttribute(com.cannontech.common.constants.LoginController.SAVED_YUKON_USERS) == null
	|| liteEC.getParent() == null) {
%>
            <span class="MainText"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log Off</a></span>
<% } else { %>
			<span class="MainText"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Back to <%= liteEC.getParent().getName() %></a></span>
<% } %>
          &nbsp;&nbsp;&nbsp;</td>
        </tr>
      </table>
    </td>
    <td width="1" bgcolor="#000000"><img src="../WebConfig/yukon/Icons/VerticalRule.gif"></td>
  </tr>
</table>
<table width="658" border="0" cellspacing="0" cellpadding="0" align="left">
  <tr> 
    <td width="102" bgcolor="#000000" height="1"><img src="../WebConfig/yukon/Icons/VerticalRule.gif"></td>
    <td width="555" bgcolor="#000000" height="1"><img src="../WebConfig/yukon/Icons/VerticalRule.gif"></td>
    <td width="1" background="../WebConfig/yukon/Icons/VerticalRule.gif" height="1"></td>
  </tr>
  
<cti:checkRole roleid="<%= ConsumerInfoRole.ROLEID %>">
  <tr> 
    <td width="102" background="../WebConfig/yukon/ConsumerImage.jpg" height="102">&nbsp;</td>
    <td width="555" bgcolor="#FFFFFF" height="102" valign="top"><img src="../WebConfig/yukon/ConsumerHeader.gif" width="229" height="15" border="0"><br>
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
              <a href='Consumer/New.jsp?Init=true<cti:checkProperty propertyid="<%= ConsumerInfoRole.NEW_ACCOUNT_WIZARD %>">&Wizard=true</cti:checkProperty>' class="Link1" style="text-decoration:none;">New Account</a>
            </div>
          </td>
          <td class="MainText" width="25%">
<%
	String importID = AuthFuncs.getRolePropertyValue(lYukonUser, ConsumerInfoRole.IMPORT_CUSTOMER_ACCOUNT);
	if (importID.equals("(none)")) importID = null;
	if (importID != null) {
		String importURI = "ImportAccount.jsp";
		String importLabel = "Import Account";
		if (importID.equalsIgnoreCase("STARS")) {
			importURI = "ImportSTARS.jsp";
			importLabel = "Import STARS";
		}
		else if (importID.equalsIgnoreCase("DSM")) {
			importURI = "ImportDSM.jsp";
			importLabel = "Import DSM";
		}
		else if (importID.equalsIgnoreCase("upload")) {
			importURI = "GenericUpload.jsp";
			importLabel = "Upload File";
		}
%>
            <div align = "center" style = "border:solid 1px #666999;">
			  <a href="Consumer/<%= importURI %>" class = "Link1" style = "text-decoration:none;"><%= importLabel %></a>
			</div>
<%
	}
%>
		  </td>
          <form name = "custSearchForm" method="POST" action="<%=request.getContextPath()%>/servlet/SOAPClient">
            <input type="hidden" name="action" value="SearchCustAccount">
            <td class = "MainText" width="43%" align = "right" nowrap> 
              <select name="SearchBy" onchange="document.custSearchForm.SearchValue.value=''">
<%
	if (selectionListTable != null) {
		Integer lastAcctOption = (Integer) session.getAttribute(ServletUtils.ATT_LAST_ACCOUNT_SEARCH_OPTION);
		StarsCustSelectionList searchByList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_SEARCH_TYPE );
		for (int i = 0; i < searchByList.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = searchByList.getStarsSelectionListEntry(i);
			if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_SEARCH_TYPE_METER_NO) continue;
			String selectedStr = (lastAcctOption != null && entry.getEntryID() == lastAcctOption.intValue()) ? "selected" : "";
%>
                <option value="<%= entry.getEntryID() %>" <%= selectedStr %>><%= entry.getContent() %></option>
<%
		}
	}
%>
              </select>
<%    String lastAcctValue = (String) session.getAttribute(ServletUtils.ATT_LAST_ACCOUNT_SEARCH_VALUE);%>
              &nbsp;
              <input type="text" name="SearchValue" size="15" value='<%=(lastAcctValue!=null?lastAcctValue:"")%>'>
              &nbsp; </td>
            <td class = "MainText" width="7%" valign = "top"><img class="Clickable" src="../WebConfig/yukon/Buttons/GoButton.gif" width="23" height="20" onClick = "Javascript:document.custSearchForm.submit();"> 
            </td>
          </form>
        </tr>
      </table>
    </td>
    <td width="1" background="../WebConfig/yukon/Icons/VerticalRule.gif" height="102"></td>
  </tr>
</cti:checkRole>

<cti:checkMultiRole roleid="<%= Integer.toString(CommercialMeteringRole.ROLEID) + ',' + Integer.toString(BillingRole.ROLEID) %>">
  <tr> 
    <td width="102" bgcolor="#000000" height="1"><img src="../WebConfig/yukon/Icons/VerticalRule.gif"></td>
    <td width="555" bgcolor="#000000" height="1"><img src="../WebConfig/yukon/Icons/VerticalRule.gif"></td>
    <td width="1" background="../WebConfig/yukon/Icons/VerticalRule.gif" height="1"></td>
  </tr>
  <tr> 
    <td width="102" height="102" background="../WebConfig/yukon/MeterImage.jpg">&nbsp;</td>
    <td width="555" bgcolor="#FFFFFF" height="102" valign="top"><img src="../WebConfig/yukon/MeteringHeader.gif" width="161" height="15"><br>
      <table width="525" border="0" cellspacing="0" cellpadding="3" align="center">
        <tr> 
          <td width="25%">&nbsp;</td>
          <td width="25%">&nbsp;</td>
          <td >&nbsp;</td>
        </tr>
        <tr> 
		  <cti:checkRole roleid="<%= BillingRole.ROLEID %>">
          <td width="25%" class = "MainText"> 
            <div align = "center" style = "border:solid 1px #666999;"><a href = "Metering/Billing.jsp" class = "Link1" style = "text-decoration:none;"><cti:getProperty propertyid="<%= BillingRole.HEADER_LABEL%>" defaultvalue="Billing"/></a></div>
          </td>
          </cti:checkRole>
          <cti:checkRole roleid="<%= CommercialMeteringRole.ROLEID %>"> 
          <td width="25%" class = "MainText"> 
           <div align = "center" style = "border:solid 1px #666999;"><a href = "Metering/Metering.jsp" class = "Link1" style = "text-decoration:none;">All Trends</a></div>
          </td>
          </cti:checkRole>
          <td class = "MainText"> </td>
        </tr>
      </table>
    </td>
    <td width="1" background="../WebConfig/yukon/Icons/VerticalRule.gif" height="102"></td>
  </tr>
</cti:checkMultiRole>

<cti:checkMultiRole roleid="<%= Integer.toString(DirectLoadcontrolRole.ROLEID) + ',' + Integer.toString(DirectCurtailmentRole.ROLEID) + ',' + Integer.toString(EnergyBuybackRole.ROLEID) + ',' + Integer.toString(OddsForControlRole.ROLEID) %>">
  <tr> 
    <td width="102" bgcolor="#000000" height="1"><img src="../WebConfig/yukon/Icons/VerticalRule.gif"></td>
    <td width="555" bgcolor="#000000" height="1"><img src="../WebConfig/yukon/Icons/VerticalRule.gif"></td>
    <td width="1" background="../WebConfig/yukon/Icons/VerticalRule.gif" height="1"></td>
  </tr>
  <tr> 
    <td width="102" height="102" background="../WebConfig/yukon/LoadImage.jpg">&nbsp;</td>
    <td width="555" bgcolor="#FFFFFF" height="102" valign="top">
      <div align="left"><img src="../WebConfig/yukon/LoadHeader.gif"><br>
      </div>
      <div align="left"></div>
      <table width="525" border="0" cellspacing="0" cellpadding="3" align="center">
        <tr> 
          <td width="25%">&nbsp;</td>
          <td width="25%">&nbsp;</td>
          <td width="25%">&nbsp;</td>
          <td width="25%">&nbsp;</td>
        </tr>
        <tr> 
          <!-- Changed direct control pages to use the 3 tier display instead of the 1tier (LoadControl/oper_direct.jsp) display-->
<cti:isPropertyTrue propertyid="<%= DirectLoadcontrolRole.DIRECT_CONTROL %>">
          <td width="25%" class = "MainText"> 
            <div align = "center" style = "border:solid 1px #666999;"><a href = "LoadControl/oper_direct.jsp" class = "Link1" style = "text-decoration:none;">Direct</a></div>
          </td>
</cti:isPropertyTrue>

<cti:checkRole roleid="<%= DirectCurtailmentRole.ROLEID %>"> 
          <td width="25%" class = "MainText"> 
            <div align = "center" style = "border:solid 1px #666999;"><a href = "LoadControl/oper_mand.jsp" class = "Link1" style = "text-decoration:none;"><cti:getProperty propertyid="<%= DirectCurtailmentRole.CURTAILMENT_LABEL%>"/></a></div>
          </td>
</cti:checkRole>

<cti:checkRole roleid="<%= EnergyBuybackRole.ROLEID %>"> 
          <td width="25%" class = "MainText"> 
            <div align = "center" style = "border:solid 1px #666999;"><a href = "LoadControl/oper_ee.jsp" class = "Link1" style = "text-decoration:none;"><cti:getProperty propertyid="<%= EnergyBuybackRole.ENERGY_BUYBACK_LABEL%>"/></a></div>
          </td>
</cti:checkRole>

<cti:checkRole roleid="<%= CICurtailmentRole.ROLEID %>"> 
          <td width="25%" class="MainText"> 
            <div align="center" style="border:solid 1px #666999;"><a href="/cc/programSelect.jsf" class="Link1" style="text-decoration:none;"><cti:getProperty property="com.cannontech.roles.operator.CICurtailmentRole.CURTAILMENT_LABEL"/></a></div>
          </td>
</cti:checkRole>

<cti:checkRole roleid="<%= OddsForControlRole.ROLEID %>"> 
          <td width="25%" class = "MainText"> 
            <div align = "center" style = "border:solid 1px #666999;"><a href = "Consumer/Odds.jsp" class = "Link1" style = "text-decoration:none;">Odds 
              for Control</a></div>
          </td>
</cti:checkRole>
        </tr>
        <tr>

<cti:isPropertyTrue propertyid="<%= DirectLoadcontrolRole.THREE_TIER_DIRECT %>"> 
          <td width="25%" class = "MainText"> 
            <div align = "center" style = "border:solid 1px #666999;"><a href = "../loadmgmt/controlareas.jsp" class = "Link1" style = "text-decoration:none;">3-Tier 
              Direct</a></div>
          </td>
</cti:isPropertyTrue>

          <td width="25%" class = "MainText">&nbsp;</td>
          <td width="25%" class = "MainText">&nbsp;</td>
          <td width="25%" class = "MainText">&nbsp;</td>
        </tr>
      </table>
    </td>
    <td width="1" background="../WebConfig/yukon/Icons/VerticalRule.gif" height="102"></td>
  </tr>
</cti:checkMultiRole>

<cti:checkRole roleid="<%= InventoryRole.ROLEID %>">
  <tr> 
    <td width="102" bgcolor="#000000" height="1"><img src="../WebConfig/yukon/Icons/VerticalRule.gif"></td>
    <td width="555" bgcolor="#000000" height="1"><img src="../WebConfig/yukon/Icons/VerticalRule.gif"></td>
    <td width="1" background="../WebConfig/yukon/Icons/VerticalRule.gif" height="1"></td>
  </tr>
  <tr> 
    <td width="102" height="102" background="../WebConfig/yukon/InventoryImage.jpg">&nbsp;</td>
    <td width="555" bgcolor="#FFFFFF" height="102" valign="top"><img src="../WebConfig/yukon/InventoryHeader.gif" width="148" height="15"><br>
      <table width="525" border="0" cellspacing="0" cellpadding="3" align="center">
        <tr> 
          <td width="25%" height="30" valign="bottom">&nbsp;</td>
          <td width="25%" height="30" valign="bottom">&nbsp;</td>
          <td width="43%" height="30" valign="bottom"><font face="Arial, Helvetica, sans-serif" size="1">Search 
            for existing hardware:</font></td>
          <td height="30" valign="bottom" width="7%">&nbsp;</td>
        </tr>
        <tr>
          <% if(((ArrayList)session.getAttribute(ServletUtil.FILTER_INVEN_LIST)) == null || ((ArrayList)session.getAttribute(ServletUtil.FILTER_INVEN_LIST)).size() < 1) { %>
          <td width="25%" class="MainText"> <cti:checkProperty propertyid="<%= InventoryRole.INVENTORY_SHOW_ALL %>"> 
            <div align = "center" style = "border:solid 1px #666999;"><a href = "Hardware/Filter.jsp" class = "Link1" style = "text-decoration:none;">Inventory</a></div>
            </cti:checkProperty></td>
          <% } else { %>
          <td width="25%" class="MainText"> <cti:checkProperty propertyid="<%= InventoryRole.INVENTORY_SHOW_ALL %>"> 
            <div align = "center" style = "border:solid 1px #666999;"><a href = "Hardware/Inventory.jsp" class = "Link1" style = "text-decoration:none;">Inventory</a></div>
            </cti:checkProperty></td>
          <% } %>
          <td width="25%" class="MainText"> 
            <div align = "center" style = "border:solid 1px #666999;"><a href = "Hardware/PurchaseTrack.jsp" class = "Link1" style = "text-decoration:none;">Purchasing</a></div>
          </td>
          <form name = "invSearchForm" method="post" action="<%= request.getContextPath() %>/servlet/InventoryManager">
		    <input type="hidden" name="action" value="SearchInventory">
			<input type="hidden" name="REDIRECT" value="<%= request.getContextPath() %>/operator/Hardware/ResultSet.jsp">
            <td width="43%" class="MainText" align="right" nowrap> 
              <select name="SearchBy" onchange="document.invSearchForm.SearchValue.value=''">
<%
	if (selectionListTable != null) {
		Integer lastInvOption = (Integer) session.getAttribute(ServletUtils.ATT_LAST_INVENTORY_SEARCH_OPTION);
		YukonListEntry devTypeMCT = liteEC.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_MCT);
		
		StarsCustSelectionList searchByList = (StarsCustSelectionList) selectionListTable.get(YukonSelectionListDefs.YUK_LIST_NAME_INV_SEARCH_BY);
		for (int i = 0; i < searchByList.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = searchByList.getStarsSelectionListEntry(i);
			if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_INV_SEARCH_BY_METER_NO) continue;
			if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_INV_SEARCH_BY_DEVICE_NAME && devTypeMCT == null) continue;
			String selectedStr = (lastInvOption != null && entry.getYukonDefID() == lastInvOption.intValue()) ? "selected" : "";
%>
			    <option value="<%= entry.getYukonDefID() %>" <%= selectedStr %>><%= entry.getContent() %></option>
<%
		}
	}
%>
			  </select>
<%    String lastInvValue = (String) session.getAttribute(ServletUtils.ATT_LAST_INVENTORY_SEARCH_VALUE);%>
              &nbsp;
            <input type="text" name="SearchValue" size="15" value='<%=(lastInvValue!=null?lastInvValue:"")%>'>
              &nbsp; </td>
            <td valign = "top" class="MainText" width="7%"><img class="Clickable" src="../WebConfig/yukon/Buttons/GoButton.gif" width="23" height="20" onclick = "Javascript:document.invSearchForm.submit();" > 
            </td>
          </form>
        </tr>
      </table>
    </td>
    <td width="1" background="../WebConfig/yukon/Icons/VerticalRule.gif" height="102"></td>
  </tr>
</cti:checkRole>

<cti:checkRole roleid="<%= WorkOrderRole.ROLEID %>">
  <tr> 
    <td width="102" bgcolor="#000000" height="1"><img src="../WebConfig/yukon/Icons/VerticalRule.gif"></td>
    <td width="555" bgcolor="#000000" height="1"><img src="../WebConfig/yukon/Icons/VerticalRule.gif"></td>
    <td width="1" background="../WebConfig/yukon/Icons/VerticalRule.gif" height="1"></td>
  </tr>
  <tr> 
    <td width="102" height="102" background="../WebConfig/yukon/WorkImage.jpg">&nbsp;</td>
    <td width="555" bgcolor="#FFFFFF" height="102" valign="top"><img src="../WebConfig/yukon/WorkHeader.gif" width="104" height="15"><br>
      <table width="525" border="0" cellspacing="0" cellpadding="3" align="center">
        <tr valign="bottom"> 
          <td width="25%">&nbsp;</td>
          <td width="25%">&nbsp;</td>
          <td width="43%"><font face="Arial, Helvetica, sans-serif" size="1">Search 
            for existing service order:</font></td>
          <td width="7%">&nbsp;</td>
        </tr>
        <tr> 
          <td width="25%" class = "MainText"> <cti:checkProperty propertyid="<%= WorkOrderRole.WORK_ORDER_SHOW_ALL %>"> 
            <div align = "center" style = "border:solid 1px #666999;"><a href = "WorkOrder/WOFilter.jsp" class = "Link1" style = "text-decoration:none;">Service Order List</a></div>
</cti:checkProperty>
          </td>
          <td width="25%" class = "MainText">&nbsp;</td>
          <form name = "soSearchForm" method="post" action="<%= request.getContextPath() %>/servlet/WorkOrderManager">
		    <input type="hidden" name="action" value="SearchWorkOrder">
			<input type="hidden" name="REDIRECT" value="<%= request.getContextPath() %>/operator/WorkOrder/SearchResults.jsp">
            <td width="43%" align="right" class = "MainText" nowrap> 
              <select name="SearchBy" onchange="document.soSearchForm.SearchValue.value=''">
<%
	if (selectionListTable != null) {
		Integer lastSrvcOption = (Integer) session.getAttribute(ServletUtils.ATT_LAST_SERVICE_SEARCH_OPTION);
		StarsCustSelectionList searchByList = (StarsCustSelectionList) selectionListTable.get(YukonSelectionListDefs.YUK_LIST_NAME_SO_SEARCH_BY);
		for (int i = 0; i < searchByList.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = searchByList.getStarsSelectionListEntry(i);
			String selectedStr = (lastSrvcOption != null && entry.getYukonDefID() == lastSrvcOption.intValue()) ? "selected" : "";
%>
              <option value="<%= entry.getYukonDefID() %>" <%= selectedStr %>><%= entry.getContent() %></option>
<%
		}
	}
%>
            </select>
<%          String lastSrvcValue = (String) session.getAttribute(ServletUtils.ATT_LAST_SERVICE_SEARCH_VALUE);%>
            &nbsp;
            <input type="text" name="SearchValue" size="15" value='<%=(lastSrvcValue!=null?lastSrvcValue:"")%>'>
            &nbsp; </td>
            <td width="7%" valign="top" class="MainText"><img class="Clickable" src="../WebConfig/yukon/Buttons/GoButton.gif" width="23" height="20" onClick = "Javascript:document.soSearchForm.submit();" ></td>
		  </form>
        </tr>
      </table>
    </td>
    <td width="1" background="../WebConfig/yukon/Icons/VerticalRule.gif" height="102"></td>
  </tr>
</cti:checkRole> 
<cti:checkMultiRole roleid="<%= Integer.toString(ReportingRole.ROLEID) + ',' + Integer.toString(CommanderRole.ROLEID)%>">
  <tr> 
    <td width="102" bgcolor="#000000" height="1"><img src="../WebConfig/yukon/Icons/VerticalRule.gif"></td>
    <td width="555" bgcolor="#000000" height="1"><img src="../WebConfig/yukon/Icons/VerticalRule.gif"></td>
    <td width="1" background="../WebConfig/yukon/Icons/VerticalRule.gif" height="1"></td>
  </tr>
  <tr>
    <td width="102" bgcolor="#000000" height="102" background="../WebConfig/yukon/AnalysisImage.jpg">&nbsp;</td>
    <td bgcolor="#FFFFFF" height="102" valign="top"><img src="../WebConfig/yukon/AnalysisHeader.gif" width="76" height="15"><br>
      <table width="525" border="0" cellspacing="0" cellpadding="3" align="center">
        <tr> 
          <td width="25%">&nbsp;</td>
          <td width="25%">&nbsp;</td>
          <td width="25%">&nbsp;</td>
          <td width="25%">&nbsp;</td>
        </tr>
        <tr> 
          <cti:checkRole roleid="<%= ReportingRole.ROLEID %>">
          <td align = "center" class = "MainText" width="25%">
            <div align = "center" style = "border:solid 1px #666999;"><a href = "../analysis/Reports.jsp" class = "Link1" style = "text-decoration:none;"> 
              Reporting</a></div>
          </td>
          </cti:checkRole>
          <cti:checkRole roleid="<%= CommanderRole.ROLEID %>">
          <td align = "center" class = "MainText" width="25%">
            <div align = "center" style = "border:solid 1px #666999;"><a href = "../apps/SelectDevice.jsp" class = "Link1" style = "text-decoration:none;"> 
              Commander</a></div>
          </td>
          </cti:checkRole>
        </tr>
      </table>
    </td>
    <td width="1" background="../WebConfig/yukon/Icons/VerticalRule.gif" height="16"></td>
  </tr>
</cti:checkMultiRole>
<cti:checkRole roleid="<%= AdministratorRole.ROLEID %>">
  <tr> 
    <td width="102" bgcolor="#000000" height="1"><img src="../WebConfig/yukon/Icons/VerticalRule.gif"></td>
    <td width="555" bgcolor="#000000" height="1"><img src="../WebConfig/yukon/Icons/VerticalRule.gif"></td>
    <td width="1" background="../WebConfig/yukon/Icons/VerticalRule.gif" height="1"></td>
  </tr>
  <tr>
    <td width="102" bgcolor="#000000" height="102" background="../WebConfig/yukon/AdminImage.jpg">&nbsp;</td>
    <td bgcolor="#FFFFFF" height="102" valign="top"><img src="../WebConfig/yukon/AdminHeader.gif" width="129" height="15"><br>
      <table width="525" border="0" cellspacing="0" cellpadding="3" align="center">
        <tr> 
          <td width="25%">&nbsp;</td>
          <td width="25%">&nbsp;</td>
          <td width="25%">&nbsp;</td>
          <td width="25%">&nbsp;</td>
        </tr>
        <tr>
<cti:checkProperty propertyid="<%= AdministratorRole.ADMIN_CONFIG_ENERGY_COMPANY %>"> 
          <td align = "center" class = "MainText" width="25%"> 
            <div align = "center" style = "border:solid 1px #666999;"><a href = "Admin/AdminTest.jsp" class = "Link1" style = "text-decoration:none;">Config 
              Energy Company</a></div>
          </td>
</cti:checkProperty>
<cti:checkProperty propertyid="<%= AdministratorRole.ADMIN_MANAGE_MEMBERS %>"> 
          <td align = "center" class = "MainText" width="25%"> 
            <div align = "center" style = "border:solid 1px #666999;"><a href = "Admin/ManageMembers.jsp" class = "Link1" style = "text-decoration:none;"> 
              Member <br/> Management</a></div>
          </td>
</cti:checkProperty>
<cti:checkProperty propertyid="<%= AdministratorRole.ADMIN_CREATE_ENERGY_COMPANY %>"> 
          <td align = "center" class = "MainText" width="25%"> 
            <div align = "center" style = "border:solid 1px #666999;"><a href = "Admin/NewEnergyCompany.jsp" class = "Link1" style = "text-decoration:none;">New 
              Energy <br/> Company</a></div>
          </td>
</cti:checkProperty> 
<cti:checkProperty propertyid="<%= AdministratorRole.ADMIN_DELETE_ENERGY_COMPANY %>"> 
          <form name="DeleteForm" method="post" action="<%= request.getContextPath() %>/servlet/StarsAdmin">
            <input type="hidden" name="action" value="DeleteEnergyCompany">
            <input type="hidden" name="REDIRECT" value="<%= request.getContextPath() %>/servlet/LoginController?ACTION=LOGOUT">
            <td align = "center" class="MainText" width="25%"> 
              <div align = "center" style = "border:solid 1px #666999;"><span class="Clickable" style="text-decoration:none;" onClick="confirmDelete()">Delete 
                Energy Company</span></div>
            </td>
          </form>
</cti:checkProperty>
        </tr>
        <tr>
<cti:checkProperty propertyid="<%= AdministratorRole.ADMIN_VIEW_BATCH_COMMANDS %>"> 
          <td align = "center" class = "MainText" width="25%"> 
            <div align = "center" style = "border:solid 1px #666999;"><a href = "Admin/SwitchCommands.jsp" class = "Link1" style = "text-decoration:none;">View 
              Batch Commands</a></div>
          </td>
</cti:checkProperty>
<cti:checkProperty propertyid="<%= AdministratorRole.ADMIN_VIEW_OPT_OUT_EVENTS %>"> 
          <td align = "center" class = "MainText" width="25%"> 
            <div align = "center" style = "border:solid 1px #666999;"><a href = "Admin/OptOutEvents.jsp" class = "Link1" style = "text-decoration:none;">View 
              Scheduled <cti:getProperty propertyid="<%= ConsumerInfoRole.WEB_TEXT_OPT_OUT_NOUN %>" defaultvalue="opt out" format="all_capital"/> 
              Events</a></div>
          </td>
</cti:checkProperty> 
        </tr>
      </table>
    </td>
    <td width="1" background="../WebConfig/yukon/Icons/VerticalRule.gif" height="16"></td>
  </tr>
</cti:checkRole>
  </table>
<div align="center"></div>
</body>

</html>
