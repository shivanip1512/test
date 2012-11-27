<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.common.device.bulk.changeDeviceTypeChoose.pageTitle"/>

<cti:standardPage title="${pageTitle}" module="amr">

    <cti:standardMenu menuSelection="" />

    <%-- BREAD CRUMBS --%>
    <cti:breadCrumbs>
    
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        
        <%-- bulk home --%>
        <cti:msg var="bulkOperationsPageTitle" key="yukon.common.device.bulk.bulkHome.pageTitle"/>
        <cti:crumbLink url="/bulk/bulkHome" title="${bulkOperationsPageTitle}" />
        
        <%-- device selection --%>
        <cti:msg var="deviceSelectionPageTitle" key="yukon.common.device.bulk.deviceSelection.pageTitle"/>
        <cti:crumbLink url="/bulk/deviceSelection" title="${deviceSelectionPageTitle}"/>
        
        <%-- collection actions --%>
        <tags:collectionActionsCrumbLink deviceCollection="${deviceCollection}" />
        
        <%-- mass delete --%>
        <cti:crumbLink>${pageTitle}</cti:crumbLink>
        
    </cti:breadCrumbs>
    
    <%-- TITLE --%>
    <h2>${pageTitle}</h2>
    <br>
    
    <%-- BOX --%>
    <tags:bulkActionContainer   key="yukon.common.device.bulk.changeDeviceTypeChoose" 
                                deviceCollection="${deviceCollection}">
    
    
        <form id="changeTypeForm" method="post" action="/bulk/changeDeviceType/changeDeviceType">
        
            <%-- DEVICE COLLECTION --%>
            <cti:deviceCollection deviceCollection="${deviceCollection}" />
            
            <%-- AVAILABLE TYPES --%>
            <select id="deviceType" name="deviceTypes">
                <c:forEach var="deviceType" items="${deviceTypes}">
                    <option value="${deviceType.value}">${deviceType.key}</option>
                </c:forEach>
            </select>
            <br><br>
            
            <%-- DELETE BUTTONS --%>
            <cti:msg var="change" key="yukon.common.device.bulk.changeDeviceTypeChoose.change" />
            <input type="submit" name="changeButton" value="${change}">
            <br>
            
        </form>
            
    </tags:bulkActionContainer>
    
</cti:standardPage>