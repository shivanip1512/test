<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<cti:standardPage module="operator" page="hardware.list">
<cti:includeCss link="/WebConfig/yukon/styles/operator/hardware.css"/>

<cti:url var="hardwareListUrl" value="/spring/stars/operator/hardware/list?accountId=${accountId}"/>
<cti:url var="createUrl" value="/spring/stars/operator/hardware/createPage"/>
<cti:url var="checkSnUrl" value="/spring/stars/operator/hardware/checkSerialNumber"/>
<cti:url var="addDevice" value="/spring/stars/operator/hardware/addDeviceToAccount"/>
<cti:url var="viewUrl" value="/spring/stars/operator/hardware/view?accountId=${accountId}&amp;inventoryId="/>
<cti:url var="editConfigUrl" value="/spring/stars/operator/hardware/config/edit?accountId=${accountId}&amp;inventoryId="/>
<cti:url var="editMeterConfigUrl" value="/spring/stars/operator/hardware/config/meterConfig?accountId=${accountId}&amp;meterId="/>
<cti:url var="savedSchedulesUrl" value="/spring/stars/operator/thermostatSchedule/savedSchedules?accountId=${accountId}&amp;thermostatIds="/>
<cti:url var="selectMultipleUrl" value="/spring/stars/operator/thermostatSelect/select?accountId=${accountId}"/>
<cti:url var="editManualUrl" value="/spring/stars/operator/thermostatManual/view?accountId=${accountId}&amp;thermostatIds="/>
<cti:url var="thermostatHistoryUrl" value="/spring/stars/operator/thermostat/history/view?accountId=${accountId}&amp;thermostatIds="/>

<form id="changeOutForm" action="/spring/stars/operator/hardware/changeOut">
    <input type="hidden" name="accountId" value="${accountId}">
    <input type="hidden" name="newInventoryId" id="newInventoryId">
    <input type="hidden" name="oldInventoryId" id="oldInventoryId">
    <input type="hidden" name="isMeter" id="isMeter">
    <input type="hidden" name="redirect" value="list">
</form>

<form id="addMeterForm" action="/spring/stars/operator/hardware/addMeter">
    <input type="hidden" name="accountId" value="${accountId}">
    <input type="hidden" name="meterId" id="meterId">
</form>

<c:choose>
    <c:when test="${starsMeters}">
        <cti:url var="meterUrl" value="/spring/stars/operator/hardware/viewMeterProfile?accountId=${accountId}&amp;inventoryId="/>
    </c:when>
    <c:otherwise>
        <cti:url var="meterUrl" value="/spring/stars/operator/hardware/view?accountId=${accountId}&amp;inventoryId="/>
    </c:otherwise>
</c:choose>

<script type="text/javascript">
    function showAddSwitchPopup() {
        $('addSwitchPopup').show();
    }

    function checkSerialNumber() {
        $('serialNumberUnavailable').show();
    }

    function showInvCheckingPopup(type) {
        if(type == 'switch') {
            $('addSwitchType').value = $F('switchTypeToAdd');
            showSimplePopup($('inventoryCheckingSwitchPopup'));
        } else if (type == 'gateway') {
            $('addGatewayType').value = $F('gatewayTypeToAdd');
            showSimplePopup($('inventoryCheckingGatewayPopup'));
        } else {
            $('addTstatType').value = $F('tstatTypeToAdd');
            showSimplePopup($('inventoryCheckingThermostatPopup'));
        }
    }

    function addMeter() {
        var form = $('addMeterForm');
        form.submit();
        return true;
    }

    function changeOut(oldId, isMeter) {
        $('oldInventoryId').value = oldId;

        if(isMeter) {
            $('isMeter').value = 'true';
        } else {
            $('isMeter').value = 'false';
        }
        
        var form = $('changeOutForm');
        form.submit();
        return true;
    }
    
    Event.observe(window, 'load', function() {
        if ($('inventoryCheckingThermostatPopup').visible())
            adjustDialogSizeAndPosition('inventoryCheckingThermostatPopup');
        if ($('inventoryCheckingGatewayPopup').visible())
            adjustDialogSizeAndPosition('inventoryCheckingGatewayPopup');
        if ($('inventoryCheckingSwitchPopup').visible())
            adjustDialogSizeAndPosition('inventoryCheckingSwitchPopup');
        if ($('addFromWarehousePopup'))
            adjustDialogSizeAndPosition('addFromWarehousePopup');
        if ($('addFromAccountPopup'))
            adjustDialogSizeAndPosition('addFromAccountPopup');
        if ($('createSerialPopup'))
            adjustDialogSizeAndPosition('createSerialPopup');
        if ($('notFoundSerial'))
            adjustDialogSizeAndPosition('notFoundSerial');
        if ($('sameAccountPopup'))
            adjustDialogSizeAndPosition('sameAccountPopup');
        if ($('anotherECPopup'))
            adjustDialogSizeAndPosition('anotherECPopup');
    });
