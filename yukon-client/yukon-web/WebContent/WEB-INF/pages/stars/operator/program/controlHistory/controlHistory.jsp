<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags/dr" prefix="dr"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:url var="completeHistoryUrl" value="/spring/stars/operator/program/controlHistory/completeHistoryView"/>

<cti:standardPage module="operator" page="controlHistory">
    <cti:includeCss link="/WebConfig/yukon/styles/controlHistory.css"/>
    
    <div id="programs">
        <dr:controlHistorySummary displayableProgramList="${displayablePrograms}"
                                  showControlHistorySummary="${true}" 
                                  completeHistoryUrl="${completeHistoryUrl}" />
    </div>
                            
</cti:standardPage>