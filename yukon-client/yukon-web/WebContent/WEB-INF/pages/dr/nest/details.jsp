<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dr" page="nest.details">

<dt:pickerIncludes/>
    <tags:sectionContainer2 nameKey="nestSyncControls" id="nest-sync-controls">
        <form:form action="nest/settings" method="POST" commandName="nestSyncSettings">
            <cti:csrfToken/>        
            <span>
                <tags:hidden path="sync" id="nest-sync"/>
                <span class="fl" style="padding-right:10px;"><i:inline key=".nest.syncSchedule"/>: </span>
                <div class="button-group button-group-toggle">
                    <cti:button nameKey="on" classes="on yes M0"/>
                    <cti:button nameKey="off" classes="no M0"/>
                </div>
                <tags:timeSlider startPath="syncTime" maxValue="1425" displayTimeToLeft="true"/>
                <cti:button nameKey="save" type="submit" classes="fn" style="margin-left:20px;"/>
                <span class="fr">
                    <cti:button nameKey="syncNow" href="nest/syncNow" classes="fn" style="margin-left:20px;" title="${syncTitle}" disabled="${!syncNowEnabled}"/>        
                </span>
            </span>
        </form:form>
        <div style="margin-top: 20px; width: 320px;">
            <tags:nameValueContainer2 tableClass="with-form-controls" naturalWidth="false">
                <tags:nameValue2 nameKey=".nest.lastSync">
                    ${fn:escapeXml(lastSyncTime)}
                </tags:nameValue2>
            </tags:nameValueContainer2>
        </div>
    </tags:sectionContainer2>
    <tags:sectionContainer2 nameKey="nestSyncDiscrepancies" id="discrepancies">
    </tags:sectionContainer2>
</cti:standardPage>