<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.common.device.bulk.assignConfigResults.pageTitle"/>

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
        
        <cti:url var="assignConfigUrl" value="/bulk/config/assignConfig">
            <c:forEach var="deviceCollectionParam" items="${deviceCollection.collectionParameters}">
                <cti:param name="${deviceCollectionParam.key}" value="${deviceCollectionParam.value}"/>
            </c:forEach>
        </cti:url>
        <cti:msg var="assignConfigPageTitle" key="yukon.common.device.bulk.assignConfig.pageTitle"/>
        <cti:crumbLink url="${assignConfigUrl}" title="${assignConfigPageTitle}" />
        
        <%-- assign config results --%>
        <cti:crumbLink>${pageTitle}</cti:crumbLink>
        
    </cti:breadCrumbs>
    
    <h2>${pageTitle}</h2>
    <br>
    
    <cti:msg var="headerTitle" key="yukon.common.device.bulk.assignConfigResults.header"/>
    <tags:boxContainer title="${headerTitle}" id="assignConfigResultsContainer" hideEnabled="false">
    
        <%-- RESULTS --%>
        <tags:backgroundProcessResultHolder resultsTypeMsgKey="assignConfig" callbackResult="${callbackResult}" />
        
    </tags:boxContainer>
    
</cti:standardPage>