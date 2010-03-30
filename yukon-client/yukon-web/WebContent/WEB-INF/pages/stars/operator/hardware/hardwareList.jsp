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
<cti:url var="savedSchedulesUrl" value="/spring/stars/operator/thermostatSchedule/savedSchedules?energyCompanyId=${energyCompanyId}&amp;accountId=${accountId}&amp;thermostatId="/>
<cti:url var="editManualUrl" value="/spring/stars/operator/thermostatManual/view?energyCompanyId=${energyCompanyId}&amp;accountId=${accountId}&amp;thermostatIds="/>
<cti:url var="commanderUrl" value="/spring/stars/operator/hardware/commander?energyCompanyId=${energyCompanyId}&amp;accountId=${accountId}&amp;inventoryId="/>
<cti:url var="changeOutUrl" value="/spring/stars/operator/hardware/changeOut?energyCompanyId=${energyCompanyId}&amp;accountId=${accountId}&amp;inventoryId="/>

<%-- SWITCHES TABLE --%>
<tags:boxContainer2 key="switches">
    <c:choose>
        <c:when test="${empty switches}">
            <i:inline key=".switches.none"/>
        </c:when>
        <c:otherwise>
            <table class="compactResultsTable hardwareListTable">
                <tr>
                    <th class="name"><i:inline key=".switches.tableHeader.serialNumber"/></th>
                    <th class="type"><i:inline key=".switches.tableHeader.displayType"/></th>
                    <th class="label"><i:inline key=".switches.tableHeader.label"/></th>
                    <th class="actions"><i:inline key=".switches.tableHeader.actions"/></th>
                </tr>
                
                <c:forEach var="switch" items="${switches}">
                    <tr class="<tags:alternateRow odd="" even="altRow"/>">
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
                    <input type="button" value="<i:inline key=".switches.add"/>" class="createAddButton formSubmit">
                </form>
            </cti:checkRolesAndProperties>
            
        </c:otherwise>
    </c:choose>
</tags:boxContainer2>

<br><br>

<%-- THERMOSTATS TABLE --%>
<tags:boxContainer2 key="thermostats">
    <c:choose>
        <c:when test="${empty thermostats}">
            <i:inline key=".thermostats.none"/>
        </c:when>
        <c:otherwise>
            <tags:alternateRowReset/>
            <table class="compactResultsTable hardwareListTable">
                <tr>
                    <th class="name"><i:inline key=".thermostats.tableHeader.serialNumber"/></th>
                    <th class="type"><i:inline key=".thermostats.tableHeader.displayType"/></th>
                    <th class="label"><i:inline key=".thermostats.tableHeader.label"/></th>
                    <th class="actions"><i:inline key=".thermostats.tableHeader.actions"/></th>
                </tr>
                
                <c:forEach var="thermostat" items="${thermostats}">
                    <tr class="<tags:alternateRow odd="" even="altRow"/>">
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
                                <cti:img key="savedSchedules" href="${savedSchedulesUrl}${thermostat.inventoryId}"/>  &nbsp;
                                <cti:img key="manual" href="${editManualUrl}${thermostat.inventoryId}"/>
                            </cti:checkRolesAndProperties>
                                
                        </td>
                    </tr>
                </c:forEach>
            </table>
            
            <br>
            
            <table style="width:100%;">
                <tr>
                    <td>
                        <a href="/spring/stars/operator/thermostatSelect/select?accountId=${accountId}&amp;energyCompanyId=${energyCompanyId}"><i:inline key=".thermostats.selectMultiple"/></a>
                    </td>
                    <td>
                        <cti:checkRolesAndProperties value="OPERATOR_CONSUMER_INFO_HARDWARES_CREATE">
                            <form action="/spring/stars/operator/hardware/addThermostat?energyCompany=${energyCompany}&amp;accountId=${accountId}">
                                <input type="button" value="<cti:msg2 key=".thermostats.add"/>" class="createAddButton formSubmit">
                            </form>
                        </cti:checkRolesAndProperties>
                    </td>
                </tr>
            </table>
            
        </c:otherwise>
    </c:choose>
</tags:boxContainer2>

<br><br>

<%-- METERS TABLE --%>
<tags:boxContainer2 key="meters">
    <c:choose>
        <c:when test="${empty meters}">
            <i:inline key=".meters.none"/>
        </c:when>
        <c:otherwise>
            <tags:alternateRowReset/>
            <table class="compactResultsTable hardwareListTable">
                <tr>
                    <th class="name"><i:inline key=".meters.tableHeader.displayName"/></th>
                    <th class="type"><i:inline key=".meters.tableHeader.displayType"/></th>
                    <th class="label"><i:inline key=".meters.tableHeader.label"/></th>
                    <th class="actions"><i:inline key=".meters.tableHeader.actions"/></th>
                </tr>
                
                <c:forEach var="meter" items="${meters}">
                    <tr class="<tags:alternateRow odd="" even="altRow"/>">
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