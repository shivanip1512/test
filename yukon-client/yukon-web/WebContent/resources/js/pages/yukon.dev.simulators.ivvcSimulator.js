yukon.namespace('yukon.dev.simulators.ivvcSimulator');

/**
 * Module handling ivvcSimulator start, stop, and status messaging
 * @module yukon.dev.simulators.ivvcSimulator
 * @requires JQUERY
 * @requires yukon
 */

yukon.dev.simulators.ivvcSimulator = ( function() {
    
    'use strict';

    var 
    _initialized = false,
    _checkStatusTime = 2500,
    
    _sendMessageButtonClick = function(event) {
        var formData = $('#formData').serialize();
        if($(this).attr('id') === 'send-message') {
            $.ajax({
                url: yukon.url('/dev/ivvc/startIvvcSimulatorRequest'),
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
                url: yukon.url('/dev/ivvc/stopIvvcSimulatorRequest'),
                type: 'get'
                });
            _checkExistingDeviceStatus(true);
        }
        
        $(this).hide();
        $(this).siblings('button').show();
    },
    
    _checkExistingDeviceStatus = function(extraCheck) {
        if (!extraCheck) {
            $.ajax({
                url: yukon.url('existing-ivvcSimulator-status'),
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
                    $("#ivvcForm :input").prop("disabled", true);
                } else {
                    $('#stop-send-message').hide();
                    $('#send-message').show();
                    $("#ivvcForm :input").prop("disabled", false);
                }
                var running = "Not Running";
                if (data.running) {
                    running = "Running";
                }
                $('#status-running').text(running);
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

$(function() { yukon.dev.simulators.ivvcSimulator.init(); });