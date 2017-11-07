yukon.namespace('yukon.deviceConfig.summary');

/**
 * This module handles behavior on the device configuration summary page.
 * @module yukon.deviceConfig.summary
 * @requires JQUERY
 * @requires JQUERY UI
 */
yukon.deviceConfig.summary = (function () {
    'use strict';
    
    var
    _initialized = false,

    mod = {

        /** Initialize the module.*/
        init : function () {
            
            if (_initialized) return;
            
            $(document).on('click', '.js-send-config', function () {   
                var deviceId = $(this).data('deviceId');
                $.ajax({
                    url: yukon.url('/deviceConfiguration/summary/' + deviceId + '/sendConfig'),
                    type: 'post'
                }).done(function () {
                    window.location.reload();
                });
            });
            
            $(document).on('click', '.js-read-config', function () {   
                var deviceId = $(this).data('deviceId');
                $.ajax({
                    url: yukon.url('/deviceConfiguration/summary/' + deviceId + '/readConfig'),
                    type: 'post'
                }).done(function () {
                    window.location.reload();
                });
            });
            
            $(document).on('click', '.js-verify-config', function () {   
                var deviceId = $(this).data('deviceId');
                $.ajax({
                    url: yukon.url('/deviceConfiguration/summary/' + deviceId + '/verifyConfig'),
                    type: 'post'
                }).done(function () {
                    window.location.reload();
                });
            });


            _initialized = true;

        },

    };

    return mod;
}());

$(function () { yukon.deviceConfig.summary.init(); });