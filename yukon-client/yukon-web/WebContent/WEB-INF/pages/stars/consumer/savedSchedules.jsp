<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="consumer" page="savedSchedules">
<cti:standardMenu/>

<cti:includeCss link="/WebConfig/yukon/styles/shared/thermostat.css"/>
<cti:msg var="timeFormatter" key="yukon.common.timeFormatter" />
<cti:includeScript link="${timeFormatter}"/>
<cti:includeScript link="/resources/js/common/yukon.temperature.js"/>
<cti:includeScript link="/resources/js/common/yukon.thermostat.js"/>

<cti:flashScopeMessages/>

<script>
var TIME_SLIDER = null;
$(function(){
    yukon.ThermostatScheduleEditor.init({        
        thermostat: {
            HEAT: {
                upper: new Temperature({degrees: parseFloat(${type.upperLimitHeat.value}), unit:'F'}),
                lower: new Temperature({degrees: parseFloat(${type.lowerLimitHeat.value}), unit:'F'}),
                temperature: new Temperature()
            },
            COOL: {
                upper: new Temperature({degrees: parseFloat(${type.upperLimitCool.value}), unit:'F'}),
                lower: new Temperature({degrees: parseFloat(${type.lowerLimitCool.value}), unit:'F'}),
                temperature: new Temperature()
            },
            mode: '${type.defaultThermostatScheduleMode}',
            fan: '${event.fanState}',
            secondsResolution: ${type.resolution.standardSeconds},
            secondsBetweenPeriods: ${type.minimumTimeBetweenPeriods.standardSeconds}
        },
        unit: '${temperatureUnit}'
    });
    
});
</script>

<h3>
    <cti:msg key="yukon.web.modules.consumer.savedSchedules.pageTitle" /><br>
    <c:forEach var="stat" items="${thermostats}">
        &nbsp;${stat.label}&nbsp;
    </c:forEach>
</h3>

<div id="timeSlider" class="slider">
    <div class="chevron"></div>
    <div class="startLabel fl"></div>
    <div class="track"></div>
    <div class="endLabel fr"></div>
</div>

<div id="tempSlider" class="slider">
    <div class="chevron"></div>
    <div class="box fl startLabel ${temperatureUnit}">
        <div class="tempHolder fl"></div><i:inline key="yukon.web.defaults.degree"/><span class="C">${celsius_char}</span><span class="F">${fahrenheit_char}</span>
    </div>
    <div class="track"></div>
    <div class="box fr endLabel ${temperatureUnit}">
        <div class="tempHolder fl"></div><i:inline key="yukon.web.defaults.degree"/><span class="C">${celsius_char}</span><span class="F">${fahrenheit_char}</span>
    </div>
</div>

