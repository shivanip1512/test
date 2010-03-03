<%@ attribute name="value" required="true" %>
<%@ attribute name="var" required="true" %>
<%@ tag description="use to insert cti:param tags to preserve current sort field" %>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<cti:url var="${pageScope.var}" value="${pageScope.value}" scope="request">
    <c:if test="${!empty param.itemsPerPage}">
        <cti:param name="itemsPerPage" value="${param.itemsPerPage}"/>
    </c:if>
    <c:if test="${!empty param.sort}">
        <cti:param name="sort" value="${param.sort}"/>
    </c:if>
    <c:if test="${!empty param.descending}">
        <cti:param name="descending" value="${param.descending}"/>
    </c:if>
    <jsp:doBody/>
</cti:url>
