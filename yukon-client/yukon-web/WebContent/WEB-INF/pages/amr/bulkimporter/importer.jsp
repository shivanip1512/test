<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%-- MODULE, MENU --%>
<cti:standardPage title="Bulk Importer" module="blank">
<cti:standardMenu menuSelection="" />

<cti:breadCrumbs>
    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
    &gt; Bulk Importer
</cti:breadCrumbs>

<h2>Bulk Importer</h2>

<div class="notAuthorized">Note: <a href="/spring/bulk/bulkHome">Bulk Operations</a> is the preferred method of importing or updating meters.</div>
<br>

<cti:includeScript link="SCRIPTACULOUS_EFFECTS"/>
<cti:includeScript link="/JavaScript/bulkImporter.js"/>

<script type="text/javascript">

    <cti:url var="refreshUrl" value="/spring/amr/bulkimporter/refreshResults" />
    
    setupRefreshStuff("${refreshUrl}", "${refreshRate}");
    
    Event.observe(window, 'load', function() {
        show_next('failed');
    });
    
</script>

<form id="actionsForm" method="post" action="/spring/amr/bulkimporter/upload" enctype="multipart/form-data">
<tags:boxContainer title="Actions" hideEnabled="false">

    <tags:nameValueContainer>
    
        <%-- UPLOAD FILE --%>
        <tags:nameValue name="Load Data From File">
            <input type="file" name="dataFile" size="30px">
            <input type="submit" name="importFile" value="Load">
            
            <%-- IMPORT MSG --%>
            <c:if test="${not empty msgStr}">
                <br/>
                <c:if test="${msgType == '1'}">
                    ${msgStr}
                </c:if>
                <c:if test="${msgType == '0'}">
                    <div style="color:#CC0000;">${msgStr}</div>
                </c:if>
            </c:if>
        
        </tags:nameValue>
        
        <%-- TOGGLE BULK IMPORTER COMMUNICATION --%>
        <tags:nameValue name="Bulk Importer Communications">
        <c:choose>
            <c:when test="${importerCommunicationsEnabled}">On</c:when>
            <c:otherwise>Off</c:otherwise>
        </c:choose>
        </tags:nameValue>
        
        <%-- CLEAR IMPORTS --%>
        <tags:nameValue name="Clear Import Results">
            <select name="clearImportsSelect">
                <option value="all">All Results</option>
                <option value="failed">Failed Entries</option>
                <option value="pendingComm">Pending Communications</option>
                <option value="failedComm">Failed Communications</option>
            </select>
            <input type="submit" name="clearImports" value="Clear">
        </tags:nameValue>
        
    </tags:nameValueContainer>
    
</tags:boxContainer>

<br>


<tags:boxContainer title="Results Of Last Import" hideEnabled="false">
    
    
    <table width="100%">
    <tr valign="top">
    
    <%-- TABS --%>
    <td width="40%">
        <table>
        
            <tr>
                <td><a href="javascript:void(0);" onclick="javascript:show_next('failed');">Failed Entries</a>:</td>
                <td><div id="failureCount"></div></td>
            </tr>
            
            <tr>
                <td><a href="javascript:void(0);" onclick="javascript:show_next('pendingComm');">Pending Communications</a>:</td>
                <td><div id="pendingCommsCount"></div></td>
            </tr>
            
            <tr>
                <td><a href="javascript:void(0);" onclick="javascript:show_next('failedComm');">Failed Communications</a>:</td>
                <td><div id="failedCommsCount"></div></td>
            </tr>
            
        </table>
    
    
    </td>
    
    
    <%-- NEXT LAST IMPORT --%>
    <td>
        <tags:nameValueContainer>
            
            <tags:nameValue name="Import Data Count">
            	<input id="prevImportDataCount" type="hidden" value="0">
                <div id="importDataCount"></div>
            </tags:nameValue>
            
            <tags:nameValue name="Previous Import">
                <div id="lastImportAttempt"></div>
            </tags:nameValue>
            
            <tags:nameValue name="Next Import">
                <span id="nextImportAttempt"></span>&nbsp;&nbsp;<input type="submit" name="forceManualImportEvent" value="Import Now">
            </tags:nameValue>
            
            <tags:nameValue name="Tabular Data">
                <div id="failed_reports">
                        <a href="<cti:url value="/spring/amr/reports/bulkImportResultsReport?def=bulkImportResultsDefinition&reportType=FAILURES" />">HTML</a>
                        |
                        <cti:simpleReportLinkFromNameTag definitionName="bulkImportResultsDefinition" viewType="csvView" reportType="FAILURES">CSV</cti:simpleReportLinkFromNameTag>
                        |
                        <cti:simpleReportLinkFromNameTag definitionName="bulkImportResultsDefinition" viewType="pdfView" reportType="FAILURES">PDF</cti:simpleReportLinkFromNameTag>
                    </div>
                    <div id="pendingComm_reports" style="display:none;">
                        <a href="<cti:url value="/spring/amr/reports/bulkImportResultsReport?def=bulkImportResultsDefinition&reportType=PENDING_COMMS" />">HTML</a>
                        |
                        <cti:simpleReportLinkFromNameTag definitionName="bulkImportResultsDefinition" viewType="csvView" reportType="PENDING_COMMS">CSV</cti:simpleReportLinkFromNameTag>
                        |
                        <cti:simpleReportLinkFromNameTag definitionName="bulkImportResultsDefinition" viewType="pdfView" reportType="PENDING_COMMS">PDF</cti:simpleReportLinkFromNameTag>
                    </div>
                    <div id="failedComm_reports" style="display:none;">
                        <a href="<cti:url value="/spring/amr/reports/bulkImportResultsReport?def=bulkImportResultsDefinition&reportType=FAILED_COMMS" />">HTML</a>
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
    
 
    <div id="failed_title" style="display:none;"><h4>Failed Entries</h4></div>
    <div id="pendingComm_title" style="display:none;"><h4>Pending Communications</h4></div>
    <div id="failedComm_title" style="display:none;"><h4>Failed Communications</h4></div>
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
        <td valign="top" width="100%">
            
            <%-- FAILURES --%>
            <div id="failed_data" style="display:none;">
                <table id="failed_table" class="miniResultsTable">
                    <tr>
                        <c:forEach var="colName" items="${failureColumnNames}">
                            <th>${colName}</th>
                        </c:forEach>
                    </tr>
                </table>
            </div>
            
            <%-- PENDING COMMS --%>
            <div id="pendingComm_data" style="display:none;">
                <table id="pendingComm_table" class="miniResultsTable">
                    <tr>
                        <c:forEach var="colName" items="${pendingCommsColumnNames}">
                            <th>${colName}</th>
                        </c:forEach>
                    </tr>
                </table>
            </div>
            
            <%-- FAILURE COMMS --%>
            <div id="failedComm_data" style="display:none;">
                <table id="failedComm_table" class="miniResultsTable">
                    <tr>
                        <c:forEach var="colName" items="${failureCommsColumnNames}">
                            <th>${colName}</th>
                        </c:forEach>
                    </tr>
                </table>
            </div>
            
        </td>
        </tr>
    </table>
    
</tags:boxContainer>
</form>

</cti:standardPage>