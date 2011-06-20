<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
    
<cti:standardPage module="operator" page="thermostatSavedSchedules">

<cti:includeCss link="/WebConfig/yukon/styles/thermostat.css"/>

<cti:msg var="timeFormatter" key="yukon.common.timeFormatter" />
<cti:includeScript link="${timeFormatter}"/>
<cti:includeScript link="/JavaScript/thermostatScheduleEditor.js"/>
<cti:includeScript link="/JavaScript/lib/JSON/2.0/json2.js"/>

<div id="editingScreen" class="ui-widget-overlay dn"></div>

<script>
var TIME_SLIDER = null;
Event.observe(window, 'load', function(){
    Yukon.ThermostatScheduleEditor.upperHeatF = ${type.upperLimitHeatInFahrenheit};
    Yukon.ThermostatScheduleEditor.lowerHeatF = ${type.lowerLimitHeatInFahrenheit};
    Yukon.ThermostatScheduleEditor.upperCoolF = ${type.upperLimitCoolInFahrenheit};
    Yukon.ThermostatScheduleEditor.lowerCoolF = ${type.lowerLimitCoolInFahrenheit};
    
    <c:choose>
        <c:when test="${temperatureUnit eq 'C'}" >
        Yukon.ThermostatScheduleEditor.currentUnit = 'celcius';
        </c:when>
        <c:otherwise>
        Yukon.ThermostatScheduleEditor.currentUnit = 'fahrenheit';
        </c:otherwise>
    </c:choose>
    
    Yukon.ThermostatScheduleEditor.initPrototype();
    
    //scroll the schedule into view
    var scheduleId = Yukon.ui.getParameterByName('scheduleId');
    if(scheduleId != null){
        window.location.hash = "scheduleId_" + scheduleId;
    }
});
</script>

<div id="timeSlider" class="slider">
    <div class="chevron"></div>
    <div class="startLabel fl"></div>
    <div class="track">
        <div class="handle"></div>
    </div>
    <div class="endLabel fr"></div>
</div>

<div id="tempSlider" class="slider">
    <div class="chevron"></div>
    <div class="box fl tempHolder ${temperatureUnit}">
        <div class="startLabel fl"></div>°<span class="C">${celcius_char}</span><span class="F">${fahrenheit_char}</span>
    </div>
    <div class="track">
        <div class="handle"></div>
    </div>
    <div class="box fr tempHolder ${temperatureUnit}">
        <div class="endLabel fl"></div>°<span class="C">${celcius_char}</span><span class="F">${fahrenheit_char}</span>
    </div>
</div>

<cti:msg2 var="heatLabel" key="yukon.dr.consumer.thermostat.mode.HEAT"/>
<cti:msg2 var="coolLabel" key="yukon.dr.consumer.thermostat.mode.COOL"/>

<c:choose>
    <c:when test="${empty schedules}">
        <div class="helper">
            <i:inline key=".noSchedulesHelper" />
        </div>
        <br>
        <br>
        <cti:button key="help" styleClass="help"/>
        <cti:button key="create" styleClass="create"/>
    </c:when>
    <c:otherwise>
      <table class="thermostatPageContent">
        <tr>
            <td>  
                <tags:formElementContainer nameKey=".pageTitle">



