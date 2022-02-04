<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<%@ attribute name="previousNeeded" type="java.lang.Boolean" description="When 'true', the Previous Page icon will display. Default: 'false'." %>
<%@ attribute name="nextNeeded" type="java.lang.Boolean" description="When 'true', the Next Page icon will display. Default: 'false'." %>

<span class="fl previous-page">
    <c:choose>
        <c:when test="${previousNeeded}">
            <cti:msg2 var="prevTitle" key="yukon.common.paging.previousIcon.alt"/>
            <button class="button naked" title="${prevTitle}">
                <cti:icon icon="icon-resultset-previous-gray"/>
            </button>
        </c:when>
        <c:otherwise>
            <button class="button naked fade-half cd" disabled="disabled">
                <cti:icon icon="icon-resultset-previous-gray"/>
            </button>
        </c:otherwise>
    </c:choose>
</span>
<span class="fl next-page">
    <c:choose>
        <c:when test="${nextNeeded}">
            <cti:msg2 var="nextTitle" key="yukon.common.paging.nextIcon.alt"/>
            <button class="button naked" title="${nextTitle}">
                <cti:icon icon="icon-resultset-next-gray"/>
            </button>
        </c:when>
        <c:otherwise>
            <button class="button naked fade-half cd" disabled="disabled">
                <cti:icon icon="icon-resultset-next-gray"/>
            </button>
        </c:otherwise>
    </c:choose>
</span>
