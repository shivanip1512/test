<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags/dr" prefix="dr"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>

<link rel="stylesheet" href="../../WebConfig/yukon/styles/YukonGeneralStyles.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/yukon/styles/StandardStyles.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/yukon/styles/StarsConsumerStyles.css" type="text/css">
    
    <script language="JavaScript" type="text/javascript"  src="/JavaScript/temp_conversion.js"></script>
    <script language="JavaScript" type="text/javascript"  src="/JavaScript/manualThermostat.js"></script>
    
    <cti:msg var="degreesCelsius" key="yukon.dr.operator.thermostat.degreesCelsius" />
    <cti:msg var="degreesFahrenheit" key="yukon.dr.operator.thermostat.degreesFahrenheit" />
    
    <c:set var="multipleThermostatsSelected" value="${fn:length(fn:split(thermostatIds, ',')) > 1}"></c:set>
    
    <h3>
        <cti:msg key="yukon.dr.operator.thermostat.header" /><br>
        <cti:msg var="label" key="${thermostatLabel}" htmlEscape="true"/><br>
        ${label}
    </h3>
    
    <div style="text-align: center;">
        <c:if test="${multipleThermostatsSelected}">
            <c:url var="allUrl" value="/spring/stars/operator/thermostat/view/all">
                <c:param name="thermostatIds" value="${thermostatIds}"></c:param>
            </c:url>
            <a href="${allUrl}"><cti:msg key="yukon.dr.operator.thermostat.changeSelected" /></a><br><br>
        </c:if>
    </div>

    <div class="plainText">
        <cti:msg key="yukon.dr.operator.thermostat.instructionText" />
    </div>
    
    <br>
    
    <c:set var="runProgram" value="${event.runProgram}" />
    
    <table cellspacing="0" cellpadding="0">
        <tr>
            <td>
                <!-- Thermostat settings table -->
                <table class="boxContainer" cellspacing="0" cellpadding="0">
                    <tr>
                        <td class="boxContainer_titleBar">
                            <table style="width: 100%" cellspacing="0" cellpadding="0">
                                <tr>
                                    <td style="white-space: nowrap;">
                                        <span id="thermostatName">
	                                        ${label}
	                                    </span>
                                    </td>
                                </tr>
                            </table>
                            
                        </td>
                    </tr>
                    <tr>
                        <td class="boxContainer_content">
                            <form action="/spring/stars/operator/thermostat/manual" method="post" >
                                <input name="thermostatIds" type="hidden" value="${thermostatIds}" />
                                <table width="100%" cellpadding="0" cellspacing="0">
                                    <tr>
                                        <td colspan="4" style="padding-bottom: .5em; font-size: .75em;">
                                            <cti:msg var="runProgramText" key="yukon.dr.operator.thermostat.runProgram" />
                                            <cti:msg var="manualSettingsText" key="yukon.dr.operator.thermostat.manualSettings" />
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
                                            
                                            <input id="temperature" type="text" name="temperature" style="${tempStyle}" maxlength="2" class="temperature" value="${(runProgram)? '' : event.previousTemperature}" onblur="validateTemp()" <c:if test="${event.modeString == 'OFF'}">disabled="disabled"</c:if>>
                                            <input id="temperatureUnit" type="hidden" name="temperatureUnit" value="F">
                                            <div style="font-size: 11px;">
                                                <a id="celsiusLink" href="javascript:setTempUnits('C');">${degreesCelsius}</a><span id="celsiusSpan" style="display: none;">${degreesCelsius}</span> | 
                                                <a id="fahrenheitLink" style="display: none;" href="javascript:setTempUnits('F');">${degreesFahrenheit}</a><span id="fahrenheitSpan">${degreesFahrenheit}</span>
                                            </div>
                                        </td>
                                        <td style="padding-left: 5px; vertical-align: middle;"> 
                                            <div class="clickable" style="text-decoration: none;" onclick="changeTemp(1)"><cti:msg key="yukon.dr.operator.thermostat.incrementTemp" /></div>
                                            <div class="clickable" style="text-decoration: none;" onclick="changeTemp(-1)"><cti:msg key="yukon.dr.operator.thermostat.decrementTemp" /></div>
                                        </td>
                                        
                                        <!-- Thermostat Modes -->
                                        <td style="vertical-align: top; padding-left: 20px;">
                                            <cti:theme var="arrow" key="yukon.dr.operator.thermostat.arrow" default="/WebConfig/yukon/Icons/triangle-right.gif" url="true"/>
                                            
                                            <c:set var="eventMode" value="${(runProgram)? '' : event.modeString}" />
                                            <table width="100%" cellpadding="0" cellspacing="0">
                                                <tr>
                                                    <td colspan="2" style="font-weight: bold;">
                                                        <cti:msg key="yukon.dr.operator.thermostat.mode" />
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td class="arrow"><img id="coolArrow" src="${arrow}" <c:if test="${eventMode != 'COOL'}">style="display: none;" </c:if>></td>
                                                    <td class="clickable subItem" style="text-decoration: none;" onClick="setMode('COOL')">
                                                        <cti:msg key="yukon.dr.operator.thermostat.mode.COOL" />
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td class="arrow"><img id="heatArrow" src="${arrow}" <c:if test="${eventMode != 'HEAT'}">style="display: none;"</c:if>></td>
                                                    <td class="clickable subItem" style="text-decoration: none;" onClick="setMode('HEAT')">
                                                        <cti:msg key="yukon.dr.operator.thermostat.mode.HEAT" />
                                                    </td>
                                                </tr>
                                                <!-- Heat pump has an extra mode setting -->
                                                <c:choose>
                                                    <c:when test="${thermostat.type == 'EXPRESSSTAT_HEAT_PUMP'}">
                                                        <tr>
                                                            <td class="arrow"><img id="emHeatArrow" src="${arrow}" <c:if test="${eventMode != 'EMERGENCY_HEAT'}">style="display: none;" </c:if>></td>
                                                            <td class="clickable subItem" style="text-decoration: none;" onClick="setMode('EMERGENCY_HEAT')">
                                                                <cti:msg key="yukon.dr.operator.thermostat.mode.EMERGENCY_HEAT" />
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
                                                        <cti:msg key="yukon.dr.operator.thermostat.mode.OFF" />
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
                                                        <cti:msg key="yukon.dr.operator.thermostat.fan" />
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td class="arrow"><img id="autoArrow" src="${arrow}" <c:if test="${eventFanState != 'AUTO'}">style="display: none;" </c:if>></td>
                                                    <td class="clickable subItem" style="text-decoration: none;" onClick="setFan('autoArrow', 'AUTO')">
                                                        <cti:msg key="yukon.dr.operator.thermostat.fan.AUTO" />
                                                    </td>
                                                </tr>
                                                
                                                <!-- Utility Pro has an extra fan mode setting -->
                                                <c:choose>
                                                    <c:when test="${thermostat.type == 'UTILITY_PRO'}">
                                                        <tr>
                                                            <td class="arrow"><img id="circulateArrow" src="${arrow}" <c:if test="${eventFanState != 'CIRCULATE'}">style="display: none;" </c:if>></td>
                                                            <td class="clickable subItem" style="text-decoration: none;" onClick="setFan('circulateArrow', 'CIRCULATE')">
                                                                <cti:msg key="yukon.dr.operator.thermostat.fan.CIRCULATE" />
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
                                                        <cti:msg key="yukon.dr.operator.thermostat.fan.ON" />
                                                    </td>
                                                </tr>
                                            </table>
                                            <input id="fan" type="hidden" name="fan" value="${eventFanState}">
                                        </td>
                                    </tr>
                                    <tr> 
                                        <td colspan="4" class="subItem">
                                            <input id="holdCheck" type="checkbox" name="hold" <c:if test="${(not runProgram) && event.holdTemperature}">checked</c:if> />
                                            <label for="holdCheck"><cti:msg key="yukon.dr.operator.thermostat.hold" /></label>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td colspan="4" style="text-align: center; padding-bottom: 5px;">
                                            <cti:msg var="saveText" key="yukon.dr.operator.thermostat.submit" />
                                            <input type="submit" value="${saveText}" />
                                        </td>
                                    </tr>
                                </table>
                            </form>
                            
                            
                        </td>
                    </tr>
                </table>
            </td>
            <td style="font-size: 11px; padding-left: 2em; vertical-align: top;">
                <cti:msg key="yukon.dr.operator.thermostat.stepText" />
                <cti:msg key="yukon.dr.operator.thermostat.runProgramText" />
                <br><br>
                <form action="/spring/stars/operator/thermostat/manual" method="post" >
                    <input name="thermostatIds" type="hidden" value="${thermostatIds}" />
                    <cti:msg var="runProgramText" key="yukon.dr.operator.thermostat.runProgram" />
                    <input name="runProgram" type="submit" value="${runProgramText}" />
                </form>
            </td>
        </tr>
    </table>     

