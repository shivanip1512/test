<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="bulk.importResults">
    
    <tags:backgroundProcessResultHolder resultsTypeMsgKey="import" callbackResult="${callbackResult}"/>
    <div class="page-action-area">
        <cti:msg2 var="label" key="yukon.common.device.bulk.importResults.backToUpload"/>
        <cti:url var="url" value="/bulk/import/upload"/>
        <cti:button label="${label}" href="${url}"/>
    </div>
    
</cti:standardPage>