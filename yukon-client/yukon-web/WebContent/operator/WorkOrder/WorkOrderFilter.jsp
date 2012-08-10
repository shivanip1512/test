<html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=EDGE" />
<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.stars.web.bean.WorkOrderBean" %>
<jsp:useBean id="soBean" class="com.cannontech.stars.web.bean.WorkOrderBean" scope="session">
	<%-- this body is executed only if the bean is created --%>
	<jsp:setProperty name="soBean" property="energyCompanyID" value="<%= user.getEnergyCompanyID() %>"/>
	<jsp:setProperty name="soBean" property="sortBy" value="<%= YukonListEntryTypes.YUK_DEF_ID_SO_SORT_BY_ORDER_NO %>"/>
	<jsp:setProperty name="soBean" property="sortOrder" value="<%= WorkOrderBean.SORT_ORDER_DESCENDING %>"/>
	<jsp:setProperty name="soBean" property="filterBy" value="0"/>
	<jsp:setProperty name="soBean" property="serviceStatus" value="0"/>
	<jsp:setProperty name="soBean" property="page" value="1"/>
</jsp:useBean>

<%-- Grab the search criteria --%>
<jsp:setProperty name="soBean" property="sortBy" param="SortBy"/>
<jsp:setProperty name="soBean" property="sortOrder" param="SortOrder"/>
<jsp:setProperty name="soBean" property="filterBy" param="FilterBy"/>
<jsp:setProperty name="soBean" property="serviceStatus" param="ServiceStatus"/>
<jsp:setProperty name="soBean" property="serviceType" param="ServiceType"/>
<jsp:setProperty name="soBean" property="serviceCompany" param="ServiceCompany"/>
<jsp:setProperty name="soBean" property="page" param="page"/>

<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
<script language="JavaScript">
function changeFilter(filterBy) {
	document.getElementById("DivServiceType").style.display = (filterBy == <%= YukonListEntryTypes.YUK_DEF_ID_SO_FILTER_BY_SRV_TYPE %>)? "" : "none";
	document.getElementById("DivServiceCompany").style.display = (filterBy == <%= YukonListEntryTypes.YUK_DEF_ID_SO_FILTER_BY_SRV_COMPANY %>)? "" : "none";
}

function init() {
	var form = document.MForm;
	changeFilter(form.FilterBy.value);
}

function showAll(form) {
	form.FilterBy.value = 0;
	form.ServiceStatus.value = 0;
	form.submit();
}
</script>
</head>

