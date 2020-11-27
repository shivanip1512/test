yukon.namespace('yukon.widget.firmwareAndCertificateInformation');

/**
 * Module for the firmwareAndCertificateInformation widget
 * @module yukon.widget.firmwareAndCertificateInformation
 * @requires JQUERY
 * @requires yukon
 */
yukon.widget.firmwareAndCertificateInformation = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    _updateInterval = 6000,
    
    _update = function () {
        $.ajax({
            url: yukon.url('/widget/firmwareAndCertificateInformationWidget/render'),
        }).done(function (data) {
            $('#js-firmware-and-certificate-info').html(data);
        }); 
        setTimeout(_update, _updateInterval)
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

$(function () { yukon.widget.firmwareAndCertificateInformation.init(); });