<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="dev" page="smartNotificationsSimulator">
    
    <form action="startDailyDigest">
        <tags:sectionContainer title="Daily Digest" helpText="Select an hour of the day, and click the Send Digest button to initiate daily digest notifications for that hour.">
            <tags:nameValueContainer tableClass="natural-width">
                <tags:nameValue name="Hour of Day">
                    <div class="js-hour-selected" data-hour-selected="${smartNotificationSimulatorSettings.dailyDigestHour }"></div>
                    <select name="hour" id="selectHour">
                        <option value="0">Midnight</option>
                        <option value="1">1:00 AM</option>
                        <option value="2">2:00 AM</option>
                        <option value="3">3:00 AM</option>
                        <option value="4">4:00 AM</option>
                        <option value="5">5:00 AM</option>
                        <option value="6">6:00 AM</option>
                        <option value="7">7:00 AM</option>
                        <option value="8">8:00 AM</option>
                        <option value="9">9:00 AM</option>
                        <option value="10">10:00 AM</option>
                        <option value="11">11:00 AM</option>
                        <option value="12">Noon</option>
                        <option value="13">1:00 PM</option>
                        <option value="14">2:00 PM</option>
                        <option value="15">3:00 PM</option>
                        <option value="16">4:00 PM</option>
                        <option value="17">5:00 PM</option>
                        <option value="18">6:00 PM</option>
                        <option value="19">7:00 PM</option>
                        <option value="20">8:00 PM</option>
                        <option value="21">9:00 PM</option>
                        <option value="22">10:00 PM</option>
                        <option value="23">11:00 PM</option>
                    </select>
                </tags:nameValue>
            </tags:nameValueContainer>
        </tags:sectionContainer>
        
        <div class="page-action-area stacked">
            <cti:button label="Send Digest" type="Submit"/>
        </div>
    </form>
    
    <tags:sectionContainer title="Subscriptions">

        <tags:nameValueContainer tableClass="natural-width">
            <tags:nameValue name="User Group">
                <input type="hidden" id="userGroupId" value = "${smartNotificationSimulatorSettings.userGroupId}"/>

                <tags:pickerDialog type="userGroupPicker" id="userGroupPicker"
                    selectionProperty="userGroupName" destinationFieldId="userGroupId" allowEmptySelection="false"
                    linkType="selection" immediateSelectMode="true" initialId="${smartNotificationSimulatorSettings.userGroupId}" />
                    
            </tags:nameValue>
            
            <tags:nameValue name="">
                <tags:checkbox id="generateTestEmailAddresses" path="smartNotificationSimulatorSettings.generateTestEmail"/> 
                Generate test email addresses
            </tags:nameValue>
            
            <tags:nameValue name="Subscription Setings">
                <cti:button label="Settings" data-popup="#create-popup"/>
            </tags:nameValue>

        </tags:nameValueContainer>


    </tags:sectionContainer>

    <div class="page-action-area stacked">
        <cti:button label="Clear All Subscriptions" type="button" href="clearAllSubscriptions" busy="true"/>

    </div>

    <tags:sectionContainer title="Events" helpText="Click on the Create Real Events button to generate real events.  Click on the Create Test Events button to generate fake events using random devices from cache.">


        <tags:nameValueContainer tableClass="natural-width">
            <tags:nameValue name="Total number of events per event type">
                <input id="numberOfMessages" value="${smartNotificationSimulatorSettings.eventsPerType }" />
            </tags:nameValue>
            <tags:nameValue name="Number of events per message">
                <input id="eventsPerMessage" value="${smartNotificationSimulatorSettings.eventsPerMessage }" />
            </tags:nameValue>
            <tags:nameValue name="Wait time in seconds">
                <input id="waitTime" value="${smartNotificationSimulatorSettings.waitTimeSec }" />
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