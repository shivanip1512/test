<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:checkRole role="operator.DeviceActionsRole.ROLEID">
<cti:checkProperty property="operator.DeviceActionsRole.DEVICE_GROUP_MODIFY">
    <c:set var="hasModifyRoleProperty" value="true"/>
</cti:checkProperty>
</cti:checkRole>
    
<div class="widgetInternalSectionHeader">Current Groups</div>
    <c:choose>
        <c:when test="${not empty currentGroups}">

            <div style="overflow:auto; height:160px;">
	            <table width="97%">
	                <c:forEach var="group" items="${currentGroups}">
	                
	                    <c:if test="${not group.hidden}">
	                
	                    <tr class="<tags:alternateRow odd="" even="altRow"/>">
	                        <td style="border: none">
	                           <cti:url value="/spring/group/editor/home" var="groupEditorUrl">
	                             <cti:param name="groupName" value="${group.fullName}"/>
	                           </cti:url>
	            
	                            <a href="${groupEditorUrl}">${fn:escapeXml(group.fullName)}</a>
	                        </td>
	                        
	                        <%-- remove device from this group --%>
	                        <c:if test="${hasModifyRoleProperty}">
	                            <td style="border: none; width: 15px; text-align: center;">
	                                <c:choose>
	                                    <c:when test="${group.modifiable}">
	                                    <tags:widgetLink method="remove" title="Remove" labelBusy="Removing" groupId="${group.id}" container="currentGroups">
	                                        <img class="cssicon" src="<cti:url value="/WebConfig/yukon/Icons/clearbits/close.gif"/>">
	                                    </tags:widgetLink>
	                                    </c:when>
	                                    <c:otherwise>
	                                        <img class="graycssicon" title="Cannot remove device from group" src="<cti:url value="/WebConfig/yukon/Icons/clearbits/close.gif"/>">
	                                    </c:otherwise>
	                                </c:choose>
	                            </td>
	                        </c:if>
	                        
	                    </tr>
	                    
	                    </c:if>
	                </c:forEach>
	            </table>
            </div>
        </c:when>
        <c:otherwise>
            No Groups
        </c:otherwise>
    </c:choose>