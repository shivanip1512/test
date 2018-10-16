<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:standardPage module="operator" page="hardware.list">
<cti:checkEnergyCompanyOperator showError="true" accountId="${accountId}">
<cti:url var="hardwareListUrl" value="/stars/operator/hardware/list?accountId=${accountId}"/>
<cti:url var="createUrl" value="/stars/operator/hardware/createPage"/>
<cti:url var="checkSnUrl" value="/stars/operator/hardware/checkSerialNumber"/>
<cti:url var="addDevice" value="/stars/operator/hardware/addDeviceToAccount"/>
<cti:url var="viewUrl" value="/stars/operator/hardware/view?accountId=${accountId}&amp;inventoryId="/>
<cti:url var="editConfigUrl" value="/stars/operator/hardware/config/edit?accountId=${accountId}&amp;inventoryId="/>
<cti:url var="editMeterConfigUrl" value="/stars/operator/hardware/config/meterConfig?accountId=${accountId}&amp;meterId="/>
<cti:url var="savedSchedulesUrl" value="/stars/operator/thermostatSchedule/savedSchedules?accountId=${accountId}&amp;thermostatIds="/>
<cti:url var="selectMultipleUrl" value="/stars/operator/thermostatSelect/select?accountId=${accountId}"/>
<cti:url var="editManualUrl" value="/stars/operator/thermostatManual/view?accountId=${accountId}&amp;thermostatIds="/>
<cti:url var="thermostatHistoryUrl" value="/stars/operator/thermostat/history/view?accountId=${accountId}&amp;thermostatIds="/>
<cti:url var="changeOutUrl" value="/stars/operator/hardware/changeOut"/>
<cti:url var="addMeterUrl" value="/stars/operator/hardware/addMeter"/>
<cti:url var="meterCreateUrl" value="/stars/operator/hardware/mp/create"/>
                    
<form id="changeOutForm" action="${changeOutUrl}">
    <input type="hidden" name="accountId" value="${accountId}">
    <input type="hidden" name="newInventoryId" id="newInventoryId">
    <input type="hidden" name="oldInventoryId" id="oldInventoryId">
    <input type="hidden" name="isMeter" id="isMeter">
    <input type="hidden" name="redirect" value="list">
</form>

<form id="addMeterForm" action="${addMeterUrl}">
    <input type="hidden" name="accountId" value="${accountId}">
    <input type="hidden" name="meterId" id="meterId">
</form>

<c:choose>
    <c:when test="${starsMeters}">
        <cti:url var="meterUrl" value="/stars/operator/hardware/mp/view?accountId=${accountId}&amp;inventoryId="/>
    </c:when>
    <c:otherwise>
        <cti:url var="meterUrl" value="/stars/operator/hardware/view?accountId=${accountId}&amp;inventoryId="/>
    </c:otherwise>
</c:choose>

<script type="text/javascript">
    function showAddSwitchPopup() {
        $('#addSwitchPopup').show();
    }

    function checkSerialNumber() {
        $('#serialNumberUnavailable').show();
    }

    function showInvCheckingPopup(type) {
        if(type === 'switch') {
            $('#addSwitchType').val($('#switchTypeToAdd').val());
            showSimplePopup('inventoryCheckingSwitchPopup');
        } else if (type === 'gateway') {
            $('#addGatewayType').val($('#gatewayTypeToAdd').val());
            showSimplePopup('inventoryCheckingGatewayPopup');
        } else {
            $('#addTstatType').val($('#tstatTypeToAdd').val());
            showSimplePopup('inventoryCheckingThermostatPopup');
        }
    }

    function addMeter() {
        var form = $('#addMeterForm');
        form.submit();
        return true;
    }

    function changeOut(oldId, isMeter) {
        $('#oldInventoryId').val(oldId);

        if(isMeter) {
            $('#isMeter').val('true');
        } else {
            $('#isMeter').val('false');
        }

        var form = $('#changeOutForm');
        form.submit();
        return true;
    }
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
        showImmediately="${showSwitchCheckingPopup}">
    <c:if test="${showSwitchCheckingPopup}">
        <cti:flashScopeMessages/>
    </c:if>
    <form:form modelAttribute="serialNumberSwitch" action="${checkSnUrl}Switch">
        <cti:csrfToken/>
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".serialNumber">
                <input type="hidden" name="accountId" value="${accountId}">
                <input type="hidden" name="hardwareTypeId" id="addSwitchType">
                <tags:input path="serialNumber" size="25"/>
            </tags:nameValue2>
        </tags:nameValueContainer2>

        <div class="action-area">
            <cti:button nameKey="checkInventoryButton" onclick="showInvCheckingPopup('switch');" type="submit"/>
        </div>
    </form:form>
