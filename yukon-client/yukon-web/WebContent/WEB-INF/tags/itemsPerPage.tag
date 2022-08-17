<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<%@ attribute name="result" required="true" type="com.cannontech.common.search.result.SearchResults" %>
<%@ attribute name="itemsPerPage" required="true" type="java.lang.Integer" %>

<c:if test="${itemsPerPage == result.count}">
    <span data-page-size="${itemsPerPage}">${itemsPerPage}</span>
</c:if>
<c:if test="${itemsPerPage != result.count}">
    <a data-page-size="${itemsPerPage}" href="javascript:void(0);">${itemsPerPage}</a>
</c:if>