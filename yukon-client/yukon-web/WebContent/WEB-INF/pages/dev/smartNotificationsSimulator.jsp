<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="dev" page="smartNotificationsSimulator">

    <tags:sectionContainer title="Subscriptions">

        <tags:nameValueContainer tableClass="natural-width">
            <tags:nameValue name="User Group">
                <input type="hidden" id="userGroupId" />

                <tags:pickerDialog type="userGroupPicker" id="userGroupPicker"
                    selectionProperty="userGroupName" destinationFieldId="userGroupId"
                    linkType="selection" immediateSelectMode="true" />
                    
            </tags:nameValue>
            
            <tags:nameValue name="">
                <input type="checkbox" id="generateTestEmailAddresses"/> Generate test email addresses
            </tags:nameValue>
            
            <tags:nameValue name="Subscription Setings">
                <cti:button label="Settings" data-popup="#create-popup"/>
            </tags:nameValue>

        </tags:nameValueContainer>


    </tags:sectionContainer>

    <div class="page-action-area" style="margin-bottom: 20px;">
        <cti:button label="Clear All Subscriptions" type="button" href="clearAllSubscriptions" busy="true"/>

    </div>

    <tags:sectionContainer title="Events" helpText="Click on the Create Real Events button to generate real events.  Click on the Create Test Events button to generate fake events using random devices from cache.">


        <tags:nameValueContainer tableClass="natural-width">
            <tags:nameValue name="Total number of events">
                <input id="numberOfMessages" value="500" />
            </tags:nameValue>
            <tags:nameValue name="Number of events per message">
                <input id="eventsPerMessage" value="5" />
            </tags:nameValue>
            <tags:nameValue name="Wait time in seconds">
                <input id="waitTime" value="3" />
            </tags:nameValue>
        </tags:nameValueContainer>
    </tags:sectionContainer>

    <div class="page-action-area">

        <cti:button label="Create Real Events" type="button" href="createRealEvents" busy="true"/>
        <cti:button label="Create Test Events" type="button" classes="js-create-events" busy="true"/>
        <cti:button label="Clear All Events" type="button" href="clearAllEvents" busy="true"/>

    </div>

    <cti:url var="smartNotificationsUrl" value="/notifications/subscription/create" />
    <div id="create-popup" data-dialog class="dn js-smart-notifications-popup"
        data-event="yukon:simulatorNotifications:save"
        data-title="<cti:msg2 key="yukon.web.modules.smartNotifications.popup.title"/>"
        data-url="${smartNotificationsUrl}" data-load-event="yukon:notifications:load"
        data-width="600"></div>

    <cti:includeScript link="YUKON_TIME_FORMATTER" />
    <cti:includeScript link="/resources/js/pages/yukon.smart.notifications.js" />
    <cti:includeScript
        link="/resources/js/pages/yukon.dev.simulators.smartNotificationsSimulator.js" />

</cti:standardPage>