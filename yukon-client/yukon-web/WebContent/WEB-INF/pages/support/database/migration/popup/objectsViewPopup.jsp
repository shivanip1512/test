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

	<tr>
		<th>Object Identifier</th>
	</tr>

	<tr><td>LM Group 1234</td></tr>
	<tr><td>LM Group 4564</td></tr>
	<tr><td>LM Group 56768</td></tr>
	<tr><td>LM Group 45756</td></tr>
	<tr><td>LM Group 1245634</td></tr>
	<tr><td>LM Group 345656</td></tr>
	<tr><td>LM Group 75689</td></tr>
	<tr><td>LM Group 956</td></tr>
	<tr><td>LM Group 45344</td></tr>
	<tr><td>LM Group 1254634</td></tr>
	<tr><td>LM Group 4566</td></tr>
	<tr><td>LM Group 87978</td></tr>
	<tr><td>LM Group 09889</td></tr>
	<tr><td>LM Group 45778</td></tr>
	<tr><td>LM Group 4556867</td></tr>
	<tr><td>LM Group 345745</td></tr>
	<tr><td>LM Group 678453</td></tr>
	<tr><td>LM Group 34667</td></tr>
	<tr><td>LM Group 3452867</td></tr>
	<tr><td>LM Group 867456</td></tr>
	<tr><td>LM Group 345756</td></tr>
	<tr><td>LM Group 34578</td></tr>
	<tr><td>LM Group 4578</td></tr>
	<tr><td>LM Group 3454684</td></tr>
	<tr><td>LM Group 5675368</td></tr>
	<tr><td>LM Group 6564</td></tr>
	<tr><td>LM Group 6755</td></tr>
	<tr><td>LM Group 34767</td></tr>
	<tr><td>LM Group 558568</td></tr>
	<tr><td>LM Group 344756</td></tr>
	<tr><td>LM Group 45758</td></tr>
	<tr><td>LM Group 34648</td></tr>
	<tr><td>LM Group 34648</td></tr>
	<tr><td>LM Group 46665</td></tr>
	<tr><td>LM Group 346457</td></tr>
	<tr><td>LM Group 346425</td></tr>
	<tr><td>LM Group 57458568</td></tr>
	<tr><td>LM Group 34648</td></tr>
	<tr><td>LM Group 567867945</td></tr>
	

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