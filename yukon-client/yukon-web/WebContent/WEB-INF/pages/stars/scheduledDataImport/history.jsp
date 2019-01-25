<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>

<cti:standardPage module="operator" page="fileImportHistory">
    <tags:sectionContainer2 nameKey="history">

        <cti:url var="saveUrl" value="/stars/scheduledDataImport/${jobGroupId}/viewHistory"/>
        <form:form id="filter-form" action="${saveUrl}" method="GET">
            <div class="column-14-10 clearfix">
                <div class="column one">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey="yukon.web.modules.tools.bulk.analysis.dateRange">
                            <dt:dateTime name="from" value="${from}"/>
                            <dt:dateTime name="to" value="${to}"/>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </div>
                <div class="column two nogutter">
                    <cti:button nameKey="filter" classes="primary action MT1" type="submit"/>
                </div>
            </div>
        </form:form>

        <c:choose>
            <c:when test="${results.hitCount == 0}">
                <span class="empty-list"><i:inline key=".noImports"/></span>
            </c:when>
            <c:otherwise>
                <cti:url var="saveUrl" value="/stars/scheduledDataImport/${jobGroupId}/viewHistory">
                    <cti:formatDate type="DATEHM" value="${from}" var="from"/>
                    <cti:param name="from" value="${from}"/>
                    <cti:formatDate type="DATEHM" value="${to}" var="to"/>
                    <cti:param name="to" value="${to}"/>
                </cti:url>
                <div id="file-detail" data-url="${saveUrl}" data-static>
                    <table class="compact-results-table row-highlighting wrbw">
                        <thead>
                            <tags:sort column="${fileName}"/>
                            <tags:sort column="${dateTime}"/>
                            <tags:sort column="${success}"/>
                            <tags:sort column="${failure}"/>
                            <tags:sort column="${total}"/>
                            <tags:sort column="${failedFileName}"/>
                        </thead>
                        <tbody>
                            <c:forEach var="result" items="${results.resultList}">
                                <c:url var="fileUrl" value="/stars/scheduledDataImport/downloadArchivedFile">
                                    <c:param name="fileName" value="${result.archiveFileName}"/>
                                    <c:param name="isSuccessFile" value="true"/>
                                    <c:param name="originalFileName" value="${result.fileName}"/>
                                    <c:param name="jobGroupId" value="${jobGroupId}"/>
                                </c:url>
                                <c:url var="errorFileUrl" value="/stars/scheduledDataImport/downloadArchivedFile">
                                    <c:param name="fileName" value="${result.failedFileName}"/>
                                    <c:param name="isSuccessFile" value="false"/>
                                    <c:param name="failedFilePath" value="${result.failedFilePath}"/>
                                    <c:param name="jobGroupId" value="${jobGroupId}"/>
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
                    <tags:pagingResultsControls result="${results}" hundreds="true" adjustPageCount="true"/>
                </div>
            </c:otherwise>
        </c:choose>
    </tags:sectionContainer2>
    <cti:includeScript link="/resources/js/pages/yukon.assets.scheduleddataimport.js" />

</cti:standardPage>