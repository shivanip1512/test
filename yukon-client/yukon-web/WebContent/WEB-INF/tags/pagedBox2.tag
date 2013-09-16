<%@ tag trimDirectiveWhitespaces="true"%>

<%@ attribute name="arguments" type="java.lang.Object"%>
<%@ attribute name="argumentSeparator"%>
<%@ attribute name="baseUrl" required="true"%>
<%@ attribute name="defaultFilterInput"%>
<%@ attribute name="filterDialog"%>
<%@ attribute name="id"%>
<%@ attribute name="isFiltered" type="java.lang.Boolean"%>
<%@ attribute name="nameKey" required="true"%>
<%@ attribute name="pageByHundereds"%>
<%@ attribute name="searchResult" required="true" type="com.cannontech.common.search.result.SearchResults"%>
<%@ attribute name="showAllUrl"%>
<%@ attribute name="styleClass"%>
<%@ attribute name="titleLinkHtml"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths=".${nameKey},">
    <c:choose>
        <c:when test="${not empty argumentSeparator}">
            <cti:msg2 var="title" key=".title" arguments="${arguments}" argumentSeparator="${argumentSeparator}"/>
        </c:when>
        <c:otherwise>
            <cti:msg2 var="title" key=".title" arguments="${arguments}"/>
        </c:otherwise>
    </c:choose>
</cti:msgScope>

<tags:pagedBox title="${pageScope.title}"
        searchResult="${pageScope.searchResult}" 
        baseUrl="${pageScope.baseUrl}"
        id="${pageScope.id}"
        defaultFilterInput="${pageScope.defaultFilterInput}"
        filterDialog="${pageScope.filterDialog}"
        isFiltered="${pageScope.isFiltered}"
        pageByHundereds="${pageScope.pageByHundereds}"
        showAllUrl="${pageScope.showAllUrl}"
        titleLinkHtml="${pageScope.titleLinkHtml}"
        styleClass="${pageScope.styleClass}">
    <jsp:doBody/>
</tags:pagedBox>