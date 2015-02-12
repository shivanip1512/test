<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<cti:standardPage module="tools" page="bulk.importUpload">

<script>
$(function(){
    updateImportTypeSelection();
    $("#importTypeSelector").change(function(){
        updateImportTypeSelection();
    });
});

function updateImportTypeSelection() {
    var itemSelected = $("#importTypeSelector").val();
    //hide all rows
    $("[class^='bulkImportType_']").hide();
    //show rows for the selected interface
    $(".bulkImportType_" + itemSelected).show();
    
    //hide all sample files
    $("[class^='sample_import_files_']").hide();
    //show sample files for the selected interface
    $(".sample_import_files_" + itemSelected).show();
}

window.pressed = function(){
	$('#fileErr').hide();
};
</script>
    
    <cti:msg var="headerTitle" key="yukon.common.device.bulk.importUpload.header"/>
    <cti:url var="parseUploadUrl" value="/bulk/import/parseUpload"/>
    <form id="uploadForm" method="post" action="${parseUploadUrl}" enctype="multipart/form-data">
        <cti:csrfToken/>    
    <tags:sectionContainer title="${headerTitle}" id="importUploadContainer" hideEnabled="false">
        <div class="column-12-12">
            <div class="column one">
                
                <div class="stacked">
                    <strong><cti:msg key="yukon.common.device.bulk.importUpload.noteLabel"/></strong>
                    <i:inline key="yukon.common.device.bulk.importUpload.noteText"/>
                </div>
                <div class="stacked">
                    <strong><i:inline key="yukon.common.device.bulk.importUpload.optionsLabel"/></strong>
                    <label><input type="checkbox" name="ignoreInvalidCols" <c:if test="${ignoreInvalidCols}">checked</c:if>><i:inline key="yukon.common.device.bulk.options.import.ignoreInvalidHeaders"/></label>
                </div>
                
                <%-- file errors --%>
                <c:if test="${not empty fileErrorKeysList}">
                    <div id="fileErr">
                        <c:forEach var="fileErrorKey" items="${fileErrorKeysList}">
                            <div class="error">
                                <i:inline key="${fileErrorKey}"/>
                            </div>
                        </c:forEach>
                    </div>
                </c:if>

                <%-- header errors --%>
                <c:if test="${not empty headersErrorResolverList}">
                    <div>
                        <c:forEach var="headersErrorResolver" items="${headersErrorResolverList}">
                            <div class="error">
                                <i:inline key="${headersErrorResolver}"/>
                            </div>
                        </c:forEach>
                    </div>
                </c:if>
            
                <%-- file select --%>
                <div>
                    <strong><i:inline key="yukon.common.device.bulk.importUpload.importFileLabel"/></strong>
                    <input type="file" name="dataFile" onchange="pressed()">
                </div>
                <div class="action-area"><cti:button nameKey="load" classes="primary action" busy="true" type="submit"/></div>
            </div>
            <div class="column two nogutter">
                <%-- INSTRUCTIONS --%>
                <h3>Instructions:</h3>
                <ul class="stacked"><cti:msg key="yukon.common.device.bulk.importUpload.instructions"/></ul>
               
                <%-- sample files --%>
                <div>
                    <strong><cti:msg key="yukon.common.device.bulk.importUpload.sampleFilesLabel"/>:</strong>
                    <c:forEach var="bulkImportType" items="${bulkImportTypes}">
                            <span class="sample_import_files_${bulkImportType}">
                                <span class="fwb">(${bulkImportType}):</span>
                                <c:choose>
                                    <c:when test="${bulkImportType=='MCT'}">
                                        <a href="<cti:url value="/WebConfig/custom/sample_bulk_files/import/${bulkImportType}/Sample_Bulk_Import_File1.csv"/>">File 1</a>,
                                        <a href="<cti:url value="/WebConfig/custom/sample_bulk_files/import/${bulkImportType}/Sample_Bulk_Import_File2.csv"/>">File 2</a>,
                                        <a href="<cti:url value="/WebConfig/custom/sample_bulk_files/import/${bulkImportType}/Sample_Bulk_Import_File3.csv"/>">File 3</a>,
                                        <a href="<cti:url value="/WebConfig/custom/sample_bulk_files/import/${bulkImportType}/Sample_Bulk_Import_File4.csv"/>">File 4</a>
                                    </c:when>
                                    <c:when test="${bulkImportType=='RFN'}">
                                        <a href="<cti:url value="/WebConfig/custom/sample_bulk_files/import/${bulkImportType}/Sample_Bulk_Import_File1.csv"/>">File 1</a>,
                                        <a href="<cti:url value="/WebConfig/custom/sample_bulk_files/import/${bulkImportType}/Sample_Bulk_Import_File2.csv"/>">File 2</a>,
                                        <a href="<cti:url value="/WebConfig/custom/sample_bulk_files/import/${bulkImportType}/Sample_Bulk_Import_File3.csv"/>">File 3</a>,
                                        <a href="<cti:url value="/WebConfig/custom/sample_bulk_files/import/${bulkImportType}/Sample_Bulk_Import_File4.csv"/>">File 4</a>
                                    </c:when>
                                </c:choose>
                            </span>
                        </c:forEach>
                </div>
            </div>
        </div>
        <div class="column-24 stacked">
            <div class="column one">
                <label>
                    <strong><cti:msg2 key="yukon.common.device.bulk.options.deviceTypeSelect.text" /></strong>
                    <select name="importTypeSelector" id="importTypeSelector">
                        <c:forEach var="bulkImportType" items="${bulkImportTypes}">
                            <option value="${bulkImportType}" <c:if test="${importTypeSelector eq bulkImportType}">selected="selected"</c:if>>
                                <i:inline key="yukon.common.device.bulk.options.deviceTypeSelect.${bulkImportType}" />
                            </option>
                        </c:forEach>
                    </select>
                </label>
            </div>
        </div>
        <div class="column-12-12">
            <c:forEach var="method" items="${importMethods}" varStatus="status">
                <c:choose>
                    <c:when test="${status.count % 2 > 0}">
                        <c:set var="classes" value="bulkImportType_${method.type} column one"/>
                    </c:when>
                    <c:otherwise>
                        <c:set var="classes" value="bulkImportType_${method.type} column two nogutter"/>
                    </c:otherwise>
                </c:choose>
                <div class="${classes}">
                    <div class="bulkImportType_${method.type}" style="display: none;">
                        <h4><i:inline key="yukon.common.device.bulk.columnHeader.tableHeader.import.method.tableLabel.${method.name}"/></h4>
                        <div><i:inline key="yukon.common.device.bulk.columnHeader.tableHeader.import.method.tableDescription.${method.name}"/></div>
                        <table class="results-table detail">
                            <thead>
                                <tr>
                                    <th><cti:msg key="yukon.common.device.bulk.columnHeader.tableHeader.import.requiredColumnHeaders"/></th>
                                    <th><cti:msg key="yukon.common.device.bulk.columnHeader.tableHeader.import.descriptionInstruction"/></th>
                                </tr>
                            <thead>
                            <c:forEach var="field" items="${requiredFieldsByMethod[method]}">
                                <tr>
                                    <td class="strong-label-small">${field}</td>
                                    <td>
                                        <cti:msg var="description" key="yukon.common.device.bulk.columnHeader.import.description.${field}"/>
                                        <cti:msg var="instruction" key="yukon.common.device.bulk.columnHeader.import.instruction.${field}"/>
                                        ${description}<c:if test="${instruction != ''}"><br>${instruction}</c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                            <tr>
                                <th><cti:msg key="yukon.common.device.bulk.columnHeader.tableHeader.import.optionalHeaders"/></th>
                                <th><cti:msg key="yukon.common.device.bulk.columnHeader.tableHeader.import.descriptionInstruction"/></th>
                            </tr>
                            <c:forEach var="field" items="${updateableFieldsByMethod[method]}">
                                <tr>
                                    <td class="strong-label-small">${field}</td>
                                    <td>
                                        <cti:msg var="description" key="yukon.common.device.bulk.columnHeader.import.description.${field}"/>
                                        <cti:msg var="instruction" key="yukon.common.device.bulk.columnHeader.import.instruction.${field}"/>
                                        ${description}<c:if test="${instruction != ''}"><br>${instruction}</c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                        </table>
                    </div>
                </div>
            </c:forEach>
        </div>
    </tags:sectionContainer>
    </form>
 </cti:standardPage>