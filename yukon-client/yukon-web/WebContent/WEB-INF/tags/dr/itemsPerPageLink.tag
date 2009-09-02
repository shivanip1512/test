<%@ attribute name="searchResult" required="true" type="com.cannontech.common.search.SearchResult" %>
<%@ attribute name="baseUrl" required="true" type="java.lang.String" %>
<%@ attribute name="itemsPerPage" required="true" type="java.lang.Integer" %>
<%@ tag body-content="empty" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<c:if test="${itemsPerPage == searchResult.count}">
    ${itemsPerPage}
</c:if>

<c:if test="${itemsPerPage != searchResult.count}">
    <cti:url var="pageUrl" value="${baseUrl}">
        <%-- keep all parameters except page number and itemsPerPage --%>
        <c:forEach var="aParam" items="${param}">
            <c:if test="${aParam.key != 'page' && aParam.key != 'itemsPerPage'}">
                <cti:param name="${aParam.key}" value="${aParam.value}"/>
            </c:if>
        </c:forEach>
        <cti:param name="itemsPerPage" value="${itemsPerPage}"/>
    </cti:url>

    <a href="${pageUrl}">${itemsPerPage}</a>
</c:if>