<div class="schedules fl">
<c:forEach var="schedule" items="${schedules}">
    <div class="schedule small <c:if test="${schedule.accountThermostatScheduleId eq currentScheduleId }">current titledContainer boxContainer</c:if>" id="scheduleId_${schedule.accountThermostatScheduleId}">
        <c:if test="${schedule.accountThermostatScheduleId eq currentScheduleId }">
            <div class="titleBar boxContainer_titleBar">
                <div class="title boxContainer_title">
                    <i:inline key=".lastSent"/>
                </div>
            </div>
        </c:if>
    <div class="boxContainer_content">
        <form method="POST" action="/spring/stars/operator/thermostatSchedule/save">
            <input type="hidden" name="thermostatId" value="${thermostatId}">
            <input type="hidden" name="accountId" value="${accountId}">
            <input type="hidden" name="thermostatIds" value="${thermostatIds}">
            <input type="hidden" name="thermostatType" value="${schedule.thermostatType}">
            <input type="hidden" name="scheduleId" value="${schedule.accountThermostatScheduleId}">
            <input type="hidden" name="scheduleName" value="${schedule.scheduleName}">
            <input type="hidden" name="scheduleMode" value="">
            <input type="hidden" name="schedules" value="">
            <input type="hidden" name="thermostatScheduleMode" value="${schedule.thermostatScheduleMode}">
            <input type="hidden" name="temperatureUnit" value="${temperatureUnit}">
            
            <div class="heading">
                <span class="title"><spring:escapeBody htmlEscape="true">${schedule.scheduleName}</spring:escapeBody></span>
            </div>
            <div class="days fl">
                <span class="labels">
                    <label class="label fl"></label>
                    <c:forEach var="period" items="${thermostatType.periodStyle.realPeriods}">
                        <div class="period">
                            <i:inline key="yukon.dr.consumer.thermostatSchedule.${period}" />
                            <br>
                            <small class="heat_cool_label"><span class="temp"><i:inline key="yukon.dr.consumer.thermostat.mode.HEAT"/></span> <span class="temp"><i:inline key="yukon.dr.consumer.thermostat.mode.COOL"/></span></small>
                        </div>
                    </c:forEach>
                </span>
                
                <c:forEach var="day" items="${schedule.entriesByTimeOfWeekMultimapAsMap}" varStatus="rowCounter">
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
                                <c:when test="${schedule.thermostatScheduleMode == 'ALL'}">
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
                                        <div class="temp heat ${temperatureUnit}" title="${heatLabel}">
                                            <span class="value "></span><input type="hidden" value="${period.heatTemp.value}" name="heat_F"><span class="hide_when_editing">°<span class="C">${celcius_char}</span><span class="F">${fahrenheit_char}</span></span>
                                        </div>
                                        <div class="temp cool ${temperatureUnit}" title="${coolLabel}">
                                            <span class="value "></span><input type="hidden" value="${period.coolTemp.value}" name="cool_F"><span class="hide_when_editing">°<span class="C">${celcius_char}</span><span class="F">${fahrenheit_char}</span></span>
                                        </div>
                                    </div>
                                </c:if>
                            </c:forEach>
                        </div>
                    </div>
                </c:forEach>
                
            </div>
            <div class="actions">
                    <cti:button key="edit" renderMode="labeledImage" styleClass="edit edit_${schedule.accountThermostatScheduleId}" />
                    <cti:button key="sendNow" renderMode="labeledImage" styleClass="send" />
                    <cti:button key="copy" renderMode="labeledImage" styleClass="copy copy_${schedule.accountThermostatScheduleId}" />
            </div>
        </form>
