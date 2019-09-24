<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<cti:standardPage module="support" page="batteryNodeAnalysis">
    <form:form action="generateBatteryConditionReport" method="get">
        <tags:boxContainer2 nameKey="batteryConditionReport">
            <i:inline key=".batteryConditionReport.description"/>
            <div><br> <i:inline key=".batteryConditionReport.depletionCategoryDefinition"/></div>
            <ul>
                <li><i:inline key=".batteryCategory.NORMAL"/></li>
                <li><i:inline key=".batteryCategory.LOW"/></li>
                <li><i:inline key=".batteryCategory.CRITICALLY_LOW"/></li>
                <li><i:inline key=".batteryCategory.UNREPORTED"/></li>
            </ul>
            <i:inline key=".batteryConditionReport.sleepingCurrentDefinition"/>
            <div><br><i:inline key=".generatedReport.description"/></div>
            <ul>
                <li><i:inline key=".fileHeader.deviceName"/></li>
                <li><i:inline key=".fileHeader.meterNumber"/></li>
                <li><i:inline key=".fileHeader.serialNumber"/></li>
                <li><i:inline key=".fileHeader.deviceType"/></li>
                <li><i:inline key=".fileHeader.depletionCategory"/></li>
                <li><i:inline key=".fileHeader.highSleepingCurrentIndicator"/></li>
                <li><i:inline key=".fileHeader.mostRecentReading"/></li>
                <li><i:inline key=".fileHeader.uom"/></li>
                <li><i:inline key=".fileHeader.date"/></li>
                <li><i:inline key=".fileHeader.time"/></li>
            </ul>
            <div class="MT30">
                <div>
                    <i:inline key=".intervalEnd"/>
                    <dt:date name="analysisEnd" maxDate="${maxDate}" minDate="${minDate}" wrapperClass="fn vam" displayValidationToRight="true" value="${batteryModel.analysisEnd}"/>
                    <cti:button nameKey="batteryConditionReport.generateReport" classes="action fn vab" type="submit" icon="icon-page-white-excel"/>
                </div>
            </div>
        </tags:boxContainer2>
    </form:form>

    <form:form action="generateVoltageDataReport" method="get">
        <tags:boxContainer2 nameKey="voltageDataReport">
            <i:inline key=".voltageDataReport.description"/>
            <ul>
                <li><i:inline key=".fileHeader.deviceName"/></li>
                <li><i:inline key=".fileHeader.meterNumber"/></li>
                <li><i:inline key=".fileHeader.serialNumber"/></li>
                <li><i:inline key=".fileHeader.deviceType"/></li>
                <li><i:inline key=".fileHeader.mostRecentReading"/></li>
                <li><i:inline key=".fileHeader.uom"/></li>                
                <li><i:inline key=".fileHeader.date"/></li>
                <li><i:inline key=".fileHeader.time"/></li>
            </ul>
            <div class="MT30">
                <div>
                    <i:inline key=".intervalEnd"/>
                    <dt:date name="lastCreatedReport" maxDate="${maxDate}" minDate="${minDate}" wrapperClass="fn vam" displayValidationToRight="true" value="${batteryModel.lastCreatedReport}"/>
                    <cti:button nameKey="voltageDataReport.generateReport" classes="action fn vab" type="submit" icon="icon-page-white-excel"/>
                </div>
            </div>
        </tags:boxContainer2>
    </form:form>
    
    <form:form action="generatePreExistingVoltageDataAnalysisReport" method="post" enctype="multipart/form-data">
        <cti:csrfToken/>
            <tags:boxContainer2 nameKey="preExistingVoltageDataAnalysisReport">
                <i:inline key=".preExistingVoltageDataAnalysisReport.description"/>
                <div><br><i:inline key=".preExistingVoltageDataAnalysisReport.headerRow.description"/></div>
                <div><br><i:inline key=".generatedReport.description"/></div>
                <ul>
                    <li><i:inline key=".fileHeader.deviceName"/></li>
                    <li><i:inline key=".fileHeader.meterNumber"/></li>
                    <li><i:inline key=".fileHeader.serialNumber"/></li>
                    <li><i:inline key=".fileHeader.deviceType"/></li>
                    <li><i:inline key=".fileHeader.depletionCategory"/></li>
                    <li><i:inline key=".fileHeader.highSleepingCurrentIndicator"/></li>
                    <li><i:inline key=".fileHeader.mostRecentReading"/></li>
                    <li><i:inline key=".fileHeader.uom"/></li>
                    <li><i:inline key=".fileHeader.date"/></li>
                    <li><i:inline key=".fileHeader.time"/></li>
                </ul>
                <div class="MT30">
                    <tags:file name="uploadedFile" classes="js-file" buttonKey=".upload"/>
                </div>
                <div class="MT30">
                    <i:inline key=".intervalEnd"/>
                    <dt:date name="csvEndDate" maxDate="${maxDate}" minDate="${minDate}" wrapperClass="fn vam" displayValidationToRight="true" value="${batteryModel.csvEndDate}"/>
                    <cti:button nameKey="preExistingVoltageDataAnalysisReport.generateReport" classes="action fn vab" type="submit" icon="icon-page-white-excel"/>
                </div>
            </tags:boxContainer2>
        </form:form>
</cti:standardPage>