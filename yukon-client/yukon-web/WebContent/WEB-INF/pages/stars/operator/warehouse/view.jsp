<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="operator" page="warehouse.${mode}">
    
    <tags:formElementContainer nameKey="viewContainer">
        <span class="meta">
            <tags:liteAddress address="${warehouseDto.address}"/>
        </span>
        <br/>
        <br/>
        <c:if test="${ not empty warehouseDto.warehouse.notes}">
            <spring:escapeBody htmlEscape="true">${warehouseDto.warehouse.notes}</spring:escapeBody>
            <br/>
            <br/>
        </c:if>
    </tags:formElementContainer>
    <cti:checkRolesAndProperties value="ADMIN_MULTI_WAREHOUSE">
        <cti:url var="warehouseEditUrl" value="${baseUrl}/edit">
            <cti:param name="ecId" value="${ecId}"/>
            <cti:param name="warehouseId" value="${warehouseDto.warehouse.warehouseID}"/>
        </cti:url>
        <cti:button key="edit" onclick="javascript:window.location ='${warehouseEditUrl}'"/>
    </cti:checkRolesAndProperties>
    
    <cti:url var="warehouseIndexUrl" value="${baseUrl}/home">
        <cti:param name="ecId" value="${ecId}"/>
    </cti:url>
    <cti:button key="cancel" onclick="javascript:window.location ='${warehouseIndexUrl}'"/>
    
</cti:standardPage>