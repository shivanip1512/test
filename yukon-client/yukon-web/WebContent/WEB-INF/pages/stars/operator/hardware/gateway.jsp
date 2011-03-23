<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:url var="gatewayControllerUrl" value="/spring/stars/operator/hardware/gateway/"/>
<cti:url var="gatewayControllerUrlParameters" value="?accountId=${accountId}&inventoryId=${inventoryId}"/>


<cti:standardPage module="operator" page="gateway">

<script type="text/javascript">

addTableRowHandler = function (selectedPaoInfo) {   
	for(var i = 0; i < selectedPaoInfo.size(); i++) {
		var devInventoryId = selectedPaoInfo[i].inventoryId;
		addTableRow(devInventoryId);	
	}
	return true;
}

addTableRow = function (devInventoryId) {
	var url = '/spring/stars/operator/hardware/gateway/assignDevice';
	
    var newRow = $('defaultDeviceRow').cloneNode(true);
    $('deviceTableBody').appendChild(newRow);

    new Ajax.Request(url,{
        parameters: {'accountId': ${accountId}, 'deviceId': devInventoryId, 'gatewayId': ${inventoryId}},
        onSuccess: function(transport) {
            var dummyHolder = document.createElement('div');
            dummyHolder.innerHTML = transport.responseText;
            var replacementRow = $(dummyHolder).down('tr');
            //var undoRow = replacementRow.next();
            $('deviceTableBody').replaceChild(replacementRow, newRow);
            //$('deviceTableBody').appendChild(undoRow);
        },
        onFailure: function() { 
	        newRow.remove();
        }
    });
}

removeTableRow = function(rowId, deviceId) {
	 var url = '/spring/stars/operator/hardware/gateway/unassignDevice';
	 new Ajax.Request(url,{
	        parameters: {'accountId': ${accountId}, 'deviceId': deviceId},
	        onSuccess: function(transport) {
				$(rowId).remove();
	        }
	    });
}

