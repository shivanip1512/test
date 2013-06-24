<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
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

<cti:url var="baseUrl" value="/capcontrol/schedule/scheduleAssignments" />
<cti:url var="startMultiUrl" value="/capcontrol/schedule/startMultiple" />

<script type="text/javascript">
jQuery(function() {
    jQuery(document).on('click', 'button.runSchedule', function(event){
        var row = jQuery(event.currentTarget).closest('tr');
        var scheduleName = row.children('td[name=schedName]').html();
        var deviceName = row.children('td[name=deviceName]').html();
        var eventId = row[0].id.split('_')[1];
        
        jQuery.getJSON("/capcontrol/schedule/startSchedule", {
            'eventId': eventId, 
            'deviceName': deviceName
        }).done(function(json) {
            if (!json.success) {
                showAlertMessageForAction(scheduleName, '', json.resultText, 'red');
            } else {
                showAlertMessageForAction(scheduleName, '', json.resultText, 'green');
            }
        });
        
    });
});

jQuery(function() {
    jQuery(document).on('click', 'button.stopSchedule', function(event){
        var row = jQuery(event.currentTarget).closest('tr');
        
        var deviceName = row.children('td[name=deviceName]').html();
        var deviceId = jQuery(event.currentTarget).attr('name').always(function() {
            
        });
        
        jQuery.getJSON("/capcontrol/schedule/stopSchedule", {
            'deviceId': deviceId, 
            'deviceName': deviceName
        }).done(function(json) {
            if(!json.success) {
                showAlertMessageForAction('<cti:msg2 key=".stopSchedule" javaScriptEscape="true"/>', '', json.resultText, 'red');
            } else {
                showAlertMessageForAction('<cti:msg2 key=".stopSchedule" javaScriptEscape="true"/>', '', json.resultText, 'green');
            }
        });
        
    });
});

function setOvUv(eventId, ovuv) {
    jQuery.getJSON("/capcontrol/schedule/setOvUv", {
        'eventId': eventId, 
        'ovuv': ovuv
    }).done(function(json) {
        if (!json.success) {
            showAlertMessageForAction('OvUv', '', json.resultText, 'red');
        }
    });
}

function clearFilter() {
    window.location = '${baseUrl}';
}

function startMultiScheduleAssignmentPopup(schedule, command) {
    var url = '/capcontrol/schedule/startMultiScheduleAssignmentPopup';
    var title = '<cti:msg2 key=".play.label" javaScriptEscape="true"/>';
    var parameters = {'schedule': schedule, 'command': command};
    openSimpleDialog('contentPopup', url, title, parameters, 'get');
}

function stopMultiScheduleAssignmentPopup(schedule, command) {
    var url = '/capcontrol/schedule/stopMultiScheduleAssignmentPopup';
    var title = '<cti:msg2 key=".stop.label" javaScriptEscape="true"/>';
    var parameters = {'schedule': schedule, 'command': command};
    openSimpleDialog('contentPopup', url, title, parameters, 'get'); 
}

function newScheduleAssignmentPopup(schedule, command) {
    var url = '/capcontrol/schedule/newScheduleAssignmentPopup';
    var title = '<cti:msg2 key=".add.label" javaScriptEscape="true"/>';
    var parameters = {'schedule': schedule, 'command': command};
    openSimpleDialog('contentPopup', url, title, parameters, 'get'); 
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

    <div class="fl">
        <tags:boxContainer2 nameKey="actionsContainer">
            <ul class="buttonStack">
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

    <div class="clear padBottom">
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
                        
                            <tr id="s_${item.eventId}_${item.paoId}">
                                <td name="schedName"><spring:escapeBody htmlEscape="true">${item.scheduleName}</spring:escapeBody></td>
                                
                                <!-- Device -->
                                <td name="deviceName"><spring:escapeBody htmlEscape="true">${item.deviceName}</spring:escapeBody></td>
                                
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
                                            <img src="/WebConfig/yukon/Icons/control_play_blue_disabled.png" class="tierImage" title="${notAuthorizedText}">
                                            <c:if test="${(item.commandName != confirmCommand)&&(item.commandName != sendTimeSyncsCommand)}">
                                                <img src="/WebConfig/yukon/Icons/control_stop_blue_disabled.png" class="tierImage" title="${notAuthorizedText}">
                                            </c:if>
                                        </c:otherwise>
                                    </c:choose>
                                    <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
                                        <form id="removeAssignmentForm" action="/capcontrol/schedule/removePao" method="post">
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