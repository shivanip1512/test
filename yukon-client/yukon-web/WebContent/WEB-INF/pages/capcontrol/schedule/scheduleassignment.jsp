<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="capcontrol" page="scheduleAssignments">

<jsp:setProperty name="CtiNavObject" property="moduleExitPage" value=""/>
<%@include file="/capcontrol/capcontrolHeader.jspf" %>
<%@include file="scheduleAssignmentFilterPopup.jsp" %>

<cti:includeScript link="/JavaScript/tableCreation.js" />
<cti:includeScript link="/JavaScript/simpleDialog.js"/>
<cti:includeScript link="/JavaScript/picker.js" />

<cti:uniqueIdentifier  prefix="addPao" var="addPao"/>
<cti:uniqueIdentifier var="addPaoSpanId" prefix="addPaoSpan_"/>

<cti:msg2 var="confirmCommand" key=".confirmCommand"/>
<cti:msg2 var="sendTimeSyncsCommand" key=".sendTimeSyncsCommand"/>

<cti:url var="baseUrl" value="/spring/capcontrol/schedule/scheduleAssignments" />
<cti:url var="startMultiUrl" value="/spring/capcontrol/schedule/startMultiple" />

<form id="removeAssignmentForm" action="/spring/capcontrol/schedule/removePao" method="post">
    <input type="hidden" name="eventId">
</form> 
    
<script type="text/javascript">
YEvent.observeSelectorClick('button.deleteAssignment', function(event) {
    var row = event.findElement('tr');
    var eventId = row.id.split('_')[1];
    var confirmMsg = event.findElement().next('span.dn').innerHTML;
    if (confirm(confirmMsg)) {
        var url = "/spring/capcontrol/schedule/removePao?eventId=" + eventId;
        var removeForm = $('removeAssignmentForm');
        removeForm.down('input').value = eventId;
        removeForm.submit();
    }
});

YEvent.observeSelectorClick('button.runSchedule', function(event) {
    var row = event.findElement('tr');
    var scheduleNameCell = row.down('td');
    var scheduleName = scheduleNameCell.innerHTML;
    var deviceName = scheduleNameCell.next().innerHTML;
    var eventId = row.id.split('_')[1];
    var url = "/spring/capcontrol/schedule/startSchedule";
    new Ajax.Request(url, {'parameters': {'eventId': eventId, 'deviceName': deviceName},
        onComplete: function(transport, json) {
            if (!json.success) {
                showAlertMessageForAction(scheduleName, '', json.resultText, 'red');
            } else {
                showAlertMessageForAction(scheduleName, '', json.resultText, 'green');
            }
        } });
});

YEvent.observeSelectorClick('button.stopSchedule', function(event) {
    var row = event.findElement('tr');
    var deviceName = row.down('td', 1).innerHTML;
    var deviceId = event.findElement().name;
    var url = "/spring/capcontrol/schedule/stopSchedule";
    new Ajax.Request(url, {'parameters': {'deviceId': deviceId, 'deviceName': deviceName},
        onComplete: function(transport, json) {
            if(!json.success) {
                showAlertMessageForAction('<cti:msg2 key=".stopSchedule" javaScriptEscape="true"/>', '', json.resultText, 'red');
            } else {
                showAlertMessageForAction('<cti:msg2 key=".stopSchedule" javaScriptEscape="true"/>', '', json.resultText, 'green');
            }
    } });
});

function setOvUv(eventId, ovuv) {
    var url = "/spring/capcontrol/schedule/setOvUv";
    new Ajax.Request(url, {'parameters': {'eventId': eventId, 'ovuv': ovuv}, 
        onComplete: function(transport, json) {
            if (!json.success) {
                showAlertMessageForAction('OvUv', '', json.resultText, 'red');
            }
        } 
    });
}

function clearFilter() {
    window.location = '${baseUrl}';
}

function startMultiScheduleAssignmentPopup(schedule, command) {
    var url = '/spring/capcontrol/schedule/startMultiScheduleAssignmentPopup';
    var title = '<cti:msg2 key=".play.label" javaScriptEscape="true"/>';
    var parameters = {'schedule': schedule, 'command': command};
    openSimpleDialog('contentPopup', url, title, parameters, null, 'get');
    $('contentPopup').toggle(); 
}

function stopMultiScheduleAssignmentPopup(schedule, command) {
    var url = '/spring/capcontrol/schedule/stopMultiScheduleAssignmentPopup';
    var title = '<cti:msg2 key=".stop.label" javaScriptEscape="true"/>';
    var parameters = {'schedule': schedule, 'command': command};
    openSimpleDialog('contentPopup', url, title, parameters, null, 'get'); 
}

function newScheduleAssignmentPopup(schedule, command) {
    var url = '/spring/capcontrol/schedule/newScheduleAssignmentPopup';
    var title = '<cti:msg2 key=".add.label" javaScriptEscape="true"/>';
    var parameters = {'schedule': schedule, 'command': command};
    openSimpleDialog('contentPopup', url, title, parameters, null, 'get'); 
}
</script>
    
<!-- Display success or failure message if a command was submitted -->
<c:if test="${param.success != null}">
    <c:choose>
        <c:when test="${param.success}">
            <script type="text/javascript">
                showAlertMessage('<spring:escapeBody javaScriptEscape="true">${param.resultText}</spring:escapeBody>', 'green');
            </script>
        </c:when>
        <c:otherwise>
            <script type="text/javascript">
                showAlertMessage('<spring:escapeBody javaScriptEscape="true">${param.resultText}</spring:escapeBody>', 'red');
            </script>
        </c:otherwise>
    </c:choose>
