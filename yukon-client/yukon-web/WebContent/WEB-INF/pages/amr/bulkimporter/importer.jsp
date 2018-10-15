<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%-- MODULE, MENU --%>
<cti:standardPage page="bulk.bulkImporter" module="tools">

    <cti:url var="recentResultsLink" value="/bulk/recentResults"/>
    <div class="fr"><a href="${recentResultsLink}"><i:inline key="yukon.common.device.bulk.recentResults"/></a></div><br/>

<cti:url var="refreshUrl" value="/amr/bulkimporter/refreshResults" />

<script type="text/javascript">
    setupRefreshStuff("${refreshUrl}", "${refreshRate}");
    $(function() {
        show_next('failed');
    });
</script>

<cti:url var="bulkImportUrl" value="/bulk/import/upload"/>
<div class="warning stacked"><i:inline key=".header" arguments="${bulkImportUrl}"/></div>

<cti:url var="uploadUrl" value="/amr/bulkimporter/upload"/>
<form:form id="actionsForm" method="post" action="${uploadUrl}" enctype="multipart/form-data">
    <cti:csrfToken/>
    <cti:msg2 var="actionsHeader" key=".actions.header" />
    <cti:msg2 var="actionsLoadDataLabel" key=".actions.loadDataFromFileLabel" />
    <cti:msg2 var="actionsBulkImporterLabel" key=".actions.bulkImporterCommsLabel" />
    <cti:msg2 var="actionsClearResultsLabel" key=".actions.clearImportResultsLabel" />
    <cti:msg2 var="lastReportResultHeader" key=".lastReportResult.header" />
    <cti:msg2 var="allResultsLabel" key=".lastReportResult.allResultsLabel" />
    <cti:msg2 var="failedEntriesLabel" key=".lastReportResult.failedEntriesLabel" />
    <cti:msg2 var="pendingCommsLabel" key=".lastReportResult.pendingCommsLabel" />
    <cti:msg2 var="failedCommsLabel" key=".lastReportResult.failedCommsLabel" />
    <cti:msg2 var="importCountLabel" key=".lastReportResult.importCountLabel" />
    <cti:msg2 var="prevImportLabel" key=".lastReportResult.prevImportLabel" />
    <cti:msg2 var="nextImportLabel" key=".lastReportResult.nextImportLabel" />
    <cti:msg2 var="tabularDataLabel" key=".lastReportResult.tabularDataLabel" />

    <tags:boxContainer title="${actionsHeader}" hideEnabled="false">
        <tags:nameValueContainer>
            <%-- UPLOAD FILE --%>
            <tags:nameValue name="${actionsLoadDataLabel}">
                <tags:file name="dataFile"/>
                <cti:button type="submit" name="importFile" value="Load" label="Load"/>
                <%-- IMPORT MSG --%>
                <c:if test="${not empty msgStr}">
                    <br />
                    <c:if test="${msgType == '1'}">${fn:escapeXml(msgStr)}</c:if>
                    <c:if test="${msgType == '0'}"><div class="error">${fn:escapeXml(msgStr)}</div></c:if>
                </c:if>
            </tags:nameValue>
            <%-- TOGGLE BULK IMPORTER COMMUNICATION --%>
            <tags:nameValue name="${actionsBulkImporterLabel}">
            <c:choose>
                <c:when test="${importerCommunicationsEnabled}">On</c:when>
                <c:otherwise>Off</c:otherwise>
            </c:choose>
        </tags:nameValue>
        <%-- CLEAR IMPORTS --%>
        <tags:nameValue name="${actionsClearResultsLabel}">
            <select name="clearImportsSelect">
                <option value="all">${allResultsLabel}</option>
                <option value="failed">${failedEntriesLabel}</option>
                <option value="pendingComm">${pendingCommsLabel}</option>
                <option value="failedComm">${failedCommsLabel}</option>
            </select>
            <cti:button type="submit" name="clearImports" value="Clear" label="Clear"/>
        </tags:nameValue>
    </tags:nameValueContainer>
