<%@ tag body-content="empty" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<c:if test="${!empty errors}">
    <ul class="errorMessage">
        <c:forEach var="error" items="${errors}">
            <li><cti:msg key="${error}"/></li>
        </c:forEach>
    </ul>
</c:if>
