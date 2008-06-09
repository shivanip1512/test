<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.common.device.bulk.massDeleteConfirm.pageTitle"/>

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
        
        <%-- mass delete --%>
        <cti:crumbLink>${pageTitle}</cti:crumbLink>
        
    </cti:breadCrumbs>
    
    <%-- TITLE --%>
    <h2>${pageTitle}</h2>
    <br>
    
    <%-- BOX --%>
    <tags:bulkActionContainer   titleKey="yukon.common.device.bulk.massDeleteConfirm.header" 
                            noteLabelKey="yukon.common.device.bulk.massDeleteConfirm.noteLabel"
                            noteLabelStyle="color:#CC0000;"
                            noteTextKey="yukon.common.device.bulk.massDeleteConfirm.noteText"
                            noteTextArguments="${deviceCount}"
                            deviceCollection="${deviceCollection}">
    
    
        <form id="massDeleteForm" method="post" action="/spring/bulk/massChange/doMassDelete">
        
            <%-- DEVICE COLLECTION --%>
            <cti:deviceCollection deviceCollection="${deviceCollection}" />
            
            <%-- DELETE OR CANCEL BUTTONS --%>
            <cti:msg var="cancel" key="yukon.common.device.bulk.massDeleteConfirm.cancel" />
            <cti:msg var="delete" key="yukon.common.device.bulk.massDeleteConfirm.delete" />
            <input type="submit" name="cancelButton" value="${cancel}"> <input type="submit" name="deleteButton" value="${delete}">
            <br>
            
        </form>
            
    </tags:bulkActionContainer>
    
</cti:standardPage>