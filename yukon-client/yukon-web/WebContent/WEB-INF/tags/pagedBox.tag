<%@ attribute name="title" required="true" %>
<%@ attribute name="baseUrl" required="true" %>
<%@ attribute name="id" %>
<%@ attribute name="filterDialog" %>
<%@ attribute name="defaultFilterInput" %>
<%@ attribute name="searchResult" required="true" type="com.cannontech.common.search.SearchResult" %>
<%@ attribute name="isFiltered" type="java.lang.Boolean" %>
<%@ attribute name="showAllUrl" %>
<%@ attribute name="pageByHundereds" %>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>

<cti:includeScript link="/JavaScript/simpleCookies.js"/>

<div class="pagedBox" <c:if test="${!empty pageScope.id}" >id="${pageScope.id}"</c:if>>

    <div class="title">
        <table class="boxTable">
            <tr style="vertical-align: bottom;">
                <td class="titleArea">${pageScope.title}</td>
                <td class="filterArea">
                    <c:if test="${!empty pageScope.filterDialog}">
                        <tags:filterLink popupId="${pageScope.filterDialog}" defaultFilterInput="${pageScope.defaultFilterInput}"/>
                        <c:if test="${pageScope.isFiltered}">
                            &nbsp;&nbsp;<a href="${pageScope.showAllUrl}">
                            <cti:msg key="yukon.common.paging.showAll"/>
                        </a>
                        </c:if>
                    </c:if>
                </td>

                <tags:nextPrevLinks searchResult="${pageScope.searchResult}"
                    baseUrl="${pageScope.baseUrl}" mode="jsp"/>
            </tr>
        </table>
    </div>

    <div class="content">
        <jsp:doBody/>
    </div>

    <div class="footer">
        <table class="boxTable">
            <tr>
                <td class="perPageArea">
                    <cti:msg key="yukon.common.paging.itemsPerPage"/>&nbsp;&nbsp;
                    <tags:itemsPerPageLink searchResult="${pageScope.searchResult}" itemsPerPage="10"
                        baseUrl="${pageScope.baseUrl}"/>&nbsp;
                    <tags:itemsPerPageLink searchResult="${pageScope.searchResult}" itemsPerPage="25"
                        baseUrl="${pageScope.baseUrl}"/>&nbsp;
                    <tags:itemsPerPageLink searchResult="${pageScope.searchResult}" itemsPerPage="50"
                        baseUrl="${pageScope.baseUrl}"/>&nbsp;
                    <c:if test="${pageScope.pageByHundereds}">
                        <tags:itemsPerPageLink searchResult="${pageScope.searchResult}" itemsPerPage="100"
                            baseUrl="${pageScope.baseUrl}"/>
                    </c:if>
                </td>
                <tags:nextPrevLinks searchResult="${pageScope.searchResult}"
                    baseUrl="${pageScope.baseUrl}" mode="jsp"/>
            </tr>
        </table>
    </div>

</div>