</script>

<c:choose>
    <c:when test="${checkingAdd.hardwareType.hardwareClass == 'SWITCH'}">
        <c:set var="titleKey" value=".switches.add"/>
    </c:when>
    <c:when test="${checkingAdd.hardwareType.hardwareClass == 'GATEWAY'}">
        <c:set var="titleKey" value=".gateways.add"/>
    </c:when>
    <c:otherwise>
        <c:set var="titleKey" value=".thermostats.add"/>
    </c:otherwise>
</c:choose>

<%-- INVENTORY CHECKING SWITCH POPUP --%>
<i:simplePopup titleKey=".switches.add" 
        id="inventoryCheckingSwitchPopup" 
        styleClass="smallSimplePopup"
        showImmediately="${showSwitchCheckingPopup}"
        onClose="window.location='${hardwareListUrl}';">
    <c:if test="${showSwitchCheckingPopup}">
        <cti:flashScopeMessages/>
    </c:if>
    <form:form commandName="serialNumberSwitch" action="${checkSnUrl}Switch">
    
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".serialNumber">
                <input type="hidden" name="accountId" value="${accountId}">
                <input type="hidden" name="hardwareTypeId" id="addSwitchType">
                <tags:input path="serialNumber" size="25"/>
            </tags:nameValue2>
        </tags:nameValueContainer2>
        
        <div class="actionArea">
            <cti:button nameKey="checkInventoryButton" onclick="showInvCheckingPopup('switch');" type="submit"/>
        </div>
        
        </form:form>
</i:simplePopup>

<%-- INVENTORY CHECKING THERMOSTAT POPUP --%>
<i:simplePopup titleKey=".thermostats.add" 
        id="inventoryCheckingThermostatPopup" 
        styleClass="smallSimplePopup" 
        showImmediately="${showThermostatCheckingPopup}"
        onClose="window.location='${hardwareListUrl}';">
    <c:if test="${showThermostatCheckingPopup}">
        <cti:flashScopeMessages/>
    </c:if>
    <form:form commandName="serialNumberThermostat" action="${checkSnUrl}Thermostat">
    
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".serialNumber">
                <input type="hidden" name="accountId" value="${accountId}">
                <input type="hidden" name="hardwareTypeId" id="addTstatType">
                <tags:input path="serialNumber" size="25"/>
            </tags:nameValue2>
        </tags:nameValueContainer2>
        
        <div class="actionArea">
            <cti:button nameKey="checkInventoryButton" onclick="showInvCheckingPopup('thermostat');" type="submit"/>
        </div>
        
    </form:form>
</i:simplePopup>

<%-- INVENTORY CHECKING GATEWAY POPUP --%>
<i:simplePopup titleKey=".gateways.add" 
        id="inventoryCheckingGatewayPopup" 
        styleClass="smallSimplePopup" 
        showImmediately="${showGatewayCheckingPopup}"
        onClose="window.location='${hardwareListUrl}';">
    <c:if test="${showGatewayCheckingPopup}">
        <cti:flashScopeMessages/>
    </c:if>
    <form:form commandName="serialNumberGateway" action="${checkSnUrl}Gateway">
    
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".serialNumber">
                <input type="hidden" name="accountId" value="${accountId}">
                <input type="hidden" name="hardwareTypeId" id="addGatewayType">
                <tags:input path="serialNumber" size="25"/>
            </tags:nameValue2>
        </tags:nameValueContainer2>
        
        <div class="actionArea">
            <cti:button nameKey="checkInventoryButton" onclick="showInvCheckingPopup('gateway');" type="submit"/>
        </div>
    
    </form:form>
