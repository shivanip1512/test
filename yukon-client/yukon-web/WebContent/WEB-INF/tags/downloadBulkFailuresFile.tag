<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="resultsId" required="true" type="java.lang.String"%>
<%@ attribute name="showText" required="true" type="java.lang.Boolean"%>

<script>
    function submitForm(id) {
        $(id).submit();
    }
</script>

<c:set var="formName" value="processingExceptionDownloadForm${resultsId}"/>
<cti:msg var="titleText" key="yukon.common.device.bulk.bulkHome.recentBulkOperations.downloadErrorFileTitle" />
<c:url var="downloadImg" value="/WebConfig/yukon/Icons/download_file.gif" />
<c:url var="downloadImgOver" value="/WebConfig/yukon/Icons/download_file_over.gif" />

<c:if test="${showText}">
    <a href="javascript:submitForm('${formName}');" class="small" title="${titleText}">${titleText}</a> 
</c:if>

<a href="javascript:submitForm('${formName}');" class="small" title="${titleText}">
    <img src="${downloadImg}" onmouseover="this.src='${downloadImgOver}'" onmouseout="this.src='${downloadImg}'">
</a> 

<form id="${formName}" method="post" action="/bulk/processingExceptionFileDownload">
    <input type="hidden" name="resultsId" value="${resultsId}">
</form>