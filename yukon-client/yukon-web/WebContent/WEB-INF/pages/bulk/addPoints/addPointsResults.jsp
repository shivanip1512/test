<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.common.device.bulk.addPointsResults.pageTitle"/>

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
        
        <%-- add points --%>
        <cti:url var="addPointsHomeUrl" value="/bulk/addPoints/home">
            <cti:mapParam value="${deviceCollection.collectionParameters}"/>
            <cti:param name="sharedPoints">${sharedPoints}</cti:param>
            <cti:param name="restorePoints">${restorePoints}</cti:param>
            <cti:param name="maskMissingPoints">${maskMissingPoints}</cti:param>
        </cti:url>
        
        <cti:msg var="addPointsHomePageTitle" key="yukon.common.device.bulk.addPointsHome.pageTitle"/>
        <cti:crumbLink url="${addPointsHomeUrl}" title="${addPointsHomePageTitle}" />
        
        <%-- add points results --%>
        <cti:crumbLink>${pageTitle}</cti:crumbLink>
        
    </cti:breadCrumbs>
    
    <h2>${pageTitle}</h2>
    <br>
    
    <cti:msg var="headerTitle" key="yukon.common.device.bulk.addPointsResults.header"/>
    <tags:boxContainer title="${headerTitle}" id="addPointsResultsContainer" hideEnabled="false">
    
        <%-- RESULTS --%>
        <tags:backgroundProcessResultHolder resultsTypeMsgKey="addPoints"
                                     callbackResult="${callbackResult}" />
        
    
    </tags:boxContainer>
    
</cti:standardPage>