<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

    <%-- User must have DEVICE_GROUP_MODIFY to remove devices from group. Set once for use in loop. --%>
    <cti:checkRole role="operator.DeviceActionsRole.ROLEID">
    <cti:checkProperty property="operator.DeviceActionsRole.DEVICE_GROUP_MODIFY">
        <c:set var="hasModifyRoleProperty" value="true"/>
    </cti:checkProperty>
    </cti:checkRole>
                
	<table style="width: 95%;" >
		<c:choose>
			<c:when test="${fn:length(deviceList) > 0}">
            
                <%-- REMOVE ALL DEVICES LINK --%>
                <%-- the group being having devices removed must itself be modifiable --%>
                <c:if test="${hasModifyRoleProperty && group.modifiable}">
                    <tr>
                        <td colspan="2" align="right">
                            <cti:msg var="removeAllDevicesFromGroupLabel" key="yukon.web.deviceGroups.editor.membersContainer.removeAllDevicesFromGroupLabel"/>
                            <cti:msg var="removeAllDevicesFromGroupDescription" key="yukon.web.deviceGroups.editor.membersContainer.removeAllDevicesFromGroupDescription"/>
                            <cti:msg var="confirmRemoveText" key="yukon.web.deviceGroups.editor.membersContainer.confirmRemoveText" javaScriptEscape="true"/>
                            
                            <input id="removeAllDevicesButton" type="button" onclick="removeAllDevices('${confirmRemoveText}')" value="${removeAllDevicesFromGroupLabel}" title="${removeAllDevicesFromGroupDescription}">
                            <img id="removeAllDevicesWaitImg" src="<cti:url value="/WebConfig/yukon/Icons/indicator_arrows.gif"/>" style="display:none;">       
                        </td>
                    </tr>
                </c:if>
                
                <c:if test="${not empty membersErrorMessage}">
                    <span class="errorRed">${membersErrorMessage}</span>
                    <br><br>
                </c:if>
                
                <c:if test="${limted}">
                    <span class="errorRed">
                        <cti:msg key="yukon.web.deviceGroups.editor.membersContainer.showDevicesLimitText" 
                                 argument="${maxGetDevicesSize}"/>
                    </span>
                    <br><br>
                </c:if>
                
				<c:forEach var="device" items="${deviceList}">
					<tr class="<tags:alternateRow odd="" even="altRow"/>">
						<td style="border: none;">
                            <cti:paoDetailUrl yukonPao="${device}">${device.name}</cti:paoDetailUrl>
						</td>
                        
                        <c:if test="${hasModifyRoleProperty}">
						<td style="border: none; width: 15px;text-align: center;">
							
							<c:choose>
								<c:when test="${group.modifiable}">
									<cti:uniqueIdentifier prefix="groupHierarchy_" var="thisId"/>
									<form style="display: inline;" id="${thisId}_removeDevice" action="/group/editor/removeDevice" method="post">
										<input type="hidden" name="deviceId" value="${device.paoIdentifier.paoId}" />
										<input type="hidden" name="groupName" value="${fn:escapeXml(group.fullName)}" />
										<input type="hidden" name="showDevices" value="true" />
                                        <div class="dib">
    										<input type="submit" title="Remove device from group" class="icon icon_remove"/>
                                        </div>
									</form>
								</c:when>
								<c:otherwise>
                                    <span class="icon icon_remove_disabled"></span>
								</c:otherwise>
							</c:choose>
						</td>
                        </c:if>
                        
					</tr>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<tr>
					<td colspan="2"><cti:msg key="yukon.web.deviceGroups.editor.membersContainer.noDevices"/></td>
				</tr>
			</c:otherwise>
		</c:choose>	
	</table>
		