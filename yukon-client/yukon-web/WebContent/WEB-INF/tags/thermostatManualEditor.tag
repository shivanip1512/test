<%@ attribute name="thermostat" required="true" type="com.cannontech.stars.dr.hardware.model.Thermostat" %>
<%@ attribute name="event" required="true" type="com.cannontech.stars.dr.thermostat.model.ThermostatManualEvent"%>
<%@ attribute name="canEditLabel" required="false" type="java.lang.Boolean" description="Allow editing of the Thermostat label?  (defaults to false)"%>
<%@ attribute name="thermostatIds" required="true" type="java.lang.String" description="id(s) of the thermostat(s) we are sending manual commands to."%>
<%@ attribute name="accountId" required="true" type="java.lang.String" description="ID of the account we are operating on." %>
<%@ attribute name="temperatureUnit" required="true" type="java.lang.String" description="Acceptable units are: ['F', 'C'] determines the initial unit." %>
<%@ attribute name="actionPath" required="true" type="java.lang.String" description="Application context will be handled inside the tag. Just pass a bare url."%>
<%@ attribute name="autoEnabledMode" required="false" type="java.lang.Boolean" description="Turns on the auto mode functionality and changes the gui to take deadbands into account" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div id="validationMessage" class="user-message error dn">
    <i:inline key="yukon.web.modules.operator.thermostatManual.temperature.invalid"/>
</div>

<div class="titled-container box-container manualThermostat box fl">
    <div class="title-bar clearfix">
        <h3 class="title">
            <c:set var="multipleThermostatsSelected" value="${fn:length(fn:split(thermostatIds, ',')) > 1}"></c:set>
            <c:choose>
                <c:when test="${not multipleThermostatsSelected and canEditLabel}">
                    <form:form action="/stars/consumer/thermostat/saveLabel" modelAttribute="thermostat" method="post">
                        <cti:csrfToken/>
                        <input name="thermostatIds" type="hidden" value="${thermostatIds}" />
                        <form:hidden path="id"/>
    
                        <span id="editName" style="display: none;">
                            <form:input path="deviceLabel" maxlength="60" />
                            <cti:button nameKey="save" type="submit" classes="js-blocker"/>
                            <cti:button nameKey="cancel" classes="cancelLabelEdit"/>
                        </span> 
                        <span id="thermostatName">
                            <spring:escapeBody>${fn:escapeXml(thermostat.deviceLabel)}</spring:escapeBody>
                            <a href="javascript:void(0)" class="editLabel"><i:inline key=".edit" /></a>
                        </span>
                    </form:form>
                </c:when>
                <c:otherwise>
                    <span id="thermostatName">
                        <c:choose>
                            <c:when test="${multipleThermostatsSelected}">
                                <i:inline key="yukon.web.modules.operator.thermostatManual.multipleLabel"/>
                            </c:when>
                            <c:otherwise>
                                <spring:escapeBody>${fn:escapeXml(thermostat.deviceLabel)}</spring:escapeBody>
                            </c:otherwise>
                        </c:choose>
                    </span>
                </c:otherwise>
            </c:choose>
        </h3>
    </div>
    <div class="content">
        <div class="box clear" style="padding-bottom: .5em; font-size: .75em;">
            <cti:msg2 var="runProgramText" key="yukon.web.modules.operator.thermostatManual.runProgram" />
            <cti:msg2 var="manualSettingsText" key="yukon.web.modules.operator.thermostatManual.manualSettings" />
            <c:if test="${not empty event.date}">
                <cti:formatDate value="${event.date}" type="BOTH" /> - ${(event.runProgram)? runProgramText : manualSettingsText}
            </c:if>
        </div>
        
        <div class="box temperatureDisplay fl">
             <div class="box fl">
                <!-- Cool Temperature -->
                <div class="coolDiv">
                    <input class="${event.modeString} temperature fl" data-temperatureMode="COOL" type="text" name="temperature_display" maxlength="4" <c:if test="${event.modeString == 'OFF'}">disabled="disabled"</c:if>>
                    <input type="hidden"  name="temperature" value="${(event.runProgram)? '' : event.previousCoolTemperature.value}" />
                    <input type="hidden" name="temperatureUnit" value="F" /> <!-- currently always stored as Fahrenheit -->
                    <div class="box fl temperatureAdjust"> 
                        <div data-temperatureMode="COOL" class="clickable up" ><cti:msg2 key="yukon.web.modules.operator.thermostatManual.incrementTemp"/></div>
                        <div data-temperatureMode="COOL" class="clickable down"><cti:msg2 key="yukon.web.modules.operator.thermostatManual.decrementTemp"/></div>
                    </div>
                </div>

                <!-- Heat Temperature -->
                <div class="heatDiv">
                    <input class="${event.modeString} temperature fl" data-temperatureMode="HEAT" type="text" name="temperature_display" maxlength="4" <c:if test="${event.modeString == 'OFF'}">disabled="disabled"</c:if>>
                    <input type="hidden"  name="temperature" value="${(event.runProgram)? '' : event.previousHeatTemperature.value}" />
                    <div class="box fl temperatureAdjust"> 
                        <div data-temperatureMode="HEAT" class="clickable up" ><cti:msg2 key="yukon.web.modules.operator.thermostatManual.incrementTemp"/></div>
                        <div data-temperatureMode="HEAT" class="clickable down"><cti:msg2 key="yukon.web.modules.operator.thermostatManual.decrementTemp"/></div>
                    </div>
                </div>

                <div class="temperatureUnit clear">
                    <ul class="list-piped">
                        <li class="unit <c:if test="${temperatureUnit == 'C'}">selected</c:if>" unit="C"><i:inline key="yukon.common.celsius" /></li>
                        <li class="unit <c:if test="${temperatureUnit == 'F'}">selected</c:if>" unit="F"><i:inline key="yukon.common.fahrenheit" /></li>
                    </ul>
                </div>
            </div>
        </div>    
            
        <div class="box fl">
            <!-- Thermostat Modes -->
            <div class="thermostatModes box fl">
                <h2><i:inline key="yukon.web.modules.operator.thermostatManual.mode" /></h2>
                
                <ul class="box">
                <c:forEach var="thermostatMode" items="${thermostat.schedulableThermostatType.supportedModes}">
                    <li class="mode ${(event.runProgram)? '' : (event.mode eq thermostatMode? 'selected' : '' )}" mode="${thermostatMode}">
                        <c:if test="${autoEnabledMode || thermostatMode != 'AUTO'}">
                            <i:inline key="yukon.web.modules.operator.thermostatManual.mode.${thermostatMode}" />
                        </c:if>
                    </li>
                </c:forEach>
                </ul>
                <input id="mode" type="hidden" name="mode" value="${(event.runProgram)? '' : event.mode}">
            </div>
            
            <!-- Fan States -->
            <div class="fanStates box fl">
                <h2><i:inline key="yukon.web.modules.operator.thermostatManual.fan" /></h2>
                
                <ul class="box">
                <c:forEach var="fanState" items="${thermostat.schedulableThermostatType.supportedFanStates}">
                    <li class="state fan ${(event.runProgram)? '' : (event.fanStateString eq fanState? 'selected' : '' )}" state="${fanState}">
                        <i:inline key=".fan.${fanState}" />
                    </li>
                </c:forEach>
                </ul>
                <input id="fan" type="hidden" name="fan" value="${(event.runProgram)? '' : event.fanStateString}">
            </div>
        </div>
        
        <div class="thermostatHold box clear">
            <input id="holdCheck" type="checkbox" name="hold" class="hold" <c:if test="${(not event.runProgram) && event.holdTemperature}">checked</c:if> />
            <label for="holdCheck"><i:inline key="yukon.web.modules.operator.thermostatManual.hold" /></label>
        </div>
        
        <div class="action-area">
            <cti:msg2 var="saveText" key="yukon.web.modules.operator.thermostatManual.submit" />
            <input type="hidden" id="confirmPopup_id" value = "confirmPopup_${event.eventId}" />
            <button id="sendSettingsSubmit" popup_id="confirmPopup_${event.eventId}" class="primary action">
                <span class="b-label">${saveText}</span>
            </button>
        </div>
    </div>