<body class="Background" leftmargin="0" topmargin="0" onload="init()">
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
          <td  valign="top" width="101">
            <% String pageName = "WorkOrderFilter.jsp"; %>
            <%@ include file="include/Nav.jspf" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"> <b> 
              <% String header = "SERVICE ORDER LIST"; %>
              <%@ include file="include/SearchBar.jspf" %>
              </b> 
              <form name="MForm" method="post" action="" onSubmit="setFilterValue(this)">

	    	<table width="600" border="1" cellspacing="0" cellpadding="0" align="center">
	        	<tr> 
	              	<td width="55" class="HeaderCell">Available</td>
	               	<td width="85"> 
	                	<div align="right">Filter by:</div>
	                </td>
	                <td width="200"> 
	                  <select name="FilterType" size="1" style="width: 200px" onChange="changeFilterType(this.value)" >
                      <%
						StarsCustSelectionList sortByList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_SO_SORT_BY );
							for (int i = 0; i < sortByList.getStarsSelectionListEntryCount(); i++) {
								StarsSelectionListEntry entry = sortByList.getStarsSelectionListEntry(i);
								String selected = (entry.getYukonDefID() == soBean.getSortBy())? "selected" : "";
					  %>
                        <option value="<%= entry.getYukonDefID() %>" <%= selected %>><%= entry.getContent() %></option>
					  <% } %>
					  </select>
                    </td>
	            	<td width="240"> 
	                	<div id='<c:out value="${filterDeviceType}"/>'> 
	                    	<select id='<c:out value="${filterDeviceType}"/>1' name='<c:out value="${filterDeviceType}"/>1' size="1" style="width: 200px" onChange="selectFilter(this.value)">
	                            <c:forEach var="deviceTypeEntry" items="${filterBean.availableDeviceTypes.yukonListEntries}">
									<option value='<c:out value="${deviceTypeEntry.entryID}"/>'> <c:out value="${deviceTypeEntry.entryText}"/> </option>
								</c:forEach>
	                      	</select>
	                    </div>
	                    <div id='<c:out value="${filterServiceCompany}"/>' style="display:none" > 
	                    	<select id='<c:out value="${filterServiceCompany}"/>1' name='<c:out value="${filterServiceCompany}"/>1' size="1" style="width: 200px" onChange="selectFilter(this.value)">
	                            <c:forEach var="serviceCompany" items="${filterBean.availableServiceCompanies}">
									<option value='<c:out value="${serviceCompany.liteID}"/>'> <c:out value="${serviceCompany.companyName}"/> </option>
								</c:forEach>
	                      	</select>
	                    </div>
	                    <div id='<c:out value="${filterDeviceStatus}"/>' style="display:none"> 
	                    	<select id='<c:out value="${filterDeviceStatus}"/>1' name='<c:out value="${filterDeviceStatus}"/>1' size="1" style="width: 200px" onChange="selectFilter(this.value)">
	                            <c:forEach var="deviceStatusEntry" items="${filterBean.availableDeviceStates.yukonListEntries}">
									<option value='<c:out value="${deviceStatusEntry.entryID}"/>'> <c:out value="${deviceStatusEntry.entryText}"/> </option>
								</c:forEach>
	                      	</select>
	                    </div>
	                    <div id='<c:out value="${filterDeviceLocation}"/>' style="display:none"> 
	                    	<select id='<c:out value="${filterDeviceLocation}"/>1' name='<c:out value="${filterDeviceLocation}"/>1' size="1" style="width: 200px" onChange="selectFilter(this.value)">
	                            <option value="0"> <c:out value="(none)"/> </option>
							</select>
	                    </div>
	                    <div id='<c:out value="${filterDeviceConfig}"/>' style="display:none"> 
	                    	<select id='<c:out value="${filterDeviceConfig}"/>1' name='<c:out value="${filterDeviceConfig}"/>1' size="1" style="width: 200px" onChange="selectFilter(this.value)">
	                            <option value="0"> <c:out value="(none)"/> </option>
							</select>
	                    </div>
	                    <div id='<c:out value="${filterDeviceMember}"/>' style="display:none"> 
	                    	<select id='<c:out value="${filterDeviceMember}"/>1' name='<c:out value="${filterDeviceMember}"/>1' size="1" style="width: 200px" onChange="selectFilter(this.value)">
	                            <option value="0"> <c:out value="(none)"/> </option>
							</select>
	                    </div>
	                   	<div id='<c:out value="${filterDeviceWarehouse}"/>' style="display:none"> 
	                    	<select id='<c:out value="${filterDeviceWarehouse}"/>1' name='<c:out value="${filterDeviceWarehouse}"/>1' size="1" style="width: 200px" onChange="selectFilter(this.value)">
	                            <option value="0"> <c:out value="(none)"/> </option>
							</select>
	                    </div>
	                    <div id='<c:out value="${filterDeviceZipCode}"/>' style="display:none"> 
	                    	<select id='<c:out value="${filterDeviceZipCode}"/>1' name='<c:out value="${filterDeviceZipCode}"/>1' size="1" style="width: 200px" onChange="selectFilter(this.value)">
	                            <option value="0"> <c:out value="(none)"/> </option>
							</select>
	                    </div>
	                </td>
	           	</tr>
			</table>
			<br>
			<div align="center"> 
            	<input type="button" name="Save" value="Assign Selected Filter" onclick="saveEntry(this.form)">
            </div>
			<br>
			<table width="600" border="1" cellspacing="0" cellpadding="0" align="center">
	        	<tr> 
	              	<td width="55" class="HeaderCell">Assigned</td>
	                <td width="285"> 
	                	<select name="AssignedFilters" size="7" style="width: 293px">
	                    	<c:forEach var="existingFilter" items="${filterBean.assignedFilters}">
								<option value='<c:out value="${existingFilter.filterID}"/>'><c:out value="${existingFilter.filterText}"/></option>
							</c:forEach>
	                    </select>
	            	</td>
	            	<td width="240"> 
                        <input type="button" name="Remove" value="Remove" style="width:80" onclick="deleteEntry(this.form)">
                        <br>
                        <input type="button" name="RemoveAll" value="Remove All" style="width:80" onclick="deleteAllEntries(this.form)">
	                </td>
	           	</tr>
        	</table>
        	<br>
        	<table width="600" border="0" cellspacing="0" cellpadding="5" align="center">
            	<tr>
                	<td width="290" align="right"> 
                    	<input type="submit" name="Submit" value="Submit To Inventory">
                  	</td>
                  	<td width="205"> 
                    	<input type="reset" name="Reset" value="Reset" onclick="location.reload()">
                  	</td>
                  	<td width="75" align="right"> 
                    	<input type="button" name="Back" value="Back" onclick="if (warnUnsavedChanges()) location.href='Inventory.jsp'">
                  	</td>
              	</tr>
        	</table>
        	<br>              
              
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
for (int i = 0; i < sortByList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = sortByList.getStarsSelectionListEntry(i);
		String selected = (entry.getYukonDefID() == soBean.getSortBy())? "selected" : "";
%>
                              <option value="<%= entry.getYukonDefID() %>" <%= selected %>><%= entry.getContent() %></option>
                              <%
	}
