<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags/i18n" prefix="i18n"%>
<%@ taglib tagdir="/WEB-INF/tags/dr" prefix="dr"%>

<cti:url var="completeHistoryUrl" value="/spring/stars/consumer/controlhistory/completeHistoryView"/>

<cti:standardPage module="consumer" page="controlHistory">
    <cti:standardMenu />
    
    <h3><i18n:inline key=".header" /></h3>
    <br>
    <div id="programs">
		<dr:controlHistorySummary displayableProgramList="${displayablePrograms}" completeHistoryUrl="${completeHistoryUrl}" />
    </div>      
                            
</cti:standardPage>