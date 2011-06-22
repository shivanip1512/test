<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="amr" page="analysis.readResults">
    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        <%-- metering --%>
        <cti:msg var="metersPageTitle" key="yukon.web.modules.amr.meteringStart.pageName" />
        <cti:crumbLink url="/spring/meter/start" title="${metersPageTitle}" />
        <%-- ADA List --%>
        <cti:msg var="adaListPageTitle" key="yukon.web.modules.amr.analysis.list.pageName" />
        <cti:crumbLink url="/spring/bulk/archiveDataAnalysis/list" title="${adaListPageTitle}" />
        <%-- ADA Results --%>
        <cti:msg var="adaResultsPageTitle" key="yukon.web.modules.amr.analysis.results.pageName"/>
        <cti:crumbLink url="/spring/bulk/archiveDataAnalysis/results?analysisId=${analysisId}" title="${adaResultsPageTitle}"/>
        <%-- ADA Read Results --%>
        <cti:crumbLink><cti:msg2 key="yukon.web.modules.amr.analysis.readResults.pageName"/></cti:crumbLink>
    </cti:breadCrumbs>
    
    <tags:boxContainer2 nameKey="yukon.web.modules.amr.analysis.readResults">
        
        <div class="smallBoldLabel notesSection">
            <tags:selectedDevices id="deviceColletion" deviceCollection="${deviceCollection}" />
        </div>
        
        <%-- PROGRESS --%>
        <div class="bottomPadded">
            <tags:resultProgressBar totalCount="${deviceCount}"
                                    countKey="ARCHIVE_DATA_ANALYSIS_LP_READ/${resultId}/COMPLETED_COUNT"
                                    progressLabelTextKey="yukon.web.modules.amr.analysis.readResults.progressLabel"
                                    statusTextKey="ARCHIVE_DATA_ANALYSIS_LP_READ/${resultId}/STATUS_TEXT"
                                    statusClassKey="ARCHIVE_DATA_ANALYSIS_LP_READ/${resultId}/STATUS_CLASS" />
        </div>
        
        <%-- SUCCESS --%>
        <div class="normalBoldLabel">
            <cti:msg key="yukon.web.modules.amr.analysis.readResults.successLabel" />
            <span class="okGreen">
                <cti:dataUpdaterValue type="ARCHIVE_DATA_ANALYSIS_LP_READ" identifier="${resultId}/SUCCESS_COUNT"/>
            </span>
        </div>
        <div id="successActionsDiv" style="padding:10px;display:none;">
            <cti:link href="/spring/bulk/collectionActions" key="yukon.web.modules.amr.analysis.readResults.collectionActionOnDevicesLabel" class="small">
                <cti:mapParam value="${result.successDeviceCollection.collectionParameters}"/>
            </cti:link>
            <tags:selectedDevicesPopup deviceCollection="${result.successDeviceCollection}"/>
        </div>
        
        <%-- FAILURE --%>
        <div class="normalBoldLabel">
            <cti:msg key="yukon.web.modules.amr.analysis.readResults.failureLabel" />
            <span class="errorRed">
                <cti:dataUpdaterValue type="ARCHIVE_DATA_ANALYSIS_LP_READ" identifier="${resultId}/FAILED_COUNT"/>
            </span>
        </div>
        <div id="failureActionsDiv" style="padding:10px;display:none;">
            <cti:link href="/spring/bulk/collectionActions" key="yukon.web.modules.amr.analysis.readResults.collectionActionOnDevicesLabel" class="small">
                <cti:mapParam value="${result.failureDeviceCollection.collectionParameters}"/>
            </cti:link>
            <tags:selectedDevicesPopup deviceCollection="${result.failureDeviceCollection}" />
        </div>
    </tags:boxContainer2>
    
    <cti:dataUpdaterCallback function="toggleElementsWhenTrue(['successActionsDiv', 'failureActionsDiv'], true)" initialize="true" value="ARCHIVE_DATA_ANALYSIS_LP_READ/${resultId}/IS_COMPLETE"/>

</cti:standardPage>