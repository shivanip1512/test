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
        
        _checkStatus = function(extraCheck) {
            if (typeof extraCheck != 'undefined') {
                var btn = $('.js-primary-action');
                setTimeout(function() {
                    $.ajax({
                        url: yukon.url('rph-simulator-injection-status'),
                        type: 'get'
                    }).done(function(data) {
                        if (data.hasError) {
                            yukon.ui.unbusy(btn);
                            $('#errorMessage').addMessage({message:data.errorMessage, messageClass:'error'}).show();
                        } else {
                            $('#errorMessage').hide();
                        }
                        if (data.status != 'neverRan') {
                            if (data.status == 'running') {
                                yukon.ui.busy(btn);
                                _checkStatus(true);
                            } 
                        }
                    
                        if (data.isCompleted == true) {
                            $('#completion-task-status').show();
                            yukon.ui.unbusy(btn);
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
        <div class="column one">
            <form id='highSpeedBulkPointInjection'>
                <tags:nameValueContainer2>
                    <tags:nameValue2 argument="Device Group" label="modules.dev.setupDatabase.setupDevDatabase.generic">
                        <cti:deviceGroupHierarchyJson predicates="NON_HIDDEN" var="groupDataJson" />
                        <tags:deviceGroupNameSelector fieldName="deviceGroupName"
                            dataJson="${groupDataJson}" submitCallback="yukon.dev.highSpeedBulkPointInjection.updateDeviceGroup"/>
                    </tags:nameValue2>
                    <tags:nameValue2 argument="Type" label="modules.dev.setupDatabase.setupDevDatabase.generic">
                    <select name="type" disabled="disabled">
                        <option value="Analog Points">Analog Points</option>
                        <option value="Status Points">Status Points</option>
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
                <div class="page-action-area">
                    <cti:button id="start-button" label="Start" classes="js-primary-action" disabled="true"/>
                </div>
            </form>
        </div>
    </div>
</cti:standardPage>
