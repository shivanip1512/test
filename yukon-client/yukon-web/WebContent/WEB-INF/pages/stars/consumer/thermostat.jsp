<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags/dr" prefix="dr"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>

<cti:standardPage module="consumer" page="thermostat">
    <cti:standardMenu/>
    
    <cti:includeScript link="/JavaScript/temp_conversion.js" />
    <cti:includeScript link="/JavaScript/manualThermostat.js" />
    
    <cti:msg var="degreesCelsius" key="yukon.dr.consumer.thermostat.degreesCelsius" />
    <cti:msg var="degreesFahrenheit" key="yukon.dr.consumer.thermostat.degreesFahrenheit" />
    <cti:msg var="holdConfirmOn" key="yukon.dr.consumer.thermostat.hold.on" javaScriptEscape="true"/>
    <cti:msg var="holdConfirmOff" key="yukon.dr.consumer.thermostat.hold.off" javaScriptEscape="true"/>
    
<script type="text/javascript">
YEvent.observeSelectorClick('#sendSettingsSubmit', function(event) {    
    var confirmPopup = $('confirmPopup');
    
    var parentElement = Event.findElement(event, 'table');

    var inputs = parentElement.getElementsByTagName('input');
    var i;
    for (i = 0; i< inputs.length; i++) {
        var input = inputs[i];
        var name = input.name;
        if (input.type=='submit'
            || input.type=='button'
            || name == 'accountId'
            || name == 'thermostatIds') {
            continue;
        }
        var formElement = confirmPopup.down('input[name='+name+']');
        if (name=='hold') {
            if (input.checked == true) {
                formElement.setAttribute('value', true);
            } else {
                formElement.setAttribute('value', false);
            }
        } else {
            formElement.setAttribute('value', $F(input));
        }

        if (name=='hold') {
            var confirmElement = $('holdConfirm').down('td.value');
            if (input.checked == true) {
                confirmElement.innerHTML = "${holdConfirmOn}";
            } else {
                confirmElement.innerHTML = "${holdConfirmOff}";
            }
        }
        
        if (name == 'temperature') {
            $('temperatureValueConfirm').innerHTML = $F(input);
        } else if (name=='temperatureUnit') {
            $('temperatureUnitConfirm').innerHTML = $F(input);
        }
    }
    confirmPopup.show();
});

