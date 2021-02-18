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
            
            $('#selectedConfigurations').chosen({width: "300px"});
            
            $(document).on('click', '.js-device-action', function () {   
                var deviceId = $(this).data('deviceId'),
                    action = $(this).data('action');
                $.ajax({
                    url: yukon.url('/deviceConfiguration/summary/' + deviceId + '/' + action),
                    type: 'post'
                }).done(function (data) {
                    if (data.successMessage) {
                        yukon.ui.alertSuccess(yukon.escapeXml(data.successMessage));
                        //refresh row
                        $.ajax(yukon.url('/deviceConfiguration/summary/' + deviceId + '/refreshDeviceRow'))
                        .done(function (rowData) {
                            //remove any existing view history dialogs
                            $('.js-view-history-' + deviceId).remove();
                            var deviceRow = $('#summary-table').find('tr[data-device-id=' + deviceId + ']');
                            deviceRow.html(rowData).flash();
                        });
                    }
                });
            });
            
            $(document).on('click', '.js-collection-action', function () {   
                var action = $(this).data('collectionAction'),
                    form = $('#filter-form'),
                    data = form.serialize();
                window.open(yukon.url('/deviceConfiguration/summary/collectionAction/' + action + '?' + data), '_blank');
            });
            
            /** Load the View History popup. */
            $(document).on('yukon:config:viewHistory', function (ev) {
                var popup = $(ev.target),
                    table = popup.find('#history-table');
                Sortable.init();
                Sortable.initTable(table);
            });

            /** Download CSV */
            $(document).on('click', '.js-config-download', function () {
                var form = $('#filter-form'),
                    data = form.serialize();
                window.location = yukon.url('/deviceConfiguration/summary/download?' + data);
            });
            
            $(document).on('click', '.js-filter-configs', function() {
                var tableContainer = $('#results-container'),
                    form = $('#filter-form');
                form.ajaxSubmit({
                    success: function(data, status, xhr, $form) {
                        tableContainer.html(data);
                        tableContainer.data('url', yukon.url('/deviceConfiguration/summary/filter?' + form.serialize()));
                        yukon.ui.unbusy($('.js-filter-configs'));
                    }
                });   
            });
            
            _initialized = true;

        },
        
        refreshCheck: function (deviceId) {
            return function (data) {
                var isActionFinished = data.isInProgress === 'false';
                if (isActionFinished) {
                    //refresh row
                    $.ajax(yukon.url('/deviceConfiguration/summary/' + deviceId + '/refreshDeviceRow'))
                    .done(function (rowData) {
                        var deviceRow = $('#summary-table').find('tr[data-device-id=' + deviceId + ']');
                        deviceRow.html(rowData).flash();
                        //stop data updaters
                        var idMap = {isInProgress : "DEVICE_CONFIG_SUMMARY/" + deviceId + "/IS_IN_PROGRESS"};
                        yukon.dataUpdater.unRegisterCallback(idMap);
                    });
                }
            }
        },

    };

    return mod;
}());

$(function () { yukon.deviceConfig.summary.init(); });