<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="operator" page="hardwareList">
<cti:includeCss link="/WebConfig/yukon/styles/operator/hardware.css"/>

<%-- For lining up the columns of the three tables --%>

<cti:url var="editUrl" value="/spring/stars/operator/hardware/hardwareEdit?energyCompanyId=${energyCompanyId}&amp;accountId=${accountId}&amp;inventoryId="/>
<cti:url var="editConfigUrl" value="/spring/stars/operator/hardware/config/list?energyCompanyId=${energyCompanyId}&amp;accountId=${accountId}&amp;inventoryId="/>
<cti:url var="editScheduleUrl" value="/spring/stars/operator/thermostatSchedule/view?energyCompanyId=${energyCompanyId}&amp;accountId=${accountId}&amp;thermostatIds="/>
<cti:url var="editManualUrl" value="/spring/stars/operator/thermostatManual/view?energyCompanyId=${energyCompanyId}&amp;accountId=${accountId}&amp;thermostatIds="/>
<cti:url var="commanderUrl" value="/spring/stars/operator/hardware/commander?energyCompanyId=${energyCompanyId}&amp;accountId=${accountId}&amp;inventoryId="/>
<cti:url var="changeOutUrl" value="/spring/stars/operator/hardware/changeOut?energyCompanyId=${energyCompanyId}&amp;accountId=${accountId}&amp;inventoryId="/>

<%-- SWITCHES TABLE --%>
<c:set var="switchesCount" value="0"/>
<tags:boxContainer2 key="switches">
    <c:choose>
        <c:when test="${empty switches}">
            <cti:msg2 key=".switches.none"/>
        </c:when>
        <c:otherwise>
            <table class="compactResultsTable hardwareListTable">
                <tr>
                    <th class="name"><cti:msg2 key=".switches.tableHeader.serialNumber"/></th>
                    <th class="type"><cti:msg2 key=".switches.tableHeader.displayType"/></th>
                    <th class="label"><cti:msg2 key=".switches.tableHeader.label"/></th>
                    <th class="actions"><cti:msg2 key=".switches.tableHeader.actions"/></th>
                </tr>
                
                <c:forEach var="switch" items="${switches}">
                    <c:set var="switchesRowClass" value="tableCell"/>
                    <c:choose>
                        <c:when test="${switchesCount % 2 == 0}">
                            <c:set var="switchesRowClass" value="tableCell"/>
                        </c:when>
                        <c:otherwise>
                            <c:set var="switchesRowClass" value="altTableCell"/>
                        </c:otherwise>
                    </c:choose>
                    <c:set var="switchesCount" value="${switchesCount + 1}"/>
                    <tr class="${switchesRowClass}">
                        <td>
                            <a href="${editUrl}${switch.inventoryId}">
                                <spring:escapeBody htmlEscape="true">${switch.serialNumber}</spring:escapeBody>
                            </a>
                        </td>
                        <td><spring:escapeBody htmlEscape="true">${switch.displayType}</spring:escapeBody></td>
                        <td><spring:escapeBody htmlEscape="true">${switch.displayLabel}</spring:escapeBody></td>
                        <td nowrap="nowrap">
                            <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
                                <cti:checkRolesAndProperties value="OPERATOR_INVENTORY_CHECKING">
                                    <cti:img key="changeOut" href="${changeOutUrl}${thermostat.inventoryId}"/> &nbsp;
                                </cti:checkRolesAndProperties>
                            </cti:checkRolesAndProperties>
                            <cti:img key="editConfig" href="${editConfigUrl}${switch.inventoryId}"/>
                        </td>
                    </tr>
                </c:forEach>
            </table>
            
            <br>
            
            <cti:checkRolesAndProperties value="OPERATOR_CONSUMER_INFO_HARDWARES_CREATE">
                <form action="/spring/stars/operator/hardware/addSwitch?energyCompany=${energyCompany}&amp;accountId=${accountId}">
                    <input type="button" value="<cti:msg2 key=".switches.add"/>" class="createAddButton formSubmit">
                </form>
            </cti:checkRolesAndProperties>
            
        </c:otherwise>
    </c:choose>
</tags:boxContainer2>

<br><br>