</i:simplePopup>

<%-- INVENTORY CHECKING THERMOSTAT POPUP --%>
<i:simplePopup titleKey=".thermostats.add" 
        id="inventoryCheckingThermostatPopup" 
        showImmediately="${showThermostatCheckingPopup}">
    <c:if test="${showThermostatCheckingPopup}">
        <cti:flashScopeMessages/>
    </c:if>
    <form:form modelAttribute="serialNumberThermostat" action="${checkSnUrl}Thermostat">
        <cti:csrfToken/>
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".serialNumber">
                <input type="hidden" name="accountId" value="${accountId}">
                <input type="hidden" name="hardwareTypeId" id="addTstatType">
                <tags:input path="serialNumber" size="25"/>
            </tags:nameValue2>
        </tags:nameValueContainer2>

        <div class="action-area">
            <cti:button nameKey="checkInventoryButton" onclick="showInvCheckingPopup('thermostat');" type="submit"/>
        </div>

    </form:form>
</i:simplePopup>

<%-- INVENTORY CHECKING GATEWAY POPUP --%>
<i:simplePopup titleKey=".gateways.add" 
        id="inventoryCheckingGatewayPopup" 
        showImmediately="${showGatewayCheckingPopup}">
    <c:if test="${showGatewayCheckingPopup}">
        <cti:flashScopeMessages/>
    </c:if>
    <form:form modelAttribute="serialNumberGateway" action="${checkSnUrl}Gateway">
        <cti:csrfToken/>
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".serialNumber">
                <input type="hidden" name="accountId" value="${accountId}">
                <input type="hidden" name="hardwareTypeId" id="addGatewayType">
                <tags:input path="serialNumber" size="25"/>
            </tags:nameValue2>
        </tags:nameValueContainer2>

        <div class="action-area">
            <cti:button nameKey="checkInventoryButton" onclick="showInvCheckingPopup('gateway');" type="submit"/>
        </div>
    
    </form:form>
</i:simplePopup>

<%-- CONFIRM ADD FROM WAREHOUSE POPUP --%>
<c:if test="${not empty confirmWarehouseSerial}">
    <i:simplePopup titleKey="${titleKey}" 
            id="addFromWarehousePopup" 
            showImmediately="true">

        <div style="padding:10px;"><i:inline key=".serialNumber.inWarehouse"/></div>

        <table class="compact-results-table invCheckingTable" align="center">
            <thead>
                <tr>
                    <th nowrap="nowrap"><i:inline key=".serialNumberShort"/></th>
                    <th nowrap="nowrap"><i:inline key=".altTrackingNumber"/></th>
                    <th nowrap="nowrap"><i:inline key=".deviceType"/></th>
                    <th nowrap="nowrap"><i:inline key=".warehouse"/></th>
                </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <tr>
                    <td>${fn:escapeXml(checkingAdd.serialNumber)}</td>
                    <td>${fn:escapeXml(checkingAdd.altTrackingNumber)}</td>
                    <td>${fn:escapeXml(checkingAdd.deviceType)}</td>
                    <td>${fn:escapeXml(checkingAdd.warehouse)}</td>
                </tr>
            </tbody>
        </table>

        <div style="padding:10px;"><i:inline key=".serialNumber.addToAccount"/></div>

        <form action="${addDevice}">
            <input type="hidden" name="accountId" value="${accountId}">
            <input type="hidden" name="serialNumber" value="${checkingAdd.serialNumber}">
            <input type="hidden" name="inventoryId" value="${checkingAdd.inventoryId}">
            <input type="hidden" name="fromAccount" value="false">

            <div class="action-area">
                <cti:button nameKey="ok" type="submit" classes="primary action"/>
                <cti:button nameKey="cancel" onclick="window.location='${hardwareListUrl}';"/>
            </div>
        </form>
    </i:simplePopup>
