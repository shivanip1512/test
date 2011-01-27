<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="operator" page="warehouse.HOME">
    <cti:includeCss link="/WebConfig/yukon/styles/operator/energyCompany.css"/>
    
    <h1>${warehouse.warehouse.warehouseName}</h1>
    
    <span class="meta">
        <tags:liteAddress address="${warehouse.address}"/>
    </span>
    <br/>
    <br/>
    ${warehouse.warehouse.notes}
    <br/>
    <br/>
    <cti:checkRolesAndProperties value="ADMIN_MULTI_WAREHOUSE">
        <cti:url var="warehouseEditUrl" value="${editUrl}">
            <cti:param name="ecId" value="${ecId}"/>
            <cti:param name="warehouseId" value="${warehouse.warehouse.warehouseID}"/>
        </cti:url>
        <cti:button key="edit" onclick="javascript:window.location ='${warehouseEditUrl}'"/>
        
        <cti:url var="warehouseDeleteUrl" value="${deleteUrl}">
            <cti:param name="ecId" value="${ecId}"/>
            <cti:param name="warehouseId" value="${warehouse.warehouse.warehouseID}"/>
        </cti:url>
        <cti:button key="delete" onclick="javascript:window.location ='${warehouseDeleteUrl}'"/>
    </cti:checkRolesAndProperties>
    
    <cti:url var="warehouseIndexUrl" value="${indexUrl}">
        <cti:param name="ecId" value="${ecId}"/>
    </cti:url>
    <cti:button key="cancel" onclick="javascript:window.location ='${warehouseIndexUrl}'"/>
    
</cti:standardPage>