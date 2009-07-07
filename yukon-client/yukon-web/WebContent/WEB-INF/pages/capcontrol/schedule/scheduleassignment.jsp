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
</cti:breadCrumbs>


<script type="text/javascript" language="JavaScript">
var firstRun = true;

function addPao(paos) {
	hideErrors();
	$('myForm').submit();
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

	for (var i=0; i < rows.length; i++) {
		var row = rows[i];
		var hidden = false;
		var cells = row.getElementsByTagName('td');

		if(cells[0].id == 'schedRow') {

			if (!hidden && schedChosen) {
				var rowSchedName = cells[1].innerHTML;
				if(rowSchedName != visibleSchedName) {
					hidden = true;
				}
			}
	
			if (!hidden && commandChosen) {
				var rowCommand = cells[4].innerHTML;
				if(rowCommand != visibleCommandName) {
					hidden = true;
				}
			}
	
			if (hidden) {
				row.hide();
			} else {
				row.show();
			}
		}
	}

	setAddPaoLink(schedChosen && commandChosen);
}

Event.observe(window, 'load', function() {
	selectionChange();
});
</script>
    
    <div id="paoTable">
		<form id="myForm" method="post" action="addPao">
			
			<span id="pickerDisabled">Select a Schedule and Command to continue.</span>
			<cti:checkProperty property="CBCSettingsRole.CBC_DATABASE_EDIT">
				<span id="pickerEnabled">
					<cti:multiPaoPicker 
					     paoIdField="paoIdList"
					     constraint="com.cannontech.common.search.criteria.CBCSubBusCriteria"
					     finalTriggerAction="addPao" 
					     >Choose Devices</cti:multiPaoPicker> to add this Schedule/Command.
		            <input id="paoIdList" name="paoIdList" type="hidden">
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
			<b><div id="addFailed" style="text-align:center;color:red">The Add Failed: ${param.failedReason}</div></b>
		</c:if>
			<b><div id="errorElement" style="text-align:center;color:red"></div></b>
		
		<table class="resultsTable" id="scheduledTable" width="90%" border="0" cellspacing="0" cellpadding="3" align="center">
		<thead>
			<tr id="header">
				<th>Schedule</th>
				<th>Last Run Time</th>
				<th>Next Run Time</th>
				<th>Command</th>
				<th>Device</th>
				<th>Disable OvUv</th>
				<th>Delete</th>
			</tr>
			</thead>
			<tbody id="tableBody">
			<c:forEach var="item" items="${itemList}">
				<tr class="<tags:alternateRow odd="" even="altRow"/>" id="s_${item.eventId}" >
					<td id="schedRow" style="display:none"/>
					<td><c:out value="${item.scheduleName}" /></td>
					<td><cti:formatDate value="${item.lastRunTime}" type="DATEHM" /></td>
					<td><cti:formatDate value="${item.nextRunTime}" type="DATEHM" /></td>
					<td><c:out value="${item.commandName}" /></td>
					<td><c:out value="${item.deviceName}" /></td>
					<td>
                        <select id="disableOvUvSelect" onchange="setOvUv(${item.eventId}, this.selectedIndex)">
                            <option value="Y" <c:if test="${item.disableOvUv == 'Y'}">selected="selected"</c:if>>Y</option>
                            <option value="N" <c:if test="${item.disableOvUv == 'N'}">selected="selected"</c:if>>N</option>
                        </select>
					</td>
					<td align="center">
                        <cti:checkProperty property="CBCSettingsRole.CBC_DATABASE_EDIT">
                            <img src="/WebConfig/yukon/Icons/delete.gif" class="pointer" onclick="removeScheduleCommand(${item.eventId})">
                        </cti:checkProperty>
					</td>
				</tr>
				
			</c:forEach>
			</tbody>
		</table>
    </div>
    <br>
</cti:standardPage>