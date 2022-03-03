<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="classes" description="CSS class names applied to the container element." %>
<%@ attribute name="response" required="true" type="com.cannontech.common.model.PaginatedResponse" %>
<%@ attribute name="adjustPageCount" type="java.lang.Boolean" description="When 'true', the items per page options appear. Default: 'false'." %>
<%@ attribute name="hundreds" type="java.lang.Boolean" description="When 'true', the '100' items per page options will appear. Default: 'false'." %>
<%@ attribute name="thousands" type="java.lang.Boolean" description="When 'true', the '1000' items per page options will appear and '25' and '50' will no longer appear. Default: 'false'." %>

<cti:default var="adjustPageCount" value="false"/>
<cti:default var="hundreds" value="false"/>
<cti:default var="thousands" value="false"/>

<c:set var="numPages" value="0"/>
<c:if test="${response.totalItems > 1}">
    <c:set var="numPages" value="${((response.totalItems - 1) / response.itemsPerPage) + 1}"/>
</c:if>

<fmt:parseNumber var="numberOfPages" integerOnly="true" value="${numPages}" />

<div class="compact-results-paging clearfix ${classes}">
    <div class="paging-area" data-current-page="${response.pageNumber}" data-page-size="${response.itemsPerPage}">
        <c:if test="${adjustPageCount == 'true'}">
            <tags:pageSizeControl count="${response.itemsPerPage}" hundreds="${hundreds}" thousands="${thousands}"/>
        </c:if>
        <span class="fl page-num-text">
            <c:set var="startIndex" value="${(response.pageNumber - 1) * response.itemsPerPage + 1}"/>
            <c:set var="endIndex" value="${startIndex + response.itemsPerPage - 1}"/>
            <c:if test="${endIndex > response.totalItems}">
                <c:set var="endIndex" value="${response.totalItems}"/>
            </c:if>
            <cti:list var="arguments">
                <cti:item value="${response.totalItems > 0 ? startIndex : 0}"/>
                <cti:item value="${endIndex}"/>
                <cti:item value="${response.totalItems > 0 ? response.totalItems : 0}"/>
            </cti:list>
            <i:inline key="yukon.common.paging.viewing" arguments="${arguments}"/>
        </span>
        <tags:pageNextPrevControls previousNeeded="${response.pageNumber > 1}" nextNeeded="${response.pageNumber < numberOfPages}"/>
    </div>
</div>
