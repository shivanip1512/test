<%@ tag trimDirectiveWhitespaces="true" body-content="empty"%>

<%@ attribute name="mode" required="true" %>
<%@ attribute name="baseUrl" %>
<%@ attribute name="previousUrl" %>
<%@ attribute name="nextUrl" %>
<%@ attribute name="searchResult" type="com.cannontech.common.search.result.SearchResults" %>
<%@ attribute name="overrideParams" type="java.lang.Boolean" description="Ignores params from the previous request. Set to true if they are specified in the baseUrl"%>
<%@ tag body-content="empty" %>

<%--
The mode attribute can be "jsp" or "javascript".
If it's "jsp", baseUrl and searchResult are required.
If it's "javascript", nextUrl and previousUrl are required. 
--%>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>

<c:set var="previousEnabled" value="true"/>
<c:set var="nextEnabled" value="true"/>

<c:if test="${pageScope.mode == 'jsp'}">
    <c:set var="currentPage" value="${param.page}"/>
    <c:if test="${empty currentPage}">
        <c:set var="currentPage" value="1"/>
    </c:if>

    <c:if test="${currentPage <= 1}">
        <c:set var="previousEnabled" value="false"/>
    </c:if>
    <c:if test="${currentPage >= pageScope.searchResult.numberOfPages ||
                   pageScope.searchResult.count >= pageScope.searchResult.hitCount}">
        <c:set var="nextEnabled" value="false"/>
    </c:if>
</c:if>
<input type="hidden" class="f-current_page_index_from_1" value="${currentPage}" />
<span class="paging-area clearfix">
    <c:if test="${pageScope.mode == 'javascript' || !previousEnabled}">
        <tags:pageNumberLink direction="previous" enabled="false" classes="dn"/>
    </c:if>
    <c:if test="${previousEnabled}">
        <c:if test="${pageScope.mode == 'jsp'}">
            <tags:pageNumberLink pageNumber="${currentPage - 1}" direction="previous" baseUrl="${pageScope.baseUrl}" enabled="true" overrideParams="${pageScope.overrideParams}"/>
        </c:if>
        <c:if test="${pageScope.mode == 'javascript'}">
            <tags:pageNumberLink direction="previous" url="${pageScope.previousUrl}" enabled="true"/>
        </c:if>
    </c:if>
    <span class="page-num-text">
        <c:if test="${!empty pageScope.searchResult && pageScope.searchResult.hitCount > 0}">
            <cti:msg key="yukon.common.paging.viewing" arguments="${pageScope.searchResult.startIndex + 1},${pageScope.searchResult.endIndex},${pageScope.searchResult.hitCount}"/>
        </c:if>
    </span>
    <c:if test="${pageScope.mode == 'javascript' || !nextEnabled}">
        <tags:pageNumberLink direction="next" enabled="false" classes="dn"/>
    </c:if>
    <c:if test="${nextEnabled}">
        <c:if test="${pageScope.mode == 'jsp'}">
            <tags:pageNumberLink pageNumber="${currentPage + 1}" direction="next" baseUrl="${pageScope.baseUrl}" enabled="true" overrideParams="${pageScope.overrideParams}"/>
        </c:if>
        <c:if test="${pageScope.mode == 'javascript'}">
            <tags:pageNumberLink direction="next" url="${pageScope.nextUrl}" enabled="true"/>
        </c:if>
    </c:if>
</span>