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
            lowerCoolF: parseFloat(${schedule.thermostatType.lowerLimitCoolInFahrenheit.value}),
            secondsResolution: ${schedule.thermostatType.resolutionInSeconds},
            secondsBetweenPeriods: ${schedule.thermostatType.minimumTimeBetweenPeriodsInSeconds}
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
        <tags:thermostatScheduleWidget schedule="${schedule}"
                                    thermostatId=""
                                    thermostatIds=""
                                    accountId=""
                                    temperatureUnit="${temperatureUnit}"
                                    actionPath="/spring/adminSetup/energyCompany/schedules/saveDefaultThermostatSchedule"
                                    thermostatType="${schedule.thermostatType}"
                                    styleClass="vh"
                                    customActions="true"
                                    omitEditor="true">
                                    
            <div class="actions">
                <cti:button key="edit" renderMode="labeledImage" styleClass="editDefaultSchedule edit_${schedule.accountThermostatScheduleId}" />
            </div>
        </tags:thermostatScheduleWidget>
                
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