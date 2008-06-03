<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.common.device.bulk.updateUpload.pageTitle"/>
<c:url var="check" value="/WebConfig/yukon/Icons/check.gif"/>

<cti:standardPage title="${pageTitle}" module="amr">

    <cti:standardMenu menuSelection="" />

    <%-- BREAD CRUMBS --%>
    <cti:breadCrumbs>
        
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        
        <%-- bulk home --%>
        <cti:msg var="bulkOperationsPageTitle" key="yukon.common.device.bulk.bulkHome.pageTitle"/>
        <cti:crumbLink url="/spring/bulk/bulkHome" title="${bulkOperationsPageTitle}" />
        
        <%-- upload --%>
        <cti:crumbLink>${pageTitle}</cti:crumbLink>
        
    </cti:breadCrumbs>
    
    <h2>${pageTitle}</h2>
    <br>

    <cti:msg var="headerTitle" key="yukon.common.device.bulk.updateUpload.header"/>
    <tags:boxContainer title="${headerTitle}" id="updateUploadContainer" hideEnabled="false">
    
        <table>
            <tr valign="top">
            
                <%-- UPLOAD FIELD --%>
                <td>
                    <form id="uploadForm" method="post" action="/spring/bulk/update/parseUpload" enctype="multipart/form-data">
                
                        <%-- note --%>
                        <table>
                            <tr>
                                <td valign="top" class="smallBoldLabel"><cti:msg key="yukon.common.device.bulk.updateUpload.noteLabel"/></td>
                                <td style="font-size:11px;">
                                <cti:msg key="yukon.common.device.bulk.updateUpload.noteText"/><br><br>
                                </td>
                            </tr>
                            
                            <%-- options --%>
                            <tr>
                                <td valign="top" class="smallBoldLabel">Options:</td>
                                <td style="font-size:11px;">
                                    <label><input type="checkbox" name="ignoreInvalidCols" <c:if test="${ignoreInvalidCols}">checked</c:if>><cti:msg key="yukon.common.device.bulk.options.update.ignoreInvalidHeaders"/></label><br>
                                    <label><input type="checkbox" name="ignoreInvalidIdentifiers" <c:if test="${ignoreInvalidIdentifiers}">checked</c:if>><cti:msg key="yukon.common.device.bulk.options.update.ignoreInvalidIdentifiers"/></label>
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
                        <div class="normalBoldLabel" style="display:inline;">Update File:</div>
                        <input type="file" name="dataFile" size="30px">
                        <input type="submit" name="importFile" value="Load">
                    
                    </form>
                </td>
                
                <td>
                    <div style="width:50px;"/>
                </td>
                
                <%-- INSTRUCTIONS --%>
                <td>
                    <c:url var="check" value="/WebConfig/yukon/Icons/check.gif"/>
                    
                    <%-- header --%>
                    <div class="normalBoldLabel">Instructions:</div>
                    
                    <%-- instructions --%>
                    <ul style="font-size:11px;">
                        <cti:msg key="yukon.common.device.bulk.updateUpload.instructions"/>
                    </ul>
                    
                    
                    <%-- field descriptions --%>
                    <table class="miniResultsTable" style="font-size:11px;">
                    
                        <tr>
                            <th><cti:msg key="yukon.common.device.bulk.columnHeader.tableHeader.update.columnHeader"/></th>
                            <th><cti:msg key="yukon.common.device.bulk.columnHeader.tableHeader.update.descriptionInstruction"/></th>
                            <th><cti:msg key="yukon.common.device.bulk.columnHeader.tableHeader.update.identifier"/></th>
                        </tr>
                        
                        <c:forEach var="field" items="${allFields}">
                        
                            <tr valign="top">
                            
                                <td class="smallBoldLabel">${field}</td>
                                
                                <td>
                                    <cti:msg var="description" key="yukon.common.device.bulk.columnHeader.update.description.${field}"/>
                                    <cti:msg var="instruction" key="yukon.common.device.bulk.columnHeader.update.instruction.${field}"/>
                                    ${description}<c:if test="${instruction != ''}"><br>${instruction}</c:if>
                                </td>
                                
                                <c:choose>
                                    <c:when test="${identifierFieldsMap[field]}">
                                        <td style="text-align:center;"><img src="${check}"></td>
                                    </c:when>
                                    <c:otherwise>
                                        <td></td>
                                    </c:otherwise>
                                </c:choose>
                                
                            </tr>
                        
                        </c:forEach>
                        
                    </table>
                    
                   
                    
                
                </td>
                
            </tr>
        </table>
    
    </tags:boxContainer>
    
 </cti:standardPage>