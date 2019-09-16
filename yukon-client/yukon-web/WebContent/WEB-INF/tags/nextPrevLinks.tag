<%@ tag trimDirectiveWhitespaces="true" body-content="empty" description="Display 'next' and 'previous' links inside a picker." %>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<%@ attribute name="previousUrl" %>
<%@ attribute name="nextUrl" %>

<span class="fr detail PB10" style="padding-top:5px;" data-current-page="${currentPage}">
    <a href="${previousUrl}" class="previous-page" style="margin-right:5px;">
        <cti:icon icon="icon-resultset-previous-gray" classes="PR0 MR0"/>
        <span class="fl"><cti:msg2 key="yukon.common.paging.previous"/></span>
    </a>
    <span class="page-num-text"></span>
    <span class="fr" style="margin-left:5px;">
        <a href="${nextUrl}" class="next-page">
            <span class="fl"><cti:msg2 key="yukon.common.paging.next"/></span>
            <cti:icon icon="icon-resultset-next-gray" classes="PL0 ML0"/>
        </a>
    </span>
</span>