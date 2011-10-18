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
            <i:inline key=".none"/>
        </c:if>
        <c:if test="${!empty warehouses}">
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

        <div class="actionArea">
            <form action="${baseUrl}/new">
                <input type="hidden" name="ecId" value="${ecId}"/>
                <cti:button nameKey="create" type="submit" name="create"/>
            </form>
        </div>
    </tags:boxContainer2>
</cti:standardPage>
