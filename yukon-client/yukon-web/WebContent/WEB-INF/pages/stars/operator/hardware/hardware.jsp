<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="operator" page="hardware.${mode}">

<tags:setFormEditMode mode="${mode}"/>
<cti:msg2 key=".noneSelectOption" var="noneSelectOption"/>

<cti:includeCss link="/WebConfig/yukon/styles/operator/hardware.css"/>
<cti:includeScript link="/JavaScript/calendarTagFuncs.js"/>

<script type="text/javascript">
jQuery(function(){
    
    jQuery(document).delegate('#refresh, button[name=commissionSubmit], button[name=decommissionSubmit]', 'click', function(event) {
        var url = '/spring/stars/operator/hardware/zb/';
        var button = event.currentTarget;
        if (button.id == 'refresh') {
            url += 'refresh';
        } else if (button.name == 'commissionSubmit') {
            url += 'commission';
            jQuery('#confirmCommissionPopup').hide();
        } else if (button.name == 'decommissionSubmit') {
            url += 'decommission';
        }
        
        var elementToBlock = event.currentTarget;
        Yukon.ui.block(elementToBlock);
        
        jQuery.ajax({
            url: url,
            method: 'GET',
            data: {'deviceId': '${hardware.deviceId}'},
            success: function(data) {
                jQuery('#zbCommandStatus').html(data.message);
                jQuery('#zbCommandStatus').show();
                if (data.success ==  true) {
                    jQuery('#zbCommandStatus').addClass('successMessage').removeClass('errorMessage');
                 } else {
                     jQuery('#zbCommandStatus').removeClass('successMessage').addClass('errorMessage');
                 }
                Yukon.ui.unblock(elementToBlock);
             },
             failure: function(){
                 Yukon.ui.unblock(elementToBlock);
             }
        });
    });
    
    jQuery(document).delegate('button[name^=assignedDevicesCommissionSubmit_], button[name^=assignedDevicesDecommissionSubmit_]', 'click', function(event) {
        var url = '/spring/stars/operator/hardware/zb/';
        var button = event.currentTarget;
        
        var name = button.name.split('_')[0];
        var deviceId = button.name.split('_')[1];
        if (name == 'assignedDevicesCommissionSubmit') {
            url += 'commission';
            jQuery('#confirmCommissionPopup_' + deviceId).hide();
        } else if (name == 'assignedDevicesDecommissionSubmit') {
            url += 'decommission';
        }
        
        var elementToBlock = event.currentTarget;
        Yukon.ui.block(elementToBlock);
        
        jQuery.ajax({
            url: url,
            method: 'GET',
            data: {'deviceId': deviceId},
            success: function(data) {
                jQuery('#zbAssignedStatus').html(data.message);
                jQuery('#zbAssignedStatus').show();
                if (data.success ==  true) {
                    jQuery('#zbAssignedStatus').addClass('successMessage').removeClass('errorMessage');
                } else {
                    jQuery('#zbAssignedStatus').removeClass('successMessage').addClass('errorMessage');
                }
                Yukon.ui.unblock(elementToBlock);
            },
            failure: function() {
                Yukon.ui.unblock(elementToBlock);
            }
        });
    });
    
    <cti:displayForPageEditModes modes="VIEW">
    jQuery(document).delegate('#sendTextMsg', 'click', function(event) {
        var params = {'accountId' : ${accountId}, 
                      'inventoryId' : ${inventoryId}, 
                      'gatewayId' : ${hardware.deviceId}};
        openSimpleDialog('ajaxDialog', 'zb/showTextMessage', null, params);
    });
    </cti:displayForPageEditModes>
});


function showDeletePopup() {
    $('deleteHardwarePopup').show();
}

function hideDeletePopup() {
	$('deleteHardwarePopup').hide();
}

function changeOut(oldId, isMeter) {
    $('oldInventoryId').value = oldId;

    if (isMeter) {
        $('isMeter').value = 'true';
    } else {
        $('isMeter').value = 'false';
    }
    
    var form = $('changeOutForm');
    form.submit();
    return true;
}

function getCommissionConfirmationCallback() {
    return function(data) {
        var commissionedValue = data.get('value');
        
        if (${!hardware.hardwareType.gateway}) {
            if (commissionedValue == 'Decommissioned') {
                //decommissioned
                $('decommissionedConfirmMsg').show();
                $('commissionedConfirmMsg').hide();
            } else {
                //commissioned - either connected or disconnected
                $('decommissionedConfirmMsg').hide();
                $('commissionedConfirmMsg').show();
            }
        }
    };
}

