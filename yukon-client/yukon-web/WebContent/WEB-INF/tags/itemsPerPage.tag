<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<%@ attribute name="result" required="true" type="com.cannontech.common.search.result.SearchResults" %>
<%@ attribute name="baseUrl" required="true" %>
<%@ attribute name="itemsPerPage" required="true" type="java.lang.Integer" %>

<c:if test="${itemsPerPage == result.count}">
    <span class="selectedItem">${itemsPerPage}</span>
</c:if>
<c:if test="${itemsPerPage != result.count}">
    <cti:url var="pageUrl" value="${baseUrl}">
        <%-- keep all parameters except page number and itemsPerPage --%>
        <c:forEach var="aParam" items="${paramValues}">
            <c:if test="${aParam.key != 'page' && aParam.key != 'itemsPerPage'}">
                <c:forEach var="theValue" items="${aParam.value}">
                    <cti:param name="${aParam.key}" value="${theValue}"/>
                </c:forEach>
            </c:if>
        </c:forEach>
        <cti:param name="itemsPerPage" value="${itemsPerPage}"/>
    </cti:url>
    <a href="javascript:void(0);" data-load="${pageUrl}">${itemsPerPage}</a>
</c:if>