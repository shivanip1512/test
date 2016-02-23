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

        _sendMessageButtonClick = function(event) {
           var paoTypeSelected = document.getElementById("pao-type").value;
            if($(this).attr('id') === 'send-message') {
                $.ajax({
                    url: yukon.url('/dev/rfn/startMetersArchieveRequest?paoTypeSelected='+paoTypeSelected),
                    type: 'GET'
                    }).done(function(data) {
                        _checkExistingDeviceStatus(true);
                    }).fail(function(data) {
                        yukon.ui.alertError("send-message failed to run LCR Data Simulator on existing devices. Try again.");
                    });
            };
              
            if($(this).attr('id') === 'stop-send-message') {
                $.ajax({
                    url: yukon.url('/dev/rfn/stopMetersArchieveRequest'),
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
                    url: yukon.url('existing-rfnMetersimulator-status'),
                    type: 'get'
                }).done(function(data) {
                    if (data.hasError) {
                        $('#taskStatusMessage').addMessage({message:data.errorMessage, messageClass:'error'}).show();
                    } else {
                        $('#taskStatusMessage').hide();
                    }
                    if (data.statusRfnBillingType == 'running') {
                        $('#stop-send-message').show();
                        $('#send-message').hide();
                        $('#status-last-simulator-billingType').text(data.lastSimulationRfnBillingType);
                        $('#messages-sent-per-minute').text(data.perMinuteMsgCount);
                        $('#message-sent-counter').show();
                    } else if (data.statusRfnBillingType == 'notRunning') {
                        $('#stop-send-message').hide();
                        $('#send-message').show();
                        $('#messages-sent-per-minute').text(data.perMinuteMsgCount);
                    } 
                    if (data.statusRfnBillingType != 'neverRan') {
                        var numRemainingRfnBillingType = data.numTotalRfnMeters - data.numCompleteRfnBillingType;
                        if (parseInt($('#status-num-remaining').text()) < numRemainingRfnBillingType) {
                            $('#status-num-complete').text(0);
                            $('#status-num-remaining').text(data.numTotalRfnMeters);
                        }
                        $('#status-last-simulator-billingType').text(data.lastSimulationRfnBillingType);
                        var taskStatus = $('#taskStatusDiv');
                        taskStatus.find('.js-simulator-never-ran').hide();
                        taskStatus.find('.js-simulator-has-ran').show();
                        if (data.statusRfnBillingType == 'running') {

                            _incrementalNumberUpdater(parseInt($('#status-num-complete').text()), data.numCompleteRfnBillingType, function(value) {
                                $('#status-num-complete').text(Math.round(value));
                                $('#status-num-complete-last-billing').text(Math.round(value));
                                $('#simulator-status-progress-bar').css('width', yukon.percent(value, data.numTotalRfnMeters, 1));
                                $('#status-percent-complete').text(yukon.percent(value, data.numTotalRfnMeters, 1));
                            });
                            _incrementalNumberUpdater(parseInt($('#status-num-remaining').text()), data.numTotalRfnMeters - data.numCompleteRfnBillingType, function(value) {
                                $('#status-num-remaining').text(Math.round(value));
                            });
                            taskStatus.find('.js-simulator-not-running').hide();
                            taskStatus.find('.js-simulator-running').show();
                            $('#message-sent-counter').show();
                            $('#running-status-last-simulator-billingType').text(data.lastSimulationRfnBillingType);
                        } else if (data.statusRfnBillingType == 'notRunning') {
                            taskStatus.find('.js-simulator-running').hide();
                            taskStatus.find('.js-simulator-not-running').show();
                        }
                        
                    }

                    if (!extraCheck) setTimeout(_checkExistingDeviceStatus, _checkStatusTime);
                }).fail(function(data) {
                    if (!extraCheck) setTimeout(_checkExistingDeviceStatus, _checkStatusTime);
                    var errorMsg = 'Failed trying to receive the rfn meter data simulation status. Trying again in five seconds.';
                    $('#taskStatusMessage').addMessage({message:errorMsg, messageClass:'error'}).show();
                });
            }
          },

        mod = {
            init : function() {
                if (_initialized) return;
                $('#send-message, #stop-send-message').click(_sendMessageButtonClick);
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
<form id='rfnMeterSimulator' commandName="rfnMeterSimulatorStatus">
<cti:csrfToken />
<tags:sectionContainer2 nameKey="rfnMeterSimulator">
	<div id='rfnMeterForm'>
    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".rfnMeterSimulator.paoType">
         <tags:selectWithItems path="rfnMeterSimulatorStatus.paoType" items="${paoTypes}" 
               id="pao-type" defaultItemLabel="ALL RFN Type" defaultItemValue="ALL RFN Type"/>
         </tags:nameValue2>
    </tags:nameValueContainer2>
    </div>
    <div><br/>
        <cti:button id="send-message" nameKey="sendRfnMeterMessages"/>
        <cti:button id="stop-send-message" nameKey="stopSendingRfnMeterMessages" classes="dn"/>
    </div>
</tags:sectionContainer2>
</form>
</div>

<div id="taskStatusDiv" class="column two nogutter">
    <tags:sectionContainer title="Rfn Meter Simulator Status">
     <div id="taskStatusMessage"></div>
                <div class="js-simulator-never-ran">
                    <em>The Meter Simulator tool is not running and is ready to be scheduled</em>
                </div>
              
                    <div class="js-simulator-not-running dn">
                        <em>
                            Last simulator had 
                            <span id="status-num-complete-last-billing"></span> 
                            RFN Meter for Billing Reading Type at
                            <span id="status-last-simulator-billingType"></span>
                        </em>
                    </div>
                    <div class="js-simulator-running dn">
                        <div class="column-8-16 clearfix">
                            <div class="column one">
                                <c:set var="successWidth" value="${existingDataSimulatorStatus.numCompleteRfnBillingType / existingDataSimulatorStatus.numTotalRfnMeters * 100}"/>
                                <div class="progress active progress-striped" style="width: 100px;float:left;">
                                    <div id="simulator-status-progress-bar" class="progress-bar progress-bar-success"
                                        role="progressbar" aria-valuemin="0" aria-valuemax="100" style="width: ${successWidth}%">
                                        </div>
                                </div>
                                <div id="status-percent-complete" style="float:right;">
                                    <fmt:formatNumber value="${existingDataSimulatorStatus.numCompleteRfnBillingType / existingDataSimulatorStatus.numTotalRfnMeters}" 
                                        type="percent" minFractionDigits="1"/>
                                </div>
                            </div>
                            <div class="column two nogutter">
                                Complete: <span id="status-num-complete" class="label label-success">
                                    ${existingDataSimulatorStatus.numCompleteRfnBillingType}
                                </span>
                                Remaining: <span id="status-num-remaining" class="label label-info">
                                    ${existingDataSimulatorStatus.numTotalRfnMeters - dataSimulatorStatus.numCompleteRfnBillingType}
                                </span>
                            </div>
                        </div>
                        <div>
                            <em>
                                Last message for RFN Meter Reading Type - Billing, Sent at
                                <span id="running-status-last-simulator-billingType"></span>
                            </em>
                        </div>
                    </div></tags:sectionContainer>
                </div>
    
    <div id="existingDeviceTaskStatusDiv" class="column two nogutter">
    
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

