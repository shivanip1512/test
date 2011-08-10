<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="consumer" page="thermostat">
    <cti:msgScope paths="modules.consumer.thermostat">
        <cti:standardMenu/>
        
        <cti:includeScript link="/JavaScript/thermostatScheduleEditor.js"/>
        
        <cti:includeCss link="/WebConfig/yukon/styles/thermostat.css"/>
        <cti:includeCss link="/WebConfig/yukon/styles/yukonUIToolkit/yukonUiToolkit.css"/>
        
        <cti:flashScopeMessages/>
        
        <script type="text/javascript">
        Event.observe(window, 'load', function(){
            Yukon.ThermostatManualEditor.init({
                currentUnit: '${temperatureUnit}',
                upperHeatF: parseFloat(${scheduleableThermostatType.upperLimitHeat.value}),
                lowerHeatF: parseFloat(${scheduleableThermostatType.lowerLimitHeat.value}),
                upperCoolF: parseFloat(${scheduleableThermostatType.upperLimitCool.value}),
                lowerCoolF: parseFloat(${scheduleableThermostatType.lowerLimitCool.value})
            });
        });
        </script> 
            
            <c:set var="multipleThermostatsSelected" value="${fn:length(fn:split(thermostatIds, ',')) > 1}"></c:set>
        
        <h3>
            <i:inline key="yukon.web.modules.consumer.thermostat.header" /><br>
            <cti:msg var="label" key="${thermostatLabel}" htmlEscape="true"/>${label}
        </h3>
        
        <div style="text-align: center;">
            <c:if test="${multipleThermostatsSelected}">
                <cti:url var="allUrl" value="/spring/stars/consumer/thermostat/view/all">
                    <cti:param name="thermostatIds" value="${thermostatIds}"></cti:param>
                </cti:url>
                <a href="${allUrl}"><cti:msg key="yukon.web.modules.consumer.thermostat.changeSelected" /></a><br><br>
            </c:if>
        </div>
        
        <div class="plainText">
            <cti:msg key="yukon.web.modules.consumer.thermostat.instructionText" />
        </div>
        
        <br>
        
        <c:set var="runProgram" value="${event.runProgram}" />
        
        <!-- Thermostat settings table -->
        <div class="fl oh">
            <tags:thermostatManualEditor actionPath="/spring/stars/consumer/thermostat/manual" 
                                             temperatureUnit="${temperatureUnit}"
                                             event="${event}"
                                             thermostatIds="${thermostatIds}"
                                             scheduleableThermostatType="${scheduleableThermostatType}"
                                             accountId="${accountId}"
                                             thermostatLabel="${label}"
                                             canEditLabel="true"/>
        </div>
                
        <div class="plainText oh">
            <cti:msg key="yukon.web.modules.consumer.thermostat.stepText" />
            <cti:msg key="yukon.web.modules.consumer.thermostat.runProgramText" />
            <br><br>
            <form action="/spring/stars/consumer/thermostat/runProgram" method="post" >
                <input name="thermostatIds" type="hidden" value="${thermostatIds}" />
                <cti:msg var="runProgramText" key="yukon.web.modules.consumer.thermostat.runProgram" />
                <input id="temperatureUnitRun" type="hidden" name="temperatureUnit" value="F">
                <input name="runProgram" type="submit" value="${runProgramText}" class="f_blocker" />
            </form>
        </div>
    </cti:msgScope>
</cti:standardPage>
