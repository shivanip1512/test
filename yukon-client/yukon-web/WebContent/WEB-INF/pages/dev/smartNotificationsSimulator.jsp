<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
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
        <div class="notes">See: <a href="https://confluence-prod.tcc.etn.com/display/EEST/Notification+Testing+Email+Addresses">Notification Testing Email Addresses</a>
        for email addresses that are set up for testing.</div>
        <div class="page-action-area stacked">
            <cti:button label="Clear All Subscriptions" type="button" href="clearAllSubscriptions" busy="true"/>
        </div>
    </tags:sectionContainer>

    <tags:sectionContainer title="Events" helpText="Click on the Create Real Events button to generate real events.  Click on the Create Test Events button to generate fake events using random devices from cache.">

        <form:form action="createEvents" method="POST" modelAttribute="smartNotificationSimulatorSettings">
            <cti:csrfToken/>
            
            <tags:hidden path="dailyDigestHour"/>
            <input type="hidden" id="ddmType" value="${ddmType}"/>
            <input type="hidden" id="assetImportType" value="${assetImportType}"/>
            <tags:alertBox type="warning" classes="js-all-types-warning">Only Device Data Monitor and Infrastructure Warning events will be created when All Event Types is selected.</tags:alertBox>
            <tags:nameValueContainer tableClass="natural-width">
                <tags:nameValue name="All Event Types">
                    <tags:checkbox path="allTypes" styleClass="js-all-types"/>
                </tags:nameValue>
                <tags:nameValue name="Event Type" nameClass="js-event-type">
                    <tags:selectWithItems path="type" items="${eventTypes}" inputClass="js-event-type js-event-type-dropdown"/>
                </tags:nameValue>
                <tags:nameValue name="Monitor" nameClass="js-monitor" valueClass="js-monitor">
                    <tags:selectWithItems path="parameter" items="${deviceDataMonitors}" 
                        inputClass="js-monitor-dropdown" itemValue="id" itemLabel="name" defaultItemLabel="All Monitors"/>
                </tags:nameValue>
                <tags:nameValue name="Asset Import Type" nameClass="js-asset-import" valueClass="js-asset-import">
                    <tags:selectWithItems path="parameter" items="${assetImportTypes}" inputClass="js-asset-import-dropdown"/>
                </tags:nameValue>
                <tags:nameValue name="Total number of events per event type">
                    <tags:input path="eventsPerType"/>
                </tags:nameValue>
                <tags:nameValue name="Number of events per message">
                    <tags:input path="eventsPerMessage"/>
                </tags:nameValue>
                <tags:nameValue name="Wait time in seconds">
                    <tags:input path="waitTimeSec"/>
                </tags:nameValue>
            </tags:nameValueContainer>
            
            <div class="page-action-area">
                <cti:button label="Clear All Events And Email History" type="button" href="clearAllEvents" busy="true"/>
                <cti:button label="Create Test Events" type="submit" busy="true"/>
            </div>
    
        </form:form>
        
    </tags:sectionContainer>

    <cti:includeScript link="/resources/js/pages/yukon.dev.simulators.smartNotificationsSimulator.js" />

</cti:standardPage>