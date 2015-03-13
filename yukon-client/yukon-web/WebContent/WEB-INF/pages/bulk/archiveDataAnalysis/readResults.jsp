<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="tools" page="bulk.analysis.readResults">
    
    <tags:sectionContainer2 nameKey="yukon.web.modules.tools.bulk.analysis.readResults">
        
        <div class="notes stacked">
            <tags:selectedDevices id="deviceColletion" deviceCollection="${deviceCollection}" />
        </div>
        
        <%-- PROGRESS --%>
        <div class="stacked">
            <tags:resultProgressBar totalCount="${deviceCount}"
                                    countKey="ARCHIVE_DATA_ANALYSIS_LP_READ/${resultId}/COMPLETED_COUNT"
                                    progressLabelTextKey="yukon.web.modules.tools.bulk.analysis.readResults.progressLabel"
                                    statusTextKey="ARCHIVE_DATA_ANALYSIS_LP_READ/${resultId}/STATUS_TEXT"
                                    statusClassKey="ARCHIVE_DATA_ANALYSIS_LP_READ/${resultId}/STATUS_CLASS" />
        </div>
        
        <%-- SUCCESS --%>
        <div class="fwb">
            <i:inline key="yukon.web.modules.tools.bulk.analysis.readResults.successLabel" />
            <span class="success">
                <cti:dataUpdaterValue type="ARCHIVE_DATA_ANALYSIS_LP_READ" identifier="${resultId}/SUCCESS_COUNT"/>
            </span>
        </div>
        <div id="js-success-actions" style="padding:10px;display:none;">
            <cti:link href="/bulk/collectionActions" key="yukon.web.modules.tools.bulk.analysis.readResults.collectionActionOnDevicesLabel" class="small">
                <cti:mapParam value="${result.successDeviceCollection.collectionParameters}"/>
            </cti:link>
            <tags:selectedDevicesPopup deviceCollection="${result.successDeviceCollection}"/>
        </div>
        
        <%-- FAILURE --%>
        <div class="fwb">
            <i:inline key="yukon.web.modules.tools.bulk.analysis.readResults.failureLabel" />
            <span class="error">
                <cti:dataUpdaterValue type="ARCHIVE_DATA_ANALYSIS_LP_READ" identifier="${resultId}/FAILED_COUNT"/>
            </span>
        </div>
        <div id="failureActionsDiv" style="padding:10px;display:none;">
            <cti:link href="/bulk/collectionActions" key="yukon.web.modules.tools.bulk.analysis.readResults.collectionActionOnDevicesLabel" class="small">
                <cti:mapParam value="${result.failureDeviceCollection.collectionParameters}"/>
            </cti:link>
            <tags:selectedDevicesPopup deviceCollection="${result.failureDeviceCollection}" />
        </div>
    </tags:sectionContainer2>
    
    <cti:dataUpdaterCallback function="yukon.ui.progressbar.toggleElementsWhenTrue(['js-success-actions', 'failureActionsDiv'], true)" initialize="true" value="ARCHIVE_DATA_ANALYSIS_LP_READ/${resultId}/IS_COMPLETE"/>

</cti:standardPage>