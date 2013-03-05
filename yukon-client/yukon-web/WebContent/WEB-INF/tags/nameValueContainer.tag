<%@ attribute name="altRowOn" required="false" %>
<%@ attribute name="tableClass" required="false" %>
<%@ attribute name="style" required="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="nameValueContainter" value="true" scope="request"/>
<table class="nameValueTable <c:if test="${pageScope.altRowOn}">striped</c:if> ${pageScope.tableClass}" style="${pageScope.style}">
	<jsp:doBody/>
</table>
<c:set var="nameValueContainter" value="false" scope="request"/>