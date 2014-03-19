<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="operator" page="hardware.${mode}">

<div id="ajaxDialog"></div>

<tags:setFormEditMode mode="${mode}"/>
<cti:msg2 key=".noneSelectOption" var="noneSelectOption"/>


<script type="text/javascript">
$(function(){

    $(document).on('click', '#refresh, button[name=commissionSubmit], button[name=decommissionSubmit]', function(event) {
        var url = '/stars/operator/hardware/zb/',
            button = event.currentTarget;
        if (button.id === 'refresh') {
            url += 'refresh';
        } else if (button.name === 'commissionSubmit') {
            url += 'commission';
            $('#confirmCommissionPopup').dialog('close');
        } else if (button.name === 'decommissionSubmit') {
            url += 'decommission';
        }

        yukon.ui.block(event);

        $.ajax({
            type: 'GET',
            url: url,
            data: {'deviceId': '${hardware.deviceId}'}
        }).done( function (data, textStatus, jqXHR) {
            $('#zbCommandStatus').html(data.message);
            $('#zbCommandStatus').show();
            if (data.success ===  true) {
                $('#zbCommandStatus').addClass('success').removeClass('error');
            } else {
                $('#zbCommandStatus').removeClass('success').addClass('error');
            }
            yukon.ui.unblock(event);
        }).fail( function (jqXHR, textStatus, errorThrown) {
            yukon.ui.unblock(event);
        });
    });

    $(document).on('click', 'button[name^=assignedDevicesCommissionSubmit_], button[name^=assignedDevicesDecommissionSubmit_]', function(event) {
        var url = '/stars/operator/hardware/zb/',
            button = event.currentTarget,
            name,
            deviceId;

        name = button.name.split('_')[0];
        deviceId = button.name.split('_')[1];
        if (name === 'assignedDevicesCommissionSubmit') {
            url += 'commission';
            $('#confirmCommissionPopup_' + deviceId).dialog('close');
        } else if (name === 'assignedDevicesDecommissionSubmit') {
            url += 'decommission';
        }

        yukon.ui.block(event);

        $.ajax({
            type: 'GET',
            url: url,
            data: {'deviceId': deviceId}
        }).done( function (data, textStatus, jqXHR) {
            $('#zbAssignedStatus').html(data.message);
            $('#zbAssignedStatus').show();
            if (data.success ===  true) {
                $('#zbAssignedStatus').addClass('success').removeClass('error');
            } else {
                $('#zbAssignedStatus').removeClass('success').addClass('error');
            }
            yukon.ui.unblock(event);
        }).fail( function (jqXHR, textStatus, errorThrown) {
            yukon.ui.unblock(event);
        });
    });

    <cti:displayForPageEditModes modes="VIEW">
    $(document).on('click', '#sendTextMsg', function(event) {
        var params = {'accountId' : ${accountId}, 
                      'inventoryId' : ${inventoryId}, 
                      'gatewayId' : ${hardware.deviceId}};
        openSimpleDialog('textMsgDialog', 'zb/showTextMessage', null, params);
    });
    </cti:displayForPageEditModes>
});

function showDeletePopup() {
    $('#deleteHardwarePopup').dialog('open');
}

function hideDeletePopup() {
    $('#deleteHardwarePopup').dialog('close');
}

function changeOut(oldId, isMeter) {
    var form;

    $('#oldInventoryId').val(oldId);

    if (isMeter) {
        $('#isMeter').val('true');
    } else {
        $('#isMeter').val('false');
    }
    
    form = $('#changeOutForm');
    form.submit();
    return true;
}

function getCommissionConfirmationCallback() {
    return function(data) {
        var commissionedValue = data.value;
        
        if (${!hardware.hardwareType.gateway}) {
            if (commissionedValue === 'Decommissioned') {
                //decommissioned
                $('#decommissionedConfirmMsg').show();
                $('#commissionedConfirmMsg').hide();
            } else {
                //commissioned - either connected or disconnected
                $('#decommissionedConfirmMsg').hide();
                $('#commissionedConfirmMsg').show();
            }
        }
    };
}

