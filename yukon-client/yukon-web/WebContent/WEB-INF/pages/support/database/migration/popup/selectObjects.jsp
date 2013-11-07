<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="selectLabel" key="yukon.web.modules.support.databaseMigration.selectObjects.selectButton" argument="${objectKey}"/>

<c:set var="objectCount" value="40"/>
<c:set var="maxObjectCountBeforeScroll" value="30"/>

<c:if test="${objectCount > maxObjectCountBeforeScroll}">
	<div style="overflow:auto; height:500px;">
</c:if>

<table class="compact-results-table">
	
	<c:forEach var="configurationRow" items="${configurationItems}">
		<c:forEach var="configurationColumn" items="${configurationRow}">
			<tr>
				<th>${configurationColumn.key}</th>
			</tr>
			<tr>
				<td>${configurationColumn.value}</td>
			</tr>
		</c:forEach>
	</c:forEach>

</table>

<c:choose>
	<c:when test="${objectCount > maxObjectCountBeforeScroll}">
		<br>
		</div>
	</c:when>
	<c:otherwise>
		<br>
	</c:otherwise>
</c:choose>
