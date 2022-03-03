<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="dev" page="rfnTest.viewLcrDataSimulator">
    <div class="column-12-12 clearfix">
    <div class="column one">
    <form id='dataSimulatorForm'>
    <tags:sectionContainer2 nameKey="lcrDataSimulator">
        <div id='lcrForm'>
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".lcrDataSimulator.serialNumberRangeLcr6200">
                <input id="lcr6200serialFrom" name="lcr6200serialFrom" type="text" value=${currentSettings.lcr6200serialFrom}> <i:inline key="yukon.common.to"/>
                <input id="lcr6200serialTo" name="lcr6200serialTo" type="text" value=${currentSettings.lcr6200serialTo}> 
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".lcrDataSimulator.serialNumberRangeLcr6600">
                <input id="lcr6600serialFrom" name="Lcr6600serialFrom" type="text" value=${currentSettings.lcr6600serialFrom}> <i:inline key="yukon.common.to"/> 
                <input id="lcr6600serialTo" name="lcr6600serialTo" type="text" value=${currentSettings.lcr6600serialTo}>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".lcrDataSimulator.serialNumberRangeLcr6700">
                <input id="lcr6700serialFrom" name="Lcr6700serialFrom" type="text" value=${currentSettings.lcr6700serialFrom}> <i:inline key="yukon.common.to"/> 
                <input id="lcr6700serialTo" name="lcr6700serialTo" type="text" value=${currentSettings.lcr6700serialTo}>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".lcrDataSimulator.duplicates">
                <input id="percentOfDuplicates" name="percentOfDuplicates" type="text" value=${currentSettings.percentOfDuplicates} maxlength="3" size="3"> %
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".lcrDataSimulator.tlvVersion">
                <select name="tlvVersion">
                    <option value="4" ${currentSettings.tlvVersion eq 4 ? 'selected' : ''}>0.0.4</option>
                    <option value="5" ${currentSettings.tlvVersion eq 5 ? 'selected' : ''}>0.0.5 (PQR)</option>
                </select>
            </tags:nameValue2>
        </tags:nameValueContainer2>
        </div>
        <br/>
        <div>
            <cti:button id="start-simulator" nameKey="startSimulator" />
            <cti:button id="stop-simulator" nameKey="stopSimulator" classes="dn"/>
        </div>
        <div class="button-group button-group-toggle">
            <div class="js-sim-startup" data-simulator-type="RFN_LCR">
                <cti:button nameKey="runSimulatorOnStartup.automatic" classes="yes enable-startup"/>
                <cti:button nameKey="runSimulatorOnStartup.manual" classes="no disable-startup"/>
            </div>
        </div>
        <br><br>
        <div>
            <cti:button id="send-message" nameKey="sendLcrDeviceMessages"/>
            <cti:button id="stop-send-message" nameKey="stopSendingLcrDeviceMessages" classes="dn"/>
        </div>
    </tags:sectionContainer2>
    </form>
    </div>
    <div id="taskStatusDiv" class="column two nogutter">
        <tags:sectionContainer title="Simulation status by range">
        <div id="taskStatusMessage"></div>
            <div>
                <div><span id="status-running" style="font-size:12pt;color:blue;">${dataSimulatorStatus.running}</span></div>
                <div>Start Time: <span id="status-start-time">${dataSimulatorStatus.startTime}</span></div>
                <div>Stop Time: <span id="status-stop-time">${dataSimulatorStatus.stopTime}</span></div>
                <div>Success:  <span id="status-num-success" class="success">${dataSimulatorStatus.success}</span></div>
                <div>Failure:  <span id="status-num-failed" class="error">${dataSimulatorStatus.failure}</span></div>
                <div>Last Injection Time: <span id="status-last-injection-time">${dataSimulatorStatus.lastInjectionTime}</span></div>
            </div>
        </div>
        </tags:sectionContainer>
        <div id="existingDeviceTaskStatusDiv" class="column two nogutter">
        <tags:sectionContainer title="Simulation status for all RFN LCRs">
        <div id="existingDeviceTaskStatusMessage"></div>
            <div>
                <div><span id="existing-status-running" style="font-size:12pt;color:blue;">${existingDataSimulatorStatus.running}</span></div>
                <div>Start Time: <span id="existing-status-start-time">${existingDataSimulatorStatus.startTime}</span></div>
                <div>Stop Time: <span id="existing-status-stop-time">${existingDataSimulatorStatus.stopTime}</span></div>
                <div>Success:  <span id="existing-status-num-success" class="success">${existingDataSimulatorStatus.success}</span></div>
                <div>Failure:  <span id="existing-status-num-failed" class="error">${existingDataSimulatorStatus.failure}</span></div>
                <div>Last Injection Time: <span id="existing-status-last-injection-time">${existingDataSimulatorStatus.lastInjectionTime}</span></div>
                <hr>
                <cti:icon icon="icon-clock"/>Insertion time is calculated as a minute of the day, using the formula (serialNumber/10) % 1440
            </div>
        </div>
        </tags:sectionContainer>
    </div>
    <cti:includeScript link="/resources/js/common/yukon.ui.progressbar.js"/>
    <cti:includeScript link="/resources/js/pages/yukon.dev.simulators.dataSimulator.js"/>
    <cti:includeScript link="/resources/js/pages/yukon.dev.simulators.simulatorStartup.js"/>
</cti:standardPage>