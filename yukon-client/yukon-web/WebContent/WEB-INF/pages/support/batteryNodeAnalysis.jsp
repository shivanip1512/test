<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<cti:standardPage module="support" page="batteryNodeAnalysis">
    <div class="column-12-12">
        <div class="column one">
            <form:form action="generateBatteryConditionReport" method="get">
                <tags:boxContainer2 nameKey="batteryConditionReport" styleClass="fixedMediumWidth">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".intervalEnd">
                            <dt:date name="analysisEnd" maxDate="${maxDate}" minDate="${minDate}" wrapperClass="fn vam" displayValidationToRight="true" value="${batteryModel.analysisEnd}"/>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                    <div style="margin-top:20px;">
                        <cti:button nameKey="generateReport" classes="action fn vab" type="submit" icon="icon-page-white-excel"/>
                    </div>
                </tags:boxContainer2>
            </form:form>
            <form:form action="generateVoltageDataReport" method="get">
                <tags:boxContainer2 nameKey="voltageDataReport">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".intervalEnd">
                            <dt:date name="lastCreatedReport" maxDate="${maxDate}" minDate="${minDate}" wrapperClass="fn vam" displayValidationToRight="true" value="${batteryModel.lastCreatedReport}"/>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                    <div style="margin-top:20px;">
                        <cti:button nameKey="generateReport" classes="action fn vab" type="submit" icon="icon-page-white-excel"/>
                    </div>
                </tags:boxContainer2>
            </form:form>

        </div>
        <div class="column two nogutter">
            <form:form action="generatePreExistingVoltageDataAnalysisReport" method="post" enctype="multipart/form-data">
                <cti:csrfToken/>
                    <tags:boxContainer2 nameKey="preExistingVoltageDataAnalysisReport">
                    <tags:nameValueContainer2>
                         <tags:nameValue2 nameKey=".importFile">
                            <tags:file name="uploadedFile" classes="js-file" buttonKey=".upload"/>
                         </tags:nameValue2>
                         <tags:nameValue2 nameKey=".intervalEnd">
                            <dt:date name="csvEndDate" maxDate="${maxDate}" minDate="${minDate}" wrapperClass="fn vam" displayValidationToRight="true" value="${batteryModel.csvEndDate}"/>
                         </tags:nameValue2>
                    </tags:nameValueContainer2>
                    <div style="margin-top:20px;">
                        <cti:button nameKey="generateReport" classes="action fn vab" type="submit" icon="icon-page-white-excel"/>
                    </div>
                    </tags:boxContainer2>
            </form:form>
        </div>
</cti:standardPage>