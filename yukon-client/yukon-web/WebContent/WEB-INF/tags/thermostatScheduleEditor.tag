<%@ attribute name="schedule" required="true" type="com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule" %>
<%@ attribute name="thermostatType" required="true" type="com.cannontech.stars.dr.hardware.model.SchedulableThermostatType"%>
<%@ attribute name="thermostatId" required="true" type="java.lang.String"%>
<%@ attribute name="thermostatIds" required="true" type="java.lang.String"%>
<%@ attribute name="accountId" required="true" type="java.lang.String"%>
<%@ attribute name="temperatureUnit" required="true" type="java.lang.String"%>
<%@ attribute name="actionPath" required="true" type="java.lang.String"%>
<%@ attribute name="styleClass" required="false" type="java.lang.String"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:msg2 var="heatLabel"  key="yukon.common.thermostat.mode.HEAT"/>
<cti:msg2 var="coolLabel"  key="yukon.common.thermostat.mode.COOL"/>

<div class="schedule_editor ${pageScope.styleClass} ${pageScope.schedule.thermostatScheduleMode}">
    <form id="form_${pageScope.schedule.accountThermostatScheduleId}" method="POST" action="${actionPath}" onsubmit="Yukon.ThermostatScheduleEditor.prepForm(this);">
        <input type="hidden" name="accountId" value="${pageScope.accountId}">
        <input type="hidden" name="thermostatId" value="${pageScope.thermostatId}">
        <input type="hidden" name="thermostatIds" value="${pageScope.thermostatIds}">
        <input type="hidden" name="schedulableThermostatType" value="${pageScope.schedule.thermostatType}">
        <input type="hidden" name="scheduleId" value="${pageScope.schedule.accountThermostatScheduleId}">
        <input type="hidden" name="scheduleMode" value="">
        <input type="hidden" name="schedules" value="">
        <input type="hidden" name="thermostatScheduleMode" value="${pageScope.schedule.thermostatScheduleMode}">
        
        <div class="temp tempLabel ${temperatureUnit}">
            <span class="F"><i:inline key="yukon.web.defaults.fahrenheit"/></span>
            <span class="C"><i:inline key="yukon.web.defaults.celsius"/></span>
        </div>
        <div class="box fl" style="height:2.5em;">
            <label for="scheduleName"><i:inline key="yukon.web.modules.operator.thermostat.schedules.name"/></label>
        </div>
        <input type="text" name="scheduleName" value="<spring:escapeBody htmlEscape="true">${pageScope.schedule.scheduleName}</spring:escapeBody>" initialValue="<spring:escapeBody htmlEscape="true">${pageScope.schedule.scheduleName}</spring:escapeBody>" size="40" maxlength="60">

        <div class="days fl">
            <span class="labels">
                <label class="label fl"></label>
                <c:forEach var="period" items="${pageScope.thermostatType.periodStyle.realPeriods}">
                    <div class="period">
                        <div class="period">
                            <div class="info time">
                                <i:inline key="yukon.web.components.thermostat.period.${period}" />
                            </div>
                            <div class="temp heat">
                                <i:inline key="yukon.common.thermostat.mode.HEAT"/>
                            </div>
                            <div class="temp cool">
                                <i:inline key="yukon.common.thermostat.mode.COOL"/>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </span>
            <c:forEach var="day" items="${pageScope.schedule.entriesByTimeOfWeekMultimapAsMap}">
                <div class="day active">
                    <div class="periods">
                        <c:choose>
                            <c:when test="${schedule.thermostatScheduleMode == 'ALL'}">
                                <label class="label fl"><i:inline key="yukon.web.components.thermostat.schedule.EVERYDAY_abbr" /></label>
                            </c:when>
                            <c:otherwise>
                                <label class="label fl"><i:inline key="yukon.web.components.thermostat.schedule.${day.key}_abbr" /></label>
                            </c:otherwise>
                        </c:choose>
                        <c:forEach var="period" items="${day.value}">
                        <!-- Comeup with a better way to determine mode -->
                            <c:choose>
                                <c:when test="${period.heatTemp.value gt -1}">
                                    <div class="period period_edit ${period.timeOfWeek}">
                                        <input type="hidden" name="timeOfWeek"
                                            value="${period.timeOfWeek}">
                                        <div class="info time">
                                            <input type="hidden" class="time"
                                                name="secondsFromMidnight"
                                                value="${period.startTime}"
                                                initialValue="${period.startTime}"
                                                defaultValue="${period.startTime}">
                                            <input type="text" class="time f_selectAll"
                                                maxlength="8">
                                        </div>

                                        <div
                                            class="temp heat ${pageScope.temperatureUnit}"
                                            title="${heatLabel}">
                                            <input type="text" 
                                                   class="heat_F f_selectAll"
                                                   maxlength="4"><i:inline key="yukon.web.defaults.degree"/>
                                            <input
                                                type="hidden"
                                                value="${period.heatTemp.value}"
                                                initialValue="${period.heatTemp.value}"
                                                name="heat_F"
                                                defaultValue="${period.heatTemp.value}">
                                        </div>
                                        <div
                                            class="temp cool ${pageScope.temperatureUnit}"
                                            title="${coolLabel}">
                                            <input type="text"
                                                   class="cool_F f_selectAll"
                                                   maxlength="4"><i:inline key="yukon.web.defaults.degree"/>
                                            <input type="hidden"
                                                   value="${period.coolTemp.value}" 
                                                   initialValue="${period.coolTemp.value}" 
                                                   name="cool_F" 
                                                   defaultValue="${period.coolTemp.value}">
                                        </div>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="period dn ${period.timeOfWeek}">
                                        <input type="hidden"
                                               name="timeOfWeek"
                                               value="${period.timeOfWeek}">
                                        <input type="hidden" 
                                               class="time" 
                                               name="secondsFromMidnight" 
                                               value="${period.startTime}"
                                               initialValue="${period.startTime}"
                                                defaultValue="${period.startTime}">
                                        <input type="hidden" 
                                               value="${period.heatTemp.value}"
                                               initialValue="${period.heatTemp.value}"
                                               name="heat_F"
                                               defaultValue="${period.heatTemp.value}">
                                        <input type="hidden"
                                               value="${period.coolTemp.value}"
                                               initialValue="${period.coolTemp.value}" 
                                               name="cool_F" 
                                               defaultValue="${period.coolTemp.value}">
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </div>
                </div>
            </c:forEach>
        </div>
    </form>
</div>
