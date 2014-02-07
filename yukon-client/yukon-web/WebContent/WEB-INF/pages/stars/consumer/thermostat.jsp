<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="consumer" page="thermostat">
    <cti:msgScope paths="modules.consumer.thermostat">
        <cti:standardMenu/>
        
        <cti:includeScript link="/JavaScript/temperature.js"/>
        <cti:includeScript link="/JavaScript/thermostatScheduleEditor.js"/>
        
        <cti:includeCss link="/WebConfig/yukon/styles/shared/thermostat.css"/>
        <cti:flashScopeMessages/>
        
        <script type="text/javascript">
        jQuery(function(){
            Yukon.ThermostatManualEditor.init({
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
            
            <c:set var="multipleThermostatsSelected" value="${fn:length(fn:split(thermostatIds, ',')) > 1}"></c:set>
        
        <h3>
            <i:inline key="yukon.web.modules.consumer.thermostat.header" /><br>
            <cti:msg var="label" key="${thermostatLabel}" htmlEscape="true"/>${label}
        </h3>
        
        <div style="text-align: center;">
            <c:if test="${multipleThermostatsSelected}">
                <cti:url var="allUrl" value="/stars/consumer/thermostat/view/all">
                    <cti:param name="thermostatIds" value="${thermostatIds}"></cti:param>
                </cti:url>
                <a href="${allUrl}"><cti:msg key="yukon.web.modules.consumer.thermostat.changeSelected" /></a><br><br>
            </c:if>
        </div>
        
        
        <c:if test="${autoModeEnabledCommandView}">
            <cti:msg key="yukon.web.modules.consumer.thermostat.autoModeDisclaimer" />
            <br><br>
        </c:if>
        
        
        <div class="plainText">
            <cti:msg key="yukon.web.modules.consumer.thermostat.instructionText" />
        </div>
        
        <br>
        
        <c:set var="runProgram" value="${event.runProgram}" />
        
        <!-- Thermostat settings table -->
        <div class="fl oh">
            <tags:thermostatManualEditor thermostat="${thermostat}"
                                         actionPath="/stars/consumer/thermostat/manual" 
                                         temperatureUnit="${temperatureUnit}"
                                         event="${event}"
                                         thermostatIds="${thermostatIds}"
                                         accountId="${accountId}"
                                         canEditLabel="true" 
                                         autoEnabledMode="${autoModeEnabledCommandView}"/>
        </div>
                
        <div class="plainText oh">
            <cti:msg key="yukon.web.modules.consumer.thermostat.stepText" />
            <cti:msg key="yukon.web.modules.consumer.thermostat.runProgramText" />
            <br><br>
            
            <form action="/stars/consumer/thermostat/runProgram" method="post" >
                <cti:csrfToken/>
                <input name="thermostatIds" type="hidden" value="${thermostatIds}" />
                <cti:msg var="runProgramText" key="yukon.web.modules.consumer.thermostat.runProgram" />
                <input id="temperatureUnitRun" type="hidden" name="temperatureUnit" value="F">
                <cti:button name="runProgram" type="submit" label="${runProgramText}" classes="f-blocker"/>
            </form>
            
            <%-- Auto Enabled Manual Page --%>
            <c:if test="${!autoModeEnabledCommandView && autoModeEnabled}">
                <cti:url var="autoEnabledManualUrl" value="/stars/consumer/thermostat/autoEnabledView">
                    <cti:param name="thermostatIds" value="${thermostatIds}"/>
                </cti:url>
                <cti:button nameKey="autoEnabledManual" href="${autoEnabledManualUrl}" classes="stacked"/>
            </c:if>
        </div>
    </cti:msgScope>
</cti:standardPage>
