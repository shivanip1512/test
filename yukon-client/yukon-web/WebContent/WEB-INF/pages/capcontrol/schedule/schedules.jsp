<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="capcontrol"  page="schedules">

<script type="text/javascript">

jQuery(function () {
    jQuery('.removeSchedule').click(function () {
        var confirmDeleteMsg = jQuery(this).next('span.confirmDelete').html();
        if (confirm(confirmDeleteMsg)) {
            var scheduleId = jQuery(this).siblings('[id^=scheduleId_]').val();
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
                            <tr id="s_${item.scheduleID}">
                                <td class="wsnw">${fn:escapeXml(item.scheduleName)}</td>
                                
                                <td>
                                    <c:choose>
                                        <c:when test="${hasEditingRole}">
                                            <form id="scheduleForm_${item.scheduleID}" action="${action}" method="POST">
                                                <cti:button nameKey="edit" icon="icon-pencil" renderMode="image" href="/editor/cbcBase.jsf?type=3&itemid=${item.scheduleID}"/>
                                                <cti:button classes="removeSchedule" nameKey="remove" renderMode="image" icon="icon-cross"/>
                                                <span class="dn confirmDelete"><i:inline key=".confirmDelete" arguments="${item.scheduleName}"/></span>
                                                <input id="scheduleId_${item.scheduleID}" name="scheduleId" type="hidden" value="${item.scheduleID}">
                                            </form>
                                        </c:when>
                                        <c:otherwise>
                                            <cti:button nameKey="info" renderMode="image" href="/editor/cbcBase.jsf?type=3&itemid=${item.scheduleID}" icon="icon-information"/>
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