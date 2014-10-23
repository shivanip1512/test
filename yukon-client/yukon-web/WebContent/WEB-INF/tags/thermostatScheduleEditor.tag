<%@ tag body-content="empty" %>

<%@ attribute name="accountId" required="true" %>
<%@ attribute name="actionPath" required="true" description="Application context will be handled inside the tag. Just pass a bare url." %>
<%@ attribute name="schedule" required="true" type="com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule" %>
<%@ attribute name="temperatureUnit" required="true" %>
<%@ attribute name="thermostatId" required="true" %>
<%@ attribute name="thermostatIds" required="true" %>
<%@ attribute name="thermostatType" required="true" type="com.cannontech.stars.dr.hardware.model.SchedulableThermostatType"%>

<%@ attribute name="styleClass" required="false" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg2 var="coolLabel"  key="yukon.common.thermostat.mode.COOL"/>
<cti:msg2 var="heatLabel"  key="yukon.common.thermostat.mode.HEAT"/>

<div class="schedule editor ${pageScope.styleClass} ${schedule.thermostatScheduleMode}">
    
    <cti:url var="editUrl" value="${actionPath}"/>
    <form id="form_${schedule.accountThermostatScheduleId}" method="POST" action="${editUrl}" onsubmit="yukon.ThermostatScheduleEditor.prepForm(this);">
        <cti:csrfToken/>
        <input type="hidden" name="accountId" value="${accountId}">
        <input type="hidden" name="schedulableThermostatType" value="${thermostatType}">
        <input type="hidden" name="scheduleId" value="${schedule.accountThermostatScheduleId}">
        <input type="hidden" name="scheduleMode" value="">
        <input type="hidden" name="schedules" value="">
        <input type="hidden" name="thermostatId" value="${thermostatId}">
        <input type="hidden" name="thermostatIds" value="${thermostatIds}">
        <input type="hidden" name="thermostatScheduleMode" value="${schedule.thermostatScheduleMode}">
        
        <div class="box clearfix">
            <div class="box fl">
                <label for="scheduleName"><i:inline key="yukon.web.modules.operator.thermostat.schedules.name"/></label>
                <input type="text" name="scheduleName" value="${fn:escapeXml(schedule.scheduleName)}" initialValue="${fn:escapeXml(schedule.scheduleName)}" size="40" maxlength="60">
            </div>
            <div class="box fr tempLabel ${temperatureUnit}">
                <span class="F"><i:inline key="yukon.web.defaults.fahrenheit"/></span>
                <span class="C"><i:inline key="yukon.web.defaults.celsius"/></span>
            </div>
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
                                <label class="row-label fl"><i:inline key="yukon.web.components.thermostat.schedule.EVERYDAY_abbr"/></label>
                            </c:when>
                            <c:otherwise>
                                <label class="row-label fl"><i:inline key="yukon.web.components.thermostat.schedule.${row.key}_abbr"/></label>
                            </c:otherwise>
                        </c:choose>
                        <c:forEach var="period" items="${row.value}">
                        <!-- Comeup with a better way to determine mode -->
                            <c:choose>
                                <c:when test="${period.heatTemp.value gt -1}">
                                    <div class="period period_edit ${period.timeOfWeek}">
                                        <input type="hidden" name="timeOfWeek" value="${period.timeOfWeek}">
                                        <div class="info time">
                                            <input type="hidden" class="time"
                                                name="secondsFromMidnight"
                                                value="${period.startTime}"
                                                initialValue="${period.startTime}"
                                                defaultValue="${period.startTime}">
                                            <input type="text" maxlength="8">
                                        </div>

                                        <div class="temp heat ${pageScope.temperatureUnit}" title="${heatLabel}">
                                            <input type="text" class="heat_F"
                                                   maxlength="4" data-temperatureMode="HEAT"><i:inline key="yukon.web.defaults.degree"/>
                                            <input type="hidden"
                                                   value="${period.heatTemp.value}"
                                                   initialValue="${period.heatTemp.value}"
                                                   name="heat_F"
                                                   defaultValue="${period.heatTemp.value}">
                                        </div>
                                        <div class="temp cool ${pageScope.temperatureUnit}" title="${coolLabel}">
                                            <input type="text" class="cool_F"
                                                   maxlength="4" data-temperatureMode="COOL"><i:inline key="yukon.web.defaults.degree"/>
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
