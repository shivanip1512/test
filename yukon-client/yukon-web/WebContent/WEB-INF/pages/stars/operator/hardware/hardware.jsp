<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="operator" page="hardware.${mode}">
<tags:setFormEditMode mode="${mode}"/>
<cti:includeCss link="/WebConfig/yukon/styles/operator/hardware.css"/>

<script type="text/javascript">
YEvent.observeSelectorClick('#newButton', function(event) {
    $('twoWayPickerContainer').hide();
    $('newTwoWayDeviceContainer').show();
    var deviceNameInput = $('twoWayDeviceName');
    deviceNameInput.value = '';
    deviceNameInput.focus();
    $('creatingNewTwoWayDevice').value = true;
});

YEvent.observeSelectorClick('#chooseButton', function(event) {
    $('twoWayPickerContainer').show();
    $('newTwoWayDeviceContainer').hide();
    $('creatingNewTwoWayDevice').value = false;
});

function showDeletePopup() {
    $('deleteHardwarePopup').show();
}

function hideDeletePopup() {
	$('deleteHardwarePopup').hide();
}

function updateServiceCompanyInfo() {
    var url = '/spring/stars/operator/hardware/serviceCompanyInfo';
    var serviceCompanyId = 

    	<cti:displayForPageEditModes modes="EDIT,CREATE">
            $F('serviceCompanyId');
        </cti:displayForPageEditModes>

    	<cti:displayForPageEditModes modes="VIEW">
            ${hardwareDto.serviceCompanyId};
        </cti:displayForPageEditModes>
        
    if (serviceCompanyId > 0) {
        var params = {'serviceCompanyId' : serviceCompanyId};
        new Ajax.Updater('serviceCompanyContainer', url, {method: 'get', evalScripts: true, parameters: params});
    } else {
        $('serviceCompanyContainer').innerHTML = '';
    }
}