</i:simplePopup>

<%-- CONFIRM ADD FROM WAREHOUSE POPUP --%>
<c:if test="${not empty confirmWarehouseSerial}">
    <i:simplePopup titleKey="${titleKey}" 
            id="addFromWarehousePopup" 
            styleClass="mediumSimplePopup" 
            showImmediately="true"
            onClose="window.location='${hardwareListUrl}';">
        
        <div style="padding:10px;"><i:inline key=".serialNumber.inWarehouse"/></div>
        
        <table class="miniResultsTable invCheckingTable" align="center">
            <tr>
                <th nowrap="nowrap"><i:inline key=".serialNumberShort"/></th>
                <th nowrap="nowrap"><i:inline key=".altTrackingNumber"/></th>
                <th nowrap="nowrap"><i:inline key=".deviceType"/></th>
                <th nowrap="nowrap"><i:inline key=".warehouse"/></th>
            </tr>
            <tr>
                <td><spring:escapeBody htmlEscape="true">${checkingAdd.serialNumber}</spring:escapeBody></td>
                <td><spring:escapeBody htmlEscape="true">${checkingAdd.altTrackingNumber}</spring:escapeBody></td>
                <td><spring:escapeBody htmlEscape="true">${checkingAdd.deviceType}</spring:escapeBody></td>
                <td><spring:escapeBody htmlEscape="true">${checkingAdd.warehouse}</spring:escapeBody></td>
            </tr>
        </table>
        
        <div style="padding:10px;"><i:inline key=".serialNumber.addToAccount"/></div>
        
        <form action="${addDevice}">
            <input type="hidden" name="accountId" value="${accountId}">
            <input type="hidden" name="serialNumber" value="${checkingAdd.serialNumber}">
            <input type="hidden" name="inventoryId" value="${checkingAdd.inventoryId}">
            <input type="hidden" name="fromAccount" value="false">
            
            <div class="actionArea">
                <cti:button nameKey="ok" type="submit"/>
                <cti:button nameKey="cancel" onclick="window.location='${hardwareListUrl}';"/>
            </div>
        </form>
    </i:simplePopup>
</c:if>

<%-- CONFIRM ADD FROM ACCOUNT POPUP --%>
<c:if test="${not empty confirmAccountSerial}">
    <i:simplePopup titleKey="${titleKey}" 
            id="addFromAccountPopup" 
            styleClass="mediumSimplePopup" 
            showImmediately="true"
            onClose="window.location='${hardwareListUrl}';">
        
        <div class="hardwarePopup"><i:inline key=".serialNumber.foundOnAnotherAccount"/></div>
        
        <table class="miniResultsTable invCheckingTable" align="center">
            <tr>
                <th nowrap="nowrap"><i:inline key=".accountNumber"/></th>
                <th nowrap="nowrap"><i:inline key=".name"/></th>
                <th nowrap="nowrap"><i:inline key=".serialNumberShort"/></th>
                <th nowrap="nowrap"><i:inline key=".deviceType"/></th>
            </tr>
            <tr title="<tags:address address="${checkingAdd.address}" inLine="true"/>">
            
                <td><spring:escapeBody htmlEscape="true">${checkingAdd.accountNumber}</spring:escapeBody></td>
                <td><spring:escapeBody htmlEscape="true">${checkingAdd.name}</spring:escapeBody></td>
                <td><spring:escapeBody htmlEscape="true">${checkingAdd.serialNumber}</spring:escapeBody></td>
                <td><spring:escapeBody htmlEscape="true">${checkingAdd.deviceType}</spring:escapeBody></td>
                
            </tr>
        </table>
        
        <div class="hardwarePopup">
            <i:inline key=".serialNumber.moveToAccount" 
                argumentSeparator="," 
                arguments="${checkingAdd.accountNumber},${accountNumber}"/>
        </div>
        
        <form action="${addDevice}">
            <input type="hidden" name="accountId" value="${accountId}">
            <input type="hidden" value="${checkingAdd.serialNumber}">
            <input type="hidden" name="inventoryId" value="${checkingAdd.inventoryId}">
            <input type="hidden" name="fromAccount" value="true">
            
            <div class="actionArea">
                <cti:button nameKey="ok" type="submit"/>
                <cti:button nameKey="cancel" onclick="window.location='${hardwareListUrl}';"/>
            </div>
            
        </form>
        
    </i:simplePopup>
