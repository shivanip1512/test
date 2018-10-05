<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dr" page="nest.details">
    <tags:sectionContainer2 nameKey="nestSyncControls" id="nest-sync-controls">
        <form:form action="nest/settings" method="POST" modelAttribute="nestSyncSettings">
            <cti:csrfToken/>        
            <span id="nest-sync-settings-row" class="">
                <tags:hidden path="sync" id="nest-sync"/>
                <span class="fl" style="padding-right:10px;"><i:inline key=".nest.syncSchedule"/>: </span>
                <div class="button-group button-group-toggle nest-sync-button-group-toggle" style="padding-right:20px;">
                    <cti:button nameKey="on" classes="on yes M0"/>
                    <cti:button nameKey="off" classes="no M0"/>
                </div>
                <span class="nest-sync-time-slider">
                    <tags:timeSlider startName="scheduledSyncTime" startValue="${scheduledSyncTime}" timeFormat="HHMM" maxValue="1425" displayTimeToLeft="true"/>
                </span>
                <span class="fr">
                    <cti:button nameKey="save" type="submit" classes="fn" style="margin-left:20px;"/>            
                    <cti:button nameKey="syncNow" href="nest/syncNow" classes="fn" style="margin-left:20px;" title="${syncTitle}" disabled="${!syncNowEnabled}"/>        
                </span>
            </span>
        </form:form>
        <div style="margin-top: 20px; width: 320px;">
            <tags:nameValueContainer2 tableClass="with-form-controls" naturalWidth="false">
                <tags:nameValue2 nameKey=".nest.lastSync">
                    ${lastSyncTime}
                </tags:nameValue2>
            </tags:nameValueContainer2>
        </div>
    </tags:sectionContainer2>
    <tags:sectionContainer2 nameKey="nestSyncDiscrepancies" id="discrepancies">
    </tags:sectionContainer2>
<cti:includeScript link="/resources/js/pages/yukon.dr.nest.js"/>
</cti:standardPage>
