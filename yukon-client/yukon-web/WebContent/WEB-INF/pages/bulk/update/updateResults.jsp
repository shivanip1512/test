<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.common.device.bulk.updateResults.pageTitle"/>

<cti:standardPage title="${pageTitle}" module="amr">

    <cti:standardMenu menuSelection="" />
    
    <cti:includeScript link="/JavaScript/bulkOperations.js"/>

    <%-- BREAD CRUMBS --%>
    <cti:breadCrumbs>
        
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        
        <%-- bulk home --%>
        <cti:msg var="bulkOperationsPageTitle" key="yukon.common.device.bulk.bulkHome.pageTitle"/>
        <cti:crumbLink url="/spring/bulk/bulkHome" title="${bulkOperationsPageTitle}" />
        
        <%-- upload --%>
        <cti:msg var="updateUploadPageTitle" key="yukon.common.device.bulk.updateUpload.pageTitle"/>
        <cti:crumbLink url="/spring/bulk/update/upload" title="${updateUploadPageTitle}" />
        
        <%-- results --%>
        <cti:crumbLink>${pageTitle}</cti:crumbLink>
        
    </cti:breadCrumbs>
    
    <h2>${pageTitle}</h2>
    <br>

    <cti:msg var="headerTitle" key="yukon.common.device.bulk.updateResults.header"/>
    <tags:boxContainer title="${headerTitle}" id="updateResultsContainer" hideEnabled="false">
        
           <%-- RESULTS --%>
           <tags:bulkUpdateResultsTable resultsTypeMsgKey="update"
                                        totalCount="${bulkUpdateOperationResults.bulkFileInfo.dataCount}"
                                        bulkUpdateOperationResults="${bulkUpdateOperationResults}" />
            
            
            <%-- BACK BUTTON --%>
            <br>
            <form id="updateResultsForm" type="post" action="/spring/bulk/update/upload">
                
                <cti:msg var="backToUploadButton" key="yukon.common.device.bulk.updateResults.backToUpload"/>
            
                <br>
                <input type="submit" value="${backToUploadButton}">
            
                <%-- PASS ALONGS --%>
                <input type="hidden" name="ignoreInvalidCols" value="${ignoreInvalidCols}">
                <input type="hidden" name="ignoreInvalidIdentifiers" value="${ignoreInvalidIdentifiers}">
            
            </form>
        
    </tags:boxContainer>
    
 </cti:standardPage>