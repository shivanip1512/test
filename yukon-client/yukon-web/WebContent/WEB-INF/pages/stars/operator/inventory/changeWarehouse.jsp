<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="operator" page="changeWarehouse">
    <cti:includeCss link="/WebConfig/yukon/styles/operator/inventory.css"/>
    
    <tags:boxContainer2 nameKey="changeWarehouseContainer" hideEnabled="false">
    
        <div class="containerHeader">
            <table>
                <tr>
                    <td valign="top" colspan="2">
                        <tags:selectedInventory inventoryCollection="${inventoryCollection}" id="inventoryCollection"/>
                    </td>
                </tr>
          
                <tr>
                    <td class="smallBoldLabel errorMessage"><i:inline key=".instructionsLabel"/></td>
                    <td><i:inline key=".instructions"/></td>
                </tr>
            </table>
        </div>
    
        <br>
        
        <c:if test="${empty task}">
            <form action="do" method="post">
                <cti:inventoryCollection inventoryCollection="${inventoryCollection}"/>
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".newWarehouse">
                        <select name="warehouseId">
                            <option value="0" selected/><cti:msg2 key="yukon.web.defaults.none"/></option> 
                            <c:forEach items="${warehouses}" var="wh">
                                <option value="${wh.warehouseID}"><spring:escapeBody htmlEscape="true">${wh.warehouseName}</spring:escapeBody></option>
                            </c:forEach>
                        </select>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
                <div class="pageActionArea">
                    <cti:button nameKey="cancel" type="submit" name="cancel"/>
                    <cti:button nameKey="start" type="submit" name="start"/>
                </div>
            </form>
        </c:if>
        <c:if test="${not empty task}">
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".progress">
                    <tags:updateableProgressBar totalCount="${task.totalItems}" countKey="INVENTORY_TASK/${task.taskId}/ITEMS_PROCESSED"/>
                </tags:nameValue2>
            </tags:nameValueContainer2>
        
            <br>
            
            <a href="/spring/stars/operator/inventory/home"><i:inline key=".inventoryHome"/></a>
        </c:if>
     </tags:boxContainer2>
</cti:standardPage>