</c:if>

<%-- CONFIRM ADD FROM ACCOUNT POPUP --%>
<c:if test="${not empty confirmAccountSerial}">
    <i:simplePopup titleKey="${titleKey}" 
            id="addFromAccountPopup" 
            showImmediately="true">

        <div class="hardwarePopup"><i:inline key=".serialNumber.foundOnAnotherAccount"/></div>

        <table class="compact-results-table invCheckingTable" align="center">
            <thead>
                <tr>
                    <th nowrap="nowrap"><i:inline key=".accountNumber"/></th>
                    <th nowrap="nowrap"><i:inline key=".name"/></th>
                    <th nowrap="nowrap"><i:inline key=".serialNumberShort"/></th>
                    <th nowrap="nowrap"><i:inline key=".deviceType"/></th>
                </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <tr title="<tags:address address="${checkingAdd.address}" inLine="true"/>">
                    <td>${fn:escapeXml(checkingAdd.accountNumber)}</td>
                    <td>${fn:escapeXml(checkingAdd.name)}</td>
                    <td>${fn:escapeXml(checkingAdd.serialNumber)}</td>
                    <td>${fn:escapeXml(checkingAdd.deviceType)}</td>
                </tr>
            </tbody>
        </table>

        <div class="hardwarePopup">
            <cti:list var="arguments">
                    <cti:item value="${checkingAdd.accountNumber}"/>
                    <cti:item value="${accountNumber}"/>
            </cti:list>
            <i:inline key=".serialNumber.moveToAccount" arguments="${arguments}"/>
        </div>

        <form action="${addDevice}">
            <input type="hidden" name="accountId" value="${accountId}">
            <input type="hidden" value="${checkingAdd.serialNumber}">
            <input type="hidden" name="inventoryId" value="${checkingAdd.inventoryId}">
            <input type="hidden" name="fromAccount" value="true">

            <div class="action-area">
                <cti:button nameKey="ok" type="submit" classes="primary action"/>
                <cti:button nameKey="cancel" onclick="window.location='${hardwareListUrl}';"/>
            </div>
        </form>
    </i:simplePopup>
</c:if>

<%-- CONFIRM CREATE POPUP --%>
<c:if test="${not empty confirmCreateSerial}">
    <i:simplePopup titleKey="${titleKey}" 
            id="createSerialPopup" 
            showImmediately="true">

        <div class="hardwarePopup"><i:inline key=".serialNumber.notFoundAdd" arguments="${confirmCreateSerial}"/></div>

        <form action="${createUrl}">
            <input type="hidden" name="accountId" value="${accountId}">
            <input type="hidden" name="serialNumber" value="${checkingAdd.serialNumber}">
            <input type="hidden" name="hardwareTypeId" value="${checkingAdd.hardwareTypeId}">

            <div class="action-area">
                <cti:button nameKey="ok" type="submit" classes="js-blocker primary action"/>
                <cti:button nameKey="cancel" onclick="window.location='${hardwareListUrl}';"/>
            </div>
        </form>
    </i:simplePopup>
</c:if>

<%-- SERIAL NUMBER NOT FOUND POPUP --%>
<c:if test="${not empty notFoundSerial}">
    <i:simplePopup titleKey="${titleKey}" 
            id="notFoundSerialPopup" 
            showImmediately="true">

        <div class="hardwarePopup"><i:inline key=".error.notFound.serialNumber" arguments="${notFoundSerial}"/></div>

        <div class="action-area">
            <cti:button nameKey="ok" type="submit" classes="js-blocker primary action"/>
            <cti:button nameKey="cancel" onclick="window.location='${hardwareListUrl}';"/>
        </div>
    </i:simplePopup>
