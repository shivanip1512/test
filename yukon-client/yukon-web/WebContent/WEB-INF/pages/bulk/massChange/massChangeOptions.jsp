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
        
        <%-- mass change options --%>
        <c:url var="massChangeSelectUrl" value="/spring/bulk/massChange/massChangeSelect">
            <c:param name="selectedBulkFieldName" value="${massChangeBulkFieldName}"/>
            <c:forEach var="deviceCollectionParam" items="${deviceCollection.collectionParameters}">
                <c:param name="${deviceCollectionParam.key}" value="${deviceCollectionParam.value}"/>
            </c:forEach>
        </c:url>
        <cti:msg var="massChangePageTitle" key="yukon.common.device.bulk.massChangeSelect.pageTitle"/>
        <cti:crumbLink url="${massChangeSelectUrl}" title="${massChangePageTitle}" />
        
        <%-- mass change options --%>
        <cti:crumbLink>${massChangeOptionsPageTitle}</cti:crumbLink>
        
    </cti:breadCrumbs>
    
    <%-- TITLE --%>
    <h2>${pageTitle}</h2>
    <br>
    
    <%-- BOX --%>
    <tags:bulkActionContainer   titleKey="yukon.common.device.bulk.massChangeOptions.${massChangeBulkFieldName}.header" 
                                deviceCollection="${deviceCollection}">
                                
        <c:if test="${not empty statusMsg}">
            <div style="font-weight:bold;color:#006633;">${statusMsg}</div>
            <br>
        </c:if>
        
        <form id="massChangeOptionsForm" method="post" action="/spring/bulk/massChangeOptions">
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