%>
                            </select>
                          </td>
                          <td width="50%"> 
                            <select name="SortOrder">
                              <option value="<%= WorkOrderBean.SORT_ORDER_ASCENDING %>" <% if (soBean.getSortOrder() == WorkOrderBean.SORT_ORDER_ASCENDING) out.print("selected"); %>>Ascending</option>
                              <option value="<%= WorkOrderBean.SORT_ORDER_DESCENDING %>" <% if (soBean.getSortOrder() == WorkOrderBean.SORT_ORDER_DESCENDING) out.print("selected"); %>>Descending</option>
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
	StarsCustSelectionList filterByList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_SO_FILTER_BY );
	for (int i = 0; i < filterByList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = filterByList.getStarsSelectionListEntry(i);
		String selected = (entry.getYukonDefID() == soBean.getFilterBy())? "selected" : "";
%>
                              <option value="<%= entry.getYukonDefID() %>" <%= selected %>><%= entry.getContent() %></option>
                              <%
	}
%>
                            </select>
                          </td>
                          <td width="50%"> 
                            <div id="DivServiceType" style="display:none"> 
                              <select name="ServiceType">
                                <%
	StarsCustSelectionList serviceTypeList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_SERVICE_TYPE );
	for (int i = 0; i < serviceTypeList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = serviceTypeList.getStarsSelectionListEntry(i);
		String selected = (entry.getEntryID() == soBean.getServiceType())? "selected" : "";
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
		String selected = (company.getCompanyID() == soBean.getServiceCompany())? "selected" : "";
%>
                                <option value="<%= company.getCompanyID() %>" <%= selected %>><%= company.getCompanyName() %></option>
                                <%
	}
%>
                              </select>
                            </div>
                          </td>
                        </tr>
                        <tr valign="top"> 
                          <td width="15%">
                            <div align="right">Status:</div>
                          </td>
                          <td width="35%"> 
                            <select name="ServiceStatus">
                              <option value="0">All</option>
                              <%
	StarsCustSelectionList statusList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_SERVICE_STATUS );
	for (int i = 0; i < statusList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = statusList.getStarsSelectionListEntry(i);
		String selected = (entry.getEntryID() == soBean.getServiceStatus())? "selected" : "";
%>
                              <option value="<%= entry.getEntryID() %>" <%= selected %>><%= entry.getContent() %></option>
                              <%
	}
%>
                            </select>
                          </td>
                          <td width="50%">&nbsp;</td>
						</tr>
                      </table>
                    </td>
                    <td width="25%"> 
                      <input type="submit" name="Submit" value="Show">
                      <% if (soBean.getFilterBy() != 0 || soBean.getServiceStatus() != 0) { %>
                      <input type="button" name="ShowAll" value="Show All" onClick="showAll(this.form)">
                      <%	} %>
                    </td>
                  </tr>
                </table>
              </form>
              <span class="MainText">Click on a Order # to view the service order 
              details.</span><br>
			  <%= soBean.getHTML(request) %>
              <!--<table border="1" cellspacing="0" cellpadding="3" width="95%">
                <tr> 
                  <td  class="HeaderCell" width="16%" >Service Order #</td>
                  <td  class="HeaderCell" width="10%" >Date/Time</td>
                  <td  class="HeaderCell" width="8%" >Type</td>
                  <td  class="HeaderCell" width="9%" >Status</td>
                  <td  class="HeaderCell" width="8%" >By Who</td>
                  <td  class="HeaderCell" width="10%" >Assigned</td>
                  <td  class="HeaderCell" width="39%" >Description</td>
                </tr>
                <tr> 
                  <td class="TableCell" width="16%" ><a href="WorkOrder.jsp" class="Link1">12345</a></td>
                  <td class="TableCell" width="10%" >05/06/02</td>
                  <td class="TableCell" width="8%" >Install</td>
                  <td class="TableCell" width="9%" >Complete</td>
                  <td class="TableCell" width="8%" >eah</td>
                  <td class="TableCell" width="10%" >XYZ Company</td>
                  <td class="TableCell" width="39%"> 
                    <textarea name="textarea" rows="2" wrap="soft" cols="40" class = "TableCell"></textarea>
                  </td>
                </tr>
              </table>-->
              <p>&nbsp;</p>
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
