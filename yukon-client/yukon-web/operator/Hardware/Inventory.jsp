<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.stars.web.bean.InventoryBean" %>

<jsp:useBean id="inventoryBean" class="com.cannontech.stars.web.bean.InventoryBean" scope="session"/>
<jsp:setProperty name="inventoryBean" property="energyCompanyID" value="<%= user.getEnergyCompanyID() %>"/>

<% if (request.getParameter("page") == null) { %>
	<%-- intialize bean properties --%>
	<jsp:setProperty name="inventoryBean" property="sortBy" value="<%= YukonListEntryTypes.YUK_DEF_ID_INV_SORT_BY_SERIAL_NO %>"/>
	<jsp:setProperty name="inventoryBean" property="sortOrder" value="<%= InventoryBean.SORT_ORDER_ASCENDING %>"/>
	<jsp:setProperty name="inventoryBean" property="filterBy" value="0"/>
	<jsp:setProperty name="inventoryBean" property="member" value="<%= user.getEnergyCompanyID() %>"/>
	<jsp:setProperty name="inventoryBean" property="page" value="1"/>
	<% inventoryBean.resetInventoryList(); %>
<% } %>

<%-- Grab the search criteria --%>
<jsp:setProperty name="inventoryBean" property="sortBy" param="SortBy"/>
<jsp:setProperty name="inventoryBean" property="sortOrder" param="SortOrder"/>
<jsp:setProperty name="inventoryBean" property="filterBy" param="FilterBy"/>
<jsp:setProperty name="inventoryBean" property="deviceType" param="DeviceType"/>
<jsp:setProperty name="inventoryBean" property="serviceCompany" param="ServiceCompany"/>
<jsp:setProperty name="inventoryBean" property="location" param="Location"/>
<jsp:setProperty name="inventoryBean" property="addressingGroup" param="AddressingGroup"/>
<jsp:setProperty name="inventoryBean" property="deviceStatus" param="DeviceStatus"/>
<jsp:setProperty name="inventoryBean" property="page" param="page"/>
<jsp:setProperty name="inventoryBean" property="member" param="Member"/>

<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
<script language="JavaScript">
function changeFilter(filterBy) {
	document.getElementById("DivDeviceType").style.display = (filterBy == <%= YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_DEV_TYPE %>)? "" : "none";
	document.getElementById("DivServiceCompany").style.display = (filterBy == <%= YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_SRV_COMPANY %>)? "" : "none";
	document.getElementById("DivLocation").style.display = (filterBy == <%= YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_LOCATION %>)? "" : "none";
	document.getElementById("DivAddressingGroup").style.display = (filterBy == <%= YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_CONFIG %>)? "" : "none";
	document.getElementById("DivDeviceStatus").style.display = (filterBy == <%= YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_DEV_STATUS %>)? "" : "none";
	document.getElementById("DivEnergyCompany").style.display = (filterBy == <%= YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_ENERGY_COMPANY %>)? "" : "none";
}

function init() {
	var form = document.MForm;
	changeFilter(form.FilterBy.value);
}

function showAll(form) {
	form.FilterBy.value = 0;
	form.submit();
}
</script>
</head>

<body class="Background" leftmargin="0" topmargin="0" onload="init()">
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
          <td  valign="top" width="101"> 
            <% String pageName = "Inventory.jsp"; %>
            <%@ include file="include/Nav.jsp" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"> 
              <% String header = "INVENTORY"; %>
              <%@ include file="include/SearchBar.jsp" %>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              <form name="MForm" method="post" action="" onsubmit="setFilterValue(this)">
			    <input type="hidden" name="page" value="1">
                <table width="80%" border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td width="75%"> 
                      <table width="100%" border="0" cellspacing="0" cellpadding="3" class="TableCell">
                        <tr> 
                          <td width="15%"> 
                            <div align="right">Sort by:</div>
                          </td>
                          <td width="35%"> 
                            <select name="SortBy">
                              <%
	StarsCustSelectionList sortByList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_INV_SORT_BY );
	for (int i = 0; i < sortByList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = sortByList.getStarsSelectionListEntry(i);
		String selected = (entry.getYukonDefID() == inventoryBean.getSortBy())? "selected" : "";
%>
                              <option value="<%= entry.getYukonDefID() %>" <%= selected %>><%= entry.getContent() %></option>
                              <%
	}