Event.observe(window, 'load', updateServiceCompanyInfo);

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
    
    <form id="changeOutForm" action="/spring/stars/operator/hardware/changeOut">
        <input type="hidden" name="accountId" value="${accountId}">
        <input type="hidden" name="newInventoryId" id="newInventoryId">
        <input type="hidden" name="oldInventoryId" id="oldInventoryId">
        <input type="hidden" name="isMeter" id="isMeter">
        <input type="hidden" name="redirect" value="view">
    </form>
    
    <c:if test="${showTextMessageAction}">
        <!-- Send Text Message Popup -->
        <tags:setFormEditMode mode="${editMode}"/>
        <i:simplePopup titleKey=".sendTextMsg" id="textMsgPopup" on="#sendTextMsg" styleClass="smallSimplePopup">
            <form:form action="sendTextMessage" commandName="textMessage" method="post">
                <form:hidden path="accountId"/>
                <form:hidden path="inventoryId"/>
                <form:hidden path="gatewayId"/>
                <tags:textarea rows="6" cols="60" path="message"/>
                <tags:nameValueContainer2>
                    <tags:checkboxNameValue nameKey=".confirmationRequired" path="confirmationRequired"/>
                    <tags:nameValue2 nameKey=".displayDuration">
                        <form:select path="displayDuration">
                            <c:forEach var="duration" items="${durations}">
                                <cti:formatDuration type="DHMS_REDUCED" value="${duration.millis}" var="durationLabel"/>
                                <form:option value="${duration}" label="${durationLabel}"/>
                            </c:forEach>
                        </form:select>
                    </tags:nameValue2>
                    
                    <%--<tags:selectNameValue nameKey=".displayDuration" items="${durations}" path="displayDuration"/> --%>
                    
                    <tags:nameValue2 nameKey=".startTime">
                        <tags:dateTimeInput path="startTime" inline="true" fieldValue="${textMessage.startTime}"/>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
                <div class="actionArea">
                    <cti:button key="send" type="submit"/>
                    <cti:button key="cancel" onclick="$('textMsgPopup').hide()"/>
                </div>
            </form:form>
        </i:simplePopup>
        <tags:setFormEditMode mode="${mode}"/>
    </c:if>
    
    <!-- Delete Hardware Popup -->
    <i:simplePopup styleClass="mediumSimplePopup" titleKey=".deleteDevice" id="deleteHardwarePopup" arguments="${hardwareDto.displayName}">
        <form id="deleteForm" action="/spring/stars/operator/hardware/delete" method="post">
            <input type="hidden" name="inventoryId" value="${inventoryId}">
            <input type="hidden" name="accountId" value="${accountId}">
            <c:choose>
                <c:when test="${showDeviceName}">
                    <c:set var="deleteMsgKeySuffix" value="DeviceName"/>
                </c:when>
                <c:otherwise>
                    <c:set var="deleteMsgKeySuffix" value="SerialNumber"/>
                </c:otherwise>
            </c:choose>
            <cti:msg2 key=".deleteMessage${deleteMsgKeySuffix}" argument="${hardwareDto.displayName}"/>
            <br><br>
            <input type="radio" name="deleteOption" value="remove" checked="checked" id="removeRadio">
            <label for="removeRadio" class="radioLabel"><i:inline key=".deleteOption1"/></label>
            <br>
            <input type="radio" name="deleteOption" value="delete" id="deleteRadio">
            <label for="deleteRadio" class="radioLabel"><i:inline key=".deleteOption2"/></label>
            <br><br>
            <table class="popupButtonTable">
                <tr>
                    <td>
                        <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
                            <cti:button key="delete" type="submit" name="delete"/>
                        </cti:checkRolesAndProperties>
                        <cti:button key="cancel" onclick="hideDeletePopup()"/>
                    </td>
                </tr>
            </table>
        </form>
    </i:simplePopup>
    
    <cti:msg2 key=".noneSelectOption" var="noneSelectOption"/>
    
    <cti:displayForPageEditModes modes="EDIT">
        <cti:url value="/spring/stars/operator/hardware/update" var="action"/>
    </cti:displayForPageEditModes>
    <cti:displayForPageEditModes modes="CREATE">
        <cti:url value="/spring/stars/operator/hardware/create" var="action"/>
    </cti:displayForPageEditModes>
    
    <cti:dataGrid cols="2" rowStyle="vertical-align:top;" cellStyle="padding-right:20px;">
    
        <%-- LEFT SIDE COLUMN --%>
        <cti:dataGridCell>
        
            <form:form id="updateForm" commandName="hardwareDto" action="${action}">
    
                <input type="hidden" name="accountId" value="${accountId}">
                <input type="hidden" name="inventoryId" value="${inventoryId}">
                <form:hidden path="energyCompanyId"/>
                <form:hidden path="displayType"/>
                <form:hidden path="displayName"/>
                <form:hidden path="hardwareType"/>
                <form:hidden path="hardwareTypeEntryId"/>
                <form:hidden path="originalDeviceStatusEntryId"/>
                <c:if test="${not showTwoWay}">
                    <form:hidden path="deviceId"/>
                </c:if>
            
                <%-- DEVICE INFO --%>
                <tags:formElementContainer nameKey="deviceInfoSection">
                    
                    <tags:nameValueContainer2>
                    
                        <tags:nameValue2 nameKey="${displayTypeKey}">
                            <spring:escapeBody htmlEscape="true">${hardwareDto.displayType}</spring:escapeBody>
                        </tags:nameValue2>
                        
    					<c:if test="${showInstallCode}">
                                <tags:inputNameValue nameKey=".installCode" path="installCode" disabled="false"/>
                        </c:if>
                        
                        <%-- For switchs and tstat's, show serial number, otherwise device name --%>
                        <c:choose>
                            <c:when test="${showSerialNumber}">
                                <c:choose>
                                
                                    <c:when test="${serialNumberEditable}">
                                        <tags:inputNameValue nameKey=".serialNumber" path="serialNumber"></tags:inputNameValue>
                                    </c:when>
                                    
                                    <c:otherwise>
                                        <form:hidden path="serialNumber"/>
                                        <tags:nameValue2 nameKey=".serialNumber"><spring:escapeBody htmlEscape="true">${hardwareDto.serialNumber}</spring:escapeBody></tags:nameValue2>
                                    </c:otherwise>
                                    
                                </c:choose>
                            </c:when>
                            
                            <c:otherwise>
                                <cti:displayForPageEditModes modes="EDIT,VIEW">
                                    <tags:nameValue2 nameKey=".displayName"><spring:escapeBody htmlEscape="true">${hardwareDto.displayName}</spring:escapeBody></tags:nameValue2>
                                </cti:displayForPageEditModes>
                            </c:otherwise>
                            
                        </c:choose>
    					
    					<c:if test="${showMacAddress}">
                    		<tags:inputNameValue nameKey=".macAddress" path="macAddress"></tags:inputNameValue>                   
                        </c:if>
                        
                        <c:if test="${showFirmwareVersion}">
                        	<tags:inputNameValue nameKey=".firmwareVersion" path="firmwareVersion"></tags:inputNameValue>
                        </c:if>
                        
                        <tags:inputNameValue nameKey=".label" path="displayLabel"/>
                        
                        <tags:inputNameValue nameKey=".altTrackingNumber" path="altTrackingNumber"/>
                        
                        <c:if test="${showVoltage}">
                        	<tags:yukonListEntrySelectNameValue nameKey=".voltage" path="voltageEntryId" energyCompanyId="${energyCompanyId}" listName="DEVICE_VOLTAGE"/>
                        </c:if>
                        
                        <tags:nameValue2 nameKey=".fieldInstallDate">
                            <tags:dateInputCalendar fieldName="fieldInstallDate" fieldValue="fieldInstallDate" springInput="true"></tags:dateInputCalendar>
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".fieldReceiveDate">
                            <tags:dateInputCalendar fieldName="fieldReceiveDate" fieldValue="fieldReceiveDate" springInput="true"></tags:dateInputCalendar>
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".fieldRemoveDate">
                            <tags:dateInputCalendar fieldName="fieldRemoveDate" fieldValue="fieldRemoveDate" springInput="true"></tags:dateInputCalendar>
                        </tags:nameValue2>
                        
                        <tags:textareaNameValue nameKey=".deviceNotes" path="deviceNotes" rows="4" cols="35" rowClass="notesRow"/>
                        
                        <c:if test="${showGateway}">
                            
                            <tags:nameValue2 nameKey=".gateway">
                                <c:if test="${mode == 'VIEW' and hardwareDto.gatewayId != null}">
                                    <cti:url value="/spring/stars/operator/hardware/view" var="viewUrl">
                                        <cti:param name="inventoryId" value="${gatewayInventoryId}"/>
                                        <cti:param name="accountId" value="${accountId}"/>
                                    </cti:url>
                                    <a href="${viewUrl}">
                                </c:if>
                                
                                <tags:selectWithItems path="gatewayId" items="${gateways}" itemValue="paoId" itemLabel="name" 
                                        defaultItemValue="" defaultItemLabel="${none}"/>
                            </tags:nameValue2>
                            
                            <c:if test="${mode == 'VIEW' and hardwareDto.gatewayId != null}">
                                </a>
                            </c:if>
                        </c:if>
                        
                        <c:if test="${showRoute}">
                            <tags:selectNameValue nameKey=".route" path="routeId"  itemLabel="paoName" itemValue="yukonID" items="${routes}"  defaultItemValue="0" defaultItemLabel="${defaultRoute}"/>
                        </c:if>
                        
                        <tags:yukonListEntrySelectNameValue nameKey=".status" path="deviceStatusEntryId" energyCompanyId="${energyCompanyId}" listName="DEVICE_STATUS" defaultItemValue="0" defaultItemLabel="${none}"/>
                        
                        <c:if test="${showZigbeeState}">
                            <cti:displayForPageEditModes modes="VIEW">
                                <tags:nameValue2 nameKey=".zigbeeStatus" rowClass="pointState">
                                    <cti:pointStatusColor pointId="${hardwareDto.commissionedId}" >
                                        <cti:pointValue pointId="${hardwareDto.commissionedId}" format="VALUE"/>
                                    </cti:pointStatusColor>
                                </tags:nameValue2>
                                <tags:nameValue2 nameKey=".connectionStatus" rowClass="pointState">
                                    <cti:pointStatusColor pointId="${hardwareDto.connectStatusId}" >
                                        <cti:pointValue pointId="${hardwareDto.connectStatusId}" format="VALUE"/>
                                    </cti:pointStatusColor>
                                </tags:nameValue2>
                        </cti:displayForPageEditModes>
                        </c:if>
                        
                        <c:if test="${showTwoWay}">
                            <form:hidden path="creatingNewTwoWayDevice" id="creatingNewTwoWayDevice"/>
                            <%-- Two Way LCR's Yukon Device --%>
                            <tags:nameValue2 nameKey=".twoWayDevice" rowClass="pickerRow">

                                <div id="twoWayPickerContainer" <c:if test="${hardwareDto.creatingNewTwoWayDevice}">style="display: none;"</c:if>>
                                    
                                    <tags:pickerDialog type="twoWayLcrPicker" 
                                            id="twoWayLcrPicker" 
                                            linkType="selection" 
                                            immediateSelectMode="true"
                                            destinationFieldName="deviceId" 
                                            selectionProperty="paoName" 
                                            initialId="${hardwareDto.deviceId}" 
                                            viewOnlyMode="${mode == 'VIEW'}"/>
                                        
                                    <cti:displayForPageEditModes modes="CREATE,EDIT">
                                        <cti:button key="new" id="newButton"/>
                                    </cti:displayForPageEditModes><form:errors path="deviceId" cssClass="errorMessage"/>
                                    
                                </div>
                                
                                <div id="newTwoWayDeviceContainer" <c:if test="${!hardwareDto.creatingNewTwoWayDevice}">style="display: none;"</c:if>>
                                    <spring:bind path="twoWayDeviceName">
                                        <c:if test="${status.error}"><c:set var="inputToClass" value="error"/></c:if>
                                        <form:input path="twoWayDeviceName" id="twoWayDeviceName"  cssClass="${inputToClass}"/>
                                        <cti:button key="choose" id="chooseButton"/>
                                        <c:if test="${status.error}">
                                            <br>
                                            <form:errors path="twoWayDeviceName" cssClass="errorMessage"/>
                                        </c:if>
                                    </spring:bind>
                                </div>
                            </tags:nameValue2>
                        
                        </c:if>
                            
                    </tags:nameValueContainer2>
                
                </tags:formElementContainer>
                
                <%--SERVICE AND STORAGE --%>
                <tags:formElementContainer nameKey="serviceAndStorageSection">
                
                    <tags:nameValueContainer2>
                            
                        <cti:displayForPageEditModes modes="EDIT,CREATE">
                            <tags:selectNameValue nameKey=".serviceCompany" path="serviceCompanyId" itemLabel="serviceCompanyName" itemValue="serviceCompanyId" 
                                items="${serviceCompanies}" defaultItemValue="0" defaultItemLabel="(none)" onchange="updateServiceCompanyInfo()"/>
                        </cti:displayForPageEditModes>
                        
                        <tags:nameValue2 nameKey=".serviceCompanyInfo">
                            <div id="serviceCompanyContainer"></div>
                        </tags:nameValue2>
                        
                        <cti:displayForPageEditModes modes="EDIT,VIEW">
                            <tags:selectNameValue nameKey=".warehouse" path="warehouseId"  itemLabel="warehouseName" itemValue="warehouseID" items="${warehouses}"  defaultItemValue="0" defaultItemLabel="${noneSelectOption}"/>
                        </cti:displayForPageEditModes>
                        
                        <tags:textareaNameValue nameKey=".installNotes" path="installNotes" rows="4" cols="35"  rowClass="notesRow"/>
                        
                    </tags:nameValueContainer2>
                    
                </tags:formElementContainer>
                
                <cti:displayForPageEditModes modes="EDIT,CREATE">
                    <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
                        <cti:button key="save" type="submit" name="save"/>
                    </cti:checkRolesAndProperties>
                    <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
                        <cti:displayForPageEditModes modes="EDIT">
                            <cti:button key="deleteDevice" type="button" onclick="showDeletePopup()" dialogButton="true"/>
                        </cti:displayForPageEditModes>
                        <cti:displayForPageEditModes modes="CREATE,EDIT">
                            <cti:button key="cancel" type="submit" name="cancel"/>
                        </cti:displayForPageEditModes>
                    </cti:checkRolesAndProperties>
            </cti:displayForPageEditModes>
                
            </form:form>
        
        </cti:dataGridCell>
        
        <%-- RIGHT SIDE COLUMN --%>
        <cti:displayForPageEditModes modes="VIEW">
        
            <cti:dataGridCell>
            
                <%--DEVICE ACTIONS --%>
                <tags:formElementContainer nameKey="actions">
                    
                    <ul class="buttonStack">
                        
                        <%-- COMMON ACTIONS --%>
                        <c:if test="${showSwitchAndTstatConfigAction}">
                            <cti:url var="configUrl" value="/spring/stars/operator/hardware/config/edit">
                                <cti:param name="accountId" value="${accountId}"/>
                                <cti:param name="inventoryId" value="${inventoryId}"/>
                            </cti:url>
                            <li>
                                <cti:button key="editConfig" href="${configUrl}" renderMode="labeledImage"/>
                            </li>
                        </c:if>
                        
                        <%-- SWITCH ACTIONS --%>
                        <c:if test="${showSwitchChangeoutAction}">
                            <li>
                                <tags:pickerDialog extraArgs="${energyCompanyId}" 
                                        nameKey="changeOut"
                                        id="availableSwitchPicker" 
                                        type="availableSwitchPicker" 
                                        destinationFieldId="newInventoryId" 
                                        immediateSelectMode="true"
                                        endAction="function(items) { return changeOut(${inventoryId}, false); }" 
                                        linkType="button"
                                        buttonRenderMode="labeledImage"/>
                            </li>
                        </c:if>
                        
                        <%-- TSTAT ACTIONS --%>
                        <c:if test="${showCommissionTstatActions}">
                            <li>
                                <cti:url var="commissionTstatUrl" value="/spring/stars/operator/hardware/commissionTstat">
                                    <cti:param name="accountId" value="${accountId}"/>
                                    <cti:param name="inventoryId" value="${inventoryId}"/>
                                    <cti:param name="deviceId" value="${hardwareDto.deviceId}"/>
                                    <cti:param name="gatewayId" value="${hardwareDto.gatewayId}"/>
                                </cti:url>
                                <cti:button key="commissionTstat" href="${commissionTstatUrl}" renderMode="labeledImage" styleClass="f_blocker"/>
                            </li>
                            <li>
                                <cti:url var="decommissionTstatUrl" value="/spring/stars/operator/hardware/decommissionTstat">
                                    <cti:param name="accountId" value="${accountId}"/>
                                    <cti:param name="inventoryId" value="${inventoryId}"/>
                                    <cti:param name="deviceId" value="${hardwareDto.deviceId}"/>
                                    <cti:param name="gatewayId" value="${hardwareDto.gatewayId}"/>
                                </cti:url>
                                <cti:button key="decommissionTstat" href="${decommissionTstatUrl}" renderMode="labeledImage" styleClass="f_blocker"/>
                            </li>
                        </c:if>
                        
                        <c:if test="${showDisabledCommissionActions}">
                            <li>
                                <cti:button key="commissionTstat.disabled" disabled="true" renderMode="labeledImage"/>
                            </li>
                            <li>
                                <cti:button key="decommissionTstat.disabled" disabled="true" renderMode="labeledImage"/>
                            </li>
                        </c:if>
                        
                        <c:if test="${showTstatChangeoutAction}">
                            <li>
                                <tags:pickerDialog extraArgs="${energyCompanyId}"
                                        nameKey="changeOut" 
                                        id="availableThermostatPicker" 
                                        type="availableThermostatPicker" 
                                        destinationFieldId="newInventoryId" 
                                        immediateSelectMode="true"
                                        endAction="function(items) { return changeOut(${inventoryId}, false); }" 
                                        linkType="button"
                                        buttonRenderMode="labeledImage"/>
                            </li>
                        </c:if>
                        
                        <c:if test="${showScheduleActions}">
                            <cti:url var="editScheduleUrl" value="/spring/stars/operator/thermostatSchedule/view">
                                <cti:param name="accountId" value="${accountId}"/>
                                <cti:param name="thermostatIds" value="${inventoryId}"/>
                            </cti:url>
                            <cti:url var="savedSchedulesUrl" value="/spring/stars/operator/thermostatSchedule/savedSchedules">
                                <cti:param name="accountId" value="${accountId}"/>
                                <cti:param name="thermostatId" value="${inventoryId}"/>
                            </cti:url>
                            <li>
                                <cti:button key="editSchedule" href="${editScheduleUrl}" renderMode="labeledImage"/>
                            </li>
                            <li>
                                <cti:button key="savedSchedules" href="${savedSchedulesUrl}" renderMode="labeledImage"/>
                            </li>
                        </c:if>
                        
                        <c:if test="${showManualAction}">
                            <cti:url var="editManualUrl" value="/spring/stars/operator/thermostatManual/view">
                                <cti:param name="accountId" value="${accountId}"/>
                                <cti:param name="thermostatIds" value="${inventoryId}"/>
                            </cti:url>
                            <li>
                                <cti:button key="manual" href="${editManualUrl}" renderMode="labeledImage"/>
                            </li>
                        </c:if>
                        
                        <%-- METER ACTIONS --%>
                        <c:if test="${showMeterConfigAction}">
                            <cti:url var="configUrl" value="/spring/stars/operator/hardware/config/meterConfig">
                                <cti:param name="accountId" value="${accountId}"/>
                                <cti:param name="meterId" value="${hardwareDto.deviceId}"/>
                            </cti:url>
                            <li>
                                <cti:button key="editConfig" href="${configUrl}" renderMode="labeledImage"/>
                            </li>
                        </c:if>
                        
                        <c:if test="${showMeterDetailAction}">
                            <cti:paoDetailUrl yukonPao="${hardwareDto.yukonPao}" var="meterDetailUrl"/>
                            <li>
                                 <cti:button key="meterDetail" href="${meterDetailUrl}" renderMode="labeledImage"/>
                            </li>
                        </c:if>
                        
                        <c:if test="${showMeterChangeoutAction}">
                            <li>
                                <tags:pickerDialog extraArgs="${energyCompanyId}" 
                                        nameKey="changeOut"
                                        id="availableMeterPicker" 
                                        type="availableMctPicker" 
                                        destinationFieldId="newInventoryId" 
                                        immediateSelectMode="true" 
                                        endAction="function(items) { return changeOut(${inventoryId}, true); }" 
                                        linkType="button"
                                        buttonRenderMode="labeledImage"/>
                            </li>
                        </c:if>
                        
                        <%-- GATEWAY ACTIONS --%>
                        <c:if test="${showCommissionGatewayActions}">
                            <li>
                                <cti:url var="commissionGatewayUrl" value="/spring/stars/operator/hardware/commissionGateway">
                                    <cti:param name="accountId" value="${accountId}"/>
                                    <cti:param name="inventoryId" value="${inventoryId}"/>
                                    <cti:param name="gatewayId" value="${hardwareDto.deviceId}"/>
                                </cti:url>
                                <cti:button key="commissionGateway" href="${commissionGatewayUrl}" renderMode="labeledImage" styleClass="f_blocker"/>
                            </li>
                            <li>
                                <cti:url var="decommissionGatewayUrl" value="/spring/stars/operator/hardware/decommissionGateway">
                                    <cti:param name="accountId" value="${accountId}"/>
                                    <cti:param name="inventoryId" value="${inventoryId}"/>
                                    <cti:param name="gatewayId" value="${hardwareDto.deviceId}"/>
                                </cti:url>
                                <cti:button key="decommissionGateway" href="${decommissionGatewayUrl}" renderMode="labeledImage" styleClass="f_blocker"/>
                            </li>
                        </c:if>
                        
                        <c:if test="${showTextMessageAction}">
                            <li>
                                <cti:button key="textMessage" id="sendTextMsg" renderMode="labeledImage" dialogButton="true"/>
                            </li>
                        </c:if>
                        
                        <c:if test="${showGatewayChangeOutAction}">
                            <li>
                                <tags:pickerDialog extraArgs="${energyCompanyId}" 
                                        nameKey="changeOut"
                                        id="availableGatewayPicker" 
                                        type="availableGatewayPicker" 
                                        destinationFieldId="newInventoryId" 
                                        immediateSelectMode="true" 
                                        endAction="function(items) { return changeOut(${inventoryId}, false); }" 
                                        linkType="button"
                                        buttonRenderMode="labeledImage"/>
                            </li>
                        </c:if>
                        
                    </ul>
                    
                </tags:formElementContainer>
                
                <c:if test="${showAssignedDevices}">
                    <tags:boxContainer2 nameKey="assignedDevices">
                        <div class="historyContainer">
                            <c:choose>
                                <c:when test="${not empty assignedDevices}">
                                    <table class="compactResultsTable">
                                        <tr>
                                            <th class="nonwrapping"><i:inline key=".serialNumber"/></th>
                                            <th class="nonwrapping"><i:inline key=".zigbeeStatus"/></th>
                                            <th class="nonwrapping"><i:inline key=".actions"/></th>
                                        </tr>
                                        <c:forEach var="device" items="${assignedDevices}">
                                            <tr>
                                                <cti:url value="/spring/stars/operator/hardware/view" var="viewUrl">
                                                    <cti:param name="accountId" value="${accountId}"/>
                                                    <cti:param name="inventoryId" value="${device.inventoryIdentifier.inventoryId}"/>
                                                </cti:url>
                                                <td><a href="${viewUrl}">${device.serialNumber}</a></td>
                                                <td class="pointStateColumn">
                                                    <cti:pointStatusColor pointId="${device.commissionId}">
                                                        <cti:pointValue pointId="${device.commissionId}" format="VALUE"/>
                                                    </cti:pointStatusColor>
                                                </td>
                                                <td class="nonwrapping">
                                                    <cti:url var="commissionTstatUrl" value="/spring/stars/operator/hardware/commissionTstat">
                                                        <cti:param name="accountId" value="${accountId}"/>
                                                        <cti:param name="inventoryId" value="${inventoryId}"/>
                                                        <cti:param name="deviceId" value="${device.deviceId}"/>
                                                        <cti:param name="gatewayId" value="${hardwareDto.deviceId}"/>
                                                    </cti:url>
                                                    <cti:button key="commissionTstat" href="${commissionTstatUrl}" renderMode="image" styleClass="f_blocker"/>
                                                    <cti:url var="decommissionTstatUrl" value="/spring/stars/operator/hardware/decommissionTstat">
                                                        <cti:param name="accountId" value="${accountId}"/>
                                                        <cti:param name="inventoryId" value="${inventoryId}"/>
                                                        <cti:param name="deviceId" value="${device.deviceId}"/>
                                                        <cti:param name="gatewayId" value="${hardwareDto.deviceId}"/>
                                                    </cti:url>
                                                    <cti:button key="decommissionTstat" href="${decommissionTstatUrl}" renderMode="image" styleClass="f_blocker"/>
                                                    <cti:url value="/spring/stars/operator/hardware/removeDeviceFromGateway" var="removeUrl">
                                                        <cti:param name="accountId" value="${accountId}"/>
                                                        <cti:param name="inventoryId" value="${inventoryId}"/>
                                                        <cti:param name="gatewayId" value="${hardwareDto.deviceId}"/>
                                                        <cti:param name="deviceId" value="${device.deviceId}"/>
                                                    </cti:url>
                                                    <cti:button key="remove" href="${removeUrl}" renderMode="image" styleClass="f_blocker"/>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </table>
                                </c:when>
                                <c:otherwise>
                                    <i:inline key=".noAssignedDevices"/>
                                </c:otherwise>
                            </c:choose>
                        </div>
                            
                        <c:if test="${not empty availableDevices}">
                            <div class="actionArea">
                                <form action="/spring/stars/operator/hardware/addDeviceToGateway" method="post">
                                    <input type="hidden" name="accountId" value="${accountId}">
                                    <input type="hidden" name="inventoryId" value="${inventoryId}">
                                    <input type="hidden" name="gatewayId" value="${hardwareDto.deviceId}">
                                    <select name="deviceId">
                                        <c:forEach var="device" items="${availableDevices}">
                                            <option value="${device.deviceId}">${device.serialNumber}</option>
                                        </c:forEach>
                                    </select>
                                    <cti:button key="add" type="submit" styleClass="f_blocker"/>
                                </form>
                            </div>
                        </c:if>
                    </tags:boxContainer2>
                </c:if>
                
                <br>
                    
                <tags:boxContainer2 nameKey="deviceStatusHistory">
                    <c:choose>
                        <c:when test="${empty deviceStatusHistory}">
                            <i:inline key=".deviceStatusHistory.none"/>
                        </c:when>
                        <c:otherwise>
                            <div class="historyContainer">
                                <table class="compactResultsTable">
                                    <tr>
                                        <th class="nonwrapping"><i:inline key=".deviceStatusHistory.event"/></th>
                                        <th class="nonwrapping"><i:inline key=".deviceStatusHistory.userName"/></th>
                                        <th class="nonwrapping"><i:inline key=".deviceStatusHistory.timeOfEvent"/></th>
                                    </tr>
                                    <c:forEach var="event" items="${deviceStatusHistory}">
                                        <tr class="<tags:alternateRow odd="" even="altRow"/>">
                                            <td class="nonwrapping"><spring:escapeBody htmlEscape="true">${event.actionText}</spring:escapeBody></td>
                                            <td class="nonwrapping"><spring:escapeBody htmlEscape="true">${event.userName}</spring:escapeBody></td>
                                            <td class="nonwrapping lastColumn"><cti:formatDate value="${event.eventBase.eventTimestamp}" type="BOTH"/></td>
                                        </tr>
                                    </c:forEach>
                                </table>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </tags:boxContainer2>
                    
                <br>
                
                <tags:boxContainer2 nameKey="hardwareHistory">
                    <c:choose>
                        <c:when test="${empty hardwareHistory}">
                            <i:inline key=".hardwareHistory.none"/>
                        </c:when>
                        <c:otherwise>
                            <div class="historyContainer">
                                <tags:alternateRowReset/>
                                <table class="compactResultsTable">
                                    <tr>
                                        <th class="nonwrapping"><i:inline key=".hardwareHistory.date"/></th>
                                        <th class="nonwrapping"><i:inline key=".hardwareHistory.action"/></th>
                                    </tr>
                                    
                                    <c:forEach var="event" items="${hardwareHistory}">
                                        <tr class="<tags:alternateRow odd="" even="altRow"/>">
                                            <td class="nonwrapping"><cti:formatDate value="${event.date}" type="BOTH"/></td>
                                            <td class="nonwrapping lastColumn"><spring:escapeBody htmlEscape="true">${event.action}</spring:escapeBody></td>
                                        </tr>
                                    </c:forEach>
                                    
                                </table>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </tags:boxContainer2>
            
            </cti:dataGridCell>
            
        </cti:displayForPageEditModes>
        
    </cti:dataGrid>
        
    <cti:displayForPageEditModes modes="VIEW">
            <cti:url value="/spring/stars/operator/hardware/edit" var="editUrl">
                <cti:param name="accountId" value="${accountId}"/>
                <cti:param name="inventoryId" value="${inventoryId}"/>
            </cti:url>
            <cti:button key="edit" href="${editUrl}"/>
        </cti:displayForPageEditModes>
    
</cti:standardPage>