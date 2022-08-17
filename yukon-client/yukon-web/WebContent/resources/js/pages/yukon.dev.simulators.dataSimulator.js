yukon.namespace('yukon.dev.simulators.dataSimulator');

/**
 * Module handling dataSimulator start, stop, and status messaging
 * @module yukon.dev.simulators.dataSimulator
 * @requires JQUERY
 * @requires yukon
 */

yukon.dev.simulators.dataSimulator = ( function() {
    
    'use strict';

    var 
    _initialized = false,
    _checkStatusTime = 2500,

    _startButtonClick = function(event) {
        var formData = $('#dataSimulatorForm').serialize();
        if ($(this).attr('id') === 'start-simulator') {
            $(this).attr("disabled", "disabled");
            $.ajax({
                url: yukon.url('/dev/rfn/startDataSimulator'),
                type: 'post',
                data: formData 
            }).done( function(data) {
                $('#stop-simulator').show();
                $('#start-simulator').hide();
                $('#start-simulator').removeAttr("disabled");
                _checkStatus(true);
            }).fail( function(data) {
                $('#start-simulator').removeAttr("disabled");
                if (data.hasError) {
                    $('#taskStatusMessage').addMessage({message:data.errorMessage, messageClass:'error'}).show();
                } else {
                    $('#taskStatusMessage').hide();
                }
            });
        }
        
        if($(this).attr('id') === 'stop-simulator') {
            $.ajax({
                url: yukon.url('/dev/rfn/stopDataSimulator'),
                type: 'GET'
                });
            _checkStatus(true);
            $(this).hide();
            $(this).siblings('button').show();
        }
    },
    
    _sendMessageButtonClick = function(event) {
        var formData = $('#dataSimulatorForm').serialize();
        if($(this).attr('id') === 'send-message') {
            $(this).attr("disabled", "disabled");
            $.ajax({
                url: yukon.url('/dev/rfn/sendLcrDeviceMessages'),
                type: 'post',
                data: formData 
            }).done( function(data) {
                $('#stop-send-message').show();
                $('#send-message').hide();
                $('#send-message').removeAttr("disabled");
                _checkStatus(true);
            }).fail( function(data) {
                $('#send-message').removeAttr("disabled");
                if (data.hasError) {
                    $('#existingDeviceTaskStatusMessage').addMessage({message:data.errorMessage, messageClass:'error'}).show();
                } else {
                    $('#existingDeviceTaskStatusMessage').hide();
                }
            });
        }
            
        if($(this).attr('id') === 'stop-send-message') {
            $.ajax({
                url: yukon.url('/dev/rfn/stopSendingLcrDeviceMessages'),
                type: 'GET'
                });
            _checkExistingDeviceStatus(true);
            $(this).hide();
            $(this).siblings('button').show();
        }
    },

    _checkStatus = function(extraCheck) {
        if (!extraCheck) {
            $.ajax({
                url: yukon.url('datasimulator-status'),
                type: 'get'
            }).done( function(data) {
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
            }).fail( function(data) {
                if (!extraCheck) {
                    setTimeout(_checkStatus, _checkStatusTime);
                }
                if (data.hasError) {
                    $('#taskStatusMessage').addMessage({message:data.errorMessage, messageClass:'error'}).show();
                } else {
                    $('#taskStatusMessage').hide();
                }
            });
        }
    },
    
    _checkExistingDeviceStatus = function(extraCheck) {
        if (!extraCheck) {
            $.ajax({
                url: yukon.url('existing-datasimulator-status'),
                type: 'get'
            }).done( function(data) {
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
            }).fail( function(data) {
                if (!extraCheck) setTimeout(_checkExistingDeviceStatus, _checkStatusTime);
                if (data.hasError) {
                    $('#existingDeviceTaskStatusMessage').addMessage({message:data.errorMessage, messageClass:'error'}).show();
                } else {
                    $('#existingDeviceTaskStatusMessage').hide();
                }
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

$(function() { yukon.dev.simulators.dataSimulator.init(); });