</c:if>

<%-- SERIAL NUMBER FOUND ON SAME ACCOUNT POPUP --%>
<c:if test="${not empty sameAccountSerial}">
    <i:simplePopup titleKey="${titleKey}" 
            id="sameAccountPopup" 
            showImmediately="true">

        <div class="hardwarePopup"><i:inline key=".error.sameAccount.serialNumber" arguments="${sameAccountSerial}"/></div>

        <div class="action-area">
            <cti:button nameKey="ok"  classes="primary action" onclick="window.location='${hardwareListUrl}';"/>
        </div>
    </i:simplePopup>
</c:if>

<%-- SERIAL NUMBER FOUND IN ANOTHER EC --%>
<c:if test="${not empty anotherECSerial}">
    <i:simplePopup titleKey="${titleKey}" 
            id="anotherECPopup" 
            showImmediately="true">

        <div class="hardwarePopup"><i:inline key=".error.anotherEC.serialNumber" arguments="${anotherEC}"/></div>

        <div class="action-area">
            <cti:button nameKey="ok" classes="primary action" onclick="window.location='${hardwareListUrl}';"/>
        </div>
    </i:simplePopup>
</c:if>

<%-- SWITCHES TABLE --%>
<c:set var="switchTypes" value="${deviceTypeMap['SWITCH']}"/>
<c:if test="${fn:length(switchTypes) > 0}">
    <tags:sectionContainer2 nameKey="switches" styleClass="stacked">
        <c:choose>
            <c:when test="${empty switches}">
                <span class="empty-list"><i:inline key=".switches.none"/></span>
            </c:when>
            <c:otherwise>
                <table class="compact-results-table dashed row-highlighting has-actions">
                    <thead>
                        <tr>
                            <th class="row-icon"/>
                            <th><i:inline key=".serialNumber"/></th>
                            <th><i:inline key=".displayType.SWITCH"/></th>
                            <th><i:inline key=".label"/></th>
                            <th class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
                        </tr>
                    </thead>
                    <tfoot></tfoot>
                    <tbody>
                        <c:forEach var="hwSwitch" items="${switches}">
                            <tr>
                                <td>
                                    <c:if test="${notesList.contains(hwSwitch.deviceId)}">
                                        <cti:msg2 var="viewAllNotesTitle" key="yukon.web.common.paoNotesSearch.viewAllNotes"/>
                                        <cti:icon icon="icon-notes-pin" classes="js-view-all-notes cp" title="${viewAllNotesTitle}" data-pao-id="${hwSwitch.deviceId}"/>
                                    </c:if>
                                </td>
                                <td>
                                    <a href="${viewUrl}${hwSwitch.inventoryId}">${fn:escapeXml(hwSwitch.serialNumber)}</a>
                                </td>
                                <td>${fn:escapeXml(hwSwitch.displayType)}</td>
                                <td>
                                    ${fn:escapeXml(hwSwitch.displayLabel)}
                                </td>
                                <td>
                                    <cm:dropdown>
                                        <cm:dropdownOption key=".editConfig.label" icon="icon-cog-edit" href="${editConfigUrl}${hwSwitch.inventoryId}" />
                                        <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
                                            <c:if test="${inventoryChecking}">
                                                <li>
                                                    <tags:pickerDialog extraArgs="${energyCompanyId}" 
                                                            id="availableSwitchPicker${hwSwitch.inventoryId}" 
                                                            type="availableSwitchPicker" 
                                                            destinationFieldId="newInventoryId" 
                                                            immediateSelectMode="true"
                                                            endAction="function(items) { return changeOut(${hwSwitch.inventoryId}, false); }">
                                                            <cti:icon icon="icon-arrow-swap"/>
                                                            <span class="dib"><cti:msg2 key=".changeOut.label"/></span>
                                                    </tags:pickerDialog>
                                                </li>
                                            </c:if>
                                        </cti:checkRolesAndProperties>
                                    </cm:dropdown>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>

        <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
            <div class="action-area">
                <form action="${createUrl}">
                    <input type="hidden" name="accountId" value="${accountId}">
                    <input type="hidden" name="hardwareClass" value="${switchClass}">

                    <c:set var="switchTypes" value="${deviceTypeMap['SWITCH']}"/>

                    <select name="hardwareTypeId" id="switchTypeToAdd" <c:if test="${fn:length(switchTypes) < 2}">class="dn"</c:if>>
                        <c:forEach var="deviceType" items="${switchTypes}">
                            <option value="${deviceType.hardwareTypeEntryId}">
                                ${fn:escapeXml(deviceType.displayName)}
                            </option>
                        </c:forEach> 
                    </select>
                    <c:choose>
                        <c:when test="${not inventoryChecking}">
                            <cti:checkRolesAndProperties value="OPERATOR_CONSUMER_INFO_HARDWARES_CREATE">
                                <cti:button nameKey="add" type="submit" icon="icon-add"/>
                            </cti:checkRolesAndProperties>
                        </c:when>
                        <c:otherwise>
                            <cti:button nameKey="add" icon="icon-add" onclick="showInvCheckingPopup('switch');" dialogButton="true"/>
                        </c:otherwise>
                    </c:choose>
                </form>
            </div>
        </cti:checkRolesAndProperties>
    </tags:sectionContainer2>
