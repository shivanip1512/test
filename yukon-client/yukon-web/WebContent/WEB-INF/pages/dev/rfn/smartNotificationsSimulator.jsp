<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dev" page="rfnTest.smartNotificationsSimulator">

    <tags:sectionContainer title="Subscriptions">
                
        <input type="hidden" id="userGroupId"/>
    
        <tags:pickerDialog type="userGroupPicker" id="userGroupPicker" selectionProperty="userGroupName"
        destinationFieldId="userGroupId" linkType="selection" immediateSelectMode="true"/>
        
        <cti:button label="Settings" data-popup="#create-popup"/>
        
    </tags:sectionContainer>

    <div class="page-action-area">

        <cti:button label="Clear All Subscriptions" type="button" href="clearAllSubscriptions"/>
        <cti:button label="Create Events" type="button" href="createEvents"/>
    
    </div>
    
    <cti:url var="smartNotificationsUrl" value="/notifications/subscription/create"/>
    <div id="create-popup" data-dialog
            class="dn js-smart-notifications-popup" data-event="yukon:simulatorNotifications:save"
            data-title="<cti:msg2 key="yukon.web.modules.smartNotifications.popup.title"/>"
            data-url="${smartNotificationsUrl}" 
            data-load-event="yukon:notifications:load"
            data-width="600"></div>
    
    <cti:includeScript link="YUKON_TIME_FORMATTER"/>
    <cti:includeScript link="/resources/js/pages/yukon.smart.notifications.js"/>
    <cti:includeScript link="/resources/js/pages/yukon.dev.simulators.smartNotificationsSimulator.js"/>

</cti:standardPage>