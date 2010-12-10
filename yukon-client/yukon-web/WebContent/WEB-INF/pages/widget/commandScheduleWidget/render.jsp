<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<c:choose>
    <c:when test="${empty schedules}">
        <div>
            <i:inline key="yukon.web.widgets.commandScheduleWidget.noSchedules"/>
        </div>
    </c:when>

    <c:otherwise>
    
        <table class="compactResultsTable">
    
            <tr>
                <th><i:inline key="yukon.web.widgets.commandScheduleWidget.start"/></th>
                <th><i:inline key="yukon.web.widgets.commandScheduleWidget.runPeriod"/></th>
                <th><i:inline key="yukon.web.widgets.commandScheduleWidget.delayPeriod"/></th>
                <th class="enabledColumn"><i:inline key="yukon.web.widgets.commandScheduleWidget.enabled"/></th>
            </tr>
        
            <c:forEach var="schedule" items="${schedules}">
            
                <c:choose>
                    <c:when test="${schedule.enabled}">
                        <c:set var="toggleEnabled" value="disable"/>
                        <c:set var="toggleEnabledImg" value="/WebConfig/yukon/Icons/green_circle.gif"/>
                    </c:when>
                    <c:otherwise>
                        <c:set var="toggleEnabled" value="enable"/>
                        <c:set var="toggleEnabledImg" value="/WebConfig/yukon/Icons/gray_circle.gif"/>
                    </c:otherwise>
                </c:choose>
            
                <tr>
                    
                    <td>
                        <cti:url var="editUrl" value="/spring/stars/operator/inventory/inventoryOperations/commandSchedule">
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
                        <cti:msg2 key="yukon.web.widgets.commandScheduleWidget.${toggleEnabled}Title" var="enableTitle"/>
                        <tags:widgetActionRefreshImage method="${toggleEnabled}" imgSrc="${toggleEnabledImg}" imgSrcHover="${toggleEnabledImg}" scheduleId="${schedule.commandScheduleId}" title="${enableTitle}"/>
                    </td>
                    
                </tr>
            
            </c:forEach>
        
        </table>
    </c:otherwise>
</c:choose>

<div class="actionArea">
    <form id="createNewScheduleForm_${widgetParameters.widgetId}" action="/spring/stars/operator/inventory/inventoryOperations/commandSchedule" method="get">
        <cti:msg2 key="yukon.web.widgets.commandScheduleWidget.disableAll" var="disableAllLabel"/>
        <tags:widgetActionRefresh labelBusy="${disableAllLabel}" label="${disableAllLabel}" method="disableAll"/>
        <input type="hidden" value="0" name="scheduleId">
        <cti:button key="create" type="sumbit"/>
    </form>
</div>