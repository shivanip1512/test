<%@ tag trimDirectiveWhitespaces="true" %>

<%@ attribute name="method" required="true" %>
<%@ attribute name="selected" required="false" %>
<%@ attribute name="title" required="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<c:choose>
    <c:when test="${pageScope.selected != null && pageScope.selected}"><jsp:doBody/></c:when>
    <c:otherwise>
        <a class="actionLinkAnchor" 
            title="${title}" 
            href="javascript:${widgetParameters.jsWidget}.doDirectActionRefresh('${method}')"><jsp:doBody/></a>
    </c:otherwise>
</c:choose>