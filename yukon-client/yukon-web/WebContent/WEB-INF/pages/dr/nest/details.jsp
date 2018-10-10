<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dr" page="nest.details">
    <tags:sectionContainer2 nameKey="nestSyncControls" id="nest-sync-controls">
        <form:form action="nest/settings" method="POST" modelAttribute="nestSyncSettings">
            <cti:csrfToken/>        
            <span id="nest-sync-settings-row">
                <tags:nameValueContainer2 naturalWidth="true">
                    <tags:hidden path="sync" id="nest-sync"/>
                    <tags:nameValue2 nameKey=".nest.syncSchedule">
                        <div class="button-group button-group-toggle nest-sync-button-group-toggle" style="padding-right:20px;">
                            <cti:button nameKey="on" classes="on yes M0"/>
                            <cti:button nameKey="off" classes="no M0"/>
                        </div>
                        <span class="nest-sync-time-slider">
                            <tags:timeSlider startName="scheduledSyncTime" startValue="${scheduledSyncTime}" timeFormat="HHMM" maxValue="1425" displayTimeToLeft="true"/>
                        </span>
                        <cti:button nameKey="save" type="submit" classes="fn" style="margin-left:20px;"/>                            
                        <cti:button nameKey="syncNow" href="nest/syncNow" classes="fn" style="margin-left:20px;" title="${syncTitle}" disabled="${!syncNowEnabled}"/>        
                    </tags:nameValue2>
                </tags:nameValueContainer2>        
            </span>
        </form:form>
        <div style="margin-top: 20px;">
            <tags:nameValueContainer2 naturalWidth="true">        
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