</c:if>

<%-- THERMOSTATS TABLE --%>
<c:set var="tstatTypes" value="${deviceTypeMap['THERMOSTAT']}"/>
<c:if test="${fn:length(tstatTypes) > 0}">
    <tags:sectionContainer2 nameKey="thermostats" styleClass="stacked">
        <c:choose>
            <c:when test="${empty thermostats}">
                <span class="empty-list"><i:inline key=".thermostats.none"/></span>
            </c:when>
            <c:otherwise>
                <table class="compact-results-table dashed row-highlighting has-actions">
                    <thead>
                        <tr>
                            <th class="row-icon"/>
                            <th><i:inline key=".serialNumber"/></th>
                            <th><i:inline key=".displayType.THERMOSTAT"/></th>
                            <th><i:inline key=".label"/></th>
                            <th class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
                        </tr>
                    </thead>
                    <tfoot></tfoot>
                    <tbody>
                        <c:forEach var="thermostat" items="${thermostats}">
                            <tr>
                                <td>
                                    <c:if test="${notesList.contains(thermostat.deviceId)}">
                                        <cti:msg2 var="viewAllNotesTitle" key="yukon.web.common.paoNotesSearch.viewAllNotes"/>
                                        <cti:icon icon="icon-notes-pin" classes="js-view-all-notes cp" title="${viewAllNotesTitle}" data-pao-id="${thermostat.deviceId}"/>
                                    </c:if>
                                </td>
                                <td>
                                    <a href="${viewUrl}${thermostat.inventoryId}">
                                        ${fn:escapeXml(thermostat.serialNumber)}
                                    </a>
                                </td>
                                <td>${fn:escapeXml(thermostat.displayType)}</td>
                                <td>
                                    ${fn:escapeXml(thermostat.displayLabel)}
                                </td>
                                <td>
                                    <cm:dropdown>
                                        <cm:dropdownOption key=".editConfig.label" icon="icon-cog-edit" href="${editConfigUrl}${thermostat.inventoryId}" />
                                        <cti:checkRolesAndProperties value="OPERATOR_CONSUMER_INFO_HARDWARES_THERMOSTAT">
                                            <c:if test="${thermostat.hardwareType.supportsSchedules}">
                                                <cm:dropdownOption key=".savedSchedules.label" icon="icon-clipboard" href="${savedSchedulesUrl}${thermostat.inventoryId}" />
                                            </c:if>
                                            <c:if test="${thermostat.hardwareType.supportsManualAdjustment}">
                                                <cm:dropdownOption key=".manual.label" icon="icon-wrench" href="${editManualUrl}${thermostat.inventoryId}" />
                                            </c:if>
                                        </cti:checkRolesAndProperties>
                                        <c:if test="${thermostat.hardwareType.supportsSchedules}">
                                            <cm:dropdownOption key=".history.label" icon="icon-time" href="${thermostatHistoryUrl}${thermostat.inventoryId}" />
                                        </c:if>
                                        <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
                                            <c:if test="${inventoryChecking}">
                                                <li>
                                                    <tags:pickerDialog extraArgs="${energyCompanyId}" 
                                                            id="availableThermostatPicker${thermostat.inventoryId}" 
                                                            type="availableThermostatPicker" 
                                                            destinationFieldId="newInventoryId" 
                                                            immediateSelectMode="true"
                                                            endAction="function(items) { return changeOut(${thermostat.inventoryId}, false); }" >
                                                            <cti:icon icon="icon-arrow-swap"/>
                                                            <cti:msg2 key=".changeOut.label"/> </tags:pickerDialog>
                                                    </li>
                                            </c:if>
                                        </cti:checkRolesAndProperties>
                                    </cm:dropdown>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>

        <div class="action-area">
            <cti:checkRolesAndProperties value="OPERATOR_CONSUMER_INFO_THERMOSTATS_ALL">
                <c:if test="${showSelectAll}">
                    <span class="fl">
                        <a href="${selectMultipleUrl}"><i:inline key=".thermostats.selectMultiple"/></a>
                    </span>
                </c:if>
            </cti:checkRolesAndProperties>

            <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
                <span class="fr">
                    <form action="${createUrl}">
                        <input type="hidden" name="accountId" value="${accountId}">
                        <input type="hidden" name="hardwareClass" value="${thermostatClass}">

                        <select name="hardwareTypeId" id="tstatTypeToAdd" <c:if test="${fn:length(tstatTypes) < 2}">class="dn"</c:if>>
                            <c:forEach var="deviceType" items="${tstatTypes}">
                                <option value="${deviceType.hardwareTypeEntryId}">
                                    ${fn:escapeXml(deviceType.displayName)}
                                </option>
                            </c:forEach> 
                        </select>

                        <c:choose>
                            <c:when test="${not inventoryChecking}">
                                <cti:checkRolesAndProperties value="OPERATOR_CONSUMER_INFO_HARDWARES_CREATE">
                                    <cti:button nameKey="add" type="submit" icon="icon-add"/>
                                </cti:checkRolesAndProperties>
                            </c:when>
                            <c:otherwise>
                                <cti:button nameKey="add" onclick="showInvCheckingPopup('thermostat');" dialogButton="true" icon="icon-add"/>
                            </c:otherwise>
                        </c:choose>
                    </form>
                </span>
            </cti:checkRolesAndProperties>
        </div>
    </tags:sectionContainer2>
