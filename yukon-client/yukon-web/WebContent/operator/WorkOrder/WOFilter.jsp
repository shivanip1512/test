<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<jsp:useBean id="filterBean" class="com.cannontech.stars.web.bean.FilterBean" scope="page"/>
<jsp:useBean id="workOrderBean" class="com.cannontech.web.bean.WorkOrderBean" scope="session"/>

<cti:standardPage title="Energy Services Operations Center" module="stars" htmlLevel="quirks">
	
    <cti:msg key="yukon.common.calendarcontrol.months" var="months"/>
    <cti:msg key="yukon.common.calendarcontrol.days" var="days"/>
    <cti:msg key="yukon.common.calendarcontrol.clear" var="clear"/>
    <cti:msg key="yukon.common.calendarcontrol.close" var="close"/>
    
	<!-- FILTER TYPES (will be accessible with JSTL tags after they are declared) -->
 	<%
 	    pageContext.setAttribute("filterServiceCompany",
 	                             new Integer(YukonListEntryTypes.YUK_DEF_ID_SO_FILTER_BY_SRV_COMPANY).toString());
 	%>
 	<%
 	    pageContext.setAttribute("filterDesignationCodes",
 	                             new Integer(YukonListEntryTypes.YUK_DEF_ID_SO_FILTER_BY_SRV_COMP_CODES).toString());
 	%>
 	<%
 	    pageContext.setAttribute("filterServiceType",
 	                             new Integer(YukonListEntryTypes.YUK_DEF_ID_SO_FILTER_BY_SRV_TYPE).toString());
 	%>
 	<%
 	    pageContext.setAttribute("filterServiceStatus",
 	                             new Integer(YukonListEntryTypes.YUK_DEF_ID_SO_FILTER_BY_STATUS).toString());
 	%>
 	<%
 	    pageContext.setAttribute("filterCICustomerType",
 	                             new Integer(YukonListEntryTypes.YUK_DEF_ID_SO_FILTER_BY_CUST_TYPE).toString());
 	%>

    <cti:includeScript link="/JavaScript/calendarControl.js"/>
    <cti:includeCss link="/WebConfig/yukon/styles/calendarControl.css"/>
	<cti:includeCss link="/include/PurpleStyles.css"/>
	<div class="headerbar">
		<%@ include file="include/HeaderBar.jspf" %>
	</div>
 	<br clear="all"> 
 	
    <!--  Need to do a little dance here.  JSTL won't see scriptlet (non-String) vars unless they
		are present in page, session, etc. as an attribute. -->
	<%
	    pageContext.setAttribute("liteEC", liteEC);
	%>
	<c:set target="${filterBean}" property="energyCompany" value="${liteEC}" />
	<%
	    pageContext.setAttribute("currentUser", lYukonUser);
	%>
	<c:set target="${filterBean}" property="currentUser" value="${currentUser}" />
 	<c:set target="${filterBean}" property="assignedFilters" value="${sessionScope.WorkOrderFilters}" />
	<%
	    pageContext.setAttribute("filterListName",
	                             YukonSelectionListDefs.YUK_LIST_NAME_SO_FILTER_BY);
	%>
	<c:set target="${filterBean}" property="filterListName" value="${filterListName}" />

	<div class="standardpurplesidebox"> 
		<%
 		    String pageName = "WOFilter.jsp";
 		%>
		<div align="right">
			<%@ include file="include/Nav.jspf" %>
		</div>
	</div>

	<div class="standardcentralwhitebody">
		<div align="center"> <br>
            <%
                String header = "FILTER CHOICES";
            %>
            <%@ include file="include/SearchBar.jspf" %>
    		<%
    		    if (errorMsg != null)
    		        out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>");
    		%>
    		<br clear="all">
    	</div>
    	
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
            <table align="center" width="75%" class="TableCell">
              <tr>
                <td colspan="2" style="font-align: center;">* To view archived Service Status Events, select a date range below.</td>
              </tr>
              <tr style="font-weight: bold;">
                <%
                    Date startDate;
                    Date stopDate;
                    String startDateSelected;
                    String stopDateSelected;
                    String startDateDivDisabled;
                    String stopDateDivDisabled;

                    if (workOrderBean.getStartDate() != null) {
                        startDate = workOrderBean.getStartDate();
                        startDateSelected = "checked";
                        startDateDivDisabled = "";
                    } else {
                        startDate = ServletUtil.getToday();
                        startDateSelected = "";
                        startDateDivDisabled = "disabled";
                    }

                    if (workOrderBean.getStopDate() != null) {
                        stopDate = workOrderBean.getStopDate();
                        stopDateSelected = "checked";
                        stopDateDivDisabled = "";
                    } else {
                        stopDate = ServletUtil.getTomorrow();
                        stopDateSelected = "";
                        stopDateDivDisabled = "disabled";
                    }

                    String formattedStartDate = datePart.format(startDate);
                    String formattedStopDate = datePart.format(stopDate);
                %>
  			    <td><input id="enableStart" type="checkbox" name="enableStart" onclick="enableStartDate('<%=formattedStartDate%>');" <%=startDateSelected%>/>Start Date</td>
                <td><input id="enableStop" type="checkbox" name="enableStop" onclick="enableStopDate('<%=formattedStopDate%>');" <%=stopDateSelected%>/>Stop Date</td>
              </tr>
              <tr>
                <td><span class='NavText'>* Greater than 00:00, not inclusive</span></td>
                <td><span class='NavText'>* Less than or equal to 00:00, inclusive</span></td>
              </tr>
              <tr>
                <td>
                    <div id='startDateDiv' <%=startDateDivDisabled%>>
                        <input id="startCal" type="text" name="start" value="<%=formattedStartDate%>" size="8">
                            <a id="startCalHRef" href="javascript:;" style="cursor: default" 
                               onMouseOver="window.status='Start Date Calendar';return true;"
                               onMouseOut="window.status='';return true;"> <img
                               src="<%=request.getContextPath()%>/WebConfig/yukon/Icons/StartCalendar.png"
                               width="20" height="15" align="ABSMIDDLE" border="0"/>
                            </a>
                        </input>
                    </div>
                </td>
                <td>
                    <div id='stopDateDiv' <%=stopDateDivDisabled%>> 
                        <input id="stopCal" type="text" name="stop" value="<%=formattedStopDate%>" size="8">
                            <a id="stopCalHRef" href="javascript:;" style="cursor:default"
                               onMouseOver="window.status='Stop Date Calendar';return true;"
                               onMouseOut="window.status='';return true;"> 
                                <img src="<%=request.getContextPath()%>/WebConfig/yukon/Icons/StartCalendar.png" width="20" height="15" align="ABSMIDDLE" border="0"/> 
                            </a>
                        </input>    
                        </div>    
                    </td>
                </tr>
            </table>
            <br>
            <div style="float: right;">
                <input style="margin-right: 0.1cm" id="submitbutton" type="submit" name="Submit" value="Query Work Orders">
                <input style="margin-right: 5.5cm" type="reset" name="Reset" value="Reset" onclick="location.reload()">
                <input style="margin-right: 0.2cm" type="button" name="Back" value="Back" onclick="if (warnUnsavedChanges()) location.href='WorkOrder.jsp'">
            </div>
		</form>
	<div id="browserWarning" style="display:none; font-weight: bold; color: red; font-size: 14px; text-align: center; margin: 12px 0"><BR><BR>This page only works with Internet Explorer.</div>
    </div>
    <script type="text/javascript">
	if (!Prototype.Browser.IE) {
	    $('browserWarning').show();
	}
	</script>
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

        function enableStartDate(startDateValue) {
            var startCalHRef = $("startCalHRef");
            var isChecked = $("enableStart").checked;
            var cursor = 'default';
            var href = 'javascript:void();';

            if (isChecked) {
                cursor = 'hand';
                href = "javascript:showCalendarControl(document.MForm.startCal, '${months}', '${days}', '${clear}', '${close}');"
            }
            
            $("startDateDiv").disabled = !isChecked;
            $("startCal").value = startDateValue;
            startCalHRef.style.cursor = cursor;
            startCalHRef.href = href; 
		}
        
        function enableStopDate(stopDateValue) {
            var stopCalHRef = $("stopCalHRef");
            var isChecked = $("enableStop").checked;
            var cursor = 'default';
            var href = 'javascript:void();';

            if (isChecked) {
                cursor = 'hand';
                href = "javascript:showCalendarControl(document.MForm.stopCal, '${months}', '${days}', '${clear}', '${close}');"
            }
            
            $("stopDateDiv").disabled = !isChecked;
            $("stopCal").value = stopDateValue;
            stopCalHRef.style.cursor = cursor;
            stopCalHRef.href = href; 
		}
		
	</script>
</cti:standardPage>          
