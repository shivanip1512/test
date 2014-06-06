<%@ tag body-content="empty" %>

<%@ attribute name="classes" description="CSS class names applied to the container element." %>
<%@ attribute name="result" required="true" type="com.cannontech.common.search.result.SearchResults" %>
<%@ attribute name="baseUrl" required="true" description="URL without context path." %>
<%@ attribute name="adjustPageCount" description="When 'true', the items per page options appear. Default: false." %>
<%@ attribute name="overrideParams" type="java.lang.Boolean" description="Ignores params from the previous request. Set to true if they are specified in the baseUrl"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:default var="adjustPageCount" value="false"/>

<c:if test="${result.numberOfPages >= 1}">
<div class="compact-results-paging clearfix ${classes}">
    <div class="paging-area" data-current-page="${result.currentPage}">
        <c:if test="${adjustPageCount == 'true'}">
            <span class="fl perPageArea">
                <i:inline key="yukon.common.paging.itemsPerPage"/>&nbsp;
                <tags:itemsPerPage result="${result}" itemsPerPage="10" baseUrl="${baseUrl}"/>&nbsp;
                <tags:itemsPerPage result="${result}" itemsPerPage="25" baseUrl="${baseUrl}"/>&nbsp;
                <tags:itemsPerPage result="${result}" itemsPerPage="50" baseUrl="${baseUrl}"/>&nbsp;
            </span>
        </c:if>
        <span class="fl previous-link">
            <c:choose>
                <c:when test="${result.previousNeeded}">
                    <cti:url value="${baseUrl}" var="prevUrl">
                        <c:if test="${not pageScope.overrideParams}">
                            <c:forEach var="aParam" items="${paramValues}">
                                <c:if test="${(aParam.key != 'page') and (aParam.key != 'itemsPerPage')}">
                                    <c:forEach var="theValue" items="${aParam.value}">
                                        <cti:param name="${aParam.key}" value="${theValue}"/>
                                    </c:forEach>
                                </c:if>
                            </c:forEach>
                        </c:if>
                        <cti:param name="page" value="${result.currentPage - 1}"/>
                        <cti:param name="itemsPerPage" value="${result.count}"/>
                    </cti:url>
                    <button class="button naked" data-reload="${prevUrl}">
                        <i class="icon icon-resultset-previous disabled"></i>
                        <span class="b-label"><i:inline key="yukon.common.paging.previous"/></span>
                    </button>
                </c:when>
                <c:otherwise>
                    <button class="button naked fade-half" disabled="disabled">
                        <i class="icon icon-resultset-previous"></i>
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
        <span class="fl next-link">
            <c:choose>
                <c:when test="${result.nextNeeded}">
                    <cti:url value="${baseUrl}" var="nextUrl">
                        <c:if test="${not pageScope.overrideParams}">
                            <c:forEach var="aParam" items="${paramValues}">
                                <c:if test="${(aParam.key != 'page') and (aParam.key != 'itemsPerPage')}">
                                    <c:forEach var="theValue" items="${aParam.value}">
                                        <cti:param name="${aParam.key}" value="${theValue}"/>
                                    </c:forEach>
                                </c:if>
                            </c:forEach>
                        </c:if>
                        <cti:param name="page" value="${result.currentPage + 1}"/>
                        <cti:param name="itemsPerPage" value="${result.count}"/>
                    </cti:url>
                    <button class="button naked" data-reload="${nextUrl}">
                        <span class="b-label"><i:inline key="yukon.common.paging.next"/></span>
                        <i class="icon icon-resultset-next disabled"></i>
                    </button>
                </c:when>
                <c:otherwise>
                    <button class="button naked fade-half" disabled="disabled">
                        <span class="b-label"><i:inline key="yukon.common.paging.next"/></span>
                        <i class="icon icon-resultset-next"></i>
                    </button>
                </c:otherwise>
            </c:choose>
        </span>
    </div>
</div>
</c:if>