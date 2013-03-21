<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="support" page="fileExportHistory">
	
	<i:simplePopup titleKey=".search.title" id="searchPopup" styleClass="smallSimplePopup">
		<form action="list">
			<input type="hidden" name="itemsPerPage" value="${empty itemsPerPage ? 25 : itemsPerPage}">
			<tags:nameValueContainer2>
				<tags:nameValue2 nameKey=".search.name">
					<input type="text" name="name" value="${searchName}">
				</tags:nameValue2>
				
				<tags:nameValue2 nameKey=".search.initiator">
					<input type="text" name="initiator" value="${searchInitiator}">
				</tags:nameValue2>
			</tags:nameValueContainer2>
			<div class="actionArea">
				<cti:button nameKey="searchButton" type="submit"/>
			</div>
		</form>
	</i:simplePopup>
	
	<tags:pagedBox2 nameKey="mainBox" searchResult="${searchResult}" baseUrl="list" filterDialog="searchPopup">
		<table id="mainTable" class="compactResultsTable">
			<thead>
				<th><i:inline key=".columns.name"/></th>
				<th><i:inline key=".columns.type"/></th>
				<th><i:inline key=".columns.initiator"/></th>
				<th><i:inline key=".columns.date"/></th>
				<th><i:inline key=".columns.exportPath"/></th>
			</thead>
			<tfoot></tfoot>
			<tbody>
				<c:forEach var="exportHistoryEntry" items="${searchResult.resultList}">
					<tr>
						<!-- NAME/DOWNLOAD -->
						<td>
							<cti:url var="downloadUrl" value="downloadArchivedCopy">
								<cti:param name="entryId" value="${exportHistoryEntry.id}"/>
							</cti:url>
							<cti:msg2 var="downloadMouseover" key=".downloadMouseover"/>
							<span title="${downloadMouseover}"/>
								<a href="${downloadUrl}">${fn:escapeXml(exportHistoryEntry.originalFileName)}</a>
							</span>
						</td>
						<!-- TYPE -->
						<td>
							${exportHistoryEntry.type}
						</td>
						<!-- INITIATOR -->
						<td>
							${exportHistoryEntry.initiator}
						</td>
						<!-- DATE -->
						<td>
							<cti:formatDate value="${exportHistoryEntry.date}" type="DATEHM"/>
						</td>
						<!-- EXPORT PATH -->
						<td>
							${exportHistoryEntry.exportPath}
						</td>
					</tr>	
				</c:forEach>
				<c:if test="${fn:length(searchResult.resultList) == 0}">
					<tr>
						<td class="empty-list" colspan="5">
		                	<i:inline key=".noExports"/>
						</td>
					</tr>
				</c:if>
			</tbody>
		</table>
	</tags:pagedBox2>
</cti:standardPage>