<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>


<c:set var="objectCount" value="40"/>
<c:set var="maxObjectCountBeforeScroll" value="30"/>

<c:if test="${objectCount > maxObjectCountBeforeScroll}">
	<div style="overflow:auto; height:500px;">
</c:if>

<table class="compactResultsTable">
	
	<c:forEach var="configurationColumn" items="${configurationItems}">
		<tr>
			<th>${configurationColumn.key}</th>
		</tr>
		<c:forEach var="configurationColumnValue" items="${configurationColumn.value}">
			<tr>
				<td>configurationColumnValue</td>
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

