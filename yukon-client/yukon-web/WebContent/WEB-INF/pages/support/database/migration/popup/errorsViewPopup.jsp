<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<c:set var="objectCount" value="10"/>
<c:set var="maxObjectCountBeforeScroll" value="30"/>

<c:if test="${objectCount > maxObjectCountBeforeScroll}">
	<div style="overflow:auto; height:500px;">
</c:if>

<table class="compactResultsTable">

	<tr>
		<th>Object Identifier</th>
		<th>Error Reason</th>
	</tr>

	<tr>
		<td>LM Group 1234</td>
		<td>
			&lt;Descriptive error message X&gt;
		</td>
	</tr>
	<tr>
		<td>LM Group 4985093</td>
		<td>
			&lt;Descriptive error message Y&gt;
		</td>
	</tr>
	<tr>
		<td>LM Group 6457</td>
		<td>
			&lt;Descriptive error message Z&gt;
		</td>
	</tr>
	<tr>
		<td>LM Group 34234</td>
		<td>
			&lt;Descriptive error message Q&gt;
		</td>
	</tr>
	

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