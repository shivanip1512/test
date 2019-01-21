<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:msgScope paths="modules.smartNotifications">

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
                        <c:set var="subDescription" value="${subType}"/>
                        <c:if test="${subscription.type == 'DEVICE_DATA_MONITOR'}">
                            <c:set var="subDescription" value="${subType} - ${deviceDataMonitors.get(subscription.parameters['monitorId'])}"/>
                        </c:if>
                        <c:if test="${subscription.type == 'ASSET_IMPORT'}">
                            <cti:msg2 var="assetImportResultType" key="yukon.web.modules.operator.assetImportResultType.${subscription.parameters['assetImportResultType']}"/>
                            <c:set var="subDescription" value="${subType} - ${assetImportResultType}"/>
                        </c:if>
                        <tr class="js-${subscription.frequency}">
                            <td>${fn:escapeXml(subDescription)}</td>
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
                                        data-event="yukon:notifications:save" data-title="<cti:msg2 key=".editPopup.title"/>"
                                        data-url="${editUrl}" data-load-event="yukon:notifications:load" data-width="600"></div>
                                    <cm:dropdownOption key="yukon.web.components.button.edit.label" icon="icon-pencil" data-popup="#edit-popup-${subId}"/>
                                    <cti:url var="detailsUrl" value="/notifications/events/${subscription.type.urlPath}"/>
                                    <c:if test="${subscription.type == 'DEVICE_DATA_MONITOR'}">
                                        <cti:url var="detailsUrl" value="/notifications/events/${subscription.type.urlPath}/${subscription.parameters['monitorId']}"/>
                                    </c:if>
                                    <cm:dropdownOption key=".notificationDetail" icon="icon-email-open" href="${detailsUrl}"/>
                                    <cm:dropdownOption key=".unsubscribe" icon="icon-email-delete" data-subscription-id="${subId}" 
                                        data-ok-event="yukon:notifications:remove" classes="js-hide-dropdown js-unsubscribe-${subId}"/>
                                    <d:confirm on=".js-unsubscribe-${subId}" nameKey="unsubscribeConfirmation" argument="${subDescription}"/>
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
        </tbody>
    </table>
    <tags:pagingResultsControls result="${subscriptions}" adjustPageCount="true"/>
        
</cti:msgScope>
