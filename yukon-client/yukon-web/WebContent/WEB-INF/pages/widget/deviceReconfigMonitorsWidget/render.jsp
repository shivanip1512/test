<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<c:set var="removeImg" value="/WebConfig/yukon/Icons/delete.gif"/>
<c:set var="removeImgHover" value="/WebConfig/yukon/Icons/delete_over.gif"/>

<c:choose>
    <c:when test="${empty tasks}">
        <div>
            <i:inline key=".noTasks"/>
        </div>
    </c:when>

    <c:otherwise>
        <table class="compactResultsTable">
    
            <tr>
                <th><i:inline key=".taskName"/></th>
                <th><i:inline key=".status"/></th>
                <th nowrap="nowrap"><i:inline key=".deviceCount"/></th>
                <th class="removeColumn"><i:inline key=".remove"/></th>
            </tr>
        
            <c:forEach var="task" items="${tasks}">
                
                <cti:url value="/spring/stars/operator/inventory/inventoryOperations/deviceReconfig/status" var="statusUrl">
                    <cti:param name="taskId" value="${task.inventoryConfigTaskId}"/>
                </cti:url>
                
                <tr>
                    
                    <td>
                        <a href="${statusUrl}">
                            <spring:escapeBody htmlEscape="true">${task.taskName}</spring:escapeBody>
                        </a>
                    </td>
                    
                    <td nowrap="nowrap">
                        <cti:classUpdater type="DEVICE_RECONFIG" identifier="${task.inventoryConfigTaskId}/STATUS_CLASS">
                            <cti:dataUpdaterValue type="DEVICE_RECONFIG" identifier="${task.inventoryConfigTaskId}/PROGRESS"/>
                        </cti:classUpdater>
                    </td>
                    
                    <td>${task.numberOfItems}</td>
                    
                    <td class="removeColumn">
                        <cti:msg2 key=".delete" var="deleteTitle" argument="${task.taskName}"/>
                        <cti:msg2 key=".confirmDelete" var="confirmText" argument="${task.taskName}"/>
                        <tags:widgetActionRefreshImage method="delete" imgSrc="${removeImg}" imgSrcHover="${removeImgHover}" taskId="${task.inventoryConfigTaskId}" confirmText="${confirmText}" title="${deleteTitle}"/>
                    </td>
                    
                </tr>
            
            </c:forEach>
        
        </table>
    </c:otherwise>
</c:choose>