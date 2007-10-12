<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<jsp:useBean id="filterBean" class="com.cannontech.stars.web.bean.FilterBean" scope="page"/>
<jsp:useBean id="workOrderBean" class="com.cannontech.stars.web.bean.WorkOrderBean" scope="session"/>

<cti:standardPage title="Energy Services Operations Center" module="stars" htmlLevel="quirks">
	
	<!-- FILTER TYPES (will be accessible with JSTL tags after they are declared) -->
 	<%pageContext.setAttribute("filterServiceCompany", new Integer(YukonListEntryTypes.YUK_DEF_ID_SO_FILTER_BY_SRV_COMPANY).toString());%>
 	<%pageContext.setAttribute("filterDesignationCodes", new Integer(YukonListEntryTypes.YUK_DEF_ID_SO_FILTER_BY_SRV_COMP_CODES).toString());%>
 	<%pageContext.setAttribute("filterServiceType", new Integer(YukonListEntryTypes.YUK_DEF_ID_SO_FILTER_BY_SRV_TYPE).toString());%>
 	<%pageContext.setAttribute("filterServiceStatus", new Integer(YukonListEntryTypes.YUK_DEF_ID_SO_FILTER_BY_STATUS).toString());%>
 	<%pageContext.setAttribute("filterCICustomerType", new Integer(YukonListEntryTypes.YUK_DEF_ID_SO_FILTER_BY_CUST_TYPE).toString());%>

	<link rel="stylesheet" href="../../include/PurpleStyles.css" type="text/css">
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
 	<c:set target="${filterBean}" property="assignedFilters" value="${sessionScope.WorkOrderFilters}" />
	<%pageContext.setAttribute("filterListName", YukonSelectionListDefs.YUK_LIST_NAME_SO_FILTER_BY);%>
	<c:set target="${filterBean}" property="filterListName" value="${filterListName}" />

	<div class="standardpurplesidebox"> 
		<% String pageName = "WOFilter.jsp"; %>
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
<!-- Java script needed for the Calender Function--->
<SCRIPT  LANGUAGE="JavaScript" SRC="../../JavaScript/calendar.js"></SCRIPT>
    	
		<form name="MForm" method="post" action="<%=request.getContextPath()%>/servlet/WorkOrderManager" onsubmit="prepareSubmit(this)">
	    	<input type="hidden" name="action" value=UpdateFilters>
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
	                    <div id='<c:out value="${filterServiceCompany}"/>' style="display:none" > 
	                    	<select id='<c:out value="${filterServiceCompany}"/>1' name='<c:out value="${filterServiceCompany}"/>1' size="1" style="width: 200px" onChange="selectFilter(this.value)">
	                    		<option value='0'> <c:out value="${filterBean.noneString}"/> </option>
	                            <c:forEach var="serviceCompany" items="${filterBean.availableServiceCompanies}">
									<option value='<c:out value="${serviceCompany.liteID}"/>'> <c:out value="${serviceCompany.companyName}"/> </option>
								</c:forEach>
	                      	</select>
	                    </div>
	                    <div id='<c:out value="${filterDesignationCodes}"/>' style="display:none" >
                            <input id='<c:out value="${filterDesignationCodes}"/>1' type="text" name="${filterDesignationCodes}" style="width: 200px;" onChange="selectFilter('<c:out value="${filterDesignationCodes}"/>1');"/>
	                    </div>
	                    <div id='<c:out value="${filterServiceType}"/>' style="display:none"> 
	                    	<select id='<c:out value="${filterServiceType}"/>1' name='<c:out value="${filterServiceType}"/>1' size="1" style="width: 200px" onChange="selectFilter(this.value)">
	                            <c:forEach var="serviceType" items="${filterBean.availableServiceTypes.yukonListEntries}">
									<option value='<c:out value="${serviceType.entryID}"/>'> <c:out value="${serviceType.entryText}"/> </option>
								</c:forEach>
	                      	</select>
	                    </div>
	                    <div id='<c:out value="${filterServiceStatus}"/>' style="display:true"> 
	                    	<select id='<c:out value="${filterServiceStatus}"/>1' name='<c:out value="${filterServiceStatus}"/>1' size="1" style="width: 200px" onChange="selectFilter(this.value)">
	                            <option value='0'> <c:out value="${filterBean.noneString}"/> </option>
	                            <c:forEach var="serviceStatus" items="${filterBean.availableServiceStatuses.yukonListEntries}">
									<option value='<c:out value="${serviceStatus.entryID}"/>'> <c:out value="${serviceStatus.entryText}"/> </option>
								</c:forEach>
							</select>
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
            <table width="100%" border="0" cellspacing="0" cellpadding="0" class="TableCell">
              <tr>
                <td colspan="3" align="center">* To view archived Service Status Events, select a date range below.  
                </td>
              </tr>
              <tr>
  			    <td align="right" width="47%"><INPUT type="checkbox" name="enableStart" onclick="enableStartDate(this.form, this.checked);"/>Start Date: 
				<div id='startDateDiv' disabled="true">
                <input id="startCal" type="text" name="start" value="<%= datePart.format(ServletUtil.getToday()) %>" size="8">
                  <a id="startCalHRef" href="javascript:;" style="cursor:default"
                      onMouseOver="window.status='Start Date Calendar';return true;"
			          onMouseOut="window.status='';return true;"> 
                <img src="<%=request.getContextPath()%>/WebConfig/yukon/Icons/StartCalendar.gif" width="20" height="15" align="ABSMIDDLE" border="0">
                </a></div></td>
                <td>&nbsp;</td>
                <td width="47%"><INPUT type="checkbox" name="enableStop" onclick="enableStopDate(this.form, this.checked);"/>Stop Date: 
   				<div id='stopDateDiv' disabled="true"> 
                <input id="stopCal" type="text" name="stop" value="<%= datePart.format(ServletUtil.getTomorrow()) %>" size="8">
                  <a id="stopCalHRef" href="javascript:;" style="cursor:default"
                      onMouseOver="window.status='Stop Date Calendar';return true;"
			          onMouseOut="window.status='';return true;"> 
                <img src="<%=request.getContextPath()%>/WebConfig/yukon/Icons/StartCalendar.gif" width="20" height="15" align="ABSMIDDLE" border="0"> 
                </a></div></td>
              </tr>
            </table>
			<br>        	
        	<table width="600" border="0" cellspacing="0" cellpadding="5" align="center">
            	<tr>
                	<td width="290" align="right"> 
                    	<input id="submitbutton" type="submit" name="Submit" value="Query Work Orders">
                  	</td>
                  	<td width="205"> 
                    	<input type="reset" name="Reset" value="Reset" onclick="location.reload()">
                  	</td>
                  	<td width="75" align="right"> 
                    	<input type="button" name="Back" value="Back" onclick="if (warnUnsavedChanges()) location.href='WorkOrder.jsp'">
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
			
			selectedFilterType = '<c:out value="${filterServiceStatus}"/>';			
			selectedFilter = "Service Status: ";
			selectedFilter += '<c:out value="${filterBean.defaultFilterSelection.entryText}"/>';
			selectedFilterID = '<c:out value="${filterBean.defaultFilterSelection.entryID}"/>';
		}
		
		function changeFilterType(filterBy) 
		{
			selectedFilterType = filterBy;
			var type = document.MForm.FilterType;
			document.getElementById('<c:out value="${filterServiceCompany}"/>').style.display = "none";
			document.getElementById('<c:out value="${filterDesignationCodes}"/>').style.display = "none";
			document.getElementById('<c:out value="${filterServiceType}"/>').style.display = "none";
			document.getElementById('<c:out value="${filterServiceStatus}"/>').style.display = "none";
			document.getElementById('<c:out value="${filterCICustomerType}"/>').style.display = "none";
 			document.getElementById(filterBy).style.display = "";
			filterBy += 1;
			selectedFilter = type.options[type.selectedIndex].text;  
			selectedFilter += ": ";
            
            var filterByElement = document.getElementById(filterBy);
            var tagName = filterByElement.tagName.toLowerCase();

            if (tagName == 'select') {
    			selectedFilter += filterByElement.options[0].text;
	       		selectedFilterID = filterByElement.options[0].value;
            }
            
            if (tagName == 'input') {
                selectedFilterID = filterByElement.name;
            }    
		}
		
		
		function selectFilter(filterID)
		{
			selectedFilterID = filterID;
			var type = document.MForm.FilterType;
			var filterBy = selectedFilterType;
			filterBy += 1;
			var filter = document.getElementById(filterBy);
            var tagName = filter.tagName.toLowerCase();
            
			selectedFilter = type.options[type.selectedIndex].text;  
			selectedFilter += ": ";
            
            if (tagName == 'select') {
    			selectedFilter += filter.options[filter.selectedIndex].text;
            }
            
            if (tagName == 'input') {
                selectedFilter += filter.value;
            }    
		}
		
		function saveEntry(form) 
		{
			var filters = form.AssignedFilters;
			var oOption = document.createElement("OPTION");
			oOption.text = selectedFilter;
			oOption.value = selectedFilter;
			curIdx = filterTexts.length;
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
					filters.options.remove(idx);
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
				if(!confirm("You have not defined any filters!")) return;
			}
			else
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
                
                $("submitbutton").toggle();
                document.body.style.cursor = "wait";
                
				form.submit();
			}
		}

        function enableStartDate(form, isChecked)
		{
			if( isChecked)
			{
				document.getElementById("startCalHRef").style.cursor = 'pointer';
				document.getElementById("startCalHRef").href = 'javascript:openCalendar(document.MForm.startCal)'
				form.startCal.value = '<%= datePart.format(ServletUtil.getToday()) %>';
			}
			else
			{
				document.getElementById("startCalHRef").style.cursor = 'default';
				document.getElementById("startCalHRef").href = 'javascript:;'
				form.startCal.value = '<%= datePart.format(ServletUtil.getToday()) %>';
			}
			document.getElementById('startDateDiv').disabled = !isChecked;
		}
        function enableStopDate(form, isChecked)
		{
			if( isChecked)
			{
				document.getElementById("stopCalHRef").style.cursor = 'pointer';
				document.getElementById("stopCalHRef").href = 'javascript:openCalendar(document.MForm.stopCal)'
			}
			else
			{
				document.getElementById("stopCalHRef").style.cursor = 'default';
				document.getElementById("stopCalHRef").href = 'javascript:;'
				form.stopCal.value = '<%= datePart.format(ServletUtil.getTomorrow()) %>';
			}
			document.getElementById('stopDateDiv').disabled = !isChecked;
		}
		
	</script>
</cti:standardPage>          
