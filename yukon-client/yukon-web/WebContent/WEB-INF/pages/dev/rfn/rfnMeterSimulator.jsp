<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="dev" page="rfnTest.viewDataSimulator">
    <cti:includeScript link="/resources/js/common/yukon.ui.progressbar.js" />
    <script>
        $(function() {
            //Script for SimulatorStartupSettings functionality
            var simType = "RFN_METER";
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
    yukon.namespace('yukon.dev.dataSimulator');

    yukon.dev.dataSimulator = (function() {
        var _initialized = false,
        _checkStatusTime = 2500,

        _sendMessageButtonClick = function(event) {
            var formData = $('#formData').serialize();
            if($(this).attr('id') === 'send-message') {
                $.ajax({
                    url: yukon.url('/dev/rfn/startMetersArchiveRequest'),
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
                    url: yukon.url('/dev/rfn/stopMetersArchieveRequest'),
                    type: 'get'
                    });
                _checkExistingDeviceStatus(true);
            };
            
            if($(this).attr('id') === 'send-test') {
                var formData = $('#formData').serialize();
                $.ajax({
                    url: yukon.url('/dev/rfn/testMeterArchiveRequest'),
                    type: 'post',
                    data: formData 
                });
            };
            
            if ($(this).attr('id') !== 'send-test') {
                $(this).hide();
                $(this).siblings('button').show();
            }
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
                    if (data.running) {
                        $('#stop-send-message').show();
                        $('#send-message').hide();
                        $("#rfnMeterForm :input").prop("disabled", true);
                    } else {
                        $('#stop-send-message').hide();
                        $('#send-message').show();
                        $("#rfnMeterForm :input").prop("disabled", false);
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
          
        mod = {
            init : function() {
                if (_initialized) return;
                $('#send-test, #send-message, #stop-send-message').click(_sendMessageButtonClick);
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
            <form id='formData'">
                <cti:csrfToken />
                <cti:msg2 key="modules.dev.rfnMeterSimulator.helpText" var="helpText"/>
                <tags:sectionContainer2 nameKey="rfnMeterSimulator">
                    <div id='rfnMeterForm'>
                        <tags:nameValueContainer2>
                            <tags:nameValue2 nameKey=".rfnMeterSimulator.rfnMeterType">
                                <tags:selectWithItems path="currentSettings.paoType"
                                    items="${paoTypes}" id="pao-type"
                                    defaultItemLabel="ALL RFN Type" defaultItemValue="ALL RFN Type" />
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".lcrDataSimulator.duplicates">
                                 <input id="percentOfDuplicates" name="percentOfDuplicates" type="text" value=${currentSettings.percentOfDuplicates} maxlength="3" size="3"> %
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".rfnMeterDataSimulator.reportingInterval">
                            <select name="reportingInterval">
                                <c:forEach var="reportingInterval" items="${rfnMeterReportingIntervals}">
                                    <c:choose>
                                        <c:when test="${selectedReportingInterval.equals(reportingInterval)}">
                                            <option value="${reportingInterval}" selected="selected"><cti:msg2 key="${reportingInterval}"/></option>
                                        </c:when>
                                        <c:otherwise>
                                            <option value="${reportingInterval}"><cti:msg2 key="${reportingInterval}"/></option>
                                        </c:otherwise>
                                    </c:choose>
                                </c:forEach>
                            </select>
                            </tags:nameValue2>
                        </tags:nameValueContainer2>
                    </div>
                    <div>
                        <br />
                        <cti:button id="send-message" nameKey="sendRfnMeterMessages" />
                        <cti:button id="stop-send-message" nameKey="stopSendingRfnMeterMessages"
                            classes="dn" />
                    </div>
                    <div class="button-group button-group-toggle">
                        <cti:button id="enable-startup" nameKey="runSimulatorOnStartup.automatic" classes="yes"/>
                        <cti:button id="disable-startup" nameKey="runSimulatorOnStartup.manual" classes="no"/>  
                    </div>
                </tags:sectionContainer2>
                <tags:sectionContainer2 nameKey="rfnMeterSimulatorTest">
                    <div id='rfnMeterForm'>
                        <tags:nameValueContainer2>
                            <tags:nameValue2 nameKey=".rfnMeterSimulator.deviceId">
                                <input id="deviceId"" name="deviceId"" type="text" value=${currentSettings.deviceId}> 
                            </tags:nameValue2>
                        </tags:nameValueContainer2>
                    </div>
                    <div>
                        <br />
                        <cti:button  id="send-test" nameKey="testMetersArchiveRequest" />
                    </div>
                </tags:sectionContainer2>
            </form>
        </div>

        <div id="taskStatusDiv" class="column two nogutter">
            <tags:sectionContainer title="Rfn Meter Simulator Status">
                <div id="taskStatusMessage"></div>
                <div>
                    <div>
                        <span id="status-running" style="font-size: 12pt; color: blue;">${dataSimulatorStatus.running}</span>
                    </div>
                    <div>
                        Start Time: <span id="status-start-time">${dataSimulatorStatus.startTime}</span>
                    </div>
                    <div>
                        Stop Time: <span id="status-stop-time">${dataSimulatorStatus.stopTime}</span>
                    </div>
                    <div>
                        Success: <span id="status-num-success" class="success">${dataSimulatorStatus.success}</span>
                    </div>
                    <div>
                        Failure: <span id="status-num-failed" class="error">${dataSimulatorStatus.failure}</span>
                    </div>
                    <div>
                        Last Injection Time: <span id="status-last-injection-time">${dataSimulatorStatus.lastInjectionTime}</span>
                    </div>
                </div>
            </tags:sectionContainer>
        </div>
</cti:standardPage>
