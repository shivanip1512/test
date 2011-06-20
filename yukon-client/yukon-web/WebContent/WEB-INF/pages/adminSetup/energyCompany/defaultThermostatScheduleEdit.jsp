<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:url var="delete" value="/WebConfig/yukon/Icons/delete.gif"/>
<cti:url var="deleteOver" value="/WebConfig/yukon/Icons/delete_over.gif"/>

<script language="JavaScript" type="text/javascript"  src="/JavaScript/yukonGeneral.js"></script>


<cti:standardPage module="adminSetup" page="schedules.${mode}">

    <cti:includeCss link="/WebConfig/yukon/styles/StarsConsumerStyles.css"/>
    <cti:includeCss link="/WebConfig/yukon/styles/thermostat.css"/>
    
    <!-- Add language specific time formatter -->
    <cti:msg var="timeFormatter" key="yukon.common.timeFormatter" />
    <cti:includeScript link="${timeFormatter}"/>
    <cti:includeScript link="/JavaScript/thermostatScheduleEditor.js"/>
    <cti:includeScript link="/JavaScript/lib/JSON/2.0/json2.js"/>
    
    <script>
    var TIME_SLIDER = null;
    Event.observe(window, 'load', function(){
        Yukon.ThermostatScheduleEditor.upperHeatF = ${schedule.thermostatType.upperLimitHeatInFahrenheit};
        Yukon.ThermostatScheduleEditor.lowerHeatF = ${schedule.thermostatType.lowerLimitHeatInFahrenheit};
        Yukon.ThermostatScheduleEditor.upperCoolF = ${schedule.thermostatType.upperLimitCoolInFahrenheit};
        Yukon.ThermostatScheduleEditor.lowerCoolF = ${schedule.thermostatType.lowerLimitCoolInFahrenheit};
        Yukon.ThermostatScheduleEditor.initPrototype();
        
        //scroll the schedule into view
        var scheduleId = Yukon.ui.getParameterByName('scheduleId');
        if(scheduleId != null){
            window.location.hash = "scheduleId_" + scheduleId;
        }
    });
    </script>
    
<tags:setFormEditMode mode="${mode}"/>

<!-- Sliders -->
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
<!-- END Sliders -->

<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
        
    
    <form id="scheduleForm" name="scheduleForm" method="POST" action="/spring/adminSetup/energyCompany/schedules/saveDefaultThermostatSchedule">
    </form>
    
    <div class="schedule small <c:if test="${schedule.accountThermostatScheduleId eq currentScheduleId }">current titledContainer boxContainer</c:if>" id="scheduleId_${schedule.accountThermostatScheduleId}">
    <c:if test="${schedule.accountThermostatScheduleId eq currentScheduleId }">
        <div class="titleBar boxContainer_titleBar">
            <div class="title boxContainer_title">
                <cti:msg2 key=".lastSent"/>
            </div>
        </div>
    </c:if>
    <div class="boxContainer_content">
        <form method="POST" action="/spring/adminSetup/energyCompany/schedules/saveDefaultThermostatSchedule">
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
                            <cti:msg2 key="yukon.dr.consumer.thermostatSchedule.${period}" />
                            <br>
                            <small class="heat_cool_label"><span class="temp"><cti:msg2 key="yukon.dr.consumer.thermostat.mode.HEAT"/></span> <span class="temp"><cti:msg2 key="yukon.dr.consumer.thermostat.mode.COOL"/></span></small>
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
                        <div class="day active ${rowStyle}">
                        <div class="periods">
                            <c:choose>
                                <c:when test="${schedule.thermostatScheduleMode == 'ALL'}">
                                    <label class="label fl"><cti:msg2 key="yukon.dr.consumer.thermostat.schedule.EVERYDAY_abbr" /></label>
                                </c:when>
                                <c:otherwise>
                                    <label class="label fl"><cti:msg2 key="yukon.dr.consumer.thermostat.schedule.${day.key}_abbr" /></label>
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
                                        <div class="temp heat ${temperatureUnit}" title="<cti:msg2 key="yukon.dr.consumer.thermostat.mode.HEAT"/>">
                                            <span class="value "></span><input type="hidden" value="${period.heatTemp.value}" name="heat_F"><span class="hide_when_editing">°<span class="C">${celcius_char}</span><span class="F">${fahrenheit_char}</span></span>
                                        </div>
                                        <div class="temp cool ${temperatureUnit}" title="<cti:msg2 key="yukon.dr.consumer.thermostat.mode.COOL"/>">
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
            </div>
        </form>
