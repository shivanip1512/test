<%@ tag trimDirectiveWhitespaces="true" %>

<%@ attribute name="accountId" required="true" %>
<%@ attribute name="actionPath" required="true" description="Application context will be handled inside the tag. Just pass a bare url." %>
<%@ attribute name="schedule" required="true" type="com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule" %>
<%@ attribute name="temperatureUnit" required="true" %>
<%@ attribute name="thermostatId" required="true" %>
<%@ attribute name="thermostatIds" required="true" %>
<%@ attribute name="thermostatType" required="true" type="com.cannontech.stars.dr.hardware.model.SchedulableThermostatType" %>

<%@ attribute name="customActions" type="java.lang.Boolean" %>
<%@ attribute name="omitEditor" type="java.lang.Boolean" %>
<%@ attribute name="styleClass" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg2 var="coolLabel"  key="yukon.common.thermostat.mode.COOL"/>
<cti:msg2 var="heatLabel"  key="yukon.common.thermostat.mode.HEAT"/>

<cti:msgScope paths="modules.operator.thermostat.schedules">
<div class="schedule ${pageScope.styleClass}" id="scheduleId_${schedule.accountThermostatScheduleId}">

    <cti:url var="saveUrl" value="${actionPath}/save"/>
    <form method="POST" action="${saveUrl}">
        <cti:csrfToken/>
        <input type="hidden" name="accountId" value="${accountId}">
        <input type="hidden" name="scheduleId" value="${schedule.accountThermostatScheduleId}">
        <input type="hidden" name="scheduleName" value="${fn:escapeXml(schedule.scheduleName)}">
        <input type="hidden" name="temperatureUnit" value="${temperatureUnit}">
        <input type="hidden" name="thermostatId" value="${thermostatId}">
        <input type="hidden" name="thermostatIds" value="${thermostatIds}">
        <input type="hidden" name="thermostatScheduleMode" value="${schedule.thermostatScheduleMode}">
        
        <div class="heading">
            <h4>${fn:escapeXml(schedule.scheduleName)}</h4>
        </div>
        <div class="rows striped-list">
            <span class="labels">
                <c:forEach var="period" items="${thermostatType.periodStyle.realPeriods}">
                    <div class="period">
                        <div class="time-label">
                            <i:inline key="yukon.web.components.thermostat.period.${period}" />
                        </div>
                        <div class="temp-label">
                            <i:inline key="yukon.common.thermostat.mode.HEAT"/>
                        </div>
                        <div class="temp-label">
                            <i:inline key="yukon.common.thermostat.mode.COOL"/>
                        </div>
                    </div>
                </c:forEach>
            </span>
            
            <c:forEach var="row" items="${schedule.entriesByTimeOfWeekMultimapAsMap}">
                <div class="row active clearfix">
                    <div class="periods">
                        <c:choose>
                            <c:when test="${schedule.thermostatScheduleMode == 'ALL'}">
                                <label class="row-label fl"><i:inline key="yukon.web.components.thermostat.schedule.EVERYDAY_abbr" /></label>
                            </c:when>
                            <c:otherwise>
                                <label class="row-label fl"><i:inline key="yukon.web.components.thermostat.schedule.${row.key}_abbr" /></label>
                            </c:otherwise>
                        </c:choose>
                        <c:forEach var="period" items="${row.value}">
                            <!-- Comeup with a better way to determine mode -->
                            <c:if test="${period.heatTemp.value gt -1 }">
                                <div class="period period_view">
                                    <input type="hidden" name="timeOfWeek" value="${row.key}">
                                    <div class="info time">
                                        <span class="time"></span>
                                        <input type="hidden" class="time" name="secondsFromMidnight" value="${period.startTime}">
                                    </div>
                                    <div class="temp heat ${temperatureUnit}" title="${heatLabel}">
                                        <span class="value "></span><input type="hidden" value="${period.heatTemp.value}" name="heat_F"><i:inline key="yukon.common.degree"/>
                                    </div>
                                    <div class="temp cool ${temperatureUnit}" title="${coolLabel}">
                                        <span class="value "></span><input type="hidden" value="${period.coolTemp.value}" name="cool_F"><i:inline key="yukon.common.degree"/>
                                    </div>
                                </div>
                            </c:if>
                        </c:forEach>
                    </div>
                </div>
            </c:forEach>
            
        </div>
        <c:choose>
            <c:when test="${pageScope.customActions}">
                <jsp:doBody/>
            </c:when>
            <c:otherwise>
                <div class="actions page-action-area">
                    <cti:button nameKey="edit" icon="icon-pencil" classes="js-edit edit_${schedule.accountThermostatScheduleId}" />
                    <cti:button nameKey="sendNow" classes="js-send" icon="icon-date-go" data-form="#send_${schedule.accountThermostatScheduleId}"/>
                    <cti:button nameKey="copy" classes="js-copy copy_${schedule.accountThermostatScheduleId}" icon="icon-page-copy"/>
                </div>
            </c:otherwise>
        </c:choose>
    </form>
    <cti:url var="sendUrl" value="${actionPath}/send"/>
    <form id="send_${schedule.accountThermostatScheduleId}" action="${sendUrl}" method="POST" class="dn">
        <cti:csrfToken/>
        <input type="hidden" name="scheduleId" value="${schedule.accountThermostatScheduleId}">
        <input type="hidden" name="thermostatIds" value="${thermostatIds}">
        <input type="hidden" name="accountId" value="${accountId}">
        <input type="hidden" name="temperatureUnit" value="${temperatureUnit}">
        <d:confirm on=".js-send" nameKey="sendConfirm" argument="${fn:escapeXml(schedule.scheduleName)}"/>
    </form>
    <c:if test="${empty pageScope.omitEditor or not pageScope.omitEditor}">
        <i:simplePopup titleKey=".editSchedule.title" 
                       arguments="${fn:escapeXml(schedule.scheduleName)}" 
                       id="editSchedule_${schedule.accountThermostatScheduleId}" 
                       on=".edit_${schedule.accountThermostatScheduleId}, .copy_${schedule.accountThermostatScheduleId}">
                       
                        <cti:msg2 var="copyPrefix" key="yukon.common.copy.prefix"/>
                        <input type="hidden" name="copyName" value="${copyPrefix} ${fn:escapeXml(schedule.scheduleName)}"/>
                        <input type="hidden" name="copyTitle" value="<i:inline key=".createSchedule.title"/>"/>
                        <input type="hidden" name="editTitle" value="<i:inline key=".editSchedule.title" arguments="${fn:escapeXml(schedule.scheduleName)}"/>"/>
                        
            <div class="container clearfix">
                 <tags:thermostatScheduleEditor schedule="${schedule}"
                                        thermostatId="${thermostatId}"
                                        thermostatIds="${thermostatIds}"
                                        accountId="${accountId}"
                                        temperatureUnit="${temperatureUnit}"
                                        actionPath="${actionPath}/save"
                                        thermostatType="${thermostatType}"
                                        styleClass="${pageScope.styleClass}"/>
            </div>
            <div class="action-area">
                <cti:button nameKey="save" classes="js-blocker primary action js-save"/>
                <cti:url var="deleteUrl" value="${actionPath}/delete"/>
                <form action="${deleteUrl}" method="POST">
                    <cti:csrfToken/>
                    <input type="hidden" name="scheduleId" value="${schedule.accountThermostatScheduleId}">
                    <input type="hidden" name="thermostatId" value="${thermostatId}">
                    <input type="hidden" name="thermostatIds" value="${thermostatIds}">
                    <input type="hidden" name="accountId" value="${accountId}">
                    <cti:button nameKey="delete" classes="js-delete delete" name="delete" type="submit"/>
                    <d:confirm on=".js-delete" nameKey="deleteConfirm" argument="${fn:escapeXml(schedule.scheduleName)}"/>
                </form>
                <cti:button nameKey="cancel" classes="js-cancel"/>
                <cti:button nameKey="recommendedSettings" renderMode="labeledImage" classes="fl js-default" icon="icon-wrench"/>
            </div>
        </i:simplePopup>
    </c:if>
</div>
</cti:msgScope>