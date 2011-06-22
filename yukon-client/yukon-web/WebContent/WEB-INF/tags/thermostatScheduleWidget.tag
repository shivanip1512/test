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

<cti:msg2 var="heatLabel"  key="yukon.dr.consumer.thermostat.mode.HEAT"/>
<cti:msg2 var="coolLabel"  key="yukon.dr.consumer.thermostat.mode.COOL"/>

<div class="schedule small ${pageScope.styleClass}" id="scheduleId_${pageScope.schedule.accountThermostatScheduleId}">
    <div class="">
        <form method="POST" action="${pageScope.actionPath}">
            <input type="hidden" name="thermostatId" value="${pageScope.thermostatId}">
            <input type="hidden" name="accountId" value="${pageScope.accountId}">
            <input type="hidden" name="thermostatIds" value="${pageScope.thermostatIds}">
            <input type="hidden" name="thermostatType" value="${pageScope.schedule.thermostatType}">
            <input type="hidden" name="scheduleId" value="${pageScope.schedule.accountThermostatScheduleId}">
            <input type="hidden" name="scheduleName" value="${pageScope.schedule.scheduleName}">
            <input type="hidden" name="scheduleMode" value="">
            <input type="hidden" name="schedules" value="">
            <input type="hidden" name="thermostatScheduleMode" value="${pageScope.schedule.thermostatScheduleMode}">
            <input type="hidden" name="temperatureUnit" value="${pageScope.temperatureUnit}">
            
            <div class="heading">
                <span class="title"><spring:escapeBody htmlEscape="true">${pageScope.schedule.scheduleName}</spring:escapeBody></span>
            </div>
            <div class="days fl">
                <span class="labels">
                    <label class="label fl"></label>
                    <c:forEach var="period" items="${pageScope.thermostatType.periodStyle.realPeriods}">
                        <div class="period">
                            <div class="info time">
                                <i:inline key="yukon.dr.consumer.thermostatSchedule.${period}" />
                            </div>
                            <div class="temp heat">
                                <i:inline key="yukon.dr.consumer.thermostat.mode.HEAT"/>
                            </div>
                            <div class="temp cool">
                                <i:inline key="yukon.dr.consumer.thermostat.mode.COOL"/>
                            </div>
                        </div>
                    </c:forEach>
                </span>
                
                <c:forEach var="day" items="${pageScope.schedule.entriesByTimeOfWeekMultimapAsMap}" varStatus="rowCounter">
                        <c:choose>
                          <c:when test="${rowCounter.count % 2 == 0}">
                            <c:set var="rowStyle" scope="page" value="odd"/>
                          </c:when>
                          <c:otherwise>
                            <c:set var="rowStyle" scope="page" value="even"/>
                          </c:otherwise>
                        </c:choose>
                        <div class="day active <tags:alternateRow even="even" odd="odd"/>">
                        <div class="periods">
                            <c:choose>
                                <c:when test="${pageScope.schedule.thermostatScheduleMode == 'ALL'}">
                                    <label class="label fl"><i:inline key="yukon.dr.consumer.thermostat.schedule.EVERYDAY_abbr" /></label>
                                </c:when>
                                <c:otherwise>
                                    <label class="label fl"><i:inline key="yukon.dr.consumer.thermostat.schedule.${day.key}_abbr" /></label>
                                </c:otherwise>
                            </c:choose>
                            <c:forEach var="period" items="${day.value}">
                                <!-- Comeup with a better way to determine mode -->
                                <c:if test="${period.heatTemp.value gt -1 }">
                                    <div class="period period_view">
                                        <input type="hidden" name="timeOfWeek" value="${day.key}">
                                        <div class="info time">
                                            <span class="time"></span>
                                            <input type="hidden" class="time" name="secondsFromMidnight" value="${period.startTime}">
                                        </div>
                                        <div class="temp heat ${pageScope.temperatureUnit}" title="${heatLabel}">
                                            <span class="value "></span><input type="hidden" value="${period.heatTemp.value}" name="heat_F"><i:inline key="yukon.web.defaults.degree"/>
                                        </div>
                                        <div class="temp cool ${pageScope.temperatureUnit}" title="${coolLabel}">
                                            <span class="value "></span><input type="hidden" value="${period.coolTemp.value}" name="cool_F"><i:inline key="yukon.web.defaults.degree"/>
                                        </div>
                                    </div>
                                </c:if>
                            </c:forEach>
                        </div>
                    </div>
                </c:forEach>
                
            </div>
            <div class="actions">
                <cti:button key="edit" renderMode="labeledImage" styleClass="edit edit_${pageScope.schedule.accountThermostatScheduleId}" />
                <cti:button key="sendNow" renderMode="labeledImage" styleClass="send" />
                <cti:button key="copy" renderMode="labeledImage" styleClass="copy copy_${pageScope.schedule.accountThermostatScheduleId}" />
            </div>
        </form>
    </div>
    <i:simplePopup  titleKey="yukon.web.modules.operator.thermostatSavedSchedules.editSchedule.title" 
                    arguments="${pageScope.schedule.scheduleName}" 
                    id="editSchedule_${pageScope.schedule.accountThermostatScheduleId}" 
                    on=".edit_${pageScope.schedule.accountThermostatScheduleId}, .copy_${pageScope.schedule.accountThermostatScheduleId}">
        <div class="container">
             <tags:thermostatScheduleEditor schedule="${pageScope.schedule}"
                                    thermostatId="${pageScope.thermostatId}"
                                    thermostatIds="${pageScope.thermostatIds}"
                                    accountId="${pageScope.accountId}"
                                    temperatureUnit="${pageScope.temperatureUnit}"
                                    actionPath="${pageScope.actionPath}"
                                    thermostatType="${pageScope.thermostatType}"
                                    styleClass="${pageScope.styleClass}"/>
        </div>
        <div class="actions">
            <div class="fr">
                <cti:button key="save"  styleClass="f_blocker save"/>
                <cti:button key="delete" styleClass="delete" />
                <cti:button key="cancel" styleClass="cancel" />
            </div>
            <div class="fl">
                <cti:button key="recommendedSettings" renderMode="labeledImage" styleClass="default"/>
            </div>
        </div>
    </i:simplePopup>
</div>