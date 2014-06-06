<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>

<cti:msgScope paths="modules.capcontrol">

<c:choose>
    <c:when test="${searchResult.hitCount == 0}">
        <span class="empty-list"><i:inline key=".noScheduleAssignments" /></span>
    </c:when>
    
    <c:otherwise>
        <table class="compact-results-table has-actions" id="scheduledTable">
            
            <thead>
                <tr id="header">
                    <th><i:inline key=".schedule"/></th>
                    <th><i:inline key=".device"/></th>
                    <th><i:inline key=".lastRunTime"/></th>
                    <th><i:inline key=".nextRunTime"/></th>
                    <th><i:inline key=".scheduleAssignments.command"/></th>
                </tr>
            </thead>
            
            <tbody id="tableBody">
            <c:forEach var="item" items="${itemList}">
            
                <tr id="s_${item.eventId}_${item.paoId}">
                    <td name="schedName">${fn:escapeXml(item.scheduleName)}</td>
                    
                    <!-- Device -->
                    <td name="deviceName">${fn:escapeXml(item.deviceName)}</td>
                    
                    <!-- Last and Next Run Time -->
                    <td><cti:formatDate value="${item.lastRunTime}" type="DATEHM" /></td>
                    
                    <td><cti:formatDate value="${item.nextRunTime}" type="DATEHM" /></td>

                    <!-- Command -->
                    <td>${fn:escapeXml(item.commandName)}
                        <cm:dropdown triggerClasses="fr">
                            <c:choose>
                                <c:when test="${hasActionRoles == true}">
                                    <cm:dropdownOption icon="icon-control-play-blue" key="yukon.web.modules.capcontrol.play.label" href="buttons#drop-down-example"></cm:dropdownOption>
                                    <c:if test="${(item.commandName != confirmCommand)&&(item.commandName != sendTimeSyncsCommand)}">
                                        <cm:dropdownOption icon="icon-control-stop-blue" key="yukon.web.modules.capcontrol.stop.label" href="buttons#drop-down-example"></cm:dropdownOption>
                                    </c:if>
                                </c:when>
                                <c:otherwise>
                                    <cm:dropdownOption icon="icon-control-play-blue" disabled="disabled">${notAuthorizedText}</cm:dropdownOption>
                                    <c:if test="${(item.commandName != confirmCommand)&&(item.commandName != sendTimeSyncsCommand)}">
                                        <cm:dropdownOption icon="icon-control-stop-blue" disabled="disabled">${notAuthorizedText}</cm:dropdownOption>
                                    </c:if>
                                </c:otherwise>
                            </c:choose>
                            <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
                                <form id="removeAssignmentForm" action="<cti:url value="/capcontrol/schedule/removePao"/>" method="post">
                                    <cti:csrfToken/>
                                    <input type="hidden" name="eventId" value="${item.eventId}">
                                    <input type="hidden" name="paoId" value="${item.paoId}">
                                    <cm:dropdownOption id="removeAssignment" key="yukon.web.modules.capcontrol.substation.remove.label" icon="icon-cross"></cm:dropdownOption>
                                </form>
                                <cti:list var="argument">
                                    <cti:item value="${item.scheduleName}" />
                                    <cti:item value="${item.deviceName}" />
                                </cti:list>
                                <d:confirm on=".deleteAssignment" nameKey="confirmDelete" argument="${argument}" />
                            </cti:checkRolesAndProperties>
                            <!-- Disable OvUv -->
                            <c:if test="${(item.commandName != confirmCommand)&&(item.commandName != sendTimeSyncsCommand)}">
                                <c:choose>
                                    <c:when test="${item.disableOvUv == 'Y'}">
                                        <cm:dropdownOption icon="icon-accept" key=".scheduleAssignments.enableOvuv" classes="js-enable-ovuv"/>
                                    </c:when>
                                    <c:otherwise>
                                        <cm:dropdownOption icon="icon-delete" key=".scheduleAssignments.disableOvuv" classes="js-disable-ovuv"/>
                                    </c:otherwise>
                                </c:choose>
                            </c:if>
                        </cm:dropdown>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:otherwise>
</c:choose>

<c:set var="baseUrl" value="scheduleAssignmentsTable" />
<tags:pagingResultsControls baseUrl="${baseUrl}" result="${pagedResults}" adjustPageCount="${pagedResults.getHitCount() > 10 ? true : false}"/>

</cti:msgScope>