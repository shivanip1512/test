<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="deleteInventory">

    <div class="notes stacked">
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
        <div class="page-action-area">
            <form action="delete" method="post">
                <cti:csrfToken/>
                <cti:inventoryCollection inventoryCollection="${inventoryCollection}"/>
                <cti:button nameKey="start" type="submit" name="start" classes="primary action"/>
                <cti:button nameKey="cancel" type="submit" name="cancel"/>
            </form>
        </div>
    </c:if>
    <c:if test="${not empty task}">
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".progress">
                <tags:updateableProgressBar totalCount="${task.totalItems}" countKey="INVENTORY_TASK/${task.taskId}/ITEMS_PROCESSED"/>
            </tags:nameValue2>
        </tags:nameValueContainer2>
    
    </c:if>
</cti:standardPage>