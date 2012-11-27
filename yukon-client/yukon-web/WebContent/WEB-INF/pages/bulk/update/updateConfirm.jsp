<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.common.device.bulk.updateConfirm.pageTitle"/>

<cti:standardPage title="${pageTitle}" module="amr">

    <cti:standardMenu menuSelection="" />

    <%-- BREAD CRUMBS --%>
    <cti:breadCrumbs>
        
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        
        <%-- bulk home --%>
        <cti:msg var="bulkOperationsPageTitle" key="yukon.common.device.bulk.bulkHome.pageTitle"/>
        <cti:crumbLink url="/bulk/bulkHome" title="${bulkOperationsPageTitle}" />
        
        <%-- upload --%>
        <cti:msg var="updateUploadPageTitle" key="yukon.common.device.bulk.updateUpload.pageTitle"/>
        <cti:crumbLink url="/bulk/update/upload" title="${updateUploadPageTitle}" />
        
        <%-- confirm --%>
        <cti:crumbLink>${pageTitle}</cti:crumbLink>
        
    </cti:breadCrumbs>
    
    <h2>${pageTitle}</h2>
    <br>

    <cti:msg var="headerTitle" key="yukon.common.device.bulk.updateConfirm.header"/>
    <tags:boxContainer title="${headerTitle}" id="updateConfirmContainer" hideEnabled="false">
    
        <form id="updateConfirmForm" action="/bulk/update/doUpdate" method="post">
            
            <%-- CONFIRMATION INFO --%>
            <cti:msg var="rows" key="yukon.common.device.bulk.updateConfirm.rows"/>
            <cti:msg var="identifierColumn" key="yukon.common.device.bulk.updateConfirm.identifierColumn"/>
            <cti:msg var="fieldsToUpdate" key="yukon.common.device.bulk.updateConfirm.fieldsToUpdate"/>
            
            <table cellspacing="8" border="0">
                <tr valign="top">
                    <td class="normalBoldLabel">${rows}</td>
                    <td>${parsedResult.bulkFileInfo.dataCount}</td>
                </tr>
                <tr valign="top">
                    <td class="normalBoldLabel">${identifierColumn}</td>
                    <td>${parsedResult.identifierBulkFieldColumnHeader}</td>
                </tr>
                <tr valign="top">
                    <td class="normalBoldLabel">${fieldsToUpdate}</td>
                    <td>
                        <c:forEach var="bulkField" items="${parsedResult.updateBulkFieldColumnHeaders}">
                            ${bulkField}<br>
                        </c:forEach>
                    </td>
                </tr>
            </table>
                
            <%-- SUBMIT BUTTONS --%>
            <cti:msg var="updateButton" key="yukon.common.device.bulk.updateConfirm.updateButton"/>
            <cti:msg var="cancelButton" key="yukon.common.device.bulk.updateConfirm.cancelButton"/>
            <cti:url var="cancelUrl" value="/bulk/update/upload"/>
            
            <br>
            <input type="button" id="cancelButton" value="${cancelButton}" onclick="window.location='${cancelUrl}'"  class="formSubmit">
            <tags:slowInput myFormId="updateConfirmForm" label="${updateButton}" labelBusy="${updateButton}" />
            
            <%-- UPDATE INFO ID --%>
            <input type="hidden" name="fileInfoId" value="${parsedResult.bulkFileInfo.id}">
            
        </form>
        
    </tags:boxContainer>
    
 </cti:standardPage>