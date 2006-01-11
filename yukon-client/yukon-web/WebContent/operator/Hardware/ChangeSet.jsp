<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<jsp:useBean id="filterBean" class="com.cannontech.stars.web.bean.FilterBean" scope="page"/>
<jsp:useBean id="inventoryBean" class="com.cannontech.stars.web.bean.InventoryBean" scope="session"/>

<cti:standardPage title="Energy Services Operations Center" module="stars">
	
	
	<link rel="stylesheet" href="../../include/PurpleStyles.css" type="text/css">
	<div class="headerbar">
		<%@ include file="include/HeaderBar.jsp" %>
	</div>
 	<br clear="all"> 
 	
	<%pageContext.setAttribute("liteEC",liteEC);%>
	<c:set target="${filterBean}" property="energyCompany" value="${liteEC}" />
	<%pageContext.setAttribute("currentUser", lYukonUser);%>
	<c:set target="${filterBean}" property="currentUser" value="${currentUser}" />
 	<c:set target="${filterBean}" property="assignedFilters" value="${sessionScope.InventoryFilters}" />
 	
	<div class="standardpurplesidebox"> 
	</div>

	<div class="standardcentralwhitebody">
		<div align="center"> <br>
            <% String header = "APPLY ACTIONS TO INVENTORY"; %>
            <%@ include file="include/SearchBar.jsp" %>
    		<br clear="all">
    	</div>
    	
		<form name="MForm" method="post" action="">
	    	<input type="hidden" name="action" value="ApplyActions">
	    	<table width="600" border="1" cellspacing="0" cellpadding="0" align="center">
	        	<tr> 
	              	<td width="55" class="HeaderCell">Actions</td>
	               	<td width="85"> 
	                </td>
	                <td width="200"> 
	                	<select name="FilterType" size="1" style="width: 200px" onChange="changeFilterType(this.value)" >
							<option value="1"> Change Device Type </option>
	                    	<option value="2"> Change Device Status </option>
	                    	<option value="3"> Move to Service Company </option>
	                    	<option value="4"> Move to Warehouse </option>
						</select>
	            	</td>
	            	<td width="240"> 
	                	<div id="1"> 
	                    	<select id="11" name="11" size="1" style="width: 200px" onChange="selectFilter(this.value)">
	                            <c:forEach var="deviceTypeEntry" items="${filterBean.availableDeviceTypes.yukonListEntries}">
									<option value='<c:out value="${deviceTypeEntry.entryID}"/>'> <c:out value="${deviceTypeEntry.entryText}"/> </option>
								</c:forEach>
	                      	</select>
	                    </div>
	                	<div id="2" style="display:none"> 
	                    	<select id="21" name="21" size="1" style="width: 200px" onChange="selectFilter(this.value)">
	                            <c:forEach var="deviceStatusEntry" items="${filterBean.availableDeviceStates.yukonListEntries}">
									<option value='<c:out value="${deviceStatusEntry.entryID}"/>'> <c:out value="${deviceStatusEntry.entryText}"/> </option>
								</c:forEach>
	                      	</select>
	                    </div>
	                    <div id="3" style="display:none" > 
	                    	<select id="31" name="31" size="1" style="width: 200px" onChange="selectFilter(this.value)">
	                            <c:forEach var="serviceCompany" items="${filterBean.availableServiceCompanies}">
									<option value='<c:out value="${serviceCompany.liteID}"/>'> <c:out value="${serviceCompany.companyName}"/> </option>
								</c:forEach>
	                      	</select>
	                    </div>

	                    <div id="4" style="display:none"> 
	                    	<select id="41" name=Warehouse1' size="1" style="width: 200px" onChange="selectFilter(this.value)">
	                            <option value="0"> WarehouseExample </option>
							</select>
	                    </div>
	                    
	                </td>
	           	</tr>
			</table>
			<br>
			<div align="center"> 
            	<input type="button" name="Save" value="Add Action to List" onclick="saveEntry(this.form)">
            </div>
			<br>
			<table width="600" border="1" cellspacing="0" cellpadding="0" align="center">
	        	<tr> 
	              	<td width="55" class="HeaderCell">Current Actions</td>
	                <td width="285"> 
	                	<select name="AssignedFilters" size="7" style="width: 293px">
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
                    	<input type="submit" name="Submit" value="Apply to Selected Inventory">
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
			
			selectedFilterType = '1';
			selectedFilter = "Change Device Type: LCR-3000";
		}
		
		function changeFilterType(filterBy) 
		{
			selectedFilterType = filterBy;
			var type = document.MForm.FilterType;
			document.getElementById("1").style.display = "none";
			document.getElementById("2").style.display = "none";
			document.getElementById("3").style.display = "none";
			document.getElementById("4").style.display = "none";
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
				if (!confirm("Are you sure you want to remove all actions?")) return;
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
		
		
	</script>
</cti:standardPage>          
