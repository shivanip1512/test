<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:msgScope paths="deviceGroups.editor">
<%-- User must have DEVICE_GROUP_MODIFY to remove devices from group. Set once for use in loop. --%>
<cti:checkRolesAndProperties value="DEVICE_ACTIONS">
<cti:checkRolesAndProperties value="DEVICE_GROUP_MODIFY">
    <c:set var="hasModifyRoleProperty" value="true"/>
</cti:checkRolesAndProperties>
</cti:checkRolesAndProperties>

<c:choose>
	<c:when test="${fn:length(deviceList) > 0}">
        <div class="stacked clearfix">
            <c:if test="${not empty membersErrorMessage}">
                <div class="error stacked">${membersErrorMessage}</div>
            </c:if>
            <c:if test="${limted}">
                <div class="warning stacked"><i:inline key="yukon.web.deviceGroups.editor.membersContainer.showDevicesLimitText" arguments="${maxGetDevicesSize}"/></div>
            </c:if>
        </div>
        <div class="scroll-small">
            <table class="compact-results-table row-highlighting">
                <thead></thead>
                <tfoot></tfoot>
                <tbody>
        			<c:forEach var="device" items="${deviceList}">
        				<tr>
        					<td><cti:paoDetailUrl yukonPao="${device}">${device.name}</cti:paoDetailUrl></td>
                            <c:if test="${hasModifyRoleProperty}">
            					<td class="tar last">
            						<c:choose>
            							<c:when test="${group.modifiable}">
            								<cti:uniqueIdentifier prefix="groupHierarchy_" var="thisId"/>
            								<form id="${thisId}_removeDevice" action="/group/editor/removeDevice" method="post" class="dib">
            									<input type="hidden" name="deviceId" value="${device.paoIdentifier.paoId}" />
            									<input type="hidden" name="groupName" value="${fn:escapeXml(group.fullName)}" />
            									<input type="hidden" name="showDevices" value="true" />
            									<cti:button nameKey="add" type="submit" renderMode="image" icon="icon-cross"/>
            								</form>
            							</c:when>
            							<c:otherwise><i class="icon icon-cross" disabled="disabled"/></c:otherwise>
            						</c:choose>
            					</td>
                            </c:if>
        				</tr>
        			</c:forEach>
                </tbody>
            </table>
        </div>
        <c:if test="${hasModifyRoleProperty && group.modifiable}">
            <cti:msg2 var="removeAllDevicesFromGroupLabel" key=".membersContainer.removeAllDevicesFromGroupLabel"/>
            <cti:msg2 var="removeAllDevicesFromGroupDescription" key=".membersContainer.removeAllDevicesFromGroupDescription"/>
            <cti:msg2 var="confirmRemoveText" key=".membersContainer.confirmRemoveText" javaScriptEscape="true"/>
            <div class="action-area stacked">
                <button id="removeAllDevicesButton" onclick="removeAllDevices('${confirmRemoveText}')" value="${removeAllDevicesFromGroupLabel}" title="${removeAllDevicesFromGroupDescription}">
                    <i class="icon icon-cross"></i><span class="b-label">${removeAllDevicesFromGroupLabel}</span>
                </button>
                <img id="removeAllDevicesWaitImg" src="<cti:url value="/WebConfig/yukon/Icons/spinner.gif"/>" style="display:none;">
            </div>       
        </c:if>
	</c:when>
	<c:otherwise><i:inline key="yukon.web.deviceGroups.editor.membersContainer.noDevices"/></c:otherwise>
</c:choose>
</cti:msgScope>