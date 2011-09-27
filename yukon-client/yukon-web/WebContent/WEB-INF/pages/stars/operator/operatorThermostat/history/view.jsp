<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="operator" page="thermostatHistory">

    <cti:includeScript link="/JavaScript/temperature.js"/>
    <cti:includeScript link="/JavaScript/thermostatScheduleEditor.js"/>
    <cti:includeCss link="/WebConfig/yukon/styles/StarsConsumerStyles.css"/>
    <cti:includeCss link="/WebConfig/yukon/styles/operator/thermostat.css"/>
    <cti:includeCss link="/WebConfig/yukon/styles/thermostat.css"/>
    
    <cti:msg2 var="degreesCelsius" key="yukon.web.modules.operator.thermostatManual.degreesCelsius" htmlEscape="false" />
    <cti:msg2 var="degreesFahrenheit" key="yukon.web.modules.operator.thermostatManual.degreesFahrenheit" htmlEscape="false" />
    <cti:msg2 var="holdConfirmOn" key="yukon.web.modules.operator.thermostatManual.hold.on" javaScriptEscape="true"/>
    <cti:msg2 var="holdConfirmOff" key="yukon.web.modules.operator.thermostatManual.hold.off" javaScriptEscape="true"/>

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
            <td>
                <%-- THERMOSTAT NAMES --%>
                <jsp:include page="/WEB-INF/pages/stars/operator/operatorThermostat/selectedThermostatsFragment.jsp" />
            </td>
        </tr>
        <tr>
            <td>
                <cti:msg2 var="historyTableTitle" key=".historyTableTitle"/>
                <tags:pagedBox title="${historyTableTitle}" searchResult="${searchResult}"
                    filterDialog="" baseUrl="/spring/stars/operator/thermostat/history/view"
                    isFiltered="false" showAllUrl="/spring/stars/operator/thermostat/history/view">
                    <c:choose>
                        <c:when test="${searchResult.hitCount == 0}">
                            <i:inline key="yukon.web.modules.operator.thermostatManual.noItems"/>
                        </c:when>
                        <c:otherwise>
                            <table class="compactResultsTable smallPadding">
                                <thead>
                                    <tr>
                                        <c:if test="${multipleThermostatsSelected}">
                                            <th><i:inline key="yukon.web.modules.operator.thermostatManual.thermostatHeader"/></th>
                                        </c:if>
                                        <th><i:inline key="yukon.web.modules.operator.thermostatManual.typeHeader"/></th>
                                        <th><i:inline key="yukon.web.modules.operator.thermostatManual.userHeader"/></th>
                                        <th><i:inline key="yukon.web.modules.operator.thermostatManual.dateHeader"/></th>
                                        <th><i:inline key="yukon.web.modules.operator.thermostatManual.detailsHeader"/></th>
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
                                                <i:inline key="${historyItem.eventType}"/>
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
                                                    <cti:msg2 key="yukon.web.modules.operator.thermostatManual.manualDetailsTemp" arguments="<span class='raw_temperature_F' raw_temperature_F='${historyItem.manualTemp.value}'></span>"  htmlEscape="false"/>
                                                    <span class="C_label unit_label">${degreesCelsius}</span>
                                                    <span class="F_label unit_label">${degreesFahrenheit}</span>,
                                                    
                                                    <!-- Heat/Cool Mode -->
                                                    <i:inline key="yukon.web.modules.operator.thermostatManual.unitMode" /> 
                                                    <i:inline key="${historyItem.manualMode}" />, 
                                                    <!-- Fan Setting -->
                                                    <i:inline key="yukon.web.modules.operator.thermostatManual.manualDetailsFan" /> 
                                                    <i:inline key="${historyItem.manualFan}" />
                                                    <!-- Hold Setting -->
                                                    <c:if test="${historyItem.manualHold == true}">
                                                        (<i:inline key="yukon.web.modules.operator.thermostatManual.hold"/>)
                                                    </c:if>
                                                </c:if>
                                                <c:if test="${historyItem.eventType == 'SCHEDULE'}">
                                                    <!-- Schedule Name and Link -->
                                                    <c:choose>
                                                        <c:when test="${historyItem.scheduleName == null}">
                                                            <i:inline key="yukon.web.modules.operator.thermostatManual.deletedSchedule"/>, 
                                                        </c:when>
                                                        <c:otherwise>
                                                            <cti:url var="savedSchedulesUrl" value="/spring/stars/operator/thermostatSchedule/savedSchedules">
                                                                <cti:param name="accountId" value="${accountId}"/>
                                                                <cti:param name="thermostatIds" value="${thermostatIds}"/>
                                                                <cti:param name="scheduleId" value="${historyItem.scheduleId}"/>
                                                                <cti:param name="accountNumber" value="${accountNumber}"/>
                                                            </cti:url>
                                                            <a href="${savedSchedulesUrl}"><i:inline key="yukon.web.modules.operator.thermostatManual.scheduleDetails" arguments="${historyItem.scheduleName}"/></a>,
                                                        </c:otherwise>
                                                    </c:choose> 
                                                    <!-- Schedule Day Mode -->
                                                    <i:inline key="yukon.web.modules.operator.thermostatManual.scheduleDetailsMode"/>
                                                    <i:inline key="yukon.web.modules.operator.thermostatManual.${historyItem.scheduleMode}"/>
                                                </c:if>
                                                <c:if test="${historyItem.eventType == 'RESTORE'}">
                                                    <i:inline key="yukon.web.defaults.dashes"/>
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
