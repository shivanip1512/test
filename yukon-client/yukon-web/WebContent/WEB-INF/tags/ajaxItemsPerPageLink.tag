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
        <cti:param name="itemsPerPage" value="${itemsPerPage}"/>
    </cti:url>
    <a href="javascript:void(0);" class="f-ajax-paging" data-url="${pageUrl}">${itemsPerPage}</a>
</c:if>