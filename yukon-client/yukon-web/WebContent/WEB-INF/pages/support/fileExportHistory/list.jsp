<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="support" page="fileExportHistory">
    
    <form action="list" class="clearfix stacked">
        <label class="dib fl">
            <span class="name"><i:inline key=".search.name"/>:&nbsp;</span>
            <input type="text" name="name" value="${searchName}">
        </label>
        <label class="dib fl" style="margin-left: 15px;">
            <span class="name"><i:inline key=".search.initiator"/>:&nbsp;</span>
            <input type="text" name="initiator" value="${searchInitiator}" size="40">
        </label>
        <cti:button nameKey="search" type="submit" classes="action primary"/>
    </form>
    
    <tags:sectionContainer2 nameKey="exports">
        <c:choose>
            <c:when test="${empty exports}">
                <span class="empty-list"><i:inline key=".noExports"/></span>
            </c:when>
            <c:otherwise>
                <div class="scroll-lg">
                    <table class="compact-results-table">
                        <thead>
                            <th><i:inline key=".columns.name"/></th>
                            <th><i:inline key=".columns.type"/></th>
                            <th><i:inline key=".columns.initiator"/></th>
                            <th><i:inline key=".columns.date"/></th>
                            <th><i:inline key=".columns.exportPath"/></th>
                        </thead>
                        <tfoot></tfoot>
                        <tbody>
                            <c:forEach var="exportHistoryEntry" items="${exports}">
                                <tr>
                                    <!-- NAME/DOWNLOAD -->
                                    <td>
                                        <c:choose>
                                            <c:when test="${exportHistoryEntry.archiveFileExists}">
                                                <cti:url var="downloadUrl" value="downloadArchivedCopy">
                                                    <cti:param name="entryId" value="${exportHistoryEntry.id}"/>
                                                </cti:url>
                                                <cti:msg2 var="downloadMouseover" key=".downloadMouseover"/>
                                                <span title="${downloadMouseover}">
                                                    <a href="${downloadUrl}">${fn:escapeXml(exportHistoryEntry.originalFileName)}</a>
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <cti:msg2 var="noDownloadMouseover" key=".noDownloadMouseover"/>
                                                <span title="${noDownloadMouseover}">
                                                    ${fn:escapeXml(exportHistoryEntry.originalFileName)}
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                        
                                    </td>
                                    <td><cti:msg2 key="${exportHistoryEntry.type}"/></td>
                                    <td>${exportHistoryEntry.initiator}</td>
                                    <td><cti:formatDate value="${exportHistoryEntry.date}" type="DATEHM"/></td>
                                    <td>
                                    <c:choose>
                                        <c:when test="${exportHistoryEntry.exportPath == null}">
                                            <cti:msg2 key="yukon.web.defaults.dashes"/>
                                        </c:when>
                                        <c:otherwise>
                                            ${exportHistoryEntry.exportPath}
                                        </c:otherwise>
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
    
</cti:standardPage>