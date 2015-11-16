<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<c:choose>
    <c:when test="${not empty schedules && fn:length(schedules) > 0}">
        <select name="scheduleId" id="scheduleId">
            <c:forEach var="schedule" items="${schedules}">
                <option value="${schedule.scheduleID}">${fn:escapeXml(schedule.scheduleName)}</option>
            </c:forEach>
        </select>

        <div class="action-area">
            <tags:widgetActionUpdate method="downloadTouSchedule" nameKey="downloadSchedule" container="${widgetParameters.widgetId}_results"/>
        </div>
        <div id="${widgetParameters.widgetId}_results"></div>
    </c:when>
    
    <c:otherwise>
        <span class="empty-list"><i:inline key=".noSchedules"/></span>
    </c:otherwise>
</c:choose>