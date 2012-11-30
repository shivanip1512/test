<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.common.device.bulk.deviceSelection.pageTitle" />
<cti:standardPage title="${pageTitle}" module="amr">
<cti:standardMenu/>

    <%-- BREAD CRUMBS --%>
    <cti:breadCrumbs>
        
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        
        <%-- bulk home --%>
        <cti:msg var="bulkOperationsPageTitle" key="yukon.common.device.bulk.bulkHome.pageTitle"/>
        <cti:crumbLink url="/bulk/bulkHome" title="${bulkOperationsPageTitle}" />
        
        <%-- device selection --%>
        <cti:msg var="deviceSelectionPageTitle" key="yukon.common.device.bulk.deviceSelection.pageTitle"/>
        <cti:crumbLink>${deviceSelectionPageTitle}</cti:crumbLink>
        
    </cti:breadCrumbs>
    
    <h2>${pageTitle}</h2>
    <br>
    
    <c:if test="${not empty errorMsg}">
            <div class="error">${errorMsg}</div>
        <br>
    </c:if>
    
    <cti:msg var="headerTitle" key="yukon.common.device.bulk.deviceSelection.header"/>
    <tags:boxContainer title="${headerTitle}" id="updateConfirmContainer" hideEnabled="false">
        
        <cti:deviceGroupHierarchyJson predicates="NON_HIDDEN" var="groupDataJson"/>
        <tags:deviceSelection action="/bulk/deviceSelectionGetDevices"
            groupDataJson="${groupDataJson}"
            pickerType="devicePicker"/>
    
    </tags:boxContainer>
    
</cti:standardPage>