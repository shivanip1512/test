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
    <div><i:inline key=".filterBy"/></div>
    <form:form id="filter-form" action="${detailUrl}" method="GET" commandName="filter" class="dib full-width push-down-4">
        <div style="display: flex !important; align-items: flex-end;">
        
            <div class="fl" style="margin-right: 15px;" >
                <cti:msg2 var="actionLabel" key=".action"/>    
                <tags:selectWithItems items="${actionsList}" path="actions" itemLabel="${formatKey}" dataPlaceholder="${actionLabel}" inputClass="js-chosen"/>
            </div>
            <div class="fl" style="margin-right: 15px;" >
                <dt:dateRange toText="to" toStyle="margin-right:4px;margin-top:1%;" wrapperClasses="fl" startPath="startDate" endPath="endDate" startValue="${filter.startDate}" endValue="${filter.endDate}"/>
            </div>
            <div class="button-group fl" style="margin-right: 15px;" >
                <c:forEach var="status" items="${statuses}">
                    <c:set var="selected" value="${false}"/>
                    <c:if test="${fn:contains(filter.statuses, status)}">
                        <c:set var="selected" value="${true}"/>
                    </c:if>
                    <cti:msg2 var="statusLabel" key="${status.formatKey}"/>
                    <tags:check name="statuses" classes="M0" value="${status}" label="${statusLabel}" checked="${selected}"></tags:check>
                </c:forEach>
            </div>
            <tags:pickerDialog id="userPicker" type="deviceActionsRoleUserPicker" destinationFieldName="userIds"
                linkType="selection" selectionProperty="userName" initialId="${filter.userIds}" 
                icon="icon-user" allowEmptySelection="true" styleClass="fl" multiSelectMode="true"
                excludeIds="${excludedUserIds}"/>
            </div>

            <div class="push-down-4 fr">
                <cti:button classes="primary action" nameKey="filter" type="submit" busy="true" style="margin: 0px 0px 5px"/>
            </div>
    </form:form>
    <hr>    
    <span class="fwn"><i:inline key=".filteredResults"/>
    <span class="badge">${recentActions.hitCount}</span>&nbsp;<i:inline key=".actions"/>
    
    <cti:url var="pagingUrl" value="${urlPath}">
        <cti:param name="startDate" value="${filter.startDate}"/>
        <cti:param name="endDate" value="${filter.endDate}"/>
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
                            ${action.userName}
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        <tags:pagingResultsControls result="${recentActions}" adjustPageCount="true" thousands="true"/>
    </div>
    <script>
        $(".js-chosen").chosen({width: "150px"});
    </script>
</cti:standardPage>
