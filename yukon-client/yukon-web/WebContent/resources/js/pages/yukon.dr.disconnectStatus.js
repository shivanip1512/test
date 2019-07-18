yukon.namespace('yukon.dr.disconnectStatus');

/**
 * Module for the Disconnect Status page.
 * @module yukon.dr.disconnectStatus
 * @requires JQUERY
 * @requires yukon
 */
yukon.dr.disconnectStatus = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    
    mod = {
        
        /** Initialize this module. */
        init : function () {
            
            if (_initialized) return;
            
            $(document).on('click', '.js-restore, .js-resend-shed', function () {
                yukon.ui.block($('#disconnect-status-table'));
                var deviceId = $(this).data('deviceId'),
                    command = $(this).data('command');
                $.ajax({
                    url: yukon.url('/dr/disconnectStatus/' + command + '?deviceId=' + deviceId),
                    type: 'POST'
                }).done(function(data) {
                    if (data.success) {
                        var timeText = moment(data.time.millis).tz(yg.timezone).format(yg.formats.date.both);
                        $('.js-status-' + deviceId).html(data.status);
                        $('.js-time-' + deviceId).html(timeText);
                    } else {
                        yukon.ui.alertError(data.errors);
                    }
                    yukon.ui.unblock($('#disconnect-status-table'));
                });
                
            });

            _initialized = true;
        }
        
    };
    
    return mod;
})();

$(function () { yukon.dr.disconnectStatus.init(); });