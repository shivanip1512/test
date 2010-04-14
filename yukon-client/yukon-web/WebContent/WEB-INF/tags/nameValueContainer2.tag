<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ attribute name="nameColumnWidth" required="false" %>
<%@ attribute name="tableClass" required="false" %>
<%@ attribute name="style" required="false" %>
<%@ attribute name="id" required="false" %>

<c:if test="${empty pageScope.tableClass}">
    <c:set var="tableClass" value="nameValueTable" />
</c:if>

<c:set var="nameColumnWidth" value="${pageScope.nameColumnWidth}" scope="request"/>
<c:set var="idAttribute" value=""/>
<c:if test="${!empty pageScope.id}">
    <c:set var="idAttribute" value=" id=\"${pageScope.id}\""/>
</c:if>

<c:set var="nameValueContainter2" value="true" scope="request"/>
<table class="${pageScope.tableClass}" style="${pageScope.style}"${idAttribute}>
	<jsp:doBody/>
</table>
<c:set var="nameValueContainter2" value="false" scope="request"/>