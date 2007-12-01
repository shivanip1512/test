<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

	<table style="width: 95%;" >
		<c:choose>
			<c:when test="${fn:length(deviceList) > 0}">
				<c:forEach var="device" items="${deviceList}">
					<tr class="<tags:alternateRow odd="" even="altRow"/>">
						<td style="border: none;">
                           <c:url value="/spring/csr/home" var="csrHomeUrl">
                             <c:param name="deviceId" value="${device.deviceId}"/>
                           </c:url>
            
							<a href="${csrHomeUrl}"><cti:deviceName device="${device}" /></a>
						</td>
						<td style="border: none; width: 15px;text-align: center;">
							
							<c:choose>
								<c:when test="${group.modifiable}">
									<cti:uniqueIdentifier prefix="groupHierarchy_" var="thisId"/>
									<form style="display: inline;" id="${thisId}_removeDevice" action="/spring/group/removeDevice" method="post">
										<input type="hidden" name="deviceId" value="${device.deviceId}" />
										<input type="hidden" name="groupName" value="${group.fullName}" />
										<input type="hidden" name="showDevices" value="true" />
										<input type="image" title="Remove device from group" class="cssicon" src="<c:url value="/WebConfig/yukon/Icons/clearbits/close.gif"/>" />
									</form>
								</c:when>
								<c:otherwise>
									<img class="graycssicon" title="Cannot remove device from group" src="<c:url value="/WebConfig/yukon/Icons/clearbits/close.gif"/>" />
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<tr>
					<td colspan="2">No devices</td>
				</tr>
			</c:otherwise>
		</c:choose>	
	</table>
		