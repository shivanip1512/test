<%@ tag trimDirectiveWhitespaces="true"%>

<%@ attribute name="baseUrl" required="true"%>
<%@ attribute name="defaultFilterInput"%>
<%@ attribute name="filterDialog"%>
<%@ attribute name="id"%>
<%@ attribute name="isFiltered" type="java.lang.Boolean"%>
<%@ attribute name="pageByHundereds"%>
<%@ attribute name="searchResult" required="true" type="com.cannontech.common.search.result.SearchResults"%>
<%@ attribute name="showAllUrl"%>
<%@ attribute name="styleClass"%>
<%@ attribute name="title" required="true"%>
<%@ attribute name="titleLinkHtml" required="false" type="java.lang.String"%>
<%@ attribute name="overrideParams" type="java.lang.Boolean" description="Ignores params from the previous request. Set to true if they are specified in the baseUrl"%>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>

<cti:includeScript link="/JavaScript/simpleCookies.js" />

<div class="titled-container paged-container <c:if test="${!empty pageScope.styleClass}">${pageScope.styleClass}</c:if>" <c:if test="${!empty pageScope.id}">id="${pageScope.id}"</c:if>>
    <div class="title-bar clearfix">
        <h3 class="title">${pageScope.title}</h3>
        <tags:nextPrevLinks searchResult="${pageScope.searchResult}" baseUrl="${pageScope.baseUrl}" overrideParams="${pageScope.overrideParams}" mode="jsp"/>
        <span class="contextArea fr">
            <span class="filterArea">
                <c:if test="${!empty pageScope.filterDialog}">
                    <cti:button nameKey="filter" classes="navlink" icon="icon-filter"
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
    <div class="content"><jsp:doBody/></div>
    <div class="footer clearfix">
        <span class="paging-area fr">
            <tags:nextPrevLinks searchResult="${pageScope.searchResult}" baseUrl="${pageScope.baseUrl}" mode="jsp" overrideParams="${pageScope.overrideParams}"/>
        </span>
        <span class="perPageArea fr">
            <cti:msg key="yukon.common.paging.itemsPerPage"/>&nbsp;&nbsp;
            <tags:itemsPerPage result="${pageScope.searchResult}" itemsPerPage="10" baseUrl="${pageScope.baseUrl}" overrideParams="${pageScope.overrideParams}"/>&nbsp;
            <tags:itemsPerPage result="${pageScope.searchResult}" itemsPerPage="25" baseUrl="${pageScope.baseUrl}" overrideParams="${pageScope.overrideParams}"/>&nbsp;
            <tags:itemsPerPage result="${pageScope.searchResult}" itemsPerPage="50" baseUrl="${pageScope.baseUrl}" overrideParams="${pageScope.overrideParams}"/>&nbsp;
            <c:if test="${pageScope.pageByHundereds}">
                <tags:itemsPerPage result="${pageScope.searchResult}" itemsPerPage="100" baseUrl="${pageScope.baseUrl}" overrideParams="${pageScope.overrideParams}"/>
            </c:if>
        </span>
    </div>
</div>