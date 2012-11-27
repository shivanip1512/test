<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<cti:url var="updateUrl" value="/bulk/fdrTranslationManager/updateLog">
    <cti:param name="resultId" value="${resultId}" />
</cti:url>

<cti:standardPage module="amr" page="fdrTranslationManagement.import">
    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        <%-- bulk home --%>
        <cti:msg var="bulkOperationsPageTitle" key="yukon.common.device.bulk.bulkHome.pageTitle"/>
        <cti:crumbLink url="/bulk/bulkHome" title="${bulkOperationsPageTitle}" />
        <%-- fdr translations --%>
        <cti:msg var="translationManagementPageTitle" key="yukon.web.modules.amr.fdrTranslationManagement.pageName"/>
        <cti:crumbLink url="/bulk/fdrTranslationManager/home" title="${translationManagementPageTitle}"/>
        <%-- fdr translation import --%>
        <cti:crumbLink><i:inline key="yukon.web.modules.amr.fdrTranslationManagement.import.pageName"/></cti:crumbLink>
    </cti:breadCrumbs>
    
    <cti:includeScript link="/JavaScript/importLogger.js"/>
    <script>
        jQuery(document).ready(function(){
            initiateLogUpdate("${updateUrl}", 1);
        });
    </script>
    
    <tags:boxContainer2  nameKey="mainBox">
        <cti:url var="folderImg" value="/WebConfig/yukon/Icons/folder_edit.gif"/>
        <img src="${folderImg}">&nbsp;<span style="color: gray">${fileName}</span><br>
        <c:if test="${ignoreInvalidColumns}">
            <i:inline key="yukon.web.modules.amr.fdrTranslationManagement.import.ignoringInvalidColumns"/>
        </c:if>
        <br><br>
        <div>
            <tags:resultProgressBar totalCount="${result.totalItems}"
                countKey="FDR_TRANSLATION_IMPORT/${resultId}/COMPLETED_LINES"
                progressLabelTextKey="yukon.web.modules.amr.fdrTranslationManagement.import.progressLabel"
                statusTextKey="FDR_TRANSLATION_IMPORT/${resultId}/STATUS_TEXT" />
        </div>
        <br>
        <tags:boxContainer2 nameKey="importLogBox">
            <div id="logDiv" style="height: 200px; overflow: auto;">
            </div>
        </tags:boxContainer2>
        <br>
        <div id="downloadFailedSection" style="display:none">
            <i:inline key="yukon.web.modules.amr.fdrTranslationManagement.import.downloadFailed"/>
            <cti:url var="downloadUrl" value="/bulk/fdrTranslationManager/downloadFailed">
                <cti:param name="resultId" value="${resultId}"/>
            </cti:url>
            <cti:button href="${downloadUrl}" nameKey="downloadFailedButton"/>
        </div>
        <cti:dataUpdaterCallback function="toggleElementsWhenTrue(['downloadFailedSection'], true)" initialize="true" value="FDR_TRANSLATION_IMPORT/${resultId}/IS_COMPLETE_WITH_FAILURES" />
    </tags:boxContainer2>
</cti:standardPage>