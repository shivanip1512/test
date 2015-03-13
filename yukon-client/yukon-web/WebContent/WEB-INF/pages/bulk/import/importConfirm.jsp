<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="bulk.importConfirm">
<cti:msgScope paths=",yukon.common.device.bulk">

<form action="<cti:url value="/bulk/import/parse"/>" method="post">
    
    <cti:csrfToken/>
    <input type="hidden" value="${bulkImportType}" name="bulkImportType"/>
    <input type="hidden" name="fileInfoId" value="${parsedResult.bulkFileInfo.id}">
    
    <tags:nameValueContainer2>
        
        <tags:nameValue2 nameKey=".importConfirm.rows">${parsedResult.bulkFileInfo.dataCount}</tags:nameValue2>
        
        <tags:nameValue2 nameKey=".importConfirm.importMethodLabel">
            <i:inline key=".columnHeader.tableHeader.import.method.displayName.${parsedResult.bulkImportMethod.name}"/>
        </tags:nameValue2>
        
        <tags:nameValue2 nameKey=".importConfirm.optionalFieldsLabel">
            <c:choose>
                <c:when test="${not empty parsedResult.updateBulkFieldColumnHeaders}">
                    <c:forEach var="bulkField" items="${parsedResult.updateBulkFieldColumnHeaders}">
                        <div>${bulkField}</div>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <i:inline key="yukon.common.none"/>
                </c:otherwise>
            </c:choose>
        </tags:nameValue2>
        
    </tags:nameValueContainer2>
    
    <%-- SUBMIT BUTTONS --%>
    <div class="page-action-area">
        <cti:button nameKey="import" classes="primary action" busy="true" type="submit"/>
        <cti:url var="url" value="/bulk/import/upload"/>
        <cti:button nameKey="cancel" href="${url}"/>
    </div>
    
</form>

</cti:msgScope>
</cti:standardPage>