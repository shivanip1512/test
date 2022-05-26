yukon.namespace('yukon.assets.signalTransmitter');

/**
 * Module that handles the behavior on the Signal Transmitter page.
 * @module yukon.assets.signalTransmitter
 * @requires JQUERY
 * @requires yukon
 */
yukon.assets.signalTransmitter = (function() {
    
    'use strict';
    
    var
    _initialized = false,
    
        
    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
                
            $(document).on('change', '.js-type', function (event) {
                var type = $('.js-type').val(); 
                if (type !== '') {
                    yukon.ui.block($('js-signal-transmitter-container'));
                
                    var name = $('.js-name').val();
                    $.ajax({
                        url: yukon.url('/stars/device/signalTransmitter/create/' + type),
                        type: 'get',
                       data: {name: name}
                    }).done(function(data) {
                         $("#signalTransmitter").html(data);
                         yukon.ui.unblock($('js-signal-transmitter-container'));
                   });
                } else {
                    $('.noswitchtype').html('');
                }
            });
            
            _initialized = true;
        }
    };
    
    return mod;
})();

$(function () { yukon.assets.signalTransmitter.init(); });