<%@ attribute name="enabled" required="true" type="java.lang.Boolean" %>
<%@ attribute name="direction" required="true" %>
<%@ attribute name="pageNumber" type="java.lang.Integer" %>
<%@ attribute name="baseUrl" %>
<%@ attribute name="url" %>
<%@ tag body-content="empty" description="Display a 'next' or 'previous' link on a page." %>

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

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

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
        <a href="${pageUrl}">
            <span style="white-space: nowrap;">
                <span style="vertical-align: middle;">
                    <cti:msg key="yukon.common.paging.next"/>
                </span>
                <cti:logo key="yukon.common.paging.nextIcon"/>
            </span>
        </a>
    </c:if>
    <c:if test="${!pageScope.enabled}">
        <span style="white-space: nowrap;">
            <span style="vertical-align: middle;">
                <cti:msg key="yukon.common.paging.next"/>
            </span>
            <cti:logo key="yukon.common.paging.nextIcon.disabled"/></a>
        </span>
    </c:if>
</c:if>
<c:if test="${pageScope.direction == 'previous'}">
    <c:if test="${pageScope.enabled}">
        <a href="${pageUrl}">
            <span style="white-space: nowrap;">
                <cti:logo key="yukon.common.paging.previousIcon"/>
                <span style="vertical-align: middle;"><cti:msg key="yukon.common.paging.previous"/></span>
            </span>
        </a>
    </c:if>
    <c:if test="${!pageScope.enabled}">
        <span style="white-space: nowrap;">
            <cti:logo key="yukon.common.paging.previousIcon.disabled"/>
            <span style="vertical-align: middle;"><cti:msg key="yukon.common.paging.previous"/></span>
        </span>
    </c:if>
</c:if>
