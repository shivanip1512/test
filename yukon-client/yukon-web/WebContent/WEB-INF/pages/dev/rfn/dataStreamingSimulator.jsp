<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="dev" page="rfnTest.viewDataStreamingSimulator">
    <script>
        $(function() {
            //Script for SimulatorStartupSettings functionality
            var simType = "DATA_STREAMING";
            $('#enable-startup, #disable-startup').click(_updateStartup);
            _checkStartupStatus(null, {simulatorType: simType});
        
            function _updateStartup(event) {
                var startupData = {simulatorType: simType};
                if ($(this).attr('id') === 'enable-startup') {
                    startupData.runOnStartup = true;
                    $.ajax({
                        url: yukon.url('/dev/rfn/updateStartup'),
                        type: 'post',
                        data: startupData
                    }).done(function(data) {
                        if (data.hasError) {
                            _checkStartupStatus(data.errorMessage, startupData);
                        } else {
                            yukon.ui.removeAlerts();
                        }
                    }).fail(function() {
                        _checkStartupStatus("The simulator startup settings update request to the controller failed.", startupData);
                    });
                } else if ($(this).attr('id') === 'disable-startup') {
                    startupData.runOnStartup = false;
                    $.ajax({
                        url: yukon.url('/dev/rfn/updateStartup'),
                        type: 'post',
                        data: startupData
                    }).done(function(data) {
                        if (data.hasError) {
                            _checkStartupStatus(data.errorMessage, startupData);
                        } else {
                            yukon.ui.removeAlerts();
                        }
                    }).fail(function() {
                        _checkStartupStatus("The simulator startup settings update request to the controller failed.", startupData);
                    });
                }
            }
             
            function _checkStartupStatus(prevErrorMessage, startupData) {
                $.ajax({
                    url: yukon.url('/dev/rfn/existingStartupStatus'),
                    type: 'post',
                    data: startupData
                }).done(function(data) {
                    if (data.hasError) {
                        yukon.ui.alertError(prevErrorMessage + " " + data.errorMessage + " Refresh the page to try again.");
                        $('#enable-startup').attr("disabled", "true");
                        $('#disable-startup').attr("disabled", "true");
                        $('#enable-startup').removeClass('on');
                        $('#disable-startup').removeClass('on');
                    } else {
                        if (prevErrorMessage) {
                            yukon.ui.alertError(prevErrorMessage);
                        }
                        if (data.runOnStartup) {
                            $('#enable-startup').addClass('on');
                            $('#disable-startup').removeClass('on');
                        } else {
                            $('#enable-startup').removeClass('on');
                            $('#disable-startup').addClass('on');
                        }
                    }
                }).fail(function() {
                    yukon.ui.alertError(prevErrorMessage + " Error communicating with NmIntegrationController. Refresh the page to try again.");
                    $('#enable-startup').attr("disabled", "true");
                    $('#disable-startup').attr("disabled", "true");
                    $('#enable-startup').removeClass('on');
                    $('#disable-startup').removeClass('on');
                });
            }
        });
    </script>
    <script>
        $(function() {
            //handle enabling and disabling of Device Error fields for verification
            $("#verification-checkbox").attr("data-toggle", "resp-device-error");
            $("#verification-select").attr("data-toggle-group", "resp-device-error");
            $("#verification-number").attr("data-toggle-group", "resp-device-error");
            if ($("#verification-checkbox")[0].hasAttribute("checked")) {
                $("#verification-select").removeAttr("disabled");
                $("#verification-number").removeAttr("disabled");
            } else {
                $("#verification-select").attr("disabled", "true");
                $("#verification-number").attr("disabled", "true");
            }
            
            //handle enabling and disabling of Device Error fields for config
            $("#config-checkbox").attr("data-toggle", "conf-device-error");
            $("#config-select").attr("data-toggle-group", "conf-device-error");
            $("#config-number").attr("data-toggle-group", "conf-device-error");
            if ($("#config-checkbox")[0].hasAttribute("checked")) {
                $("#config-select").removeAttr("disabled");
                $("#config-number").removeAttr("disabled");
            } else {
                $("#config-select").attr("disabled", "true");
                $("#config-number").attr("disabled", "true");
            }
        });
    </script>
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
        <cti:button id="enable-startup" nameKey="runSimulatorOnStartup.automatic" classes="yes"/>
        <cti:button id="disable-startup" nameKey="runSimulatorOnStartup.manual" classes="no"/>  
    </div>
</cti:standardPage>