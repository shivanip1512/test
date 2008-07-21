<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<div class="widgetInternalSectionHeader">Current Groups</div>
    <c:choose>
        <c:when test="${not empty currentGroups}">
<table style="width: 100%">
            <c:forEach var="group" items="${currentGroups}">
            
                <c:if test="${not group.hidden}">
            
                <tr class="<tags:alternateRow odd="" even="altRow"/>">
                    <td style="border: none">
                       <c:url value="/spring/group/editor/home" var="groupEditorUrl">
                         <c:param name="groupName" value="${group.fullName}"/>
                       </c:url>
        
                        <a href="${groupEditorUrl}">${fn:escapeXml(group.fullName)}</a>
                    </td>
                    <td style="border: none; width: 15px; text-align: center;">
                        <c:choose>
                            <c:when test="${group.modifiable}">
                            <tags:widgetLink method="remove" title="Remove" labelBusy="Removing" groupId="${group.id}" container="currentGroups">
                                <img class="cssicon" src="<c:url value="/WebConfig/yukon/Icons/clearbits/close.gif"/>">
                            </tags:widgetLink>
                            </c:when>
                            <c:otherwise>
                                <img class="graycssicon" title="Cannot remove device from group" src="<c:url value="/WebConfig/yukon/Icons/clearbits/close.gif"/>">
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
                
                </c:if>
            </c:forEach>
</table>
        </c:when>
        <c:otherwise>
            No Groups
        </c:otherwise>
    </c:choose>