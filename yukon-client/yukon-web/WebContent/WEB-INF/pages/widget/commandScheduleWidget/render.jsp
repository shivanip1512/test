<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="yukon.web.widgets.commandScheduleWidget">
<c:choose>
    <c:when test="${empty schedules}">
        <div class="empty-list"><i:inline key=".noSchedules"/></div>
    </c:when>
    <c:otherwise>
        <table class="compact-results-table dashed">
            <thead>
                <tr>
                    <th><i:inline key=".start"/></th>
                    <th><i:inline key=".runPeriod"/></th>
                    <th><i:inline key=".delayPeriod"/></th>
                    <th></th>
                </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach var="schedule" items="${schedules}">
                    <tr>
                        <td>
                            <cti:url var="url" value="/stars/operator/inventory/commandSchedule">
                                <cti:param name="scheduleId" value="${schedule.commandScheduleId}" />
                            </cti:url>
                            <a href="${url}">
                                <span><cti:formatCron value="${schedule.startTimeCronString}"/></span>
                            </a>
                        </td>
                        
                        <td>
                            <span><cti:formatPeriod type="HM_SHORT" value="${schedule.runPeriod}"/></span>
                        </td>
                        
                        <td>
                            <span><cti:formatPeriod type="S" value="${schedule.delayPeriod}"/></span>
                        </td>
                        
                        <td>
                            <tags:switch checked="${schedule.enabled}" name="toggle" data-schedule-id="${schedule.commandScheduleId}" classes="js-toggle-schedule toggle-sm"/>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:otherwise>
</c:choose>

<cti:url value="/stars/operator/inventory/commandSchedule" var="createNewScheduleURL"/>
<div class="action-area">
    <form id="createNewScheduleForm_${widgetParameters.widgetId}" action="${createNewScheduleURL}" method="get">
        <tags:widgetActionRefresh nameKey="disableAll" method="disableAll"/>
        <input type="hidden" value="0" name="scheduleId">
        <cti:button nameKey="create" icon="icon-plus-green" type="submit"/>
    </form>
</div>
</cti:msgScope>