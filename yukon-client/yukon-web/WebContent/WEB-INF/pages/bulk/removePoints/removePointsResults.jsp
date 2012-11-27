<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.common.device.bulk.removePointsResults.pageTitle"/>

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
        
        <%-- remove points --%>
        <cti:url var="removePointsHomeUrl" value="/bulk/removePoints/home">
            <cti:mapParam value="${deviceCollection.collectionParameters}"/>
            <cti:param name="sharedPoints">${sharedPoints}</cti:param>
        </cti:url>
        
        <cti:msg var="removePointsHomePageTitle" key="yukon.common.device.bulk.removePointsHome.pageTitle"/>
        <cti:crumbLink url="${removePointsHomeUrl}" title="${removePointsHomePageTitle}" />
        
        <%-- remove points results --%>
        <cti:crumbLink>${pageTitle}</cti:crumbLink>
        
    </cti:breadCrumbs>
    
    <h2>${pageTitle}</h2>
    <br>
    
    <cti:msg var="headerTitle" key="yukon.common.device.bulk.removePointsResults.header"/>
    <tags:boxContainer title="${headerTitle}" id="removePointsResultsContainer" hideEnabled="false">
    
        <%-- RESULTS --%>
        <tags:backgroundProcessResultHolder resultsTypeMsgKey="removePoints"
                                     callbackResult="${callbackResult}" />
        
    
    </tags:boxContainer>
    
</cti:standardPage>