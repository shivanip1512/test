<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>

<%@ attribute name="nameKey" required="true" %>
<%@ attribute name="arguments" required="false" type="java.lang.Object"%>
<%@ attribute name="argumentSeparator" required="false" type="java.lang.String"%>

<%@ attribute name="baseUrl" required="true" %>
<%@ attribute name="id" %>
<%@ attribute name="filterDialog" %>
<%@ attribute name="defaultFilterInput" %>
<%@ attribute name="searchResult" required="true" type="com.cannontech.common.search.SearchResult" %>
<%@ attribute name="isFiltered" type="java.lang.Boolean" %>
<%@ attribute name="showAllUrl" %>
<%@ attribute name="pageByHundereds" %>
<%@ attribute name="styleClass" %>

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
        styleClass="${pageScope.styleClass}">
        
    <jsp:doBody/>
    
</tags:pagedBox>