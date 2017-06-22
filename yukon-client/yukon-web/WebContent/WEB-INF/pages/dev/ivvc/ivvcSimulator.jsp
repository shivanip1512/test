<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="dev" page="ivvc.ivvcSimulator">
    <cti:includeScript link="/resources/js/common/yukon.ui.progressbar.js" />
    <script>
        $(function() {
            _sendMessageButtonClick = function(event) {
                var formData = $('#formData').serialize();
                if($(this).attr('id') === 'send-message') {
                    $.ajax({
                        url: yukon.url('/dev/ivvc/startIvvcSimulatorRequest'),
                        type: 'post',
                        data: formData 
                    }).done(function(data) {
                        $('#stop-send-message').show();
                        $('#send-message').hide();
                        $('#send-message').removeAttr("disabled");
                        _checkExistingDeviceStatus(true);
                    }).fail(function(data) {
                        $('#send-message').removeAttr("disabled");
                        if (data.hasError) {
                            $('#taskStatusMessage').addMessage({message:data.errorMessage, messageClass:'error'}).show();
                        } else {
                            $('#taskStatusMessage').hide();
                        }
                    });
                };
                  
                if($(this).attr('id') === 'stop-send-message') {
                    $.ajax({
                        url: yukon.url('/dev/ivvc/stopIvvcSimulatorRequest'),
                        type: 'get'
                        });
                    _checkExistingDeviceStatus(true);
                };
                
                $(this).hide();
                $(this).siblings('button').show();
            },
            
            _checkExistingDeviceStatus = function(extraCheck) {
                if (!extraCheck) {
                    $.ajax({
                        url: yukon.url('existing-ivvcSimulator-status'),
                        type: 'get'
                    }).done(function(data) {
                        if (data.hasError) {
                            $('#taskStatusMessage').addMessage({message:data.errorMessage, messageClass:'error'}).show();
                        } else {
                            $('#taskStatusMessage').hide();
                        }
                        if (data.running) {
                            $('#stop-send-message').show();
                            $('#send-message').hide();
                            $("#ivvcForm :input").prop("disabled", true);
                        } else {
                            $('#stop-send-message').hide();
                            $('#send-message').show();
                            $("#ivvcForm :input").prop("disabled", false);
                        }
                        var running = "Not Running";
                        if (data.running) {
                            running = "Running";
                        }
                        $('#status-running').text(running);
                        if (!extraCheck) setTimeout(_checkExistingDeviceStatus, _checkStatusTime);
                    }).fail(function(data) {
                        if (!extraCheck) setTimeout(_checkExistingDeviceStatus, _checkStatusTime);
                        if (data.hasError) {
                            $('#taskStatusMessage').addMessage({message:data.errorMessage, messageClass:'error'}).show();
                        } else {
                            $('#taskStatusMessage').hide();
                        }
                    });
                }
            },
            
            _updateStartup = function(event) {
                var startupData = {
                        simulatorType: simType
                    };
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
            },
             
            _checkStartupStatus = function(prevErrorMessage, startupData) {
                $.ajax({
                    url: yukon.url('/dev/ivvc/existingStartupStatus'),
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
                    yukon.ui.alertError(prevErrorMessage + " Error communicating with IvvcSimulatorController. Refresh the page to try again.");
                    $('#enable-startup').attr("disabled", "true");
                    $('#disable-startup').attr("disabled", "true");
                    $('#enable-startup').removeClass('on');
                    $('#disable-startup').removeClass('on');
                });
            },
             
            _checkStatusTime = 2500;
            simType = "IVVC";
            $('#send-test, #send-message, #stop-send-message').click(_sendMessageButtonClick);
            _checkExistingDeviceStatus();
            $('#enable-startup, #disable-startup').click(_updateStartup);
            _checkStartupStatus(null, {simulatorType: simType});
        });
    </script>
    <div class="column-12-12 clearfix">
        <div class="column one">
            <form id='formData'">
                <cti:csrfToken />
                <cti:msg2 key="modules.dev.ivvc.ivvcSimulator.helpText" var="helpText"/>
                <tags:sectionContainer2 nameKey="ivvc.ivvcSimulator">
                    <div id='ivvcForm'>
                        <tags:nameValueContainer2>
                            <tags:checkboxNameValue path="ivvcSimulatorSettings.increasedSpeedMode" nameKey=".ivvcSimulator.increasedSpeedMode"/>
                        </tags:nameValueContainer2>
                    </div>
                    <div>
                        <br/>
                        <cti:button id="send-message" nameKey="sendIvvcMessages"/>
                        <cti:button id="stop-send-message" nameKey="stopSendingIvvcMessages" classes="dn"/>
                    </div>
                    <div class="button-group button-group-toggle">
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
</cti:standardPage>
