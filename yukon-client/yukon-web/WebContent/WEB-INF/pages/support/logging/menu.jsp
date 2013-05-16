<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="support" page="logMenu">
    <cti:includeScript link="JQUERY_COOKIE"/>

	<cti:msg2 var="directoriesHeader" key=".directories"/>
	<cti:msg2 var="logs" key=".logs"/>
	<tags:sectionContainer title='${directoriesHeader}'>
		<ul>
			<c:if test="${isSubDirectory}">
				<li>
					<a class="labeled_icon prev fn" href="?file=${currentDirectory}">${file}</a>
				</li>
			</c:if>

			<c:forEach var="dirName" items="${dirList}">
				<li class="${isSubDirectory ? 'subDirectory' : ''}">
					<a class="labeled_icon icon_folder fn" href="?file=${file}${dirName}/">${dirName}</a>
				</li>
			</c:forEach>

		</ul>
	</tags:sectionContainer>

	<tags:sectionContainer title='${logs}'>
		<div>
			<span id="showAllSection" style="display: none">
				<a href="javascript:void(0)" id="showAllBtn"><i:inline key=".showAll"/></a> (<span id="numHidden"></span> <i:inline key=".hidden"/>)
			</span>
			<a href="javascript:void(0)" id="collapseBtn" style="display: none"><i:inline key=".showMostRecent"/></a>
		</div>
		<div>
			<a href="javascript:void(0)" id="sortByDateBtn" style="display: none"><i:inline key=".sortByDate"/></a>
			<a href="javascript:void(0)" id="sortByApplicationBtn" style="display: none"> <i:inline key=".sortByApplication"/></a>
		</div>

		<!--  Date Sorting -->
		<div id="sortByDateSection"  style="display: none">
			<c:forEach var="logSection" items="${localLogListByDate}" varStatus="sectionIndex">
				<div class="logSection ${sectionIndex.count != 1 ? 'hiddenSection' : ''}" style="${sectionIndex.count != 1 ? 'display:none' : ''}">
					<tags:sectionContainer title="${logSection.key}">
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

		<!--  Application Sorting -->
		<div id="sortByApplicationSection" style="display: none">
			<c:forEach var="logSection" items="${localLogListByApplication}" varStatus="sectionIndex">
				<div class="logSection">
				<tags:sectionContainer title="${logSection.key}">
					<table class="contentTable rowHighlighting">
						<c:forEach var="logFile" items="${logSection.value}" varStatus="logFileIndex">
							<cti:url value="view" var="url"> <cti:param name="file" value="${file}${logFile.name}"></cti:param></cti:url>
							<tr title="${logFile.name}" class="${logFileIndex.count != 1 ? 'hiddenRow' : ''}" style="${logFileIndex.count != 1 ? 'display:none' : ''}">
								<td><a href="${url}">${logFile.identifier}</a></td>
								<td><cti:msg2 key="${logFile.size}" /></td>
							</tr>
						</c:forEach>
					</table>
				</tags:sectionContainer>
				</div>
			</c:forEach>
		</div>
	</tags:sectionContainer>
	
<script>
	jQuery(function() {

		sortByDate = function() {
			jQuery.cookie('support_logging_sortBy', 'date');
			jQuery("#sortByDateBtn").hide();
			jQuery("#sortByApplicationBtn").show();
			jQuery('#sortByApplicationSection').fadeOut(150, function() {
				jQuery('#sortByDateSection').fadeIn(50);
			});
		};

		sortByApplication = function() {
			jQuery.cookie('support_logging_sortBy', 'application');
			jQuery("#sortByApplicationBtn").hide();
			jQuery("#sortByDateBtn").show();
			jQuery('#sortByDateSection').fadeOut(150, function() {
				jQuery('#sortByApplicationSection').fadeIn(50);
			});
		};

		showAll = function() {
			jQuery.cookie('support_logging_showAll', 'showAll');
			jQuery('.hiddenRow').each(function(index) {
				jQuery(this).show();
			});

			jQuery('.hiddenSection').each(function(index) {
				jQuery(this).show();
			});
			jQuery('#collapseBtn').show();
			jQuery('#showAllSection').hide();
		};

		collapse = function() {
			jQuery.cookie('support_logging_showAll', 'collapse');
			jQuery('.hiddenRow').each(function(index) {
				jQuery(this).hide();
			});

			jQuery('.hiddenSection').each(function(index) {
				jQuery(this).hide();
			});
			jQuery('#showAllSection').show();
			jQuery('#numHidden').html(jQuery('.hiddenSection').size());
			jQuery('#collapseBtn').hide();
		};

		if (jQuery.cookie('support_logging_sortBy') === 'application') {
			sortByApplication();
		} else {
			sortByDate();
		}

		if (jQuery.cookie('support_logging_showAll') === 'showAll') {
			showAll();
		} else {
			collapse();	
		}

		jQuery('#sortByDateBtn').click(sortByDate);
		jQuery('#sortByApplicationBtn').click(sortByApplication);
		jQuery('#collapseBtn').click(collapse);
		jQuery('#showAllBtn').click(showAll);
	});
</script>

</cti:standardPage>