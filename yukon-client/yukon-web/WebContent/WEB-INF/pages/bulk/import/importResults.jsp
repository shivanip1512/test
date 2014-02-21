<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="bulk.importResults">

    <cti:msg var="headerTitle" key="yukon.common.device.bulk.importResults.header" />
    <tags:sectionContainer title="${headerTitle}" id="importResultsContainer" hideEnabled="false">
    
        <tags:backgroundProcessResultHolder resultsTypeMsgKey="import" callbackResult="${callbackResult}" />
        <form id="importResultsForm" type="post" action="/bulk/import/upload">
            <div class="page-action-area">
                <cti:msg2 var="label" key="yukon.common.device.bulk.importResults.backToUpload" />
                <cti:button type="submit" label="${label}"/>
            </div>
            <input type="hidden" name="ignoreInvalidCols" value="${ignoreInvalidCols}">
        </form>
    </tags:sectionContainer>

</cti:standardPage>