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
            <div class="MT30">
                <div>
                    <i:inline key=".intervalEnd"/>
                    <dt:date name="analysisEnd" wrapperClass="fn vam" displayValidationToRight="true" value="${batteryModel.analysisEnd}"/>
                    <cti:button nameKey="generateReport" classes="action fn vab" type="submit" icon="icon-page-white-excel"/>
                </div>
            </div>
        </tags:boxContainer2>
    </form:form>

    <form:form action="generateVoltageReport" method="get">
        <tags:boxContainer2 nameKey="waterNode.voltageData">
            <i:inline key="yukon.web.modules.support.waterNode.voltageData.description"/>
            <div class="MT30">
                <div>
                    <i:inline key=".lastGenerated"/>
                    <dt:date name="lastCreatedReport" wrapperClass="fn vam" displayValidationToRight="true" value="${batteryModel.lastCreatedReport}"/>
                    <cti:button nameKey="generateDetailedReport" classes="action fn vab" type="submit" icon="icon-page-white-excel"/>
                </div>
            </div>
        </tags:boxContainer2>
    </form:form>
    
    <form:form action="generateCSVReport" method="get">
        <tags:boxContainer2 nameKey="waterNode.csvReport" >
            <i:inline key="yukon.web.modules.support.waterNode.csvReport.description"/>
            <div class="MT30">
                <div>
                    <tags:file name="csvUpload.csv" buttonKey="yukon.web.modules.support.waterNode.upload"/>
                    <cti:button nameKey="generateCSVReport" classes="action fn vab" type="submit" icon="icon-page-white-excel"/>
                </div>
            </div>
        </tags:boxContainer2>
    </form:form>
</cti:standardPage>