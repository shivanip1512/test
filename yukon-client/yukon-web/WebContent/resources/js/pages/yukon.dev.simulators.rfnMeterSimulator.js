yukon.namespace('yukon.dev.simulators.rfnMeterSimulator');

/**
 * Module handling rfnMeterSimulator start, stop, and status messaging
 * @module yukon.dev.simulators.rfnMeterSimulator
 * @requires JQUERY
 * @requires yukon
 */

yukon.dev.simulators.rfnMeterSimulator = (function() {
    
    'use strict';
    var _initialized = false,
    _checkStatusTime = 2500,

    _sendMessageButtonClick = function(event) {
        var formData = $('#formData').serialize();
        
        if($(this).attr('id') === 'disconnect-message') {
            $.ajax({
                url: yukon.url('/dev/rfn/startMetersDisconnectRequest'),
                type: 'post',
                data: formData
            });
        }
        
        if($(this).attr('id') === 'stop-disconnect-message') {
            $.ajax({
                url: yukon.url('/dev/rfn/stopMetersDisconnectRequest'),
                type: 'post',
                data: formData
            });
        }
        
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
        }
          
        if($(this).attr('id') === 'stop-send-message') {
            $.ajax({
                url: yukon.url('/dev/rfn/stopMetersArchiveRequest'),
                type: 'get'
                });
            _checkExistingDeviceStatus(true);
        }
        
        if($(this).attr('id') === 'send-test') {
            var formData = $('#formData').serialize();
            $.ajax({
                url: yukon.url('/dev/rfn/testMeterArchiveRequest'),
                type: 'post',
                data: formData
            });
        }
        
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
            $('#send-test, #send-message, #stop-send-message, #disconnect-message, #stop-disconnect-message').click(_sendMessageButtonClick);
            _checkExistingDeviceStatus();
            _initialized = true;
        },
    };
    return mod;
}());

$(function() { yukon.dev.simulators.rfnMeterSimulator.init(); });