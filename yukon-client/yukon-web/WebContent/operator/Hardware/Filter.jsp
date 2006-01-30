<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<jsp:useBean id="filterBean" class="com.cannontech.stars.web.bean.FilterBean" scope="page"/>
<jsp:useBean id="inventoryBean" class="com.cannontech.stars.web.bean.InventoryBean" scope="session"/>

<cti:standardPage title="Energy Services Operations Center" module="stars">
	
	<!-- FILTER TYPES (will be accessible with JSTL tags after they are declared) -->
 	<%pageContext.setAttribute("filterDeviceType", new Integer(YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_DEV_TYPE).toString());%>
 	<%pageContext.setAttribute("filterServiceCompany", new Integer(YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_SRV_COMPANY).toString());%>
 	<%pageContext.setAttribute("filterDeviceStatus", new Integer(YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_DEV_STATUS).toString());%>
 	<%pageContext.setAttribute("filterDeviceLocation", new Integer(YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_LOCATION).toString());%>
 	<%pageContext.setAttribute("filterDeviceConfig", new Integer(YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_CONFIG).toString());%>
 	<%pageContext.setAttribute("filterDeviceMember", new Integer(YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_MEMBER).toString());%>
 	<%pageContext.setAttribute("filterDeviceWarehouse", new Integer(YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_WAREHOUSE).toString());%>
 	<%pageContext.setAttribute("filterDeviceZipCode", new Integer(YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_ZIP_CODE).toString());%>
 	<%pageContext.setAttribute("filterDeviceSerialRange", new Integer(YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_SERIAL_RANGE).toString());%>

	<link rel="stylesheet" href="../../include/PurpleStyles.css" type="text/css">
	<div class="headerbar">
		<%@ include file="include/HeaderBar.jsp" %>
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
	</div>

	<div class="standardcentralwhitebody">
		<div align="center"> <br>
            <% String header = "FILTER CHOICES"; %>
            <%@ include file="include/SearchBar.jsp" %>
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
								<option value='<c:out value="${filterEntry.yukonDefID}"/>'> <c:out value="${filterEntry.entryText}"/> </option>
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
            			
            			<input type="button" name="MoveUp" value="Move Up" style="width:80" onclick="moveUp(this.form)">
                        <br>
                        <input type="button" name="MoveDown" value="Move Down" style="width:80" onclick="moveDown(this.form)">
                        <br>
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
        	<c:if test="${param.Record != null}">
				<c:set target="${inventoryBean}" property="internalRequest" value="${pageContext.request}" />
				<table width="600" border="0" cellspacing="0" cellpadding="0" align="center" class="TableCell">
					<tr>
						<td width="100%" class="headeremphasis"> INVENTORY RESULTS: 
							<c:out value="${inventoryBean.filterInventoryHTML}"/>
							hardware records match these requirements.
						</td>
					</tr>
				</table>
				<table width="600" border="0" cellspacing="0" cellpadding="0" align="center" class="TableCell">
					<tr>
						<td width="100" align="right"> 
	                    	<input type="button" name="ViewSet" value="View All Results" onclick="viewAll(this.form)">
	                  	</td>
	                  	<td width="500"> 
	                    	<input type="button" name="Manipulate" value="Manipulate Results" onclick="manipulateAll(this.form)">
	                  	</td>
	                </tr>
				</table>
			</c:if>
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
			selectedFilter += ": ";
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
			document.getElementById('<c:out value="${filterDeviceLocation}"/>').style.display = "none";
			document.getElementById('<c:out value="${filterDeviceConfig}"/>').style.display = "none";
 			document.getElementById('<c:out value="${filterDeviceMember}"/>').style.display = "none";
 			document.getElementById('<c:out value="${filterDeviceWarehouse}"/>').style.display = "none";
 			document.getElementById('<c:out value="${filterDeviceZipCode}"/>').style.display = "none";
 			document.getElementById(filterBy).style.display = "";
			filterBy += 1;
			selectedFilter = type.options[type.selectedIndex].text;  
			selectedFilter += ": ";
			selectedFilter += document.getElementById(filterBy).options[0].text;
			
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
				filters.options.remove(idx);
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
				filters.options.remove(idx);
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
				filters.options.remove(idx);
				filterTexts.splice(idx, 1);
				selectionIDs.splice(idx, 1);
				yukonDefIDs.splice(idx, 1);
				filters.selectedIndex = filters.options.length;
				showEntry(form);
				setContentChanged(true);
			}
		}
		
		function deleteAllEntries(form) 
		{
			var filters = form.AssignedFilters;
			if (filters.options.length > 1) 
			{
				if (!confirm("Are you sure you want to remove all filters?")) return;
				for (idx = filters.options.length; idx >= 0; idx--)
					filters.options.remove(idx);
				filterTexts.splice(0, filterTexts.length);
				selectionIDs.splice(idx, selectionsIDs.length);
				yukonDefIDs.splice(idx, yukonDefIDs.length);
				filters.selectedIndex = 0;
				showEntry(form);
				setContentChanged(true);
			}
		}
		
		function prepareSubmit(form) 
		{
			for (idx = 0; idx < filterTexts.length; idx++) 
			{
				var html = '<input type="hidden" name="SelectionIDs" value="' + selectionIDs[idx] + '">';
				form.insertAdjacentHTML("beforeEnd", html);
				html = '<input type="hidden" name="FilterTexts" value="' + filterTexts[idx] + '">';
				form.insertAdjacentHTML("beforeEnd", html);
				html = '<input type="hidden" name="YukonDefIDs" value="' + yukonDefIDs[idx] + '">';
				form.insertAdjacentHTML("beforeEnd", html);
			}
		}
		
		function viewAll(form)
		{
			form.action.value = "ViewInventoryResults";
			form.submit();
		}
		
		function manipulateAll(form)
		{
			form.action.value = "ManipulateInventoryResults";
			form.submit();
		}
	</script>
</cti:standardPage>          