</div>
        <cti:msg2 var="editTitle" key=".editSchedule.title" argument="${schedule.scheduleName}" />
        <tags:simplePopup title="${editTitle}" id="editSchedule_${schedule.accountThermostatScheduleId}" on=".edit_${schedule.accountThermostatScheduleId}, .copy_${schedule.accountThermostatScheduleId}">
        <div class="container">
            <div class="schedule_editor">
                <form id="form_${schedule.accountThermostatScheduleId}" method="POST" action="/spring/adminSetup/energyCompany/schedules/saveDefaultThermostatSchedule" onsubmit="Yukon.ThermostatScheduleEditor.prepForm(this);">
                    <input type="hidden" name="schedulableThermostatType" value="${schedule.thermostatType}">
                    <input type="hidden" name="scheduleId" value="${schedule.accountThermostatScheduleId}">
                    <input type="hidden" name="scheduleMode" value="">
                    <input type="hidden" name="schedules" value="">
                    <input type="hidden" name="thermostatScheduleMode" value="${schedule.thermostatScheduleMode}">
                    <input type="hidden" name="temperatureUnit" value="${temperatureUnit}">
                    <input type="hidden" name="ecId" value="${ecId}">
                    
                    <label for="scheduleName"><cti:msg2 key=".name"/></label><input type="text" name="scheduleName" value="${schedule.scheduleName}" initialValue="${schedule.scheduleName}" size="40" maxlength="60">
    
                    <div class="days fl">
                        <span class="labels">
                            <label class="label fl"></label>
                            <c:forEach var="period" items="${thermostatType.periodStyle.realPeriods}">
                                <div class="period">
                                    <cti:msg2 key="yukon.dr.consumer.thermostatSchedule.${period}" />
                                    <br>
                                    <small class="heat_cool_label"><span class="temp"><cti:msg2 key="yukon.dr.consumer.thermostat.mode.HEAT"/></span> <span class="temp"><cti:msg2 key="yukon.dr.consumer.thermostat.mode.COOL"/></span></small>
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
                                            <label class="label fl"><cti:msg2 key="yukon.dr.consumer.thermostat.schedule.EVERYDAY_abbr" /></label>
                                        </c:when>
                                        <c:otherwise>
                                            <label class="label fl"><cti:msg2 key="yukon.dr.consumer.thermostat.schedule.${day.key}_abbr" /></label>
                                        </c:otherwise>
                                    </c:choose>
                                    <c:forEach var="period" items="${day.value}">
                                    <!-- Comeup with a better way to determine mode -->
                                        <c:if test="${period.heatTemp.value gt -1 }">
                                            <div class="period period_edit ${period.timeOfWeek}">
                                                <input type="hidden" name="timeOfWeek" value="${period.timeOfWeek}">
                                                <div class="info time">
                                                    <input type="hidden" class="time" name="secondsFromMidnight" value="${period.startTime}" initialValue="${period.startTime}">
                                                    <input type="text" class="time" maxlength="8">
                                                </div>
                                                
                                                <div class="temp heat ${temperatureUnit}" title="<cti:msg2 key="yukon.dr.consumer.thermostat.mode.HEAT"/>">
                                                    <input type="text" class="heat_F" maxlength="4">°<span class="C">${celcius_char}</span><span class="F">${fahrenheit_char}</span>
                                                    <input type="hidden" value="${period.heatTemp.value}" initialValue="${period.heatTemp.value}" name="heat_F">
                                                </div>
                                                <div class="temp cool ${temperatureUnit}" title="<cti:msg2 key="yukon.dr.consumer.thermostat.mode.COOL"/>">
                                                    <input type="text" class="cool_F" maxlength="4">°<span class="C">${celcius_char}</span><span class="F">${fahrenheit_char}</span>
                                                    <input type="hidden" value="${period.coolTemp.value}" initialValue="${period.coolTemp.value}" name="cool_F">
                                                </div>
                                            </div>
                                        </c:if>
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
                <cti:button key="save" styleClass="save f_blocker" />
                <cti:button key="cancel" styleClass="cancel" />
            </div>
        </div>
        </tags:simplePopup>
    </div>
    

    </td>
    <td class="vat">
        <i:inline key="yukon.web.modules.consumer.savedSchedules.tempUnit"/>
        <br>
        <div class="tempControls">
            <label><input name="units" type="radio" value="celcius" <c:if test="${temperatureUnit eq 'C'}" >checked="checked"</c:if>><i:inline key="yukon.web.defaults.celcius"/></label>
            <label><input name="units" type="radio" value="fahrenheit" <c:if test="${temperatureUnit eq 'F'}" >checked="checked"</c:if>><i:inline key="yukon.web.defaults.fahrenheit"/></label>
        </div>
    </td>
  </tr>
</table>
    
    
</cti:standardPage>