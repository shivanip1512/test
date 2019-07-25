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
            
            $('#disconnectStatus').chosen({width: "300px"});
            
            $(document).on('click', '.js-filter', function (ev) {
                var tableContainer = $('#disconnect-status-table'),
                    form = $('#disconnect-form');
                form.ajaxSubmit({
                    success: function(data, status, xhr, $form) {
                        tableContainer.html(data);
                        tableContainer.data('url', yukon.url('/dr/program/disconnectStatusTable?' + form.serialize()));
                    }
                });
            });
            
            $(document).on('click', '.js-connect, .js-disconnect', function () {
                yukon.ui.block($('#disconnect-status-table'));
                var deviceId = $(this).data('deviceId'),
                    connect = $(this).hasClass('js-connect');
                $.ajax({
                    url: yukon.url('/dr/disconnectStatus/change?deviceId=' + deviceId + '&connect=' + connect),
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
            
            $(document).on('click', '.js-download', function () {
                var form = $('#disconnect-form');
                var data = form.serialize();
                window.location = yukon.url('/dr/program/disconnectStatus/download?' + data);
            });

            _initialized = true;
        }
        
    };
    
    return mod;
})();

$(function () { yukon.dr.disconnectStatus.init(); });