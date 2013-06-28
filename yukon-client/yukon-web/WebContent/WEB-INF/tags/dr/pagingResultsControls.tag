<%@ tag body-content="empty" %>
<%@ attribute name="result" required="true" type="com.cannontech.common.search.SearchResult"%>
<%@ attribute name="prevUrl" required="true" type="java.lang.String"%>
<%@ attribute name="nextUrl" required="true" type="java.lang.String"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
    <div class="compactResultsPaging">
        <div class="pagingArea">
            <span class="fl previousLink">
                <c:choose>
                    <c:when test="${result.previousNeeded}">
                        <button class="naked f-ajaxPaging" data-url="${prevUrl}">
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
            <span class="fl pageNumText"><i:inline key="yukon.common.paging.viewing" arguments="${result.startIndex + 1},${result.endIndex},${result.hitCount}" argumentSeparator=","/></span>
            <span class="fl nextLink">
                <c:choose>
                    <c:when test="${result.nextNeeded}">
                        <button class="naked f-ajaxPaging" data-url="${nextUrl}">
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
