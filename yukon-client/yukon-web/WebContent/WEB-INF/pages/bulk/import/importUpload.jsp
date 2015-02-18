<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="bulk.importUpload">
<cti:msgScope paths=", yukon.common.device.bulk">

<cti:includeScript link="/JavaScript/yukon.tools.import.upload.js"/>

<div class="stacked">
    <strong><i:inline key=".importUpload.noteLabel"/></strong>
    <i:inline key=".importUpload.noteText"/>
</div>

<cti:url var="url" value="/bulk/import/parseUpload"/>
<form id="uploadForm" method="post" action="${url}" enctype="multipart/form-data">
    
    <cti:csrfToken/>
        
    <div class="column-12-12 clearfix stacked-md">
        <div class="column one">
        
            <tags:nameValueContainer2 tableClass="with-form-controls">
                
                <tags:nameValue2 nameKey=".importUpload.importFileLabel">
                    <tags:file classes="js-file"/>
                </tags:nameValue2>
                
                <tags:nameValue2 nameKey=".options.deviceTypeSelect.text">
                    <select name="importTypeSelector" class="js-import-type">
                        <c:forEach var="type" items="${bulkImportTypes}">
                            <option value="${type}" 
                                <c:if test="${type eq importTypeSelector}">selected</c:if>>
                                <i:inline key=".options.deviceTypeSelect.${type}"/>
                            </option>
                        </c:forEach>
                    </select>
                </tags:nameValue2>
                
                <tags:nameValue2 excludeColon="true">
                    <label>
                        <input type="checkbox" name="ignoreInvalidCols" <c:if test="${ignoreInvalidCols}">checked</c:if>>
                        <i:inline key=".options.import.ignoreInvalidHeaders"/>
                    </label>
                </tags:nameValue2>
                
            </tags:nameValueContainer2>
            
            <%-- file errors --%>
            <c:if test="${not empty fileErrorKeysList}">
                <div class="js-file-validation">
                    <c:forEach var="key" items="${fileErrorKeysList}">
                        <div class="error"><i:inline key="${key}"/></div>
                    </c:forEach>
                </div>
            </c:if>
            
            <%-- header errors --%>
            <c:if test="${not empty headersErrorResolverList}">
                <div class="js-file-validation">
                    <c:forEach var="key" items="${headersErrorResolverList}">
                        <div class="error"><i:inline key="${key}"/></div>
                    </c:forEach>
                </div>
            </c:if>
            
            <div class="action-area">
                <cti:button nameKey="upload" classes="primary action" busy="true" type="submit"/>
            </div>
        </div>
        
        <div class="column two nogutter">
            <%-- INSTRUCTIONS --%>
            <h3><i:inline key="yukon.common.instructions"/></h3>
            <ul class="stacked"><cti:msg2 key=".importUpload.instructions"/></ul>
           
            <%-- sample files --%>
            <div>
                <cti:msg2 key=".importUpload.sampleFilesLabel"/>
                <c:set var="base" value="/WebConfig/custom/sample_bulk_files/import"/>
                <c:forEach var="type" items="${bulkImportTypes}">
                    <cti:url var="url" value="${base}/${type}/Sample_Bulk_Import_File"/>
                    <span data-file-type="${type}">
                        <span>(${type}):</span>
                        <a href="${url}1.csv"><i:inline key="yukon.common.file1"/></a>,
                        <a href="${url}2.csv"><i:inline key="yukon.common.file2"/></a>,
                        <a href="${url}3.csv"><i:inline key="yukon.common.file3"/></a>,
                        <a href="${url}4.csv"><i:inline key="yukon.common.file4"/></a>
                    </span>
                </c:forEach>
            </div>
        </div>
    </div>
    
    <div class="column-12-12 clearfix">
        
        <c:forEach var="method" items="${importMethods}" varStatus="status">
            
            <c:choose>
                <c:when test="${status.count % 2 > 0}">
                    <c:set var="classes" value="column one"/>
                </c:when>
                <c:otherwise>
                    <c:set var="classes" value="column two nogutter"/>
                </c:otherwise>
            </c:choose>
            
            <div class="${classes} dn" data-import-type="${method.type}">
                <h3><i:inline key=".columnHeader.tableHeader.import.method.tableLabel.${method.name}"/></h3>
                <div class="stacked">
                    <em><i:inline key=".columnHeader.tableHeader.import.method.tableDescription.${method.name}"/></em>
                </div>
                <table class="compact-results-table manual-striping">
                    <thead>
                        <tr>
                            <th><cti:msg2 key=".columnHeader.tableHeader.import.requiredColumnHeaders"/></th>
                            <th><cti:msg2 key=".columnHeader.tableHeader.import.descriptionInstruction"/></th>
                        </tr>
                    <thead>
                    <c:forEach var="field" items="${requiredFieldsByMethod[method]}" varStatus="status">
                        <c:set var="classes" value="${status.count % 2 == 0 ? 'alt-row' : ''}"/>
                        <tr class="${classes}">
                            <td>${field}</td>
                            <td>
                                <cti:msg2 var="description" key=".columnHeader.import.description.${field}"/>
                                <cti:msg2 var="instruction" key=".columnHeader.import.instruction.${field}"/>
                                ${description}<c:if test="${instruction != ''}"><br>${instruction}</c:if>
                            </td>
                        </tr>
                    </c:forEach>
                    <tr><td colspan="2">&nbsp;</td></tr>
                    <tr>
                        <th><cti:msg2 key=".columnHeader.tableHeader.import.optionalHeaders"/></th>
                        <th><cti:msg2 key=".columnHeader.tableHeader.import.descriptionInstruction"/></th>
                    </tr>
                    <c:forEach var="field" items="${updateableFieldsByMethod[method]}" varStatus="status">
                        <c:set var="classes" value="${status.count % 2 == 0 ? 'alt-row' : ''}"/>
                        <tr class="${classes}">
                            <td>${field}</td>
                            <td>
                                <cti:msg2 var="description" key=".columnHeader.import.description.${field}"/>
                                <cti:msg2 var="instruction" key=".columnHeader.import.instruction.${field}"/>
                                ${description}<c:if test="${instruction != ''}"><br>${instruction}</c:if>
                            </td>
                        </tr>
                    </c:forEach>
                </table>
            </div>
            
        </c:forEach>
        
    </div>
</form>

</cti:msgScope>
 </cti:standardPage>