<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags/dr" prefix="dr"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="operator" page="thermostatManual">

	<cti:includeScript link="/JavaScript/temp_conversion.js"/>
	<cti:includeScript link="/JavaScript/manualThermostat.js"/>
	<cti:includeCss link="/WebConfig/yukon/styles/StarsConsumerStyles.css"/>
	<cti:includeCss link="/WebConfig/yukon/styles/operator/thermostat.css"/>
    
    <cti:msg var="degreesCelsius" key="yukon.web.modules.operator.thermostatManual.degreesCelsius" />
    <cti:msg var="degreesFahrenheit" key="yukon.web.modules.operator.thermostatManual.degreesFahrenheit" />
    
    <table class="thermostatPageContent">
    	<tr>
    	
    		<%-- THE SCHEDULE UI --%>
    		<td>
   
   				<tags:formElementContainer nameKey="manualUiContainerHeader">
   				
   					<%-- INSTRUCTIONS --%>
				    <div class="plainText">
				    	<cti:url var="scheduleUrl" value="/spring/stars/operator/thermostatSchedule/view">
				    		<cti:param name="accountId" value="${accountId}"/>
				    		<cti:param name="thermostatIds" value="${thermostatIds}"/>
				    	</cti:url>
				        <cti:msg key="yukon.web.modules.operator.thermostatManual.instructionText" arguments="${scheduleUrl}" htmlEscape="false"/>
				    </div>
				    <br>
    
				    <c:set var="runProgram" value="${event.runProgram}" />
				    
				    <table cellspacing="0" cellpadding="0">
				        <tr>
				            <td style="vertical-align:top;">
				            
				                <!-- Thermostat settings table -->
							    <c:choose>
							    	<c:when test="${fn:length(thermostatNames) > 1}">
							    		<cti:msg var="settingsLabel" key="yukon.web.modules.operator.thermostatManual.multipleLabel" />
							    	</c:when>
							    	<c:otherwise>
							    		<c:set var="settingsLabel" value="${thermostatNames[0]}"/>
							    	</c:otherwise>
							    </c:choose>
    
				                <table class="boxContainer" cellspacing="0" cellpadding="0">
				                    <tr>
				                        <td class="boxContainer_titleBar">
				                            <table style="width: 100%" cellspacing="0" cellpadding="0">
				                                <tr>
				                                    <td style="white-space: nowrap;">
				                                        <span id="thermostatName">
					                                        ${settingsLabel}
					                                    </span>
				                                    </td>
				                                </tr>
				                            </table>
				                            
				                        </td>
				                    </tr>
				                    <tr>
				                        <td class="boxContainer_content">
				                            <form action="/spring/stars/operator/thermostatManual/save" method="post" >
				                                <input name="accountId" type="hidden" value="${accountId}" />
				                                <input name="thermostatIds" type="hidden" value="${thermostatIds}" />
				                                <table width="100%" cellpadding="0" cellspacing="0">
				                                    <tr>
				                                        <td colspan="4" style="padding-bottom: .5em; font-size: .75em;">
				                                            <cti:msg var="runProgramText" key="yukon.web.modules.operator.thermostatManual.runProgram" />
				                                            <cti:msg var="manualSettingsText" key="yukon.web.modules.operator.thermostatManual.manualSettings" />
				                                            <c:if test="${not empty event.date}">
				                                                <cti:formatDate value="${event.date}" type="BOTH" /> - ${(runProgram)? runProgramText : manualSettingsText}
				                                            </c:if>
				                                        </td>
				                                    </tr>
				                                    <tr> 
				                                        <td style="text-align: center;"> 
				                                            <c:set var="tempStyle" value="" />
				                                            <c:if test="${event.modeString == 'COOL'}">
				                                                <c:set var="tempStyle" value="color: blue;" />
				                                            </c:if>
				                                            <c:if test="${event.modeString == 'HEAT' || event.modeString == 'EMERGENCY_HEAT'}">
				                                                <c:set var="tempStyle" value="color: red;" />
				                                            </c:if>
				                                            
				                                            <input id="temperature" type="text" name="temperature" style="${tempStyle}" maxlength="2" class="temperature" value="${(runProgram)? '' : event.previousTemperatureForUnit}" onblur="validateTemp()" <c:if test="${event.modeString == 'OFF'}">disabled="disabled"</c:if>>
				                                            <input id="temperatureUnit" type="hidden" name="temperatureUnit" value="${event.temperatureUnit}">
				                                            <div style="font-size: 11px;">
				                                                <c:choose>
				                                                    <c:when test="${event.temperatureUnit == 'F'}">
				                                                        <a id="celsiusLink" href="javascript:setTempUnits('C');">${degreesCelsius}</a><span id="celsiusSpan" style="display: none;">${degreesCelsius}</span> | 
				                                                        <a id="fahrenheitLink" style="display: none;" href="javascript:setTempUnits('F');">${degreesFahrenheit}</a><span id="fahrenheitSpan">${degreesFahrenheit}</span>
				                                                    </c:when>
				                                                    <c:otherwise>
				                                                        <a id="celsiusLink" style="display: none;" href="javascript:setTempUnits('C');">${degreesCelsius}</a><span id="celsiusSpan">${degreesCelsius}</span> | 
				                                                        <a id="fahrenheitLink" href="javascript:setTempUnits('F');">${degreesFahrenheit}</a><span id="fahrenheitSpan" style="display: none;">${degreesFahrenheit}</span>
				                                                    </c:otherwise>
				                                                </c:choose>
				                                            </div>
				                                        </td>
				                                        <td style="padding-left: 5px; vertical-align: middle;"> 
				                                            <div class="clickable" style="text-decoration: none;" onclick="changeTemp(1)"><cti:msg key="yukon.web.modules.operator.thermostatManual.incrementTemp" /></div>
				                                            <div class="clickable" style="text-decoration: none;" onclick="changeTemp(-1)"><cti:msg key="yukon.web.modules.operator.thermostatManual.decrementTemp" /></div>
				                                        </td>
				                                        
				                                        <!-- Thermostat Modes -->
				                                        <td style="vertical-align: top; padding-left: 20px;">
				                                            <cti:theme var="arrow" key="yukon.web.modules.operator.thermostatManual.arrow" default="/WebConfig/yukon/Icons/triangle-right.gif" url="true"/>
				                                            
				                                            <c:set var="eventMode" value="${(runProgram)? '' : event.modeString}" />
				                                            <table width="100%" cellpadding="0" cellspacing="0">
				                                                <tr>
				                                                    <td colspan="2" style="font-weight: bold;">
				                                                        <cti:msg key="yukon.web.modules.operator.thermostatManual.mode" />
				                                                    </td>
				                                                </tr>
				                                                <tr>
				                                                    <td class="arrow"><img id="coolArrow" src="${arrow}" <c:if test="${eventMode != 'COOL'}">style="display: none;" </c:if>></td>
				                                                    <td class="clickable subItem" style="text-decoration: none;" onClick="setMode('COOL')">
				                                                        <cti:msg key="yukon.web.modules.operator.thermostatManual.mode.COOL" />
				                                                    </td>
				                                                </tr>
				                                                <tr>
				                                                    <td class="arrow"><img id="heatArrow" src="${arrow}" <c:if test="${eventMode != 'HEAT'}">style="display: none;"</c:if>></td>
				                                                    <td class="clickable subItem" style="text-decoration: none;" onClick="setMode('HEAT')">
				                                                        <cti:msg key="yukon.web.modules.operator.thermostatManual.mode.HEAT" />
				                                                    </td>
				                                                </tr>
				                                                <!-- Heat pump has an extra mode setting -->
				                                                <c:choose>
				                                                    <c:when test="${thermostat.type == 'EXPRESSSTAT_HEAT_PUMP'}">
				                                                        <tr>
				                                                            <td class="arrow"><img id="emHeatArrow" src="${arrow}" <c:if test="${eventMode != 'EMERGENCY_HEAT'}">style="display: none;" </c:if>></td>
				                                                            <td class="clickable subItem" style="text-decoration: none;" onClick="setMode('EMERGENCY_HEAT')">
				                                                                <cti:msg key="yukon.web.modules.operator.thermostatManual.mode.EMERGENCY_HEAT" />
				                                                            </td>
				                                                        </tr>
				                                                    </c:when>
				                                                    <c:otherwise>
				                                                        <!-- place holder for javascript -->
				                                                        <tr><td><span id="emHeatArrow"></span></td></tr>
				                                                    </c:otherwise>
				                                                </c:choose>
				                                                <tr>
				                                                    <td class="arrow"><img id="offArrow" src="${arrow}" <c:if test="${eventMode != 'OFF'}">style="display: none;" </c:if>></td>
				                                                    <td class="clickable subItem" style="text-decoration: none;" onClick="setMode('OFF')">
				                                                        <cti:msg key="yukon.web.modules.operator.thermostatManual.mode.OFF" />
				                                                    </td>
				                                                </tr>
				                                            </table>
				                                            <input id="mode" type="hidden" name="mode" value="${eventMode}">
				                                        </td>
				                                        
				                                        <!-- Thermostat Fan states -->
				                                        <td style="vertical-align: top; padding-left: 20px;">
				                                            
				                                            <c:set var="eventFanState" value="${(runProgram)? '' : event.fanStateString}" />
				                                            <table width="100%" cellpadding="0" cellspacing="0">
				                                                <tr>
				                                                    <td style="font-weight: bold;" colspan="2">
				                                                        <cti:msg key="yukon.web.modules.operator.thermostatManual.fan" />
				                                                    </td>
				                                                </tr>
				                                                <tr>
				                                                    <td class="arrow"><img id="autoArrow" src="${arrow}" <c:if test="${eventFanState != 'AUTO'}">style="display: none;" </c:if>></td>
				                                                    <td class="clickable subItem" style="text-decoration: none;" onClick="setFan('autoArrow', 'AUTO')">
				                                                        <cti:msg key="yukon.web.modules.operator.thermostatManual.fan.AUTO" />
				                                                    </td>
				                                                </tr>
				                                                
				                                                <!-- Utility Pro has an extra fan mode setting -->
				                                                <c:choose>
				                                                    <c:when test="${thermostat.type == 'UTILITY_PRO'}">
				                                                        <tr>
				                                                            <td class="arrow"><img id="circulateArrow" src="${arrow}" <c:if test="${eventFanState != 'CIRCULATE'}">style="display: none;" </c:if>></td>
				                                                            <td class="clickable subItem" style="text-decoration: none;" onClick="setFan('circulateArrow', 'CIRCULATE')">
				                                                                <cti:msg key="yukon.web.modules.operator.thermostatManual.fan.CIRCULATE" />
				                                                            </td>
				                                                        </tr>
				                                                    </c:when>
				                                                    <c:otherwise>
				                                                        <tr><td><span id="circulateArrow"></span></td></tr>
				                                                    </c:otherwise>
				                                                </c:choose>
				                                                
				                                                <tr>
				                                                    <td class="arrow"><img id="onArrow" src="${arrow}" <c:if test="${eventFanState != 'ON'}">style="display: none;" </c:if>></td>
				                                                    <td class="clickable subItem" style="text-decoration: none;" onClick="setFan('onArrow', 'ON')">
				                                                        <cti:msg key="yukon.web.modules.operator.thermostatManual.fan.ON" />
				                                                    </td>
				                                                </tr>
				                                            </table>
				                                            <input id="fan" type="hidden" name="fan" value="${eventFanState}">
				                                        </td>
				                                    </tr>
				                                    <tr> 
				                                        <td colspan="4" class="subItem">
				                                            <input id="holdCheck" type="checkbox" name="hold" <c:if test="${(not runProgram) && event.holdTemperature}">checked</c:if> />
				                                            <label for="holdCheck"><cti:msg key="yukon.web.modules.operator.thermostatManual.hold" /></label>
				                                        </td>
				                                    </tr>
				                                    <tr>
				                                        <td colspan="4" style="text-align: center; padding-bottom: 5px;">
				                                            <cti:msg var="saveText" key="yukon.web.modules.operator.thermostatManual.submit" />
				                                            <input type="submit" value="${saveText}" style="width:80px;"/>
				                                        </td>
				                                    </tr>
				                                </table>
				                            </form>
				                            
				                            
				                        </td>
				                    </tr>
				                </table>
				            </td>
				            
				            <td style="vertical-align:top;padding-left:20px;font-size:11px;">
				            
				            	<%-- INSTRUCTIONS --%>
				                <cti:msg key="yukon.web.modules.operator.thermostatManual.stepText" />
				                <cti:msg key="yukon.web.modules.operator.thermostatManual.runProgramText" />
				                
				                <%-- RUN PROGRAM BUTTON --%>
				                <br><br>
				                <form action="/spring/stars/operator/thermostatManual/runProgram" method="post" >
				                	<input name="accountId" type="hidden" value="${accountId}" />
				                    <input name="thermostatIds" type="hidden" value="${thermostatIds}" />
				                    <cti:msg var="runProgramText" key="yukon.web.modules.operator.thermostatManual.runProgram" />
				                    <input id="temperatureUnitRun" type="hidden" name="temperatureUnit" value="F">
				                    <input name="runProgram" type="submit" value="${runProgramText}" style="width:100px;"/>
				                </form>
				                
				            </td>
				        </tr>
				    </table>
				    
				</tags:formElementContainer>
				
			</td>
			
			<td class="selectedThermostatsTd">
    			<%-- THERMOSTAT NAMES --%>
    			<jsp:include page="/WEB-INF/pages/stars/operator/operatorThermostat/selectedThermostatsFragment.jsp" />
    		</td>
			
		</tr>
		
	</table>
    
</cti:standardPage>