function getEndpointCommissionConfirmationCallback(deviceId) {
    return function(data) {
        var commissionedValue = data.value;

        if (commissionedValue === 'Decommissioned') {
            //decommissioned
            $('#decommissionedConfirmMsg_' + deviceId).show();
            $('#commissionedConfirmMsg_' + deviceId).hide();
        } else {
            //commissioned - either connected or disconnected
            $('#decommissionedConfirmMsg_' + deviceId).hide();
            $('#commissionedConfirmMsg_' + deviceId).show();
        }
    };
}
</script>

     <!-- Text Message Popup -->
    <cti:msg2 var="textMsgTitle" key=".sendTextMsg"/>
    <tags:simpleDialog id="textMsgDialog" title="${textMsgTitle}"/>

    <!-- Changeout Form -->
    <cti:displayForPageEditModes modes="VIEW">
        <form id="changeOutForm" action="/stars/operator/hardware/changeOut">
            <input type="hidden" name="accountId" value="${accountId}">
            <input type="hidden" name="newInventoryId" id="newInventoryId">
            <input type="hidden" name="oldInventoryId" id="oldInventoryId">
            <input type="hidden" name="isMeter" id="isMeter">
            <input type="hidden" name="redirect" value="view">
        </form>
    </cti:displayForPageEditModes>
    
    <!-- Delete Hardware Popup -->
    <i:simplePopup titleKey=".deleteDevice" id="deleteHardwarePopup" arguments="${hardware.displayName}">
        <form id="deleteForm" action="/stars/operator/hardware/delete" method="post">
            <cti:csrfToken/>
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
            
            <div class="action-area">
                <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
                    <cti:button nameKey="delete" type="submit" name="delete" classes="f-blocker action primary"/>
                </cti:checkRolesAndProperties>
                <cti:button nameKey="cancel" onclick="hideDeletePopup()"/>
            </div>
        </form>
    </i:simplePopup>
    
    <cti:displayForPageEditModes modes="EDIT">
        <cti:url value="update" var="action"/>
    </cti:displayForPageEditModes>
    <cti:displayForPageEditModes modes="CREATE">
        <cti:url value="create" var="action"/>
    </cti:displayForPageEditModes>
    
    <div class="column-12-12 clearfix">
    
        <%-- COMMON HARDWARE INFO --%>
        <div class="column one">
            <%@ include file="hardwareInfo.jspf" %>
        </div>
        <%-- RIGHT SIDE COLUMN --%>
        <cti:displayForPageEditModes modes="VIEW">
        
            <div class="column two nogutter">
            
                <%--DEVICE ACTIONS --%>
                <tags:sectionContainer2 nameKey="actions" styleClass="stacked">
                    
                    <ul class="button-stack">
                        
                        <%-- COMMON ACTIONS --%>
                        <c:if test="${showSwitchAndTstatConfigAction}">
                            <cti:url var="configUrl" value="/stars/operator/hardware/config/edit">
                                <cti:param name="accountId" value="${accountId}"/>
                                <cti:param name="inventoryId" value="${inventoryId}"/>
                            </cti:url>
                            <li>
                                <cti:button nameKey="editConfig" href="${configUrl}" renderMode="labeledImage" icon="icon-cog-edit"/>
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
                                        icon="icon-arrow-swap"
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
                                        icon="icon-arrow-swap"
                                        buttonRenderMode="labeledImage"/>
                            </li>
                        </c:if>
                        
                        <c:if test="${showScheduleActions}">
                            <cti:url var="savedSchedulesUrl" value="/stars/operator/thermostatSchedule/savedSchedules">
                                <cti:param name="accountId" value="${accountId}"/>
                                <cti:param name="thermostatIds" value="${inventoryId}"/>
                            </cti:url>
                            <li>
                                <cti:button nameKey="savedSchedules" href="${savedSchedulesUrl}" renderMode="labeledImage" icon="icon-clipboard"/>
                            </li>
                        </c:if>
                        
                        <c:if test="${showManualAction}">
                            <cti:url var="editManualUrl" value="/stars/operator/thermostatManual/view">
                                <cti:param name="accountId" value="${accountId}"/>
                                <cti:param name="thermostatIds" value="${inventoryId}"/>
                            </cti:url>
                            <li>
                                <cti:button nameKey="manual" href="${editManualUrl}" renderMode="labeledImage" icon="icon-wrench"/>
                            </li>
                        </c:if>
                        
                        <%-- METER ACTIONS --%>
                        <c:if test="${showMeterConfigAction}">
                            <cti:url var="configUrl" value="/stars/operator/hardware/config/meterConfig">
                                <cti:param name="accountId" value="${accountId}"/>
                                <cti:param name="meterId" value="${hardware.deviceId}"/>
                            </cti:url>
                            <li>
                                <cti:button nameKey="editConfig" href="${configUrl}" renderMode="labeledImage" icon="icon-cog-edit"/>
                            </li>
                        </c:if>
                        
                        <c:if test="${showMeterDetailAction}">
                            <cti:paoDetailUrl yukonPao="${hardware.yukonPao}" var="meterDetailUrl"/>
                            <li>
                                 <cti:button nameKey="meterDetail" href="${meterDetailUrl}" renderMode="labeledImage" icon="icon-control-equalizer-blue"/>
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
                                        icon="icon-arrow-swap"
                                        buttonRenderMode="labeledImage" />
                            </li>
                        </c:if>
                        
                        <%-- GATEWAY ACTIONS --%>
                        
                        <c:if test="${showTextMessageAction}">
                            <li>
                                <cti:button nameKey="textMessage" id="sendTextMsg" renderMode="labeledImage" dialogButton="true" icon="icon-phone-sound"/>
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
                                        icon="icon-arrow-swap"
                                        buttonRenderMode="labeledImage"/>
                            </li>
                        </c:if>
                        
                    </ul>
                    
                </tags:sectionContainer2>
                
                <c:if test="${showZigbeeState}">
                    <cti:displayForPageEditModes modes="VIEW">
                        <tags:sectionContainer2 nameKey="zigbeeStatus" styleClass="statusContainer f-block-this stacked" id="zigbeeStatus">
                            <table class="compact-results-table dashed">
                                <thead>
                                <tr>
                                    <th><i:inline key=".status"/></th>
                                    <th><i:inline key=".timestamp"/></th>
                                </tr>
                                </thead>
                                <tfoot></tfoot>
                                <tbody>
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
                                </tbody>
                            </table>
                            <div id="zbCommandStatus" class="dn error zbCommandMsg"></div>
                            <div class="page-action-area">
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
                                        <i:simplePopup id="confirmCommissionPopup" styleClass="commissionConfirmationMsg" titleKey=".title" on="#commission">
                                            <p>
                                                <div id="decommissionedConfirmMsg">
                                                    <i:inline key=".message"/>
                                                </div>
                                                <div id="commissionedConfirmMsg" class="error" style="display:none">
                                                    <i:inline key=".warnMessage"/>
                                                </div>
                                            <p>
                                            <div class="action-area">
                                                <cti:button nameKey="ok" name="commissionSubmit" type="submit" classes="primary action"/>
                                                <cti:button nameKey="cancel" onclick="$('#confirmCommissionPopup').dialog('close')" />
                                            </div>
                                        </i:simplePopup>
                                    </cti:msgScope>
                                    <cti:dataUpdaterCallback function="getCommissionConfirmationCallback()" initialize="true" value="POINT/${hardware.commissionedId}/VALUE"/>

                                    <tags:confirmDialog submitName="decommissionSubmit"
                                        nameKey=".decommissionConfirmation"
                                        styleClass="commissionConfirmationMsg" on="#decommission"
                                        endAction="hide" />
                                </c:if>
                                
                                <c:if test="${showDisabledCommissionActions}">
                                    <cti:button nameKey="commission.disabled" disabled="true"/>
                                    <cti:button nameKey="decommission.disabled" disabled="true"/>
                                </c:if>
                            </div>
                        </tags:sectionContainer2>
                    </cti:displayForPageEditModes>
                </c:if>
                
                <c:if test="${showAssignedDevices}">
                    <tags:sectionContainer2 nameKey="assignedDevices" id="assignedDevices" styleClass="f-block-this stacked">
                        <div class="scroll-small">
                            <c:choose>
                                <c:when test="${not empty assignedDevices}">
                                    <table class="compact-results-table dashed">
                                        <tr>
                                            <th><i:inline key=".serialNumber"/></th>
                                            <th><i:inline key=".status"/></th>
                                            <th><i:inline key=".actions"/></th>
                                        </tr>
                                        <c:forEach var="device" items="${assignedDevices}">
                                            <tr>
                                                <cti:url value="/stars/operator/hardware/view" var="viewUrl">
                                                    <cti:param name="accountId" value="${accountId}"/>
                                                    <cti:param name="inventoryId" value="${device.inventoryIdentifier.inventoryId}"/>
                                                </cti:url>
                                                <td><a href="${viewUrl}">${device.serialNumber}</a></td>
                                                <td class="pointStateColumn">
                                                    <cti:pointStatusColor pointId="${device.commissionId}">
                                                        <cti:pointValue pointId="${device.commissionId}" format="VALUE"/>
                                                    </cti:pointStatusColor>
                                                </td>
                                                <td class="wsnw last">
                                                    <cti:button nameKey="assignedDevices.commission" renderMode="image" classes="assignedDevicesCommission" id="assignedDevicesCommission_${device.deviceId}" icon="icon-accept"/>
                                                    <cti:button nameKey="assignedDevices.decommission" renderMode="image" classes="assignedDevicesDecommission" id="assignedDevicesDecommission_${device.deviceId}" icon="icon-delete"/>
                                                    <cti:msgScope paths=".commissionConfirmation,">
                                                        <i:simplePopup id="confirmCommissionPopup_${device.deviceId}" styleClass="commissionConfirmationMsg" titleKey=".title" on="#assignedDevicesCommission_${device.deviceId}">
                                                            <p>
                                                                <div id="decommissionedConfirmMsg_${device.deviceId}">
                                                                    <i:inline key=".message"/>
                                                                </div>
                                                                <div id="commissionedConfirmMsg_${device.deviceId}" class="error" style="display:none">
                                                                    <i:inline key=".warnMessage"/>
                                                                </div>
                                                            <p>
                                                            <div class="action-area">
                                                                <cti:button name="assignedDevicesCommissionSubmit_${device.deviceId}" nameKey="ok" type="submit" classes="primary action"/>
                                                                <cti:button nameKey="cancel" onclick="$('#confirmCommissionPopup_${device.deviceId}').dialog('close')" />
                                                            </div>
                                                        </i:simplePopup>
                                                    </cti:msgScope>
                                                    <cti:dataUpdaterCallback function="getEndpointCommissionConfirmationCallback('${device.deviceId}')" initialize="true" value="POINT/${device.commissionId}/VALUE"/>
                                                    <tags:confirmDialog submitName="assignedDevicesDecommissionSubmit_${device.deviceId}"
                                                        nameKey=".decommissionConfirmation" 
                                                        styleClass="commissionConfirmationMsg" on="#assignedDevicesDecommission_${device.deviceId}"
                                                        endAction="hide" />
                                                    
                                                    <cti:url value="/stars/operator/hardware/zb/removeDeviceFromGateway" var="removeUrl">
                                                        <cti:param name="accountId" value="${accountId}"/>
                                                        <cti:param name="inventoryId" value="${inventoryId}"/>
                                                        <cti:param name="gatewayId" value="${hardware.deviceId}"/>
                                                        <cti:param name="deviceId" value="${device.deviceId}"/>
                                                    </cti:url>
                                                    <cti:button nameKey="remove" href="${removeUrl}" renderMode="image" classes="f-blocker" icon="icon-cross"/>
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
                            <div class="action-area">
                                <form action="/stars/operator/hardware/zb/addDeviceToGateway" method="post">
                                    <cti:csrfToken/>
                                    <input type="hidden" name="accountId" value="${accountId}">
                                    <input type="hidden" name="inventoryId" value="${inventoryId}">
                                    <input type="hidden" name="gatewayId" value="${hardware.deviceId}">
                                    <select name="deviceId">
                                        <c:forEach var="device" items="${availableDevices}">
                                            <option value="${device.deviceId}">${device.serialNumber}</option>
                                        </c:forEach>
                                    </select>
                                    <cti:button nameKey="add" type="submit" classes="f-blocker"/>
                                </form>
                            </div>
                        </c:if>
                    </tags:sectionContainer2>
                </c:if>
                
                <c:if test="${showPoints}">
                    
                    <%@ include file="hardwarePoints.jspf" %>
                    
                    <br>
                
                </c:if>
                    
                <%-- COMMON HARDWARE HISTORY --%>
                <tags:sectionContainer2 nameKey="history" styleClass="stacked">
                    <%@ include file="hardwareHistory.jspf" %>
                </tags:sectionContainer2>
                
            </div>
            
        </cti:displayForPageEditModes>
        
    </div>
        
    <cti:displayForPageEditModes modes="VIEW">
        <cti:checkRolesAndProperties value="${editingRoleProperty}">
            <div class="page-action-area">
                <cti:url value="/stars/operator/hardware/edit" var="editUrl">
                    <cti:param name="accountId" value="${accountId}"/>
                    <cti:param name="inventoryId" value="${inventoryId}"/>
                </cti:url>
                <cti:button nameKey="edit" icon="icon-pencil" href="${editUrl}"/>
            </div>
        </cti:checkRolesAndProperties>
    </cti:displayForPageEditModes>
</cti:standardPage>