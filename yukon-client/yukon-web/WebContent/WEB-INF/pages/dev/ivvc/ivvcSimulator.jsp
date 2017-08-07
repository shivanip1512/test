<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="dev" page="ivvc.ivvcSimulator">
    <div id="validationErrors" class="user-message error dn">
        <cti:msg key="yukon.web.modules.dev.ivvc.ivvcSimulator.substationBuskWh.invalid"/>
    </div>
    <div id="save-settings-message" class="user-message dn"></div>
    <div class="column-14-10 clearfix">
        <div class="column one">
            <form id='formData'">
                <cti:csrfToken />
                <cti:msg2 key="modules.dev.ivvc.ivvcSimulator.helpText" var="helpText"/>
                <tags:sectionContainer2 nameKey="ivvc.ivvcSimulator">
                    <div id='ivvcForm'>
                        <tags:nameValueContainer2>
                            <tags:checkboxNameValue path="ivvcSimulatorSettings.increasedSpeedMode" nameKey=".ivvcSimulator.increasedSpeedMode"/>
                            <tags:checkboxNameValue path="ivvcSimulatorSettings.autogenerateSubstationBuskWh" nameKey=".ivvcSimulator.generatekWhAutomatically"/>
                            <tags:nameValue2 nameKey=".ivvcSimulator.substationBuskWh">
                                <tags:input path="ivvcSimulatorSettings.substationBuskWh" id="substationBuskWh"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".ivvcSimulator.localVoltageOffsetVar">
                                <tags:input path="ivvcSimulatorSettings.localVoltageOffsetVar" id="localVoltageOffsetVar"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".ivvcSimulator.remoteVoltageOffsetVar">
                                <tags:input path="ivvcSimulatorSettings.remoteVoltageOffsetVar" id="remoteVoltageOffsetVar"/>
                            </tags:nameValue2>
                        </tags:nameValueContainer2>
                    </div>
                    <div>
                       <cti:button id="save-settings" nameKey="saveSettings"/>
                       <cti:button id="send-message" nameKey="sendIvvcMessages"/>
                       <cti:button id="stop-send-message" nameKey="stopSendingIvvcMessages" classes="dn"/>
                    </div>
                    <div class="button-group button-group-toggle">
                       <div class="js-sim-startup" data-simulator-type="IVVC"></div>
                       <cti:button id="enable-startup" nameKey="runSimulatorOnStartup.automatic" classes="yes"/>
                       <cti:button id="disable-startup" nameKey="runSimulatorOnStartup.manual" classes="no"/>  
                    </div>
                </tags:sectionContainer2>
            </form>
        </div>

        <div id="taskStatusDiv" class="column two nogutter">
            <tags:sectionContainer title="IVVC Simulator Status">
                <div id="taskStatusMessage"></div>
                <div>
                    <div>
                        <span id="status-running" style="font-size: 12pt; color: blue;">${ivvcSimulatorStatus.running}</span>
                    </div>
                </div>
            </tags:sectionContainer>
        </div>
    </div>
    <cti:includeScript link="/resources/js/common/yukon.ui.progressbar.js" />
    <cti:includeScript link="/resources/js/pages/yukon.dev.simulators.simulatorStartup.js" />
    <cti:includeScript link="/resources/js/pages/yukon.dev.simulators.ivvcSimulator.js" />
</cti:standardPage>