</script>

    <cti:includeCss link="/WebConfig/yukon/styles/operator/hardware.css"/>

	<cti:dataGrid tableStyle="width:100%;" cols="2" rowStyle="vertical-align:top;" cellStyle="padding-right:20px;width:30%">
		<cti:dataGridCell>

		    <form:form id="updateForm" commandName="gatewayDto" action="${action}">
		    
		        <input type="hidden" name="accountId" value="${accountId}">
		        <input type="hidden" name="inventoryId" value="${inventoryId}">
		        
				<tags:formElementContainer nameKey="deviceInfoSection">
					<tags:nameValueContainer2>
						<tags:nameValue2 nameKey=".serialNumber">
							<spring:escapeBody htmlEscape="true">${gatewayDto.serialNumber}</spring:escapeBody>
						</tags:nameValue2>
						<tags:nameValue2 nameKey=".gatewayType">
							<spring:escapeBody htmlEscape="true">${gatewayDto.gatewayType}</spring:escapeBody>
						</tags:nameValue2>
						<tags:nameValue2 nameKey=".externalId">
							<spring:escapeBody htmlEscape="true">${gatewayDto.digiId}</spring:escapeBody>
						</tags:nameValue2>
						<tags:nameValue2 nameKey=".macAddress">
							<spring:escapeBody htmlEscape="true">${gatewayDto.macAddress}</spring:escapeBody>
						</tags:nameValue2>
						<tags:nameValue2 nameKey=".connectionStatus">
							<cti:classUpdater type="POINT" identifier="${gatewayDto.connectionStatusId}/SHORT">
                                <cti:pointValue pointId="${gatewayDto.connectionStatusId}" format="VALUE"/>
                            </cti:classUpdater>
						</tags:nameValue2>
						<tags:nameValue2 nameKey=".gatewayStatus">
                            <cti:classUpdater type="POINT" identifier="${gatewayDto.gatewayStatusId}/SHORT">
                                <cti:pointValue pointId="${gatewayDto.gatewayStatusId}" format="VALUE"/>
                            </cti:classUpdater>
						</tags:nameValue2>
					</tags:nameValueContainer2>
				</tags:formElementContainer>
			</form:form>
		</cti:dataGridCell>
		
		<cti:dataGridCell>
			<tags:formElementContainer nameKey="commandsSection">	
				<div>
					<cti:labeledImg key="commission" href="${gatewayControllerUrl}commission${gatewayControllerUrlParameters}"/>
				</div>
				<div>
					<cti:labeledImg key="decommission" href="${gatewayControllerUrl}decommission${gatewayControllerUrlParameters}"/>
				</div>
				
                <br>
				<div>
					Temp way to call "reportAllDevices"
					<br>
					<cti:labeledImg key="add" href="${gatewayControllerUrl}reportAllDevices${gatewayControllerUrlParameters}"/>
				</div>
                
			</tags:formElementContainer>
		</cti:dataGridCell>
	</cti:dataGrid>
    
    <br>
    
	<tags:boxContainer2 nameKey="assignedZigbeeDevices">
        <tags:alternateRowReset/>
        
        <table style="display: none">
			<tr id="defaultDeviceRow">
				<td colspan="5" style="text-align: center">
					<img src="/WebConfig/yukon/Icons/indicator_arrows.gif">
				</td>
			</tr>
		</table>
        
        <table id="deviceTable" class="compactResultsTable deviceRowCounter">
            <thead>
            	<tr>
                    <th class="name" nowrap="nowrap"><i:inline key=".serialNumber"/></th>
                    <th class="type" nowrap="nowrap"><i:inline key=".assignedZigbeeDevices.displayType"/></th>
                    <th class="label" nowrap="nowrap"><i:inline key=".assignedZigbeeDevices.deviceState"/></th>
                    <th class="actions"><i:inline key=".actions"/></th>
                    <th class="removeColumn"><i:inline key=".remove"/></th>
                </tr>
            </thead>
            <tbody id="deviceTableBody">
            <c:forEach var="assignedDevice" items="${assignedDevices}">
                <tr id="${assignedDevice.serialNumber}" class="deviceRow">
                    <td>
						<spring:escapeBody htmlEscape="true">${assignedDevice.serialNumber}</spring:escapeBody>
                    </td>
                    <td><spring:escapeBody htmlEscape="true">${assignedDevice.deviceType}</spring:escapeBody></td>
                    <td>
                        <cti:classUpdater type="POINT" identifier="${assignedDevice.commissionId}/SHORT">
                            <cti:pointValue pointId="${assignedDevice.commissionId}" format="VALUE"/>
                        </cti:classUpdater>
                    </td>
                    <td nowrap="nowrap">
                        <cti:img key="link" href="${gatewayControllerUrl}installStat?accountId=${accountId}&deviceId=${assignedDevice.deviceId}&gatewayInvId=${inventoryId}"/>
                        <cti:img key="unlink" href="${gatewayControllerUrl}uninstallStat?accountId=${accountId}&deviceId=${assignedDevice.deviceId}&gatewayInvId=${inventoryId}"/>
                        <cti:img key="textMessage" href="${gatewayControllerUrl}sendTextMessage?accountId=${accountId}&deviceId=${assignedDevice.deviceId}&message=testmessage&gatewayInvId=${inventoryId}"/>
                    </td>
                    <td class="removeColumn" >
						<cti:img key="remove" href="javascript:removeTableRow('${assignedDevice.serialNumber}',${assignedDevice.deviceId})"/>
					</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>

		<div class="actionArea">
			<tags:pickerDialog  extraArgs="${accountId}"
								id="availableZigbeeDevicePicker" 
								type="availableZigbeeDevicePicker" 
                                endAction="addTableRowHandler" 
                                linkType="button"
                                multiSelectMode="true"
                                nameKey="add"/>
		</div>
		
    </tags:boxContainer2>
</cti:standardPage>
    
