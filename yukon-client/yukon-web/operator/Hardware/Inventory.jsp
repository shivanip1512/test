<%@ include file="../Consumer/StarsHeader.jsp" %>
<%@ page import="com.cannontech.stars.web.InventoryBean" %>
<jsp:useBean id="inventoryBean" class="com.cannontech.stars.web.InventoryBean" scope="session">
	<%-- this body is executed only if the bean is created --%>
	<jsp:setProperty name="inventoryBean" property="energyCompanyID" value="<%= user.getEnergyCompanyID() %>"/>
	<jsp:setProperty name="inventoryBean" property="sortBy" value="<%= YukonListEntryTypes.YUK_DEF_ID_INV_SORT_BY_SERIAL_NO %>"/>
	<jsp:setProperty name="inventoryBean" property="filterBy" value="0"/>
	<%-- intialize bean properties --%>
</jsp:useBean>

<%-- Grab the search criteria --%>
<jsp:setProperty name="inventoryBean" property="sortBy" param="SortBy"/>
<jsp:setProperty name="inventoryBean" property="sortOrder" param="SortOrder"/>
<jsp:setProperty name="inventoryBean" property="filterBy" param="FilterBy"/>
<jsp:setProperty name="inventoryBean" property="deviceType" param="DeviceType"/>
<jsp:setProperty name="inventoryBean" property="serviceCompany" param="ServiceCompany"/>
<jsp:setProperty name="inventoryBean" property="location" param="Location"/>
<jsp:setProperty name="inventoryBean" property="addressingGroup" param="AddressingGroup"/>
<jsp:setProperty name="inventoryBean" property="page" param="page"/>

<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
<script language="JavaScript">
function changeFilter(filterBy) {
	document.getElementById("DivDeviceType").style.display = (filterBy == <%= YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_DEV_TYPE %>)? "" : "none";
	document.getElementById("DivServiceCompany").style.display = (filterBy == <%= YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_SRV_COMPANY %>)? "" : "none";
	document.getElementById("DivLocation").style.display = (filterBy == <%= YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_LOCATION %>)? "" : "none";
	document.getElementById("DivAddressingGroup").style.display = (filterBy == <%= YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_CONFIG %>)? "" : "none";
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
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="InventoryImage.jpg">&nbsp;</td>
          <td valign="bottom" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>"/>">&nbsp;</td>
              </tr>
              <tr> 
                <td width="310" height="28" class="PageHeader">&nbsp;&nbsp;&nbsp;Hardware 
                  Inventory </td>
                <td width="235" height = "30" valign="middle">&nbsp;</td>
                <form method="post" action="../Operations.jsp">
                  <td width="58" valign="middle"> 
                    <div align="center"><span class="Main"><a href="../Operations.jsp" class="Link3">Home</a></span></div>
                  </td>
                  <td width="57" valign="middle"> 
                    <div align="left"><span class="Main"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log 
                      Off</a>&nbsp;</span></div>
                  </td>
                </form>
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
          <td  valign="top" width="101"> 
            <% String pageName = "Inventory.jsp"; %>
            <%@ include file="Nav.jsp" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center">
              <% String header = "INVENTORY"; %>
              <%@ include file="SearchBar.jsp" %>
			  <form name="MForm" method="post" action="<%= pageName %>" onsubmit="setFilterValue(this)">
			    <input type="hidden" name="page" value="1">
                <table width="80%" border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td width="85%">
                      <table width="100%" border="0" cellspacing="0" cellpadding="3" class="TableCell">
                        <tr> 
                          <td width="14%"> 
                            <div align="right">Sort by:</div>
                          </td>
                          <td width="18%"> 
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
                          <td width="68%">
                            <select name="SortOrder">
                              <option value="<%= InventoryBean.SORT_ORDER_ASCENDING %>" <% if (inventoryBean.getSortOrder() == InventoryBean.SORT_ORDER_ASCENDING) out.print("selected"); %>>Ascending</option>
                              <option value="<%= InventoryBean.SORT_ORDER_DESCENDING %>" <% if (inventoryBean.getSortOrder() == InventoryBean.SORT_ORDER_DESCENDING) out.print("selected"); %>>Descending</option>
                            </select>
                          </td>
                        </tr>
                        <tr valign="top"> 
                          <td width="14%"> 
                            <div align="right">Filter by:</div>
                          </td>
                          <td width="18%"> 
                            <select name="FilterBy" onchange="changeFilter(this.value)">
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
%>
                            </select>
                          </td>
                          <td width="68%"> 
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
                          </td>
                        </tr>
                        <tr valign="top"> 
                          <td width="14%">&nbsp;</td>
                          <td colspan="2"> 
                            <input type="submit" name="Submit" value="Show">
<% if (inventoryBean.getFilterBy() != 0) { %>
                            <input type="button" name="ShowAll" value="Show All" onclick="showAll(this.form)">
<%	} %>
                          </td>
                        </tr>
                      </table>
                    </td>
                    </tr>
                </table>
              </form>
              <table width="80%" border="0" cellspacing="0" cellpadding="0">
                <tr>
                  <td>
                    <hr>
                  </td>
                </tr>
              </table>
              <%= inventoryBean.getHTML() %>
<!--              <table width='80%' border='0' cellspacing='0' cellpadding='3' class='TableCell'>
                <tr>
                  <td>1-4 of 4 | <font color='#CCCCCC'>First</font> | <font color='#CCCCCC'>Previous</font> 
                    | <font color='#CCCCCC'>Next</font> | <font color='#CCCCCC'>Last</font></td>
                </tr>
                <tr>
                  <td> 
                    <table width='100%' border='1' cellspacing='0' cellpadding='3'>
                      <tr> 
                        <td class='HeaderCell' width='17%'>Serial #</td>
                        <td class='HeaderCell' width='17%'>Device Type</td>
                        <td class='HeaderCell' width='17%'>Install Date</td>
                        <td class='HeaderCell' width='49%'>Location</td>
                      </tr>
                      <tr> 
                        <td class='TableCell' width='17%'><a href='InventoryDetail.jsp'>500000000</a></td>
                        <td class='TableCell' width='17%'>LCR-5000</td>
                        <td class='TableCell' width='17%'>08/24/2003</td>
                        <td class='TableCell' width='49%'>Acct #12345 (8301 Golden 
                          Valley Rd, Suite 300...)</td>
                      </tr>
                    </table>
                  </td>
                </tr>
                <tr>
                  <td>1-4 of 4 | <font color='#CCCCCC'>First</font> | <font color='#CCCCCC'>Previous</font> 
                    | <font color='#CCCCCC'>Next</font> | <font color='#CCCCCC'>Last</font></td>
                </tr>
              </table>-->
              <p>&nbsp; </p>
            </div>
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
