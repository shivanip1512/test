<%@ attribute name="title" required="true" %>
<%@ attribute name="baseUrl" required="true" %>
<%@ attribute name="id" required="false" %>
<%@ attribute name="filterDialog" required="false" %>
<%@ attribute name="searchResult" required="true" type="com.cannontech.common.search.SearchResult" %>

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
                        <tags:filterLink popupId="${pageScope.filterDialog}"/>
                    </c:if>
                </td>

                <tags:nextPrevLinks searchResult="${searchResults}" baseUrl="${baseUrl}"/>
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
                    <tags:itemsPerPageLink searchResult="${searchResult}" itemsPerPage="10"
                        baseUrl="${baseUrl}"/>&nbsp;
                    <tags:itemsPerPageLink searchResult="${searchResult}" itemsPerPage="25"
                        baseUrl="${baseUrl}"/>&nbsp;
                    <tags:itemsPerPageLink searchResult="${searchResult}" itemsPerPage="50"
                        baseUrl="${baseUrl}"/>
                </td>
                <tags:nextPrevLinks searchResult="${searchResults}" baseUrl="${baseUrl}"/>
            </tr>
        </table>
    </div>

</div>
