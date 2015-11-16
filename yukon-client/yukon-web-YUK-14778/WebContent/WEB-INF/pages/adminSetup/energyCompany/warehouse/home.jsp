<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="adminSetup" page="warehouse.home">
    <tags:boxContainer2 nameKey="pageName" styleClass="fixedMediumWidth">
        <c:if test="${empty warehouses}">
            <span class="empty-list"><i:inline key="yukon.common.none.choice"/></span>
        </c:if>
        <c:if test="${!empty warehouses}">
            <table class="compact-results-table row-highlighting">
                <c:forEach items="${warehouses}" var="warehouse">
                <tr>
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
                        <br>
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
        </c:if>

        <div class="action-area">
            <cti:url var="createWarehouseUrl" value="${baseUrl}/new">
                <cti:param name="ecId" value="${ecId}"/>
            </cti:url>
            <cti:button nameKey="create" icon="icon-plus-green" href="${createWarehouseUrl}" name="createWarehouse" />
        </div>
    </tags:boxContainer2>
</cti:standardPage>
