<%@ tag body-content="empty" %>

<%@ attribute name="result" required="true" type="com.cannontech.common.search.result.SearchResults" %>
<%@ attribute name="baseUrl" required="true" %>
<%@ attribute name="adjustPageCount" description="When 'true', the items per page options appear. Default: false." %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:default var="adjustPageCount" value="false"/>

<div class="compact-results-paging">
    <div class="paging-area">
        <c:if test="${adjustPageCount == 'true'}">
            <span class="fl perPageArea">
                <i:inline key="yukon.common.paging.itemsPerPage"/>&nbsp;
                <tags:ajaxItemsPerPageLink result="${result}" itemsPerPage="10" baseUrl="${baseUrl}"/>&nbsp;
                <tags:ajaxItemsPerPageLink result="${result}" itemsPerPage="25" baseUrl="${baseUrl}"/>&nbsp;
                <tags:ajaxItemsPerPageLink result="${result}" itemsPerPage="50" baseUrl="${baseUrl}"/>&nbsp;
            </span>
        </c:if>
        <span class="fl previous-link">
            <c:choose>
                <c:when test="${result.previousNeeded}">
                    <cti:url value="${baseUrl}" var="prevUrl">
                        <cti:param name="page" value="${result.currentPage - 1}"/>
                        <cti:param name="itemsPerPage" value="${result.count}"/>
                    </cti:url>
                    <button class="naked f-ajax-paging" data-url="${prevUrl}">
                        <i class="icon icon-resultset-previous"></i>
                        <span class="label"><i:inline key="yukon.common.paging.previous"/></span>
                    </button>
                </c:when>
                <c:otherwise>
                    <button class="naked" disabled="disabled">
                        <i class="icon icon-resultset-previous"></i>
                        <span class="label"><i:inline key="yukon.common.paging.previous"/></span>
                    </button>
                </c:otherwise>
            </c:choose>
        </span>
        <span class="fl page-num-text"><i:inline key="yukon.common.paging.viewing" arguments="${result.startIndex + 1},${result.endIndex},${result.hitCount}" argumentSeparator=","/></span>
        <span class="fl next-link">
            <c:choose>
                <c:when test="${result.nextNeeded}">
                    <cti:url value="${baseUrl}" var="nextUrl">
                        <cti:param name="page" value="${result.currentPage + 1}"/>
                        <cti:param name="itemsPerPage" value="${result.count}"/>
                    </cti:url>
                    <button class="naked f-ajax-paging" data-url="${nextUrl}">
                        <span class="label"><i:inline key="yukon.common.paging.next"/></span>
                        <i class="icon icon-resultset-next"></i>
                    </button>
                </c:when>
                <c:otherwise>
                    <button class="naked" disabled="disabled">
                        <span class="label"><i:inline key="yukon.common.paging.next"/></span>
                        <i class="icon icon-resultset-next"></i>
                    </button>
                </c:otherwise>
            </c:choose>
        </span>
    </div>
</div>
