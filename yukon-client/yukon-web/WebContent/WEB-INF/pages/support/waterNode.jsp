<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<cti:standardPage module="support" page="waterNode">
    <form:form action="generateReport" method="get">
        <tags:boxContainer2 nameKey="waterNode.batteryAnalysis">
            <i:inline key=".description"/>
            <div><br> <i:inline key=".depletionCategoryDefinition"/></div>
            <ul>
                <li><i:inline key=".batteryCategory.NORMAL"/></li>
                <li><i:inline key=".batteryCategory.LOW"/></li>
                <li><i:inline key=".batteryCategory.CRITICALLY_LOW"/></li>
                <li><i:inline key=".batteryCategory.UNREPORTED"/></li>
            </ul>
            <i:inline key=".sleepingCurrentDefinition"/>
            <div><br><i:inline key="yukon.web.modules.support.waterNode.batteryAnalysis.description"/></div>
            <ul>
                <li><i:inline key="yukon.web.modules.support.waterNode.deviceName"/></li>
                <li><i:inline key="yukon.web.modules.support.waterNode.meterNumber"/></li>
                <li><i:inline key="yukon.web.modules.support.waterNode.serialNumber"/></li>
                <li><i:inline key="yukon.web.modules.support.waterNode.deviceType"/></li>
                <li><i:inline key="yukon.web.modules.support.waterNode.depletionCategory"/></li>
                <li><i:inline key="yukon.web.modules.support.waterNode.highSleepingCurrent"/></li>
                <li><i:inline key="yukon.web.modules.support.waterNode.mostRecentVoltage"/></li>
                <li><i:inline key="yukon.web.modules.support.waterNode.uom"/></li>
                <li><i:inline key="yukon.web.modules.support.waterNode.date"/></li>
                <li><i:inline key="yukon.web.modules.support.waterNode.time"/></li>
            </ul>
            <div class="MT30">
                <div>
                    <i:inline key=".intervalEnd"/>
                    <dt:date name="analysisEnd" maxDate="${maxDate}" minDate="${minDate}" wrapperClass="fn vam" displayValidationToRight="true" value="${batteryModel.analysisEnd}"/>
                    <cti:button nameKey="generateReport" classes="action fn vab" type="submit" icon="icon-page-white-excel"/>
                </div>
            </div>
        </tags:boxContainer2>
    </form:form>

    <form:form action="generateVoltageReport" method="get">
        <tags:boxContainer2 nameKey="waterNode.voltageData">
            <i:inline key="yukon.web.modules.support.waterNode.voltageData.description"/>
            <ul>
                <li><i:inline key="yukon.web.modules.support.waterNode.deviceName"/></li>
                <li><i:inline key="yukon.web.modules.support.waterNode.meterNumber"/></li>
                <li><i:inline key="yukon.web.modules.support.waterNode.serialNumber"/></li>
                <li><i:inline key="yukon.web.modules.support.waterNode.deviceType"/></li>
                <li><i:inline key="yukon.web.modules.support.waterNode.date"/></li>
                <li><i:inline key="yukon.web.modules.support.waterNode.time"/></li>
                <li><i:inline key="yukon.web.modules.support.waterNode.reading"/></li>
            </ul>
            <div class="MT30">
                <div>
                    <i:inline key=".intervalEnd"/>
                    <dt:date name="lastCreatedReport" maxDate="${maxDate}" minDate="${minDate}" wrapperClass="fn vam" displayValidationToRight="true" value="${batteryModel.lastCreatedReport}"/>
                    <cti:button nameKey="generateDetailedReport" classes="action fn vab" type="submit" icon="icon-page-white-excel"/>
                </div>
            </div>
        </tags:boxContainer2>
    </form:form>
    
    <form:form action="generateCSVReport" method="post" enctype="multipart/form-data">
        <cti:csrfToken/>
            <tags:boxContainer2 nameKey="waterNode.csvReport">
                <i:inline key="yukon.web.modules.support.waterNode.csvReport.description"/>
                <div><br><i:inline key="yukon.web.modules.support.waterNode.csvReport.headerRow"/></div>
                <div><br><i:inline key="yukon.web.modules.support.waterNode.batteryAnalysis.description"/></div>
                <ul>
                    <li><i:inline key="yukon.web.modules.support.waterNode.deviceName"/></li>
                    <li><i:inline key="yukon.web.modules.support.waterNode.meterNumber"/></li>
                    <li><i:inline key="yukon.web.modules.support.waterNode.serialNumber"/></li>
                    <li><i:inline key="yukon.web.modules.support.waterNode.deviceType"/></li>
                    <li><i:inline key="yukon.web.modules.support.waterNode.depletionCategory"/></li>
                    <li><i:inline key="yukon.web.modules.support.waterNode.highSleepingCurrent"/></li>
                    <li><i:inline key="yukon.web.modules.support.waterNode.mostRecentVoltage"/></li>
                    <li><i:inline key="yukon.web.modules.support.waterNode.uom"/></li>
                    <li><i:inline key="yukon.web.modules.support.waterNode.date"/></li>
                    <li><i:inline key="yukon.web.modules.support.waterNode.time"/></li>
                </ul>
                <div class="MT30">
                    <tags:file name="uploadedFile" classes="js-file" buttonKey="yukon.web.modules.support.waterNode.upload"/>
                </div>
                <div class="MT30">
                    <i:inline key=".intervalEnd"/>
                    <dt:date name="csvEndDate" maxDate="${maxDate}" minDate="${minDate}" wrapperClass="fn vam" displayValidationToRight="true" value="${batteryModel.csvEndDate}"/>
                    <cti:button nameKey="generateCSVReport" classes="action fn vab" type="submit" icon="icon-page-white-excel"/>
                </div>
            </tags:boxContainer2>
        </form:form>
</cti:standardPage>