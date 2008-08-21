<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.common.device.bulk.changeDeviceTypeResults.pageTitle"/>

<cti:standardPage title="${pageTitle}" module="amr">

    <cti:standardMenu menuSelection="" />

    <%-- BREAD CRUMBS --%>
    <cti:breadCrumbs>
        
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        
        <%-- bulk home --%>
        <cti:msg var="bulkOperationsPageTitle" key="yukon.common.device.bulk.bulkHome.pageTitle"/>
        <cti:crumbLink url="/spring/bulk/bulkHome" title="${bulkOperationsPageTitle}" />
        
        <%-- device selection --%>
        <cti:msg var="deviceSelectionPageTitle" key="yukon.common.device.bulk.deviceSelection.pageTitle"/>
        <cti:crumbLink url="/spring/bulk/deviceSelection" title="${deviceSelectionPageTitle}"/>
        
        <%-- collection actions --%>
        <c:url var="collectionActionsUrl" value="/spring/bulk/collectionActions">
            <c:forEach var="deviceCollectionParam" items="${deviceCollection.collectionParameters}">
                <c:param name="${deviceCollectionParam.key}" value="${deviceCollectionParam.value}"/>
            </c:forEach>
        </c:url>
        <cti:msg var="collectionActionsPageTitle" key="yukon.common.device.bulk.collectionActions.pageTitle"/>
        <cti:crumbLink url="${collectionActionsUrl}" title="${collectionActionsPageTitle}" />
        
        <%-- choose change type --%>
        <c:url var="changeDeviceTypeUrl" value="/spring/bulk/changeDeviceType/chooseDeviceType">
            <c:forEach var="deviceCollectionParam" items="${deviceCollection.collectionParameters}">
                <c:param name="${deviceCollectionParam.key}" value="${deviceCollectionParam.value}"/>
            </c:forEach>
        </c:url>
        <cti:msg var="changeDeviceTypePageTitle" key="yukon.common.device.bulk.changeDeviceTypeChoose.pageTitle"/>
        <cti:crumbLink url="${changeDeviceTypeUrl}" title="${changeDeviceTypePageTitle}" />
        
        <%-- change type results --%>
        <cti:crumbLink>${pageTitle}</cti:crumbLink>
        
    </cti:breadCrumbs>
    
    <h2>${pageTitle}</h2>
    <br>
    
    <cti:msg var="headerTitle" key="yukon.common.device.bulk.changeDeviceTypeResults.header"/>
    <tags:boxContainer title="${headerTitle}" id="changeDeviceTypeResultsContainer" hideEnabled="false">
    
        <%-- RESULTS --%>
        <tags:bulkUpdateResultsTable    resultsTypeMsgKey="changeDeviceType"
                                        totalCount="${deviceCollection.deviceCount}"
                                        bulkUpdateOperationResults="${bulkUpdateOperationResults}"
                                        isMassChange="true"
                                        isMassDelete="false" />
        
    
    </tags:boxContainer>
    
</cti:standardPage>