%>
                            </select>
                          </td>
                          <td width="50%"> 
                            <select name="SortOrder">
                              <option value="<%= InventoryBean.SORT_ORDER_ASCENDING %>" <% if (inventoryBean.getSortOrder() == InventoryBean.SORT_ORDER_ASCENDING) out.print("selected"); %>>Ascending</option>
                              <option value="<%= InventoryBean.SORT_ORDER_DESCENDING %>" <% if (inventoryBean.getSortOrder() == InventoryBean.SORT_ORDER_DESCENDING) out.print("selected"); %>>Descending</option>
                            </select>
                          </td>
                        </tr>
                        <tr valign="top"> 
                          <td width="15%"> 
                            <div align="right">Filter by:</div>
                          </td>
                          <td width="35%"> 
                            <select name="FilterBy" onChange="changeFilter(this.value)">
                              <option value="0">(none)</option>
<%
	StarsCustSelectionList filterByList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_INV_FILTER_BY );
	for (int i = 0; i < filterByList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = filterByList.getStarsSelectionListEntry(i);
		String selected = (entry.getYukonDefID() == inventoryBean.getFilterBy())? "selected" : "";
%>
                              <option value="<%= entry.getYukonDefID() %>" <%= selected %>><%= entry.getContent() %></option>
<%
	}
	if (AuthFuncs.checkRoleProperty(lYukonUser, AdministratorRole.ADMIN_MANAGE_MEMBERS) && (liteEC.getChildren().size() > 0)) {
		String selected = (inventoryBean.getFilterBy() == YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_ENERGY_COMPANY)? "selected" : "";
%>
                              <option value="<%= YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_ENERGY_COMPANY %>" <%= selected %>>Energy Company</option>
<%
	}
%>
                            </select>
                          </td>
                          <td width="50%"> 
                            <div id="DivDeviceType" style="display:none"> 
                              <select name="DeviceType">
                                <%
	StarsCustSelectionList deviceTypeList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE );
	for (int i = 0; i < deviceTypeList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = deviceTypeList.getStarsSelectionListEntry(i);
		String selected = (entry.getEntryID() == inventoryBean.getDeviceType())? "selected" : "";
%>
                                <option value="<%= entry.getEntryID() %>" <%= selected %>><%= entry.getContent() %></option>
                                <%
	}
%>
                              </select>
                            </div>
                            <div id="DivServiceCompany" style="display:none"> 
                              <select name="ServiceCompany">
                                <%
	for (int i = 0; i < companies.getStarsServiceCompanyCount(); i++) {
		StarsServiceCompany company = companies.getStarsServiceCompany(i);
		String selected = (company.getCompanyID() == inventoryBean.getServiceCompany())? "selected" : "";
%>
                                <option value="<%= company.getCompanyID() %>" <%= selected %>><%= company.getCompanyName() %></option>
                                <%
	}
%>
                              </select>
                            </div>
                            <div id="DivLocation" style="display:none"> 
                              <select name="Location">
                                <option value="<%= InventoryBean.INV_LOCATION_WAREHOUSE %>" <% if (inventoryBean.getLocation() == InventoryBean.INV_LOCATION_WAREHOUSE) out.print("selected"); %>>Warehouse</option>
                                <option value="<%= InventoryBean.INV_LOCATION_RESIDENCE %>" <% if (inventoryBean.getLocation() == InventoryBean.INV_LOCATION_RESIDENCE) out.print("selected"); %>>Residence</option>
                              </select>
                            </div>
                            <div id="DivAddressingGroup" style="display:none"> 
                              <select name="AddressingGroup">
                                <option value="0">(none)</option>
                                <%
	TreeSet sortedGroups = new TreeSet(new Comparator() {
			public int compare(Object o1, Object o2) {
				AddressingGroup g1 = (AddressingGroup) o1;
				AddressingGroup g2 = (AddressingGroup) o2;
				int rslt = g1.getContent().compareToIgnoreCase( g2.getContent() );
				if (rslt == 0) rslt = g1.getEntryID() - g2.getEntryID();
				return rslt;
			}
			});
	for (int i = 0; i < categories.getStarsApplianceCategoryCount(); i++) {
		StarsApplianceCategory category = categories.getStarsApplianceCategory(i);
		for (int j = 0; j < category.getStarsEnrLMProgramCount(); j++) {
			StarsEnrLMProgram program = category.getStarsEnrLMProgram(j);
			for (int k = 0; k < program.getAddressingGroupCount(); k++) {
				AddressingGroup group = program.getAddressingGroup(k);
				sortedGroups.add( group );
			}
		}
	}
	Iterator it = sortedGroups.iterator();
	while (it.hasNext()) {
		AddressingGroup group = (AddressingGroup) it.next();
		String selected = (inventoryBean.getAddressingGroup() == group.getEntryID())? "selected" : "";
%>
                                <option value="<%= group.getEntryID() %>" <%= selected %>><%= group.getContent() %></option>
                                <%
	}
