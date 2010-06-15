<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:setProperty name="CtiNavObject" property="moduleExitPage" value=""/>

<cti:includeScript link="/JavaScript/itemPicker.js" />
<cti:includeScript link="/JavaScript/tableCreation.js" />
<cti:includeScript link="/JavaScript/paoPicker.js" />
<cti:includeCss link="/WebConfig/yukon/styles/itemPicker.css" />

<cti:uniqueIdentifier  prefix="addPao" var="addPao"/>
<cti:uniqueIdentifier var="addPaoSpanId" prefix="addPaoSpan_"/>
        
<cti:standardPage title="Schedule Assignment" module="capcontrol">

<script type="text/javascript" src="/capcontrol/js/cbc_funcs.js"></script>

<cti:standardMenu menuSelection="view|scheduleassignment"/>
<cti:breadCrumbs>
	<cti:crumbLink url="/spring/capcontrol/tier/areas" title="Home" />
	<cti:crumbLink title="Schedule Assignments"/>
</cti:breadCrumbs>

<cti:url var="baseUrl" value="/spring/capcontrol/schedule/scheduleAssignments" />

<script type="text/javascript" language="JavaScript">
var firstRun = true;

function addPao(paos) {
	hideErrors();
	$('myForm').submit();
	return true;
}

function removeScheduleCommand(eventId) {
	var url = "/spring/capcontrol/schedule/removePao";

	hideErrors();
	
    new Ajax.Request(url, {'parameters': {'eventId': eventId}, 
        onComplete: function(transport, json) {
			if (!json.success) {
				$('errorElement').innerHTML = json.resultText;
				$('errorElement').show();
			} else {
				deleteScheduleCommandFromPage(eventId);
			}
        
        } });
}

function setOvUv(eventId, ovuv) {
    var url = "/spring/capcontrol/schedule/setOvUv";
    hideErrors();
    new Ajax.Request(url, {'parameters': {'eventId': eventId, 'ovuv': ovuv}, 
        onComplete: function(transport, json) {
            if (!json.success) {
                $('errorElement').innerHTML = json.resultText;
                $('errorElement').show();
            }
        } 
    });
}

function hideErrors() {
	
	if(!firstRun) {	
		$('errorElement').hide();

		var addFailed = $('addFailed');
		if(addFailed != null) {
			addFailed.hide();
		}
	}
	firstRun = false;
}

function deleteScheduleCommandFromPage(eventId) {
	var line = document.getElementById('s_' + eventId);
	var table = document.getElementById('tableBody');

	table.removeChild(line);
}

function setAddPaoLink(enabled) {

	if(enabled) {
		$('pickerDisabled').hide();
		$('pickerEnabled').show();
	} else {
		$('pickerDisabled').show();
		$('pickerEnabled').hide();
	}
}

function trim(stringToTrim) {
	return stringToTrim.replace(/^\s+|\s+$/g,"");
}

function selectionChange() {
	hideErrors();
	
	var schedIndex = $('schedSelect').selectedIndex;
	var visibleSchedName = $('schedSelect')[schedIndex].text;

	var commandIndex = document.getElementById('commandSelect').selectedIndex;
	var visibleCommandName = document.getElementById('commandSelect')[commandIndex].text;
	
	var rows = $$('tbody tr');

	var schedChosen = visibleSchedName != "All Schedules";
	var commandChosen = visibleCommandName != "All Commands";

	setAddPaoLink(schedChosen && commandChosen);
}

Event.observe(window, 'load', function() {
	selectionChange();
	$('paoTable').show();
});

function clearFilter() {
	window.location = '${baseUrl}';
}

</script>

<%-- Filtering popup --%>
<tags:simplePopup id="filterPopup" title="Schedule Assignment Filters">
	<form name="filterForm" action="">
		<table class="filterSelection">
			<tr>
				<td>
					Schedules:
				</td>
				<td> 
					<select name="schedule" id="scheduleSelection">
						<option value="All">All Schedules</option>
						<c:forEach var="aSchedule" items="${scheduleList}">
								<option value="${aSchedule.scheduleName}" <c:if test="${param.schedule == aSchedule.scheduleName}">selected="selected" </c:if> > ${aSchedule.scheduleName}</option>
						</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<td>
					Commands:
				</td>
				<td>
					<select name="command" id="commandSelection">
						<option value="All">All Commands</option>
						<c:forEach var="aCommand" items="${commandList}">
							<c:choose>
								<c:when test="${param.command == 'All'}">
									<option value="${aCommand}">${aCommand.commandName}</option>
								</c:when>
								<c:otherwise>
									<option value="${aCommand}" <c:if test="${param.command == aCommand}">selected="selected" </c:if> >${aCommand.commandName}</option>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</select>
				</td>
			</tr>
		</table>
		<br />
		<div style="text-align:right">
			<input type="submit" value="Filter" />
			<input type="button" class="formSubmit" value="Clear Filters" onclick="javascript:clearFilter()" />
		</div>
	</form>
