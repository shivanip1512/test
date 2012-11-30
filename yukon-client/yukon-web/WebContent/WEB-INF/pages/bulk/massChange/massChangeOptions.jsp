<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<cti:msg var="pageTitle" key="yukon.common.device.bulk.massChangeOptions.pageTitle"/>

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
        
        <%-- mass change options --%>
        <cti:url var="massChangeSelectUrl" value="/bulk/massChange/massChangeSelect">
            <cti:param name="selectedBulkFieldName" value="${massChangeBulkFieldName}"/>
            <c:forEach var="deviceCollectionParam" items="${deviceCollection.collectionParameters}">
                <cti:param name="${deviceCollectionParam.key}" value="${deviceCollectionParam.value}"/>
            </c:forEach>
        </cti:url>
        <cti:msg var="massChangePageTitle" key="yukon.common.device.bulk.massChangeSelect.pageTitle"/>
        <cti:crumbLink url="${massChangeSelectUrl}" title="${massChangePageTitle}" />
        
        <%-- mass change options --%>
        <cti:crumbLink>${massChangeOptionsPageTitle}</cti:crumbLink>
        
    </cti:breadCrumbs>
    
    <%-- TITLE --%>
    <h2>${pageTitle}</h2>
    <br>
    
    <%-- BOX --%>
    <tags:bulkActionContainer   key="yukon.common.device.bulk.massChangeOptions.${massChangeBulkFieldName}" 
                                deviceCollection="${deviceCollection}">
                                
        <c:if test="${not empty statusMsg}">
            <div class="fwb success">${statusMsg}</div>
            <br>
        </c:if>
        
        <form id="massChangeOptionsForm" method="post" action="/bulk/massChangeOptions">
        <spring:nestedPath path="massChangeOptions">
        
            <%-- DEVICE COLLECTION --%>
            <cti:deviceCollection deviceCollection="${deviceCollection}" />
            
            <%-- NAME OF FIELD --%>
            <input type="hidden" id="massChangeBulkFieldName" name="massChangeBulkFieldName" value="${massChangeBulkFieldName}">
            
            <%-- INCLUDE JSP FOR SELECTED CHANGE FIELD --%>
            <c:forEach var="input" items="${inputRoot.inputList}">
                <cti:renderInput input="${input}" />
            </c:forEach>
            
            <%-- SUBMIT --%>
            <tags:slowInput myFormId="massChangeOptionsForm" labelBusy="Submit" label="Submit" />
            
        </spring:nestedPath>
        </form>
    
    </tags:bulkActionContainer>
    
</cti:standardPage>