</c:if>

<%-- CONFIRM CREATE POPUP --%>
<c:if test="${not empty confirmCreateSerial}">
    <i:simplePopup titleKey="${titleKey}" 
            id="createSerialPopup" 
            styleClass="smallSimplePopup" 
            showImmediately="true"
            onClose="window.location='${hardwareListUrl}';">

        <div class="hardwarePopup"><i:inline key=".serialNumber.notFoundAdd" arguments="${confirmCreateSerial}"/></div>
        
        <form action="${createUrl}">
            <input type="hidden" name="accountId" value="${accountId}">
            <input type="hidden" name="serialNumber" value="${checkingAdd.serialNumber}">
            <input type="hidden" name="hardwareTypeId" value="${checkingAdd.hardwareTypeId}">
            
            <div class="actionArea">
                <cti:button nameKey="ok" type="submit" styleClass="f_blocker"/>
                <cti:button nameKey="cancel" onclick="window.location='${hardwareListUrl}';"/>
            </div>
            
        </form>
    </i:simplePopup>
</c:if>

<%-- SERIAL NUMBER NOT FOUND POPUP --%>
<c:if test="${not empty notFoundSerial}">
    <i:simplePopup titleKey="${titleKey}" 
            id="notFoundSerialPopup" 
            styleClass="smallSimplePopup" 
            showImmediately="true"
            onClose="window.location='${hardwareListUrl}';">

        <div class="hardwarePopup"><i:inline key=".error.notFound.serialNumber" arguments="${notFoundSerial}"/></div>
        
        <div class="actionArea">
            <cti:button nameKey="ok" type="submit" styleClass="f_blocker"/>
            <cti:button nameKey="cancel" onclick="window.location='${hardwareListUrl}';"/>
        </div>
        
    </i:simplePopup>
</c:if>

<%-- SERIAL NUMBER FOUND ON SAME ACCOUNT POPUP --%>
<c:if test="${not empty sameAccountSerial}">
    <i:simplePopup titleKey="${titleKey}" 
            id="sameAccountPopup" 
            styleClass="smallSimplePopup" 
            showImmediately="true"
            onClose="window.location='${hardwareListUrl}';">

        <div class="hardwarePopup"><i:inline key=".error.sameAccount.serialNumber" arguments="${sameAccountSerial}"/></div>
        
        <div class="actionArea">
            <cti:button nameKey="ok" onclick="window.location='${hardwareListUrl}';"/>
        </div>
        
    </i:simplePopup>
</c:if>

<%-- SERIAL NUMBER FOUND IN ANOTHER EC --%>
<c:if test="${not empty anotherECSerial}">
    <i:simplePopup titleKey="${titleKey}" 
            id="anotherECPopup" 
            styleClass="smallSimplePopup" 
            showImmediately="true"
            onClose="window.location='${hardwareListUrl}';">

        <div class="hardwarePopup"><i:inline key=".error.anotherEC.serialNumber" arguments="${anotherEC}"/></div>
        
        <div class="actionArea">
            <cti:button nameKey="ok" onclick="window.location='${hardwareListUrl}';"/>
        </div>
        
    </i:simplePopup>
</c:if>

