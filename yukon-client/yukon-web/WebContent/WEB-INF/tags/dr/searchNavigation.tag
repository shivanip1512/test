<%@ attribute name="searchResult" required="true" type="com.cannontech.common.search.SearchResult" %>
<%@ attribute name="baseUrl" required="true" type="java.lang.String" %>
<%@ attribute name="pageLinksToShow" required="false" type="java.lang.Integer" %>
<%@ tag body-content="empty" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>

<%-- define the maximum number of page links to show on either side of the
     unlinked number representing this page --%>
<c:set var="linksOnEitherSideOfThis" value="2"/>
<c:if test="${!empty pageLinksToShow}">
    <%-- rounding down because this page is included in the links to show but
         not in "either side" --%>
    <c:set var="linksOnEitherSideOfThis" value="${pageLinksToShow / 2}"/>
</c:if>
<c:set var="currentPage" value="${param.page}"/>
<c:if test="${empty currentPage}">
    <c:set var="currentPage" value="1"/>
</c:if>

<table class="pagingArea" width="95%" style="font-size: .75em">
    <tr>
        <td align="left">Viewing ${searchResult.startIndex + 1}-${searchResult.endIndex} of ${searchResult.hitCount}</td>
        <td align="right">

            Results per page:
                <dr:itemsPerPageLink searchResult="${searchResult}" itemsPerPage="10"
                    baseUrl="${baseUrl}"/>&nbsp;
                <dr:itemsPerPageLink searchResult="${searchResult}" itemsPerPage="25"
                    baseUrl="${baseUrl}"/>&nbsp;
                <dr:itemsPerPageLink searchResult="${searchResult}" itemsPerPage="50"
                    baseUrl="${baseUrl}"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;

            <c:if test="${currentPage > 1}">
                <c:if test="${currentPage - 1 != 1}">
                    <dr:pageNumberLink pageNumber="1" currentPage="${currentPage}"
                        key="yukon.web.modules.dr.paging.first" baseUrl="${baseUrl}"/>&nbsp;
                </c:if>
                <dr:pageNumberLink pageNumber="${currentPage - 1}" currentPage="${currentPage}"
                    key="yukon.web.modules.dr.paging.previous" baseUrl="${baseUrl}"/>&nbsp;
            </c:if>
            <c:set var="beginIndex" value="1"/>
            <c:if test="${currentPage > linksOnEitherSideOfThis + 1}">
                <c:set var="beginIndex" value="${currentPage - linksOnEitherSideOfThis}"/>
                &hellip;&nbsp;
            </c:if>
            <c:set var="endIndex" value="${searchResult.numberOfPages}"/>
            <c:if test="${currentPage < searchResult.numberOfPages - linksOnEitherSideOfThis}">
                <c:set var="endIndex" value="${currentPage + linksOnEitherSideOfThis}"/>
            </c:if>
            <c:forEach var="pageNumber" begin="${beginIndex}" end="${endIndex}">
                <dr:pageNumberLink pageNumber="${pageNumber}" currentPage="${currentPage}"
                    baseUrl="${baseUrl}"/>&nbsp;
            </c:forEach>
            <c:if test="${currentPage < searchResult.numberOfPages - linksOnEitherSideOfThis}">
                &hellip;&nbsp;
            </c:if>
            <c:if test="${currentPage < searchResult.numberOfPages}">
                <dr:pageNumberLink pageNumber="${currentPage + 1}" currentPage="${currentPage}"
                    key="yukon.web.modules.dr.paging.next" baseUrl="${baseUrl}"/>&nbsp;
                <c:if test="${currentPage + 1 != searchResult.numberOfPages}">
                    <dr:pageNumberLink pageNumber="${searchResult.numberOfPages}" currentPage="${currentPage}"
                        key="yukon.web.modules.dr.paging.last" baseUrl="${baseUrl}"/>&nbsp;
                </c:if>
            </c:if>
        </td>
    </tr>
</table>