</tags:simplePopup>

    <div id="paoTable" style="display:none;">
		<form id="myForm" method="post" action="addPao">
			
			<span id="pickerDisabled">Select a Schedule and Command to add entry.</span>
			<cti:checkProperty property="CBCSettingsRole.CBC_DATABASE_EDIT">
				<span id="pickerEnabled">
                    <tags:pickerDialog id="cbcSubBusPicker" type="cbcSubBusPicker"
                        destinationFieldName="paoIdList" multiSelectMode="true" endAction="addPao">
                        Choose Devices</tags:pickerDialog> to add this Schedule/Command.
	            </span>
            </cti:checkProperty>
			<br>

	<SELECT name="scheduleSelection" id="schedSelect" onChange="selectionChange()" >
				<option value="All">All Schedules</option>
				<c:forEach var="schedule" items="${scheduleList}">
					<c:choose>
						<c:when test="${param.defSchedule != schedule.scheduleID}"> 
							<option value="${schedule.scheduleID}">${schedule.scheduleName}</option> 
						</c:when>
						<c:when test="${param.defSchedule == schedule.scheduleID}"> 
							<option value="${schedule.scheduleID}" selected="selected" >${schedule.scheduleName}</option> 
						</c:when>
					</c:choose>
					
				</c:forEach>
			</SELECT>
			
			<SELECT name="commandSelection" id="commandSelect" onChange="selectionChange()">
				<option value="all">All Commands</option>
				<c:forEach var="command" items="${commandList}">
					<c:choose>
						<c:when test="${param.defCommand != command}">
							<option value="${command}">${command.commandName}</option>
						</c:when>
						<c:when test="${param.defCommand == command}">
							<option value="${command}" selected="selected" >${command.commandName}</option>
						</c:when>
					</c:choose>
				</c:forEach>
			</SELECT>
		</form>
				
		<br><br>
		
		<c:if test="${param.failed}">
			<div id="addFailed" style="text-align:center;color:red;font-weight:bold;">The Add Failed: ${param.failedReason}</div>
		</c:if>
        
		<div id="errorElement" style="text-align:center;color:red;font-weight:bold;"></div>
		
		<tags:pagedBox title="Strategies" searchResult="${searchResult}"
		    filterDialog="filterPopup" baseUrl="${baseUrl}"
		    isFiltered="${isFiltered}" showAllUrl="${baseUrl}">
			
			<c:choose>
				<c:when test="${searchResult.hitCount == 0}">
					No items to display.
				</c:when>
				<c:otherwise>
					<table class="compactResultsTable smallPadding" id="scheduledTable" align="center">
					<thead>
						<tr id="header">
							<th>Schedule</th>
							<th>Last Run Time</th>
							<th>Next Run Time</th>
							<th>Command</th>
							<th>Device</th>
							<th>Disable OvUv</th>
						</tr>
						</thead>
						<tbody id="tableBody">
						<c:forEach var="item" items="${itemList}">
							<tr class="<tags:alternateRow odd="" even="altRow"/>" id="s_${item.eventId}" >
								<td>
			                        <cti:checkProperty property="CBCSettingsRole.CBC_DATABASE_EDIT">
			                        	<img src="/WebConfig/yukon/Icons/delete.gif" class="pointer" onclick="removeScheduleCommand(${item.eventId})">
			                    	</cti:checkProperty>
			                        
			                        <c:out value="${item.scheduleName}" />
			                    </td>
								<td><cti:formatDate value="${item.lastRunTime}" type="DATEHM" /></td>
								<td><cti:formatDate value="${item.nextRunTime}" type="DATEHM" /></td>
								<td><c:out value="${item.commandName}" /></td>
								<td><c:out value="${item.deviceName}" /></td>
								<td>
			                        <input type=checkbox onclick="setOvUv(${item.eventId}, Number(!this.checked))" 
			                        	<c:if test="${item.disableOvUv == 'Y'}">checked</c:if> />
								</td>
							</tr>
						</c:forEach>
						</tbody>
					</table>
				</c:otherwise>
			</c:choose>
		</tags:pagedBox>
    </div>
    <br>
</cti:standardPage>