</div>
        <cti:msg2 var="editTitle" key=".editSchedule.title" argument="${schedule.scheduleName}" />
        <tags:simplePopup title="${editTitle}" id="editSchedule_${schedule.accountThermostatScheduleId}" on=".edit_${schedule.accountThermostatScheduleId}, .copy_${schedule.accountThermostatScheduleId}">
        <div class="container">
            <div class="schedule_editor">
                <form id="form_${schedule.accountThermostatScheduleId}" method="POST" action="/spring/stars/operator/thermostatSchedule/save" onsubmit="Yukon.ThermostatScheduleEditor.prepForm(this);">
                    <input type="hidden" name="accountId" value="${accountId}">
                    <input type="hidden" name="thermostatId" value="${thermostatId}">
                    <input type="hidden" name="thermostatIds" value="${thermostatIds}">
                    <input type="hidden" name="schedulableThermostatType" value="${schedule.thermostatType}">
                    <input type="hidden" name="scheduleId" value="${schedule.accountThermostatScheduleId}">
                    <input type="hidden" name="scheduleMode" value="">
                    <input type="hidden" name="schedules" value="">
                    <input type="hidden" name="thermostatScheduleMode" value="${schedule.thermostatScheduleMode}">
                    <input type="hidden" name="temperatureUnit" value="${temperatureUnit}">
                    
                    <label for="scheduleName"><i:inline key=".name"/></label><input type="text" name="scheduleName" value="${schedule.scheduleName}" initialValue="${schedule.scheduleName}" size="40" maxlength="60">
    
                    <div class="days fl">
                        <span class="labels">
                            <label class="label fl"></label>
                            <c:forEach var="period" items="${thermostatType.periodStyle.realPeriods}">
                                <div class="period">
                                    <i:inline key="yukon.dr.consumer.thermostatSchedule.${period}" />
                                    <br>
                                    <small class="heat_cool_label"><span class="temp"><i:inline key="yukon.dr.consumer.thermostat.mode.HEAT"/></span> <span class="temp"><i:inline key="yukon.dr.consumer.thermostat.mode.COOL"/></span></small>
                                </div>
                            </c:forEach>
                        </span>
                        <c:forEach var="day" items="${schedule.entriesByTimeOfWeekMultimapAsMap}" varStatus="rowCounter">
                            <c:choose>
                              <c:when test="${rowCounter.count % 2 == 0}">
                                <c:set var="rowStyle" scope="page" value="odd"/>
                              </c:when>
                              <c:otherwise>
                                <c:set var="rowStyle" scope="page" value="even"/>
                              </c:otherwise>
                            </c:choose>
                            <div class="day active ${rowStyle} ${day.key}">
                                <div class="periods">
                                    <c:choose>
                                        <c:when test="${schedule.thermostatScheduleMode == 'ALL'}">
                                            <label class="label fl"><i:inline key="yukon.dr.consumer.thermostat.schedule.EVERYDAY_abbr" /></label>
                                        </c:when>
                                        <c:otherwise>
                                            <label class="label fl"><i:inline key="yukon.dr.consumer.thermostat.schedule.${day.key}_abbr" /></label>
                                        </c:otherwise>
                                    </c:choose>
                                    <c:forEach var="period" items="${day.value}">
                                    <!-- Comeup with a better way to determine mode -->
                                        <c:choose>
                                                    <c:when test="${period.heatTemp.value gt -1}">
                                                        <div class="period period_edit ${period.timeOfWeek}">
                                                            <input type="hidden" name="timeOfWeek" value="${period.timeOfWeek}">
                                                            <div class="info time">
                                                                <input type="hidden" class="time" name="secondsFromMidnight" value="${period.startTime}" initialValue="${period.startTime}" defaultValue="${period.startTime}">
                                                                <input type="text" class="time" maxlength="8">
                                                            </div>
                                                            
                                                            <div class="temp heat ${temperatureUnit}" title="${heatLabel}">
                                                                <input type="text" class="heat_F" maxlength="4">°<span class="C">${celcius_char}</span><span class="F">${fahrenheit_char}</span>
                                                                <input type="hidden" value="${period.heatTemp.value}" initialValue="${period.heatTemp.value}" name="heat_F" defaultValue="${period.heatTemp.value}">
                                                            </div>
                                                            <div class="temp cool ${temperatureUnit}" title="${coolLabel}">
                                                                <input type="text" class="cool_F" maxlength="4">°<span class="C">${celcius_char}</span><span class="F">${fahrenheit_char}</span>
                                                                <input type="hidden" value="${period.coolTemp.value}" initialValue="${period.coolTemp.value}" name="cool_F" defaultValue="${period.coolTemp.value}">
                                                            </div>
                                                        </div>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <div class="period dn ${period.timeOfWeek}">
                                                            <input type="hidden" name="timeOfWeek" value="${period.timeOfWeek}">
                                                            <input type="hidden" class="time" name="secondsFromMidnight" value="${period.startTime}">
                                                            <input type="hidden" value="${period.heatTemp.value}" name="heat_F">
                                                            <input type="hidden" value="${period.coolTemp.value}" name="cool_F">
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
        </tags:simplePopup>
    </div>
</c:forEach>
</div>
</tags:formElementContainer>
        </td>

    </c:otherwise>
