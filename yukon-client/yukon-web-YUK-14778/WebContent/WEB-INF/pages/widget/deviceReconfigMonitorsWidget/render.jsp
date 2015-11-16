<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:choose>
    <c:when test="${empty tasks}">
        <div class="empty-list"><i:inline key=".noTasks"/></div>
    </c:when>
    <c:otherwise>
        <table class="compact-results-table dashed">
            <thead>
                <tr>
                    <th><i:inline key=".taskName"/></th>
                    <th><i:inline key=".status"/></th>
                    <th><i:inline key=".deviceCount"/></th>
                </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach var="task" items="${tasks}">
                    <cti:url value="/stars/operator/inventory/deviceReconfig/status" var="url">
                        <cti:param name="taskId" value="${task.inventoryConfigTaskId}"/>
                    </cti:url>
                    <tr>
                        <td>
                            <a href="${url}" title="${fn:escapeXml(task.taskName)}">${fn:escapeXml(task.displayName)}</a>
                        </td>
                        
                        <td>
                            <cti:classUpdater type="DEVICE_RECONFIG" 
                                    identifier="${task.inventoryConfigTaskId}/STATUS_CLASS">
                                <cti:dataUpdaterValue type="DEVICE_RECONFIG" 
                                        identifier="${task.inventoryConfigTaskId}/STATUS_TEXT"/>&nbsp;
                                <cti:dataUpdaterValue type="DEVICE_RECONFIG" 
                                    identifier="${task.inventoryConfigTaskId}/PROGRESS"/>
                            </cti:classUpdater>
                        </td>
                        <td>${task.numberOfItems}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:otherwise>
</c:choose>