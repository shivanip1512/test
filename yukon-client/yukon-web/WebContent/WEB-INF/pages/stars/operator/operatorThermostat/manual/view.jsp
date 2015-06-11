<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags/dr" prefix="dr"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="operator" page="thermostatManual.${pageNameSuffix}">

    <cti:includeScript link="/resources/js/common/yukon.temperature.js"/>
    <cti:includeScript link="/resources/js/common/yukon.thermostat.js"/>
    <cti:includeCss link="/WebConfig/yukon/styles/consumer/StarsConsumerStyles.css"/>
    <cti:includeCss link="/WebConfig/yukon/styles/shared/thermostat.css"/>
    
    <cti:msg var="degreesCelsius" key="yukon.web.modules.operator.thermostatManual.degreesCelsius" />
    <cti:msg var="degreesFahrenheit" key="yukon.web.modules.operator.thermostatManual.degreesFahrenheit" />
    <cti:msg var="holdConfirmOn" key="yukon.web.modules.operator.thermostatManual.hold.on" javaScriptEscape="true"/>
    <cti:msg var="holdConfirmOff" key="yukon.web.modules.operator.thermostatManual.hold.off" javaScriptEscape="true"/>

<script type="text/javascript">
$(function(){
    yukon.ThermostatManualEditor.init({
        thermostat: {
            HEAT: {
                upper: new Temperature({degrees: parseFloat(${thermostat.schedulableThermostatType.upperLimitHeat.value}), unit:'F'}),
                lower: new Temperature({degrees: parseFloat(${thermostat.schedulableThermostatType.lowerLimitHeat.value}), unit:'F'}),
                temperature: new Temperature({degrees: parseFloat(${event.previousHeatTemperature.value}), unit: 'F'})
            },
            COOL: {
                upper: new Temperature({degrees: parseFloat(${thermostat.schedulableThermostatType.upperLimitCool.value}), unit:'F'}),
                lower: new Temperature({degrees: parseFloat(${thermostat.schedulableThermostatType.lowerLimitCool.value}), unit:'F'}),
                temperature: new Temperature({degrees: parseFloat(${event.previousCoolTemperature.value}), unit: 'F'})
            },
            mode: '${event.mode}',
            fan: '${event.fanState}',
            deadband: ${deadband},
            autoEnabled: ${autoModeEnabledCommandView}
        },
        unit: '${temperatureUnit}'
    });
});
</script>
    <c:set var="multipleThermostatsSelected" value="${fn:length(thermostatNames) > 1}"></c:set>

    <%-- THERMOSTAT NAMES --%>
    <div>
        <jsp:include page="/WEB-INF/pages/stars/operator/operatorThermostat/selectedThermostatsFragment.jsp" />
    </div>
    
    <tags:sectionContainer2 nameKey="manualUiContainerHeader" styleClass="oh">
    
        <%-- INSTRUCTIONS --%>
        <div class="stacked">
            <cti:url var="scheduleUrl" value="/stars/operator/thermostatSchedule/savedSchedules">
                <cti:param name="accountId" value="${accountId}"/>
                <cti:param name="thermostatIds" value="${thermostatIds}"/>
            </cti:url>
        
            <c:if test="${autoModeEnabledCommandView}">
                <cti:msg key="yukon.web.modules.operator.thermostatManual.autoModeDisclaimer" />
                <br><br>
            </c:if>
            <cti:msg key="yukon.web.modules.operator.thermostatManual.instructionText" arguments="${scheduleUrl}" htmlEscape="false"/>
        </div>
        
        <c:set var="runProgram" value="${event.runProgram}" />
        
        <div class="fl oh">
        
            <!-- Thermostat settings table -->
            <c:choose>
                <c:when test="${multipleThermostatsSelected}">
                    <cti:msg var="settingsLabel" key="yukon.web.modules.operator.thermostatManual.multipleLabel" />
                </c:when>
                <c:otherwise>
                    <c:set var="settingsLabel" value="${thermostatNames[0]}"/>
                </c:otherwise>
            </c:choose>
            
            <tags:thermostatManualEditor thermostat="${thermostat}"
                                         actionPath="/stars/operator/thermostatManual/save" 
                                         temperatureUnit="${temperatureUnit}"
                                         event="${event}"
                                         thermostatIds="${thermostatIds}"
                                         accountId="${accountId}" 
                                         autoEnabledMode="${autoModeEnabledCommandView}"/>
        </div>
        <div style="padding-left:20px;font-size:11px;" class="oh">
        
            <%-- INSTRUCTIONS --%>
            <div class="clearfix stacked">
                <cti:msg key="yukon.web.modules.operator.thermostatManual.stepText" />
                <cti:msg key="yukon.web.modules.operator.thermostatManual.runProgramText" />
            </div>
            <%-- Auto Enabled Manual Page --%>
            <c:if test="${!autoModeEnabledCommandView && autoModeEnabled}">
                <cti:url var="autoEnabledManualUrl" value="/stars/operator/thermostatManual/autoEnabledView">
                    <cti:param name="accountId" value="${accountId}" />
                    <cti:param name="thermostatIds" value="${thermostatIds}"/>
                </cti:url>
                <div class="clearfix stacked"><cti:button nameKey="autoEnabledManual" href="${autoEnabledManualUrl}" /></div>
            </c:if>
            
            <%-- RUN PROGRAM BUTTON --%>
            <form action="/stars/operator/thermostatManual/runProgram" method="post" >
                <cti:csrfToken/>
                <input name="accountId" type="hidden" value="${accountId}" />
                <input name="thermostatIds" type="hidden" value="${thermostatIds}" />
                <cti:msg var="runProgramText" key="yukon.web.modules.operator.thermostatManual.runProgram" />
                <input id="temperatureUnitRun" type="hidden" name="temperatureUnit" value="F">
                <div class="clearfix stacked">
                    <cti:button type="submit" name="runProgram" label="${runProgramText}"/>
                </div>
            </form>
            <cti:url var="historyUrl" value="/stars/operator/thermostat/history/view">
                <cti:param name="accountId" value="${accountId}" />
                <cti:param name="thermostatIds" value="${thermostatIds}"/>
            </cti:url>
            <div class="clearfix stacked"><cti:button nameKey="history" href="${historyUrl}" /></div>
        </div>
    
    </tags:sectionContainer2>
     
</cti:standardPage>
