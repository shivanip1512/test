<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:includeScript link="/JavaScript/itemPicker.js" />
<cti:includeScript link="/JavaScript/tableCreation.js" />
<cti:includeScript link="/JavaScript/paoPicker.js" />
<cti:includeCss link="/WebConfig/yukon/styles/itemPicker.css" />

<cti:uniqueIdentifier  prefix="addPao" var="addPao"/>
<cti:uniqueIdentifier var="addPaoSpanId" prefix="addPaoSpan_"/>
        
<cti:standardPage title="Schedule Assignment" module="capcontrol">

<script type="text/JavaScript" src="/capcontrol/js/cbc_funcs.js"></script>

<cti:standardMenu/>
<cti:breadCrumbs>
	<cti:crumbLink url="/capcontrol/subareas.jsp" title="Home" />
</cti:breadCrumbs>


<script type="text/javascript" language="JavaScript">
var firstRun = true;

function addPao(paos) {
	hideErrors();
	$('myForm').submit();
}

function removeScheduleCommand(eventId) {
	var url = "/spring/capcontrol/scheduleAssignments/removePao";

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
		var rowId = row.id;
		if(row.id[0] == 's') {
			if (!hidden && schedChosen) {
				var rowSchedName = row.getElementsByTagName('td')[0].textContent;
				rowSchedName = trim(rowSchedName);
	
				if(rowSchedName != visibleSchedName) {
					hidden = true;
				}
			}
	
			if (!hidden && commandChosen) {
				var rowCommand = row.getElementsByTagName('td')[3].textContent;
				rowCommand = trim(rowCommand);
	
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

</script>

	<body onload="selectionChange();">
    
    <div id="paoTable">
		<form id="myForm" method="post" action="addPao">
			
			<span id="pickerDisabled">Select a Schedule and Command to continue.</span>
			<span id="pickerEnabled">
				<cti:multiPaoPicker 
				     paoIdField="newPaoId"
				     constraint="com.cannontech.common.search.criteria.CBCSubBusCriteria"
				     finalTriggerAction="addPao" 
				     >Click here</cti:multiPaoPicker> to choose devices to add this Schedule/Command.
	            <input id="newPaoId" name="newPaoId" type="hidden">
            </span>
			<br>
			
			<SELECT NAME="scheduleList" id="schedSelect" onChange="selectionChange()" >
				<option value="All">All Schedules</option>
				<c:forEach var="schedule" items="${scheduleList}">
					<c:choose>
						<c:when test="${param.defSchedule != schedule.scheduleID}"> 
							<option value="${schedule.scheduleID}">${schedule.scheduleName}</option> 
						</c:when>
						<c:when test="${param.defSchedule == schedule.scheduleID}"> 
							<option value="${schedule.scheduleID}" selected >${schedule.scheduleName}</option> 
						</c:when>
					</c:choose>
					
				</c:forEach>
			</SELECT>
			
			<SELECT NAME="commandList" id="commandSelect" onChange="selectionChange()">
				<option value="all">All Commands</option>
				<c:forEach var="command" items="${commandList}">
					<c:choose>
						<c:when test="${param.defCommand != command}">
							<option value="${command}">${command.commandName}</option>
						</c:when>
						<c:when test="${param.defCommand == command}">
							<option value="${command}" selected >${command.commandName}</option>
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
				<th style="text-align:left">Schedule</th>
				<th style="text-align:left">Last Run Time</th>
				<th style="text-align:left">Next Run Time</th>
				<th style="text-align:left">Command</th>
				<th style="text-align:left">Device</th>
				<th style="text-align:center">Delete</th>
			</tr>
			</thead>
			<tbody id="tableBody">
			<c:forEach var="item" items="${itemList}">
				<tr class="altRow" id="s_${item.eventId}" >
					<td><c:out value="${item.scheduleName}" /></td>
					<td><c:out value="${item.lastRunTime}" /></td>
					<td><c:out value="${item.nextRunTime}" /></td>
					<td><c:out value="${item.commandName}" /></td>
					<td><c:out value="${item.deviceName}" /></td>
					<td align="center">
                        <img src="/WebConfig/yukon/Icons/cancel.gif" class="pointer" onclick="removeScheduleCommand(${item.eventId})">
					</td>
				</tr>
				
			</c:forEach>
			</tbody>
		</table>
    </div>
    </body>
    <br>
</cti:standardPage>