<%-- SWITCHES TABLE --%>
<c:set var="switchTypes" value="${deviceTypeMap['SWITCH']}"/>
<c:if test="${fn:length(switchTypes) > 0}">
    <tags:boxContainer2 nameKey="switches" styleClass="hardwareContainer">
        <c:choose>
            <c:when test="${empty switches}">
                <i:inline key=".switches.none"/>
            </c:when>
            <c:otherwise>
                <table class="compactResultsTable hardwareListTable">
                    <tr>
                        <th class="name"><i:inline key=".serialNumber"/></th>
                        <th class="type"><i:inline key=".switches.displayType"/></th>
                        <th class="label"><i:inline key=".label"/></th>
                        <th class="actions"><i:inline key=".actions"/></th>
                    </tr>
                    
                    <c:forEach var="switch" items="${switches}">
                        <tr class="<tags:alternateRow odd="" even="altRow"/>">
                            <td>
                                <a href="${viewUrl}${switch.inventoryId}">
                                    <spring:escapeBody htmlEscape="true">${switch.serialNumber}</spring:escapeBody>
                                </a>
                            </td>
                            <td><spring:escapeBody htmlEscape="true">${switch.displayType}</spring:escapeBody></td>
                            <td><spring:escapeBody htmlEscape="true">${switch.displayLabel}</spring:escapeBody></td>
                            <td nowrap="nowrap">
                                <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
                                    <c:if test="${inventoryChecking}">
                                        <tags:pickerDialog extraArgs="${energyCompanyId}" 
                                                id="availableSwitchPicker${switch.inventoryId}" 
                                                type="availableSwitchPicker" 
                                                destinationFieldId="newInventoryId" 
                                                immediateSelectMode="true"
                                                endAction="function(items) { return changeOut(${switch.inventoryId}, false); }" 
                                                linkType="button"
                                                buttonRenderMode="image"
                                                styleClass="vam"
                                                nameKey="changeOut"/>
                                    </c:if>
                                </cti:checkRolesAndProperties>
                                <cti:img nameKey="editConfig" href="${editConfigUrl}${switch.inventoryId}"/>
                            </td>
                        </tr>
                    </c:forEach>
                </table>
                
            </c:otherwise>
        </c:choose>
        
        <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
            <div class="actionArea">
                <form action="${createUrl}">
                    <input type="hidden" name="accountId" value="${accountId}">
                    <input type="hidden" name="hardwareClass" value="${switchClass}">
                    
                    <c:set var="switchTypes" value="${deviceTypeMap['SWITCH']}"/>
                    
                    <select name="hardwareTypeId" id="switchTypeToAdd" <c:if test="${fn:length(switchTypes) < 2}">class="dn"</c:if>>
                        <c:forEach var="deviceType" items="${switchTypes}">
                            <option value="${deviceType.hardwareTypeEntryId}">
                                <spring:escapeBody htmlEscape="true">${deviceType.displayName}</spring:escapeBody>
                            </option>
                        </c:forEach> 
                    </select>
                    <c:choose>
                        <c:when test="${not inventoryChecking}">
                            <cti:checkRolesAndProperties value="OPERATOR_CONSUMER_INFO_HARDWARES_CREATE">
                                <cti:button nameKey="add" type="submit"/>
                            </cti:checkRolesAndProperties>
                        </c:when>
                        <c:otherwise>
                            <cti:button nameKey="add" onclick="showInvCheckingPopup('switch');" dialogButton="true"/>
                        </c:otherwise>
                    </c:choose>
                </form>
            </div>
        </cti:checkRolesAndProperties>
    </tags:boxContainer2>
</c:if>

