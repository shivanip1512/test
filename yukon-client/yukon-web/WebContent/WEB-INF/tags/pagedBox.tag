<%@ attribute name="title" required="true" %>
<%@ attribute name="baseUrl" required="true" %>
<%@ attribute name="id" %>
<%@ attribute name="filterDialog" %>
<%@ attribute name="defaultFilterInput" %>
<%@ attribute name="searchResult" required="true" type="com.cannontech.common.search.SearchResult" %>
<%@ attribute name="isFiltered" type="java.lang.Boolean" %>
<%@ attribute name="showAllUrl" %>
<%@ attribute name="pageByHundereds" %>
<%@ attribute name="styleClass" %>
<%@ attribute name="titleLinkHtml" required="false" type="java.lang.String" %>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>

<cti:includeScript link="/JavaScript/simpleCookies.js" />

<div class="pagedBox<c:if test="${!empty pageScope.styleClass}"> ${pageScope.styleClass}</c:if>"<c:if test="${!empty pageScope.id}"> id="${pageScope.id}"</c:if>>
    <div class="title">
        <span class="titleArea">${pageScope.title}</span>
        <span class="pagingArea fr">
            <tags:nextPrevLinks searchResult="${pageScope.searchResult}" baseUrl="${pageScope.baseUrl}" mode="jsp"/>
        </span>
        <span class="contextArea">
	        <span class="filterArea">
	            <c:if test="${!empty pageScope.filterDialog}">
                    <cti:button nameKey="filter" styleClass="navlink"
                        onclick="showSimplePopup('${pageScope.filterDialog}', '${pageScope.defaultFilterInput}');"
                        renderMode="labeledImage"/>
	                <c:if test="${pageScope.isFiltered}">
	                    &nbsp;&nbsp;<a href="${pageScope.showAllUrl}">
	                        <cti:msg key="yukon.common.paging.showAll"/>
	                    </a>
	                </c:if>
	            </c:if>
	        </span>
			<c:if test="${!empty pageScope.titleLinkHtml}">
                ${pageScope.titleLinkHtml}
			</c:if>
        </span>
    </div>
    <div class="content">
        <jsp:doBody/>
    </div>

    <div class="footer">
	    <span class="pagingArea fr">
            <tags:nextPrevLinks searchResult="${pageScope.searchResult}" baseUrl="${pageScope.baseUrl}" mode="jsp"/>
	    </span>
	    <span class="perPageArea fr">
	        <cti:msg key="yukon.common.paging.itemsPerPage"/>&nbsp;&nbsp;
	        <tags:itemsPerPageLink searchResult="${pageScope.searchResult}" itemsPerPage="10" baseUrl="${pageScope.baseUrl}"/>&nbsp;
            <tags:itemsPerPageLink searchResult="${pageScope.searchResult}" itemsPerPage="25" baseUrl="${pageScope.baseUrl}"/>&nbsp;
            <tags:itemsPerPageLink searchResult="${pageScope.searchResult}" itemsPerPage="50" baseUrl="${pageScope.baseUrl}"/>&nbsp;
            <c:if test="${pageScope.pageByHundereds}">
                <tags:itemsPerPageLink searchResult="${pageScope.searchResult}" itemsPerPage="100" baseUrl="${pageScope.baseUrl}"/>
            </c:if>
	    </span>
    </div>

</div>
