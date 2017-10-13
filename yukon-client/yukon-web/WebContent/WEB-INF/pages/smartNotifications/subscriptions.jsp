<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>

<cti:msgScope paths="modules.smartNotifications">

<div id="notifications-section" style="margin-top:50px;">
    <cti:button icon="icon-email-add" nameKey="subscribe" data-popup="#create-popup" classes="fr"/>
    <tags:sectionContainer2 nameKey="notifications">
    <cti:url var="singleNotificationUrl" value="/notifications/singleNotification"/>
    <form action="${singleNotificationUrl}" method="POST">
    <cti:csrfToken/>
    <span><i:inline key=".singleNotification"/>:
        <div class="button-group">
            <tags:radio id="singleNotificationValue" key=".yes" classes="yes js-single-notification" value="true" name="singleNotification" checked="${!empty sendTime}"></tags:radio>
            <tags:radio key=".no" classes="no js-single-notification" value="false" name="singleNotification" checked="${empty sendTime}"></tags:radio>
        </div>
        <c:set var="sendTimeClass" value="${empty sendTime ? 'dn' : ''}"/>
        <div id="send-time" class="dib column-6-18 clearfix ${sendTimeClass}">
            <div class="column one">
                <div class="js-time-label fwb" style="width:70px;"></div>
                <input type="hidden" id="notifications-send-time" name="sendTime" value="${sendTime}"/>
                <input type="hidden" id="userPreferenceSendTime" value="${sendTime}"/>
            </div>
            <div class="column two nogutter">
                <div class="js-time-slider" style="margin-top: 5px;width:250px;"></div>
            </div>
        </div>
        <cti:button nameKey="save" type="submit" classes="fn" style="margin-left:20px;"/>
    </span>
    </form>
    <span class="fr">
        <a href="javascript:void(0);" data-popup="#filter-popup">
            <cti:icon icon="icon-filter"/>&nbsp;
            <i:inline key="yukon.common.filter"/>
        </a>
        </span>
        <hr/>
            <table class="compact-results-table has-actions row-highlighting">
                <tr>
                    <tags:sort column="${type}" />
                    <tags:sort column="${frequency}" />
                    <tags:sort column="${media}" />
                    <tags:sort column="${recipient}" />
                    <tags:sort column="${detail}" />
                    <th class="action-column"><cti:icon icon="icon-cog" classes="M0" /></th>
                </tr>
                <tbody>
                    <c:choose>
                        <c:when test="${subscriptions.hitCount > 0}">
                            <c:forEach var="subscription" items="${subscriptions.resultList}">
                                <c:set var="subId" value="${subscription.id}"/>
                                <cti:msg2 var="subType" key="${subscription.type.formatKey}"/>
                                <tr class="js-${subscription.frequency}">
                                    <td>${subType}
                                        <c:if test="${subscription.type == 'DEVICE_DATA_MONITOR'}">
                                            - <c:out value="${deviceDataMonitors.get(subscription.parameters['monitorId'])}"/>
                                        </c:if>
                                    </td>
                                    <td>
                                        <i:inline key="${subscription.frequency.formatKey}"/>
                                        <c:if test="${subscription.frequency == 'DAILY_DIGEST'}">
                                            <input type="hidden" class="js-daily-row-time" value="${subscription.parameters['sendTime']}"/>
                                            <c:set var="dailyTime" value="${!empty sendTime ? sendTime : subscription.parameters['sendTime']}"/>
                                            <fmt:parseDate value="${dailyTime}" type="TIME" pattern="HH:mm" var="parsedTime"/>
                                            @ <cti:formatDate value="${parsedTime}" type="TIME"/>
                                        </c:if>
                                    </td>
                                    <td><i:inline key="${subscription.media.formatKey}"/></td>
                                    <td>${subscription.recipient}</td>
                                    <td><i:inline key="${subscription.verbosity.formatKey}"/></td>
                                    <td>
                                        <cm:dropdown icon="icon-cog">
                                            <cti:url var="editUrl" value="/notifications/subscription/${subId}/edit"/>
                                            <div id="edit-popup-${subId}" data-dialog class="dn js-smart-notifications-popup"
                                                data-event="yukon:notifications:save" data-title="<cti:msg2 key="yukon.web.modules.smartNotifications.popup.title"/>"
                                                data-url="${editUrl}" data-load-event="yukon:notifications:load" data-width="600"></div>
                                            <cm:dropdownOption key="yukon.web.components.button.edit.label" icon="icon-pencil" data-popup="#edit-popup-${subId}"/>
                                            <cti:url var="detailsUrl" value="/notifications/events/${subscription.type.urlPath}"/>
                                            <c:if test="${subscription.type == 'DEVICE_DATA_MONITOR'}">
                                                <cti:url var="detailsUrl" value="/notifications/events/${subscription.type.urlPath}/${subscription.parameters['monitorId']}"/>
                                            </c:if>
                                            <cm:dropdownOption key=".notificationDetail" icon="icon-email-open" href="${detailsUrl}"/>
                                            <cm:dropdownOption id="unsubscribe-${subId}" key=".unsubscribe" icon="icon-email-delete"
                                                data-subscription-id="${subId}" data-ok-event="yukon:notifications:remove"/>
                                            <d:confirm on="#unsubscribe-${subId}" nameKey="confirmDelete" argument="${subType}"/>
                                        </cm:dropdown>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                             <tr><td colspan="6">
                                <span class="empty-list"><i:inline key="yukon.common.search.noResultsFound"/></span>
                            </td></tr>
                        </c:otherwise>
                    </c:choose>
            </table>
            <tags:pagingResultsControls result="${subscriptions}" adjustPageCount="true"/>
    </tags:sectionContainer2>
</div>


    <cti:url var="smartNotificationsUrl" value="/notifications/subscription/create"/>
    <div id="create-popup" data-dialog
            class="dn js-smart-notifications-popup" data-event="yukon:notifications:save"
            data-title="<cti:msg2 key="yukon.web.modules.smartNotifications.popup.title"/>"
            data-url="${smartNotificationsUrl}" 
            data-load-event="yukon:notifications:load"
            data-width="600"></div>
            
    <div id="filter-popup" class="dn js-filter-popup" data-title="<cti:msg2 key="yukon.common.filter"/>">
        <cti:url var="action" value="/notifications/subscriptions"/>
        <form:form id="filter-form" action="${action}" commandName="filter" method="get">
            
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".type">
                    <tags:selectWithItems path="eventType" items="${eventTypes}" defaultItemLabel="All" />
                </tags:nameValue2>
            </tags:nameValueContainer2>
            
            <div class="action-area">
                <cti:button nameKey="filter" classes="action primary js-filter"/>
                <cti:button nameKey="showAll" classes="js-show-all"/>
            </div>
        </form:form>
     </div>

</cti:msgScope>

<cti:includeScript link="/resources/js/pages/yukon.smart.notifications.js"/>
<cti:includeScript link="YUKON_TIME_FORMATTER"/>

<script>
    yukon.smart.notifications.initTimeSlider();
</script>