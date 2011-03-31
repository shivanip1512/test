<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="operator" page="gateway">
    <cti:includeCss link="/WebConfig/yukon/styles/operator/hardware.css"/>
    
    <cti:url var="editGatewayUrl" value="/spring/stars/operator/hardware/hardwareEdit">
        <cti:param name="accountId" value="${accountId}"/>
        <cti:param name="inventoryId" value="${inventoryId}"/>
    </cti:url>

    <cti:dataGrid tableStyle="width:100%;" cols="2" rowStyle="vertical-align:top;" cellStyle="padding-right:20px;width:30%">
    
        <cti:dataGridCell>

            <tags:formElementContainer nameKey="deviceInfoSection">
                
                <tags:nameValueContainer2>
                    
                    <tags:nameValue2 nameKey=".serialNumber">
                        <a href="${editGatewayUrl}">
                            <spring:escapeBody htmlEscape="true">${gatewayDto.serialNumber}</spring:escapeBody>
                        </a>
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
                    
                    <tags:nameValue2 nameKey=".connectionStatus" rowClass="pointState">
                        <cti:pointStatusColor pointId="${gatewayDto.connectionStatusId}">
                            <cti:pointValue pointId="${gatewayDto.connectionStatusId}" format="VALUE"/>
                        </cti:pointStatusColor>
                    </tags:nameValue2>
                    
                    <tags:nameValue2 nameKey=".gatewayStatus" rowClass="pointState">
                        <cti:pointStatusColor pointId="${gatewayDto.gatewayStatusId}">
                            <cti:pointValue pointId="${gatewayDto.gatewayStatusId}" format="VALUE"/>
                        </cti:pointStatusColor>
                    </tags:nameValue2>
                    
                </tags:nameValueContainer2>
                
            </tags:formElementContainer>
                
        </cti:dataGridCell>
        
        <cti:dataGridCell>
        
            <tags:formElementContainer nameKey="commandsSection">
                <form action="/spring/stars/operator/hardware/gateway/gatewayAction" method="post">
                    <input name="accountId" type="hidden" value="${accountId}">
                    <input name="inventoryId" type="hidden" value="${inventoryId}">
                    <input name="gatewayId" type="hidden" value="${gatewayId}">
                    
                    <ul>
                        <li>
                           <cti:button key="commission" type="submit" name="commission" naked="true"/>
                        </li>
                        <li>
                           <cti:button key="decommission" type="submit" name="decommission" naked="true"/>
                        </li>
                        <li>
                            <cti:button key="textMessage" id="sendTextMsg" naked="true"/>
                            <i:simplePopup titleKey=".sendTextMsg" id="textMsgPopup" on="#sendTextMsg" styleClass="smallSimplePopup">
                                    <textarea rows="6" cols="60" name="message"></textarea>
                                    <div class="actionArea">
                                        <cti:button key="ok" type="submit" name="sendTextMsg"/>
                                        <cti:button key="cancel" onclick="$('textMsgPopup').hide()"/>
                                    </div>
                            </i:simplePopup>
                        </li>
                    </ul>
                    
                </form>
                
            </tags:formElementContainer>
            
        </cti:dataGridCell>
        
    </cti:dataGrid>
    
    <br>
    
    <tags:boxContainer2 nameKey="assignedZigbeeDevices" arguments="${accountNumber}">
    
        <table class="compactResultsTable">
            <thead>
                <tr>
                    <th><i:inline key=".serialNumber"/></th>
                    <th><i:inline key=".assignedZigbeeDevices.displayType"/></th>
                    <th><i:inline key=".assignedZigbeeDevices.deviceState"/></th>
                    <th><i:inline key=".actions"/></th>
                    <th><i:inline key=".gateway"/></th>
                </tr>
            </thead>
            
            <tbody id="deviceTableBody">
            
                <c:forEach var="pair" items="${zigbeeDevices}">
                    <c:set var="deviceGateway" value="${pair.first}"/>
                    <c:set var="zigbeeDevice" value="${pair.second}"/>

                    <tr id="${zigbeeDevice.serialNumber}" class="deviceRow">
                    
                        <td>
                            <cti:url var="editDeviceUrl" value="/spring/stars/operator/hardware/hardwareEdit">
                                <cti:param name="accountId" value="${accountId}"/>
                                <cti:param name="inventoryId" value="${zigbeeDevice.inventoryIdentifier.inventoryId}"/>
                            </cti:url>
                            <a href="${editDeviceUrl}">
                                <spring:escapeBody htmlEscape="true">${zigbeeDevice.serialNumber}</spring:escapeBody>
                            </a>
                        </td>
                    
                        <td>
                            <spring:escapeBody htmlEscape="true">${zigbeeDevice.deviceType}</spring:escapeBody>
                        </td>
                    
                        <td class="pointStateColumn">
                            <cti:pointStatusColor pointId="${zigbeeDevice.commissionId}">
                                <cti:pointValue pointId="${zigbeeDevice.commissionId}" format="VALUE"/>
                            </cti:pointStatusColor>
                        </td>
                    
                        <td nowrap="nowrap">
                            <form action="/spring/stars/operator/hardware/gateway/deviceAction" method="post">
                                <input name="accountId" type="hidden" value="${accountId}">
                                <input name="inventoryId" type="hidden" value="${inventoryId}">
                                <input name="gatewayId" type="hidden" value="${gatewayId}">
                                <input name="deviceId" type="hidden" value="${zigbeeDevice.deviceId}">
                                
                                <cti:img key="link" name="install" type="input"/>
                                <cti:img key="unlink" name="uninstall" type="input"/>
                                <c:if test="${zigbeeDevice.gatewayId == gatewayId}">
                                    <cti:img key="remove" name="remove" type="input"/>
                                </c:if>
                                <c:if test="${zigbeeDevice.gatewayId == null}">
                                    <cti:img key="add" name="add" type="input"/>
                                </c:if>
                                
                            </form>
                        </td>
                        
                        <td>
                            <c:choose>
                                <c:when test="${zigbeeDevice.gatewayId != null}">
                                    <cti:url var="editGatewayUrl" value="/spring/stars/operator/hardware/hardwareEdit">
                                        <cti:param name="accountId" value="${accountId}"/>
                                        <cti:param name="inventoryId" value="${deviceGateway.inventoryId}"/>
                                    </cti:url>
                                    <a href="${editGatewayUrl}">
                                        <cti:deviceName deviceId="${zigbeeDevice.gatewayId}"/>
                                    </a>
                                </c:when>
                                <c:otherwise><i:inline key=".notAssigned"/></c:otherwise>
                            </c:choose>
                        </td>
                    
                    </tr>
                </c:forEach>
                
            </tbody>
            
        </table>

    </tags:boxContainer2>
    
</cti:standardPage>