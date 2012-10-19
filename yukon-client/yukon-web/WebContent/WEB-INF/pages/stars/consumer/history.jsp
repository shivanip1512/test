<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib tagdir="/WEB-INF/tags/dr" prefix="dr"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>

<cti:standardPage module="consumer" page="history">
<cti:standardMenu/>

<cti:includeScript link="/JavaScript/temperature.js"/>
<cti:includeScript link="/JavaScript/thermostatScheduleEditor.js"/>

<script type="text/javascript">
jQuery(function() {
    //for rendering the display safe temperatures in the correct unit for the customer    
    Yukon.ThermostatManualEditor.renderOtherTemperatures('${temperatureUnit}');
});
</script>

<h3>
    <cti:msg key="yukon.web.modules.consumer.history.pageTitle" /><br>
    <c:forEach var="stat" items="${thermostats}">
        &nbsp;${stat.label}&nbsp;
    </c:forEach>
</h3>

<c:set var="multipleThermostatsSelected" value="false"></c:set>
<cti:msg2 var="historyTableTitle" key=".historyTableTitle"/>
            <tags:pagedBox2 nameKey="${historyTableTitle}" searchResult="${searchResult}"
                filterDialog="" baseUrl="/spring/stars/consumer/thermostat/schedule/history"
                isFiltered="false" showAllUrl="/spring/stars/consumer/thermostat/schedule/history">
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
	                                                <span class="F_label unit_label"><cti:msg htmlEscape="false" key="yukon.web.defaults.fahrenheit" /></span>,
                                                </c:if>

												<c:if test="${historyItem.manualHeatTemp.value != 0}">
	                                                <cti:msg key="yukon.web.modules.operator.thermostatManual.manualDetailsHeatTemp" arguments="<span class='raw_temperature_F' raw_temperature_F='${historyItem.manualHeatTemp.value}'></span>"/>
	                                                <span class="C_label unit_label"><cti:msg htmlEscape="false" key="yukon.web.defaults.celsius" /></span>
	                                                <span class="F_label unit_label"><cti:msg htmlEscape="false" key="yukon.web.defaults.fahrenheit" /></span>,
                                                </c:if>
                                                
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
                                                        <a href="/spring/stars/consumer/thermostat/schedule/view/saved?thermostatIds=${thermostatIds}&scheduleId=${historyItem.scheduleId}"><cti:msg key="yukon.web.modules.operator.thermostatManual.scheduleDetails" arguments="${historyItem.scheduleName}"/></a>,
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
            </tags:pagedBox2>
</cti:standardPage>
