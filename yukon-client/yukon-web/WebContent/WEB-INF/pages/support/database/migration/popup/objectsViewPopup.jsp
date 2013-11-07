<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>


<c:set var="maxObjectCountBeforeScroll" value="30"/>

<c:if test="${objectCount > maxObjectCountBeforeScroll}">
	<div style="overflow:auto; height:500px;">
</c:if>

<table class="compact-results-table">
	
	<c:forEach var="item" items="${itemList}">
		<tr>
			<td>${item}</td>
		</tr>
	</c:forEach>

</table>
