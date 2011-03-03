<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ attribute name="tableClass" required="false" %>
<%@ attribute name="style" required="false" %>
<%@ attribute name="id" required="false" %>
<%@ attribute name="naturalWidth" required="false" type="java.lang.Boolean" description="Default is true. Causes table have a width:auto style (as opposed to 100%)."%>

<c:set var="nameValueContainter2" value="true" scope="request"/>
<table class="nameValueTable <c:if test="${empty pageScope.naturalWidth || pageScope.naturalWidth == true}">naturalWidth </c:if>${pageScope.tableClass}" 
       <c:if test="${!empty pageScope.style}">style="${pageScope.style}"</c:if>
       <c:if test="${!empty pageScope.id}">id="${pageScope.id}"</c:if>>
       
	<jsp:doBody/>
	
</table>
<c:set var="nameValueContainter2" value="false" scope="request"/>