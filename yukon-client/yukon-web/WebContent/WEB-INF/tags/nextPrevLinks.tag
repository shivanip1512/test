<%@ tag trimDirectiveWhitespaces="true" body-content="empty" description="Display 'next' and 'previous' links inside a picker." %>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<%@ attribute name="previousUrl" %>
<%@ attribute name="nextUrl" %>

<span class="paging-area clearfix" data-current-page="${currentPage}">
    <cti:url var="previousUrl" value="${previousUrl}"/>
    <a href="${previousUrl}" class="previous-page">
        <cti:icon icon="icon-resultset-previous-gray"/>
        <span class="fl"><cti:msg2 key="yukon.common.paging.previous"/></span>
    </a>
    <span class="page-num-text"></span>
    <cti:url var="nextUrl" value="${nextUrl}"/>
    <a href="${nextUrl}" class="next-page">
        <span class="fl"><cti:msg2 key="yukon.common.paging.next"/></span>
        <cti:icon icon="icon-resultset-next-gray"/>
    </a>
</span>