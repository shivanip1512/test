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

    _updateStartup = function (event) {
        
        var
        startupData = {simulatorType: $(event.target).closest(".js-sim-startup").data("simulatorType")};
        if ($(this).hasClass('enable-startup')) {
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
        } else if ($(this).hasClass('disable-startup')) {
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
     
    _checkStartupStatus = function (prevErrorMessage, startupData, button) {
        $.ajax({
            url: yukon.url('/dev/existingStartupStatus'),
            type: 'post',
            data: startupData
        }).done(function(data) {
            if (data.hasError) {
                yukon.ui.alertError(prevErrorMessage + " " + data.errorMessage + " Refresh the page to try again.");
                button.find('.enable-startup').attr("disabled", "true");
                button.find('.disable-startup').attr("disabled", "true");
                button.find('.enable-startup').removeClass('on');
                button.find('.disable-startup').removeClass('on');
            } else {
                if (prevErrorMessage) {
                    yukon.ui.alertError(prevErrorMessage);
                }
                if (data.runOnStartup) {
                    button.find('.enable-startup').addClass('on');
                    button.find('.disable-startup').removeClass('on');
                } else {
                    button.find('.enable-startup').removeClass('on');
                    button.find('.disable-startup').addClass('on');
                }
            }
        }).fail(function () {
            yukon.ui.alertError(prevErrorMessage + " Error communicating with NmIntegrationController. Refresh the page to try again.");
            button.find('.enable-startup').attr("disabled", "true");
            button.find('.disable-startup').attr("disabled", "true");
            button.find('.enable-startup').removeClass('on');
            button.find('.disable-startup').removeClass('on');
        });
    },
        
    mod = {
        
        init: function () {
            if (_initialized) return;
            
            $(".js-sim-startup").each( function() {
                var simType = $( this ).data("simulatorType");
                _checkStartupStatus(null, {simulatorType: simType}, $(this));
            });
            
            $(document).on('click', '.enable-startup, .disable-startup', _updateStartup);
            _initialized = true;
        },
    };
    
    return mod;
})();

$(function () { yukon.dev.simulators.simulatorStartup.init(); });