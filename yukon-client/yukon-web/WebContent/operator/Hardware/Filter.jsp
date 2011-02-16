<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.web.navigation.CtiNavObject" %>
<jsp:useBean id="filterBean" class="com.cannontech.stars.web.bean.FilterBean" scope="page"/>
<jsp:useBean id="inventoryBean" class="com.cannontech.stars.web.bean.InventoryBean" scope="session"/>

<cti:standardPage title="Energy Services Operations Center" module="stars" htmlLevel="quirks">
	
	<!-- FILTER TYPES (will be accessible with JSTL tags after they are declared) -->
 	<%pageContext.setAttribute("filterDeviceType", new Integer(YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_DEV_TYPE).toString());%>
 	<%pageContext.setAttribute("filterServiceCompany", new Integer(YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_SRV_COMPANY).toString());%>
 	<%pageContext.setAttribute("filterDeviceStatus", new Integer(YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_DEV_STATUS).toString());%>
 	<%pageContext.setAttribute("filterApplianceType", new Integer(YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_APPLIANCE_TYPE).toString());%>
 	<%pageContext.setAttribute("filterDeviceMember", new Integer(YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_MEMBER).toString());%>
 	<%pageContext.setAttribute("filterDeviceWarehouse", new Integer(YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_WAREHOUSE).toString());%>
 	<%pageContext.setAttribute("filterDeviceSerialRangeMin", new Integer(YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_SERIAL_RANGE_MIN).toString());%>
 	<%pageContext.setAttribute("filterDeviceSerialRangeMax", new Integer(YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_SERIAL_RANGE_MAX).toString());%>
	<%pageContext.setAttribute("filterDevicePostalCode", new Integer(YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_POSTAL_CODES).toString());%>
	<%pageContext.setAttribute("filterCICustomerType", new Integer(YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_CUST_TYPE).toString());%>
	<%
    String referer = ServletUtil.createSafeRedirectUrl(request, ((CtiNavObject)session.getAttribute(ServletUtils.NAVIGATE)).getPreviousPage());	
	pageContext.setAttribute("backOnePage", referer);
	%>

	<cti:includeCss link="/include/PurpleStyles.css"/>
	<div class="headerbar">
		<%@ include file="include/HeaderBar.jspf" %>
	</div>
 	<br clear="all"> 
 	
    <!--  Need to do a little dance here.  JSTL won't see scriptlet (non-String) vars unless they
		are present in page, session, etc. as an attribute. -->
	<%pageContext.setAttribute("liteEC",liteEC);%>
	<c:set target="${filterBean}" property="energyCompany" value="${liteEC}" />
	<%pageContext.setAttribute("currentUser", lYukonUser);%>
	<c:set target="${filterBean}" property="currentUser" value="${currentUser}" />
 	<c:set target="${filterBean}" property="assignedFilters" value="${sessionScope.InventoryFilters}" />
 	
	<div class="standardpurplesidebox"> 
		<% String pageName = "Filter.jsp"; %>
		<div align="right">
			<%@ include file="include/Nav.jspf" %>
		</div>
	</div>

	<div class="standardcentralwhitebody">
		<div align="center"> <br>
            <% String header = "FILTER CHOICES"; %>
            <%@ include file="include/SearchBar.jspf" %>
    		<% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
    		<br clear="all">
    	</div>
    	
		<form name="MForm" method="post" action="<%=request.getContextPath()%>/servlet/InventoryManager" onsubmit="prepareSubmit(this)">
	    	<input type="hidden" name="action" value="FiltersUpdated">
	    	<table width="600" border="1" cellspacing="0" cellpadding="0" align="center">
	        	<tr> 
	              	<td width="55" class="HeaderCell">Available</td>
	               	<td width="85"> 
	                	<div align="right">Filter by:</div>
	                </td>
	                <td width="200"> 
	                	<select name="FilterType" size="1" style="width: 200px" onChange="changeFilterType(this.value)" >
	                    	<c:forEach var="filterEntry" items="${filterBean.availableFilters.yukonListEntries}">
                                <c:choose>
                                    <c:when test="${filterEntry.yukonDefID == 2908 || filterEntry.yukonDefID == 2909}">
                                        <c:if test="${filterBean.rangeValid}">
                                            <option value='<c:out value="${filterEntry.yukonDefID}"/>'> <c:out value="${filterEntry.entryText}"/> </option>
                                        </c:if>
                                    </c:when>
                                    <c:otherwise>
								        <option value='<c:out value="${filterEntry.yukonDefID}"/>'> <c:out value="${filterEntry.entryText}"/> </option>
                                    </c:otherwise>
                                </c:choose>
							</c:forEach>
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
	                            <option value='0'> <c:out value="${filterBean.noneString}"/> </option>
	                            <c:forEach var="serviceCompany" items="${filterBean.availableServiceCompanies}">
									<option value='<c:out value="${serviceCompany.liteID}"/>'> <c:out value="${serviceCompany.companyName}"/> </option>
								</c:forEach>
	                      	</select>
	                    </div>
	                    <div id='<c:out value="${filterDeviceStatus}"/>' style="display:none"> 
	                    	<select id='<c:out value="${filterDeviceStatus}"/>1' name='<c:out value="${filterDeviceStatus}"/>1' size="1" style="width: 200px" onChange="selectFilter(this.value)">
	                    		<option value='0'> <c:out value="${filterBean.noneString}"/> </option>
	                            <c:forEach var="deviceStatusEntry" items="${filterBean.availableDeviceStates.yukonListEntries}">
									<option value='<c:out value="${deviceStatusEntry.entryID}"/>'> <c:out value="${deviceStatusEntry.entryText}"/> </option>
								</c:forEach>
	                      	</select>
	                    </div>
	                    <div id='<c:out value="${filterApplianceType}"/>' style="display:none"> 
	                    	<select id='<c:out value="${filterApplianceType}"/>1' name='<c:out value="${filterApplianceType}"/>1' size="1" style="width: 200px" onChange="selectFilter(this.value)">
	                            <c:forEach var="appliance" items="${filterBean.availableApplianceCategories}">
									<option value='<c:out value="${appliance.liteID}"/>'> <c:out value="${appliance.description}"/> </option>
								</c:forEach>
							</select>
	                    </div>
	                    <div id='<c:out value="${filterDeviceMember}"/>' style="display:none"> 
	                    	<select id='<c:out value="${filterDeviceMember}"/>1' name='<c:out value="${filterDeviceMember}"/>1' size="1" style="width: 200px" onChange="selectFilter(this.value)">
	                            <c:forEach var="energyCo" items="${filterBean.availableMembers}">
									<option value='<c:out value="${energyCo.energyCompanyID}"/>'> <c:out value="${energyCo.name}"/> </option>
								</c:forEach>
							</select>
	                    </div>
	                   	<div id='<c:out value="${filterDeviceWarehouse}"/>' style="display:none"> 
	                    	<select id='<c:out value="${filterDeviceWarehouse}"/>1' name='<c:out value="${filterDeviceWarehouse}"/>1' size="1" style="width: 200px" onChange="selectFilter(this.value)">
	                    		<option value='0'> <c:out value="${filterBean.noneString}"/> </option>
	                            <c:forEach var="warehouse" items="${filterBean.availableWarehouses}">
									<option value='<c:out value="${warehouse.warehouseID}"/>'> <c:out value="${warehouse.warehouseName}"/> </option>
								</c:forEach>
							</select>
	                    </div>
	                    <div id='<c:out value="${filterDeviceSerialRangeMin}"/>' style="display:none"> 
		                    <input id='<c:out value="${filterDeviceSerialRangeMin}"/>1' type="text" name='<c:out value="${filterDeviceSerialRangeMin}"/>1' maxlength="12" size="14" onchange="storeSerial(this.value)">
	               		</div>
	               		<div id='<c:out value="${filterDeviceSerialRangeMax}"/>' style="display:none"> 
		                    <input id='<c:out value="${filterDeviceSerialRangeMax}"/>1' type="text" name='<c:out value="${filterDeviceSerialRangeMax}"/>1' maxlength="12" size="14" onchange="storeSerial(this.value)">
	               		</div>
	               		<div id='<c:out value="${filterDevicePostalCode}"/>' style="display:none"> 
		                    <input id='<c:out value="${filterDevicePostalCode}"/>1' type="text" name='<c:out value="${filterDevicePostalCode}"/>1' maxlength="12" size="14" onchange="storeSerial(this.value)">
	               		</div>
	               		<div id='<c:out value="${filterCICustomerType}"/>' style="display:none"> 
	                    	<select id='<c:out value="${filterCICustomerType}"/>1' name='<c:out value="${filterCICustomerType}"/>1' size="1" style="width: 200px" onChange="selectFilter(this.value)">
	                            <c:forEach var="ciCustomerType" items="${filterBean.availableCustomerTypes}">
									<option value='<c:out value="${ciCustomerType.first}"/>'> <c:out value="${ciCustomerType.second}"/> </option>
								</c:forEach>
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
                    	<input id="submitbutton" type="button" name="Submit" value="Query Inventory" onclick="prepareSubmit(this.form)">
                  	</td>
                  	<td width="205">  
                    	<input type="reset" name="Reset" value="Reset" onclick="location.reload()">
                  	</td>
                  	<td width="75" align="right"> 
                    	<input type="button" name="Back" value="Back" onclick="if (warnUnsavedChanges()) location.href='${backOnePage}'">
                  	</td>
              	</tr>
        	</table>
        	<br>
		</form>
    </div>
    
    <script language="JavaScript">
		var filterTexts = new Array();
		var selectionIDs = new Array();
		var yukonDefIDs = new Array();
		var curIdx = 0;
		var selectedFilterType = "0";
		var selectedFilter = ""
		var selectedFilterID = "0";
		
		function init()
		{
			var filters = document.MForm.AssignedFilters;
			if(filterTexts.length < filters.options.length)
			{
				curIdx = 0;
				<c:forEach var="existingFilter" items="${filterBean.assignedFilters}">
					filterTexts[curIdx] = '<c:out value="${existingFilter.filterText}"/>';
					yukonDefIDs[curIdx] = '<c:out value="${existingFilter.filterTypeID}"/>';
					selectionIDs[curIdx] = '<c:out value="${existingFilter.filterID}"/>';
					curIdx++;
				</c:forEach> 
			}
			
			selectedFilterType = '<c:out value="${filterDeviceType}"/>';
			selectedFilter = "Device type: ";
			selectedFilter += '<c:out value="${filterBean.defaultFilterSelection.entryText}"/>';
			selectedFilterID = '<c:out value="${filterBean.defaultFilterSelection.entryID}"/>';
		}
		
		function changeFilterType(filterBy) 
		{
			selectedFilterType = filterBy;
			var type = document.MForm.FilterType;
			document.getElementById('<c:out value="${filterDeviceType}"/>').style.display = "none";
			document.getElementById('<c:out value="${filterServiceCompany}"/>').style.display = "none";
			document.getElementById('<c:out value="${filterDeviceStatus}"/>').style.display = "none";
			document.getElementById('<c:out value="${filterApplianceType}"/>').style.display = "none";
 			document.getElementById('<c:out value="${filterDeviceMember}"/>').style.display = "none";
 			document.getElementById('<c:out value="${filterDeviceWarehouse}"/>').style.display = "none";
 			document.getElementById('<c:out value="${filterDeviceSerialRangeMin}"/>').style.display = "none";
 			document.getElementById('<c:out value="${filterDeviceSerialRangeMax}"/>').style.display = "none";
 			document.getElementById('<c:out value="${filterDevicePostalCode}"/>').style.display = "none";
 			document.getElementById('<c:out value="${filterCICustomerType}"/>').style.display = "none";
 			document.getElementById(filterBy).style.display = "";
			var comboID = filterBy + 1;
			selectedFilter = type.options[type.selectedIndex].text;  
			selectedFilter += ": ";
			if(filterBy == '<c:out value="${filterDeviceSerialRangeMax}"/>' || filterBy == '<c:out value="${filterDeviceSerialRangeMin}"/>' ||
							filterBy == '<c:out value="${filterDevicePostalCode}"/>')
			{
				selectedFilter += document.getElementById(comboID).value;
				selectedFilterID = 0; 
			}
			else
			{
                var index = document.getElementById(comboID).selectedIndex;
				selectedFilter += document.getElementById(comboID).options[index].text;
				selectedFilterID = document.getElementById(comboID).options[index].value; 
			}
			
		}
		
		function storeSerial(serialNum)
		{
			selectedFilterID = serialNum;
			var type = document.MForm.FilterType;
			var filterBy = selectedFilterType;
			filterBy += 1;
			var textField = document.getElementById(filterBy);
			selectedFilter = type.options[type.selectedIndex].text;  
			selectedFilter += ": ";
			selectedFilter += textField.value;
		}
		
		function selectFilter(filterID)
		{
			selectedFilterID = filterID;
			var type = document.MForm.FilterType;
			var filterBy = selectedFilterType;
			filterBy += 1;
			var filter = document.getElementById(filterBy);
			selectedFilter = type.options[type.selectedIndex].text;  
			selectedFilter += ": ";
			selectedFilter += filter.options[filter.selectedIndex].text;
		}
		
		function saveEntry(form) 
		{
			var filters = form.AssignedFilters;
			var oOption = document.createElement("OPTION");
			oOption.text = selectedFilter;
			oOption.value = selectedFilter;
			filters.options.add(oOption, curIdx);
			filters.selectedIndex = curIdx;
			filterTexts[curIdx] = selectedFilter;
			selectionIDs[curIdx] = selectedFilterID;
			yukonDefIDs[curIdx] = selectedFilterType;
			curIdx = filterTexts.length;

			setContentChanged(true);
		}
		
		function moveUp(form) 
		{
			var filters = form.AssignedFilters;
			var idx = filters.selectedIndex;
			if (idx > 0 && idx < filters.options.length) 
			{
				var oOption = filters.options[idx];
				filters.remove(idx);
				filters.options.add(oOption, idx-1);
				value = filterTexts[idx];
				filterTexts[idx] = filterTexts[idx-1];
				filterTexts[idx-1] = value;
				value = yukonDefIDs[idx];
				yukonDefIDs[idx] = yukonDefIDs[idx-1];
				yukonDefIDs[idx-1] = value;
				value = selectionIDs[idx];
				selectionIDs[idx] = selectionIDs[idx-1];
				selectionIDs[idx-1] = value;
				curIdx--;
				setContentChanged(true);
			}
		}
		
		function moveDown(form) 
		{
			var filters = form.AssignedFilters;
			var idx = filters.selectedIndex;
			if (idx >= 0 && idx < filters.options.length) {
				var oOption = filters.options[idx];
				filters.remove(idx);
				filters.options.add(oOption, idx+1);
				var value = filterTexts[idx];
				filterTexts[idx] = filterTexts[idx+1];
				filterTexts[idx+1] = value;
				value = yukonDefIDs[idx];
				yukonDefIDs[idx] = yukonDefIDs[idx+1];
				yukonDefIDs[idx+1] = value;
				value = selectionIDs[idx];
				selectionIDs[idx] = selectionIDs[idx+1];
				selectionIDs[idx+1] = value;
				curIdx++;
				setContentChanged(true);
			}
		}
		
		function deleteEntry(form) 
		{
			var filters = form.AssignedFilters;
			var idx = filters.selectedIndex;
			if (idx >= 0 && idx < filters.options.length) 
			{
				filters.remove(idx);
				filterTexts.splice(idx, 1);
				selectionIDs.splice(idx, 1);
				yukonDefIDs.splice(idx, 1);
				filters.selectedIndex = filters.options.length;
				setContentChanged(true);
			}
			
			curIdx = filterTexts.length;
		}
		
		function deleteAllEntries(form) 
		{
			var filters = form.AssignedFilters;
			if (filters.options.length > 0) 
			{
				if (!confirm("Are you sure you want to remove all filters?")) return;
				for (idx = filters.options.length; idx >= 0; idx--)
					filters.remove(idx);
				filterTexts.splice(0, filterTexts.length);
				selectionIDs.splice(0, selectionIDs.length);
				yukonDefIDs.splice(0, yukonDefIDs.length);
				filters.selectedIndex = 0;
				setContentChanged(true);
			}
			
			curIdx = filterTexts.length;
		}
		
		function prepareSubmit(form) 
		{
			if(filterTexts.length < 1)
			{
                alert("You have not defined any filters!");
                return;
			}
			else
			{
				for (idx = 0; idx < filterTexts.length; idx++) 
				{
                    var input = document.createElement('input');
                    input.setAttribute('type', 'hidden');
                    input.setAttribute('name', 'SelectionIDs');
                    input.setAttribute('value', selectionIDs[idx]);

                    var input2 = document.createElement('input');
                    input2.setAttribute('type', 'hidden');
                    input2.setAttribute('name', 'FilterTexts');
                    input2.setAttribute('value', filterTexts[idx]);

                    var input3 = document.createElement('input');
                    input3.setAttribute('type', 'hidden');
                    input3.setAttribute('name', 'YukonDefIDs');
                    input3.setAttribute('value', yukonDefIDs[idx]);
                    
                    form.appendChild(input);
                    form.appendChild(input2);
                    form.appendChild(input3);
				}
                
                $("submitbutton").toggle();
                document.body.style.cursor = "wait";
                
				form.submit();
			}
		}
	</script>
</cti:standardPage>          
