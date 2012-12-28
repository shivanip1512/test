<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="debug" tagdir="/WEB-INF/tags/debug"%>

<cti:standardPage module="support" page="localization" title="${pageTitle}">

	<cti:includeScript link="JQUERY_COOKIE" />

	<script type="text/javascript">
		jQuery(function() {
			jQuery('#tabs').tabs({
				'cookie' : {}
			});
		});
	</script>

	<form:form action="view" modelAttribute="localizationBackingBean">
		<button type="submit">
			<i:inline key=".dumpKeys" />
		</button>
		<input type="hidden" name="task" value="dump"/> 
	</form:form> 

	<div id="tabs">
		<ul>
			<li><a href="#searchTab">Search</a></li>
			<li><a href="#compareTab">Compare</a></li>
		</ul>

		<div id="searchTab">
			<%@ include file="search.jspf"%>
			&nbsp;
		</div>

		<div id="compareTab">
			<%@ include file="compare.jspf"%>
			&nbsp;
		</div>
	</div>

	<br>
	<br>

	<c:if test="${not empty query}">
		<table class="resultsTable">
			<thead>
			<tr>
				<th><i:inline key=".searchResults" arguments="${query}" /></th>
			</tr>
			</thead>
			<tfoot>
			</tfoot>
			<tbody>
				<c:forEach items="${result}" var="entryResult">
					<tr>
						<td>${entryResult}</td>
					</tr>
				</c:forEach>
				<c:if test="${empty result}">
					<tr>
						<td><i:inline key=".noResults" /></td>
					</tr>
				</c:if>
			</tbody>
		</table>
	</c:if>

	<c:if test="${compareSets}">
		<c:if test="${empty compareResults}">
			<i:inline key=".noResults" />
		</c:if>
		<c:if test="${!empty compareResults}">
			<table class="resultsTable">
				<thead>
					<tr>
						<th><i:inline key=".key" /></th>
						<th><i:inline key=".modifiedValue" /></th>
						<th><i:inline key=".baseValue" /></th>
					</tr>
				</thead>
				<tfoot>
				</tfoot>
				<tbody>
					<c:forEach items="${compareResults}" var="entryResult">
						<tr>
							<td>${entryResult.key}</td>
							<td>${entryResult.value}</td>
							<td>${entryResult.secondaryValue}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</c:if>
	</c:if>

</cti:standardPage>