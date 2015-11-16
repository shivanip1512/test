<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="tools" page="bulk.pointImport.results">

<cti:url var="updateUrl" value="/bulk/pointImport/updateLog">
    <cti:param name="resultId" value="${resultId}" />
</cti:url>
<cti:includeScript link="/resources/js/pages/yukon.import.logger.js"/>
<script>
    $(document).ready( function () {
        initiateLogUpdate("${updateUrl}", 1);
    });
</script>
    
    <tags:sectionContainer2 nameKey="mainBox">
        <div class="stacked">
            <cti:icon icon="icon-folder-edit"/>&nbsp;<span style="color: gray">${fileName}</span><br>
            <c:if test="${ignoreInvalidColumns}">
                <cti:msg2 key=".ignoringInvalidColumns"/>
            </c:if>
        </div>
        <div class="stacked">
            <tags:resultProgressBar totalCount="${result.totalItems}"
                countKey="POINT_IMPORT/${resultId}/COMPLETED_LINES"
                progressLabelTextKey="yukon.web.modules.tools.bulk.pointImport.results.importProgress"
                statusTextKey="POINT_IMPORT/${resultId}/STATUS_TEXT" />
        </div>
        <tags:boxContainer2 nameKey="importLogBox">
            <div id="logDiv" style="height: 200px; overflow: auto;">
                <%-- Populated dynamically --%>
            </div>
        </tags:boxContainer2>
        <br>
        <div id="downloadFailedSection" class="page-action-area" style="display:none">
            <i:inline key=".downloadFailedLabel"/>
            <cti:url var="downloadUrl" value="/bulk/pointImport/downloadFailed">
                <cti:param name="resultId" value="${resultId}"/>
            </cti:url>
            <cti:button href="${downloadUrl}" nameKey="downloadFailedButton" icon="icon-bullet-go-down"/>
        </div>
        <cti:dataUpdaterCallback function="yukon.ui.progressbar.toggleElementsWhenTrue(['downloadFailedSection'], true)" initialize="true" value="POINT_IMPORT/${resultId}/IS_COMPLETE_WITH_FAILURES" />
    </tags:sectionContainer2>
</cti:standardPage>