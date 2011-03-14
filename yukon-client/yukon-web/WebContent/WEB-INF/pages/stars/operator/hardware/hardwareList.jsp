<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="operator" page="hardware.list">
<cti:includeCss link="/WebConfig/yukon/styles/operator/hardware.css"/>

<cti:url var="editUrl" value="/spring/stars/operator/hardware/hardwareEdit?accountId=${accountId}&amp;inventoryId="/>
<cti:url var="editConfigUrl" value="/spring/stars/operator/hardware/config/edit?accountId=${accountId}&amp;inventoryId="/>
<cti:url var="editMeterConfigUrl" value="/spring/stars/operator/hardware/config/meterConfig?accountId=${accountId}&amp;meterId="/>
<cti:url var="editScheduleUrl" value="/spring/stars/operator/thermostatSchedule/view?accountId=${accountId}&amp;thermostatIds="/>
<cti:url var="savedSchedulesUrl" value="/spring/stars/operator/thermostatSchedule/savedSchedules?accountId=${accountId}&amp;thermostatId="/>
<cti:url var="selectMultipleUrl" value="/spring/stars/operator/thermostatSelect/select?accountId=${accountId}"/>
<cti:url var="editManualUrl" value="/spring/stars/operator/thermostatManual/view?accountId=${accountId}&amp;thermostatIds="/>
<cti:url var="configureGatewayUrl" value="/spring/stars/operator/hardware/gateway/configuration?accountId=${accountId}&amp;inventoryId="/>

<form id="changeOutForm" action="/spring/stars/operator/hardware/changeOut">
    <input type="hidden" name="accountId" value="${accountId}">
    <input type="hidden" name="changeOutId" id="changeOutId">
    <input type="hidden" name="oldInventoryId" id="oldInventoryId">
    <input type="hidden" name="isMeter" id="isMeter">
</form>

<form id="addMeterForm" action="/spring/stars/operator/hardware/addMeter">
    <input type="hidden" name="accountId" value="${accountId}">
    <input type="hidden" name="meterId" id="meterId">
</form>

