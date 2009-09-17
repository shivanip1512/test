<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:if test="${show}">
	<table class="resultsTable" style="width: 375px;padding-top: 5px;">
	    <tr>
	        <th nowrap="nowrap">
	            <b>Routes On: <font color="Black">${currentSubstation}</font></b>
	        </th>
	    </tr>
	    <c:forEach var="route" items="${routes}">
	        <tr>
	            <td nowrap="nowrap">
	                ${route.name}
	            </td>
	        </tr>
	    </c:forEach>
	</table>
</c:if>