</c:choose>
        <td>
            <jsp:include page="/WEB-INF/pages/stars/operator/operatorThermostat/selectedThermostatsFragment.jsp" />
            <p>
                <i:inline key="yukon.web.modules.consumer.savedSchedules.tempUnit"/>
                <br>
                <div class="tempControls">
                    <label><input name="units" type="radio" value="celcius" <c:if test="${temperatureUnit eq 'C'}" >checked="checked"</c:if>><i:inline key="yukon.web.defaults.celcius"/></label>
                    <label><input name="units" type="radio" value="fahrenheit" <c:if test="${temperatureUnit eq 'F'}" >checked="checked"</c:if>><i:inline key="yukon.web.defaults.fahrenheit"/></label>
                </div>
            </p>
            <p>
                <br>
                <br>
                <cti:button key="create" styleClass="create"/>
                <br>
                <cti:button key="help" styleClass="help"/>
            </p>
        </td>
        </tr>
        </table>

<cti:msg2 var="createTitle" key=".createSchedule.title"/>
<div class="schedule">
<i:simplePopup titleKey="${createTitle}" id="createSchedule" on=".create" >
    <div class="f_wizard">
        <div class="f_page page_0">
            <div class="box">
                <div class="helper box">
                    <i:inline key=".modeHint" />
                </div>
                <div class="box pad">
                    <c:forEach var="mode" items="${allowedModes}">
                        <label><input type="radio" name="defaultScheduleMode" value="${mode}"> <cti:msg key="yukon.web.modules.operator.thermostatMode.${mode}" /> </label>
                        <br>
                    </c:forEach>
                </div>
            </div>
        
            <div class="actionArea">
                <div class="fr">
                    <cti:button key="next" styleClass="f_next"/>
                </div>
            </div>
        </div>
        
        <div class="f_page page_1">
            <div class="createSchedule box">
                <c:forEach var="schedule" items="${defaultSchedules}">
                    <div class="schedule_editor ${schedule.thermostatScheduleMode} small">
                        <form method="POST" action="/spring/stars/operator/thermostatSchedule/save">
                            <input type="hidden" name="accountId" value="${accountId}">
                            <input type="hidden" name="thermostatId" value="${thermostatIds}">
                            <input type="hidden" name="thermostatIds" value="${thermostatIds}">
                            <input type="hidden" name="schedulableThermostatType" value="${schedule.thermostatType}">
                            <input type="hidden" name="scheduleId" value="${schedule.accountThermostatScheduleId}">
                            <input type="hidden" name="scheduleMode" value="">
                            <input type="hidden" name="schedules" value="">
                            <input type="hidden" name="thermostatScheduleMode" value="${schedule.thermostatScheduleMode}">
                            <input type="hidden" name="temperatureUnit" value="${temperatureUnit}">
                            
                            <label for="scheduleName"><i:inline key=".name"/></label><input type="text" name="scheduleName" value="${schedule.scheduleName}" initialValue="${schedule.scheduleName}" size="40" maxlength="60">
            
                            <div class="days fl">
                                <span class="labels">
                                    <label class="label fl"></label>
                                    <c:forEach var="period" items="${thermostatType.periodStyle.realPeriods}">
                                        <div class="period">
                                            <i:inline key="yukon.dr.consumer.thermostatSchedule.${period}" />
                                            <br>
                                            <small class="heat_cool_label"><span class="temp"><i:inline key="yukon.dr.consumer.thermostat.mode.HEAT"/></span> <span class="temp"><i:inline key="yukon.dr.consumer.thermostat.mode.COOL"/></span></small>
                                        </div>
                                    </c:forEach>
                                </span>
                                <c:forEach var="day" items="${schedule.entriesByTimeOfWeekMultimapAsMap}" varStatus="rowCounter">
                                    <c:choose>
                                      <c:when test="${rowCounter.count % 2 == 0}">
                                        <c:set var="rowStyle" scope="page" value="odd"/>
                                      </c:when>
                                      <c:otherwise>
                                        <c:set var="rowStyle" scope="page" value="even"/>
                                      </c:otherwise>
                                    </c:choose>
                                    <div class="day active ${rowStyle} ${day.key}">
                                        <div class="periods">
                                            <c:choose>
                                                <c:when test="${schedule.thermostatScheduleMode == 'ALL'}">
                                                    <label class="label fl"><i:inline key="yukon.dr.consumer.thermostat.schedule.EVERYDAY_abbr" /></label>
                                                </c:when>
                                                <c:otherwise>
                                                    <label class="label fl"><i:inline key="yukon.dr.consumer.thermostat.schedule.${day.key}_abbr" /></label>
                                                </c:otherwise>
                                            </c:choose>
                                            <c:forEach var="period" items="${day.value}" varStatus="idx">
                                                <!-- Comeup with a better way to determine mode -->
                                                <c:choose>
                                                    <c:when test="${period.heatTemp.value gt -1}">
                                                        <div class="period period_edit ${period.timeOfWeek}">
                                                            <input type="hidden" name="timeOfWeek" value="${period.timeOfWeek}">
                                                            <div class="info time">
                                                                <input type="hidden" class="time" name="secondsFromMidnight" value="${period.startTime}" initialValue="${period.startTime}" defaultValue="${period.startTime}">
                                                                <input type="text" class="time" maxlength="8">
                                                            </div>
                                                            
                                                            <div class="temp heat ${temperatureUnit}" title="${heatLabel}">
                                                                <input type="text" class="heat_F" maxlength="4">°<span class="C">${celcius_char}</span><span class="F">${fahrenheit_char}</span>
                                                                <input type="hidden" value="${period.heatTemp.value}" initialValue="${period.heatTemp.value}" name="heat_F" defaultValue="${period.heatTemp.value}">
                                                            </div>
                                                            <div class="temp cool ${temperatureUnit}" title="${coolLabel}">
                                                                <input type="text" class="cool_F" maxlength="4">°<span class="C">${celcius_char}</span><span class="F">${fahrenheit_char}</span>
                                                                <input type="hidden" value="${period.coolTemp.value}" initialValue="${period.coolTemp.value}" name="cool_F" defaultValue="${period.coolTemp.value}">
                                                            </div>
                                                        </div>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <div class="period dn ${period.timeOfWeek}">
                                                            <input type="hidden" name="timeOfWeek" value="${period.timeOfWeek}">
                                                            <input type="hidden" class="time" name="secondsFromMidnight" value="${period.startTime}">
                                                            <input type="hidden" value="${period.heatTemp.value}" name="heat_F">
                                                            <input type="hidden" value="${period.coolTemp.value}" name="cool_F">
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
                </c:forEach>
            </div>
        
            <div class="actions">
                <div class="fr">
                    <cti:button key="previous" styleClass="f_prev"/>
                    <cti:button key="save" styleClass="save f_blocker" />
                    <cti:button key="cancel" styleClass="cancel" />
                </div>
                <div class="fl">
                    <cti:button key="recommendedSettings" renderMode="labeledImage" styleClass="default"/>
                </div>
            </div>
        </div>
    </div>
