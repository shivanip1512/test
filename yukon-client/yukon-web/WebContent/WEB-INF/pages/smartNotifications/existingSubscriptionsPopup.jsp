<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.smartNotifications">

    <div class="js-existing-subscriptions">

        <div id="successMessage" class="user-message success dn"></div>
        <input type="hidden" id="type" value="${type}"/>
        <input type="hidden" id="monitorId" value="${monitorId}"/>
        <cti:msg2 var="subType" key="${type.formatKey}"/>
        
        <c:choose>
            <c:when test="${existingSubscriptions.size() > 0}">
                <div class="empty-list"><i:inline key=".existingSubscriptions.message"/></div>
                <tags:sectionContainer2 nameKey="existingSubscriptions" arguments="${subType}">
                    <div class="scroll-md">
                        <table class="compact-results-table">
                            <tr>
                                <c:if test="${type == eventTypeDDM}">
                                    <th><i:inline key=".monitor"/></th>
                                </c:if>
                                <c:if test="${type == eventTypeAssetImport}">
                                    <th><i:inline key=".importResult"/></th>
                                </c:if>
                                <th><i:inline key=".frequency"/></th>
                                <th><i:inline key=".media"/></th>
                                <th><i:inline key=".recipient"/></th>
                                <th><i:inline key=".detail"/></th>
                                <th style="width:20%;"></th>
                            </tr>
                            <tbody>
                                <c:forEach var="subscription" items="${existingSubscriptions}">
                                    <c:set var="subId" value="${subscription.id}"/>
                                    <tr class="js-${subscription.frequency}">
                                        <c:set var="subDescription" value="${subType}"/>
                                        <c:if test="${type == eventTypeDDM}">
                                            <c:set var="monitor" value="${deviceDataMonitors.get(subscription.parameters['monitorId'])}"/>
                                            <c:set var="subDescription" value="${subType} - ${monitor}"/>
                                            <td>${monitor}</td>
                                        </c:if>
                                        <c:if test="${type == eventTypeAssetImport}">
                                            <cti:msg2 var="importResultType" 
                                                      key="yukon.web.modules.operator.assetImportResultType.${subscription.parameters['assetImportResultType']}"/> 
                                            <c:set var="subDescription" value="${subType} - ${importResultType}"/>
                                            <td>${importResultType}</td>
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
                                            <div class="button-group fr wsnw oh">
                                                <cti:url var="editUrl" value="/notifications/subscription/${subId}/edit"/>
                                                <div id="edit-popup-${subId}" data-dialog class="dn js-smart-notifications-popup"
                                                    data-event="yukon:notifications:save" data-title="<cti:msg2 key=".editPopup.title"/>"
                                                    data-url="${editUrl}" data-load-event="yukon:notifications:load" data-width="600"></div>
                                                <cti:button renderMode="buttonImage" icon="icon-pencil" data-popup="#edit-popup-${subId}"/>
                                                <cti:button id="unsubscribe-${subId}" renderMode="buttonImage" icon="icon-cross"
                                                        data-subscription-id="${subId}" data-ok-event="yukon:notifications:remove"/>
                                                <d:confirm on="#unsubscribe-${subId}" nameKey="unsubscribeConfirmation" argument="${subDescription}"/>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </tags:sectionContainer2>
            </c:when>
            <c:otherwise>
                <div class="empty-list stacked"><cti:msg2 key=".noSubscriptions.message" argument="${subType}"/></div>
            </c:otherwise>
        </c:choose>
            
        <div class="fr stacked">
            <cti:url var="createUrl" value="/notifications/subscription/popup/${type}">
                <cti:param name="monitorId" value="${monitorId}"/>
            </cti:url>
            <div id="create-popup-${type}" data-dialog class="dn js-smart-notifications-popup"
                data-event="yukon:notifications:save" data-title="<cti:msg2 key=".createPopup.title"/>"
                data-url="${createUrl}" data-load-event="yukon:notifications:load" data-width="600"></div>
            <cti:button icon="icon-add" data-popup="#create-popup-${type}" nameKey="add"/>
        </div>
    
    </div>

</cti:msgScope>
