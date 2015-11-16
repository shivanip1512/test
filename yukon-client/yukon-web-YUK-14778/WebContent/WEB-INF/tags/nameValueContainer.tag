<%@ attribute name="altRowOn" required="false" %>
<%@ attribute name="tableClass" required="false" %>
<%@ attribute name="style" required="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="nameValueContainer" value="true" scope="request"/>
<table class="name-value-table <c:if test="${pageScope.altRowOn}">striped</c:if> ${pageScope.tableClass}" style="${pageScope.style}">
    <jsp:doBody/>
</table>
<c:set var="nameValueContainer" value="false" scope="request"/>