%>
                              </select>
                            </div>
                            <div id="DivDeviceStatus" style="display:none"> 
                              <select name="DeviceStatus">
                                <%
	StarsCustSelectionList deviceStatList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_STATUS );
	for (int i = 0; i < deviceStatList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = deviceStatList.getStarsSelectionListEntry(i);
		String selected = (entry.getYukonDefID() == inventoryBean.getDeviceStatus())? "selected" : "";
%>
                                <option value="<%= entry.getYukonDefID() %>" <%= selected %>><%= entry.getContent() %></option>
                                <%
	}
%>
                              </select>
                            </div>
                            <div id="DivEnergyCompany" style="display:none"> 
                              <select name="Member">
                                <%
	ArrayList descendants = com.cannontech.stars.util.ECUtils.getAllDescendants(liteEC);
	for (int i = 0; i < descendants.size(); i++) {
		LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) descendants.get(i);
		String selected = (company.getLiteID() == inventoryBean.getMember())? "selected" : "";
%>
                                <option value="<%= company.getLiteID() %>" <%= selected %>><%= company.getName() %></option>
                                <%
	}
%>
                              </select>
                            </div>
                          </td>
                        </tr>
                      </table>
                    </td>
                    <td width="25%"> 
                      <input type="submit" name="Submit" value="Show">
                      <% if (inventoryBean.getFilterBy() != 0) { %>
                      <input type="button" name="ShowAll" value="Show All" onClick="showAll(this.form)">
                      <%	} %>
                    </td>
                  </tr>
                </table>
              </form>
			  <table width="80%" border="0" cellspacing="0" cellpadding="0" class="MainText">
                <tr>
                  <td>Click on a serial # (device name) to view the hardware details, 
                    or click on an account # (if available) to view the account 
                    information.</td>
                </tr>
              </table>
			  <br>
              <%= inventoryBean.getHTML(request) %> 
              <!--              <table width='80%' border='0' cellspacing='0' cellpadding='3' class='TableCell'>
                <tr>
                  <td>1-4 of 4 | <font color='#CCCCCC'>First</font> | <font color='#CCCCCC'>Previous</font> 
                    | <font color='#CCCCCC'>Next</font> | <font color='#CCCCCC'>Last</font></td>
                </tr>
                <tr>
                  <td> 
                    <table width='100%' border='1' cellspacing='0' cellpadding='3'>
                      <tr> 
                        <td class='HeaderCell' width='17%'>Serial # / Device Name</td>
                        <td class='HeaderCell' width='17%'>Device Type</td>
                        <td class='HeaderCell' width='17%'>Install Date</td>
                        <td class='HeaderCell' width='49%'>Location</td>
                      </tr>
                      <tr> 
                        <td class='TableCell' width='17%'><a href='InventoryDetail.jsp?InvId=7'>500000000</a></td>
                        <td class='TableCell' width='17%'>LCR-5000</td>
                        <td class='TableCell' width='17%'>08/24/2003</td>
                        <td class='TableCell' width='49%'><a href='' onclick='selectAccount(7); return false;'>Acct 
                          # 12345</a> Robert Livingston, 8301 Golden Valley Rd, Suite 300...</td>
                      </tr>
                    </table>
                  </td>
                </tr>
                <tr>
                  <td>1-4 of 4 | <font color='#CCCCCC'>First</font> | <font color='#CCCCCC'>Previous</font> 
                    | <font color='#CCCCCC'>Next</font> | <font color='#CCCCCC'>Last</font></td>
                </tr>
              </table>
			  <form name='cusForm' method='post' action='/servlet/SOAPClient'>
                <input type='hidden' name='action' value='GetCustAccount'>
                <input type='hidden' name='AccountID' value=''>
                <input type='hidden' name='REDIRECT' value='/operator/Consumer/Update.jsp'>
                <input type='hidden' name='REFERRER' value='/operator/Hardware/Inventory.jsp'>
			  </form>
<script language='JavaScript'>
function selectAccount(accountID) {
  var form = document.cusForm;
  form.AccountID.value = accountID;
  form.submit();
}
</script>-->
              <p>&nbsp; </p>
            </div>
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