</div>

<%-- Confirm Dialog for send settings --%>
<i:simplePopup titleKey=".sendConfirm.title" id="confirmPopup_${event.eventId}">
    <cti:url var="postUrl" value="${actionPath}"/>
    <form action="${postUrl}" method="post">
        <cti:csrfToken/>
        <input type="hidden" name="accountId"  value="${accountId}" />
        <input type="hidden" name="thermostatIds"  value="${thermostatIds}" />
        <input type="hidden" name="fan" value=""/>
        <input type="hidden" name="coolTemperature"/>
        <input type="hidden" name="heatTemperature"/>
        <input type="hidden" name="temperatureUnit" value=""/>
        <input type="hidden" name="mode" value="">
        <input type="hidden" name="hold"/>
        <input type="hidden" name="autoModeEnabled" value="${autoEnabledMode}" />
    
        <div id="confirmMessage"><i:inline key=".sendConfirm.message"/></div>
        <br/>
        
        <tags:nameValueContainer2>

            <tags:nameValue2 nameKey=".coolTemperature" rowId="coolTemperatureConfirm">
                <span id="coolTemperatureValueConfirm" class="coolTemperatureConfirm"></span><span class="C unit"><i:inline key="yukon.common.celsius" /></span><span class="F unit"><i:inline key="yukon.common.fahrenheit" /></span>
            </tags:nameValue2>

            <tags:nameValue2 nameKey=".heatTemperature" rowId="heatTemperatureConfirm">
                <span id="heatTemperatureValueConfirm" class="heatTemperatureConfirm"></span><span class="C unit"><i:inline key="yukon.common.celsius" /></span><span class="F unit"><i:inline key="yukon.common.fahrenheit" /></span>
            </tags:nameValue2>
            
            <tags:nameValue2 nameKey=".mode" rowId="modeConfirm">
                <span class="modeConfirm"><i:inline key="yukon.web.modules.operator.thermostatManual.mode.${event.modeString}"/></span>
            </tags:nameValue2>
            
            <tags:nameValue2 nameKey=".fan" rowId="fanConfirm" rowClass="fan" >
                <span class="fanConfirm"><i:inline key=".fan.${event.fanStateString}"/></span>
            </tags:nameValue2>
            
            <tags:nameValue2 nameKey=".hold" rowId="holdConfirm" rowClass="hold">
                <span class="true"><i:inline key="yukon.web.modules.operator.thermostatManual.hold.on"/></span>
                <span class="false"><i:inline key="yukon.web.modules.operator.thermostatManual.hold.off"/></span>
            </tags:nameValue2>
        </tags:nameValueContainer2>
        
        <div class="action-area">
            <cti:button nameKey="ok" type="submit" classes="js-blocker"/> 
            <cti:button nameKey="cancel" id="confirmCancel" onclick="$('#confirmPopup_${event.eventId}').dialog('close')"/>
        </div>
    </form>
</i:simplePopup>
