<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<cti:standardPage module="operator" page="controlHistory">

<c:set var="completeHistoryUrl" value="/stars/operator/program/controlHistory/completeHistoryView"/>
    
    <div id="programs">
        <div class="clearfix stacked">
            <dr:controlHistorySummary displayableProgramList="${currentControlHistory}"
                                      showControlHistorySummary="${true}" 
                                      past="false"
                                      completeHistoryUrl="${completeHistoryUrl}" 
                                      titleKey="yukon.web.modules.operator.controlHistory.currentEnrollmentControlHistory"/>
        </div>
        
        <div class="clearfix stacked">
            <dr:controlHistorySummary displayableProgramList="${previousControlHistory}"
                                      showControlHistorySummary="${true}" 
                                      past="true"
                                      completeHistoryUrl="${completeHistoryUrl}" 
                                      titleKey="yukon.web.modules.operator.controlHistory.previousEnrollmentControlHistory"/>
        </div>
    </div>

</cti:standardPage>