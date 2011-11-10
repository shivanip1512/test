<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="capcontrol"  page="schedules">

<script type="text/javascript">
function removeSchedule(scheduleId, event) {
    var confirmDeleteMsg = event.findElement().next('span.confirmDelete').innerHTML;
    if (confirm(confirmDeleteMsg)) {
        var url = "/spring/capcontrol/schedule/deleteSchedule?scheduleId=" + scheduleId;
        window.location = url;
    }
}
</script>

    <tags:pagedBox2 nameKey="scheduleContainer" 
            searchResult="${searchResult}"
            baseUrl="/spring/capcontrol/schedule/schedules"
            isFiltered="false" 
            showAllUrl="/spring/capcontrol/schedule/schedules"
            styleClass="padBottom">
        
        <c:choose>
        
            <c:when test="${searchResult.hitCount == 0}">
                <i:inline key=".noSchedules"/>
            </c:when>
        
            <c:otherwise>
                <table class="compactResultsTable" id="scheduleTable">
                
                    <thead>
                        <tr id="header">
                            <th><i:inline key=".schedule"/></th>
                            <th><i:inline key=".actions"/></th>
                            <th><i:inline key=".lastRunTime"/></th>
                            <th><i:inline key=".nextRunTime"/></th>
                            <th><i:inline key=".interval"/></th>
                            <th><i:inline key=".disabled"/></th>
                        </tr>
                    </thead>
                
                    <tbody id="tableBody">
                        <c:forEach var="item" items="${scheduleList}">
                            <tr class="<tags:alternateRow odd="" even="altRow"/>" id="s_${item.scheduleID}">
                                
                                <td nowrap="nowrap">
                                    <spring:escapeBody htmlEscape="true">${item.scheduleName}</spring:escapeBody>
                                </td>
                                
                                <td>
                                    <c:choose>
                                        <c:when test="${hasEditingRole}">
                                            <cti:button nameKey="edit" renderMode="image" href="/editor/cbcBase.jsf?type=3&itemid=${item.scheduleID}"/>
                                            <cti:button nameKey="remove" renderMode="image" onclick="removeSchedule(${item.scheduleID}, event)"/>
                                            <span class="dn confirmDelete"><i:inline key=".confirmDelete" arguments="${item.scheduleName}"/></span>
                                        </c:when>
                                        <c:otherwise>
                                            <cti:button nameKey="info" renderMode="image" href="/editor/cbcBase.jsf?type=3&itemid=${item.scheduleID}"/>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                
                                <td>
                                    <c:choose>
                                        <c:when test="${item.lastRunTime.time <= startOfTime}">
                                            <i:inline key="yukon.web.defaults.dashes"/>
                                        </c:when>
                                        <c:otherwise>
                                            <cti:formatDate value="${item.lastRunTime}" type="DATEHM" />
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                
                                <td>
                                    <c:choose>
                                        <c:when test="${item.nextRunTime.time <= startOfTime}">
                                            <i:inline key="yukon.web.defaults.dashes"/>
                                        </c:when>
                                        <c:otherwise>
                                            <cti:formatDate value="${item.nextRunTime}" type="DATEHM" />
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                
                                <td>
                                    <spring:escapeBody htmlEscape="true">${item.intervalRateString}</spring:escapeBody>
                                </td>
                                
                                <td>
                                    <c:choose>
                                        <c:when test="${item.disabled}">
                                            <i:inline key="yukon.web.defaults.yes"/>
                                        </c:when>
                                        <c:otherwise>
                                             <i:inline key="yukon.web.defaults.no"/>
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
    
</cti:standardPage>