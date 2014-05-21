<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="changeType">
    
    <div class="note">
        <table>
            <tr>
                <td valign="top" colspan="2">
                    <tags:selectedInventory inventoryCollection="${inventoryCollection}" id="inventoryCollection"/>
                </td>
            </tr>
      
            <tr>
                <td class="strong-label-small error"><i:inline key=".instructionsLabel"/></td>
                <td><i:inline key=".instructions"/></td>
            </tr>
        </table>
    </div>

    
    <c:if test="${empty task}">
        <form action="do" method="post">
            <cti:csrfToken/>
            <cti:inventoryCollection inventoryCollection="${inventoryCollection}"/>
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".newType">
                    <select name="entry">
                        <c:forEach items="${validEntries}" var="entry">
                            <option value="${entry.entryID}">${fn:escapeXml(entry.entryText)}</option>
                        </c:forEach>
                    </select>
                </tags:nameValue2>
            </tags:nameValueContainer2>
            <div class="page-action-area">
                <cti:button nameKey="start" type="submit" name="start" classes="primary action"/>
                <cti:button nameKey="cancel" type="submit" name="cancel"/>
            </div>
        </form>
    </c:if>
    <c:if test="${not empty task}">
        
        <cti:url var="newOperationSuccess" value="newOperation">
            <cti:param name="type" value="SUCCESS"/>
            <cti:param name="taskId" value="${task.taskId}"/>
        </cti:url>
        <cti:url var="newOperationUnsupported" value="newOperation">
            <cti:param name="type" value="UNSUPPORTED"/>
            <cti:param name="taskId" value="${task.taskId}"/>
        </cti:url>
        
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".progress">
                <tags:updateableProgressBar totalCount="${task.totalItems}" countKey="INVENTORY_TASK/${task.taskId}/ITEMS_PROCESSED"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".successful">
                <cti:dataUpdaterValue type="INVENTORY_TASK" identifier="${task.taskId}/SUCCESS_COUNT" styleClass="success fwb"/>&nbsp;
                <cti:classUpdater type="INVENTORY_TASK" identifier="${task.taskId}/NEW_OPERATION_FOR_SUCCESS">
                    <a href="${newOperationSuccess}"><i:inline key=".newOperation"/></a>
                </cti:classUpdater>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".unsupported">
                <cti:dataUpdaterValue type="INVENTORY_TASK" identifier="${task.taskId}/UNSUPPORTED_COUNT" styleClass="warning fwb"/>&nbsp;
                <cti:classUpdater type="INVENTORY_TASK" identifier="${task.taskId}/NEW_OPERATION_FOR_UNSUPPORTED">
                    <a href="${newOperationUnsupported}"><i:inline key=".newOperation"/></a>
                </cti:classUpdater>
            </tags:nameValue2>
        </tags:nameValueContainer2>
        
    </c:if>
</cti:standardPage>