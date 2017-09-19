<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dev" page="smartNotificationsSimulator">

    <tags:sectionContainer title="Subscriptions">
                
        <input type="hidden" id="userGroupId"/>
    
        <tags:pickerDialog type="userGroupPicker" id="userGroupPicker" selectionProperty="userGroupName"
        destinationFieldId="userGroupId" linkType="selection" immediateSelectMode="true"/>
        
        <cti:button label="Settings" data-popup="#create-popup"/>
        
    </tags:sectionContainer>
    
    <tags:nameValueContainer tableClass="natural-width">
        <tags:nameValue name="Wait time in seconds">
            <input id="waitTime" value="0"/>
        </tags:nameValue>
    </tags:nameValueContainer>

    <div class="page-action-area">

        <cti:button label="Create Events" type="button" classes="js-create-events"/>
        <cti:button label="Clear All Subscriptions" type="button" href="clearAllSubscriptions"/>
    
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