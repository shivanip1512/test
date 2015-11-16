<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:standardPage module="tools" page="bulk.fdrTranslationManagement.import">

    <cti:includeScript link="/resources/js/pages/yukon.import.logger.js" />
    
    <cti:icon icon="icon-folder-edit"/>&nbsp;<span>${fn:escapeXml(fileName)}</span>
    <c:if test="${ignoreInvalidColumns}">
        <div class="stacked">
            <i:inline key="yukon.web.modules.tools.bulk.fdrTranslationManagement.import.ignoringInvalidColumns" />
        </div>
    </c:if>
    <div class="stacked">
        <tags:resultProgressBar totalCount="${result.totalItems}" 
                                countKey="FDR_TRANSLATION_IMPORT/${resultId}/COMPLETED_LINES"
                                progressLabelTextKey="yukon.web.modules.tools.bulk.fdrTranslationManagement.import.progressLabel" 
                                statusTextKey="FDR_TRANSLATION_IMPORT/${resultId}/STATUS_TEXT"/>
    </div>
    
    <tags:boxContainer2 nameKey="importLogBox">
      <div id="logDiv" class="scroll-lg"></div>
    </tags:boxContainer2>
    
    <div id="downloadFailedSection" class="dn">
        <i:inline key="yukon.web.modules.tools.bulk.fdrTranslationManagement.import.downloadFailed" />
        <cti:url var="downloadUrl" value="/bulk/fdrTranslationManager/downloadFailed">
            <cti:param name="resultId" value="${resultId}" />
        </cti:url>
        <cti:button href="${downloadUrl}" nameKey="downloadFailedButton" />
    </div>
    
    <cti:dataUpdaterCallback function="toggleElementsWhenTrue(['downloadFailedSection'], true)" initialize="true" value="FDR_TRANSLATION_IMPORT/${resultId}/IS_COMPLETE_WITH_FAILURES" />
    
    <cti:url var="updateUrl" value="/bulk/fdrTranslationManager/updateLog">
        <cti:param name="resultId" value="${resultId}" />
    </cti:url>
    <script>initiateLogUpdate("${updateUrl}", 1);</script>
</cti:standardPage>