</c:if>

<%-- METERS TABLE --%>
<c:set var="meterTypes" value="${deviceTypeMap['METER']}"/>
<c:if test="${fn:length(meterTypes) > 0 || !starsMeters}">
    <tags:sectionContainer2 nameKey="meters" styleClass="stacked">
        <c:choose>
            <c:when test="${empty meters}">
                <span class="empty-list"><i:inline key=".meters.none"/></span>
            </c:when>
            <c:otherwise>
                <table class="compact-results-table dashed row-highlighting has-actions">
                    <thead>
                        <tr>
                            <c:if test="${not starsMeters}">
                                <th class="row-icon"/>
                            </c:if>
                            <th>
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
                                <th><i:inline key=".displayType.METER"/></th>
                            </c:if>
                            <th><i:inline key=".label"/></th>
                            <c:if test="${not starsMeters}">
                                <th class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
                            </c:if>
                        </tr>
                    </thead>
                    <tfoot></tfoot>
                    <tbody>
                        <c:forEach var="meter" items="${meters}">
                            <tr>
                                <c:if test="${not starsMeters}">
                                    <td>
                                        <c:if test="${notesList.contains(meter.deviceId)}">
                                            <cti:msg2 var="viewAllNotesTitle" key="yukon.web.common.paoNotesSearch.viewAllNotes"/>
                                            <cti:icon icon="icon-notes-pin" classes="js-view-all-notes cp" title="${viewAllNotesTitle}" data-pao-id="${meter.deviceId}"/>
                                        </c:if>
                                    </td>
                                </c:if>
                                <td>
                                    <a href="${meterUrl}${meter.inventoryId}">
                                        ${fn:escapeXml(meter.displayName)}
                                    </a>
                                </td>

                                <c:if test="${starsMeters}">
                                    <td>${fn:escapeXml(meter.displayLabel)}</td>
                                </c:if>

                                <c:if test="${not starsMeters}">
                                    <td>${fn:escapeXml(meter.displayType)}</td>
                                    <td>
                                        ${fn:escapeXml(meter.displayLabel)}
                                    </td>
                                    <td>
                                        <cm:dropdown>
                                            <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
                                                <c:if test="${inventoryChecking}">
                                                    <li>
                                                        <tags:pickerDialog extraArgs="${energyCompanyId}"
                                                                id="availableMeterPicker${meter.inventoryId}"
                                                                type="availableMctPicker"
                                                                destinationFieldId="newInventoryId"
                                                                immediateSelectMode="true"
                                                                endAction="function(items) { return changeOut(${meter.inventoryId}, true); }" >
                                                            <cti:icon icon="icon-arrow-swap"/>
                                                            <cti:msg2 key=".changeOut.label"/> 
                                                        </tags:pickerDialog>
                                                    </li>
                                                </c:if>
                                            </cti:checkRolesAndProperties>
                                            <cm:dropdownOption key=".editConfig.label" icon="icon-cog-edit" href="${editMeterConfigUrl}${meter.deviceId}" />
                                            <cti:checkRolesAndProperties value="METERING">
                                                <li>
                                                    <cti:paoDetailUrl  yukonPao="${meter.yukonPao}">
                                                        <cti:icon icon="icon-control-equalizer-blue"/>
                                                        <cti:msg2 key="yukon.web.components.button.meterDetail.label"/>
                                                    </cti:paoDetailUrl>
                                                </li>
                                            </cti:checkRolesAndProperties>
                                        </cm:dropdown>
                                    </td>
                                </c:if>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>

        <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
            <cti:checkRolesAndProperties value="OPERATOR_CONSUMER_INFO_HARDWARES_CREATE">
                <div class="action-area">
                    <form action="${meterCreateUrl}" method="get">
                        <input type="hidden" name="accountId" value="${accountId}">
                        <c:choose>
                            <c:when test="${starsMeters}">
                                <cti:button nameKey="add" type="submit" icon="icon-add"/>
                            </c:when>

                            <c:otherwise>
                                <tags:pickerDialog extraArgs="${energyCompanyId}"
                                        id="meterPicker"
                                        type="availableMctPicker"
                                        destinationFieldId="meterId"
                                        immediateSelectMode="true"
                                        endAction="addMeter"
                                        linkType="button"
                                        icon="icon-add"
                                        nameKey="add"/>
                            </c:otherwise>
                        </c:choose>
                    </form>
                </div>
            </cti:checkRolesAndProperties>
        </cti:checkRolesAndProperties>
    </tags:sectionContainer2>
