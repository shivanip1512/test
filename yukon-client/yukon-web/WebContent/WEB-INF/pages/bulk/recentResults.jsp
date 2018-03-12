<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<cti:standardPage page="bulk.recentResults" module="tools">

    <c:set var="urlPath" value="/bulk/filter"/>
    <cti:url var="detailUrl" value="${urlPath}"/>
    <form:form id="filter-form" action="${detailUrl}" method="GET" commandName="filter">
            <h3><i:inline key="yukon.common.filters"/></h3>
            <div class="column one filter-container">
                <tags:nameValueContainer2 naturalWidth="false">
                    <tags:nameValue2 nameKey=".action">    
                        <select path="action">
                            <option><i:inline key="modules.tools.selectAll"/></option>
                            <c:forEach items="${actions}" var="action">
                                <c:if test="${filter.action eq action.name()}">
                                    <option value="${action}" selected><i:inline key="${action.formatKey}"/></option>
                                </c:if>
                                <c:if test="${filter.action != action.name()}">
                                    <option value="${action}"><i:inline key="${action.formatKey}"/></option>
                                </c:if>
                            </c:forEach>
                        </select>            
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".dateRange">
                        <dt:dateRange startPath="startDate" endPath="endDate" startValue="${filter.startDate}" endValue="${filter.endDate}"/>
                    </tags:nameValue2>
                    
                    <tags:nameValue2 nameKey=".status">
                        <div class="button-group stacked">
                            <c:forEach var="status" items="${statuses}">
                                <c:set var="selected" value="${false}"/>
                                <c:if test="${fn:contains(filter.statuses, status)}">
                                    <c:set var="selected" value="${true}"/>
                                </c:if>
                                <cti:msg2 var="statusLabel" key="${status.formatKey}"/>
                                <tags:check path="statuses" classes="M0" value="${status}" label="${statusLabel}" checked="${selected}"></tags:check>
                            </c:forEach>
                        </div>
                    </tags:nameValue2>
                    
                    <tags:nameValue2 nameKey=".userName">
                        <tags:pickerDialog id="userPicker" type="userPicker" destinationFieldName="userName"
                            linkType="selection" selectionProperty="userName" initialId="${filter.userName}" 
                            icon="icon-user" includeRemoveButton="true"/>
                    </tags:nameValue2>
                </tags:nameValueContainer2>

                <div class="action-area">
                    <cti:button classes="primary action" nameKey="filter" type="submit" busy="true"/>
                </div>
                
            </div>
        </div>
    </form:form>
        
    <span class="fwn"><i:inline key=".filteredResults"/></span>
    <span class="badge">${recentActions.hitCount}</span>&nbsp;<i:inline key=".filteredResults"/>
    
    <cti:url var="pagingUrl" value="${urlPath}">
        <cti:param name="startDate" value="${filter.startDate}"/>
        <cti:param name="endDate" value="${filter.endDate}"/>
        <c:forEach var="status" items="${filter.statuses}">
            <cti:param name="statuses" value="${status}"/>
        </c:forEach>
        <cti:param name="userName" value="${filter.userName}"/>
        <cti:param name="action" value="${filter.action}"/>
   
    </cti:url>
    
    <div id="events-detail" data-url="${pagingUrl}" data-static>
        <table class="compact-results-table has-actions row-highlighting">
            <tr>
                <tags:sort column="${action}" />
                <tags:sort column="${startTime}" />
                <tags:sort column="${success}" />
                <tags:sort column="${failure}" />
                <tags:sort column="${notAttempted}" />
                <th>
                    <a class="wsnw sortable">
                    ${fn:escapeXml(detail)}<cti:icon icon="icon-blank"/>
                    </a>
                </th>
                <tags:sort column="${status}" />
                <tags:sort column="${userName}" />
            </tr>
            <tbody>
                <c:forEach var="action" items="${recentActions.resultList}">
                    <tr>
                        <td>
                            <i:inline key="${action.action.formatKey}"/>
                        </td>
                        <td>
                            <fmt:formatDate type = "both" dateStyle = "short" timeStyle = "short" value = "${action.formattedStartTime}" />
                                <c:if test="${not empty action.stopTime}">-
                                <fmt:formatDate type = "both" dateStyle = "short" timeStyle = "short" value = "${action.formattedStopTime}" />
                                </c:if>
                        </td>
                        <td>
                            ${action.successCount}
                        </td>
                        <td>
                            ${action.failureCount}
                        </td>
                        <td>
                            ${action.notAttemptedCount}
                        </td>
                        <td>
                            <a href="progressReport/detail?key=${action.cacheKey}">View</a>
                        </td>                        
                        <td>
                            <i:inline key="${action.status.formatKey}"/>
                        </td>
                        <td>
                            ${action.userName}
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        <tags:pagingResultsControls result="${recentActions}" adjustPageCount="true" thousands="true"/>
    </div>
</cti:standardPage>