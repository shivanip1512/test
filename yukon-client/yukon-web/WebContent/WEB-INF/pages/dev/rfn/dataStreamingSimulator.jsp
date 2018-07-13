<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="dev" page="rfnTest.viewDataStreamingSimulator">
    <c:if test="${not simulatorRunning}">
        <form action="startDataStreamingSimulator" method="POST">
            <cti:csrfToken/>
            
            <tags:sectionContainer title="Verification Response">
                <div><tags:checkbox path="settings.overloadGatewaysOnVerification"/> Overload Gateways</div>
                <div><tags:checkbox path="settings.networkManagerFailOnVerification"/> Network Manager Fail</div>
                <div>
                    <tags:checkbox id="verification-checkbox" path="settings.deviceErrorOnVerificationEnabled"/> Device Error 
                    <tags:selectWithItems id="verification-select" path="settings.deviceErrorOnVerification" items="${deviceErrors}"/> (Error for first
                    <input id="verification-number" type="number" name="numberOfDevicesToErrorOnVerification" min="0" value="${settings.numberOfDevicesToErrorOnVerification}" data-toggle-group="resp-device-error"/> devices)
                </div>
            </tags:sectionContainer>
            
            <tags:sectionContainer title="Config Response">
                <div><tags:checkbox path="settings.overloadGatewaysOnConfig"/> Overload Gateways</div>
                <div><tags:checkbox path="settings.networkManagerFailOnConfig"/> Network Manager Fail</div>
                <div><tags:checkbox path="settings.acceptedWithError"/> Accepted With Error (Overloaded Gateways). Only for "re-send". The first 5 devices will always return no device errors. The remaining devices will always return a random error</div>
                <div>
                    <tags:checkbox id="config-checkbox" path="settings.deviceErrorOnConfigEnabled"/> Device Error 
                    <tags:selectWithItems id="config-select" path="settings.deviceErrorOnConfig" items="${deviceErrors}"/> (Error for first
                    <input id="config-number" type="number" name="numberOfDevicesToErrorOnConfig" min="0" value="${settings.numberOfDevicesToErrorOnConfig}" data-toggle-group="conf-device-error"/> devices)
                </div>
            </tags:sectionContainer>
            <cti:button label="Start Simulator" type="submit"/>
        </form>
    </c:if>
    
    <c:if test="${simulatorRunning}">
        <tags:sectionContainer title="Verification Response">
            <div><tags:checkbox disabled="true" path="settings.overloadGatewaysOnVerification"/> Overload Gateways</div>
            <div><tags:checkbox disabled="true" path="settings.networkManagerFailOnVerification"/> Network Manager Fail</div>
            <div>
                <tags:checkbox path="settings.deviceErrorOnVerificationEnabled" disabled="true"/> Device Error 
                <tags:selectWithItems path="settings.deviceErrorOnVerification" items="${deviceErrors}" disabled="true"/> (Error for first
                <input type="number" min="0" value="${settings.numberOfDevicesToErrorOnVerification}" disabled="true" data-toggle-group="conf-device-error"/> devices)
            </div>
        </tags:sectionContainer>
        
        <tags:sectionContainer title="Config Response">
            <div><tags:checkbox disabled="true" path="settings.overloadGatewaysOnConfig"/> Overload Gateways</div>
            <div><tags:checkbox disabled="true" path="settings.networkManagerFailOnConfig"/> Network Manager Fail</div>
            <div><tags:checkbox disabled="true" path="settings.acceptedWithError"/> Accepted With Error (Overloaded Gateways). Only for "re-send". The first 5 devices will always return no device errors. The remaining devices will always return a random error</div>
            <div>
                <tags:checkbox path="settings.deviceErrorOnConfigEnabled" disabled="true"/> Device Error 
                <tags:selectWithItems path="settings.deviceErrorOnConfig" items="${deviceErrors}" disabled="true"/> (Error for first
                <input type="number" min="0" value="${settings.numberOfDevicesToErrorOnConfig}" data-toggle-group="resp-device-error" disabled="true"/> devices)
            </div>
        </tags:sectionContainer>
        
        <cti:button label="Stop Simulator" href="stopDataStreamingSimulator"/>
    </c:if>
    <div class="button-group button-group-toggle">
        <div class="js-sim-startup" data-simulator-type="DATA_STREAMING">
        <cti:button id="enable-startup" nameKey="runSimulatorOnStartup.automatic" classes="yes"/>
        <cti:button id="disable-startup" nameKey="runSimulatorOnStartup.manual" classes="no"/>
        </div>  
    </div>
    <cti:includeScript link="/resources/js/pages/yukon.dev.simulators.dataStreamingSimulator.js" />
    <cti:includeScript link="/resources/js/pages/yukon.dev.simulators.simulatorStartup.js" />
</cti:standardPage>