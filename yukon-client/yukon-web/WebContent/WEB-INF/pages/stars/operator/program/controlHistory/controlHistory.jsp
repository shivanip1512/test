<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags/dr" prefix="dr"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:url var="completeHistoryUrl" value="/stars/operator/program/controlHistory/completeHistoryView"/>

<cti:standardPage module="operator" page="controlHistory">
    <cti:includeCss link="/WebConfig/yukon/styles/controlHistory.css"/>
    
    <div id="programs">
        <dr:controlHistorySummary displayableProgramList="${currentControlHistory}"
                                  showControlHistorySummary="${true}" past="false"
                                  completeHistoryUrl="${completeHistoryUrl}" titleKey="yukon.web.modules.operator.controlHistory.currentEnrollmentControlHistory"/>
        
        <br>
        <br>
        
        <dr:controlHistorySummary displayableProgramList="${previousControlHistory}"
                                  showControlHistorySummary="${true}" 
                                  completeHistoryUrl="${completeHistoryUrl}" titleKey="yukon.web.modules.operator.controlHistory.previousEnrollmentControlHistory" past="true"/>
    </div>
                            
</cti:standardPage>