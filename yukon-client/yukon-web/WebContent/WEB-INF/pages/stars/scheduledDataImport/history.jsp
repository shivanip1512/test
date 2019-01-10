<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="fileImportHistory">
    <tags:sectionContainer2 nameKey="history">
        <c:choose>
            <c:when test="${empty results}">
                <span class="empty-list"><i:inline key=".noImports"/></span>
            </c:when>
            <c:otherwise>
                <div>
                    <table class="compact-results-table">
                        <thead>
                            <tr>
                                <th><i:inline key=".fileName"/></th>
                                <th><i:inline key=".dateTime"/></th>
                                <th><i:inline key=".success"/></th>
                                <th><i:inline key=".failure"/></th>
                                <th><i:inline key=".total"/></th>
                                <th><i:inline key=".failedFileName"/></th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="result" items="${results}">
                                <c:url var="fileUrl" value="/stars/scheduledDataImport/downloadArchivedFile">
                                    <c:param name="fileName" value= "${result.archiveFileName}"/>
                                    <c:param name="isSuccessFile" value= "true"/>
                                    <c:param name="originalFileName" value= "${result.fileName}"/>
                                    <c:param name="jobGroupId" value= "${jobGroupId}"/>
                                </c:url>
                                <c:url var="errorFileUrl" value="/stars/scheduledDataImport/downloadArchivedFile">
                                    <c:param name="fileName" value= "${result.failedFileName}"/>
                                    <c:param name="isSuccessFile" value="false"/>
                                    <c:param name="failedFilePath" value= "${result.failedFilePath}"/>
                                    <c:param name="jobGroupId" value= "${jobGroupId}"/>
                                </c:url>
                                <tr>
                                    <td>
                                        <c:choose>
                                            <c:when test="${result.archiveFileExists}">
                                                <cti:msg2 var="downloadMouseover" key=".downloadMouseover"/>
                                                <span title="${downloadMouseover}">
                                                    <a href="${fileUrl}" onclick="yukon.ui.removeAlerts()">${fn:escapeXml(result.fileName)}</a>
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <cti:msg2 var="deletedFileMouseover" key=".deletedFileMouseover"/>
                                                <span title="${deletedFileMouseover}">
                                                    ${fn:escapeXml(result.fileName)}
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td><cti:formatDate type="DATEHM" value="${result.importDate}"/></td>
                                    <td>${result.successCount}</td>
                                    <td>${result.failureCount}</td>
                                    <td>${result.totalCount}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty result.failedFileName}">
                                                <cti:msg2 var="downloadErrFileMouseover" key=".downloadErrFileMouseover"/>
                                                <span title="${downloadErrFileMouseover}">
                                                    <a href="${errorFileUrl}" onclick="yukon.ui.removeAlerts()">${fn:escapeXml(result.failedFileName)}</a>
                                                </span>
                                            </c:when>
                                            <c:otherwise><i:inline key=".doesNotExist"/></c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </tags:sectionContainer2>
    <cti:includeScript link="/resources/js/pages/yukon.assets.scheduleddataimport.js" />
</cti:standardPage>