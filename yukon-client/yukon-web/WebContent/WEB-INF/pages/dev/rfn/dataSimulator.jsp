<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="dev" page="rfnTest.viewDataSimulator">
<cti:includeScript link="/resources/js/common/yukon.ui.progressbar.js"/>
    <script>
    yukon.namespace('yukon.dev.dataSimulator');

    yukon.dev.dataSimulator = (function() {
        var _initialized = false,
        _checkStatusTime = 2500,

        _startButtonClick = function(event) {
            var formData = $('#dataSimulatorForm').serialize();
            if($(this).attr('id') === 'start-simulator') {
                    $.ajax({
                    url: yukon.url('/dev/rfn/startDataSimulator'),
                    type: 'post',
                    data: formData 
                }).done(function(data) {
                    _checkStatus(true);
                }).fail(function(data) {
                    yukon.ui.alertError("Failed to run LCR Data Simulator. Try again.");
                });
            };
            
            if($(this).attr('id') === 'stop-simulator') {
                $.ajax({
                    url: yukon.url('/dev/rfn/stopDataSimulator'),
                    type: 'GET'
                    });
                _checkStatus(true);
            };
            $(this).hide();
            $(this).siblings('button').show();
        
        },
        
        _sendMessageButtonClick = function(event) {
            
            if($(this).attr('id') === 'send-message') {
                $.ajax({
                    url: yukon.url('/dev/rfn/sendLcrDeviceMessages'),
                    type: 'GET'
                    }).done(function(data) {
                        _checkExistingDeviceStatus(true);
                    }).fail(function(data) {
                        yukon.ui.alertError("send-message failed to run LCR Data Simulator on existing devices. Try again.");
                    });
            };
              
            if($(this).attr('id') === 'stop-send-message') {
                $.ajax({
                    url: yukon.url('/dev/rfn/stopSendingLcrDeviceMessages'),
                    type: 'GET'
                    });
                _checkExistingDeviceStatus(true);
            };
            $(this).hide();
            $(this).siblings('button').show();
        },

        _checkStatus = function(extraCheck) {
            if (!extraCheck) {
                $.ajax({
                    url: yukon.url('datasimulator-status'),
                    type: 'get'
                }).done(function(data) {
                    if (data.hasError) {
                        $('#taskStatusMessage').addMessage({message:data.errorMessage, messageClass:'error'}).show();
                    } else {
                        $('#taskStatusMessage').hide();
                    }
                    if (data.running) {
                        $('#stop-simulator').show();
                        $('#start-simulator').hide();
                        $("#lcrForm :input").prop("disabled", true);
                    } else {
                        $('#stop-simulator').hide();
                        $('#start-simulator').show();
                        $("#lcrForm :input").prop("disabled", false);
                    }
                    $('#status-start-time').text(data.startTime);
                    $('#status-stop-time').text(data.stopTime);
                    $('#status-num-success').text(data.success);
                    $('#status-num-failed').text(data.failure);
            var running = "Not Running";
            if (data.running) {
            running = "Running";
            }
                    $('#status-running').text(running);
                    $('#status-last-injection-time').text(data.lastInjectionTime);
                    yukon.ui.alertError
                    if (!extraCheck) {
                        setTimeout(_checkStatus, _checkStatusTime);
                    }
                }).fail(function(data) {
                    if (!extraCheck) {
                        setTimeout(_checkStatus, _checkStatusTime);
                    }
                    var errorMsg = 'Failed trying to receive the data simulation status. Trying again in five seconds.';
                    $('#taskStatusMessage').addMessage({message:errorMsg, messageClass:'error'}).show();
                });
              }
        },
        
        _checkExistingDeviceStatus = function(extraCheck) {
            if (!extraCheck) {
                $.ajax({
                    url: yukon.url('existing-datasimulator-status'),
                    type: 'get'
                }).done(function(data) {
                    if (data.hasError) {
                        $('#existingDeviceTaskStatusMessage').addMessage({message:data.errorMessage, messageClass:'error'}).show();
                    } else {
                        $('#existingDeviceTaskStatusMessage').hide();
                    }
                    if (data.running) {
                        $('#stop-send-message').show();
                        $('#send-message').hide();
                    } else {
                        $('#stop-send-message').hide();
                        $('#send-message').show();
                    } 
                    $('#existing-status-start-time').text(data.startTime);
                    $('#existing-status-stop-time').text(data.stopTime);
                    $('#existing-status-num-success').text(data.success);
                    $('#existing-status-num-failed').text(data.failure);
            var running = "Not Running";
            if (data.running) {
            running = "Running";
            }
                    $('#existing-status-running').text(running);
                    $('#existing-status-last-injection-time').text(data.lastInjectionTime);
                    if (!extraCheck) setTimeout(_checkExistingDeviceStatus, _checkStatusTime);
                }).fail(function(data) {
                    if (!extraCheck) setTimeout(_checkExistingDeviceStatus, _checkStatusTime);
                    var errorMsg = 'Failed trying to receive the data simulation status. Trying again in five seconds.';
                    $('#existingDeviceTaskStatusMessage').addMessage({message:errorMsg, messageClass:'error'}).show();
                });
            }
          },

        mod = {
            init : function() {
                if (_initialized) return;
                $('#start-simulator, #stop-simulator').click(_startButtonClick);
                $('#send-message, #stop-send-message').click(_sendMessageButtonClick);
                _checkStatus();
                _checkExistingDeviceStatus();
                _initialized = true;
            },

        };
        return mod;
    }());

    $(function() {
        yukon.dev.dataSimulator.init();
    });
    </script>
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
    </tags:nameValueContainer2>
    </div>
    <div>
        <cti:button id="start-simulator" nameKey="startSimulator" />
        <cti:button id="stop-simulator" nameKey="stopSimulator" classes="dn"/>
    </div>
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
                </div>
                </div>
    </tags:sectionContainer>
</div>
</cti:standardPage>