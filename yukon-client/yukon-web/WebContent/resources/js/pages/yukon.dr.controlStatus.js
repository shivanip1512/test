yukon.namespace('yukon.dr.controlStatus');

/**
 * Module for the DR Meter Control Status page.
 * @module yukon.dr.controlStatus
 * @requires JQUERY
 * @requires yukon
 */
yukon.dr.controlStatus = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    
    _updateTable = function () {
        var tableContainer = $('#control-status-table'),
            form = $('#control-status-form');
        form.ajaxSubmit({
            success: function(data, status, xhr, $form) {
                tableContainer.html(data);
                tableContainer.data('url', yukon.url('/dr/program/controlStatusTable?' + form.serialize()));
            }
        });
    },
    
    mod = {
        
        /** Initialize this module. */
        init : function () {
            
            if (_initialized) return;
            
            $('#controlStatuses').chosen({width: "300px"});
            $('#restoreStatuses').chosen({width: "300px"});
            
            $(document).on('click', '.js-filter', function (ev) {
                _updateTable();
            });
            
            $(document).on('click', '.js-connect, .js-disconnect', function () {
                yukon.ui.block($('#control-status-table'));
                var deviceId = $(this).data('deviceId'),
                    programId = $('#programId').val(),
                    connect = $(this).hasClass('js-connect');
                $.ajax({
                    url: yukon.url('/dr/disconnectStatus/change?deviceId=' + deviceId + '&connect=' + connect + '&programId=' + programId),
                    type: 'POST'
                }).done(function(data) {
                    if (data.errors) {
                        yukon.ui.alertError(data.errors);
                    }
                    _updateTable();
                    yukon.ui.unblock($('#control-status-table'));
                    $('.js-row-' + deviceId).flash();
                });
                
            });
            
            $(document).on('click', '.js-download', function () {
                var form = $('#control-status-form');
                var data = form.serialize();
                window.location = yukon.url('/dr/program/controlStatus/download?' + data);
            });

            _initialized = true;
        }
        
    };
    
    return mod;
})();

$(function () { yukon.dr.controlStatus.init(); });