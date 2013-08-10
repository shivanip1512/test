<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="tools" page="bulk.fdrTranslationManagement.import">

  <cti:url var="updateUrl" value="/bulk/fdrTranslationManager/updateLog">
    <cti:param name="resultId" value="${resultId}" />
  </cti:url>

  <cti:includeScript link="/JavaScript/importLogger.js" />

  <cti:icon icon="icon-folder-edit"/&nbsp;<span style="color: gray">${fileName}</span>
  <br>
  <c:if test="${ignoreInvalidColumns}">
    <i:inline key="yukon.web.modules.tools.bulk.fdrTranslationManagement.import.ignoringInvalidColumns" />
  </c:if>
  <br>
  <br>
  <div>
    <tags:resultProgressBar totalCount="${result.totalItems}" countKey="FDR_TRANSLATION_IMPORT/${resultId}/COMPLETED_LINES"
      progressLabelTextKey="yukon.web.modules.tools.bulk.fdrTranslationManagement.import.progressLabel" 
      statusTextKey="FDR_TRANSLATION_IMPORT/${resultId}/STATUS_TEXT" />
  </div>
  <br>
  <tags:boxContainer2 nameKey="importLogBox">
    <div id="logDiv" style="height: 200px; overflow: auto;"></div>
  </tags:boxContainer2>
  <br>
  <div id="downloadFailedSection" style="display: none">
    <i:inline key="yukon.web.modules.tools.bulk.fdrTranslationManagement.import.downloadFailed" />
    <cti:url var="downloadUrl" value="/bulk/fdrTranslationManager/downloadFailed">
      <cti:param name="resultId" value="${resultId}" />
    </cti:url>
    <cti:button href="${downloadUrl}" nameKey="downloadFailedButton" />
  </div>
  <cti:dataUpdaterCallback function="toggleElementsWhenTrue(['downloadFailedSection'], true)" initialize="true" value="FDR_TRANSLATION_IMPORT/${resultId}/IS_COMPLETE_WITH_FAILURES" />

  <script>initiateLogUpdate("${updateUrl}", 1);</script>
</cti:standardPage>