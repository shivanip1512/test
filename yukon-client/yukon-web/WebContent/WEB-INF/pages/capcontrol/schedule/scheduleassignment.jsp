<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="capcontrol" page="scheduleAssignments">

<cti:linkTabbedContainer mode="section">
    <cti:linkTab selectorKey="yukon.web.modules.capcontrol.schedules.tab.title">
        <c:url value="/capcontrol/schedule/schedules" />
    </cti:linkTab>
    <cti:linkTab selectorKey="yukon.web.modules.capcontrol.scheduleAssignments.tab.title" initiallySelected="${true}">
        <c:url value="/capcontrol/schedule/scheduleAssignments" />
    </cti:linkTab>
</cti:linkTabbedContainer>

<jsp:setProperty name="CtiNavObject" property="moduleExitPage" value=""/>
<%@include file="/capcontrol/capcontrolHeader.jspf" %>
<%@include file="scheduleAssignmentFilterPopup.jsp" %>

<cti:includeScript link="/JavaScript/yukon.tables.js" />
<cti:includeScript link="/JavaScript/yukon.dialog.js"/>
<cti:includeScript link="/JavaScript/yukon.picker.js" />

<cti:uniqueIdentifier  prefix="addPao" var="addPao"/>
<cti:uniqueIdentifier var="addPaoSpanId" prefix="addPaoSpan_"/>

<cti:msg2 var="confirmCommand" key=".confirmCommand"/>
<cti:msg2 var="sendTimeSyncsCommand" key=".sendTimeSyncsCommand"/>

<c:set var="baseUrl" value="/capcontrol/schedule/scheduleAssignments"/>

<script type="text/javascript">
$(function() {
    $(document).on('click', 'button.runSchedule', function(event){
        var row = $(event.currentTarget).closest('tr');
        var scheduleName = row.children('td[name=schedName]').html();
        var deviceName = row.children('td[name=deviceName]').html();
        var eventId = row[0].id.split('_')[1];
        
        $.getJSON(yukon.url('/capcontrol/schedule/startSchedule'), {
            'eventId': eventId, 
            'deviceName': deviceName
        }).done(function(json) {
            if (!json.success) {
                yukon.da.showAlertMessageForAction(scheduleName, '', json.resultText, 'red');
            } else {
                yukon.da.showAlertMessageForAction(scheduleName, '', json.resultText, 'green');
            }
        });
        
    });
});

$(function() {
    $(document).on('click', 'button.stopSchedule', function(event){
        var row = $(event.currentTarget).closest('tr');
        
        var deviceName = row.children('td[name=deviceName]').html();
        var deviceId = $(event.currentTarget).attr('name');
        
        $.getJSON(yukon.url('/capcontrol/schedule/stopSchedule'), {
            'deviceId': deviceId, 
            'deviceName': deviceName
        }).done(function(json) {
            if(!json.success) {
                yukon.da.showAlertMessageForAction('<cti:msg2 key=".stopSchedule" javaScriptEscape="true"/>', '', json.resultText, 'red');
            } else {
                yukon.da.showAlertMessageForAction('<cti:msg2 key=".stopSchedule" javaScriptEscape="true"/>', '', json.resultText, 'green');
            }
        });
        
    });
});

function setOvUv(eventId, ovuv) {
    $.post(yukon.url('/capcontrol/schedule/setOvUv'), {
        'eventId': eventId, 
        'ovuv': ovuv
    }).done(function(json) {
        if (!json.success) {
            yukon.da.showAlertMessageForAction('OvUv', '', json.resultText, 'red');
        }
    });
}

function clearFilter() {
    window.location.href = yukon.url('/capcontrol/schedule/scheduleAssignments');
}

function startMultiScheduleAssignmentPopup(schedule, command) {
    var url = yukon.url('/capcontrol/schedule/startMultiScheduleAssignmentPopup');
    var title = '<cti:msg2 key=".play.label" javaScriptEscape="true"/>';
    var parameters = {'schedule': schedule, 'command': command};
    openSimpleDialog('contentPopup', url, title, parameters, 'get', {width: 500});
}

function stopMultiScheduleAssignmentPopup(schedule, command) {
    var url = yukon.url('/capcontrol/schedule/stopMultiScheduleAssignmentPopup');
    var title = '<cti:msg2 key=".stop.label" javaScriptEscape="true"/>';
    var parameters = {'schedule': schedule, 'command': command};
    openSimpleDialog('contentPopup', url, title, parameters, 'get', {width: 500}); 
}

function newScheduleAssignmentPopup(schedule, command) {
    var url = yukon.url('/capcontrol/schedule/newScheduleAssignmentPopup');
    var title = '<cti:msg2 key=".add.label" javaScriptEscape="true"/>';
    var parameters = {'schedule': schedule, 'command': command};
    openSimpleDialog('contentPopup', url, title, parameters, 'get', {width: 500}); 
}
</script>

