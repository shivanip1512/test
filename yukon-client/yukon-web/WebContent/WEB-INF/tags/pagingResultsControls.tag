<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ attribute name="classes" description="CSS class names applied to the container element." %>
<%@ attribute name="result" required="true" type="com.cannontech.common.search.result.SearchResults" %>
<%@ attribute name="adjustPageCount" type="java.lang.Boolean" description="When 'true', the items per page options appear. Default: 'false'." %>
<%@ attribute name="hundreds" type="java.lang.Boolean" description="When 'true', the '100' items per page options will appear. Default: 'false'." %>
<%@ attribute name="thousands" type="java.lang.Boolean" description="When 'true', the '1000' items per page options will appear and '25' and '50' will no longer appear. Default: 'false'." %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:default var="adjustPageCount" value="false"/>
<cti:default var="hundreds" value="false"/>
<cti:default var="thousands" value="false"/>

<c:if test="${result.numberOfPages >= 1}">
<div class="compact-results-paging clearfix ${classes}">
    <div class="paging-area" data-current-page="${result.currentPage}" data-page-size="${result.count}">
        <c:if test="${adjustPageCount == 'true'}">
            <tags:pageSizeControl count="${result.count}" hundreds="${hundreds}" thousands="${thousands}"/>
        </c:if>
        <span class="fl page-num-text">
            <cti:list var="arguments">
                <cti:item value="${result.hitCount > 0 ? result.startIndex + 1 : 0}"/>
                <cti:item value="${result.endIndex}"/>
                <cti:item value="${result.hitCount}"/>
            </cti:list>
            <i:inline key="yukon.common.paging.viewing" arguments="${arguments}"/>
        </span>
        <tags:pageNextPrevControls previousNeeded="${result.previousNeeded}" nextNeeded="${result.nextNeeded}"/>
    </div>
</div>
</c:if>