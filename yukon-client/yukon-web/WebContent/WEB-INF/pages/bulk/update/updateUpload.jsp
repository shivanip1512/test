<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:url var="check" value="/WebConfig/yukon/Icons/check.gif"/>

<cti:standardPage module="tools" page="bulk.updateUpload">

    <cti:msg var="headerTitle" key="yukon.common.device.bulk.updateUpload.header"/>
    <form id="uploadForm" method="post" action="/bulk/update/parseUpload" enctype="multipart/form-data">
    <tags:sectionContainer title="${headerTitle}" id="updateUploadContainer" hideEnabled="false">
        <div class="column_12_12 stacked clearfix">
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
                    <div class="stacked scrollingContainer_medium">
                        <c:forEach var="fileErrorKey" items="${fileErrorKeysList}">
                            <div class="error"><cti:msg key="${fileErrorKey}"/></div>
                        </c:forEach>
                    </div>
                </c:if>
                <%-- header errors --%>
                <c:if test="${not empty headersErrorResolverList}">
                    <div class="stacked liteContainer scrollingContainer_medium">
                        <c:forEach var="headersErrorResolver" items="${headersErrorResolverList}">
                            <div class="error"><cti:msg key="${headersErrorResolver}"/></div>
                        </c:forEach>
                    </div>
                </c:if>
                <div class="stacked">
                    <%-- sample files --%>
                    <strong><i:inline key="yukon.common.device.bulk.updateUpload.sampleFilesLabel"/>:</strong>
                    <a href="<cti:url value="/WebConfig/custom/sample_bulk_files/Sample_Bulk_Update_File1.csv"/>">File 1</a>, 
                    <a href="<cti:url value="/WebConfig/custom/sample_bulk_files/Sample_Bulk_Update_File2.csv"/>">File 2</a>, 
                    <a href="<cti:url value="/WebConfig/custom/sample_bulk_files/Sample_Bulk_Update_File3.csv"/>">File 3</a>, 
                    <a href="<cti:url value="/WebConfig/custom/sample_bulk_files/Sample_Bulk_Update_File4.csv"/>">File 4</a>
                </div>
                <div>
                    <%-- file select --%>
                    <strong><i:inline key="yukon.common.device.bulk.updateUpload.updateFileLabel"/></strong>
                    <input type="file" name="dataFile" size="30px">
                    <div class="actionArea">
                        <cti:button nameKey="load" type="submit" classes="f-disableAfterClick primary action" busy="true"/>
                    </div> 
                </div>
            </div>
            <div class="column two nogutter">
                <%-- INSTRUCTIONS --%>
                <h3>Instructions:</h3>
                <ul><cti:msg key="yukon.common.device.bulk.updateUpload.instructions"/></ul>
            </div>
        </div>
        <div class="column_24">
            <div class="column one">
                <%-- field descriptions --%>
                <table class="resultsTable detail">
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