</c:if>

    <div class="fl padBottom">
        <tags:boxContainer2 nameKey="actionsContainer">
            <ul class="buttonStack">
                <li>
                    <c:choose>
                        <c:when test="${hasActionRoles == true}">
                            <cti:button nameKey="play" renderMode="labeledImage" onclick="startMultiScheduleAssignmentPopup('${param.schedule}', '${param.command}');"/>
                        </c:when>
                        <c:otherwise>
                            <cti:button nameKey="playDisabled" renderMode="labeledImage"/>
                        </c:otherwise>
                    </c:choose>
                </li>
                <li>
                    <c:choose>
                        <c:when test="${hasActionRoles == true}">
                            <cti:button nameKey="stop" renderMode="labeledImage" onclick="stopMultiScheduleAssignmentPopup('${param.schedule}', '${param.command}');"/>
                        </c:when>
                        <c:otherwise>
                            <cti:button nameKey="stopDisabled" renderMode="labeledImage"/>
                        </c:otherwise>
                    </c:choose>
                </li>
                <li>
                    <c:choose>
                        <c:when test="${hasEditingRole == true}">
                            <cti:button nameKey="add" renderMode="labeledImage" onclick="newScheduleAssignmentPopup('${param.schedule}', '${param.command}');"/>
                        </c:when>
                        <c:otherwise>
                            <cti:button nameKey="addDisabled" renderMode="labeledImage"/>
                        </c:otherwise>
                    </c:choose>
                </li>
            </ul>
        </tags:boxContainer2>
    </div>

    <div class="clear padBottom">
        <tags:pagedBox2 nameKey="assignmentsContainer" 
                searchResult="${searchResult}"
                filterDialog="filterPopup" 
                baseUrl="${baseUrl}"
                isFiltered="${isFiltered}" 
                showAllUrl="${baseUrl}">
            <c:choose>
                <c:when test="${searchResult.hitCount == 0}">
                    <i:inline key=".noScheduleAssignments" />
                </c:when>
                
                <c:otherwise>
                    <table class="compactResultsTable rowHighlighting" id="scheduledTable">
                        
                        <thead>
                            <tr id="header">
                                <th><i:inline key=".schedule"/></th>
                                <th><i:inline key=".device"/></th>
                                <th><i:inline key=".actions"/></th>
                                <th><i:inline key=".lastRunTime"/></th>
                                <th><i:inline key=".nextRunTime"/></th>
                                <th><i:inline key=".command"/></th>
                                <th><i:inline key=".disableOvuv"/></th>
                            </tr>
                        </thead>
                        
                        <tbody id="tableBody">
                        <c:forEach var="item" items="${itemList}">
                        
                            <tr class="<tags:alternateRow odd="" even="altRow"/>" id="s_${item.eventId}">
                                <td><spring:escapeBody htmlEscape="true">${item.scheduleName}</spring:escapeBody></td>
                                
                                <!-- Device -->
                                <td><spring:escapeBody htmlEscape="true">${item.deviceName}</spring:escapeBody></td>
                                
                                <td>
                                    <!-- Actions -->
                                    <c:choose>
                                        <c:when test="${hasActionRoles == true}">
                                            <cti:button styleClass="runSchedule" nameKey="runCommand" renderMode="image"/>
                                            <c:if test="${(item.commandName != confirmCommand)&&(item.commandName != sendTimeSyncsCommand)}">
                                                <cti:button styleClass="stopSchedule" nameKey="stopVerification" renderMode="image" name="${item.paoId}"/>
                                            </c:if>
                                        </c:when>
                                        <c:otherwise>
                                            <img src="/WebConfig/yukon/Icons/control_play_blue_disabled.png" class="tierImage" title="${notAuthorizedText}">
                                            <c:if test="${(item.commandName != confirmCommand)&&(item.commandName != sendTimeSyncsCommand)}">
                                                <img src="/WebConfig/yukon/Icons/control_stop_blue_disabled.png" class="tierImage" title="${notAuthorizedText}">
                                            </c:if>
                                        </c:otherwise>
                                    </c:choose>
                                    <cti:checkProperty property="CBCSettingsRole.CBC_DATABASE_EDIT">
                                        <cti:button nameKey="remove" renderMode="image" styleClass="deleteAssignment"/>
                                        <span class="dn"><i:inline key=".confirmDeleteMsg" arguments="${item.scheduleName},${item.deviceName}" argumentSeparator=","/></span>
                                    </cti:checkProperty>
                                </td>
                                
                                <!-- Last and Next Run Time -->
                                <td><cti:formatDate value="${item.lastRunTime}" type="DATEHM" /></td>
                                
                                <td><cti:formatDate value="${item.nextRunTime}" type="DATEHM" /></td>

                                <!-- Command -->
                                <td><spring:escapeBody htmlEscape="true">${item.commandName}</spring:escapeBody></td>
                                
                                <td>
                                    <!-- Disable OvUv -->
                                    <c:choose>
                                        <c:when test="${(item.commandName == confirmCommand)||(item.commandName == sendTimeSyncsCommand)}">
                                            <i:inline key="yukon.web.defaults.na"/>
                                        </c:when>
                                        <c:otherwise>
                                            <input type="checkbox" onclick="setOvUv(${item.eventId}, Number(!this.checked))" <c:if test="${item.disableOvUv == 'Y'}">checked</c:if> />
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </tags:pagedBox2>
    </div>
</cti:standardPage>