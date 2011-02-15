<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="operator" page="warehouse.HOME">

        <cti:dataGrid cols="1" tableClasses="energyCompanyOperationsLayout">
        
            <%-- RIGHT SIDE COLUMN --%>
            <cti:dataGridCell>
                <tags:boxContainer2 nameKey="pageName">
                    
                    <table class="compactResultsTable rowHighlighting">
                        <c:forEach items="${warehouses}" var="warehouse">
                        <tr class="<tags:alternateRow odd="" even="altRow"/>">
                            <td style="width:30%">
                                <cti:url var="warehouseViewUrl" value="${baseUrl}/view">
                                    <cti:param name="ecId" value="${ecId}"/>
                                    <cti:param name="warehouseId" value="${warehouse.warehouse.warehouseID}"/>
                                </cti:url>
                                <b>
                                    <a href="${warehouseViewUrl}">
                                        <spring:escapeBody htmlEscape="true">${warehouse.warehouse.warehouseName}</spring:escapeBody>
                                    </a>
                                </b>
                                <br/>
                                <span class="meta">
                                    <tags:liteAddress address="${warehouse.address}"></tags:liteAddress>
                                </span>
                            </td>
                            <td>
                                <spring:escapeBody htmlEscape="true">${warehouse.warehouse.notes}</spring:escapeBody>
                            </td>
                        </tr>
                        </c:forEach>
                    </table>
                    
                </tags:boxContainer2>
                
                <cti:checkRolesAndProperties value="ADMIN_MULTI_WAREHOUSE">
                    <div class="actionArea">
                        <cti:url var="createWarehouseUrl" value="${baseUrl}/new">
                            <cti:param name="ecId" value="${ecId}"/>
                        </cti:url>
                        <cti:button key="add" onclick="javascript:window.location='${createWarehouseUrl}'"/>
                    </div>
                </cti:checkRolesAndProperties>
                
            </cti:dataGridCell>
        </cti:dataGrid>
    
</cti:standardPage>