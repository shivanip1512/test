<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:includeScript link="/JavaScript/thermostatScheduleEditor.js"/>
<cti:includeScript link="/JavaScript/temperature.js"/>
<cti:includeCss link="/WebConfig/yukon/styles/consumer/StarsConsumerStyles.css"/>
<cti:includeCss link="/WebConfig/yukon/styles/shared/thermostat.css"/>
<cti:msg var="timeFormatter" key="yukon.common.timeFormatter" />
<cti:includeScript link="${timeFormatter}"/>
    
<cti:msg2 var="degreesCelsius" key="yukon.web.modules.operator.thermostatManual.degreesCelsius"/>
<cti:msg2 var="degreesFahrenheit" key="yukon.web.modules.operator.thermostatManual.degreesFahrenheit"/>

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
            fan: '${event.fanState}'
        },
        unit: '${temperatureUnit}'
    });
});
</script>

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
                    <tr>
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
                                <c:if test="${historyItem.manualCoolTemp.value != 0}">
                                    <cti:msg2 key="yukon.web.modules.operator.thermostatManual.manualDetailsCoolTemp" arguments="<span class='raw_temperature_F' raw_temperature_F='${historyItem.manualCoolTemp.value}'></span>" htmlEscapeArguments="false"/>
                                    <span class="C_label unit_label">${degreesCelsius}</span>
                                    <span class="F_label unit_label">${degreesFahrenheit}</span>
                                </c:if>
                                <c:if test="${historyItem.manualHeatTemp.value != 0}">
                                    <cti:msg2 key="yukon.web.modules.operator.thermostatManual.manualDetailsHeatTemp" arguments="<span class='raw_temperature_F' raw_temperature_F='${historyItem.manualHeatTemp.value}'></span>" htmlEscapeArguments="false"/>
                                    <span class="C_label unit_label">${degreesCelsius}</span>
                                    <span class="F_label unit_label">${degreesFahrenheit}</span>
                                </c:if>
                                
                                <!-- Heat/Cool Mode -->
                                [<i:inline key="yukon.web.modules.operator.thermostatManual.unitMode" /> 
                                <i:inline key="${historyItem.manualMode}" />, 
                                <!-- Fan Setting -->
                                <i:inline key="yukon.web.modules.operator.thermostatManual.manualDetailsFan" /> 
                                <i:inline key="${historyItem.manualFan}" />]
                                <!-- Hold Setting -->
                                <c:if test="${historyItem.manualHold == true}">
                                    (<i:inline key="yukon.web.modules.operator.thermostatManual.hold"/>)
                                </c:if>
                            </c:if>
                            <c:if test="${historyItem.eventType == 'SCHEDULE'}">
                                <!-- Schedule Name and Link -->
                                <c:choose>
                                    <c:when test="${historyItem.scheduleName == null}">
                                        <i:inline key="yukon.web.modules.operator.thermostatHistory.noInfo"/> 
                                    </c:when>
                                    <c:otherwise>
                                        <cti:url var="viewArchivedScheduleUrl" value="/stars/operator/thermostatSchedule/viewArchivedSchedule">
                                            <cti:param name="accountId" value="${accountId}"/>
                                            <cti:param name="thermostatIds" value="${thermostatIds}"/>
                                            <cti:param name="scheduleId" value="${historyItem.scheduleId}"/>
                                            <cti:param name="accountNumber" value="${accountNumber}"/>
                                        </cti:url>
                                        <a href="${viewArchivedScheduleUrl}" class="f-ajaxPage" data-selector="#ajaxDialog"> ${fn:escapeXml(historyItem.scheduleName)} [<i:inline key="yukon.web.modules.operator.thermostatManual.scheduleDetailsMode"/> <i:inline key="yukon.web.modules.operator.thermostatManual.${historyItem.scheduleMode}"/>]</a>
                                    </c:otherwise>
                                </c:choose> 
                            </c:if>
                            <c:if test="${historyItem.eventType == 'RESTORE'}">
                                <i:inline key="yukon.web.defaults.dashes"/>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        <c:if test="${showViewMore && moreResults}">
            <cti:url var="historyUrl" value="/stars/operator/thermostat/history/view">
                <cti:param name="accountId" value="${accountId}" />
                <cti:param name="thermostatIds" value="${inventoryId}"/>
            </cti:url>
            <a href="${historyUrl}"><i:inline key=".commandHistory.viewMore"/></a><br>
        </c:if>
    </c:otherwise>
</c:choose>
