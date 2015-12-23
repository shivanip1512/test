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
        
        _incrementalNumberUpdater = function (currentValue, targetValue, callback) {
            var numIterations = Math.round((_checkStatusTime) / 125) - 1;
            
            var interval = setInterval(function() {
                var value = currentValue;
                var iteration = 0;
                var delta = (targetValue - currentValue) / numIterations;
                return function() {
                    iteration++;
                    value += delta;
                    if (iteration < numIterations) {
                        callback(value);
                    } else {
                        clearInterval(interval);
                        callback(targetValue);
                    }
                };
            }(), _checkStatusTime / numIterations);
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
                    if (data.status6200 == 'running' || data.status6600 == 'running') {
                        $('#stop-simulator').show();
                        $('#start-simulator').hide();
                        $('#status-last-simulator-6200').text(data.lastSimulation6200);
                        $('#status-last-simulator-6600').text(data.lastSimulation6600);
                        $('#messages-sent-per-minute').text(data.perMinuteMsgCount);
                        $('#message-sent-counter').show();
                        $("#lcrForm :input").prop("disabled", true);
                    } else if (data.status6200 == 'notRunning' || data.status6600 == 'notRunning') {
                        $('#messages-sent-per-minute').text(data.perMinuteMsgCount);
                        $('#stop-simulator').hide();
                        $('#start-simulator').show();
                        $("#lcrForm :input").prop("disabled", false);
                    } else {
                        // Never Ran
                        $('#message-sent-counter').hide();
                    }
                    if (data.status6200 != 'neverRan') {
                        var numRemaining6200 = data.numTotal6200 - data.numComplete6200;
                        if (parseInt($('#status-num-remaining').text()) < numRemaining6200) {
                            $('#status-num-complete').text(0);
                            $('#status-num-remaining').text(data.numTotal6200); 
                        }
                        $('#status-last-simulator-6200').text(data.lastSimulation6200);
                        var taskStatus = $('#taskStatusDiv');
                        taskStatus.find('.js-simulator-never-ran').hide();
                        taskStatus.find('.js-simulator-has-ran').show();
                        if (data.status6200 == 'running') {
                            _incrementalNumberUpdater(parseInt($('#status-num-complete').text()), data.numComplete6200, function(value) {
                                $('#status-num-complete').text(Math.round(value));
                                $('#status-num-complete-last').text(Math.round(value));
                                $('#simulator-status-progress-bar').css('width', yukon.percent(value, data.numTotal6200, 1));
                                $('#status-percent-complete').text(yukon.percent(value, data.numTotal6200, 1));
                            });
                            _incrementalNumberUpdater(parseInt($('#status-num-remaining').text()), data.numTotal6200 - data.numComplete6200, function(value) {
                                $('#status-num-remaining').text(Math.round(value));
                            });
                            taskStatus.find('.js-simulator-not-running').hide();
                            taskStatus.find('.js-simulator-running').show();
                            $('#message-sent-counter').show();
                            $('#running-status-last-simulator-6200').text(data.lastSimulation6200);
                        } else if (data.status6200 == 'notRunning') {
                            taskStatus.find('.js-simulator-running').hide();
                            taskStatus.find('.js-simulator-not-running').show();
                        }
                        
                    }
                    //6600
                    if (data.status6600 != 'neverRan') {
                        var numRemaining6600 = data.numTotal6600 - data.numComplete6600;
                        if (parseInt($('#status-num-remaining-6600').text()) < numRemaining6600) {
                            $('#status-num-complete-6600').text(0);
                            $('#status-num-remaining-6600').text(data.numTotal6600);
                        }
                        $('#status-last-simulator-6600').text(data.lastSimulation6600);
                        var taskStatus = $('#taskStatusDiv');
                        taskStatus.find('.js-simulator-never-ran').hide();
                        taskStatus.find('.js-simulator-has-ran').show();
                        if (data.status6600 == 'running') {
                           
                            _incrementalNumberUpdater(parseInt($('#status-num-complete-6600').text()), data.numComplete6600, function(value) {
                                $('#status-num-complete-6600').text(Math.round(value));
                                $('#status-num-complete-last-6600').text(Math.round(value));
                                $('#simulator-status-progress-bar-6600').css('width', yukon.percent(value, data.numTotal6600, 1));
                                $('#status-percent-complete-6600').text(yukon.percent(value, data.numTotal6600, 1));
                            });
                            _incrementalNumberUpdater(parseInt($('#status-num-remaining-6600').text()), data.numTotal6600 - data.numComplete6600, function(value) {
                                $('#status-num-remaining-6600').text(Math.round(value));
                            });
                            taskStatus.find('.js-simulator-not-running').hide();
                            taskStatus.find('.js-simulator-running').show();
                            $('#message-sent-counter').show();
                            $('#running-status-last-simulator-6600').text(data.lastSimulation6600);
                        } else if (data.status6600 == 'notRunning') {
                            taskStatus.find('.js-simulator-running').hide();
                            taskStatus.find('.js-simulator-not-running').show();
                        }
                        
                    }
                    //End
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
                    if (data.status6200 == 'running' || data.status6600 == 'running') {
                        $('#stop-send-message').show();
                        $('#send-message').hide();
                        $('#existing-device-status-last-simulator-6200').text(data.lastSimulation6200);
                        $('#existing-device-status-last-simulator-6600').text(data.lastSimulation6600);
                        $('#messages-sent-per-minute').text(data.perMinuteMsgCount);
                        $('#message-sent-counter').show();
                    } else if (data.status6200 == 'notRunning' || data.status6600 == 'notRunning') {
                        $('#stop-send-message').hide();
                        $('#send-message').show();
                        $('#messages-sent-per-minute').text(data.perMinuteMsgCount);
                    } 
                    if (data.status6200 != 'neverRan') {
                        var numRemaining6200 = data.numTotal6200 - data.numComplete6200;
                        if (parseInt($('#existing-device-status-num-remaining').text()) < numRemaining6200) {
                            $('#existing-device-status-num-complete').text(0);
                            $('#existing-device-status-num-remaining').text(data.numTotal6200);
                        }
                        $('#existing-device-status-last-simulator-6200').text(data.lastSimulation6200);
                        var taskStatus = $('#existingDeviceTaskStatusDiv');
                        taskStatus.find('.js-simulator-never-ran').hide();
                        taskStatus.find('.js-simulator-has-ran').show();
                        if (data.status6200 == 'running') {

                            _incrementalNumberUpdater(parseInt($('#existing-device-status-num-complete').text()), data.numComplete6200, function(value) {
                                $('#existing-device-status-num-complete').text(Math.round(value));
                                $('#existing-device-status-num-complete-last').text(Math.round(value));
                                $('#existing-device-simulator-status-progress-bar').css('width', yukon.percent(value, data.numTotal6200, 1));
                                $('#existing-device-status-percent-complete').text(yukon.percent(value, data.numTotal6200, 1));
                            });
                            _incrementalNumberUpdater(parseInt($('#existing-device-status-num-remaining').text()), data.numTotal6200 - data.numComplete6200, function(value) {
                                $('#existing-device-status-num-remaining').text(Math.round(value));
                            });
                            taskStatus.find('.js-simulator-not-running').hide();
                            taskStatus.find('.js-simulator-running').show();
                            $('#message-sent-counter').show();
                            $('#running-existing-device-status-last-simulator-6200').text(data.lastSimulation6200);
                        } else if (data.status6200 == 'notRunning') {
                            taskStatus.find('.js-simulator-running').hide();
                            taskStatus.find('.js-simulator-not-running').show();
                        }
                        
                    }
                    //6600
                    if (data.status6600 != 'neverRan') {
                        var numRemaining6600 = data.numTotal6600 - data.numComplete6600;
                        if (parseInt($('#existing-device-status-num-remaining-6600').text()) < numRemaining6600) {
                            $('#existing-device-status-num-complete-6600').text(0);
                            $('#existing-device-status-num-remaining-6600').text(data.numTotal6600);
                        }
                        $('#existing-device-status-last-simulator-6600').text(data.lastSimulation6600);
                        var taskStatus = $('#existingDeviceTaskStatusDiv');
                        taskStatus.find('.js-simulator-never-ran').hide();
                        taskStatus.find('.js-simulator-has-ran').show();
                        if (data.status6600 == 'running') {
                           
                            _incrementalNumberUpdater(parseInt($('#existing-device-status-num-complete-6600').text()), data.numComplete6600, function(value) {
                                $('#existing-device-status-num-complete-6600').text(Math.round(value));
                                $('#existing-device-status-num-complete-last-6600').text(Math.round(value));
                                $('#existing-device-simulator-status-progress-bar-6600').css('width', yukon.percent(value, data.numTotal6600, 1));
                                $('#existing-device-status-percent-complete-6600').text(yukon.percent(value, data.numTotal6600, 1));
                            });
                            _incrementalNumberUpdater(parseInt($('#existing-device-status-num-remaining-6600').text()), data.numTotal6600 - data.numComplete6600, function(value) {
                                $('#existing-device-status-num-remaining-6600').text(Math.round(value));
                            });
                            taskStatus.find('.js-simulator-not-running').hide();
                            taskStatus.find('.js-simulator-running').show();
                            $('#message-sent-counter').show();
                            $('#running-existing-device-status-last-simulator-6600').text(data.lastSimulation6600);
                            
                        } else if (data.status6600 == 'notRunning') {
                            taskStatus.find('.js-simulator-running').hide();
                            taskStatus.find('.js-simulator-not-running').show();
                        }
                        
                    }
                    //End
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
        <tags:nameValue2 nameKey=".lcrDataSimulator.messageId">
            <input id="messageId" name="messageId" type="text" value=${currentSettings.messageId}>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".lcrDataSimulator.messageIdTimestamp">
            <input id="messageIdTimestamp" name="messageIdTimestamp" type="text" value=${currentSettings.messageIdTimestamp}>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".lcrDataSimulator.daysBehind">
            <input id="daysBehind" name="daysBehind" type="text" value=${currentSettings.daysBehind}>
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
    <tags:sectionContainer title="Data Simulator Status">
     <div id="taskStatusMessage"></div>
                <div class="js-simulator-never-ran">
                    <em>The Data Simulator tool is not running and is ready to be scheduled</em>
                </div>
              
                    <div class="js-simulator-not-running dn">
                        <em>
                            Last simulator had 
                            <span id="status-num-complete-last"></span> 
                            RFN LCR 6200 at
                            <span id="status-last-simulator-6200"></span>
                        </em>
                    </div>
                    <div class="js-simulator-running dn">
                        <div class="column-8-16 clearfix">
                            <div class="column one">
                                <c:set var="successWidth" value="${dataSimulatorStatus.numComplete6200 / dataSimulatorStatus.numTotal6200 * 100}"/>
                                <div class="progress active progress-striped" style="width: 100px;float:left;">
                                    <div id="simulator-status-progress-bar" class="progress-bar progress-bar-success"
                                        role="progressbar" aria-valuemin="0" aria-valuemax="100" style="width: ${successWidth}%">
                                        </div>
                                </div>
                                <div id="status-percent-complete" style="float:right;">
                                    <fmt:formatNumber value="${dataSimulatorStatus.numComplete6200 / dataSimulatorStatus.numTotal6200}" 
                                        type="percent" minFractionDigits="1"/>
                                </div>
                            </div>
                            <div class="column two nogutter">
                                Complete: <span id="status-num-complete" class="label label-success">
                                    ${dataSimulatorStatus.numComplete6200}
                                </span>
                                Remaining: <span id="status-num-remaining" class="label label-info">
                                    ${dataSimulatorStatus.numTotal6200 - dataSimulatorStatus.numComplete6200}
                                </span>
                            </div>
                        </div>
                        <div>
                            <em>
                                Last message for RFN LCR 6200 Sent at
                                <span id="running-status-last-simulator-6200"></span>
                            </em>
                        </div>
                    </div>
                    <!-- 6600 -->
                    <div class="js-simulator-not-running dn">
                        <em>
                            Last simulator had 
                            <span id="status-num-complete-last-6600"></span> 
                            RFN LCR 6600 at
                            <span id="status-last-simulator-6600"></span>
                        </em>
                    </div>
                    <div class="js-simulator-running dn">
                        <div class="column-8-16 clearfix">
                            <div class="column one">
                                <c:set var="successWidth" value="${dataSimulatorStatus.numComplete6600 / dataSimulatorStatus.numTotal6600 * 100}"/>
                                <div class="progress active progress-striped" style="width: 100px;float:left;">
                                    <div id="simulator-status-progress-bar-6600" class="progress-bar progress-bar-success"
                                        role="progressbar" aria-valuemin="0" aria-valuemax="100" style="width: ${successWidth}%">
                                        </div>
                                </div>
                                <div id="status-percent-complete-6600" style="float:right;">
                                    <fmt:formatNumber value="${dataSimulatorStatus.numComplete6600 / dataSimulatorStatus.numTotal6600}" 
                                        type="percent" minFractionDigits="1"/>
                                </div>
                            </div>
                            <div class="column two nogutter">
                                Complete: <span id="status-num-complete-6600" class="label label-success">
                                    ${dataSimulatorStatus.numComplete6600}
                                </span>
                                Remaining: <span id="status-num-remaining-6600" class="label label-info">
                                    ${dataSimulatorStatus.numTotal6600 - dataSimulatorStatus.numComplete6600}
                                </span>
                            </div>
                        </div>
                        <div>
                            <em>
                                Last message for RFN LCR 6600 Sent at
                               <span id="running-status-last-simulator-6600"></span>
                            </em>
                        </div>
                    </div>
                    <!-- End -->
                </div>
    </tags:sectionContainer>
    <div id="existingDeviceTaskStatusDiv" class="column two nogutter">
    <tags:sectionContainer title="Existing Data Simulator Status">
     <div id="existingDeviceTaskStatusMessage"></div>
                <div class="js-simulator-never-ran">
                    <em>The Data Simulator tool for existing devices is not running and is ready to be scheduled</em>
                </div>
              
                    <div class="js-simulator-not-running dn">
                        <em>
                            Last simulator had 
                            <span id="existing-device-status-num-complete-last"></span> 
                            RFN LCR 6200 at
                            <span id="existing-device-status-last-simulator-6200"></span>
                        </em>
                    </div>
                    <div class="js-simulator-running dn">
                        <div class="column-8-16 clearfix">
                            <div class="column one">
                                <c:set var="successWidth" value="${existingDataSimulatorStatus.numComplete6200 / existingDataSimulatorStatus.numTotal6200 * 100}"/>
                                <div class="progress active progress-striped" style="width: 100px;float:left;">
                                    <div id="existing-device-simulator-status-progress-bar" class="progress-bar progress-bar-success"
                                        role="progressbar" aria-valuemin="0" aria-valuemax="100" style="width: ${successWidth}%">
                                        </div>
                                </div>
                                <div id="existing-device-status-percent-complete" style="float:right;">
                                    <fmt:formatNumber value="${existingDataSimulatorStatus.numComplete6200 / existingDataSimulatorStatus.numTotal6200}" 
                                        type="percent" minFractionDigits="1"/>
                                </div>
                            </div>
                            <div class="column two nogutter">
                                Complete: <span id="existing-device-status-num-complete" class="label label-success">
                                    ${existingDataSimulatorStatus.numComplete6200}
                                </span>
                                Remaining: <span id="existing-device-status-num-remaining" class="label label-info">
                                    ${existingDataSimulatorStatus.numTotal6200 - existingDataSimulatorStatus.numComplete6200}
                                </span>
                            </div>
                        </div>
                        <div>
                            <em>
                                Last message for RFN LCR 6200 Sent at
                                <span id="running-existing-device-status-last-simulator-6200"></span>
                            </em>
                        </div>
                    </div>
                    <!-- 6600 -->
                    <div class="js-simulator-not-running dn">
                        <em>
                            Last simulator had 
                            <span id="existing-device-status-num-complete-last-6600"></span> 
                            RFN LCR 6600 at
                            <span id="existing-device-status-last-simulator-6600"></span>
                        </em>
                    </div>
                    <div class="js-simulator-running dn">
                        <div class="column-8-16 clearfix">
                            <div class="column one">
                                <c:set var="successWidth" value="${existingDataSimulatorStatus.numComplete6600 / existingDataSimulatorStatus.numTotal6600 * 100}"/>
                                <div class="progress active progress-striped" style="width: 100px;float:left;">
                                    <div id="existing-device-simulator-status-progress-bar-6600" class="progress-bar progress-bar-success"
                                        role="progressbar" aria-valuemin="0" aria-valuemax="100" style="width: ${successWidth}%">
                                        </div>
                                </div>
                                <div id="existing-device-status-percent-complete-6600" style="float:right;">
                                    <fmt:formatNumber value="${existingDataSimulatorStatus.numComplete6600 / existingDataSimulatorStatus.numTotal6600}" 
                                        type="percent" minFractionDigits="1"/>
                                </div>
                            </div>
                            <div class="column two nogutter">
                                Complete: <span id="existing-device-status-num-complete-6600" class="label label-success">
                                    ${existingDataSimulatorStatus.numComplete6600}
                                </span>
                                Remaining: <span id="existing-device-status-num-remaining-6600" class="label label-info">
                                    ${existingDataSimulatorStatus.numTotal6600 - existingDataSimulatorStatus.numComplete6600}
                                </span>
                            </div>
                        </div>
                        <div>
                            <em>
                                Last message for RFN LCR 6600 Sent at
                                <span id="running-existing-device-status-last-simulator-6600"></span>
                            </em>
                        </div>
                    </div>
                    <!-- End -->
    </tags:sectionContainer>
    <div class="column two nogutter">
    <tags:sectionContainer title="Simulator Statistics">
         <div id="message-sent-counter" >
           <em>
                Messages sent this minute
                <span id="messages-sent-per-minute"></span>
           </em>
        </div>
    </tags:sectionContainer>
    </div>
</div>
</cti:standardPage>