<%-- THERMOSTATS TABLE --%>
<c:set var="tstatTypes" value="${deviceTypeMap['THERMOSTAT']}"/>
<c:if test="${fn:length(tstatTypes) > 0}">
    <tags:boxContainer2 nameKey="thermostats" styleClass="hardwareContainer">
        <c:choose>
            <c:when test="${empty thermostats}">
                <i:inline key=".thermostats.none"/>
            </c:when>
            <c:otherwise>
                <tags:alternateRowReset/>
                <table class="compactResultsTable hardwareListTable">
                    <tr>
                        <th class="name"><i:inline key=".serialNumber"/></th>
                        <th class="type"><i:inline key=".thermostats.displayType"/></th>
                        <th class="label"><i:inline key=".label"/></th>
                        <th class="actions"><i:inline key=".actions"/></th>
                    </tr>
                    
                    <c:forEach var="thermostat" items="${thermostats}">
                        <tr class="<tags:alternateRow odd="" even="altRow"/>">
                            <td>
                                <a href="${viewUrl}${thermostat.inventoryId}">
                                    <spring:escapeBody htmlEscape="true">${thermostat.serialNumber}</spring:escapeBody>
                                </a>
                            </td>
                            <td><spring:escapeBody htmlEscape="true">${thermostat.displayType}</spring:escapeBody></td>
                            <td><spring:escapeBody htmlEscape="true">${thermostat.displayLabel}</spring:escapeBody></td>
                            <td nowrap="nowrap">
                                <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
                                    <c:if test="${inventoryChecking}">
                                        <tags:pickerDialog extraArgs="${energyCompanyId}" 
                                                id="availableThermostatPicker${thermostat.inventoryId}" 
                                                type="availableThermostatPicker" 
                                                destinationFieldId="newInventoryId" 
                                                immediateSelectMode="true"
                                                endAction="function(items) { return changeOut(${thermostat.inventoryId}, false); }" 
                                                linkType="button"
                                                buttonRenderMode="image"
                                                nameKey="changeOut"/>
                                    </c:if>
                                </cti:checkRolesAndProperties>
                                
                                <cti:img nameKey="editConfig" href="${editConfigUrl}${thermostat.inventoryId}"/>
                                
                                <cti:checkRolesAndProperties value="OPERATOR_CONSUMER_INFO_HARDWARES_THERMOSTAT">
                                    <c:if test="${thermostat.hardwareType.supportsSchedules}">
                                        <cti:img nameKey="savedSchedules" href="${savedSchedulesUrl}${thermostat.inventoryId}"/>
                                    </c:if>
                                    <c:if test="${thermostat.hardwareType.supportsManualAdjustment}">
                                        <cti:img nameKey="manual" href="${editManualUrl}${thermostat.inventoryId}"/>
                                    </c:if>
                                </cti:checkRolesAndProperties>
                                <cti:img nameKey="history" href="${thermostatHistoryUrl}${thermostat.inventoryId}" />
                                    
                            </td>
                        </tr>
                    </c:forEach>
                </table>
                
            </c:otherwise>
        </c:choose>
        
        <br>
        
        <table class="theremostatActionTable">
            <tr>
                <cti:checkRolesAndProperties value="OPERATOR_CONSUMER_INFO_THERMOSTATS_ALL">
                    <c:if test="${showSelectAll}">
                        <td>
                            <a href="${selectMultipleUrl}"><i:inline key=".thermostats.selectMultiple"/></a>
                        </td>
                    </c:if>
                </cti:checkRolesAndProperties>
                <td class="buttonCell">
                    <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
                            <form action="${createUrl}">
                                <input type="hidden" name="accountId" value="${accountId}">
                                <input type="hidden" name="hardwareClass" value="${thermostatClass}">

                                <select name="hardwareTypeId" id="tstatTypeToAdd" <c:if test="${fn:length(tstatTypes) < 2}">class="dn"</c:if>>
                                    <c:forEach var="deviceType" items="${tstatTypes}">
                                        <option value="${deviceType.hardwareTypeEntryId}">
                                            <spring:escapeBody htmlEscape="true">${deviceType.displayName}</spring:escapeBody>
                                        </option>
                                    </c:forEach> 
                                </select>
                            
                                <c:choose>
                                    <c:when test="${not inventoryChecking}">
                                        <cti:checkRolesAndProperties value="OPERATOR_CONSUMER_INFO_HARDWARES_CREATE">
                                            <cti:button nameKey="add" type="submit"/>
                                        </cti:checkRolesAndProperties>
                                    </c:when>
                                    <c:otherwise>
                                        <cti:button nameKey="add" onclick="showInvCheckingPopup('thermostat');" dialogButton="true"/>
                                    </c:otherwise>
                                </c:choose>
                            </form>
                    </cti:checkRolesAndProperties>
                </td>
            </tr>
        </table>
        
    </tags:boxContainer2>
