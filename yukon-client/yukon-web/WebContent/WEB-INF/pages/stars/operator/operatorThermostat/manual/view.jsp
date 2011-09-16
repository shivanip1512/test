<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags/dr" prefix="dr"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="operator" page="thermostatManual">

    <cti:includeScript link="/JavaScript/temperature.js"/>
    <cti:includeScript link="/JavaScript/thermostatScheduleEditor.js"/>
	<cti:includeCss link="/WebConfig/yukon/styles/StarsConsumerStyles.css"/>
	<cti:includeCss link="/WebConfig/yukon/styles/operator/thermostat.css"/>
    <cti:includeCss link="/WebConfig/yukon/styles/thermostat.css"/>
    
    <cti:msg var="degreesCelsius" key="yukon.web.modules.operator.thermostatManual.degreesCelsius" />
    <cti:msg var="degreesFahrenheit" key="yukon.web.modules.operator.thermostatManual.degreesFahrenheit" />
    <cti:msg var="holdConfirmOn" key="yukon.web.modules.operator.thermostatManual.hold.on" javaScriptEscape="true"/>
    <cti:msg var="holdConfirmOff" key="yukon.web.modules.operator.thermostatManual.hold.off" javaScriptEscape="true"/>

<script type="text/javascript">
Event.observe(window, 'load', function(){
    Yukon.ThermostatManualEditor.init({
        thermostat: {
            heat: {
                upper: new Temperature({degrees: parseFloat(${thermostat.schedulableThermostatType.upperLimitHeat.value}), unit:'F'}),
                lower: new Temperature({degrees: parseFloat(${thermostat.schedulableThermostatType.lowerLimitHeat.value}), unit:'F'})
            },
            cool: {
                upper: new Temperature({degrees: parseFloat(${thermostat.schedulableThermostatType.upperLimitCool.value}), unit:'F'}),
                lower: new Temperature({degrees: parseFloat(${thermostat.schedulableThermostatType.lowerLimitCool.value}), unit:'F'})
            },
            temperature: new Temperature({degrees: parseFloat(${event.previousTemperature.value}), unit: 'F'}),
            mode: '${event.mode}',
            fan: '${event.fanState}'
        },
        initialUnit: '${temperatureUnit}'
    });
});
</script>

    <c:set var="multipleThermostatsSelected" value="${fn:length(thermostatNames) > 1}"></c:set>

    <table class="thermostatPageContent">
    	<tr>
    	
    		<%-- THE SCHEDULE UI --%>
    		<td>
   
   				<tags:formElementContainer nameKey="manualUiContainerHeader" styleClass="oh">
   				
   					<%-- INSTRUCTIONS --%>
				    <div class="plainText">
				    	<cti:url var="scheduleUrl" value="/spring/stars/operator/thermostatSchedule/savedSchedules">
				    		<cti:param name="accountId" value="${accountId}"/>
				    		<cti:param name="thermostatIds" value="${thermostatIds}"/>
				    	</cti:url>
				        <cti:msg key="yukon.web.modules.operator.thermostatManual.instructionText" arguments="${scheduleUrl}" htmlEscape="false"/>
				    </div>
				    <br>
    
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
                                                             actionPath="/spring/stars/operator/thermostatManual/save" 
                                                             temperatureUnit="${temperatureUnit}"
                                                             event="${event}"
                                                             thermostatIds="${thermostatIds}"
                                                             accountId="${accountId}" />
				            </div>
				            <div style="padding-left:20px;font-size:11px;" class="oh">
				            
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
				            </div>
				    
				</tags:formElementContainer>
			</td>
			
			<td class="selectedThermostatsTd">
    			<%-- THERMOSTAT NAMES --%>
    			<jsp:include page="/WEB-INF/pages/stars/operator/operatorThermostat/selectedThermostatsFragment.jsp" />
    		</td>
		</tr>
        <tr>
            <td colspan="2">
                <cti:msg2 var="historyTableTitle" key=".historyTableTitle"/>
                <tags:pagedBox title="${historyTableTitle}" searchResult="${searchResult}"
                    filterDialog="" baseUrl="/spring/stars/operator/thermostatManual/view"
                    isFiltered="false" showAllUrl="/spring/stars/operator/thermostatManual/view">
                    <c:choose>
                        <c:when test="${searchResult.hitCount == 0}">
                            <cti:msg key="yukon.web.modules.operator.thermostatManual.noItems"/>
                        </c:when>
                        <c:otherwise>
                            <table class="compactResultsTable smallPadding">
                                <thead>
                                    <tr>
                                        <c:if test="${multipleThermostatsSelected}">
                                            <th><cti:msg key="yukon.web.modules.operator.thermostatManual.thermostatHeader"/></th>
                                        </c:if>
                                        <th><cti:msg key="yukon.web.modules.operator.thermostatManual.typeHeader"/></th>
                                        <th><cti:msg key="yukon.web.modules.operator.thermostatManual.userHeader"/></th>
                                        <th><cti:msg key="yukon.web.modules.operator.thermostatManual.dateHeader"/></th>
                                        <th><cti:msg key="yukon.web.modules.operator.thermostatManual.detailsHeader"/></th>
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
                                            <!-- User -->
                                            <td> 
                                                ${fn:escapeXml(historyItem.userName)}
                                            </td>
                                            <!-- Date -->
                                            <td>
                                                <cti:formatDate value="${historyItem.eventTime}" type="DATEHM" />
                                            </td>
                                            <!-- Details -->
                                            <td>
                                                <c:if test="${historyItem.eventType == 'MANUAL'}">
                                                    <!-- Temperature and Degree Units -->
                                                    <cti:msg key="yukon.web.modules.operator.thermostatManual.manualDetailsTemp" arguments="<span class='raw_temperature_F' raw_temperature_F='${historyItem.manualTemp.value}'></span>"/>
                                                    <span class="C_label unit_label"><cti:msg htmlEscape="false" key="yukon.web.modules.operator.thermostatManual.degreesCelsius" /></span>
                                                    <span class="F_label unit_label"><cti:msg htmlEscape="false" key="yukon.web.modules.operator.thermostatManual.degreesFahrenheit" /></span>,
                                                    
                                                    <!-- Heat/Cool Mode -->
                                                    <cti:msg key="yukon.web.modules.operator.thermostatManual.unitMode" /> 
                                                    <cti:msg key="${historyItem.manualMode}" />, 
                                                    <!-- Fan Setting -->
                                                    <cti:msg key="yukon.web.modules.operator.thermostatManual.manualDetailsFan" /> 
                                                    <cti:msg key="${historyItem.manualFan}" />
                                                    <!-- Hold Setting -->
                                                    <c:if test="${historyItem.manualHold == true}">
                                                        (<cti:msg key="yukon.web.modules.operator.thermostatManual.hold"/>)
                                                    </c:if>
                                                </c:if>
                                                <c:if test="${historyItem.eventType == 'SCHEDULE'}">
                                                    <!-- Schedule Name and Link -->
                                                    <c:choose>
                                                        <c:when test="${historyItem.scheduleName == null}">
                                                            <cti:msg key="yukon.web.modules.operator.thermostatManual.deletedSchedule"/>, 
                                                        </c:when>
                                                        <c:otherwise>
                                                            <a href="/spring/stars/operator/thermostatSchedule/savedSchedules?thermostatIds=${thermostatIds}&scheduleId=${historyItem.scheduleId}&accountNumber=${accountNumber}&accountId=${accountId}"><cti:msg key="yukon.web.modules.operator.thermostatManual.scheduleDetails" arguments="${historyItem.scheduleName}"/></a>,
                                                        </c:otherwise>
                                                    </c:choose> 
                                                    <!-- Schedule Day Mode -->
                                                    <cti:msg key="yukon.web.modules.operator.thermostatManual.scheduleDetailsMode"/>
                                                    <cti:msg key="yukon.web.modules.operator.thermostatManual.${historyItem.scheduleMode}"/>
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
                </tags:pagedBox>
            </td>
        </tr>
	</table>
    
     
</cti:standardPage>
