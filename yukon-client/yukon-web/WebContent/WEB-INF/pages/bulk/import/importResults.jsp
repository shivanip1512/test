<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.common.device.bulk.importResults.pageTitle"/>

<cti:standardPage title="${pageTitle}" module="amr">

    <cti:standardMenu menuSelection="" />
    
    <%-- BREAD CRUMBS --%>
    <cti:breadCrumbs>
        
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        
        <%-- bulk home --%>
        <cti:msg var="bulkOperationsPageTitle" key="yukon.common.device.bulk.bulkHome.pageTitle"/>
        <cti:crumbLink url="/bulk/bulkHome" title="${bulkOperationsPageTitle}" />
        
        <%-- import --%>
        <cti:msg var="importUploadPageTitle" key="yukon.common.device.bulk.importUpload.pageTitle"/>
        <cti:crumbLink url="/bulk/import/upload" title="${importUploadPageTitle}" />
        
        <%-- results --%>
        <cti:crumbLink>${pageTitle}</cti:crumbLink>
        
    </cti:breadCrumbs>
    
    <h2>${pageTitle}</h2>
    <br>
    

    <cti:msg var="headerTitle" key="yukon.common.device.bulk.importResults.header"/>
    <tags:boxContainer title="${headerTitle}" id="importResultsContainer" hideEnabled="false">
    
            
           <%-- RESULTS --%>
           <tags:backgroundProcessResultHolder resultsTypeMsgKey="import"
                                        callbackResult="${callbackResult}" />
            
            
            <%-- BACK BUTTON --%>
            <br>
            <form id="importResultsForm" type="post" action="/bulk/import/upload">
                
                <cti:msg var="backToUploadButton" key="yukon.common.device.bulk.importResults.backToUpload"/>
                <br>
                <input type="submit" value="${backToUploadButton}" class="formSubmit">
                
                <%-- PASS ALONGS --%>
                <input type="hidden" name="ignoreInvalidCols" value="${ignoreInvalidCols}">
            
            </form>
        
    </tags:boxContainer>
    
 </cti:standardPage>