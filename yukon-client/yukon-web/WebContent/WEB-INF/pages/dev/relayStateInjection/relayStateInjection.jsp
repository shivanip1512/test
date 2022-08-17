<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="date" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:standardPage module="dev" page="relayStateInjection">
    
    <script>
        yukon.namespace('yukon.dev.relayStateInjection');
        
        yukon.dev.relayStateInjection = (function() {
            var _initialized = false;
            
            _updateStatus = function() {
                $.ajax({
                    url: yukon.url('injection-status'),
                    type: 'get'
                }).done(function(data) {
                    var injectionTime = '';
                    if (data.status === 'RUNNING') {
                        injectionTime = ' (Started: ' + data.injectionStart + ')';
                    }
                    if (data.status === 'CANCELED' || data.status === 'COMPLETE') {
                        injectionTime = ' (Last Run Finished: ' + data.injectionEnd + ')';
                    }
                    $('#js-injection-status').text('Status: ' + data.status + injectionTime);
                    if (data.status === 'NOT_RUN') {
                        _setInputsEnabled(true);
                        $('#js-progress').hide();
                        $('#cancel-button').prop('disabled', true);
                        _setStatusFieldsVisible(false);
                    } else if (data.status === 'CANCELING') {
                        _setInputsEnabled(false);
                        $('#js-progress').hide();
                        $('#cancel-button').prop('disabled', true);
                        _setStatusFieldsVisible(true);
                    } else if (data.status === 'RUNNING') {
                        _setInputsEnabled(false);
                        $('#js-progress').show();
                        $('#cancel-button').prop('disabled', false);
                        _setStatusFieldsVisible(true);
                    } else {
                        _setInputsEnabled(true);
                        $('#js-progress').hide();
                        $('#cancel-button').prop('disabled', true);
                        _setStatusFieldsVisible(true);
                    }
                    
                    $('#js-device-group').text('Device Group: ' + data.group);
                    $('#js-device-count').text('Device Count: ' + data.deviceCount);
                    
                    var afterLastReading = data.startAfterLastReading === 'true' ? ' (after last reading) ' : '';
                    
                    $('#js-time-range').text('Insert from ' + data.start + afterLastReading + ' - ' + data.stop);
                    $('#js-period').text('Insert data every ' + data.period);
                    var bar = $('#js-progress'),
                        count = data.devicesComplete,
                        total = data.deviceCount,
                        width = Number((count / total * 100).toFixed(2)),
                        percentage = yukon.percent(count, total, 2);
                    
                    bar.data('progress', width)
                       .find('.progress-bar').css({width: percentage});
                }).fail(function(data) {
                    var errorMsg = 'Failed trying to receive the point injection status.';
                    $('#js-update-status').addMessage({message:errorMsg, messageClass:'error'}).show();
                });
                setTimeout(_updateStatus, 4000);
            },
            
            _setInputsEnabled = function(enabled) {
                $('.js-input').prop('disabled', !enabled);
            },
            
            _setStatusFieldsVisible = function(enabled) {
                $('.js-status').toggle(enabled);
            },
            
            mod = {
                init : function() {
                    if (_initialized) return;
                    _updateStatus();
                    _initialized = true;
                }
            };
            return mod;
        }());
        
        $(function() {
            yukon.dev.relayStateInjection.init();
        });
    </script>
    
    <div class="column-12-12 clearfix">
        <div class="column one">
            <form:form id="relayStateInjectionForm" modelAttribute="relayStateInjectionParams" action="startInjection">
                <cti:csrfToken/>
                <tags:nameValueContainer2>
                    <tags:nameValue2 argument="Device Group" label="modules.dev.setupDatabase.setupDevDatabase.generic">
                        <cti:deviceGroupHierarchyJson predicates="NON_HIDDEN" var="groupDataJson" />
                        <tags:deviceGroupNameSelector fieldName="groupName"
                            fieldValue="${relayStateInjectionParams.groupName}"
                            dataJson="${groupDataJson}" 
                            linkGroupName="true"/>
                    </tags:nameValue2>
                    <tags:nameValue2 argument="Start After Last Reading" label="modules.dev.setupDatabase.setupDevDatabase.generic" excludeColon="true">
                        <form:checkbox path="startAfterLastReading" disabled="true" cssClass="js-input" title="Start the data insertion after any existing data. This avoids writing new data over time periods where data already exists."/>
                    </tags:nameValue2>
                    <tags:nameValue2 argument="Start" label="modules.dev.setupDatabase.setupDevDatabase.generic">
                        <date:dateTime path="start" value="${relayStateInjectionParams.start}" disabled="true" cssClass="js-input"/>
                    </tags:nameValue2>
                    <tags:nameValue2 argument="Stop" label="modules.dev.setupDatabase.setupDevDatabase.generic">
                        <date:dateTime path="stop" value="${relayStateInjectionParams.stop}" disabled="true"  cssClass="js-input"/>
                    </tags:nameValue2>
                    <tags:nameValue2 argument="Period" label="modules.dev.setupDatabase.setupDevDatabase.generic">
                        <form:input path="period" value="1h" disabled="true" cssClass="js-input" title="Inserts point data for every iteration of this period. (e.g. 12h, 1h, 15m)"/>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
                <div class="page-action-area">
                    <cti:button id="run-button" type="submit" label="Run" classes="primary action js-input" disabled="true"/>
                    <cti:button id="cancel-button" label="Cancel" classes="action" disabled="true" href="stopInjection"/>
                </div>
            </form:form>
        </div>
        <div id="taskStatusDiv" class="column two nogutter">
            <tags:sectionContainer title="Injection Status">
                <div id="js-update-status"></div>
                <div id="js-injection-status"></div>
                <div id="js-progress" class="progress">
                    <div class="progress-bar progress-bar-info progress-bar-striped active" role="progressbar" aria-valuemin="0" aria-valuemax="100" style="width: 0%"></div>
                </div>
                <div id="js-device-group" class="js-status"></div>
                <div id="js-device-count" class="js-status"></div>
                <div id="js-time-range" class="js-status"></div>
                <div id="js-period" class="js-status"></div>
            </tags:sectionContainer>
        </div>
    </div>
</cti:standardPage>
