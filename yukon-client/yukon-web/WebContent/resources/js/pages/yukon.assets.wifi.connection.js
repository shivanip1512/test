yukon.namespace('yukon.assets.wifi.connection');
 
/**
 * Module to handle the Wifi Connection Status.
 * 
 * @module yukon.assets.wifi.connection
 * @requires yukon
 * @requires JQUERY
 */
yukon.assets.wifi.connection = (function () {
 
    var
    _initialized = false,
    
    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
            //refresh connection status for a device
            $(document).on('click', '.js-refresh-wifi', function () {
                var btn = $(this),
                    refreshMsg = $('.js-refresh-msg');
                refreshMsg.addClass('dn');
                yukon.ui.busy(btn);
                $('#wifi-refresh-form').ajaxSubmit({
                    complete: function () {
                        refreshMsg.removeClass('dn');
                        setTimeout(function() {yukon.ui.unbusy(btn); }, 1000);
                    }
                });
            });

            
            _initialized = true;
        },

    };
 
    return mod;
})();
 
$(function () { yukon.assets.wifi.connection.init(); });