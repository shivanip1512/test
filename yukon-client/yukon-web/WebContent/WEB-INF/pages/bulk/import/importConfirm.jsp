<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="bulk.importConfirm">

    <cti:msg var="headerTitle" key="yukon.common.device.bulk.importConfirm.header"/>
    <tags:sectionContainer title="${headerTitle}" id="importConfirmContainer" hideEnabled="false">
    
        <form id="importConfirmForm" action="/bulk/import/doImport" method="post">
            <cti:csrfToken/>
            <input type="hidden" value="${bulkImportType}" name="bulkImportType" />
            
            <%-- CONFIRMATION INFO --%>
            <cti:msg var="rows" key="yukon.common.device.bulk.importConfirm.rows"/>
            <cti:msg var="importMethodLabel" key="yukon.common.device.bulk.importConfirm.importMethodLabel"/>
            <cti:msg var="optionalFieldsLabel" key="yukon.common.device.bulk.importConfirm.optionalFieldsLabel"/>
            
            <table cellspacing="8" border="0">
                <tr valign="top">
                    <td class="fwb">${rows}</td>
                    <td>${parsedResult.bulkFileInfo.dataCount}</td>
                </tr>
                <tr valign="top">
                    <td class="fwb">${importMethodLabel}</td>
                    <td><cti:msg key="yukon.common.device.bulk.columnHeader.tableHeader.import.method.displayName.${parsedResult.bulkImportMethod.name}"/></td>
                </tr>
                <tr valign="top">
                    <td class="fwb">${optionalFieldsLabel}</td>
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
            <div class="page-action-area">
                <cti:button nameKey="import" classes="primary action" busy="true" type="submit"/>
                <cti:url var="cancelUrl" value="/bulk/import/upload"/>
                <cti:button nameKey="cancel" onclick="window.location='${cancelUrl}'"/>
            </div>
            <%-- UPDATE INFO ID --%>
            <input type="hidden" name="fileInfoId" value="${parsedResult.bulkFileInfo.id}">
            
        </form>
        
    </tags:sectionContainer>
    
 </cti:standardPage>