</c:if>

<%-- GATEWAYS TABLE --%>
<c:set var="gatewayTypes" value="${deviceTypeMap['GATEWAY']}"/>
<c:if test="${fn:length(gatewayTypes) > 0}">
    <tags:sectionContainer2 nameKey="gateways" styleClass="stacked">
        <c:choose>
            <c:when test="${empty gateways}">
                <span class="empty-list"><i:inline key=".gateways.none"/></span>
            </c:when>
            <c:otherwise>
                <table class="compact-results-table dashed row-highlighting has-actions">
                    <thead>
                        <tr>
                            <th class="row-icon"/>
                            <th><i:inline key=".displayName"/></th>
                            <th><i:inline key=".displayType.GATEWAY"/></th>
                            <th><i:inline key=".gateways.commStatus"/></th>
                            <th class="action-column">
                                <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
                                    <c:if test="${inventoryChecking}">
                                        <cti:icon icon="icon-cog" classes="M0"/>
                                    </c:if>
                                </cti:checkRolesAndProperties>
                            </th>
                        </tr>
                    </thead>
                    <tfoot></tfoot>
                    <tbody>
                        <c:forEach var="gateway" items="${gateways}">
                            <tr>
                                <td>
                                    <c:if test="${notesList.contains(gateway.deviceId)}">
                                        <cti:msg2 var="viewAllNotesTitle" key="yukon.web.common.paoNotesSearch.viewAllNotes"/>
                                        <cti:icon icon="icon-notes-pin" classes="js-view-all-notes cp" title="${viewAllNotesTitle}" data-pao-id="${gateway.deviceId}"/>
                                    </c:if>
                                </td>
                                <td>
                                    <a href="${viewUrl}${gateway.inventoryId}">
                                        ${fn:escapeXml(gateway.serialNumber)}
                                    </a>
                                </td>
                                <td>${fn:escapeXml(gateway.displayType)}</td>
                                <td class="pointStateColumn">
                                    <cti:pointStatus pointId="${gateway.commissionedId}"/>&nbsp;
                                    <cti:pointValue pointId="${gateway.commissionedId}" format="VALUE"/>
                                </td>
                                <td>
                                    <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
                                        <c:if test="${inventoryChecking}">
                                            <cm:dropdown>
                                                <li>
                                                    <tags:pickerDialog extraArgs="${energyCompanyId}" 
                                                            id="availableGatewayPicker${gateway.inventoryId}" 
                                                            type="availableGatewayPicker" 
                                                            destinationFieldId="newInventoryId"
                                                            immediateSelectMode="true"
                                                            endAction="function(items) { return changeOut(${gateway.inventoryId}, false); }">
                                                        <cti:icon icon="icon-arrow-swap"/>
                                                        <cti:msg2 key=".changeOut.label"/>
                                                    </tags:pickerDialog>
                                                </li>
                                            </cm:dropdown>
                                        </c:if>
                                    </cti:checkRolesAndProperties>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>

        <cti:checkRolesAndProperties value="OPERATOR_CONSUMER_INFO_HARDWARES_CREATE">
            <div class="action-area">
                <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
                    <form action="${createUrl}">
                        <input type="hidden" name="accountId" value="${accountId}">
                        <input type="hidden" name="hardwareClass" value="${gatewayClass}">

                        <select name="hardwareTypeId" id="gatewayTypeToAdd" <c:if test="${fn:length(gatewayTypes) < 2}">class="dn"</c:if>>
                            <c:forEach var="deviceType" items="${gatewayTypes}">
                                <option value="${deviceType.hardwareTypeEntryId}">
                                    ${fn:escapeXml(deviceType.displayName)}
                                </option>
                            </c:forEach> 
                        </select>

                        <c:choose>
                            <c:when test="${not inventoryChecking}">
                                <cti:checkRolesAndProperties value="OPERATOR_CONSUMER_INFO_HARDWARES_CREATE">
                                    <cti:button nameKey="add" type="submit" icon="icon-add"/>
                                </cti:checkRolesAndProperties>
                            </c:when>
                            <c:otherwise>
                                <cti:button nameKey="add" icon="icon-add" onclick="showInvCheckingPopup('gateway');" dialogButton="true"/>
                            </c:otherwise>
                        </c:choose>
                    </form>
                </cti:checkRolesAndProperties>
            </div>
        </cti:checkRolesAndProperties>
    </tags:sectionContainer2>
</c:if>
</cti:checkEnergyCompanyOperator>
<div class="dn" id="js-pao-notes-popup"></div>
<cti:includeScript link="/resources/js/pages/yukon.tools.paonotespopup.js"/>
</cti:standardPage>