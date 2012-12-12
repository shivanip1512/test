<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<cti:msg var="pageTitle" key="yukon.common.device.bulk.importUpload.pageTitle"/>
<cti:url var="check" value="/WebConfig/yukon/Icons/check.gif"/>

<cti:standardPage title="${pageTitle}" module="amr">

    <cti:standardMenu menuSelection="" />

    <%-- BREAD CRUMBS --%>
    <cti:breadCrumbs>
        
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        
        <%-- bulk home --%>
        <cti:msg var="bulkOperationsPageTitle" key="yukon.common.device.bulk.bulkHome.pageTitle"/>
        <cti:crumbLink url="/spring/bulk/bulkHome" title="${bulkOperationsPageTitle}" />
        
        <%-- import --%>
        <cti:crumbLink>${pageTitle}</cti:crumbLink>
        
    </cti:breadCrumbs>

    <script>
    jQuery(function(){
        updateImportTypeSelection();
        jQuery("#importTypeSelector").change(function(){
            updateImportTypeSelection();
        });
    });
    
    function updateImportTypeSelection() {
        var itemSelected = jQuery("#importTypeSelector").val();
        //hide all rows
        jQuery("[class^='bulkImportType_']").hide();
        //show rows for the selected interface
        jQuery(".bulkImportType_" + itemSelected).show();
    }
    </script>
    
    <h2>${pageTitle}</h2>
    <br>

    <cti:msg var="headerTitle" key="yukon.common.device.bulk.importUpload.header"/>
    <tags:boxContainer title="${headerTitle}" id="importUploadContainer" hideEnabled="false">
        <form id="uploadForm" method="post" action="/spring/bulk/import/parseUpload" enctype="multipart/form-data">
        <table>
                
            <tr valign="top">
            
                <%-- UPLOAD FIELD --%>
                <td>
                   
                
                        <%-- note --%>
                        <table>
                            <tr>
                                <td valign="top" class="smallBoldLabel"><cti:msg key="yukon.common.device.bulk.importUpload.noteLabel"/></td>
                                <td style="font-size:11px;">
                                <cti:msg key="yukon.common.device.bulk.importUpload.noteText"/><br><br>
                                </td>
                            </tr>
                            
                            <%-- options --%>
                            <tr>
                                <td valign="top" class="smallBoldLabel"><i:inline key="yukon.common.device.bulk.importUpload.optionsLabel"/></td>
                                <td style="font-size:11px;">
                                    <label><input type="checkbox" name="ignoreInvalidCols" <c:if test="${ignoreInvalidCols}">checked</c:if>><cti:msg key="yukon.common.device.bulk.options.import.ignoreInvalidHeaders"/></label><br>
                                </td>
                            </tr> 
                            
                        </table>
                        <br>
                        
                        <%-- file errors --%>
                        <c:if test="${not empty fileErrorKeysList}">
                            <c:forEach var="fileErrorKey" items="${fileErrorKeysList}">
                                <div style="color:#CC0000;">
                                    <cti:msg key="${fileErrorKey}"/>
                                </div>
                            </c:forEach>
                            <br>
                        </c:if>
                        
                        <%-- header errors --%>
                        <c:if test="${not empty headersErrorResolverList}">
                            <c:forEach var="headersErrorResolver" items="${headersErrorResolverList}">
                                <div style="color:#CC0000;">
                                    <cti:msg key="${headersErrorResolver}"/>
                                </div>
                            </c:forEach>
                            <br>
                        </c:if>
                        
                        <%-- file select --%>
                        <div class="normalBoldLabel" style="display:inline;"><i:inline key="yukon.common.device.bulk.importUpload.importFileLabel"/></div>
                        <input type="file" name="dataFile" >
                        <cti:msg2 var="loadButtonLabel" key="yukon.common.device.bulk.importUpload.loadButton"/>
                        <cti:msg2 var="loadButtonBusyLabel" key="yukon.common.device.bulk.importUpload.loadButton.busy"/> 
                        <tags:slowInput myFormId="uploadForm" label="${loadButtonLabel}" labelBusy="${loadButtonBusyLabel}" />
                        
                   
                </td>
            
                <%-- INSTRUCTIONS --%>
                <td>
                    <div class="normalBoldLabel">Instructions:</div>
                    <ul style="font-size:11px;">
                        <cti:msg key="yukon.common.device.bulk.importUpload.instructions"/>
                    </ul>
                   
                    <%-- sample files --%>
                    <div class="small">
                        <div class="normalBoldLabel" style="display:inline;"><cti:msg key="yukon.common.device.bulk.importUpload.sampleFilesLabel"/>:</div>
                        <a href="<cti:url value="/WebConfig/custom/sample_bulk_files/Sample_Bulk_Import_File1.csv"/>">File 1</a>, 
                        <a href="<cti:url value="/WebConfig/custom/sample_bulk_files/Sample_Bulk_Import_File2.csv"/>">File 2</a>, 
                        <a href="<cti:url value="/WebConfig/custom/sample_bulk_files/Sample_Bulk_Import_File3.csv"/>">File 3</a>, 
                        <a href="<cti:url value="/WebConfig/custom/sample_bulk_files/Sample_Bulk_Import_File4.csv"/>">File 4</a>
                    </div>
                    
                </td>
            
            </tr>
                <tr>
                    <td colspan="2"><div style="height: 20px;"></div>
                    </td>
                </tr>
                <tr>
                    <td><label class="normalBoldLabel"><cti:msg2 key="yukon.common.device.bulk.options.deviceTypeSelect.text" /> <select name="importTypeSelector" id="importTypeSelector">
                                <c:forEach var="bulkImportType" items="${bulkImportTypes}">
                                    <option value="${bulkImportType}" <c:if test="${importTypeSelector eq bulkImportType}">selected="selected"</c:if>>
                                        <i:inline key="yukon.common.device.bulk.options.deviceTypeSelect.${bulkImportType}" />
                                    </option>
                                </c:forEach>
                        </select> </label>
                    </td>
                    <td></td>
                </tr>
                <tr>
                    <td colspan="2"><div style="height: 20px;"></div></td>
                </tr>
                <%-- METHODS --%>
                <tr valign="top">
                
                <%-- methods --%>
                <c:forEach var="method" items="${importMethods}">
                   
                    <cti:url var="methodImg" value="/WebConfig/yukon/Icons/import_by_${method.name}.gif"/>
                
                    <td class="bulkImportType_${method.type}"  style="display: none;">
                        <table class="miniResultsTable" style="font-size:11px;">
                    
                            <tr>
                                <td colspan="2" style="background-color:#CDCDCD;" >
                                
                                    <div >
                                    <table class="noStyle">
                                        <tr valign="top">
                                            <td rowspan="2"><img src="${methodImg}"></td>
                                            <td><div class="normalBoldLabel"><cti:msg key="yukon.common.device.bulk.columnHeader.tableHeader.import.method.tableLabel.${method.name}"/></div></td>
                                        </tr>
                                        <tr>
                                            <td><cti:msg key="yukon.common.device.bulk.columnHeader.tableHeader.import.method.tableDescription.${method.name}"/></td>
                                        </tr>
                                    </table>
                                    </div>
                                
                                </td>
                            </tr>
                    
                            <tr valign="bottom">
                                <th style="width:150px;" align="left"><cti:msg key="yukon.common.device.bulk.columnHeader.tableHeader.import.requiredColumnHeaders"/></th>
                                <th><cti:msg key="yukon.common.device.bulk.columnHeader.tableHeader.import.descriptionInstruction"/></th>
                            </tr>
                            
                            <c:forEach var="field" items="${method.requiredColumns}">
                            
                                <tr valign="top">
                                 
                                    <td class="smallBoldLabel">${field}</td>
                                   
                                    <td>
                                        <cti:msg var="description" key="yukon.common.device.bulk.columnHeader.import.description.${field}"/>
                                        <cti:msg var="instruction" key="yukon.common.device.bulk.columnHeader.import.instruction.${field}"/>
                                        ${description}<c:if test="${instruction != ''}"><br>${instruction}</c:if>
                                    </td>
                                    
                                </tr>
                            
                            </c:forEach>
                            
                            <tr>
                                <th align="left"><cti:msg key="yukon.common.device.bulk.columnHeader.tableHeader.import.optionalHeaders"/></th>
                                <th><cti:msg key="yukon.common.device.bulk.columnHeader.tableHeader.import.descriptionInstruction"/></th>
                            </tr>
                            
                            <c:forEach var="field" items="${methodUpdateableFieldsMap[method]}">
                               
                                <tr>
                        
                                    <td class="smallBoldLabel">${field}</td>
                                    
                                    <td>
                                        <cti:msg var="description" key="yukon.common.device.bulk.columnHeader.import.description.${field}"/>
                                        <cti:msg var="instruction" key="yukon.common.device.bulk.columnHeader.import.instruction.${field}"/>
                                        ${description}<c:if test="${instruction != ''}"><br>${instruction}</c:if>
                                    </td>
                                    
                                </tr>
                            
                            </c:forEach>
                            
                    </table>
                    </td>
                </c:forEach>
                    
            </tr>
                
        </table>
     </form>
    </tags:boxContainer>
    
 </cti:standardPage>