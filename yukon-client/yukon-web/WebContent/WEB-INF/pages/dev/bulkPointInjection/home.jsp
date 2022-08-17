<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="date" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:standardPage module="dev" page="bulkPointInjection">
    <cti:includeScript link="/resources/js/common/yukon.ui.progressbar.js"/>
    <script>
    yukon.namespace('yukon.dev.bulkPointInjection');

    yukon.dev.bulkPointInjection = (function() {
        var _initialized = false,
        _checkStatusTime = 2500,

        _fullSupportCheckboxClick = function() {
            var showFullSupport = $('#require-full-support-check-box').is(':checked');
            var isEmpty;
            if (showFullSupport) {
                $('#empty-attribute').hide();
                $('#partial-support').hide();
                $('#full-support').show(50);
                isEmpty = $('#full-support').has('option').length == 0;
            } else {
                $('#empty-attribute').hide();
                $('#full-support').hide();
                $('#partial-support').show(50);
                isEmpty = $('#partial-support').has('option').length == 0;
            }
            
            if (isEmpty) {
                $('#full-support').hide();
                $('#partial-support').hide();
                _setInputsEnabled(false);
                $('#attribute-no-devices').text('No attributes available.').show(75);
            } else {
                _setInputsEnabled(true);
                $('#attribute-no-devices').hide();
            }
        },
        
        _fillAttributeSelects = function(supportedAttributes) {
            var fullSupportSelect = $('#full-support').empty();
            var partialSupportSelect = $('#partial-support').empty();

            var numFullSupport = supportedAttributes.fullSupport.length;
            if (numFullSupport > 0) {
                for (var i = 0; i < numFullSupport; i++) {
                    var attribute = supportedAttributes.fullSupport[i];
                    var attributeLabel = supportedAttributes.fullSupportDisplayable[i];
                    $("<option />", {value: attribute, text: attributeLabel}).appendTo(fullSupportSelect);
                }
            }

            var numPartialSupport = supportedAttributes.partialSupport.length;
            if (numPartialSupport > 0) {
                for (var i = 0; i < numPartialSupport; i++) {
                    var attribute = supportedAttributes.partialSupport[i];
                    var attributeLabel = supportedAttributes.partialSupportDisplayable[i];
                    $("<option />", {value: attribute, text: attributeLabel}).appendTo(partialSupportSelect);
                }
            }
            _fullSupportCheckboxClick();
        },
        
        _runButtonClick = function() {
            var formData = $('#bulkPointInjectionForm').serialize();
            $.ajax({
                url: yukon.url('run'),
                type: 'post',
                data: formData
            }).done(function(data) {
                _checkStatus(true);
            }).fail(function(data) {
                yukon.ui.alertError("Failed to run point injection. Try again.");
            });
        },
        
        _stopButtonClick = function() {
            $.post(yukon.url('stop'));
            _checkStatus(true);
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
            $.ajax({
                url: yukon.url('injection-status'),
                type: 'get'
            }).done(function(data) {
                if (data.hasError) {
                    $('#taskStatusMessage').addMessage({message:data.errorMessage, messageClass:'error'}).show();
                } else {
                    $('#taskStatusMessage').hide();
                }

                if (data.status != 'neverRan') {
                    var numRemaining = data.numTotal - data.numComplete;
                    if (parseInt($('#status-num-remaining').text()) < numRemaining) {
                        $('#status-num-complete').text(0);
                        $('#status-num-remaining').text(data.numTotal);
                    }

                    $('#status-attribute').text(data.attribute);
                    $('#status-last-injection').text(data.lastInjection);

                    var taskStatus = $('#taskStatusDiv');
                    taskStatus.find('.js-injection-never-ran').hide();
                    taskStatus.find('.js-injection-has-ran').show();
                    if (data.status == 'running') {
                        $('#status-device-group').text(data.deviceGroupName);
                        _incrementalNumberUpdater(parseInt($('#status-num-complete').text()), data.numComplete, function(value) {
                            $('#status-num-complete').text(Math.round(value));
                            $('#status-num-complete-last').text(Math.round(value));
                            $('#injection-status-progress-bar').css('width', yukon.percent(value, data.numTotal, 1));
                            $('#status-percent-complete').text(yukon.percent(value, data.numTotal, 1));
                        });
                        _incrementalNumberUpdater(parseInt($('#status-num-remaining').text()), data.numTotal - data.numComplete, function(value) {
                            $('#status-num-remaining').text(Math.round(value));
                        });
                        taskStatus.find('.js-injection-not-running').hide();
                        taskStatus.find('.js-injection-running').show();
                    } else if (data.status == 'notRunning') {
                        taskStatus.find('.js-injection-running').hide();
                        taskStatus.find('.js-injection-not-running').show();
                    }
                    if (Math.random() < .1) {
                        taskStatus.find('.js-injection-icon').hide();
                        taskStatus.find('.js-injection-alt-icon').show();
                    } else {
                        taskStatus.find('.js-injection-alt-icon').hide();
                        taskStatus.find('.js-injection-icon').show();
                    }
                }
                
                if (!extraCheck) setTimeout(_checkStatus, _checkStatusTime);
            }).fail(function(data) {
                if (!extraCheck) setTimeout(_checkStatus, _checkStatusTime);
                var errorMsg = 'Failed trying to receive the point injection status. Trying again in five seconds.';
                $('#taskStatusMessage').addMessage({message:errorMsg, messageClass:'error'}).show();
            });
          },
          _setInputsEnabled = function(enabled) {
              $('#bulkPointInjectionForm :input').not('#require-full-support-check-box').prop('disabled', !enabled);
          },

        mod = {
            init : function() {
                if (_initialized) return;
                $('#require-full-support-check-box').click(_fullSupportCheckboxClick);
                $('#run-button').click(_runButtonClick);
                $(document).on('click', '#stop-button', _stopButtonClick);
                _checkStatus();

                _initialized = true;
            },

            updateDeviceGroup : function() {
                var deviceGroupName = $("[name='deviceGroupName']").val();
                var params = {deviceGroupName : deviceGroupName}; 
                $.getJSON(yukon.url('supported-attributes'), params)
                    .done(function (response) {
                        $('#taskStatusMessage').hide();
                        if (response.error) {
                            _setInputsEnabled(false);
                            $('#attribute-no-devices').text(response.errorMsg).show();
                            $('#full-support, #partial-support, #empty-attribute').empty();
                        } else {
                            _setInputsEnabled(true);
                            $('#attribute-no-devices').hide();
                            _fillAttributeSelects(response);
                        }
                    }).fail(function() {
                        var errorMsg = 'Failed to recieve supported attributes. Try again.';
                        $('#taskStatusMessage').addMessage({message:errorMsg, messageClass:'error'}).show();
                    });
            },
        };
        return mod;
    }());

    $(function() {
        yukon.dev.bulkPointInjection.init();
    });
    </script>
    
    <a href="<cti:url value="/dev/bulkPointInjection/main"/>">Previous Bulk Point Injection tool</a>
    <div class="column-12-12 clearfix">
        <div class="column one">
            <form id='bulkPointInjectionForm'>
                <tags:nameValueContainer2>
                    <tags:nameValue2 argument="Device Group" label="modules.dev.setupDatabase.setupDevDatabase.generic">
                        <cti:deviceGroupHierarchyJson predicates="NON_HIDDEN" var="groupDataJson" />
                        <tags:deviceGroupNameSelector fieldName="deviceGroupName"
                            dataJson="${groupDataJson}" submitCallback="yukon.dev.bulkPointInjection.updateDeviceGroup" />
                    </tags:nameValue2>
                    <tags:nameValue2 excludeColon="true">
                        <label title='Only show attributes that all devices in this group support. Otherwise show attributes that any of the devices support.'>
                            <input id='require-full-support-check-box' name='fullSupport' type="checkbox" checked>All devices must support attribute</input>
                        </label>
                    </tags:nameValue2>
                    <tags:nameValue2 argument="Attribute" label="modules.dev.setupDatabase.setupDevDatabase.generic">
                        <div id="attribute-no-devices" class="error dn"></div>
                        <select name="fullAttribute" id='full-support' class='dn'></select>
                        <select name="partialAttribute" id='partial-support' class='dn'></select>
                        <input id='empty-attribute' type="text" disabled/>
                    </tags:nameValue2>
                    <tags:nameValue2 argument="Start" label="modules.dev.setupDatabase.setupDevDatabase.generic">
                        <date:dateTime name="start" value="${start}" disabled="true"/>
                    </tags:nameValue2>
                    <tags:nameValue2 argument="Stop" label="modules.dev.setupDatabase.setupDevDatabase.generic">
                        <date:dateTime name="stop" value="${stop}" disabled="true"/>
                    </tags:nameValue2>
                    <tags:nameValue2 argument="Period" label="modules.dev.setupDatabase.setupDevDatabase.generic">
                       <input id="period" name="period" type="text" value="1h" disabled title='Inserts point data for every iteration of this period. (e.g. 12h, 1h, 15m)'>
                    </tags:nameValue2>
                    <tags:nameValue2 argument="Value Low" label="modules.dev.setupDatabase.setupDevDatabase.generic">
                       <input id="valueLow" name="valueLow" type="text" value="10.0" disabled>
                    </tags:nameValue2>
                    <tags:nameValue2 argument="Value High" label="modules.dev.setupDatabase.setupDevDatabase.generic">
                       <input id="valueHigh" name="valueHigh" type="text" value="100.0" disabled>
                    </tags:nameValue2>
                    <tags:nameValue2 argument="Throttle (per second)" label="modules.dev.setupDatabase.setupDevDatabase.generic">
                       <input id="throttlePerSecond" name="throttlePerSecond" type="text" value="5000" disabled>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
                <div class="page-action-area">
                    <cti:button id="run-button" label="Run" classes="primary action" disabled="true"/>
                </div>
            </form>
        </div>
        <div id="taskStatusDiv" class="column two nogutter">
            <tags:sectionContainer title="Injection Status">
                <div id="taskStatusMessage"></div>
                <div class="js-injection-never-ran">
                    <em>The bulk point injection tool is not running and is ready to be scheduled</em>
                </div>
                <div class="js-injection-has-ran dn">
                    <div class="column-4-20 clearfix stacked">
                        <div class="column one">
                            <div class="js-injection-running dn">
                                <cti:icon icon="icon-32-emoticon-smile" classes="js-injection-icon icon-32"/>
                                <cti:icon icon="icon-32-emoticon-grin" classes="js-injection-alt-icon icon-32"/>
                            </div>
                            <div class="js-injection-not-running dn pr">
                                <cti:icon icon="icon-32-emoticon-sleeping" classes="js-injection-icon icon-32"/>
                                <cti:icon icon="icon-32-emoticon-sad" classes="js-injection-alt-icon icon-32"/>
                            </div>
                        </div>
                        <div class="column two nogutter">
                            <div class="column-8-16 clearfix">
                                <div class="column one">
                                    <h4>Attribute</h4>
                                </div>
                                <div class="column two nogutter">
                                    <h4>Device Group</h4>
                                </div>
                            </div>
                            <div class="column-8-16 clearfix">
                                <div id="status-attribute" class="column one">${injectionStatus.attribute}</div>
                                <div id="status-device-group" class="column two nogutter">${injectionStatus.deviceGroupName}</div>
                            </div>
                        </div>
                    </div>
                    <div class="js-injection-not-running dn">
                        <em>
                            Last injection had 
                            <span id="status-num-complete-last"></span> 
                            points at
                            <span id="status-last-injection"></span>
                        </em>
                    </div>
                    <div class="js-injection-running dn">
                        <div class="column-8-16 clearfix">
                            <div class="column one">
                                <c:set var="successWidth" value="${injectionStatus.numComplete / injectionStatus.numTotal * 100}"/>
                                <div class="progress active progress-striped" style="width: 100px;float:left;">
                                    <div id="injection-status-progress-bar" class="progress-bar progress-bar-success"
                                        role="progressbar" aria-valuemin="0" aria-valuemax="100" style="width: ${successWidth}%">
                                        </div>
                                </div>
                                <div id="status-percent-complete" style="float:right;">
                                    <fmt:formatNumber value="${injectionStatus.numComplete / injectionStatus.numTotal}" 
                                        type="percent" minFractionDigits="1"/>
                                </div>
                            </div>
                            <div class="column two nogutter">
                                Complete: <span id="status-num-complete" class="label label-success">
                                    ${injectionStatus.numComplete}
                                </span>
                                Remaining: <span id="status-num-remaining" class="label label-info">
                                    ${injectionStatus.numTotal - injectionStatus.numComplete}
                                </span>
                            </div>
                        </div>
                        <div class="page-action-area">
                            <cti:button id="stop-button" label="Stop" classes="action"/>
                        </div>
                    </div>
                </div>
            </tags:sectionContainer>
        </div>
    </div>
</cti:standardPage>
