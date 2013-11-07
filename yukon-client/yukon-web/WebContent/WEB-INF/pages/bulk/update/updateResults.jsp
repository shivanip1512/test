<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="bulk.updateResults">

    <cti:msg var="headerTitle" key="yukon.common.device.bulk.updateResults.header"/>
    <tags:sectionContainer title="${headerTitle}" id="updateResultsContainer">
        <tags:backgroundProcessResultHolder resultsTypeMsgKey="update" callbackResult="${callbackResult}" />
        <form id="updateResultsForm" type="post" action="/bulk/update/upload">
            <cti:msg var="backToUploadButton" key="yukon.common.device.bulk.updateResults.backToUpload"/>
            <div class="page-action-area">
                <button type="submit" class="button"><span class="label">${backToUploadButton}</span></button>
            </div>
            <input type="hidden" name="ignoreInvalidCols" value="${ignoreInvalidCols}">
            <input type="hidden" name="ignoreInvalidIdentifiers" value="${ignoreInvalidIdentifiers}">
        </form>
    </tags:sectionContainer>
    
 </cti:standardPage>