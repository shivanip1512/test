<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<script type="text/javascript">
jQuery(function () {
    jQuery('.icon_remove').click(function(event) {
        var form = jQuery(event.currentTarget).closest('form');
        form.submit();
    });
});
</script>

<%-- User must have DEVICE_GROUP_MODIFY to remove devices from group. Set once for use in loop. --%>
<cti:checkRolesAndProperties value="DEVICE_ACTIONS">
<cti:checkRolesAndProperties value="DEVICE_GROUP_MODIFY">
    <c:set var="hasModifyRoleProperty" value="true"/>
</cti:checkRolesAndProperties>
</cti:checkRolesAndProperties>

<c:choose>
	<c:when test="${fn:length(deviceList) > 0}">
        
        <%-- REMOVE ALL DEVICES LINK --%>
        <%-- the group having devices removed must itself be modifiable --%>
        <div class="stacked">
            <c:if test="${hasModifyRoleProperty && group.modifiable}">
                <cti:msg var="removeAllDevicesFromGroupLabel" key="yukon.web.deviceGroups.editor.membersContainer.removeAllDevicesFromGroupLabel"/>
                <cti:msg var="removeAllDevicesFromGroupDescription" key="yukon.web.deviceGroups.editor.membersContainer.removeAllDevicesFromGroupDescription"/>
                <cti:msg var="confirmRemoveText" key="yukon.web.deviceGroups.editor.membersContainer.confirmRemoveText" javaScriptEscape="true"/>
                <div class="stacked">
                    <input id="removeAllDevicesButton" type="button" onclick="removeAllDevices('${confirmRemoveText}')" value="${removeAllDevicesFromGroupLabel}" title="${removeAllDevicesFromGroupDescription}">
                    <img id="removeAllDevicesWaitImg" src="<cti:url value="/WebConfig/yukon/Icons/indicator_arrows.gif"/>" style="display:none;">
                </div>       
            </c:if>
            <c:if test="${not empty membersErrorMessage}">
                <div class="error stacked">${membersErrorMessage}</div>
            </c:if>
            
            <c:if test="${limted}">
                <div class="warning stacked"><i:inline key="yukon.web.deviceGroups.editor.membersContainer.showDevicesLimitText" arguments="${maxGetDevicesSize}"/></div>
            </c:if>
        </div>
        <div class="liteContainer scrollingContainer_small">
            <table class="compactResultsTable rowHighlighting">
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
            									<a title="Remove device from group" class="tar icon icon_remove" href="javascript:void(0);"></a>
            								</form>
            							</c:when>
            							<c:otherwise><span class="icon icon_remove_disabled"/></c:otherwise>
            						</c:choose>
            					</td>
                            </c:if>
        				</tr>
        			</c:forEach>
                </tbody>
            </table>
        </div>
	</c:when>
	<c:otherwise><i:inline key="yukon.web.deviceGroups.editor.membersContainer.noDevices"/></c:otherwise>
</c:choose>	