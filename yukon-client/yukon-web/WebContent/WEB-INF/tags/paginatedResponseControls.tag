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

<fmt:parseNumber var="numberOfPages" integerOnly="true" value="${((response.totalItems - 1) / response.itemsPerPage) + 1}" />

<div class="compact-results-paging clearfix ${classes}">
    <div class="paging-area" data-current-page="${response.pageNumber}" data-page-size="${response.itemsPerPage}">
        <c:if test="${adjustPageCount == 'true'}">
            <span class="fl page-size">
                <i:inline key="yukon.common.paging.itemsPerPage"/>&nbsp;
                <c:if test="${!thousands}">
                    <tags:itemsPerPage count="${response.itemsPerPage}" itemsPerPage="10"/>&nbsp;
                    <tags:itemsPerPage count="${response.itemsPerPage}" itemsPerPage="25"/>&nbsp;
                    <tags:itemsPerPage count="${response.itemsPerPage}" itemsPerPage="50"/>&nbsp;
                    <c:if test="${hundreds}">
                        <tags:itemsPerPage count="${response.itemsPerPage}" itemsPerPage="100"/>&nbsp;
                    </c:if>
                </c:if>
                <c:if test="${thousands}">
                    <tags:itemsPerPage count="${response.itemsPerPage}" itemsPerPage="250"/>&nbsp;
                    <tags:itemsPerPage count="${response.itemsPerPage}" itemsPerPage="500"/>&nbsp;
                    <tags:itemsPerPage count="${response.itemsPerPage}" itemsPerPage="1000"/>&nbsp;
                </c:if>
            </span>
        </c:if>
        <span class="fl previous-page">
            <c:choose>
                <c:when test="${response.pageNumber > 1}">
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
            <c:set var="startIndex" value="${(response.pageNumber - 1) * response.itemsPerPage + 1}"/>
            <c:set var="endIndex" value="${startIndex + response.itemsPerPage - 1}"/>
            <c:if test="${endIndex > response.totalItems}">
                <c:set var="endIndex" value="${response.totalItems}"/>
            </c:if>
            <cti:list var="arguments">
                <cti:item value="${startIndex}"/>
                <cti:item value="${endIndex}"/>
                <cti:item value="${response.totalItems}"/>
            </cti:list>
            <i:inline key="yukon.common.paging.viewing" arguments="${arguments}"/>
        </span>
        <span class="fl next-page">
            <c:choose>
                <c:when test="${response.pageNumber < numberOfPages}">
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
