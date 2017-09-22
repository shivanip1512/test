<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>

<cti:msgScope paths="modules.smartNotifications">

<div id="notifications-section" style="margin-top:50px;">
    <cti:button icon="icon-email-add" nameKey="subscribe" data-popup="#create-popup" classes="fr"/>
    <tags:sectionContainer2 nameKey="notifications">
    <span><i:inline key=".singleNotification"/>:
        <div class="button-group button-group-toggle"><cti:button nameKey="yes" classes="yes js-single-notification"/><cti:button nameKey="no" classes="no"/></div>
        <div id="send-time" class="dib column-6-18 clearfix dn">
            <div class="column one">
                <div class="js-time-label fwb" style="width:70px;">10:00 AM</div>
                <input type="hidden" id="notifications-send-time" name="sendTime"/>
            </div>
            <div class="column two nogutter">
                <div class="js-time-slider" style="margin-top: 5px;width:250px;"></div>
            </div>
        </div>
        <cti:button nameKey="save" classes="fn" style="margin-left:20px;"/>
    </span>
    <span class="fr">
        <a href="javascript:void(0);" data-popup="#filter-popup" data-popup-toggle>
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
                <th class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
            </tr>
            <c:forEach var="subscription" items="${subscriptions.resultList}">
                <tr>
                    <td><i:inline key="${subscription.type.formatKey}"/></td>
                    <td>
                        <i:inline key="${subscription.frequency.formatKey}"/>
                        <c:if test="${subscription.frequency == 'DAILY_DIGEST'}">
                            - ${subscription.parameters['sendTime']}
                        </c:if>
                    </td>
                    <td><i:inline key="${subscription.media.formatKey}"/></td>
                    <td>${subscription.recipient}</td>    
                    <td><i:inline key="${subscription.verbosity.formatKey}"/></td>
                    <td>
                        <cm:dropdown icon="icon-cog">
                            <cti:url var="editUrl" value="/notifications/subscription/${subscription.id}/edit" />
                            <div id="edit-popup-${subscription.id}" data-dialog
                                    class="dn js-smart-notifications-popup" data-event="yukon:notifications:save"
                                    data-title="<cti:msg2 key="yukon.web.modules.smartNotifications.popup.title"/>"
                                    data-url="${editUrl}" 
                                    data-load-event="yukon:notifications:load"
                                    data-width="600"></div>
                            <cm:dropdownOption key="yukon.web.components.button.edit.label" icon="icon-pencil" data-popup="#edit-popup-${subscription.id}" />
                            <cti:url var="detailsUrl" value="/notifications/${subscription.id}" />
                            <cm:dropdownOption key=".notificationDetail" icon="icon-email-open" href="${detailsUrl}"/>
                            <cm:dropdownOption id="unsubscribe-${subscription.id}" key=".unsubscribe" icon="icon-email-delete" 
                                data-subscription-id="${subscription.id}" data-ok-event="yukon:notifications:remove"/>
                            <d:confirm on="#unsubscribe-${subscription.id}" nameKey="confirmDelete" argument="${subscription.type}"/>
                        </cm:dropdown>
                    </td>       
                </tr>
            </c:forEach>
        </table>
        <tags:pagingResultsControls result="${subscriptions}" adjustPageCount="true" thousands="true"/>
    </tags:sectionContainer2>
</div>

</cti:msgScope>

    <cti:url var="smartNotificationsUrl" value="/notifications/subscription/create"/>
    <div id="create-popup" data-dialog
            class="dn js-smart-notifications-popup" data-event="yukon:notifications:save"
            data-title="<cti:msg2 key="yukon.web.modules.smartNotifications.popup.title"/>"
            data-url="${smartNotificationsUrl}" 
            data-load-event="yukon:notifications:load"
            data-width="600"></div>
            
<cti:includeScript link="/resources/js/pages/yukon.smart.notifications.js"/>
<cti:includeScript link="YUKON_TIME_FORMATTER"/>