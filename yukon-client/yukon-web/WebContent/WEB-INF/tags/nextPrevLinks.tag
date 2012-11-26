<%@ attribute name="mode" required="true" %>
<%@ attribute name="baseUrl" %>
<%@ attribute name="previousUrl" %>
<%@ attribute name="nextUrl" %>
<%@ attribute name="searchResult" type="com.cannontech.common.search.SearchResult" %>
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

<c:if test="${pageScope.mode == 'javascript'}">
<c:set var="disabledNextStyle" value=" style=\"display: none;\""/>
<c:set var="disabledPreviousStyle" value=" style=\"display: none;\""/>
</c:if>

<div class="fr">
    <table>
        <tr>
            <td class="previousLink">
                <c:if test="${pageScope.mode == 'javascript' || !previousEnabled}">
	                <div class="disabledAction"${disabledPreviousStyle}>
	                    <tags:pageNumberLink direction="previous" enabled="false"/>
	                </div>
	            </c:if>
                <c:if test="${previousEnabled}">
                    <div class="enabledAction">
	                    <c:if test="${pageScope.mode == 'jsp'}">
                            <tags:pageNumberLink pageNumber="${currentPage - 1}" 
                                direction="previous"
                                baseUrl="${pageScope.baseUrl}" enabled="true"/>
	                    </c:if>
	                    <c:if test="${pageScope.mode == 'javascript'}">
	                        <tags:pageNumberLink direction="previous"
	                            url="${pageScope.previousUrl}" enabled="true"/>
	                    </c:if>
	                </div>
                </c:if>
            </td>

            <td class="pageNumText no_padding" >
			    <c:if test="${!empty pageScope.searchResult && pageScope.searchResult.hitCount > 0}">
			        <cti:msg key="yukon.common.paging.viewing"
			            arguments="${pageScope.searchResult.startIndex + 1},${pageScope.searchResult.endIndex},${pageScope.searchResult.hitCount}"/>
			    </c:if>
            </td>

            <td class="nextLink">
                <c:if test="${pageScope.mode == 'javascript' || !nextEnabled}">
                    <div class="disabledAction"${disabledNextStyle}>
                        <tags:pageNumberLink direction="next" enabled="false"/>
                    </div>
                </c:if>
                <c:if test="${nextEnabled}">
                    <div class="enabledAction">
                        <c:if test="${pageScope.mode == 'jsp'}">
                            <tags:pageNumberLink pageNumber="${currentPage + 1}" 
                                direction="next"
                                baseUrl="${pageScope.baseUrl}" enabled="true"/>
                        </c:if>
                        <c:if test="${pageScope.mode == 'javascript'}">
                            <tags:pageNumberLink direction="next"
                                url="${pageScope.nextUrl}" enabled="true"/>
                        </c:if>
                    </div>
                </c:if>
            </td>
        </tr>
    </table>
</div>
