<%@ attribute name="pageNumber" required="true" type="java.lang.Integer" %>
<%@ attribute name="currentPage" required="true" type="java.lang.Integer" %>
<%@ attribute name="key" required="false" type="java.lang.String" %>
<%@ attribute name="baseUrl" required="true" type="java.lang.String" %>
<%@ tag body-content="empty" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<cti:url var="pageUrl" value="${baseUrl}">
    <%-- keep all parameters except page number --%>
    <c:forEach var="aParam" items="${param}">
        <c:if test="${aParam.key != 'page'}">
            <cti:param name="${aParam.key}" value="${aParam.value}"/>
        </c:if>
    </c:forEach>
    <cti:param name="page" value="${pageNumber}"/>
</cti:url>

<c:set var="linkText" value="${pageNumber}"/>
<c:if test="${!empty key}">
    <c:set var="linkText"><cti:msg key="${key}"/></c:set>
</c:if>

<c:if test="${currentPage != pageNumber}">
    <a href="${pageUrl}">${linkText}</a>
</c:if>
<c:if test="${currentPage == pageNumber}">
    ${linkText}
</c:if>
