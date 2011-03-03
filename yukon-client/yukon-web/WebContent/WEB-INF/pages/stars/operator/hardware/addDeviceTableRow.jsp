<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:url var="gatewayControllerUrl" value="/spring/stars/operator/hardware/gateway/"/>
<cti:url var="gatewayControllerUrlParameters" value="?accountId=${accountId}&inventoryId=${inventoryId}"/>


<tags:standardPageFragment module="operator" pageName="gateway" fragmentName="addDevice">

	<table class="compactResultsTable">
        <tr id="${zigbeeDto.serialNumber}" class="deviceRow">
            <td>
				<spring:escapeBody htmlEscape="true">${zigbeeDto.serialNumber}</spring:escapeBody>
            </td>
            <td><spring:escapeBody htmlEscape="true">${zigbeeDto.deviceType}</spring:escapeBody></td>
            <td>
                    <cti:classUpdater type="POINT" identifier="${zigbeeDto.commissionId}/STATE">
                        <cti:pointValue pointId="${zigbeeDto.commissionId}" format="VALUE"/>
                    </cti:classUpdater>            
            </td>
            <td nowrap="nowrap">
                <cti:img key="link" href="${gatewayControllerUrl}installStat?accountId=${accountId}&deviceId=${zigbeeDto.deviceId}"/>
                <cti:img key="unlink" href="${gatewayControllerUrl}uninstallStat?accountId=${accountId}&deviceId=${zigbeeDto.deviceId}"/>
                <cti:img key="textMessage" href="${gatewayControllerUrl}sendTextMessage?accountId=${accountId}&deviceId=${zigbeeDto.deviceId}&message=testmessage&gatewayInventoryId=${inventoryId}"/>
            </td>
			<td class="removeColumn" >
				<cti:img key="delete" href="javascript:removeTableRow('${zigbeeDto.serialNumber}',${zigbeeDto.deviceId})"/>
			</td>
         </tr>
    </table>

</tags:standardPageFragment>