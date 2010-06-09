<%@ tag body-content="empty" %>
<%@ attribute name="path" required="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>


<table class="bitTable">
	<c:forEach var="rowIndex" begin="0" end="1">
	    <tr>
	        <c:forEach var="labelNum" begin="${rowIndex * 8 + 1}" end="${rowIndex * 8 + 8}">
	            <th>${labelNum}</th>
	        </c:forEach>
	    </tr>
	    <tr>
	        <c:forEach var="bitNumber" begin="${rowIndex * 8}" end="${rowIndex * 8 + 7}">
	            <td><tags:checkbox path="${path}[${bitNumber}]"/></td>
	        </c:forEach>
	    </tr>
	</c:forEach>
</table>
