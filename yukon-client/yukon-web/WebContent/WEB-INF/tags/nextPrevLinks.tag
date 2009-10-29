<%@ attribute name="baseUrl" required="true" %>
<%@ attribute name="searchResult" required="true" type="com.cannontech.common.search.SearchResult" %>
<%@ tag body-content="empty" %>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>

<c:set var="currentPage" value="${param.page}"/>
<c:if test="${empty currentPage}">
    <c:set var="currentPage" value="1"/>
</c:if>

<td class="pagingArea">
    <c:if test="${currentPage > 1}">
        <tags:pageNumberLink pageNumber="${currentPage - 1}" currentPage="${currentPage}"
            baseUrl="${baseUrl}"/>&nbsp;&nbsp;
    </c:if>
    <c:if test="${searchResult.hitCount > 0}">
        <cti:msg key="yukon.common.paging.viewing"
            arguments="${searchResult.startIndex + 1},${searchResult.endIndex},${searchResult.hitCount}"/>&nbsp;&nbsp;
    </c:if>
    <c:if test="${currentPage < searchResult.numberOfPages}">
        <tags:pageNumberLink pageNumber="${currentPage + 1}" currentPage="${currentPage}"
            baseUrl="${baseUrl}"/>
    </c:if>
</td>
