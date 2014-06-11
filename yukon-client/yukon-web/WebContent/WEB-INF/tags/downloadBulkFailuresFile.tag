<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="resultsId" required="true" type="java.lang.String"%>
<%@ attribute name="showText" required="true" type="java.lang.Boolean"%>

<script>
    function submitForm(id) {
        $('#' + id).submit();
    }
</script>

<c:set var="formName" value="processingExceptionDownloadForm${resultsId}"/>
<cti:msg var="titleText" key="yukon.common.device.bulk.bulkHome.recentBulkOperations.downloadErrorFileTitle" />

<c:if test="${showText}">
    <a href="javascript:submitForm('${formName}');" class="small" title="${titleText}">${titleText}</a> 
</c:if>

<a href="javascript:submitForm('${formName}');" class="small" title="${titleText}">
    <cti:icon icon="icon-page-white-excel"/>
</a> 

<form id="${formName}" method="post" action="<cti:url value="/bulk/processingExceptionFileDownload"/>">
    <cti:csrfToken/>
    <input type="hidden" name="resultsId" value="${resultsId}">
</form>