</c:if>

<%-- METERS TABLE --%>
<c:set var="meterTypes" value="${deviceTypeMap['METER']}"/>
<c:if test="${fn:length(meterTypes) > 0}">
    <tags:boxContainer2 nameKey="meters" styleClass="hardwareContainer">
        <c:choose>
            <c:when test="${empty meters}">
                <i:inline key=".meters.none"/>
            </c:when>
            <c:otherwise>
                <tags:alternateRowReset/>
                <table class="compactResultsTable hardwareListTable">
                    <tr>
                        <th class="name">
                            <c:choose>
                                <c:when test="${starsMeters}">
                                    <i:inline key=".meters.meterNumber"/>
                                </c:when>
                                <c:otherwise>
                                    <i:inline key=".displayName"/>
                                </c:otherwise>
                            </c:choose>
                        </th>
                        <c:if test="${not starsMeters}">
                            <th class="type"><i:inline key=".meters.displayType"/></th>
                        </c:if>
                        <th class="label"><i:inline key=".label"/></th>
                        <c:if test="${not starsMeters}">
                            <th class="actions"><i:inline key=".actions"/></th>
                        </c:if>
                    </tr>
                    
                    <c:forEach var="meter" items="${meters}">
                        <tr class="<tags:alternateRow odd="" even="altRow"/>">
                            
                            <td>
                                <a href="${meterUrl}${meter.inventoryId}">
                                    <spring:escapeBody htmlEscape="true">${meter.displayName}</spring:escapeBody>
                                </a>
                            </td>
                            
                            <c:if test="${not starsMeters}">
                                <td><spring:escapeBody htmlEscape="true">${meter.displayType}</spring:escapeBody></td>
                            </c:if>
                            
                            <td><spring:escapeBody htmlEscape="true">${meter.displayLabel}</spring:escapeBody></td>
                            
                            <c:if test="${not starsMeters}">
                                <td nowrap="nowrap">
                                    <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
                                        
                                        <c:if test="${inventoryChecking}">
                                            <tags:pickerDialog extraArgs="${energyCompanyId}" 
                                                    id="availableMeterPicker${meter.inventoryId}" 
                                                    type="availableMctPicker" 
                                                    destinationFieldId="newInventoryId" 
                                                    immediateSelectMode="true" 
                                                    endAction="function(items) { return changeOut(${meter.inventoryId}, true); }" 
                                                    linkType="button"
                                                    buttonRenderMode="image"
                                                    nameKey="changeOut"/>
                                        </c:if>
                                        
                                    </cti:checkRolesAndProperties>
                                    
                                    <cti:img nameKey="editConfig" href="${editMeterConfigUrl}${meter.deviceId}"/>
                                    
                                    <cti:checkRolesAndProperties value="METERING">
                                        <cti:paoDetailUrl  yukonPao="${meter.yukonPao}">
                                            <cti:img nameKey="meterDetail" />
                                        </cti:paoDetailUrl>
                                    </cti:checkRolesAndProperties>
                                </td>
                            </c:if>
                            
                        </tr>
                    </c:forEach>
                </table>
                
            </c:otherwise>
        </c:choose>
            
        <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
            <cti:checkRolesAndProperties value="OPERATOR_CONSUMER_INFO_HARDWARES_CREATE">
            
                <br>
                
                <form action="/spring/stars/operator/hardware/meterProfileCreate">
                    <input type="hidden" name="accountId" value="${accountId}">
                    <table class="popupButtonTable">
                        <tr>
                            <td>
                                <c:choose>
                                    <c:when test="${starsMeters}">
                                        <cti:button nameKey="add" type="submit"/>
                                    </c:when>
                            
                                    <c:otherwise>
                                        <tags:pickerDialog extraArgs="${energyCompanyId}" 
                                                id="meterPicker" 
                                                type="availableMctPicker" 
                                                destinationFieldId="meterId" 
                                                immediateSelectMode="true"
                                                endAction="addMeter" 
                                                linkType="button" 
                                                nameKey="add"/>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </table>
                </form>
                
            </cti:checkRolesAndProperties>
        </cti:checkRolesAndProperties>
    </tags:boxContainer2>
