<%@ tag body-content="empty" %>

<%@ attribute name="name" required="true" rtexprvalue="true"%>
<%@ attribute name="items" type="java.lang.Object[]" rtexprvalue="true"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:forEach var="arrayItem" items="${pageScope.items}">
    <input type="hidden" name="${pageScope.name}" value="${arrayItem}"/>
</c:forEach>
