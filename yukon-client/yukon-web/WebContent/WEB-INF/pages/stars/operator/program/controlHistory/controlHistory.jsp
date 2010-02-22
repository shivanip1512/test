<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags/i18n" prefix="i18n"%>
<%@ taglib tagdir="/WEB-INF/tags/dr" prefix="dr"%>

<cti:url var="completeHistoryUrl" value="/spring/stars/operator/program/controlHistory/completeHistoryView"/>

<cti:standardPage module="operator" page="controlHistory">
    <cti:standardMenu />
    
    <i18n:sectionContainer titleKey=".header" >
	    <div id="programs">
			<dr:controlHistorySummary displayableProgramList="${displayablePrograms}"
			                          showControlHistorySummary="${true}" 
			                          completeHistoryUrl="${completeHistoryUrl}" />
	    </div>
    </i18n:sectionContainer>
                            
</cti:standardPage>