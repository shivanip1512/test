<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="controlHistory">
    <cti:includeScript link="/JavaScript/yukon.assets.controlhistory.js"/>
        
        <div id="programs" class="f-block-this">
            <div class="clearfix stacked">
                <dr:controlHistorySummary displayableProgramList="${currentControlHistory}"
                                          showControlHistorySummary="${true}" 
                                          past="false"
                                          completeHistoryUrl="/stars/operator/program/controlHistory/completeHistoryView" 
                                          titleKey="yukon.web.modules.operator.controlHistory.currentEnrollmentControlHistory"/>
            </div>
            <div class="clearfix stacked js-past-enrollment-history">
                <input name="accountId" value="${accountId}" hidden="true"/>
                <a href="javascript:void(0);" class="js-show-past-enrollment-history"><i:inline key=".viewPastEnrollments"/></a>
            </div>
        </div>
</cti:standardPage>