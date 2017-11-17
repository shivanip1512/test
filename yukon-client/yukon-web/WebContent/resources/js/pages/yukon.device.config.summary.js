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
    
    _filterResults = function () {
        var filterButton = $('.js-filter'),
            form = $('#filter-form');
        yukon.ui.busy(filterButton);
        form.ajaxSubmit({
            success: function(data, status, xhr, $form) {
                yukon.ui.unbusy(filterButton);
                $('#results-table').html(data);
                $('#results-table').data('url', yukon.url('/deviceConfiguration/summary/filter?' + form.serialize()));
            },
            error: function(xhr, status, error, $form) {
                yukon.ui.unbusy(filterButton);
                $('#results-table').html(xhr.responseText);
            }
        });
    },

    mod = {

        /** Initialize the module.*/
        init : function () {
            
            if (_initialized) return;
            
            _filterResults();
            
            /** Filter the results */
            $(document).on('click', '.js-filter', function (ev) {
                _filterResults();
            });
            
            $(document).on('click', '.js-send-config', function () {   
                var deviceId = $(this).data('deviceId');
                $.ajax({
                    url: yukon.url('/deviceConfiguration/summary/' + deviceId + '/sendConfig'),
                    type: 'post'
                }).done(function () {
                    $(document).scrollTop(0);
                    window.location.reload();
                });
            });
            
            $(document).on('click', '.js-read-config', function () {   
                var deviceId = $(this).data('deviceId');
                $.ajax({
                    url: yukon.url('/deviceConfiguration/summary/' + deviceId + '/readConfig'),
                    type: 'post'
                }).done(function () {
                    $(document).scrollTop(0);
                    window.location.reload();
                });
            });
            
            $(document).on('click', '.js-verify-config', function () {   
                var deviceId = $(this).data('deviceId');
                $.ajax({
                    url: yukon.url('/deviceConfiguration/summary/' + deviceId + '/verifyConfig'),
                    type: 'post'
                }).done(function () {
                    $(document).scrollTop(0);
                    window.location.reload();
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


            _initialized = true;

        },

    };

    return mod;
}());

$(function () { yukon.deviceConfig.summary.init(); });