<!-- Display success or failure message if a command was submitted -->
<c:if test="${param.success != null}">
    <c:choose>
        <c:when test="${param.success}">
            <script type="text/javascript">
                yukon.da.showAlertMessage('${cti:escapeJavaScript(param.resultText)}', 'green');
            </script>
        </c:when>
        <c:otherwise>
            <script type="text/javascript">
            yukon.da.showAlertMessage('${cti:escapeJavaScript(param.resultText)}', 'red');
            </script>
        </c:otherwise>
    </c:choose>
</c:if>

    <div class="fl">
        <tags:boxContainer2 nameKey="actionsContainer">
            <ul class="button-stack">
                <li>
                    <c:choose>
                        <c:when test="${hasActionRoles == true}">
                            <cti:button nameKey="play" renderMode="labeledImage" onclick="startMultiScheduleAssignmentPopup('${param.schedule}', '${param.command}');" icon="icon-control-play-blue"/>
                        </c:when>
                        <c:otherwise>
                            <cti:button nameKey="playDisabled" renderMode="labeledImage" icon="icon-control-play-blue"/>
                        </c:otherwise>
                    </c:choose>
                </li>
                <li>
                    <c:choose>
                        <c:when test="${hasActionRoles == true}">
                            <cti:button nameKey="stop" renderMode="labeledImage" onclick="stopMultiScheduleAssignmentPopup('${param.schedule}', '${param.command}');" icon="icon-control-stop-blue"/>
                        </c:when>
                        <c:otherwise>
                            <cti:button nameKey="stopDisabled" renderMode="labeledImage" icon="icon-control-stop-blue"/>
                        </c:otherwise>
                    </c:choose>
                </li>
                <li>
                    <c:choose>
                        <c:when test="${hasEditingRole == true}">
                            <cti:button nameKey="add" renderMode="labeledImage" onclick="newScheduleAssignmentPopup('${param.schedule}', '${param.command}');" icon="icon-add"/>
                        </c:when>
                        <c:otherwise>
                            <cti:button nameKey="addDisabled" renderMode="labeledImage" icon="icon-add" disabled="true"/>
                        </c:otherwise>
                    </c:choose>
                </li>
            </ul>
        </tags:boxContainer2>
    </div>

    <div class="clear">
        <tags:pagedBox2 nameKey="assignmentsContainer" 
                searchResult="${searchResult}"
                filterDialog="filterPopup" 
                baseUrl="${baseUrl}"
                isFiltered="${isFiltered}" 
                showAllUrl="${baseUrl}">
            <c:choose>
                <c:when test="${searchResult.hitCount == 0}">
                    <span class="empty-list"><i:inline key=".noScheduleAssignments" /></span>
                </c:when>
                
                <c:otherwise>
                    <table class="compact-results-table row-highlighting" id="scheduledTable">
                        
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
                        
                            <tr id="s_${item.eventId}_${item.paoId}">
                                <td name="schedName">${fn:escapeXml(item.scheduleName)}</td>
                                
                                <!-- Device -->
                                <td name="deviceName">${fn:escapeXml(item.deviceName)}</td>
                                
                                <td>
                                    <!-- Actions -->
                                    <c:choose>
                                        <c:when test="${hasActionRoles == true}">
                                            <cti:button classes="runSchedule" nameKey="runCommand" renderMode="image" icon="icon-control-play-blue"/>
                                            <c:if test="${(item.commandName != confirmCommand)&&(item.commandName != sendTimeSyncsCommand)}">
                                                <cti:button classes="stopSchedule" nameKey="stopVerification" renderMode="image" name="${item.paoId}" icon="icon-control-stop-blue"/>
                                            </c:if>
                                        </c:when>
                                        <c:otherwise>
                                            <cti:icon icon="icon-control-play-blue" disabled="disabled" title="${notAuthorizedText}"/>
                                            <c:if test="${(item.commandName != confirmCommand)&&(item.commandName != sendTimeSyncsCommand)}">
                                                <cti:icon icon="icon-control-stop-blue" disabled="disabled" title="${notAuthorizedText}"/>
                                            </c:if>
                                        </c:otherwise>
                                    </c:choose>
                                    <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
                                        <form id="removeAssignmentForm" action="<cti:url value="/capcontrol/schedule/removePao"/>" method="post">
                                            <cti:csrfToken/>
                                            <input type="hidden" name="eventId" value="${item.eventId}">
                                            <input type="hidden" name="paoId" value="${item.paoId}">
                                            <cti:button  type="submit" nameKey="remove" renderMode="image" classes="deleteAssignment" icon="icon-cross"/>
                                        </form>
                                        <cti:list var="argument">
                                            <cti:item value="${item.scheduleName}" />
                                            <cti:item value="${item.deviceName}" />
                                        </cti:list>
                                        <d:confirm on=".deleteAssignment" nameKey="confirmDelete" argument="${argument}" />
                                    </cti:checkRolesAndProperties>
                                </td>
                                
                                <!-- Last and Next Run Time -->
                                <td><cti:formatDate value="${item.lastRunTime}" type="DATEHM" /></td>
                                
                                <td><cti:formatDate value="${item.nextRunTime}" type="DATEHM" /></td>

                                <!-- Command -->
                                <td>${fn:escapeXml(item.commandName)}</td>
                                
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