<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>

<cti:standardPage module="smartNotifications" page="settings">

    <div class="action-area">
<%--         <cti:button icon="icon-add" nameKey="create" data-popup="#create-popup"/> --%>
        <cti:button icon="icon-email-add" nameKey="subscribe" data-popup="#create-popup"/>
    </div>

    <cti:url var="smartNotificationsUrl" value="/notifications/subscription/create"/>
    <div id="create-popup" data-dialog
            class="dn js-smart-notifications-popup" data-event="yukon:notifications:save"
            data-title="<cti:msg2 key="yukon.web.modules.smartNotifications.popup.title"/>"
            data-url="${smartNotificationsUrl}" 
            data-load-event="yukon:notifications:load"
            data-width="600"></div>
            
    <cti:includeScript link="/resources/js/pages/yukon.smart.notifications.js"/>
    <cti:includeScript link="YUKON_TIME_FORMATTER"/>

</cti:standardPage>