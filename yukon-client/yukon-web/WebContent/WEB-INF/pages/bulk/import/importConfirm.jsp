<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.common.device.bulk.importConfirm.pageTitle"/>

<cti:standardPage title="${pageTitle}" module="amr">

    <cti:standardMenu menuSelection="" />

    <%-- BREAD CRUMBS --%>
    <cti:breadCrumbs>
        
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        
        <%-- bulk home --%>
        <cti:msg var="bulkOperationsPageTitle" key="yukon.common.device.bulk.bulkHome.pageTitle"/>
        <cti:crumbLink url="/spring/bulk/bulkHome" title="${bulkOperationsPageTitle}" />
        
        <%-- import --%>
        <cti:msg var="importUploadPageTitle" key="yukon.common.device.bulk.importUpload.pageTitle"/>
        <cti:crumbLink url="/spring/bulk/import/upload" title="${importUploadPageTitle}" />
        
        <%-- confirm --%>
        <cti:crumbLink>${pageTitle}</cti:crumbLink>
        
    </cti:breadCrumbs>
    
    <h2>${pageTitle}</h2>
    <br>

    <cti:msg var="headerTitle" key="yukon.common.device.bulk.importConfirm.header"/>
    <tags:boxContainer title="${headerTitle}" id="importConfirmContainer" hideEnabled="false">
    
        <form id="importConfirmForm" action="/spring/bulk/import/doImport" method="post">

            <input type="hidden" value="${deviceType}" name="deviceType" />
            
            <%-- CONFIRMATION INFO --%>
            <cti:msg var="rows" key="yukon.common.device.bulk.importConfirm.rows"/>
            <cti:msg var="importMethodLabel" key="yukon.common.device.bulk.importConfirm.importMethodLabel"/>
            <cti:msg var="optionalFieldsLabel" key="yukon.common.device.bulk.importConfirm.optionalFieldsLabel"/>
            
            <table cellspacing="8" border="0">
                <tr valign="top">
                    <td class="normalBoldLabel">${rows}</td>
                    <td>${parsedResult.bulkFileInfo.dataCount}</td>
                </tr>
                <tr valign="top">
                    <td class="normalBoldLabel">${importMethodLabel}</td>
                    <td><cti:msg key="yukon.common.device.bulk.columnHeader.tableHeader.import.method.displayName.${parsedResult.bulkImportMethod.name}"/></td>
                </tr>
                <tr valign="top">
                    <td class="normalBoldLabel">${optionalFieldsLabel}</td>
                    <td>
                        <c:choose>
                            <c:when test="${not empty parsedResult.updateBulkFieldColumnHeaders}">
                                <c:forEach var="bulkField" items="${parsedResult.updateBulkFieldColumnHeaders}">
                                    ${bulkField}<br>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                None
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </table>
                
            <%-- SUBMIT BUTTONS --%>
            <cti:msg var="importButton" key="yukon.common.device.bulk.importConfirm.importButton"/>
            <cti:msg var="cancelButton" key="yukon.common.device.bulk.importConfirm.cancelButton"/>
            <cti:url var="cancelUrl" value="/spring/bulk/import/upload"/>
            
            <br>
            <input type="button" id="cancelButton" value="${cancelButton}" onclick="window.location='${cancelUrl}'" class="formSubmit">
            <tags:slowInput myFormId="importConfirmForm" label="${importButton}" labelBusy="${importButton}" />
            
            <%-- UPDATE INFO ID --%>
            <input type="hidden" name="fileInfoId" value="${parsedResult.bulkFileInfo.id}">
            
        </form>
        
    </tags:boxContainer>
    
 </cti:standardPage>