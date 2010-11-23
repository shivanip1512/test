<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<c:set var="removeImg" value="/WebConfig/yukon/Icons/delete.gif"/>
<c:set var="removeImgHover" value="/WebConfig/yukon/Icons/delete_over.gif"/>

<c:choose>
    <c:when test="${empty monitors}">

    </c:when>

    <c:otherwise>
        <table class="compactResultsTable">
    
            <tr>
                <th><i:inline key=".monitorName"/></th>
                <th><i:inline key=".status"/></th>
                <th nowrap="nowrap"><i:inline key=".deviceCount"/></th>
                <th class="removeColumn"><i:inline key=".remove"/></th>
            </tr>
        
            <c:forEach var="monitor" items="${monitors}">
            
                <tr>
                    
                    <td>
                        <spring:escapeBody htmlEscape="true">${monitor.name}</spring:escapeBody>
                    </td>
                    
                    <td nowrap="nowrap">
                        <cti:dataUpdaterValue type="DEVICE_RECONFIG" identifier="${monitor.id}/PROGRESS"/>
                    </td>
                    
                    <td>${monitor.deviceCount}</td>
                    
                    <td class="removeColumn">
                        <cti:msg2 key=".delete" var="deleteTitle" argument="${monitor.name}"/>
                        <cti:msg2 key=".confirmDelete" var="confirmText" argument="${monitor.name}"/>
                        <tags:widgetActionRefreshImage method="delete" imgSrc="${removeImg}" imgSrcHover="${removeImgHover}" monitorId="${monitor.id}" confirmText="${confirmText}" title="${deleteTitle}"/>
                    </td>
                    
                </tr>
            
            </c:forEach>
        
        </table>
    </c:otherwise>
</c:choose>