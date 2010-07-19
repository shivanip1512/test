<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:setProperty name="CtiNavObject" property="moduleExitPage" value=""/>

<cti:includeScript link="/JavaScript/itemPicker.js" />
<cti:includeScript link="/JavaScript/tableCreation.js" />
<cti:includeScript link="/JavaScript/paoPicker.js" />
<cti:includeCss link="/WebConfig/yukon/styles/itemPicker.css" />

<cti:uniqueIdentifier  prefix="addPao" var="addPao"/>
<cti:uniqueIdentifier var="addPaoSpanId" prefix="addPaoSpan_"/>
<c:set var="notAuthorizedText" value="User is not authorized to perform this action" />
<c:set value="Confirm Sub" var="confirmCommand" />
<c:set value="Send Time Syncs" var="sendTimeSyncsCommand" />

<cti:standardPage title="Schedule Assignment" module="capcontrol">
	<cti:standardMenu menuSelection="view|scheduleassignment"/>
	<cti:breadCrumbs>
		<cti:crumbLink url="/spring/capcontrol/tier/areas" title="Home" />
		<cti:crumbLink title="Schedule Assignments"/>
	</cti:breadCrumbs>
	
	<%@include file="scheduleassignmentpopups.jsp" %>
	<%@include file="/capcontrol/cbc_inc.jspf" %>
	
	<cti:url var="baseUrl" value="/spring/capcontrol/schedule/scheduleAssignments" />
	<cti:url var="startMultiUrl" value="/spring/capcontrol/schedule/startMultiple" />
	
	<script type="text/javascript" language="JavaScript">
		function removeScheduleCommand(eventId) {
			var url = "/spring/capcontrol/schedule/removePao";
		    new Ajax.Request(url, {'parameters': {'eventId': eventId}, 
		        onComplete: function(transport, json) {
					if (!json.success) {
						display_status('Remove Schedule', '', json.resultText, 'red');
					} else {
						deleteScheduleCommandFromPage(eventId);
					}
		        
		        } });
		}
		
		function submitCommand(eventId, scheduleName, deviceName) {
			var url = "/spring/capcontrol/schedule/startSchedule";
			new Ajax.Request(url, {'parameters': {'eventId': eventId, 'deviceName': deviceName},
				onComplete: function(transport, json) {
					if (!json.success) {
		                display_status(scheduleName, '', json.resultText, 'red');
					} else {
		                display_status(scheduleName, '', json.resultText, 'green');
					}
				} });
		}
		
		function submitStop(deviceId, deviceName) {
			var url = "/spring/capcontrol/schedule/stopSchedule";
			new Ajax.Request(url, {'parameters': {'deviceId': deviceId, 'deviceName': deviceName},
				onComplete: function(transport, json) {
					if(!json.success) {
						display_status('Stop Schedule', '', json.resultText, 'red');
					} else {
						display_status('Stop Schedule', '', json.resultText, 'green');
					}
			} });
		}
		
		function setOvUv(eventId, ovuv) {
		    var url = "/spring/capcontrol/schedule/setOvUv";
		    new Ajax.Request(url, {'parameters': {'eventId': eventId, 'ovuv': ovuv}, 
		        onComplete: function(transport, json) {
		            if (!json.success) {
		                display_status('OvUv', '', json.resultText, 'red');
		            }
		        } 
		    });
		}
		
		function deleteScheduleCommandFromPage(eventId) {
			var line = document.getElementById('s_' + eventId);
			var table = document.getElementById('tableBody');
			table.removeChild(line);
		}
		
		function clearFilter() {
			window.location = '${baseUrl}';
		}
	</script>
	
	<!-- Display success or failure message if a command was submitted -->

	<c:if test="${param.success != null}">
		<c:choose>
			<c:when test="${param.success}">
				<script type="text/javascript">
					display_status('', '', '<spring:escapeBody javaScriptEscape="true">${param.resultText}</spring:escapeBody>', 'green');
				</script>
			</c:when>
			<c:otherwise>
				<script type="text/javascript">
					display_status('', '', '<spring:escapeBody javaScriptEscape="true">${param.resultText}</spring:escapeBody>', 'red');
				</script>
			</c:otherwise>
		</c:choose>
	</c:if>
		
	<table class="widgetColumns">
		<tr>
			<td class="widgetColumnCell" valign="top">
				<div class="widgetContainer">
					<tags:abstractContainer type="box" title="Actions">
								<div style="padding: 1px">
									<c:choose>
										<c:when test="${hasActionRoles == true}">
												<img src="/WebConfig/yukon/Icons/control_play_blue.gif" class="tierImg pointer" title="Run multiple commands" 
												onclick="$('startMultipleSchedulesPopup').toggle();">
										</c:when>
										<c:otherwise>
											<img src="/WebConfig/yukon/Icons/control_play_blue_disabled.gif" class="tierImg" 
											title="${notAuthorizedText}">
										</c:otherwise>
									</c:choose>
									Run Multiple Schedule Assignment Commands
								</div>
								<div style="padding: 1px">
									<c:choose>
										<c:when test="${hasActionRoles == true}">
												<img src="/WebConfig/yukon/Icons/control_stop_blue.gif" class="tierImg pointer" title="Stop multiple commands" 
												onclick="$('stopMultipleSchedulesPopup').toggle();">
										</c:when>
										<c:otherwise>
											<img src="/WebConfig/yukon/Icons/control_stop_blue_disabled.gif" class="tierImg" 
											title="${notAuthorizedText}">
										</c:otherwise>
									</c:choose>
									Stop Multiple Schedule Assignment Commands
								</div>
								<div style="padding: 1px">
									<c:choose>
										<c:when test="${hasEditingRole == true}">
												<img src="/WebConfig/yukon/Icons/add.gif" class="tierImg pointer" title="Create a new schedule assignment" 
												onclick="$('newScheduleAssignmentPopup').toggle();">
										</c:when>
										<c:otherwise>
											<img src="/WebConfig/yukon/Icons/add_disabled_gray.gif" class="tierImg" 
											title="${notAuthorizedText}">
										</c:otherwise>
									</c:choose>
									New Schedule Assignment
								</div>
					</tags:abstractContainer>
				</div>
			</td>
			<td class="widgetColumnCell" valign="top"></td>
		</tr>
		<tr>
			<td class="widgetColumnCell" colspan="2">
				<div class="widgetContainer">
				
	<tags:pagedBox title="Schedule Assignments" searchResult="${searchResult}"
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
						<th>Actions</th>
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
		                        <!-- delete button and schedule name -->
		                        <cti:checkProperty property="CBCSettingsRole.CBC_DATABASE_EDIT">
		                        	<img src="/WebConfig/yukon/Icons/delete.gif" class="pointer" onclick="removeScheduleCommand(${item.eventId})">
		                    	</cti:checkProperty>
		                        <c:out value="${item.scheduleName}" />
		                    </td>
		                    <td>
		                    	<!-- Actions -->
		                    	<c:choose>
		                    		<c:when test="${hasActionRoles == true}">
			                    		<img src="/WebConfig/yukon/Icons/control_play_blue.gif" class="pointer" title="Run command" 
			                    		onclick="submitCommand(${item.eventId},'${item.scheduleName}', '<spring:escapeBody javaScriptEscape="true">${item.deviceName}</spring:escapeBody>')">
		                    			<c:if test="${(item.commandName != confirmCommand)&&(item.commandName != sendTimeSyncsCommand)}">
		                    				<img src="/WebConfig/yukon/Icons/control_stop_blue.gif" class="pointer" title="Stop verification"
		                    				onclick="submitStop('${item.paoId}', '<spring:escapeBody javaScriptEscape="true">${item.deviceName}</spring:escapeBody>')">
										</c:if>
									</c:when>
									<c:otherwise>
		                    			<img src="/WebConfig/yukon/Icons/control_play_blue_disabled.gif" class="tierImage" title="${notAuthorizedText}">
		                    			<c:if test="${(item.commandName != confirmCommand)&&(item.commandName != sendTimeSyncsCommand)}">
		                    				<img src="/WebConfig/yukon/Icons/control_stop_blue_disabled.gif" class="tierImage" title="${notAuthorizedText}">
		                    			</c:if>
		                    		</c:otherwise>
		                    	</c:choose>
		                    </td>
							<!-- Last and Next Run Time -->
							<td><cti:formatDate value="${item.lastRunTime}" type="DATEHM" /></td>
							<td><cti:formatDate value="${item.nextRunTime}" type="DATEHM" /></td>
							<!-- Command -->
							<td><c:out value="${item.commandName}" /></td>
							<!-- Device -->
							<td><c:out value="${item.deviceName}" /></td>
							<td>
		                        <!-- Disable OvUv -->
		                        <c:choose>
			                        <c:when test="${(item.commandName == confirmCommand)||(item.commandName == sendTimeSyncsCommand)}">
			                        	N/A
			                        </c:when>
			                        <c:otherwise>
			                        <input type="checkbox" 
			                        	onclick="setOvUv(${item.eventId}, Number(!this.checked))" 
			                        	<c:if test="${item.disableOvUv == 'Y'}">checked</c:if> />
			                        </c:otherwise>
		                        </c:choose>
							</td>
						</tr>
					</c:forEach>
					</tbody>
				</table>
			</c:otherwise>
		</c:choose>
	</tags:pagedBox>
	
	</div>
	</td></tr></table>
	
</cti:standardPage>