<%@ attribute name="altRowOn" required="false" %>
<%@ attribute name="tableClass" required="false" %>
<%@ attribute name="style" required="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="nameValueTableClass" value="nameValueTable" />
<c:if test="${empty pageScope.tableClass}">
    <c:set var="nameValueTableClass" value="nameValueTable ${pageScope.tableClass}" />
</c:if>

<c:set var="nameValueContainter" value="true" scope="request"/>
<c:set var="altRow" value="false" scope="request"/>
<c:set var="altRowOn" value="${pageScope.altRowOn}" scope="request"/>

<table class="${pageScope.nameValueTableClass}" style="${pageScope.style}">
	<jsp:doBody/>
</table>
	
<c:set var="nameValueContainter" value="false" scope="request"/>