</tags:boxContainer>
<br />
<tags:boxContainer title="${lastReportResultHeader}" hideEnabled="false">
    <table class="full-width">
    <tr class="vat">
    
    <%-- TABS --%>
    <td>
        <table>
            <tr>
                <td><a href="javascript:void(0);" onclick="javascript:show_next('failed');">${failedEntriesLabel}</a>:</td>
                <td><div id="failureCount"></div></td>
            </tr>
            <tr>
                <td><a href="javascript:void(0);" onclick="javascript:show_next('pendingComm');">${pendingCommsLabel}</a>:</td>
                <td><div id="pendingCommsCount"></div></td>
            </tr>
            <tr>
                <td><a href="javascript:void(0);" onclick="javascript:show_next('failedComm');">${failedCommsLabel}</a>:</td>
                <td><div id="failedCommsCount"></div></td>
            </tr>
        </table>
    </td>
    
    <%-- NEXT LAST IMPORT --%>
    <td>
        <tags:nameValueContainer>
            
            <tags:nameValue name="${importCountLabel}">
            	<input id="prevImportDataCount" type="hidden" value="0">
                <div id="importDataCount"></div>
            </tags:nameValue>
            
            <tags:nameValue name="${prevImportLabel}">
                <div id="lastImportAttempt"></div>
            </tags:nameValue>
            
            <tags:nameValue name="${nextImportLabel}">
                <span id="nextImportAttempt"></span>&nbsp;&nbsp;<cti:button type="submit" name="forceManualImportEvent" value="Import Now" label="Import Now"/>
            </tags:nameValue>
            
            <tags:nameValue name="${tabularDataLabel}">
                <div id="failed_reports">
                        <a href="<cti:url value="/amr/reports/bulkImportResultsReport?def=bulkImportResultsDefinition&reportType=FAILURES" />">HTML</a>
                        |
                        <cti:simpleReportLinkFromNameTag definitionName="bulkImportResultsDefinition" viewType="csvView" reportType="FAILURES">CSV</cti:simpleReportLinkFromNameTag>
                        |
                        <cti:simpleReportLinkFromNameTag definitionName="bulkImportResultsDefinition" viewType="pdfView" reportType="FAILURES">PDF</cti:simpleReportLinkFromNameTag>
                    </div>
                    <div id="pendingComm_reports" style="display:none;">
                        <a href="<cti:url value="/amr/reports/bulkImportResultsReport?def=bulkImportResultsDefinition&reportType=PENDING_COMMS" />">HTML</a>
                        |
                        <cti:simpleReportLinkFromNameTag definitionName="bulkImportResultsDefinition" viewType="csvView" reportType="PENDING_COMMS">CSV</cti:simpleReportLinkFromNameTag>
                        |
                        <cti:simpleReportLinkFromNameTag definitionName="bulkImportResultsDefinition" viewType="pdfView" reportType="PENDING_COMMS">PDF</cti:simpleReportLinkFromNameTag>
                    </div>
                    <div id="failedComm_reports" style="display:none;">
                        <a href="<cti:url value="/amr/reports/bulkImportResultsReport?def=bulkImportResultsDefinition&reportType=FAILED_COMMS" />">HTML</a>
                        |
                        <cti:simpleReportLinkFromNameTag definitionName="bulkImportResultsDefinition" viewType="csvView" reportType="FAILED_COMMS">CSV</cti:simpleReportLinkFromNameTag>
                        |
                        <cti:simpleReportLinkFromNameTag definitionName="bulkImportResultsDefinition" viewType="pdfView" reportType="FAILED_COMMS">PDF</cti:simpleReportLinkFromNameTag>
                    </div>
            </tags:nameValue>
            
        </tags:nameValueContainer>
    </td>
    </tr>
    </table>
    
    <div id="failed_title" style="display:none;"><h4>${failedEntriesLabel}</h4></div>
    <div id="pendingComm_title" style="display:none;"><h4>${pendingCommsLabel}</h4></div>
    <div id="failedComm_title" style="display:none;"><h4>${failedCommsLabel}</h4></div>
    <div class="box">
        <%-- FAILURES --%>
        <div id="failed_data" style="display:none;">
            <table id="failed_table" class="results-table">
                <thead>
                    <tr>
                        <c:forEach var="colName" items="${failureColumnNames}">
                            <th>${colName}</th>
                        </c:forEach>
                    </tr>
                </thead>
            </table>
        </div>
        <%-- PENDING COMMS --%>
        <div id="pendingComm_data" style="display:none;">
            <table id="pendingComm_table" class="results-table">
                <tr>
                    <c:forEach var="colName" items="${pendingCommsColumnNames}">
                        <th>${colName}</th>
                    </c:forEach>
                </tr>
            </table>
        </div>
        <%-- FAILURE COMMS --%>
        <div id="failedComm_data" style="display:none;">
            <table id="failedComm_table" class="results-table">
                <tr>
                    <c:forEach var="colName" items="${failureCommsColumnNames}">
                        <th>${colName}</th>
                    </c:forEach>
                </tr>
            </table>
        </div>
    </div>    
</tags:boxContainer>
</form:form>

    <cti:includeScript link="/resources/js/pages/yukon.ami.bulk.import.js"/>

</cti:standardPage>