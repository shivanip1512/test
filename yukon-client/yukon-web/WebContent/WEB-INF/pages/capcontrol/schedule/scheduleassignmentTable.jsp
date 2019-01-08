<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>

<cti:msgScope paths="modules.capcontrol">

<c:choose>
    <c:when test="${fn:length(assignments) == 0}">
        <span class="empty-list"><i:inline key=".scheduleAssignments.noScheduleAssignments" /></span>
    </c:when>
    
    <c:otherwise>
        <table class="compact-results-table has-actions" id="scheduledTable">
            
            <thead>
                <tr>
                    <th><i:inline key=".schedule"/></th>
                    <th><i:inline key=".device"/></th>
                    <th><i:inline key=".lastRunTime"/></th>
                    <th><i:inline key=".nextRunTime"/></th>
                    <th><i:inline key=".scheduleAssignments.command"/></th>
                    <th class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
                </tr>
            </thead>
            
            <tbody>
            <c:forEach var="item" items="${assignments}">
            
                <tr id="s_${item.eventId}_${item.paoId}">
                    <td name="schedName">${fn:escapeXml(item.scheduleName)}</td>
                    
                    <!-- Device -->
                    <td name="deviceName">${fn:escapeXml(item.deviceName)}</td>
                    
                    <!-- Last and Next Run Time -->
                    <td><cti:formatDate value="${item.lastRunTime}" type="DATEHM" /></td>
                    
                    <td><cti:formatDate value="${item.nextRunTime}" type="DATEHM" /></td>

                    <!-- Command -->
                    <td>${fn:escapeXml(item.commandName)}</td>
                    <td>
                        <cm:dropdown>
                            <c:if test="${hasActionRoles == true}">
                                <cm:dropdownOption classes="run-schedule" data-device-name="${fn:escapeXml(item.deviceName)}" 
                                    data-schedule-name="${fn:escapeXml(item.scheduleName)}" data-event-id="${item.eventId}" 
                                    icon="icon-control-play-blue" key="yukon.web.modules.capcontrol.play.label"></cm:dropdownOption>
                                <c:if test="${(item.commandName != confirmCommand)&&(item.commandName != sendTimeSyncsCommand)}">
                                    <cm:dropdownOption classes="stop-schedule" data-device-name="${fn:escapeXml(item.deviceName)}" 
                                        data-device-id="${item.paoId}" data-event-id="${item.eventId}" 
                                        icon="icon-control-stop-blue" key="yukon.web.modules.capcontrol.stop.label"></cm:dropdownOption>
                                </c:if>
                            </c:if>
                            <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
                                <li>
                                    <cti:csrfToken var="csrfToken"/>
                                    <a class="deleteAssignment" data-ok-event="yukon_da_schedules_remove" 
                                       data-csrf-token="${csrfToken}" data-pao-id="${item.paoId}" data-event-id="${item.eventId}">
                                        <cti:icon icon="icon-cross"/>
                                        <i:inline key=".substation.remove.label"/>
                                    </a>
                                    <cti:list var="confirmArgument">
                                        <cti:item value="${fn:escapeXml(item.scheduleName)}" />
                                        <cti:item value="${fn:escapeXml(item.deviceName)}" />
                                    </cti:list>
                                    <d:confirm on='[data-event-id="${item.eventId}"][data-pao-id="${item.paoId}"]' 
                                               nameKey="confirmDelete" argument="${confirmArgument}" />
                                </li>
                            </cti:checkRolesAndProperties>
                            <!-- Disable OvUv -->
                            <c:if test="${hasActionRoles == true}">
                                <c:if test="${(item.commandName != confirmCommand)&&(item.commandName != sendTimeSyncsCommand)}">
                                    <c:choose>
                                        <c:when test="${item.disableOvUv == 'Y'}">
                                            <cm:dropdownOption icon="icon-accept" value="${item.eventId}" key=".scheduleAssignments.enableOvuv" classes="js-enable-ovuv"/>
                                            <cm:dropdownOption icon="icon-delete" value="${item.eventId}" key=".scheduleAssignments.disableOvuv" classes="js-disable-ovuv dn"/>
                                        </c:when>
                                        <c:otherwise>
                                            <cm:dropdownOption icon="icon-delete" value="${item.eventId}" key=".scheduleAssignments.disableOvuv" classes="js-disable-ovuv"/>
                                            <cm:dropdownOption icon="icon-accept" value="${item.eventId}" key=".scheduleAssignments.enableOvuv" classes="js-enable-ovuv dn"/>
                                        </c:otherwise>
                                    </c:choose>
                                </c:if>
                            </c:if>
                        </cm:dropdown>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:otherwise>
</c:choose>

</cti:msgScope>