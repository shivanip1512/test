<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:standardPage module="tools" page="bulk.updateUpload">

    <cti:url var="recentResultsLink" value="/bulk/recentResults"/>
    <div class="fr"><a href="${recentResultsLink}"><i:inline key="yukon.common.device.bulk.recentResults"/></a></div><br/>

    <cti:msg var="headerTitle" key="yukon.common.device.bulk.updateUpload.header"/>
    <cti:url var="parseUploadUrl" value="/bulk/update/parseUpload"/>
    <form id="uploadForm" method="post" action="${parseUploadUrl}" enctype="multipart/form-data">
        <cti:csrfToken/>
    <tags:sectionContainer title="${headerTitle}" id="updateUploadContainer" hideEnabled="false">
        <div class="column-12-12 stacked clearfix">
            <div class="column one">
                <div class="stacked">
                    <strong><cti:msg key="yukon.common.device.bulk.updateUpload.noteLabel"/></strong>
                    <cti:msg key="yukon.common.device.bulk.updateUpload.noteText"/>
                </div>
                <div class="stacked">
                    <strong><i:inline key="yukon.common.device.bulk.updateUpload.optionsLabel"/></strong>
                    <div><label><input type="checkbox" name="ignoreInvalidCols" <c:if test="${ignoreInvalidCols}">checked</c:if>><cti:msg key="yukon.common.device.bulk.options.update.ignoreInvalidHeaders"/></label></div>
                    <div><label><input type="checkbox" name="ignoreInvalidIdentifiers" <c:if test="${ignoreInvalidIdentifiers}">checked</c:if>><cti:msg key="yukon.common.device.bulk.options.update.ignoreInvalidIdentifiers"/></label></div>
                </div>
                <%-- file errors --%>
                <c:if test="${not empty fileErrorKeysList}">
                    <div class="stacked scroll-md">
                        <c:forEach var="fileErrorKey" items="${fileErrorKeysList}">
                            <div class="error"><cti:msg key="${fileErrorKey}"/></div>
                        </c:forEach>
                    </div>
                </c:if>
                <%-- header errors --%>
                <c:if test="${not empty headersErrorResolverList}">
                    <div class="stacked lite-container scroll-md">
                        <c:forEach var="headersErrorResolver" items="${headersErrorResolverList}">
                            <div class="error"><cti:msg key="${headersErrorResolver}" htmlEscape="true"/></div>
                        </c:forEach>
                    </div>
                </c:if>
                <div class="stacked">
                    <%-- sample files --%>
                    <strong><i:inline key="yukon.common.device.bulk.updateUpload.sampleFilesLabel"/>:</strong>
                    <a href="<cti:url value="/WebConfig/custom/sample_bulk_files/Sample_Bulk_Update_File1.csv"/>"><i:inline key="yukon.common.file1"/></a>, 
                    <a href="<cti:url value="/WebConfig/custom/sample_bulk_files/Sample_Bulk_Update_File2.csv"/>"><i:inline key="yukon.common.file2"/></a>, 
                    <a href="<cti:url value="/WebConfig/custom/sample_bulk_files/Sample_Bulk_Update_File3.csv"/>"><i:inline key="yukon.common.file3"/></a>, 
                    <a href="<cti:url value="/WebConfig/custom/sample_bulk_files/Sample_Bulk_Update_File4.csv"/>"><i:inline key="yukon.common.file4"/></a>
                </div>
                <div>
                    <%-- file select --%>
                    <strong><i:inline key="yukon.common.device.bulk.updateUpload.updateFileLabel"/></strong>
                    <tags:file name="dataFile"/>
                    <div class="action-area">
                        <cti:button nameKey="load" type="submit" classes="primary action" busy="true"/>
                    </div> 
                </div>
            </div>
            <div class="column two nogutter">
                <%-- INSTRUCTIONS --%>
                <h3><i:inline key="yukon.common.device.bulk.updateUpload.instructions.title"/></h3>
                <ul><i:inline key="yukon.common.device.bulk.updateUpload.instructions.description"/></ul>
            </div>
        </div>
        <div class="column-24">
            <div class="column one">
                <%-- field descriptions --%>
                <table class="results-table detail">
                    <tr>
                        <th><cti:msg key="yukon.common.device.bulk.columnHeader.tableHeader.update.columnHeader"/></th>
                        <th><cti:msg key="yukon.common.device.bulk.columnHeader.tableHeader.update.descriptionInstruction"/></th>
                        <th><cti:msg key="yukon.common.device.bulk.columnHeader.tableHeader.update.identifier"/></th>
                    </tr>
                    <c:forEach var="field" items="${allFields}">
                        <tr valign="top">
                            <td class="strong-label-small">${field}</td>
                            <td>
                                <cti:msg var="description" key="yukon.common.device.bulk.columnHeader.update.description.${field}"/>
                                <cti:msg var="instruction" key="yukon.common.device.bulk.columnHeader.update.instruction.${field}"/>
                                ${description}<c:if test="${instruction != ''}"><br>${instruction}</c:if>
                            </td>
                            <c:choose>
                                <c:when test="${identifierFieldsMap[field]}">
                                    <td style="text-align:center;"><cti:icon icon="icon-check"/></td>
                                </c:when>
                                <c:otherwise>
                                    <td></td>
                                </c:otherwise>
                            </c:choose>
                        </tr>
                    </c:forEach>
                </table>
            </div>
        </div>
    </tags:sectionContainer>
 </cti:standardPage>