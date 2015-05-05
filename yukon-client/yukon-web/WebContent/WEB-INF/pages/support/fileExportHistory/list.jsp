<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="support" page="fileExportHistory">
    
    <form action="list" class="clearfix stacked">
        <tags:nameValueContainer2 tableClass="with-form-controls">
		    <tags:nameValue2 nameKey=".search.name">
            	<input type="text" name="name" value="${fn:escapeXml(searchName)}">
            </tags:nameValue2>
		    <tags:nameValue2 nameKey=".search.jobName">
            	<input type="text" name="jobName" value="${fn:escapeXml(searchJobName)}" size="40">
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".search.exportType">
            	<select name="exportType">
				    <c:forEach var="dataExportType" items="${dataExportTypeList}">
				    	<c:choose>
					    	<c:when test="${searchExportType.equals(dataExportType)}">
								<option value="${dataExportType}" selected="selected"><cti:msg2 key="${dataExportType.formatKey}"/></option>
							</c:when>
							<c:otherwise>
								<option value="${dataExportType}"><cti:msg2 key="${dataExportType.formatKey}"/></option>
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</select>
            </tags:nameValue2>
		</tags:nameValueContainer2>
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
                            <th><i:inline key=".columns.jobName"/></th>
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
                                    <td>${exportHistoryEntry.jobName}</td>
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