</i:simplePopup>
</div>

<cti:msg2 var="helpTitle" key=".help.title"/>
<tags:simplePopup title="${helpTitle}" id="help" on=".help">
<div class="help pad">
        <cti:msg2 key="yukon.web.modules.consumer.savedSchedules.hint" htmlEscape="false"/>
    </div>
</tags:simplePopup>

<!-- shared action forms -->
<form name="deleteSchedule" method="POST" action="/spring/stars/operator/thermostatSchedule/delete">
    <input type="hidden" name="scheduleId">
    <input type="hidden" name="thermostatId" value="${thermostatId}">
    <input type="hidden" name="thermostatIds" value="${thermostatIds}">
    <input type="hidden" name="accountId" value="${accountId}">
    <tags:confirmDialog nameKey=".deleteConfirm" styleClass="smallSimplePopup f_blocker" submitName="delete" on=".delete"/>
</form>

<form name="sendSchedule" method="POST" action="/spring/stars/operator/thermostatSchedule/send">
    <input type="hidden" name="scheduleId">
    <input type="hidden" name="thermostatIds" value="${thermostatIds}">
    <input type="hidden" name="accountId" value="${accountId}">
    <input type="hidden" name="temperatureUnit" value="${temperatureUnit}">
    <tags:confirmDialog nameKey=".sendConfirm" styleClass="smallSimplePopup f_blocker" submitName="send" on=".send"/>
</form>
<!--  END action forms -->
    
</cti:standardPage>