function getEndpointCommissionConfirmationCallback(deviceId) {
    return function(data) {
        var commissionedValue = data.get('value');
        
        if (commissionedValue == 'Decommissioned') {
            //decommissioned
            $('decommissionedConfirmMsg_' + deviceId).show();
            $('commissionedConfirmMsg_' + deviceId).hide();
        } else {
            //commissioned - either connected or disconnected
            $('decommissionedConfirmMsg_' + deviceId).hide();
            $('commissionedConfirmMsg_' + deviceId).show();
        }
    };
}
</script>

     <!-- Text Message Popup -->
    <cti:msg2 var="textMsgTitle" key=".sendTextMsg"/>
    <tags:simpleDialog id="ajaxDialog" styleClass="smallSimplePopup" title="${textMsgTitle}"/>

    <!-- Changeout Form -->
    <cti:displayForPageEditModes modes="VIEW">
        <form id="changeOutForm" action="/spring/stars/operator/hardware/changeOut">
            <input type="hidden" name="accountId" value="${accountId}">
            <input type="hidden" name="newInventoryId" id="newInventoryId">
            <input type="hidden" name="oldInventoryId" id="oldInventoryId">
            <input type="hidden" name="isMeter" id="isMeter">
            <input type="hidden" name="redirect" value="view">
        </form>
    </cti:displayForPageEditModes>
    
    <!-- Delete Hardware Popup -->
    <i:simplePopup styleClass="mediumSimplePopup" titleKey=".deleteDevice" id="deleteHardwarePopup" arguments="${hardware.displayName}">
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
            <cti:msg2 key=".deleteMessage${deleteMsgKeySuffix}" argument="${hardware.displayName}"/>
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
                            <cti:button nameKey="delete" type="submit" name="delete" styleClass="f_blocker" />
                        </cti:checkRolesAndProperties>
                        <cti:button nameKey="cancel" onclick="hideDeletePopup()"/>
                    </td>
                </tr>
            </table>
        </form>
    </i:simplePopup>
    
    <cti:displayForPageEditModes modes="EDIT">
        <cti:url value="update" var="action"/>
    </cti:displayForPageEditModes>
    <cti:displayForPageEditModes modes="CREATE">
        <cti:url value="create" var="action"/>
    </cti:displayForPageEditModes>
    
    <cti:dataGrid cols="2" rowStyle="vertical-align:top;" cellStyle="padding-right:20px;">
    
        <%-- LEFT SIDE COLUMN --%>
        <%-- COMMON HARDWARE INFO --%>
        <%@ include file="hardwareInfo.jspf" %>
        
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
                                <cti:button nameKey="editConfig" href="${configUrl}" renderMode="labeledImage"/>
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
                            <cti:url var="savedSchedulesUrl" value="/spring/stars/operator/thermostatSchedule/savedSchedules">
                                <cti:param name="accountId" value="${accountId}"/>
                                <cti:param name="thermostatIds" value="${inventoryId}"/>
                            </cti:url>
                            <li>
                                <cti:button nameKey="savedSchedules" href="${savedSchedulesUrl}" renderMode="labeledImage"/>
                            </li>
                        </c:if>
                        
                        <c:if test="${showManualAction}">
                            <cti:url var="editManualUrl" value="/spring/stars/operator/thermostatManual/view">
                                <cti:param name="accountId" value="${accountId}"/>
                                <cti:param name="thermostatIds" value="${inventoryId}"/>
                            </cti:url>
                            <li>
                                <cti:button nameKey="manual" href="${editManualUrl}" renderMode="labeledImage"/>
                            </li>
                        </c:if>
                        
                        <c:if test="${showThermostatHistoryAction}">
                            <cti:url var="historyUrl" value="/spring/stars/operator/thermostat/history/view">
                                <cti:param name="accountId" value="${accountId}" />
                                <cti:param name="thermostatIds" value="${inventoryId}"/>
                            </cti:url>
                            <li>
                                <cti:button nameKey="history" href="${historyUrl}" renderMode="labeledImage" />
                            </li>
                        </c:if>
                        
                        <%-- METER ACTIONS --%>
                        <c:if test="${showMeterConfigAction}">
                            <cti:url var="configUrl" value="/spring/stars/operator/hardware/config/meterConfig">
                                <cti:param name="accountId" value="${accountId}"/>
                                <cti:param name="meterId" value="${hardware.deviceId}"/>
                            </cti:url>
                            <li>
                                <cti:button nameKey="editConfig" href="${configUrl}" renderMode="labeledImage"/>
                            </li>
                        </c:if>
                        
                        <c:if test="${showMeterDetailAction}">
                            <cti:paoDetailUrl yukonPao="${hardware.yukonPao}" var="meterDetailUrl"/>
                            <li>
                                 <cti:button nameKey="meterDetail" href="${meterDetailUrl}" renderMode="labeledImage"/>
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
                        
                        <c:if test="${showTextMessageAction}">
                            <li>
                                <cti:button nameKey="textMessage" id="sendTextMsg" renderMode="labeledImage" dialogButton="true"/>
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
                
                <c:if test="${showZigbeeState}">
                    <cti:displayForPageEditModes modes="VIEW">
                        <tags:boxContainer2 nameKey="zigbeeStatus" styleClass="statusContainer f_block_this" id="zigbeeStatus">
                            <table class="compactResultsTable">
                                <tr>
                                    <th><i:inline key=".status"/></th>
                                    <th><i:inline key=".timestamp"/></th>
                                </tr>
                                <tr>
                                    <td>
                                        <cti:pointStatusColor pointId="${hardware.commissionedId}" >
                                            <span class="fwb">
                                                <cti:pointValue pointId="${hardware.commissionedId}" format="VALUE"/>
                                            </span>
                                        </cti:pointStatusColor>
                                    </td>
                                    <td>
                                        <cti:pointValue pointId="${hardware.commissionedId}" format="DATE"/>
                                    </td>
                                </tr>
                            </table>
                            <div id="zbCommandStatus" class="dn errorMessage zbCommandMsg"></div>
                            <div class="pageActionArea">
                                <c:choose>
                                    <c:when test="${showDisabledRefresh}">
                                        <cti:button nameKey="refreshDisabled" disabled="true"/>
                                    </c:when>
                                    <c:otherwise>
                                        <cti:button id="refresh" nameKey="refresh"/>
                                    </c:otherwise>
                                </c:choose>
                                
                                <c:if test="${showCommissionActions}">
                                    <cti:button nameKey="commission" id="commission"/>
                                    <cti:button nameKey="decommission" id="decommission"/>
                                    
                                    <cti:msgScope paths=".commissionConfirmation,">
                                        <i:simplePopup id="confirmCommissionPopup" styleClass="commissionConfirmationMsg smallSimplePopup" titleKey=".title" on="#commission">
                                            <p>
                                                <div id="decommissionedConfirmMsg">
                                                    <i:inline key=".message"/>
                                                </div>
                                                <div id="commissionedConfirmMsg" class="errorMessage" style="display:none">
                                                    <i:inline key=".warnMessage"/>
                                                </div>
                                            <p>
                                            <div class="actionArea">
                                                <cti:button nameKey="ok" name="commissionSubmit" type="submit" />
                                                <cti:button nameKey="cancel" onclick="$('confirmCommissionPopup').hide()" />
                                            </div>
                                        </i:simplePopup>
                                    </cti:msgScope>
                                    <cti:dataUpdaterCallback function="getCommissionConfirmationCallback()" initialize="true" value="POINT/${hardware.commissionedId}/VALUE"/>

                                    <tags:confirmDialog submitName="decommissionSubmit"
                                        nameKey=".decommissionConfirmation"
                                        styleClass="commissionConfirmationMsg smallSimplePopup" on="#decommission"
                                        endAction="hide" />
                                </c:if>
                                
                                <c:if test="${showDisabledCommissionActions}">
                                    <cti:button nameKey="commission.disabled" disabled="true"/>
                                    <cti:button nameKey="decommission.disabled" disabled="true"/>
                                </c:if>
                            </div>
                        </tags:boxContainer2>
                        <br>
                    </cti:displayForPageEditModes>
                </c:if>
                
                <c:if test="${showAssignedDevices}">
                    <tags:boxContainer2 nameKey="assignedDevices" id="assignedDevices" styleClass="f_block_this">
                        <div class="historyContainer">
                            <c:choose>
                                <c:when test="${not empty assignedDevices}">
                                    <table class="compactResultsTable">
                                        <tr>
                                            <th class="nonwrapping"><i:inline key=".serialNumber"/></th>
                                            <th class="nonwrapping"><i:inline key=".status"/></th>
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
                                                    <cti:button nameKey="assignedDevices.commission" renderMode="image" styleClass="assignedDevicesCommission" id="assignedDevicesCommission_${device.deviceId}"/>
                                                    <cti:button nameKey="assignedDevices.decommission" renderMode="image" styleClass="assignedDevicesDecommission" id="assignedDevicesDecommission_${device.deviceId}"/>
                                                    <cti:msgScope paths=".commissionConfirmation,">
                                                        <i:simplePopup id="confirmCommissionPopup_${device.deviceId}" styleClass="commissionConfirmationMsg smallSimplePopup" titleKey=".title" on="#assignedDevicesCommission_${device.deviceId}">
                                                            <p>
                                                                <div id="decommissionedConfirmMsg_${device.deviceId}">
                                                                    <i:inline key=".message"/>
                                                                </div>
                                                                <div id="commissionedConfirmMsg_${device.deviceId}" class="errorMessage" style="display:none">
                                                                    <i:inline key=".warnMessage"/>
                                                                </div>
                                                            <p>
                                                            <div class="actionArea">
                                                                <cti:button name="assignedDevicesCommissionSubmit_${device.deviceId}" nameKey="ok" type="submit" />
                                                                <cti:button nameKey="cancel" onclick="$('confirmCommissionPopup_${device.deviceId}').hide()" />
                                                            </div>
                                                        </i:simplePopup>
                                                    </cti:msgScope>
                                                    <cti:dataUpdaterCallback function="getEndpointCommissionConfirmationCallback('${device.deviceId}')" initialize="true" value="POINT/${device.commissionId}/VALUE"/>
                                                    <tags:confirmDialog submitName="assignedDevicesDecommissionSubmit_${device.deviceId}"
                                                        nameKey=".decommissionConfirmation" 
                                                        styleClass="commissionConfirmationMsg smallSimplePopup" on="#assignedDevicesDecommission_${device.deviceId}"
                                                        endAction="hide" />
                                                    
                                                    <cti:url value="/spring/stars/operator/hardware/zb/removeDeviceFromGateway" var="removeUrl">
                                                        <cti:param name="accountId" value="${accountId}"/>
                                                        <cti:param name="inventoryId" value="${inventoryId}"/>
                                                        <cti:param name="gatewayId" value="${hardware.deviceId}"/>
                                                        <cti:param name="deviceId" value="${device.deviceId}"/>
                                                    </cti:url>
                                                    <cti:button nameKey="remove" href="${removeUrl}" renderMode="image" styleClass="f_blocker"/>
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
                        
                        <div id="zbAssignedStatus" class="dn zbCommandMsg"></div>
                        
                        <c:if test="${not empty availableDevices}">
                            <div class="actionArea">
                                <form action="/spring/stars/operator/hardware/zb/addDeviceToGateway" method="post">
                                    <input type="hidden" name="accountId" value="${accountId}">
                                    <input type="hidden" name="inventoryId" value="${inventoryId}">
                                    <input type="hidden" name="gatewayId" value="${hardware.deviceId}">
                                    <select name="deviceId">
                                        <c:forEach var="device" items="${availableDevices}">
                                            <option value="${device.deviceId}">${device.serialNumber}</option>
                                        </c:forEach>
                                    </select>
                                    <cti:button nameKey="add" type="submit" styleClass="f_blocker"/>
                                </form>
                            </div>
                        </c:if>
                    </tags:boxContainer2>
                </c:if>
                
                <br>
                    
                <%-- COMMON HARDWARE HISTORY --%>
                <%@ include file="hardwareHistory.jspf" %>
            
            </cti:dataGridCell>
            
        </cti:displayForPageEditModes>
        
    </cti:dataGrid>
        
    <cti:displayForPageEditModes modes="VIEW">
        <cti:checkRolesAndProperties value="${editingRoleProperty}">
            <cti:url value="/spring/stars/operator/hardware/edit" var="editUrl">
                <cti:param name="accountId" value="${accountId}"/>
                <cti:param name="inventoryId" value="${inventoryId}"/>
            </cti:url>
            <cti:button nameKey="edit" href="${editUrl}"/>
        </cti:checkRolesAndProperties>
    </cti:displayForPageEditModes>
</cti:standardPage>