YEvent.observeSelectorClick('#confirmCancel', function(event) {
    $('confirmPopup').hide();
});
</script> 
    
    <c:set var="multipleThermostatsSelected" value="${fn:length(fn:split(thermostatIds, ',')) > 1}"></c:set>
    
    <h3>
        <cti:msg key="yukon.dr.consumer.thermostat.header" /><br>
        <cti:msg var="label" key="${thermostatLabel}" htmlEscape="true"/><br>
        ${label}
    </h3>
    
    <div style="text-align: center;">
        <c:if test="${multipleThermostatsSelected}">
            <cti:url var="allUrl" value="/spring/stars/consumer/thermostat/view/all">
                <cti:param name="thermostatIds" value="${thermostatIds}"></cti:param>
            </cti:url>
            <a href="${allUrl}"><cti:msg key="yukon.dr.consumer.thermostat.changeSelected" /></a><br><br>
        </c:if>
    </div>

    <div class="plainText">
        <cti:msg key="yukon.dr.consumer.thermostat.instructionText" />
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
                                        
                                        <c:choose>
                                            <c:when test="${!multipleThermostatsSelected}">
                                                <form action="/spring/stars/consumer/thermostat/saveLabel" method="post">
                                                    <input name="thermostatIds" type="hidden" value="${thermostatIds}" />
                                                    <span id="editName" style="display: none;">
                                                        <input id="thermostatLabel" name="displayLabel" type="text" value="${label}" />
                                                        <cti:msg var="saveText" key="yukon.dr.consumer.thermostat.save" />
                                                        <input type="submit" value="${saveText}" />
                                                    </span> 
                                                    <span id="thermostatName">
                                                        ${label}
                                                    </span>
                                                    <span style="font-size: 11px;"><a href="javascript:editName()"><cti:msg key="yukon.dr.consumer.thermostat.edit" /></a></span>
                                                    
                                                </form>
                                            </c:when>
                                            <c:otherwise>
                                                <span id="thermostatName">
                                                    ${label}
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </table>
                            
                        </td>
                    </tr>
                    <tr>
                        <td class="boxContainer_content">
                            <form action="/spring/stars/consumer/thermostat/manual" method="post" >
                                <input name="thermostatIds" type="hidden" value="${thermostatIds}" />
                                <table width="100%" cellpadding="0" cellspacing="0">
                                    <tr>
                                        <td colspan="4" style="padding-bottom: .5em; font-size: .75em;">
                                            <cti:msg var="runProgramText" key="yukon.dr.consumer.thermostat.runProgram" />
                                            <cti:msg var="manualSettingsText" key="yukon.dr.consumer.thermostat.manualSettings" />
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
                                            <div class="clickable" onclick="changeTemp(1)"><cti:msg key="yukon.dr.consumer.thermostat.incrementTemp" /></div>
                                            <div class="clickable" onclick="changeTemp(-1)"><cti:msg key="yukon.dr.consumer.thermostat.decrementTemp" /></div>
                                        </td>
                                        
                                        <!-- Thermostat Modes -->
                                        <td style="vertical-align: top; padding-left: 20px;">
                                            <cti:theme var="arrow" key="yukon.dr.consumer.thermostat.arrow" default="/WebConfig/yukon/Icons/triangle-right.gif" url="true"/>
                                            
                                            <c:set var="eventMode" value="${(runProgram)? '' : event.modeString}" />
                                            <table width="100%" cellpadding="0" cellspacing="0">
                                                <tr>
                                                    <td colspan="2" style="font-weight: bold;">
                                                        <cti:msg key="yukon.dr.consumer.thermostat.mode" />
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td class="arrow"><img id="coolArrow" src="${arrow}" <c:if test="${eventMode != 'COOL'}">style="display: none;" </c:if>></td>
                                                    <td class="clickable subItem" onClick="setMode('COOL', this)">
                                                        <cti:msg key="yukon.dr.consumer.thermostat.mode.COOL" />
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td class="arrow"><img id="heatArrow" src="${arrow}" <c:if test="${eventMode != 'HEAT'}">style="display: none;"</c:if>></td>
                                                    <td class="clickable subItem" onClick="setMode('HEAT', this)">
                                                        <cti:msg key="yukon.dr.consumer.thermostat.mode.HEAT" />
                                                    </td>
                                                </tr>
                                                <!-- Heat pump has an extra mode setting -->
                                                <c:choose>
                                                    <c:when test="${thermostat.type == 'EXPRESSSTAT_HEAT_PUMP'}">
                                                        <tr>
                                                            <td class="arrow"><img id="emHeatArrow" src="${arrow}" <c:if test="${eventMode != 'EMERGENCY_HEAT'}">style="display: none;" </c:if>></td>
                                                            <td class="clickable subItem" onClick="setMode('EMERGENCY_HEAT', this)">
                                                                <cti:msg key="yukon.dr.consumer.thermostat.mode.EMERGENCY_HEAT" />
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
                                                    <td class="clickable subItem" onClick="setMode('OFF', this)">
                                                        <cti:msg key="yukon.dr.consumer.thermostat.mode.OFF" />
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
                                                        <cti:msg key="yukon.dr.consumer.thermostat.fan" />
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td class="arrow"><img id="autoArrow" src="${arrow}" <c:if test="${eventFanState != 'AUTO'}">style="display: none;" </c:if>></td>
                                                    <td class="clickable subItem" onClick="setFan('autoArrow', 'AUTO', this)">
                                                        <cti:msg key="yukon.dr.consumer.thermostat.fan.AUTO" />
                                                    </td>
                                                </tr>
                                                
                                                <!-- Utility Pro has an extra fan mode setting -->
                                                <c:choose>
                                                    <c:when test="${thermostat.type == 'UTILITY_PRO'}">
                                                        <tr>
                                                            <td class="arrow"><img id="circulateArrow" src="${arrow}" <c:if test="${eventFanState != 'CIRCULATE'}">style="display: none;" </c:if>></td>
                                                            <td class="clickable subItem" onClick="setFan('circulateArrow', 'CIRCULATE', this)">
                                                                <cti:msg key="yukon.dr.consumer.thermostat.fan.CIRCULATE" />
                                                            </td>
                                                        </tr>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <tr><td><span id="circulateArrow"></span></td></tr>
                                                    </c:otherwise>
                                                </c:choose>
                                                
                                                <tr>
                                                    <td class="arrow"><img id="onArrow" src="${arrow}" <c:if test="${eventFanState != 'ON'}">style="display: none;" </c:if>></td>
                                                    <td class="clickable subItem" onClick="setFan('onArrow', 'ON', this)">
                                                        <cti:msg key="yukon.dr.consumer.thermostat.fan.ON" />
                                                    </td>
                                                </tr>
                                            </table>
                                            <input id="fan" type="hidden" name="fan" value="${eventFanState}">
                                        </td>
                                    </tr>
                                    <tr> 
                                        <td colspan="4" class="subItem">
                                            <input id="holdCheck" type="checkbox" name="hold" <c:if test="${(not runProgram) && event.holdTemperature}">checked</c:if> />
                                            <label for="holdCheck"><cti:msg key="yukon.dr.consumer.thermostat.hold" /></label>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td colspan="4" style="text-align: center; padding-bottom: 5px;">
                                            <cti:msg var="saveText" key="yukon.dr.consumer.thermostat.submit" />
                                            <input id="sendSettingsSubmit" type="button" value="${saveText}" class="formSubmit"/>
                                        </td>
                                    </tr>
                                </table>
                            </form>
                            
                            
                        </td>
                    </tr>
                </table>
            </td>
            <td style="font-size: 11px; padding-left: 2em; vertical-align: top;">
                <cti:msg key="yukon.dr.consumer.thermostat.stepText" />
                <cti:msg key="yukon.dr.consumer.thermostat.runProgramText" />
                <br><br>
                <form action="/spring/stars/consumer/thermostat/runProgram" method="post" >
                    <input name="thermostatIds" type="hidden" value="${thermostatIds}" />
                    <cti:msg var="runProgramText" key="yukon.dr.consumer.thermostat.runProgram" />
                    <input id="temperatureUnitRun" type="hidden" name="temperatureUnit" value="F">
                    <input name="runProgram" type="submit" value="${runProgramText}" />
                </form>
            </td>
        </tr>
    </table>     
    
    <br>
    <div class="boxContainer">
        <div class="boxContainer_titleBar">
            <div class="boxContainer_title">
                <cti:msg key="yukon.dr.consumer.thermostat.historyTableTitle"/>
            </div>
        </div>
        <div class="boxContainer_content">
            <c:choose>
                <c:when test="${fn:length(eventHistoryList) == 0}">
                    <cti:msg key="yukon.dr.consumer.thermostat.noItems"/>
                </c:when>
                <c:otherwise>
                    <table class="compactResultsTable smallPadding">
                        <thead>
                            <tr>
                                <c:if test="${multipleThermostatsSelected}">
                                    <th><cti:msg key="yukon.dr.consumer.thermostat.thermostatHeader"/></th>
                                </c:if>
                                <th><cti:msg key="yukon.dr.consumer.thermostat.typeHeader"/></th>
                                <th><cti:msg key="yukon.dr.consumer.thermostat.dateHeader"/></th>
                                <th><cti:msg key="yukon.dr.consumer.thermostat.detailsHeader"/></th>
                            </tr>
                         </thead>
                         <tbody>
                            <c:forEach var="historyItem" items="${eventHistoryList}">
                                <tr class="<tags:alternateRow odd="" even="altRow"/>">
                                    <!-- Thermostat -->
                                    <c:if test="${multipleThermostatsSelected}">
                                        <td>
                                            ${fn:escapeXml(historyItem.thermostatName)}
                                        </td>
                                    </c:if>
                                    <!-- Type -->
                                    <td>
                                        <cti:msg key="${historyItem.eventType}"/>
                                    </td>
                                    <!-- Date -->
                                    <td>
                                        <cti:formatDate value="${historyItem.eventTime}" type="DATEHM" />
                                    </td>
                                    <!-- Details -->
                                    <td>
                                        <c:if test="${historyItem.eventType == 'MANUAL'}">
                                            <!-- Temperature and Degree Units -->
                                            <c:choose>
                                                <c:when test="${event.temperatureUnit == 'F'}">
                                                    <cti:msg key="yukon.dr.consumer.thermostat.manualDetailsTemp" arguments="${historyItem.manualTemp}"/> 
                                                    <cti:msg key="yukon.dr.consumer.thermostat.degreesFahrenheit"/>,
                                                </c:when>
                                                <c:otherwise>
                                                    <cti:msg key="yukon.dr.consumer.thermostat.manualDetailsTemp" arguments="${historyItem.manualTempInC}"/>
                                                    <cti:msg key="yukon.dr.consumer.thermostat.degreesCelsius"/>,
                                                </c:otherwise>
                                            </c:choose>
                                            <!-- Heat/Cool Mode -->
                                            <cti:msg key="yukon.dr.consumer.thermostat.unitMode" /> 
                                            <cti:msg key="${historyItem.manualMode}" />, 
                                            <!-- Fan Setting-->
                                            <cti:msg key="yukon.dr.consumer.thermostat.manualDetailsFan" /> 
                                            <cti:msg key="${historyItem.manualFan}" />
                                            <!-- Hold Setting -->
                                            <c:if test="${historyItem.manualHold == true}">
                                                (<cti:msg key="yukon.dr.consumer.thermostat.hold"/>)
                                            </c:if>
                                        </c:if>
                                        <c:if test="${historyItem.eventType == 'SCHEDULE'}">
                                            <!-- Schedule Name and Link -->
                                            <c:choose>
                                                <c:when test="${historyItem.scheduleName == null}">
                                                    <cti:msg key="yukon.dr.consumer.thermostat.deletedSchedule"/>, 
                                                </c:when>
                                                <c:otherwise>
                                                    <a href="/spring/stars/consumer/thermostat/schedule/view?thermostatIds=${thermostatIds}"><cti:msg key="yukon.web.modules.operator.thermostatManual.scheduleDetails" arguments="${historyItem.scheduleName}"/></a>,
                                                </c:otherwise>
                                            </c:choose> 
                                            <!-- Schedule Day Mode -->
                                            <cti:msg key="yukon.dr.consumer.thermostat.scheduleDetailsMode"/>
                                            <cti:msg key="yukon.dr.consumer.thermostat.${historyItem.scheduleMode}"/>
                                        </c:if>
                                        <c:if test="${historyItem.eventType == 'RESTORE'}">
                                            --
                                        </c:if>                                        
                                    </td>
                                </tr>
                            </c:forEach>
                         </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
    
    <%-- Confirm Dialog for send settings --%>
     <cti:msg2 key="yukon.dr.consumer.thermostat.sendConfirm.title" var="confirmDialogTitle"/>
     <tags:simplePopup title="${confirmDialogTitle}" id="confirmPopup" styleClass="smallSimplePopup">
        <c:set var="initalModeConfirm" value="yukon.dr.consumer.thermostat.mode.${eventMode}"/>
        <c:set var="initalFanConfirm" value="yukon.dr.consumer.thermostat.fan.${eventFanState}"/>
        
        <form action="/spring/stars/consumer/thermostat/manual" method="post">
        
            <input type="hidden" name="accountId"  value="${accountId}" />
            <input type="hidden" name="thermostatIds"  value="${thermostatIds}" />
            <input type="hidden" name="fan" value=""/>
            <input type="hidden" name="temperature"/>
            <input type="hidden" name="temperatureUnit" value=""/>
            <input type="hidden" name="mode" value="">
            <input type="hidden" name="hold"/>

            <div id="confirmMessage"><cti:msg2 key="yukon.dr.consumer.thermostat.sendConfirm.message"/></div>
            <br/>
            
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey="yukon.dr.consumer.thermostat.temperature" rowId="temperatureConfirm">
                    <span id="temperatureValueConfirm"></span>&deg;<span id="temperatureUnitConfirm"></span>
                </tags:nameValue2>
                
                <tags:nameValue2 nameKey="yukon.dr.consumer.thermostat.mode" rowId="modeConfirm">
                    <cti:msg2 key="${initalModeConfirm}"/>
                </tags:nameValue2>
                
                <tags:nameValue2 nameKey="yukon.dr.consumer.thermostat.fan" rowId="fanConfirm">
                    <cti:msg2 key="${initalFanConfirm}"/>
                </tags:nameValue2>
                
                <tags:nameValue2 nameKey="yukon.dr.consumer.thermostat.hold" rowId="holdConfirm">
                </tags:nameValue2>
                
            </tags:nameValueContainer2>
            
            <div class="actionArea">
                <cti:button key="ok" type="submit"/> 
                <cti:button key="cancel" id="confirmCancel" />
            </div>
        </form>
    </tags:simplePopup> 
    
</cti:standardPage>