<%-- THERMOSTATS TABLE --%>
<c:set var="thermostatsCount" value="0"/>
<tags:boxContainer2 key="thermostats">
    <c:choose>
        <c:when test="${empty thermostats}">
            <cti:msg2 key=".thermostats.none"/>
        </c:when>
        <c:otherwise>
            <table class="compactResultsTable hardwareListTable">
                <tr>
                    <th class="name"><cti:msg2 key=".thermostats.tableHeader.serialNumber"/></th>
                    <th class="type"><cti:msg2 key=".thermostats.tableHeader.displayType"/></th>
                    <th class="label"><cti:msg2 key=".thermostats.tableHeader.label"/></th>
                    <th class="actions"><cti:msg2 key=".thermostats.tableHeader.actions"/></th>
                </tr>
                
                <c:forEach var="thermostat" items="${thermostats}">
                    <c:set var="thermostatsRowClass" value="tableCell"/>
                    <c:choose>
                        <c:when test="${thermostatsCount % 2 == 0}">
                            <c:set var="thermostatsRowClass" value="tableCell"/>
                        </c:when>
                        <c:otherwise>
                            <c:set var="thermostatsRowClass" value="altTableCell"/>
                        </c:otherwise>
                    </c:choose>
                    <c:set var="thermostatsCount" value="${thermostatsCount + 1}"/>
                    <tr class="${thermostatsRowClass}">
                        <td>
                            <a href="${editUrl}${thermostat.inventoryId}">
                                <spring:escapeBody htmlEscape="true">${thermostat.serialNumber}</spring:escapeBody>
                            </a>
                        </td>
                        <td><spring:escapeBody htmlEscape="true">${thermostat.displayType}</spring:escapeBody></td>
                        <td><spring:escapeBody htmlEscape="true">${thermostat.displayLabel}</spring:escapeBody></td>
                        <td nowrap="nowrap">
                            <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
                                <cti:checkRolesAndProperties value="OPERATOR_INVENTORY_CHECKING">
                                    <cti:img key="changeOut" href="${changeOutUrl}${thermostat.inventoryId}"/> &nbsp;
                                </cti:checkRolesAndProperties>
                            </cti:checkRolesAndProperties>
                                
                            <cti:img key="editConfig" href="${editConfigUrl}${thermostat.inventoryId}"/>  &nbsp;
                            
                            <cti:checkRolesAndProperties value="OPERATOR_CONSUMER_INFO_HARDWARES_THERMOSTAT">
                                <cti:img key="editSchedule" href="${editScheduleUrl}${thermostat.inventoryId}"/>  &nbsp;
                                <cti:img key="manual" href="${editManualUrl}${thermostat.inventoryId}"/>
                            </cti:checkRolesAndProperties>
                                
                        </td>
                    </tr>
                </c:forEach>
            </table>
            
            <br>
            
            <a href="/spring/stars/operator/thermostatSelect/select?accountId=${accountId}&amp;energyCompanyId=${energyCompanyId}"><cti:msg2 key=".thermostats.selectMultiple"></cti:msg2></a>
            
            <cti:checkRolesAndProperties value="OPERATOR_CONSUMER_INFO_HARDWARES_CREATE">
                <form action="/spring/stars/operator/hardware/addThermostat?energyCompany=${energyCompany}&amp;accountId=${accountId}">
                    <input type="button" value="<cti:msg2 key=".thermostats.add"/>" class="createAddButton formSubmit">
                </form>
            </cti:checkRolesAndProperties>
            
        </c:otherwise>
    </c:choose>
</tags:boxContainer2>

<br><br>

<%-- METERS TABLE --%>
<c:set var="metersCount" value="0"/>
<tags:boxContainer2 key="meters">
    <c:choose>
        <c:when test="${empty meters}">
            <cti:msg2 key=".meters.none"/>
        </c:when>
        <c:otherwise>
            <table class="compactResultsTable hardwareListTable">
                <tr>
                    <th class="name"><cti:msg2 key=".meters.tableHeader.displayName"/></th>
                    <th class="type"><cti:msg2 key=".meters.tableHeader.displayType"/></th>
                    <th class="label"><cti:msg2 key=".meters.tableHeader.label"/></th>
                    <th class="actions"><cti:msg2 key=".meters.tableHeader.actions"/></th>
                </tr>
                
                <c:forEach var="meter" items="${meters}">
                    <c:set var="metersRowClass" value="tableCell"/>
                    <c:choose>
                        <c:when test="${metersCount % 2 == 0}">
                            <c:set var="metersRowClass" value="tableCell"/>
                        </c:when>
                        <c:otherwise>
                            <c:set var="metersRowClass" value="altTableCell"/>
                        </c:otherwise>
                    </c:choose>
                    <c:set var="metersCount" value="${metersCount + 1}"/>
                    <tr class="${metersRowClass}">
                        <td>
                            <a href="${editUrl}${meter.inventoryId}">
                                <spring:escapeBody htmlEscape="true">${meter.displayName}</spring:escapeBody>
                            </a>
                        </td>
                        <td><spring:escapeBody htmlEscape="true">${meter.displayType}</spring:escapeBody></td>
                        <td><spring:escapeBody htmlEscape="true">${meter.displayLabel}</spring:escapeBody></td>
                        <td nowrap="nowrap">
                            <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
                                <cti:checkRolesAndProperties value="OPERATOR_INVENTORY_CHECKING">
                                    <cti:img key="changeOut" href="${changeOutUrl}${meter.inventoryId}"/> &nbsp;
                                </cti:checkRolesAndProperties>
                            </cti:checkRolesAndProperties>
                            <cti:img key="editConfig" href="${editConfigUrl}${meter.inventoryId}"/> &nbsp;
                            <cti:img key="commander" href="${commanderUrl}${meter.inventoryId}"/>
                        </td>
                    </tr>
                </c:forEach>
            </table>
            
            <br>
            
            <cti:checkRolesAndProperties value="OPERATOR_CONSUMER_INFO_HARDWARES_CREATE">
                <form action="/spring/stars/operator/hardware/addMeter?energyCompany=${energyCompany}&amp;accountId=${accountId}">
                    <input type="submit" value="<cti:msg2 key=".meters.add"/>" class="createAddButton formSubmit">
                </form>
            </cti:checkRolesAndProperties>
            
        </c:otherwise>
    </c:choose>
</tags:boxContainer2>
</cti:standardPage>