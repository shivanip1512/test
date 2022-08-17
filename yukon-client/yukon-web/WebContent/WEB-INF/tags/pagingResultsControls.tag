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
            <span class="fl page-size">
                <i:inline key="yukon.common.paging.itemsPerPage"/>&nbsp;
                <c:if test="${!thousands}">
                    <tags:itemsPerPage result="${result}" itemsPerPage="10"/>&nbsp;
                    <tags:itemsPerPage result="${result}" itemsPerPage="25"/>&nbsp;
                    <tags:itemsPerPage result="${result}" itemsPerPage="50"/>&nbsp;
                    <c:if test="${hundreds}">
                        <tags:itemsPerPage result="${result}" itemsPerPage="100"/>&nbsp;
                    </c:if>
                </c:if>
                <c:if test="${thousands}">
                    <tags:itemsPerPage result="${result}" itemsPerPage="250"/>&nbsp;
                    <tags:itemsPerPage result="${result}" itemsPerPage="500"/>&nbsp;
                    <tags:itemsPerPage result="${result}" itemsPerPage="1000"/>&nbsp;
                </c:if>
            </span>
        </c:if>
        <span class="fl previous-page">
            <c:choose>
                <c:when test="${result.previousNeeded}">
                    <button class="button naked">
                        <i class="icon icon-resultset-previous-gray"></i>
                        <span class="b-label"><i:inline key="yukon.common.paging.previous"/></span>
                    </button>
                </c:when>
                <c:otherwise>
                    <button class="button naked fade-half" disabled="disabled">
                        <i class="icon icon-resultset-previous-gray"></i>
                        <span class="b-label"><i:inline key="yukon.common.paging.previous"/></span>
                    </button>
                </c:otherwise>
            </c:choose>
        </span>
        <span class="fl page-num-text">
            <cti:list var="arguments">
                <cti:item value="${result.startIndex + 1}"/>
                <cti:item value="${result.endIndex}"/>
                <cti:item value="${result.hitCount}"/>
            </cti:list>
            <i:inline key="yukon.common.paging.viewing" arguments="${arguments}"/>
        </span>
        <span class="fl next-page">
            <c:choose>
                <c:when test="${result.nextNeeded}">
                    <button class="button naked">
                        <span class="b-label"><i:inline key="yukon.common.paging.next"/></span>
                        <i class="icon icon-resultset-next-gray"></i>
                    </button>
                </c:when>
                <c:otherwise>
                    <button class="button naked fade-half" disabled="disabled">
                        <span class="b-label"><i:inline key="yukon.common.paging.next"/></span>
                        <i class="icon icon-resultset-next-gray"></i>
                    </button>
                </c:otherwise>
            </c:choose>
        </span>
    </div>
</div>
</c:if>