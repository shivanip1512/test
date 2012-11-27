<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:url var="updateUrl" value="/bulk/pointImport/updateLog">
    <cti:param name="resultId" value="${resultId}" />
</cti:url>

<cti:standardPage module="amr" page="pointImport.results">
    <cti:includeScript link="/JavaScript/importLogger.js"/>
    <script>
        jQuery(document).ready(function(){
            initiateLogUpdate("${updateUrl}", 1);
        });
    </script>
    
    <tags:boxContainer2 nameKey="mainBox">
        <div class="bottomPadded">
            <cti:url var="folderImg" value="/WebConfig/yukon/Icons/folder_edit.gif"/>
            <img src="${folderImg}">&nbsp;<span style="color: gray">${fileName}</span><br>
            <c:if test="${ignoreInvalidColumns}">
                <cti:msg2 key=".ignoringInvalidColumns"/>
            </c:if>
        </div>
        <div class="bottomPadded">
            <tags:resultProgressBar totalCount="${result.totalItems}"
                countKey="POINT_IMPORT/${resultId}/COMPLETED_LINES"
                progressLabelTextKey="yukon.web.modules.amr.pointImport.results.importProgress"
                statusTextKey="POINT_IMPORT/${resultId}/STATUS_TEXT" />
        </div>
        <tags:boxContainer2 nameKey="importLogBox">
            <div id="logDiv" style="height: 200px; overflow: auto;">
                <%-- Populated dynamically --%>
            </div>
        </tags:boxContainer2>
        <br>
        <div id="downloadFailedSection" style="display:none">
            <i:inline key=".downloadFailedLabel"/>
            <cti:url var="downloadUrl" value="/bulk/pointImport/downloadFailed">
                <cti:param name="resultId" value="${resultId}"/>
            </cti:url>
            <cti:button href="${downloadUrl}" nameKey="downloadFailedButton"/>
        </div>
        <cti:dataUpdaterCallback function="toggleElementsWhenTrue(['downloadFailedSection'], true)" initialize="true" value="POINT_IMPORT/${resultId}/IS_COMPLETE_WITH_FAILURES" />
    </tags:boxContainer2>
</cti:standardPage>