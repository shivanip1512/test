<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="consumer" page="savedSchedules">
<cti:standardMenu/>

<cti:includeCss link="/WebConfig/yukon/styles/thermostat.css"/>
<cti:includeCss link="/WebConfig/yukon/styles/yukonUIToolkit/yukon_ui_toolkit.css"/>

<cti:msg var="timeFormatter" key="yukon.common.timeFormatter" />
<cti:includeScript link="${timeFormatter}"/>
<cti:includeScript link="/JavaScript/thermostatScheduleEditor.js"/>
<cti:includeScript link="/JavaScript/lib/JSON/2.0/json2.js"/>

<cti:flashScopeMessages/>

<script>
var TIME_SLIDER = null;
Event.observe(window, 'load', function(){
    Yukon.ThermostatScheduleEditor.upperHeatF = ${type.upperLimitHeatInFahrenheit};
    Yukon.ThermostatScheduleEditor.lowerHeatF = ${type.lowerLimitHeatInFahrenheit};
    Yukon.ThermostatScheduleEditor.upperCoolF = ${type.upperLimitCoolInFahrenheit};
    Yukon.ThermostatScheduleEditor.lowerCoolF = ${type.lowerLimitCoolInFahrenheit};
    Yukon.ThermostatScheduleEditor.copyPrefix = "<cti:msg2 key="defaults.copy.prefix"/>";
    
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

<h3>
    <cti:msg key="yukon.web.modules.consumer.savedSchedules.pageTitle" /><br>
    <c:forEach var="stat" items="${thermostats}">
        &#160;${stat.label}&#160;
    </c:forEach>
</h3>

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

<c:choose>
    <c:when test="${empty schedules}">
        <div class="helper">
            <i:inline key=".noSchedulesHelper" />
        </div>
        <br>
        <br>
        <cti:button key="create" styleClass="create"/>
    </c:when>
    <c:otherwise>
        
    </c:otherwise>
</c:choose>


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
                    <div class="actions">
                        <div class="fl">
                            <div class="fl">
                                <cti:button key="create" styleClass="create"/>
                                <cti:button key="help" styleClass="help"/>
                            </div>
                            <div class="tempControls fl">
                                <label><input name="units" type="radio" value="celcius" <c:if test="${temperatureUnit eq 'C'}" >checked="checked"</c:if>><i:inline key="yukon.web.defaults.celcius"/></label>
                                <label><input name="units" type="radio" value="fahrenheit" <c:if test="${temperatureUnit eq 'F'}" >checked="checked"</c:if>><i:inline key="yukon.web.defaults.fahrenheit"/></label>
                            </div>
                        </div>
                    </div>
                    <div class="schedules fl">
                        <c:choose>
                            <c:when test="${ currentScheduleId gt -1 }">
                                <c:forEach var="schedule" items="${schedules}" varStatus="status" begin="0" end="0" >
                                    <tags:sectionContainer2 nameKey="lastSent">
                                        <tags:thermostatScheduleWidget schedule="${schedule}"
                                            thermostatId="${thermostatId}"
                                            thermostatIds="${thermostatIds}"
                                            accountId="${customerAccount.accountId}"
                                            temperatureUnit="${temperatureUnit}"
                                            actionPath="/spring/stars/consumer/thermostat/schedule/saveJSON"
                                            thermostatType="${thermostatType}"
                                            styleClass="vh"/>
                                    </tags:sectionContainer2>
                                </c:forEach>
                                <br>
                                    <tags:sectionContainer2 nameKey="otherSchedules">
                                        <c:forEach var="schedule" items="${schedules}" begin="1" >
                                            <tags:thermostatScheduleWidget schedule="${schedule}"
                                                thermostatId="${thermostatId}"
                                                thermostatIds="${thermostatIds}"
                                                accountId="${customerAccount.accountId}"
                                                temperatureUnit="${temperatureUnit}"
                                                actionPath="/spring/stars/consumer/thermostat/schedule/saveJSON"
                                                thermostatType="${thermostatType}"
                                                styleClass="vh"/>
                                        </c:forEach>    
                                    </tags:sectionContainer2>
                            </c:when>
                            <c:otherwise>
                                <tags:sectionContainer2 nameKey="lastSent">
                                    <div class="schedule">
                                        <div class="actions">
                                            <i:inline key="defaults.none"/>
                                        </div>
                                    </div>
                                </tags:sectionContainer2>
                                <tags:sectionContainer2 nameKey="otherSchedules">
                                    <c:forEach var="schedule" items="${schedules}" >
                                        <tags:thermostatScheduleWidget schedule="${schedule}"
                                            thermostatId="${thermostatId}"
                                            thermostatIds="${thermostatIds}"
                                            accountId="${customerAccount.accountId}"
                                            temperatureUnit="${temperatureUnit}"
                                            actionPath="/spring/stars/consumer/thermostat/schedule/saveJSON"
                                            thermostatType="${thermostatType}"
                                            styleClass="vh"/>
                                    </c:forEach>
                                </tags:sectionContainer2>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </c:otherwise>
            </c:choose>

<div class="schedule">
<i:simplePopup titleKey=".createSchedule.title" id="createSchedule" on=".create" >
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
            <div class="createSchedule box small">
                <c:forEach var="schedule" items="${defaultSchedules}">
                    <tags:thermostatScheduleEditor  schedule="${schedule}"
                                                    thermostatId="${thermostatId}"
                                                    thermostatIds="${thermostatIds}"
                                                    accountId="${customerAccount.accountId}"
                                                    temperatureUnit="${temperatureUnit}"
                                                    actionPath="/spring/stars/consumer/thermostat/schedule/saveJSON"
                                                    thermostatType="${thermostatType}"/>
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

<!-- help/hints popup -->
<i:simplePopup titleKey=".help.title" id="help" on=".help">
<div class="help pad">
        <cti:msg2 key=".hint" htmlEscape="false"/>
    </div>
</i:simplePopup>

<!-- shared action forms -->
<form name="deleteSchedule" method="POST" action="/spring/stars/consumer/thermostat/schedule/delete">
    <input type="hidden" name="scheduleId">
    <input type="hidden" name="thermostatIds" value="${thermostatIds}">
    <tags:confirmDialog nameKey=".deleteConfirm" styleClass="smallSimplePopup f_blocker" submitName="delete" on=".delete"/>
</form>

<form name="sendSchedule" method="POST" action="/spring/stars/consumer/thermostat/schedule/send">
    <input type="hidden" name="scheduleId">
    <input type="hidden" name="thermostatIds" value="${thermostatIds}">
    <input type="hidden" name="temperatureUnit" value="${temperatureUnit}">
    <tags:confirmDialog nameKey=".sendConfirm" styleClass="smallSimplePopup f_blocker" submitName="send" on=".send"/>
</form>
<!--  END action forms -->

</cti:standardPage>