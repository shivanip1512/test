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
        <cti:crumbLink url="/spring/bulk/bulkHome" title="${bulkOperationsPageTitle}" />
        
        <%-- device selection --%>
        <cti:msg var="deviceSelectionPageTitle" key="yukon.common.device.bulk.deviceSelection.pageTitle"/>
        <cti:crumbLink>${deviceSelectionPageTitle}</cti:crumbLink>
        
    </cti:breadCrumbs>
    
    <h2>${pageTitle}</h2>
    <br>
    
    <cti:msg var="headerTitle" key="yukon.common.device.bulk.deviceSelection.header"/>
    <tags:boxContainer title="${headerTitle}" id="updateConfirmContainer" hideEnabled="false">
    
        <tags:deviceSelection action="/spring/bulk/collectionActions" groupDataJson="${groupDataJson}" pickerConstraint="com.cannontech.common.search.criteria.MeterCriteria"/>
    
    </tags:boxContainer>
    
</cti:standardPage>