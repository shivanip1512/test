<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:msgScope paths="modules.operator.thermostatManual">

<cti:includeScript link="/resources/js/common/yukon.thermostat.js"/>
<cti:includeScript link="/resources/js/common/yukon.temperature.js"/>

<cti:includeCss link="/WebConfig/yukon/styles/consumer/StarsConsumerStyles.css"/>
<cti:includeCss link="/WebConfig/yukon/styles/shared/thermostat.css"/>

<cti:msg var="timeFormatter" key="yukon.common.timeFormatter" />
<cti:includeScript link="${timeFormatter}"/>
    
<cti:msg2 var="degreesCelsius" key=".degreesCelsius"/>
<cti:msg2 var="degreesFahrenheit" key=".degreesFahrenheit"/>

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
            fan: '${event.fanState}'
        },
        unit: '${temperatureUnit}'
    });
});
</script>

<c:choose>
    <c:when test="${searchResult.hitCount == 0}">
        <span class="empty-list"><i:inline key=".noItems"/></span>
    </c:when>
    <c:otherwise>
        <table class="compact-results-table dashed">
            <thead>
                <tr>
                    <c:if test="${multipleThermostatsSelected}">
                        <th><i:inline key=".thermostatHeader"/></th>
                    </c:if>
                    <th><i:inline key=".typeHeader"/></th>
                    <th><i:inline key=".userHeader"/></th>
                    <th><i:inline key=".dateHeader"/></th>
                    <th><i:inline key=".detailsHeader"/></th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="historyItem" items="${eventHistoryList}" varStatus="status">
                    <tr>
                        <!-- Thermostat -->
                        <c:if test="${multipleThermostatsSelected}">
                            <td>${fn:escapeXml(historyItem.thermostatName)}</td>
                        </c:if>
                        <!-- Type -->
                        <td><i:inline key="${historyItem.eventType}"/></td>
                        <!-- User -->
                        <td>${fn:escapeXml(historyItem.userName)}</td>
                        <!-- Date -->
                        <td><cti:formatDate value="${historyItem.eventTime}" type="DATEHM"/></td>
                        <!-- Details -->
                        <td>
                            <c:if test="${historyItem.eventType == 'MANUAL'}">
                                <!-- Temperature and Degree Units -->
                                <c:if test="${historyItem.manualCoolTemp.value != 0}">
                                    <cti:msg2 key=".manualDetailsCoolTemp" arguments="<span class='raw_temperature_F' raw_temperature_F='${historyItem.manualCoolTemp.value}'></span>" htmlEscapeArguments="false"/>
                                    <span class="C_label unit_label">${degreesCelsius}</span>
                                    <span class="F_label unit_label">${degreesFahrenheit}</span>
                                </c:if>
                                <c:if test="${historyItem.manualHeatTemp.value != 0}">
                                    <cti:msg2 key=".manualDetailsHeatTemp" arguments="<span class='raw_temperature_F' raw_temperature_F='${historyItem.manualHeatTemp.value}'></span>" htmlEscapeArguments="false"/>
                                    <span class="C_label unit_label">${degreesCelsius}</span>
                                    <span class="F_label unit_label">${degreesFahrenheit}</span>
                                </c:if>
                                
                                <!-- Heat/Cool Mode -->
                                [<i:inline key=".unitMode" /> 
                                <i:inline key="${historyItem.manualMode}" />, 
                                <!-- Fan Setting -->
                                <i:inline key=".manualDetailsFan" /> 
                                <i:inline key="${historyItem.manualFan}" />]
                                <!-- Hold Setting -->
                                <c:if test="${historyItem.manualHold == true}">
                                    (<i:inline key=".hold"/>)
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
                                        <a href="javascript:void(0);" data-popup="#command-details-${status.index}">${fn:escapeXml(historyItem.scheduleName)} [<i:inline key=".scheduleDetailsMode"/> <i:inline key=".${historyItem.scheduleMode}"/>]</a>
                                        <cti:msg2 var="title" key="modules.operator.thermostatHistory.details.title"/>
                                        <div id="command-details-${status.index}" data-url="${viewArchivedScheduleUrl}" data-title="${title}"></div>
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
        <c:if test="${empty showViewMore}"><tags:pagingResultsControls result="${searchResult}" adjustPageCount="true"/></c:if>
        <c:if test="${showViewMore && moreResults}">
            <cti:url var="historyUrl" value="/stars/operator/thermostat/history/view">
                <cti:param name="accountId" value="${accountId}" />
                <cti:param name="thermostatIds" value="${inventoryId}"/>
            </cti:url>
            <a href="${historyUrl}"><i:inline key="modules.operator.hardware.commandHistory.viewMore"/></a><br>
        </c:if>
    </c:otherwise>
</c:choose>

</cti:msgScope>