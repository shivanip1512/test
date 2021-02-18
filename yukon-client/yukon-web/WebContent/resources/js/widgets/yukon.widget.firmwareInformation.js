yukon.namespace('yukon.widget.firmwareInformation');

/**
 * Module for the firmwareInformation widget
 * @module yukon.widget.firmwareInformation
 * @requires JQUERY
 * @requires yukon
 */
yukon.widget.firmwareInformation = (function () {
    
    'use strict';
    
    var
    _initialized = false,

    _update = function () {
        $.ajax({
            url: yukon.url('/widget/firmwareInformationWidget/render'),
        }).done(function (data) {
            $('#js-firmware-info').html(data);
        });
        setTimeout(_update, yg._updateInterval);
    },

    mod = {
        
        /** Initialize this module. */
        init : function () {
            
            if (_initialized) return;

            _update();
            _initialized = true;
        },
        
    };
    
    return mod;
})();

$(function () { yukon.widget.firmwareInformation.init(); });