<c:choose>
    <c:when test="${starsMeters}">
        <cti:url var="meterEditUrl" value="/spring/stars/operator/hardware/meterProfile?accountId=${accountId}&amp;inventoryId="/>
    </c:when>
    <c:otherwise>
        <cti:url var="meterEditUrl" value="/spring/stars/operator/hardware/hardwareEdit?accountId=${accountId}&amp;inventoryId="/>
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
            $('inventoryCheckingSwitchPopup').show();
        } else if (type == 'gateway') {
            $('inventoryCheckingGatewayPopup').show();
        } else {
            $('inventoryCheckingThermostatPopup').show();
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

</script>

<c:choose>
    <c:when test="${checkingAdd.switch}">
        <c:set var="titleKey" value=".switches.add"/>
    </c:when>
    <c:when test="${checkingAdd.gateway}">
        <c:set var="titleKey" value=".gateways.add"/>
    </c:when>
    <c:otherwise>
        <c:set var="titleKey" value=".thermostats.add"/>
    </c:otherwise>
</c:choose>

<%-- INVENTORY CHECKING SWITCH POPUP --%>
<i:simplePopup titleKey=".switches.add" id="inventoryCheckingSwitchPopup" styleClass="smallSimplePopup" showImmediately="${param.showSwitchCheckingPopup}">
    <form:form commandName="serialNumber" action="/spring/stars/operator/hardware/checkSerialNumber">
    
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".serialNumber">
                <input type="hidden" name="accountId" value="${accountId}">
                <input type="hidden" name="hardwareClass" value="${switchClass}">
                <form:input path="serialNumber" size="25"/>
            </tags:nameValue2>
        </tags:nameValueContainer2>
        
        <br>
        
        <table class="popupButtonTable">
            <tr>
                <td><input type="submit" value="<cti:msg2 key=".checkInventoryButton"/>" class="formSubmit"></td>
            </tr>
        </table>

        </form:form>
</i:simplePopup>

<%-- INVENTORY CHECKING THERMOSTAT POPUP --%>
<i:simplePopup titleKey=".thermostats.add" id="inventoryCheckingThermostatPopup" styleClass="smallSimplePopup" showImmediately="${param.showThermostatCheckingPopup}">
    <form:form commandName="serialNumber" action="/spring/stars/operator/hardware/checkSerialNumber">
    
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".serialNumber">
                <input type="hidden" name="accountId" value="${accountId}">
                <input type="hidden" name="hardwareClass" value="${thermostatClass}">
                <form:input path="serialNumber" size="25"/>
            </tags:nameValue2>
        </tags:nameValueContainer2>
        
        <br>
        
        <table class="popupButtonTable">
            <tr>
                <td><input type="submit" value="<cti:msg2 key=".checkInventoryButton"/>" class="formSubmit"></td>
            </tr>
        </table>
    
    </form:form>
</i:simplePopup>

<%-- INVENTORY CHECKING GATEWAY POPUP --%>
<i:simplePopup titleKey=".gateways.add" id="inventoryCheckingGatewayPopup" styleClass="smallSimplePopup" showImmediately="${param.showGatewayCheckingPopup}">
    <form:form commandName="serialNumber" action="/spring/stars/operator/hardware/checkSerialNumber">
    
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".serialNumber">
                <input type="hidden" name="accountId" value="${accountId}">
                <input type="hidden" name="hardwareClass" value="${gatewayClass}">
                <form:input path="serialNumber" size="25"/>
            </tags:nameValue2>
        </tags:nameValueContainer2>
        
        <br>
        
        <table class="popupButtonTable">
            <tr>
                <td><input type="submit" value="<cti:msg2 key=".checkInventoryButton"/>" class="formSubmit"></td>
            </tr>
        </table>
    
    </form:form>
</i:simplePopup>

<%-- CONFIRM ADD FROM WAREHOUSE POPUP --%>
<c:if test="${not empty confirmWarehouseSerial}">
    <i:simplePopup titleKey="${titleKey}" id="addFromWarehousePopup" styleClass="mediumSimplePopup" showImmediately="true">
        
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
        
        <form action="/spring/stars/operator/hardware/addDeviceToAccount">
            <input type="hidden" name="accountId" value="${accountId}">
            <input type="hidden" name="serialNumber" value="${checkingAdd.serialNumber}">
            <input type="hidden" name="inventoryId" value="${checkingAdd.inventoryId}">
            <input type="hidden" name="fromAccount" value="false">
            
            <table align="right">
                <tr>
                    <td>
                        <span class="buttonSeperator">
                            <input type="submit" value="<cti:msg2 key="yukon.web.components.slowInput.ok.label" />" class="formSubmit">
                        </span>
                        <span>
                            <input type="button" value="<cti:msg2 key="yukon.web.components.slowInput.cancel.label" />" onclick="javascript:$('addFromWarehousePopup').hide();" class="formSubmit">
                        </span>
                    </td>
                </tr>
            </table>
        </form>
    </i:simplePopup>
</c:if>

<%-- CONFIRM ADD FROM ACCOUNT POPUP --%>
<c:if test="${not empty confirmAccountSerial}">
    <i:simplePopup titleKey="${titleKey}" id="addFromAccountPopup" styleClass="mediumSimplePopup" showImmediately="true">
        
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
        
        <div class="hardwarePopup"><i:inline key=".serialNumber.moveToAccount" argumentSeparator="," arguments="${checkingAdd.accountNumber},${accountNumber}"/></div>
        
        <form action="/spring/stars/operator/hardware/addDeviceToAccount">
            <input type="hidden" name="accountId" value="${accountId}">
            <input type="hidden" value="${checkingAdd.serialNumber}">
            <input type="hidden" name="inventoryId" value="${checkingAdd.inventoryId}">
            <input type="hidden" name="fromAccount" value="true">
            
            <table align="right">
                <tr>
                    <td>
                        <span class="buttonSeperator">
                            <input type="submit" value="<cti:msg2 key="yukon.web.components.slowInput.ok.label" />" class="formSubmit">
                        </span>
                        <span>
                            <input type="button" value="<cti:msg2 key="yukon.web.components.slowInput.cancel.label" />" onclick="javascript:$('addFromAccountPopup').hide();" class="formSubmit">
                        </span>
                    </td>
                </tr>
            </table>
        </form>
        
    </i:simplePopup>
</c:if>

<%-- CONFIRM CREATE POPUP --%>
<c:if test="${not empty confirmCreateSerial}">
    <i:simplePopup titleKey="${titleKey}" id="createSerialPopup" styleClass="smallSimplePopup" showImmediately="true">

        <div class="hardwarePopup"><i:inline key=".serialNumber.notFoundAdd" arguments="${confirmCreateSerial}"/></div>
        
        <form action="/spring/stars/operator/hardware/hardwareCreate">
            <input type="hidden" name="accountId" value="${accountId}">
            <input type="hidden" name="serialNumber" value="${checkingAdd.serialNumber}">
            
            <c:choose>
                <c:when test="${checkingAdd.switch}">
                    <input type="hidden" name="hardwareClass" value="${switchClass}">
                </c:when>
                <c:when test="${checkingAdd.gateway}">
                    <input type="hidden" name="hardwareClass" value="${gatewayClass}">
                </c:when>
                <c:otherwise>
                    <input type="hidden" name="hardwareClass" value="${thermostatClass}">
                </c:otherwise>
            </c:choose>
            
            <table align="right">
                <tr>
                    <td>
                        <span class="buttonSeperator"><input type="submit" value="<cti:msg2 key="yukon.web.components.slowInput.ok.label" />" class="formSubmit"></span>
                        <span><input type="button" value="<cti:msg2 key="yukon.web.components.slowInput.cancel.label" />" onclick="javascript:$('createSerialPopup').hide();" class="formSubmit"></span>
                    </td>
                </tr>
            </table>
        </form>
    </i:simplePopup>
</c:if>

<%-- SERIAL NUMBER NOT FOUND POPUP --%>
<c:if test="${not empty notFoundSerial}">
    <i:simplePopup titleKey="${titleKey}" id="notFoundSerialPopup" styleClass="smallSimplePopup" showImmediately="true">

        <div class="hardwarePopup"><i:inline key=".error.notFound.serialNumber" arguments="${notFoundSerial}"/></div>
        
        <table align="right">
            <tr>
                <td>
                    <span><input type="button" value="<cti:msg2 key="yukon.web.components.slowInput.ok.label" />" onclick="javascript:$('notFoundSerialPopup').hide();" class="formSubmit"></span>
                </td>
            </tr>
        </table>
    </i:simplePopup>
</c:if>

<%-- SERIAL NUMBER FOUND ON SAME ACCOUNT POPUP --%>
<c:if test="${not empty sameAccountSerial}">
    <i:simplePopup titleKey="${titleKey}" id="sameAccountPopup" styleClass="smallSimplePopup" showImmediately="true">

        <div class="hardwarePopup"><i:inline key=".error.sameAccount.serialNumber" arguments="${sameAccountSerial}"/></div>
        
        <table align="right">
            <tr>
                <td>
                    <span><input type="button" value="<cti:msg2 key="yukon.web.components.slowInput.ok.label"/>" onclick="javascript:$('sameAccountPopup').hide();" class="formSubmit"></span>
                </td>
            </tr>
        </table>
    </i:simplePopup>
</c:if>

<%-- SERIAL NUMBER FOUND IN ANOTHER EC --%>
<c:if test="${not empty anotherECSerial}">
    <i:simplePopup titleKey="${titleKey}" id="anotherECPopup" styleClass="smallSimplePopup" showImmediately="true">

        <div class="hardwarePopup"><i:inline key=".error.anotherEC.serialNumber" arguments="${anotherEC}"/></div>
        
        <table align="right">
            <tr>
                <td>
                    <span><input type="button" value="<cti:msg2 key="yukon.web.components.slowInput.ok.label"/>" onclick="javascript:$('anotherECPopup').hide();" class="formSubmit"></span>
                </td>
            </tr>
        </table>
    </i:simplePopup>
</c:if>

<%-- SWITCHES TABLE --%>
<tags:boxContainer2 nameKey="switches">
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
                            <a href="${editUrl}${switch.inventoryId}">
                                <spring:escapeBody htmlEscape="true">${switch.serialNumber}</spring:escapeBody>
                            </a>
                        </td>
                        <td><spring:escapeBody htmlEscape="true">${switch.displayType}</spring:escapeBody></td>
                        <td><spring:escapeBody htmlEscape="true">${switch.displayLabel}</spring:escapeBody></td>
                        <td nowrap="nowrap">
                            <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
                                <c:if test="${inventoryChecking}">
                                    <tags:pickerDialog extraArgs="${energyCompanyId}" id="availableSwitchPicker${switch.inventoryId}" type="availableSwitchPicker" destinationFieldId="changeOutId" immediateSelectMode="true"
                                        endAction="function(items) { return changeOut(${switch.inventoryId}, false); }" anchorStyleClass="imgLink"><cti:img key="changeOut"/></tags:pickerDialog>
                                </c:if>
                            </cti:checkRolesAndProperties>
                            <cti:img key="editConfig" href="${editConfigUrl}${switch.inventoryId}"/>
                        </td>
                    </tr>
                </c:forEach>
            </table>
            
        </c:otherwise>
    </c:choose>
    
    <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
        
        <form action="/spring/stars/operator/hardware/hardwareCreate">
            <input type="hidden" name="accountId" value="${accountId}">
            <input type="hidden" name="hardwareClass" value="${switchClass}">
            <table class="popupButtonTable">
                <tr>
                    <td>
                        <c:choose>
                            <c:when test="${not inventoryChecking}">
                                <cti:checkRolesAndProperties value="OPERATOR_CONSUMER_INFO_HARDWARES_CREATE">
                                    <br>
                                    <cti:button key="add" type="submit"/>
                                </cti:checkRolesAndProperties>
                            </c:when>
                            <c:otherwise>
                                <br>
                                <cti:button key="add" type="button" onclick="showInvCheckingPopup('switch');"/>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </table>
        </form>
    </cti:checkRolesAndProperties>
    
</tags:boxContainer2>

<br><br>

<%-- THERMOSTATS TABLE --%>
<tags:boxContainer2 nameKey="thermostats">
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
                            <a href="${editUrl}${thermostat.inventoryId}">
                                <spring:escapeBody htmlEscape="true">${thermostat.serialNumber}</spring:escapeBody>
                            </a>
                        </td>
                        <td><spring:escapeBody htmlEscape="true">${thermostat.displayType}</spring:escapeBody></td>
                        <td><spring:escapeBody htmlEscape="true">${thermostat.displayLabel}</spring:escapeBody></td>
                        <td nowrap="nowrap">
                            <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
                                <c:if test="${inventoryChecking}">
                                    <tags:pickerDialog extraArgs="${energyCompanyId}" id="availableThermostatPicker${thermostat.inventoryId}" type="availableThermostatPicker" destinationFieldId="changeOutId" immediateSelectMode="true"
                                        endAction="function(items) { return changeOut(${thermostat.inventoryId}, false); }" anchorStyleClass="imgLink"><cti:img key="changeOut"/></tags:pickerDialog>
                                </c:if>
                            </cti:checkRolesAndProperties>
                            
                            <cti:img key="editConfig" href="${editConfigUrl}${thermostat.inventoryId}"/>
                            
                            <cti:checkRolesAndProperties value="OPERATOR_CONSUMER_INFO_HARDWARES_THERMOSTAT">
                                <c:if test="${thermostat.hardwareType.supportsSchedules}">
                                    <cti:img key="editSchedule" href="${editScheduleUrl}${thermostat.inventoryId}"/>
                                    <cti:img key="savedSchedules" href="${savedSchedulesUrl}${thermostat.inventoryId}"/>
                                </c:if>
                                <c:if test="${thermostat.hardwareType.supportsManualAdjustment}">
                                    <cti:img key="manual" href="${editManualUrl}${thermostat.inventoryId}"/>
                                </c:if>
                            </cti:checkRolesAndProperties>
                                
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
                        <form action="/spring/stars/operator/hardware/hardwareCreate">
                            <input type="hidden" name="accountId" value="${accountId}">
                            <input type="hidden" name="hardwareClass" value="${thermostatClass}">
                        
                            <c:choose>
                                <c:when test="${not inventoryChecking}">
                                    <cti:checkRolesAndProperties value="OPERATOR_CONSUMER_INFO_HARDWARES_CREATE">
                                        <cti:button key="add" type="submit"/>
                                    </cti:checkRolesAndProperties>
                                </c:when>
                                <c:otherwise>
                                    <cti:button key="add" type="button" onclick="showInvCheckingPopup('thermostat');"/>
                                </c:otherwise>
                            </c:choose>
                        </form>
                </cti:checkRolesAndProperties>
            </td>
        </tr>
    </table>
    
</tags:boxContainer2>

<br><br>

<%-- METERS TABLE --%>
<tags:boxContainer2 nameKey="meters">
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
                            <a href="${meterEditUrl}${meter.inventoryId}">
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
                                        <tags:pickerDialog extraArgs="${energyCompanyId}" id="availableMeterPicker${meter.inventoryId}" type="availableMctPicker" 
                                            destinationFieldId="changeOutId" immediateSelectMode="true" endAction="function(items) { return changeOut(${meter.inventoryId}, true); }" 
                                            anchorStyleClass="imgLink"><cti:img key="changeOut"/></tags:pickerDialog>
                                    </c:if>
                                    
                                </cti:checkRolesAndProperties>
                                
                                <cti:img key="editConfig" href="${editMeterConfigUrl}${meter.deviceId}"/>
                                
                                <cti:checkRolesAndProperties value="METERING">
                                    <cti:paoDetailUrl  yukonPao="${meter.yukonPao}">
                                        <cti:img key="meterDetail" />
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
                                    <cti:button key="add" type="submit"/>
                                </c:when>
                        
                                <c:otherwise>
                                    <tags:pickerDialog extraArgs="${energyCompanyId}" id="meterPicker" type="availableMctPicker" destinationFieldId="meterId" immediateSelectMode="true"
                                        endAction="addMeter" linkType="button" nameKey="add"/>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </table>
            </form>
        </cti:checkRolesAndProperties>
    </cti:checkRolesAndProperties>
    
</tags:boxContainer2>
<br><br>

<%-- GATEWAYS TABLE --%>
<cti:url var="gatewayControllerUrl" value="/spring/stars/operator/hardware/gateway/"/>
<cti:url var="gatewayControllerUrlParameters" value="?accountId=${accountId}&inventoryId="/>

<tags:boxContainer2 nameKey="gateways">
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
                            <a href="${editUrl}${gateway.inventoryId}">
                                <spring:escapeBody htmlEscape="true">${gateway.serialNumber}</spring:escapeBody>
                            </a>
                        </td>
                        <td><spring:escapeBody htmlEscape="true">${gateway.displayType}</spring:escapeBody></td>
                        <td>
                            <cti:classUpdater type="POINT" identifier="${gateway.commissionedId}/SHORT">
                                <cti:pointValue pointId="${gateway.commissionedId}" format="VALUE"/>
                            </cti:classUpdater>
                        </td>
                        <td nowrap="nowrap">
                            <cti:img key="editConfig" href="${configureGatewayUrl}${gateway.inventoryId}"/>
                            
                        </td>
                    </tr>
                </c:forEach>
                
            </table>
        </c:otherwise>
    </c:choose>
    

    <cti:checkRolesAndProperties value="OPERATOR_CONSUMER_INFO_HARDWARES_CREATE">
        <br>
        <div class="actionArea">
            <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
                <form action="/spring/stars/operator/hardware/hardwareCreate">
                    <input type="hidden" name="accountId" value="${accountId}">
                    <input type="hidden" name="hardwareClass" value="${gatewayClass}">

                    <c:choose>
                        <c:when test="${not inventoryChecking}">
                            <cti:checkRolesAndProperties value="OPERATOR_CONSUMER_INFO_HARDWARES_CREATE">
                                <cti:button key="add" type="submit"/>
                            </cti:checkRolesAndProperties>
                        </c:when>
                        <c:otherwise>
                            <cti:button key="add" type="button" onclick="showInvCheckingPopup('gateway');"/>
                        </c:otherwise>
                    </c:choose>

                </form>
            </cti:checkRolesAndProperties>
        </div>
    </cti:checkRolesAndProperties>

</tags:boxContainer2>

</cti:standardPage>