<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

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
        Yukon.ThermostatScheduleEditor.init({
            currentUnit: '${temperatureUnit}',
            upperHeatF: parseFloat(${schedule.thermostatType.upperLimitHeatInFahrenheit.value}),
            lowerHeatF: parseFloat(${schedule.thermostatType.lowerLimitHeatInFahrenheit.value}),
            upperCoolF: parseFloat(${schedule.thermostatType.upperLimitCoolInFahrenheit.value}),
            lowerCoolF: parseFloat(${schedule.thermostatType.lowerLimitCoolInFahrenheit.value})
        });
    });
    </script>
    
<tags:setFormEditMode mode="${mode}"/>

<!-- Sliders -->
<div id="timeSlider" class="slider">
    <div class="chevron"></div>
    <div class="startLabel fl label"></div>
    <div class="track">
        <div class="handle"></div>
    </div>
    <div class="endLabel fr label"></div>
</div>

<div id="tempSlider" class="slider">
    <div class="chevron"></div>
    <div class="box fl tempHolder ${temperatureUnit} label">
        <div class="startLabel fl"></div><span class="C"><i:inline key="yukon.web.defaults.celsius"/></span><span class="F"><i:inline key="yukon.web.defaults.fahrenheit"/></span>
    </div>
    <div class="track">
        <div class="handle"></div>
    </div>
    <div class="box fr tempHolder ${temperatureUnit} label">
        <div class="endLabel fl"></div><span class="C"><i:inline key="yukon.web.defaults.celsius"/></span><span class="F"><i:inline key="yukon.web.defaults.fahrenheit"/></span>
    </div>
</div>
<!-- END Sliders -->

<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
        
    
    <form id="scheduleForm" name="scheduleForm" method="POST" action="/spring/adminSetup/energyCompany/schedules/saveDefaultThermostatSchedule">
    </form>
    
    <div class="schedule small vh <c:if test="${schedule.accountThermostatScheduleId eq currentScheduleId }">current titledContainer boxContainer</c:if>" id="scheduleId_${schedule.accountThermostatScheduleId}">
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
                                            <span class="value "></span><input type="hidden" value="${period.heatTemp.value}" name="heat_F">°
                                        </div>
                                        <div class="temp cool ${temperatureUnit}" title="${coolLabel}">
                                            <span class="value "></span><input type="hidden" value="${period.coolTemp.value}" name="cool_F">°
                                        </div>
                                    </div>
                                </c:if>
                            </c:forEach>
                        </div>
                    </div>
                </c:forEach>
                
            </div>
                <div class="actions">
                        <cti:button key="edit" renderMode="labeledImage" styleClass="editDefaultSchedule edit_${schedule.accountThermostatScheduleId}" />
                </div>
            </form>
        </div>
        
        <i:simplePopup titleKey=".editSchedule.title" id="createSchedule" on=".edit_${schedule.accountThermostatScheduleId}" >
            <div class="f_wizard">
                <div class="f_page page_0">
                    <div class="box">
                        <div class="helper box">
                            <i:inline key=".modeHint" />
                        </div>
                        <div class="box pad">
                            <c:forEach var="mode" items="${allowedModes}">
                                <label><input type="radio" name="defaultScheduleMode" value="${mode}" <c:if test="${status.first}">checked</c:if>> <cti:msg key="yukon.web.modules.operator.thermostatMode.${mode}" /> </label>
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
                    <div class="createSchedule box small">
                        <c:forEach var="schedule" items="${defaultSchedules}">
                            <tags:thermostatScheduleEditor schedule="${schedule}"
                                            thermostatId="${thermostatId}"
                                            thermostatIds="${thermostatIds}"
                                            accountId="${accountId}"
                                            temperatureUnit="${temperatureUnit}"
                                            actionPath="/spring/adminSetup/energyCompany/schedules/saveDefaultThermostatSchedule?ecId=${ecId}"
                                            thermostatType="${thermostatType}"/>
                        </c:forEach>
                    </div>
                
                    <div class="actions">
                        <div class="fr">
                            <cti:button key="chooseMode" styleClass="f_prev"/>
                            <cti:button key="save" styleClass="save f_blocker" />
                            <cti:button key="cancel" styleClass="cancel" />
                        </div>
                    </div>
                </div>
            </div>
        </i:simplePopup>
    </div>
    

    </td>
    <td class="vat">
        <div class="tempControlsNoUpdate">
            <label><input name="units" type="radio" value="C" <c:if test="${temperatureUnit eq 'C'}" >checked="checked"</c:if>><i:inline key="yukon.web.defaults.celsius"/></label>
            <label><input name="units" type="radio" value="F" <c:if test="${temperatureUnit eq 'F'}" >checked="checked"</c:if>><i:inline key="yukon.web.defaults.fahrenheit"/></label>
        </div>
    </td>
  </tr>
</table>
    
    
</cti:standardPage>