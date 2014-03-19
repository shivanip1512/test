<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="capcontrol"  page="schedules">

<cti:linkTabbedContainer mode="section">
    <cti:linkTab selectorKey="yukon.web.modules.capcontrol.schedules.tab.title"
                 initiallySelected='${true}'>
        <c:url value="/capcontrol/schedule/schedules" />
    </cti:linkTab>
    <cti:linkTab selectorKey="yukon.web.modules.capcontrol.scheduleAssignments.tab.title">
        <c:url value="/capcontrol/schedule/scheduleAssignments" />
    </cti:linkTab>
</cti:linkTabbedContainer>

<jsp:setProperty name="CtiNavObject" property="moduleExitPage" value=""/>
<%@include file="/capcontrol/capcontrolHeader.jspf" %>

<script type="text/javascript">

$(function () {
    $('.removeSchedule').click(function () {
        var confirmDeleteMsg = $(this).next('span.confirmDelete').html();
        if (confirm(confirmDeleteMsg)) {
            var scheduleId = $(this).siblings('[id^=scheduleId_]').val();
            document.getElementById("scheduleForm_" + scheduleId).submit();
        }
    });
});

</script>

<cti:url var="action" value="/capcontrol/schedule/deleteSchedule"/>

    <tags:pagedBox2 nameKey="scheduleContainer" 
            searchResult="${searchResult}"
            baseUrl="/capcontrol/schedule/schedules"
            isFiltered="false" 
            showAllUrl="/capcontrol/schedule/schedules"
            styleClass="padBottom">
        
        <c:choose>
        
            <c:when test="${searchResult.hitCount == 0}">
                <span class="empty-list"><i:inline key=".noSchedules"/></span>
            </c:when>
        
            <c:otherwise>
                <table class="compact-results-table" id="scheduleTable">
                
                    <thead>
                        <tr id="header">
                            <th><i:inline key=".schedule"/></th>
                            <th><i:inline key=".lastRunTime"/></th>
                            <th><i:inline key=".nextRunTime"/></th>
                            <th><i:inline key=".interval"/></th>
                            <th><i:inline key=".disabled"/></th>
                        </tr>
                    </thead>
                
                    <tbody id="tableBody">
                        <c:forEach var="item" items="${scheduleList}">
                            <tr id="s_${item.scheduleID}">
                                <td class="wsnw">
                                    <cti:url var="editUrl" value="/editor/cbcBase.jsf">
                                        <cti:param name="type" value="3"/>
                                        <cti:param name="itemid" value="${item.scheduleID}"/>
                                    </cti:url>
                                    <a href="${editUrl}">
                                        ${fn:escapeXml(item.scheduleName)}
                                    </a>
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
                                
                                <td>${fn:escapeXml(item.intervalRateString)}</td>
                                
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