<cti:url var="postUrl" value="/stars/consumer/thermostat/schedule/updateTemperaturePreference"/>
<c:choose>
    <c:when test="${empty schedules and empty currentSchedule}">
        <div class="helper">
            <i:inline key=".noSchedulesHelper" />
        </div>
        <br>
        <div class="tempControls fl">
            <form method="post" action="${postUrl}">
                <cti:csrfToken/>
                <label><input name="units" type="radio" value="C" <c:if test="${temperatureUnit eq 'C'}" >checked="checked"</c:if>><i:inline key="yukon.web.defaults.celsius"/></label>
                <label><input name="units" type="radio" value="F" <c:if test="${temperatureUnit eq 'F'}" >checked="checked"</c:if>><i:inline key="yukon.web.defaults.fahrenheit"/></label>
            </form>
        </div>
        <br>
        <br>
        <cti:button nameKey="help" icon="icon-help" classes="js-help fl"/>
        <cti:button nameKey="create" icon="icon-plus-green" classes="js-create fl"/>
    </c:when>
    <c:otherwise>
        <div class="schedules fl">
            <c:if test="${not empty currentSchedule}">
	            <tags:sectionContainer2 nameKey="lastSent">
	                <div>
	                    <tags:thermostatScheduleWidget schedule="${currentSchedule}"
	                        thermostatId="${thermostatId}"
	                        thermostatIds="${thermostatIds}"
	                        accountId="${customerAccount.accountId}"
	                        temperatureUnit="${temperatureUnit}"
	                        actionPath="/stars/consumer/thermostat/schedule"
	                        thermostatType="${thermostatType}"
	                        styleClass="vh"/>
	                </div>
                </tags:sectionContainer2>
            </c:if>
            <div class="box clear">
                <div class="fr button-container">
                    <cti:button nameKey="help" icon="icon-help" classes="js-help fl"/>
                    <cti:button nameKey="create" icon="icon-plus-green" classes="js-create fl"/>
                </div>
                <div class="tempControls fl">
                    <form method="post" action="${postUrl}">
                        <cti:csrfToken/>
                        <label><input name="units" type="radio" value="C" <c:if test="${temperatureUnit eq 'C'}" >checked="checked"</c:if>><i:inline key="yukon.web.defaults.celsius"/></label>
                        <label><input name="units" type="radio" value="F" <c:if test="${temperatureUnit eq 'F'}" >checked="checked"</c:if>><i:inline key="yukon.web.defaults.fahrenheit"/></label>
                    </form>
                </div>
            </div>
            <br>
            <c:if test="${not empty schedules}">
                <tags:sectionContainer2 nameKey="otherSchedules">
                    <c:forEach var="schedule" items="${schedules}">
                        <tags:thermostatScheduleWidget schedule="${schedule}"
                            thermostatId="${thermostatId}"
                            thermostatIds="${thermostatIds}"
                            accountId="${customerAccount.accountId}"
                            temperatureUnit="${temperatureUnit}"
                            actionPath="/stars/consumer/thermostat/schedule"
                            thermostatType="${thermostatType}"
                            styleClass="vh"/>
                    </c:forEach>    
                </tags:sectionContainer2>
            </c:if>
        </div>
    </c:otherwise>
</c:choose>

<div class="schedule">
<i:simplePopup titleKey=".createSchedule.title" id="createSchedule" on=".js-create" >
    <div class="js-wizard">
        <div class="js-page page_0">
            <div class="box">
                <div class="helper box">
                    <i:inline key=".modeHint" />
                </div>
                <div class="box pad">
                    <c:forEach var="mode" items="${allowedModes}" varStatus="status">
                        <label><input type="radio" name="defaultScheduleMode" value="${mode}" <c:if test="${status.first}">checked</c:if>> <cti:msg key="yukon.web.modules.operator.thermostatMode.${mode}" /> </label>
                        <br>
                    </c:forEach>
                </div>
            </div>
        
            <div class="action-area">
                <div class="fr">
                    <cti:button nameKey="next" classes="js-next"/>
                </div>
            </div>
        </div>
        
        <div class="js-page page_1">
            <div class="createSchedule box small">
                <c:forEach var="schedule" items="${defaultSchedules}">
                    <tags:thermostatScheduleEditor schedule="${schedule}"
                                                   thermostatId="${thermostatId}"
                                                   thermostatIds="${thermostatIds}"
                                                   accountId="${customerAccount.accountId}"
                                                   temperatureUnit="${temperatureUnit}"
                                                   actionPath="/stars/consumer/thermostat/schedule/save"
                                                   thermostatType="${thermostatType}"/>
                </c:forEach>
            </div>
        
            <div class="actions">
                <div class="fr">
                    <cti:button nameKey="cancel" classes="js-cancel"/>
                    <cti:button nameKey="previous" classes="js-prev"/>
                    <cti:button nameKey="save" classes="js-save js-blocker primary action"/>
                </div>
                <div class="fl">
                    <cti:button nameKey="recommendedSettings" renderMode="labeledImage" classes="js-createDefault" icon="icon-wrench"/>
                </div>
            </div>
        </div>
    </div>
</i:simplePopup>
</div>

<!-- help/hints popup -->
<i:simplePopup titleKey=".help.title" id="help" on=".js-help" options="{width:600}">
  <div class="help pad">
    <cti:msg2 key=".hint"/>
  </div>
</i:simplePopup>

</cti:standardPage>