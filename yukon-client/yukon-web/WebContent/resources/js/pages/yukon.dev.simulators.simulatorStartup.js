yukon.namespace('yukon.dev.simulators.simulatorStartup');
 
/**
 * Module handling simulator startup functionality in Yukon
 * @module yukon.dev.simulators.simulatorStartup.js
 * @requires JQUERY
 * @requires yukon
 */

yukon.dev.simulators.simulatorStartup = ( function() {

    'use strict';
    
    var
    _initialized = false,
    _simType = null,

    _updateStartup = function (event) {
        
        var
        startupData = {simulatorType: $(event.target).closest(".js-sim-startup").data("simulatorType")};
        if ($(this).attr('id') === 'enable-startup') {
            startupData.runOnStartup = true;
            $.ajax({
                url: yukon.url('/dev/updateStartup'),
                type: 'post',
                data: startupData
            }).done(function (data) {
                if (data.hasError) {
                    _checkStartupStatus(data.errorMessage, startupData);
                } else {
                    yukon.ui.removeAlerts();
                }
            }).fail(function () {
                _checkStartupStatus("The simulator startup settings update request to the controller failed.", startupData);
            });
        } else if ($(this).attr('id') === 'disable-startup') {
            startupData.runOnStartup = false;
            $.ajax({
                url: yukon.url('/dev/updateStartup'),
                type: 'post',
                data: startupData
            }).done(function (data) {
                if (data.hasError) {
                    _checkStartupStatus(data.errorMessage, startupData);
                } else {
                    yukon.ui.removeAlerts();
                }
            }).fail(function () {
                _checkStartupStatus("The simulator startup settings update request to the controller failed.", startupData);
            });
        }
    },
     
    _checkStartupStatus = function (prevErrorMessage, startupData) {
        $.ajax({
            url: yukon.url('/dev/existingStartupStatus'),
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
        }).fail(function () {
            yukon.ui.alertError(prevErrorMessage + " Error communicating with NmIntegrationController. Refresh the page to try again.");
            $('#enable-startup').attr("disabled", "true");
            $('#disable-startup').attr("disabled", "true");
            $('#enable-startup').removeClass('on');
            $('#disable-startup').removeClass('on');
        });
    },
        
    mod = {
        
        init: function () {
            if (_initialized) return;
            _simType = $(".js-sim-startup").data("simulatorType");
            _checkStartupStatus(null, {simulatorType: _simType});
            $('#enable-startup, #disable-startup').on('click', _updateStartup);
            _initialized = true;
        },
    };
    
    return mod;
})();

$(function () { yukon.dev.simulators.simulatorStartup.init(); });