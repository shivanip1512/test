<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.smartNotifications">

    <i:inline key=".existingSubscriptions.message"/>

    <cti:msg2 var="subType" key="${type.formatKey}"/>
    <tags:sectionContainer2 nameKey="existingSubscriptions" arguments="${subType}">

        <div class="scroll-md">
            <table id="existingSubscriptions" class="compact-results-table has-actions">
                <tr>
                    <c:if test="${type == 'DEVICE_DATA_MONITOR'}">
                        <th><i:inline key=".monitor"/></th>
                    </c:if>
                    <th><i:inline key=".frequency"/></th>
                    <th><i:inline key=".media"/></th>
                    <th><i:inline key=".recipient"/></th>
                    <th><i:inline key=".detail"/></th>
                    <th class="action-column"></th>
                </tr>
                <tbody>
                    <c:forEach var="subscription" items="${existingSubscriptions}">
                        <c:set var="subId" value="${subscription.id}"/>
                        <tr class="js-${subscription.frequency}">
                            <c:if test="${type == 'DEVICE_DATA_MONITOR'}">
                                <td>${deviceDataMonitors.get(subscription.parameters['monitorId'])}</td>
                            </c:if>
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
                                <cti:url var="editUrl" value="/notifications/subscription/${subId}/edit"/>
                                <div id="edit-popup-${subId}" data-dialog class="dn js-smart-notifications-popup"
                                    data-event="yukon:notifications:save" data-title="<cti:msg2 key=".editPopup.title"/>"
                                    data-url="${editUrl}" data-load-event="yukon:notifications:load" data-width="600"></div>
                                <cti:button renderMode="buttonImage" icon="icon-pencil" data-popup="#edit-popup-${subId}"/>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
        
    </tags:sectionContainer2>
        
    <div class="fr stacked">
        <cti:url var="createUrl" value="/notifications/subscription/popup/${type}">
            <cti:param name="monitorId" value="${monitorId}"/>
        </cti:url>
        <div id="create-popup" data-dialog class="dn js-smart-notifications-popup"
            data-event="yukon:notifications:save" data-title="<cti:msg2 key=".createPopup.title"/>"
            data-url="${createUrl}" data-load-event="yukon:notifications:load" data-width="600"></div>
        <cti:button icon="icon-plus-green" data-popup="#create-popup" nameKey="add"/>
    </div>

</cti:msgScope>
