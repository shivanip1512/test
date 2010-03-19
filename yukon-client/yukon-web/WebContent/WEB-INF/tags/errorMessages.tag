<%@ tag body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<c:if test="${!empty errors}">
    <div class="userMessage ERROR">
        <c:if test="${fn:length(errors) == 1}">
            <cti:msg key="${errors[0]}"/>
        </c:if>
        <c:if test="${fn:length(errors) > 1}">
            <ul>
                <c:forEach var="error" items="${errors}">
                    <li><cti:msg key="${error}"/></li>
                </c:forEach>
            </ul>
        </c:if>
    </div>
</c:if>