</c:if>

<%-- GATEWAYS TABLE --%>
<c:set var="gatewayTypes" value="${deviceTypeMap['GATEWAY']}"/>
<c:if test="${fn:length(gatewayTypes) > 0}">
    <tags:boxContainer2 nameKey="gateways" styleClass="hardwareContainer">
        <c:choose>
            <c:when test="${empty gateways}">
                <i:inline key=".gateways.none"/>
            </c:when>
            <c:otherwise>
                <tags:alternateRowReset/>
                <table class="compactResultsTable hardwareListTable">
                    <tr>
                        <th class="name"><i:inline key=".displayName"/></th>
                        <th class="type"><i:inline key=".gateways.displayType"/></th>
                        <th class="label"><i:inline key=".gateways.commStatus"/></th>
                        <th class="actions"><i:inline key=".actions"/></th>
                    </tr>
                    
                    <c:forEach var="gateway" items="${gateways}">
                        <tr class="<tags:alternateRow odd="" even="altRow"/>">
                            <td>
                                <a href="${viewUrl}${gateway.inventoryId}">
                                    <spring:escapeBody htmlEscape="true">${gateway.serialNumber}</spring:escapeBody>
                                </a>
                            </td>
                            <td><spring:escapeBody htmlEscape="true">${gateway.displayType}</spring:escapeBody></td>
                            <td class="pointStateColumn">
                                <cti:pointStatusColor pointId="${gateway.commissionedId}" >
                                    <cti:pointValue pointId="${gateway.commissionedId}" format="VALUE"/>
                                </cti:pointStatusColor>
                            </td>
                            <td nowrap="nowrap">
                                <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
                                    <c:if test="${inventoryChecking}">
                                        <tags:pickerDialog extraArgs="${energyCompanyId}" 
                                                id="availableGatewayPicker${gateway.inventoryId}" 
                                                type="availableGatewayPicker" 
                                                destinationFieldId="newInventoryId"
                                                immediateSelectMode="true"
                                                endAction="function(items) { return changeOut(${gateway.inventoryId}, false); }" 
                                                nameKey="changeOut"
                                                linkType="button"
                                                buttonRenderMode="image"/>
                                    </c:if>
                                </cti:checkRolesAndProperties>
                            </td>
                        </tr>
                    </c:forEach>
                    
                </table>
            </c:otherwise>
        </c:choose>
        
        <cti:checkRolesAndProperties value="OPERATOR_CONSUMER_INFO_HARDWARES_CREATE">
            <div class="actionArea">
                <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
                    <form action="${createUrl}">
                        <input type="hidden" name="accountId" value="${accountId}">
                        <input type="hidden" name="hardwareClass" value="${gatewayClass}">
                        
                        <select name="hardwareTypeId" id="gatewayTypeToAdd" <c:if test="${fn:length(gatewayTypes) < 2}">class="dn"</c:if>>
                            <c:forEach var="deviceType" items="${gatewayTypes}">
                                <option value="${deviceType.hardwareTypeEntryId}">
                                    <spring:escapeBody htmlEscape="true">${deviceType.displayName}</spring:escapeBody>
                                </option>
                            </c:forEach> 
                        </select>
                    
                        <c:choose>
                            <c:when test="${not inventoryChecking}">
                                <cti:checkRolesAndProperties value="OPERATOR_CONSUMER_INFO_HARDWARES_CREATE">
                                    <cti:button nameKey="add" type="submit"/>
                                </cti:checkRolesAndProperties>
                            </c:when>
                            <c:otherwise>
                                <cti:button nameKey="add" type="button" onclick="showInvCheckingPopup('gateway');" dialogButton="true"/>
                            </c:otherwise>
                        </c:choose>
    
                    </form>
                </cti:checkRolesAndProperties>
            </div>
        </cti:checkRolesAndProperties>
    
    </tags:boxContainer2>
</c:if>

</cti:standardPage>