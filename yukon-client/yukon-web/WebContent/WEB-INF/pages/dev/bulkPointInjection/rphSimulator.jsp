<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="date" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:standardPage module="dev" page="highSpeedBulkPointInjection">
    <script>
    yukon.namespace('yukon.dev.highSpeedBulkPointInjection');

    yukon.dev.highSpeedBulkPointInjection = (function() {
        var _initialized = false,
        _checkStatusTime = 1000,
        
        _startButtonClick = function() {
            
            var formData = $('#highSpeedBulkPointInjection').serialize();
            $.ajax({
                url: yukon.url('start'),
                type: 'post',
                data: formData
            }).done(function(data) {
                _checkStatus(true);
            }).fail(function(data) {
                yukon.ui.alertError("Failed to run point injection. Try again.");
            });
        },
        
      
        _setInputsEnabled = function(enabled) {
            $('#highSpeedBulkPointInjection :input').prop('disabled', !enabled);
        },
        
        _stopButtonClick = function() {
            $.post(yukon.url('stopRphSimulator'));
            _checkStatus(true);
        },
        
        _checkStatus = function(extraCheck) {
            if (typeof extraCheck != 'undefined') {
                var startBtn = $('.js-primary-action-start');
                var stopBtn = $('.js-primary-action-stop');
                setTimeout(function() {
                    $.ajax({
                        url: yukon.url('rph-simulator-injection-status'),
                        type: 'get'
                    }).done(function(data) {
                        if (data.hasError) {
                            yukon.ui.unbusy(startBtn);
                            $('#errorMessage').addMessage({message:data.errorMessage, messageClass:'error'}).show();
                        } else {
                            $('#errorMessage').hide();
                        }
                        if (data.status != 'neverRan') {
                            if (data.status == 'running') {
                                $('#completion-task-status').hide();
                                $('#cancel-task-status').hide();
                                yukon.ui.busy(startBtn);
                                
                                _checkStatus(true);
                            } 
                        }
                        
                        if (data.isCanceled == true) {
                            yukon.ui.busy(stopBtn);
                        }
                        
                        if (data.isCompleted == true) {
                           
                            yukon.ui.unbusy(startBtn);
                            if (data.isCanceled == true ) {
                                $('#cancel-task-status').show();
                                yukon.ui.unbusy(stopBtn);
                            } else {
                                $('#completion-task-status').show();
                            }
                            clearTimeout(_checkStatusTime);
                        }
                        
                    }).fail(function(data) {
                        var errorMsg = 'Failed trying to receive the point injection status. Trying again in five seconds.';
                        $('#highSpeedBulkPointInjection').addMessage({message:errorMsg, messageClass:'error'}).show();
                    });
                }, _checkStatusTime);
            }
        },
        
        
        mod = {
            init : function() {
                if (_initialized) return;
                $('#start-button').click(_startButtonClick);
                $(document).on('click', '#stop-button', _stopButtonClick);
                _checkStatus();
                _initialized = true;
            },
            
            updateDeviceGroup : function() {
                _setInputsEnabled(true);
            },

        };
        return mod;
    }());

    $(function() {
        yukon.dev.highSpeedBulkPointInjection.init();
    });
    </script>
    
    <div class="column-12-12 clearfix">
       <div id="errorMessage"></div>
       <div id="completion-task-status" class="user-message success" hidden="true"> 
           <em>Points insertion completed</em>
       </div>
       <div id="cancel-task-status" class="user-message success" hidden="true"> 
           <em>Points insertion cancelled by user</em>
       </div>
        <div class="column one">
            <form id='highSpeedBulkPointInjection'>
            <cti:msg2 key="modules.dev.highSpeedBulkPointInjection.helpText" var="helpText"/>
            <tags:sectionContainer title="Settings" helpText="${helpText}" >
                <tags:nameValueContainer2>
                    <tags:nameValue2 argument="Device Group" label="modules.dev.setupDatabase.setupDevDatabase.generic">
                        <cti:deviceGroupHierarchyJson predicates="NON_HIDDEN" var="groupDataJson" />
                        <tags:deviceGroupNameSelector fieldName="deviceGroupName"
                            dataJson="${groupDataJson}" submitCallback="yukon.dev.highSpeedBulkPointInjection.updateDeviceGroup"/>
                    </tags:nameValue2>
                    <tags:nameValue2 argument="Type" label="modules.dev.setupDatabase.setupDevDatabase.generic">
                    <select name="type" disabled="disabled">
                        <option value="ANALOG">ANALOG</option>
                        <option value="STATUS">STATUS</option>
                        <option value="KWH">KWH</option>
                    </select>
                    </tags:nameValue2>
                    <tags:nameValue2 argument="Start" label="modules.dev.setupDatabase.setupDevDatabase.generic">
                        <date:dateTime name="start" value="${start}" disabled="true"/>
                    </tags:nameValue2>
                    <tags:nameValue2 argument="Stop" label="modules.dev.setupDatabase.setupDevDatabase.generic">
                        <date:dateTime name="stop" value="${stop}" disabled="true" />
                    </tags:nameValue2>
                    <tags:nameValue2 argument="Period" label="modules.dev.setupDatabase.setupDevDatabase.generic">
                       <input id="period" name="period" type="text" value="1h"  disabled title='Inserts point data for every iteration of this period. (e.g. 1s 1m 1h)'>
                    </tags:nameValue2>
                    <tags:nameValue2 argument="Value Low" label="modules.dev.setupDatabase.setupDevDatabase.generic">
                       <input id="valueLow" name="valueLow" type="text" value="10.0" disabled>
                    </tags:nameValue2>
                    <tags:nameValue2 argument="Value High" label="modules.dev.setupDatabase.setupDevDatabase.generic">
                       <input id="valueHigh" name="valueHigh" type="text" value="100.0" disabled>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </tags:sectionContainer>
                <div class="page-action-area">
                    <cti:button id="start-button" label="Start" classes="js-primary-action-start" disabled="true"/>
                    <cti:button id="stop-button" label="Stop" classes="js-primary-action-stop" disabled="true" />
                </div>
            </form>
        </div>
    </div>
</cti:standardPage>
