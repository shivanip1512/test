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
        <cti:crumbLink url="/bulk/bulkHome" title="${bulkOperationsPageTitle}" />
        
        <%-- device selection --%>
        <cti:msg var="deviceSelectionPageTitle" key="yukon.common.device.bulk.deviceSelection.pageTitle"/>
        <cti:crumbLink url="/bulk/deviceSelection" title="${deviceSelectionPageTitle}"/>
        
        <%-- collection actions --%>
        <tags:collectionActionsCrumbLink deviceCollection="${deviceCollection}" />
        
        <%-- choose change type --%>
        <cti:url var="changeDeviceTypeUrl" value="/bulk/changeDeviceType/chooseDeviceType">
            <c:forEach var="deviceCollectionParam" items="${deviceCollection.collectionParameters}">
                <cti:param name="${deviceCollectionParam.key}" value="${deviceCollectionParam.value}"/>
            </c:forEach>
        </cti:url>
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
        <tags:backgroundProcessResultHolder resultsTypeMsgKey="changeDeviceType"
                                     callbackResult="${callbackResult}" />
        
    
    </tags:boxContainer>
    
</cti:standardPage>