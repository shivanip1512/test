<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<cti:standardPage module="tools" page="collectionActions.recentResults">

    <c:set var="urlPath" value="/collectionActions/filter"/>
    <cti:url var="detailUrl" value="${urlPath}"/>
    <hr>
        <div>
            <cti:url var="detailUrl" value="${urlPath}"/>
            <form:form id="filter-form" action="${detailUrl}" method="GET" modelAttribute="filter" class="full-width push-down-4">
                <div class="dib vat">
                    <i:inline key=".filterBy"/>
                </div>
                <div class="dib ML10">
                    <cti:msg2 var="actionLabel" key=".action"/>
                    <tags:selectWithItems items="${actionsList}" path="actions" itemLabel="${formatKey}" dataPlaceholder="${actionLabel}" inputClass="js-chosen"/>
                </div>
                <div class="dib ML15 vat">
                    <dt:dateRange toText="to" toStyle="margin-right:4px;margin-top:2%;"  startPath="startDate" endPath="endDate" 
                                  startValue="${filter.startDate}" endValue="${filter.endDate}" displayValidationToRight="true"/>
                </div>
                <div>
                    <div class="button-group MT10" style="margin-left: 70px;">
                        <c:forEach var="status" items="${statuses}">
                            <c:set var="selected" value="${false}"/>
                            <c:if test="${fn:contains(filter.statuses, status)}">
                                <c:set var="selected" value="${true}"/>
                            </c:if>
                            <cti:msg2 var="statusLabel" key="${status.formatKey}"/>
                            <tags:check name="statuses" classes="M0" value="${status}" label="${statusLabel}" checked="${selected}"></tags:check>
                        </c:forEach>
                    </div>
                    <div class="dib ML15">
                        <tags:pickerDialog id="userPicker" type="deviceActionsRoleUserPicker" destinationFieldName="userIds"
                                           linkType="selection" selectionProperty="userName" initialId="${filter.userIds}" 
                                           icon="icon-user" allowEmptySelection="true" styleClass="fl MT15 vam" multiSelectMode="true"
                                           excludeIds="${excludedUserIds}" iconMagnifierCssClasses="MT5"/>
                    </div>
                    <div class="push-down-4 fr">
                        <cti:button classes="primary action" nameKey="filter" type="submit" busy="true" style="margin: 0px 0px 5px"/>
                    </div>
                </div>
            </form:form>
        </div>
    <hr>
    
    <c:choose>
        <c:when test="${empty recentActions || recentActions.hitCount == 0}">
            <span class="empty-list"><i:inline key="yukon.common.search.noResultsFound"/></span>
        </c:when>
        <c:otherwise>
            <span class="fwn"><i:inline key=".filteredResults"/>
            <span class="badge">${recentActions.hitCount}</span>&nbsp;<i:inline key=".actions"/>
            
            <cti:url var="pagingUrl" value="${urlPath}">
                <cti:formatDate type="DATE" value="${filter.startDate}" var="startMin"/>
                <cti:formatDate type="DATE" value="${filter.endDate}" var="startMax"/>
                <cti:param name="startDate" value="${startMin}"/>
                <cti:param name="endDate" value="${startMax}"/>
                <c:forEach var="status" items="${filter.statuses}">
                    <cti:param name="statuses" value="${status}"/>
                </c:forEach>
                <c:forEach var="userId" items="${filter.userIds}">
                    <cti:param name="userIds" value="${userId}"/>
                </c:forEach>   
                <c:forEach var="action" items="${filter.actions}">
                    <cti:param name="actions" value="${action}"/>
                </c:forEach>
            </cti:url>
            
            <div id="events-detail" data-url="${pagingUrl}" data-static>
                <table class="compact-results-table row-highlighting">
                    <tr>
                        <tags:sort column="${action}" />
                        <tags:sort column="${startTime}" />
                        <tags:sort column="${success}" />
                        <tags:sort column="${failure}" />
                        <tags:sort column="${notAttempted}" />
                        <th class="vab"><i:inline key=".detail"/></th>
                        <tags:sort column="${runStatus}" />
                        <tags:sort column="${userName}" />
                    </tr>
                    <tbody>
                        <c:forEach var="action" items="${recentActions.resultList}">
                            <tr>
                                <td>
                                    <i:inline key="${action.action.formatKey}"/>
                                </td>
                                <td>
                                    <fmt:formatDate type="both" dateStyle="short" timeStyle="short" value="${action.formattedStartTime}"/>
                                    <c:if test="${not empty action.stopTime}">
                                        - <fmt:formatDate type="both" dateStyle="short" timeStyle="short" value="${action.formattedStopTime}"/>
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
                                    <a href="progressReport/view?key=${action.cacheKey}">View</a>
                                </td>
                                <td>
                                    <i:inline key="${action.status.formatKey}"/>
                                </td>
                                <td>
                                    ${fn:escapeXml(action.userName)}
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                <tags:pagingResultsControls result="${recentActions}" adjustPageCount="true" thousands="true"/>
            </div>
        </c:otherwise>
    </c:choose>
    <script>
        $(".js-chosen").chosen({width: "330px"});
    </script>
</cti:standardPage>
