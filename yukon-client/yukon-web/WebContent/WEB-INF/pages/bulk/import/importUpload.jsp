<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.common.device.bulk.importUpload.pageTitle"/>
<c:url var="check" value="/WebConfig/yukon/Icons/check.gif"/>

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
    
    <h2>${pageTitle}</h2>
    <br>

    <cti:msg var="headerTitle" key="yukon.common.device.bulk.importUpload.header"/>
    <tags:boxContainer title="${headerTitle}" id="importUploadContainer" hideEnabled="false">
    
        <table>
                
            <tr valign="top">
            
                <%-- UPLOAD FIELD --%>
                <td>
                    <form id="uploadForm" method="post" action="/spring/bulk/import/parseUpload" enctype="multipart/form-data">
                
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
                                <td valign="top" class="smallBoldLabel">Options:</td>
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
                        <div class="normalBoldLabel" style="display:inline;">Import File:</div>
                        <input type="file" name="dataFile" size="30px">
                        <input type="submit" name="importFile" value="Load">
                        
                    </form>
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
                        <a href="<c:url value="/WebConfig/custom/sample_bulk_files/Sample_Bulk_Import_File1.csv"/>" target="_blank">File 1</a>, 
                        <a href="<c:url value="/WebConfig/custom/sample_bulk_files/Sample_Bulk_Import_File2.csv"/>" target="_blank">File 2</a>, 
                        <a href="<c:url value="/WebConfig/custom/sample_bulk_files/Sample_Bulk_Import_File3.csv"/>" target="_blank">File 3</a>, 
                        <a href="<c:url value="/WebConfig/custom/sample_bulk_files/Sample_Bulk_Import_File4.csv"/>" target="_blank">File 4</a>
                    </div>
                    
                </td>
            
            </tr>
                
            <tr>
                <td colspan="2"><div style="height:20px;"></div></td>
            </tr>
                
                    
            <%-- METHODS --%>
            <tr valign="top">
                
                <%-- methods --%>
                <c:forEach var="method" items="${importMethods}">
                
                    <c:url var="methodImg" value="/WebConfig/yukon/Icons/import_by_${method.name}.gif"/>
                
                    <td>
                        <table class="miniResultsTable" style="font-size:11px;">
                    
                            <tr>
                                <td colspan="2" style="background-color:#CDCDCD;">
                                
                                    <div >
                                    <table class="noStyle" cellpadding="0" cellspacing="0">
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
                    
                </td>
                
            </tr>
                
        </table>
    
    </tags:boxContainer>
    
 </cti:standardPage>