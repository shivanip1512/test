<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags/i18n" prefix="i"%>
<%@ taglib tagdir="/WEB-INF/tags/dr" prefix="dr"%>

<cti:url var="completeHistoryUrl" value="/stars/consumer/controlhistory/completeHistoryView"/>

<cti:standardPage module="consumer" page="controlhistory">
    <div id="programs">
		<dr:controlHistorySummary displayableProgramList="${displayablePrograms}" completeHistoryUrl="${completeHistoryUrl}" titleKey="" past="false"/>
    </div>      
</cti:standardPage>