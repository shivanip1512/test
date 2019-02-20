<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>

<cti:standardPage module="operator" page="fileImportHistory">
    <tags:sectionContainer2 nameKey="history">
        <div class="dib">
            <form action="<cti:url value="/stars/scheduledDataImport/${jobGroupId}/viewHistory"/>">
                <div class="dib vam"><i:inline key="yukon.common.dateRange"/>:</div>
                <div class="dib vam">
                    <dt:date name="startDate" value = "${startDate}" wrapperClass="fn vam"/>
                    <div class="dib vam" style="margin-right: 3px;">
                        <i:inline key="yukon.common.to"/>
                    </div>
                    <dt:date name="endDate" value = "${endDate}" wrapperClass="fn vam"/>
                </div>
                <div class="dib vam">
                    <cti:button nameKey="filter" classes="primary action" type="submit"/>
                </div>
            </form>
        </div>
        <hr>
        <cti:formatDate type="DATE" value="${startDate}" var="startDate"/>
        <cti:formatDate type="DATE" value="${endDate}" var="endDate"/>
        <c:choose>
            <c:when test="${results.hitCount == 0}">
                <span class="empty-list"><i:inline key=".noImports"/></span>
            </c:when>
            <c:otherwise>
                <cti:url var="viewHistoryUrl" value="/stars/scheduledDataImport/${jobGroupId}/viewHistory">
                    <cti:param name="startDate" value="${startDate}"/>
                    <cti:param name="endDate" value="${endDate}"/>
                </cti:url>
                <div id="file-detail" data-url="${viewHistoryUrl}" data-static>
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
                                    <cti:param name="startDate" value="${startDate}"/>
                                    <cti:param name="endDate" value="${endDate}"/>
                                    <cti:param name="dir" value="${sorting.direction}"/>
                                    <cti:param name="sort" value="${sorting.sort}"/>
                                    <cti:param name="itemsPerPage" value="${paging.itemsPerPage}"/>
                                    <cti:param name="page" value="${paging.page}"/>
                                    <cti:param name="entryId" value="${result.entryId}"/>
                                </c:url>
                                <tr>
                                    <td class="wbba">
                                        <c:choose>
                                            <c:when test="${result.archiveFileExists}">
                                                <cti:msg2 var="downloadMouseover" key=".downloadMouseover"/>
                                                <span title="${downloadMouseover}">
                                                    <a href="${fileUrl}&isSuccessFile=true" onclick="yukon.ui.removeAlerts()">${fn:escapeXml(result.fileName)}</a>
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
                                    <td class="wbba">
                                        <c:choose>
                                            <c:when test="${not empty result.failedFileName}">
                                                <cti:msg2 var="downloadErrFileMouseover" key=".downloadErrFileMouseover"/>
                                                <span title="${downloadErrFileMouseover}">
                                                    <a href="${fileUrl}&isSuccessFile=false" onclick="yukon.ui.removeAlerts()">${fn:escapeXml(result.failedFileName)}</a>
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <c:choose>
                                                    <c:when test="${result.successCount == result.totalCount}">
                                                        <i:inline key="yukon.common.na"/>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <i:inline key=".doesNotExist"/>
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:otherwise>
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