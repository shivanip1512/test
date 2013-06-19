<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<c:choose>
    <c:when test="${empty schedules}">
        <div class="empty-list">
            <i:inline key="yukon.web.widgets.commandScheduleWidget.noSchedules"/>
        </div>
    </c:when>
    <c:otherwise>
        <table class="compactResultsTable">
            <thead>
                <tr>
                    <th><i:inline key="yukon.web.widgets.commandScheduleWidget.start"/></th>
                    <th><i:inline key="yukon.web.widgets.commandScheduleWidget.runPeriod"/></th>
                    <th><i:inline key="yukon.web.widgets.commandScheduleWidget.delayPeriod"/></th>
                    <th class="enabledColumn"><i:inline key="yukon.web.widgets.commandScheduleWidget.enabled"/></th>
                </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach var="schedule" items="${schedules}">
                    <tr>
                        <td>
                            <cti:url var="editUrl" value="/stars/operator/inventory/commandSchedule">
                                <cti:param name="scheduleId" value="${schedule.commandScheduleId}" />
                            </cti:url>
                            <a href="${editUrl}">
                                <span><cti:formatCron value="${schedule.startTimeCronString}"/></span>
                            </a>
                        </td>
                        
                        <td>
                            <span><cti:formatPeriod type="HM_SHORT" value="${schedule.runPeriod}"/></span>
                        </td>
                        
                        <td>
                            <span><cti:formatPeriod type="S" value="${schedule.delayPeriod}"/></span>
                        </td>
                        
                        <td class="enabledColumn">
                            <c:choose>
                                <c:when test="${schedule.enabled}">
                                    <tags:widgetActionRefreshImage method="disable" nameKey="disable" scheduleId="${schedule.commandScheduleId}" icon="icon-enabled" btnClass="fr"/>
                                </c:when>
                                <c:when test="${not schedule.enabled}">
                                    <tags:widgetActionRefreshImage method="enable" nameKey="enable" scheduleId="${schedule.commandScheduleId}" icon="icon-disabled" btnClass="fr"/> 
                                </c:when>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:otherwise>
</c:choose>

<div class="actionArea">
    <form id="createNewScheduleForm_${widgetParameters.widgetId}" action="/stars/operator/inventory/commandSchedule" method="get">
        <tags:widgetActionRefresh nameKey="disableAll" method="disableAll"/>
        <input type="hidden" value="0" name="scheduleId">
        <cti:button nameKey="create" icon="icon-plus-green" type="submit"/>
    </form>
</div>