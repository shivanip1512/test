<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="support" page="logMenu">
    <cti:includeScript link="JQUERY_COOKIE"/>

	<cti:dataGrid cols="2">
		<cti:dataGridCell>
			<tags:sectionContainer2 nameKey="settings">
				<tags:nameValueContainer2>
					<tags:nameValue2 nameKey=".show">
						<a href="javascript:void(0)" id="collapseBtn"><i:inline key=".mostRecent"/></a> |
						<a href="javascript:void(0)" id="showAllBtn"><i:inline key=".all"/></a>
					</tags:nameValue2>
					<tags:nameValue2 nameKey=".sortBy">
						<a href="javascript:void(0)" id="sortByDateBtn"><i:inline key=".date"/></a> |
						<a href="javascript:void(0)" id="sortByApplicationBtn" "> <i:inline key=".application"/></a>
					</tags:nameValue2>
				</tags:nameValueContainer2>
			</tags:sectionContainer2>
		</cti:dataGridCell>
		<cti:dataGridCell>
			<tags:sectionContainer2 nameKey="directories" >
				<ul>
					<c:if test="${isSubDirectory}">
						<li><a class="labeled_icon prev fn" href="?file=${currentDirectory}">${logBaseDir}${file}</a></li>
					</c:if>

					<c:if test="${!isSubDirectory}">
						<li>${logBaseDir}</li>
					</c:if>

					<c:forEach var="dirName" items="${dirList}">
						<li class="subDirectory">
							<a class="labeled_icon icon_folder fn" href="?file=${file}${dirName}/">${dirName}</a>
						</li>
					</c:forEach>
				</ul>
			</tags:sectionContainer2>
		</cti:dataGridCell>
	</cti:dataGrid>
	
	<!--  Date Sorting -->
	<div id="sortByDateSection"  style="display: none">
		<div class="logSectionMostRecent" style="display: none">
			<c:forEach var="logSection" items="${localLogListByDate}" end='0'>
				<div class="logSection">
					<tags:sectionContainer title="${logSection.key}">
						<table class="contentTable rowHighlighting">
							<c:forEach var="logFile" items="${logSection.value}">
								<cti:url value="view" var="url"><cti:param name="file" value="${file}${logFile.name}"></cti:param></cti:url>
								<tr title="${logFile.name}">
									<td><a href="${url}">${logFile.identifier}</a></td>
									<td><cti:msg2 key="${logFile.size}" /></td>
								</tr>
							</c:forEach>
						</table>
					</tags:sectionContainer>
				</div>
			</c:forEach>
		</div>
		<div class="logSectionAll" style="display: none">
			<c:forEach var="logSection" items="${localLogListByDate}">
				<div class="logSection">
					<tags:sectionContainer hideEnabled="true" title="${logSection.key}">
						<table class="contentTable rowHighlighting">
							<c:forEach var="logFile" items="${logSection.value}" varStatus="logFileIndex">
								<cti:url value="view" var="url"><cti:param name="file" value="${file}${logFile.name}"></cti:param></cti:url>
								<tr title="${logFile.name}">
									<td><a href="${url}">${logFile.identifier}</a></td>
									<td><cti:msg2 key="${logFile.size}" /></td>
								</tr>
							</c:forEach>
						</table>
					</tags:sectionContainer>
				</div>
			</c:forEach>
		</div>
	</div>

	<!--  Application Sorting -->
	<div id="sortByApplicationSection" style="display: none">
		<c:forEach var="logSection" items="${localLogListByApplication}">
			<div class="logSection">
				<div class="logSectionMostRecent" style="display: none">
					<tags:sectionContainer title="${logSection.key}" styleClass="display:none">
						<table class="contentTable rowHighlighting">
							<c:forEach var="logFile" items="${logSection.value}"  end='0'>
								<cti:url value="view" var="url"> <cti:param name="file" value="${file}${logFile.name}"/></cti:url>
								<tr title="${logFile.name}">
									<td><a href="${url}">${logFile.identifier}</a></td>
									<td><cti:msg2 key="${logFile.size}" /></td>
								</tr>
							</c:forEach>
						</table>
					</tags:sectionContainer>
				</div>
				<div class="logSectionAll" style="display: none">
				<tags:sectionContainer hideEnabled="true" title="${logSection.key}" styleClass="display:none">
					<table class="contentTable rowHighlighting">
						<c:forEach var="logFile" items="${logSection.value}" varStatus="logFileIndex">
							<cti:url value="view" var="url"> <cti:param name="file" value="${file}${logFile.name}"></cti:param></cti:url>
							<tr title="${logFile.name}">
								<td><a href="${url}">${logFile.identifier}</a></td>
								<td><cti:msg2 key="${logFile.size}" /></td>
							</tr>
						</c:forEach>
					</table>
				</tags:sectionContainer>
				</div>
			</div>
		</c:forEach>
	</div>
<script>
	jQuery(function() {

		sortByDate = function() {
			jQuery.cookie('support_logging_sortBy', 'date');
			jQuery("#sortByDateBtn").addClass("currentSelection");
			jQuery("#sortByApplicationBtn").removeClass("currentSelection");

			swapSections("#sortByApplicationSection", "#sortByDateSection");
		};

		sortByApplication = function() {
			jQuery.cookie('support_logging_sortBy', 'application');
			jQuery("#sortByApplicationBtn").addClass("currentSelection");
			jQuery("#sortByDateBtn").removeClass("currentSelection");

			swapSections("#sortByDateSection", "#sortByApplicationSection");
		};

		showAll = function() {
			jQuery.cookie('support_logging_showAll', 'showAll');
			jQuery('#collapseBtn').removeClass("currentSelection");
			jQuery('#showAllBtn').addClass("currentSelection");

			swapSections(".logSectionMostRecent", ".logSectionAll");
		};

		showMostRecent = function() {
			jQuery.cookie('support_logging_showAll', 'collapse');
			jQuery('#showAllBtn').removeClass("currentSelection");
			jQuery('#collapseBtn').addClass("currentSelection");
			
			swapSections(".logSectionAll", ".logSectionMostRecent");
		};
		
		swapSections = function(hideSelector, showSelector) {
			jQuery(hideSelector).fadeOut(150, function() {
				jQuery(showSelector).fadeIn(50);
			});
		};

		if (jQuery.cookie('support_logging_sortBy') === 'application') {
			sortByApplication();
		} else {
			sortByDate();
		}

		if (jQuery.cookie('support_logging_showAll') === 'showAll') {
			showAll();
		} else {
			showMostRecent();	
		}

		jQuery('#sortByDateBtn').click(sortByDate);
		jQuery('#sortByApplicationBtn').click(sortByApplication);
		jQuery('#collapseBtn').click(showMostRecent);
		jQuery('#showAllBtn').click(showAll);
	});
</script>

</cti:standardPage>