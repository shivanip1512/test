<%@ tag body-content="empty" trimDirectiveWhitespaces="true" description="Display a 'next' or 'previous' link on a page." %>

<%@ attribute name="enabled" required="true" type="java.lang.Boolean" %>
<%@ attribute name="direction" required="true" %>
<%@ attribute name="pageNumber" type="java.lang.Integer" %>
<%@ attribute name="baseUrl" %>
<%@ attribute name="url" %>
<%@ attribute name="classes" description="CSS classes for the anchor element."%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<%--
One can specify a base URL via baseUrl or complete URL via url.  If a complete
URL is specified via url, it will simply be used.  If a baseUrl is specified,
This page will link to that page with all parameters for this page added back
to the URL unmodified with the exception of the "page" parameter which will
be set to the value passed in via the pageNumber attribute.

If baseUrl is used, pageNumber is required.  If url is used no further
attributes are needed. 

Of course, if "enabled" is set to false, none of this matters because no link
will be used.
--%>

<c:if test="${pageScope.enabled && !empty pageScope.baseUrl}">
    <cti:url var="pageUrl" value="${pageScope.baseUrl}">
        <%-- keep all parameters except page number --%>
        <c:forEach var="aParam" items="${paramValues}">
            <c:if test="${aParam.key != 'page'}">
                <c:forEach var="theValue" items="${aParam.value}">
                    <cti:param name="${aParam.key}" value="${theValue}"/>
                </c:forEach>
            </c:if>
        </c:forEach>
        <cti:param name="page" value="${pageScope.pageNumber}"/>
    </cti:url>
</c:if>
<c:if test="${pageScope.enabled && !empty pageScope.url}">
    <c:set var="pageUrl" value="${pageScope.url}"/>
</c:if>

<c:if test="${pageScope.direction == 'next'}">
    <c:if test="${pageScope.enabled}">
        <a href="${pageUrl}" class="nextLink enabledAction">
            <span class="label"><cti:msg2 key="yukon.common.paging.next"/></span>
            <i class="icon icon-resultset-next"></i>
        </a>
    </c:if>
    <c:if test="${!pageScope.enabled}">
        <span class="nextLink disabledAction" disabled="disabled">
            <span class="label"><cti:msg2 key="yukon.common.paging.next"/></span>
            <i class="icon icon-resultset-next" disabled="disabled"></i>
        </span>
    </c:if>
</c:if>
<c:if test="${pageScope.direction == 'previous'}">
    <c:if test="${pageScope.enabled}">
        <a href="${pageUrl}" class="previousLink enabledAction">
            <i class="icon icon-resultset-previous"></i>
            <span class="label"><cti:msg2 key="yukon.common.paging.previous"/></span>
        </a>
    </c:if>
    <c:if test="${!pageScope.enabled}">
        <span class="previousLink disabledAction">
            <i class="icon icon-resultset-previous" disabled="disabled"></i>
            <span class="label"><cti:msg2 key="yukon.common.paging.previous"/></span>
        </span>
    </c:if>
</c:if>