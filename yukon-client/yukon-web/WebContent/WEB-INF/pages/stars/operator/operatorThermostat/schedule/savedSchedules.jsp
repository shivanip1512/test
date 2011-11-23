<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
    
<cti:standardPage module="operator" page="thermostat.schedules.${pageName}">

<cti:includeCss link="/WebConfig/yukon/styles/thermostat.css"/>

<cti:msg var="timeFormatter" key="yukon.common.timeFormatter" />
<cti:includeScript link="${timeFormatter}"/>
<cti:includeScript link="/JavaScript/temperature.js"/>
<cti:includeScript link="/JavaScript/thermostatScheduleEditor.js"/>
<cti:includeScript link="/JavaScript/lib/JSON/2.0/json2.js"/>

<script>
var TIME_SLIDER = null;
Event.observe(window, 'load', function(){
    Yukon.ThermostatScheduleEditor.init({
        thermostat: {
            heat: {
                upper: new Temperature({degrees: parseFloat(${type.upperLimitHeat.value}), unit:'F'}),
                lower: new Temperature({degrees: parseFloat(${type.lowerLimitHeat.value}), unit:'F'})
            },
            cool: {
                upper: new Temperature({degrees: parseFloat(${type.upperLimitCool.value}), unit:'F'}),
                lower: new Temperature({degrees: parseFloat(${type.lowerLimitCool.value}), unit:'F'})
            },
            temperature: new Temperature({unit: '${temperatureUnit}'}),
            mode: '${type.defaultThermostatScheduleMode}',
            fan: '',
            secondsResolution: ${type.resolution.standardSeconds},
            secondsBetweenPeriods: ${type.minimumTimeBetweenPeriods.standardSeconds}
        }
    });
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
        <div class="startLabel fl"></div><span class="C"><i:inline key="yukon.web.defaults.celsius"/></span><span class="F"><i:inline key="yukon.web.defaults.fahrenheit"/></span>
    </div>
    <div class="track">
        <div class="handle"></div>
    </div>
    <div class="box fr tempHolder ${temperatureUnit}">
        <div class="endLabel fl"></div><span class="C"><i:inline key="yukon.web.defaults.celsius"/></span><span class="F"><i:inline key="yukon.web.defaults.fahrenheit"/></span>
    </div>
</div>

<table class="thermostatPageContent">
    <tr>
        <td> 
            <c:choose>
                <c:when test="${empty schedules and empty currentSchedule}">
                    <%-- THERMOSTAT NAMES --%>
                    <jsp:include page="/WEB-INF/pages/stars/operator/operatorThermostat/selectedThermostatsFragment.jsp" />
                    
                    <div class="helper">
                        <i:inline key=".noSchedulesHelper" />
                    </div>
                    <br>
                    <br>
                    <cti:button nameKey="help" styleClass="help fl"/>
                    <cti:button nameKey="create" styleClass="create fl"/>
                    <cti:url var="historyUrl" value="/spring/stars/operator/thermostat/history/view">
                        <cti:param name="accountId" value="${accountId}" />
                        <cti:param name="thermostatIds" value="${thermostatIds}"/>
                    </cti:url>
                    <cti:button nameKey="history" href="${historyUrl}" />
                </c:when>
                <c:otherwise>
                    <div class="schedules fl">
                        <%-- THERMOSTAT NAMES --%>
                        <jsp:include page="/WEB-INF/pages/stars/operator/operatorThermostat/selectedThermostatsFragment.jsp" />
                        <c:if test="${not empty currentSchedule}">
                            <div class="paddedContainer">
                                <tags:thermostatScheduleWidget schedule="${currentSchedule}"
                                    thermostatId="${thermostatId}"
                                    thermostatIds="${thermostatIds}"
                                    accountId="${accountId}"
                                    temperatureUnit="${temperatureUnit}"
                                    actionPath="/spring/stars/operator/thermostatSchedule/save"
                                    thermostatType="${thermostatType}"
                                    styleClass="vh"/>
                            </div>
                        </c:if>
                        <div class="box clear">
                            <div class="fr">
                                <cti:button nameKey="create" styleClass="create fl"/>
                                <cti:button nameKey="help" styleClass="help fl"/>
                                <cti:url var="historyUrl" value="/spring/stars/operator/thermostat/history/view">
                                    <cti:param name="accountId" value="${accountId}" />
                                    <cti:param name="thermostatIds" value="${thermostatIds}"/>
                                </cti:url>
                                <cti:button nameKey="history" href="${historyUrl}" />
                            </div>
                            <div class="tempControls fl">
                                <form method="post" action="/spring/stars/operator/thermostatSchedule/updateTemperaturePreference">
                                    <input type="hidden" name="accountId" value="${accountId}"/>
                                    <label><input name="units" type="radio" value="C" <c:if test="${temperatureUnit eq 'C'}" >checked="checked"</c:if>><i:inline key="yukon.web.defaults.celsius"/></label>
                                    <label><input name="units" type="radio" value="F" <c:if test="${temperatureUnit eq 'F'}" >checked="checked"</c:if>><i:inline key="yukon.web.defaults.fahrenheit"/></label>
                                </form>
                            </div>
                        </div>
                        <br>
                        <c:if test="${not empty schedules}">
                            <tags:sectionContainer2 nameKey="otherSchedules">
                                <c:forEach var="schedule" items="${schedules}" >
                                    <tags:thermostatScheduleWidget schedule="${schedule}"
                                        thermostatId="${thermostatId}"
                                        thermostatIds="${thermostatIds}"
                                        accountId="${accountId}"
                                        temperatureUnit="${temperatureUnit}"
                                        actionPath="/spring/stars/operator/thermostatSchedule/save"
                                        thermostatType="${thermostatType}"
                                        styleClass="vh"/>
                                </c:forEach>    
                            </tags:sectionContainer2>
                        </c:if>
                    </div>
                </c:otherwise>
            </c:choose>
        </td>
    </tr>
</table>

<!-- Create Schedule Wizard -->
<div class="schedule">
    <i:simplePopup titleKey=".createSchedule.title" id="createSchedule" on=".create" >
        <div class="f_wizard">
            <div class="f_page page_0">
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
            
                <div class="actionArea">
                    <div class="fr">
                        <cti:button nameKey="next" styleClass="f_next"/>
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
                                        actionPath="/spring/stars/operator/thermostatSchedule/save"
                                        thermostatType="${thermostatType}"/>
                    </c:forEach>
                </div>
            
                <div class="actions">
                    <div class="fr">
                        <cti:button nameKey="previous" styleClass="f_prev"/>
                        <cti:button nameKey="save" styleClass="save f_blocker" />
                        <cti:button nameKey="cancel" styleClass="cancel" />
                    </div>
                    <div class="fl">
                        <cti:button nameKey="recommendedSettings" renderMode="labeledImage" styleClass="createDefault"/>
                    </div>
                </div>
            </div>
        </div>
    </i:simplePopup>
</div>

<i:simplePopup titleKey=".help.title" id="help" on=".help">
<div class="help pad">
        <cti:msg2 key="yukon.web.modules.consumer.savedSchedules.hint" htmlEscape="false"/>
    </div>
</i:simplePopup>

<!-- shared action forms -->
<form name="deleteSchedule" method="POST" action="/spring/stars/operator/thermostatSchedule/delete">
    <input type="hidden" name="scheduleId">
    <input type="hidden" name="thermostatId" value="${thermostatId}">
    <input type="hidden" name="thermostatIds" value="${thermostatIds}">
    <input type="hidden" name="accountId" value="${accountId}">
    <cti:msg2 var="delete_message" key="yukon.web.modules.operator.thermostat.deleteConfirm" />
    <input type="hidden" name="message" value="${delete_message}"/>
    <tags:confirmDialog nameKey=".deleteConfirm" styleClass="smallSimplePopup f_blocker" submitName="delete" on=".delete"/>
</form>

<form name="sendSchedule" method="POST" action="/spring/stars/operator/thermostatSchedule/send">
    <input type="hidden" name="scheduleId">
    <input type="hidden" name="thermostatIds" value="${thermostatIds}">
    <input type="hidden" name="accountId" value="${accountId}">
    <input type="hidden" name="temperatureUnit" value="${temperatureUnit}">
    <cti:msg2 var="send_message" key="yukon.web.modules.operator.thermostat.sendConfirm" />
    <input type="hidden" name="message" value="${send_message}"/>
    <tags:confirmDialog nameKey=".sendConfirm" styleClass="smallSimplePopup f_blocker" submitName="send" on=".send"/>
</form>
<!--  END action forms -->
    
</cti:standardPage>
