<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>


<cti:standardPage module="consumer" page="history">
<cti:standardMenu/>

<cti:includeScript link="/JavaScript/thermostatScheduleEditor.js"/>
<cti:includeScript link="/JavaScript/temperature.js"/>
<cti:includeCss link="/WebConfig/yukon/styles/consumer/StarsConsumerStyles.css"/>
<cti:includeCss link="/WebConfig/yukon/styles/shared/thermostat.css"/>
<cti:msg var="timeFormatter" key="yukon.common.timeFormatter" />
<cti:includeScript link="${timeFormatter}"/>

<script type="text/javascript">
jQuery(function() {
    //for rendering the display safe temperatures in the correct unit for the customer    
    Yukon.ThermostatManualEditor.renderOtherTemperatures('${temperatureUnit}');
});
</script>

<div id="ajaxDialog"></div>

<h3>
    <cti:msg key="yukon.web.modules.consumer.history.pageTitle" /><br>
    <c:forEach var="stat" items="${thermostats}">
        &nbsp;${stat.label}&nbsp;
    </c:forEach>
</h3>

<c:set var="multipleThermostatsSelected" value="false"></c:set>
<cti:msg2 var="historyTableTitle" key=".historyTableTitle"/>
    <tags:pagedBox2 nameKey="${historyTableTitle}" searchResult="${searchResult}"
        baseUrl="/stars/consumer/thermostat/schedule/history">
        <c:choose>
            <c:when test="${searchResult.hitCount == 0}">
                <cti:msg key="yukon.web.modules.operator.thermostatManual.noItems"/>
            </c:when>
            <c:otherwise>
                <table class="compact-results-table smallPadding">
                    <thead>
                        <tr>
                            <c:if test="${multipleThermostatsSelected}">
                                <th><cti:msg key="yukon.web.modules.operator.thermostatManual.thermostatHeader"/></th>
                            </c:if>
                            <th><cti:msg key="yukon.web.modules.operator.thermostatManual.typeHeader"/></th>
                            <th><cti:msg key="yukon.web.modules.operator.thermostatManual.dateHeader"/></th>
                            <th><cti:msg key="yukon.web.modules.operator.thermostatManual.detailsHeader"/></th>
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
                                        <c:if test="${historyItem.manualCoolTemp.value != 0}">
                                            <cti:msg key="yukon.web.modules.operator.thermostatManual.manualDetailsCoolTemp" arguments="<span class='raw_temperature_F' raw_temperature_F='${historyItem.manualCoolTemp.value}'></span>"/>
                                            <span class="C_label unit_label"><cti:msg htmlEscape="false" key="yukon.web.defaults.celsius" /></span>
                                            <span class="F_label unit_label"><cti:msg htmlEscape="false" key="yukon.web.defaults.fahrenheit" /></span>
                                        </c:if>

										<c:if test="${historyItem.manualHeatTemp.value != 0}">
                                            <cti:msg key="yukon.web.modules.operator.thermostatManual.manualDetailsHeatTemp" arguments="<span class='raw_temperature_F' raw_temperature_F='${historyItem.manualHeatTemp.value}'></span>"/>
                                            <span class="C_label unit_label"><cti:msg htmlEscape="false" key="yukon.web.defaults.celsius" /></span>
                                            <span class="F_label unit_label"><cti:msg htmlEscape="false" key="yukon.web.defaults.fahrenheit" /></span>
                                        </c:if>
                                        
                                        <!-- Heat/Cool Mode -->
                                        [<cti:msg key="yukon.web.modules.operator.thermostatManual.unitMode" /> 
                                        <cti:msg key="${historyItem.manualMode}" />, 
                                        <!-- Fan Setting -->
                                        <cti:msg key="yukon.web.modules.operator.thermostatManual.manualDetailsFan" /> 
                                        <cti:msg key="${historyItem.manualFan}" />]
                                        <!-- Hold Setting -->
                                        <c:if test="${historyItem.manualHold == true}">
                                            (<cti:msg key="yukon.web.modules.operator.thermostatManual.hold"/>)
                                        </c:if>
                                    </c:if>
                                    <c:if test="${historyItem.eventType == 'SCHEDULE'}">
                                        <!-- Schedule Name and Link -->
                                        <c:choose>
                                            <c:when test="${historyItem.scheduleName == null}">
                                                <cti:msg key="yukon.web.modules.operator.thermostatHistory.noInfo"/>
                                            </c:when>
                                            <c:otherwise>
                                                <cti:url var="viewArchivedScheduleUrl" value="/stars/consumer/thermostat/schedule/viewArchivedSchedule">
                                                    <cti:param name="thermostatIds" value="${thermostatIds}"/>
                                                    <cti:param name="scheduleId" value="${historyItem.scheduleId}"/>
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
            </c:otherwise>
        </c:choose>
    </tags:pagedBox2>
</cti:standardPage>
