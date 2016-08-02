yukon.namespace('yukon.tools.dataStreaming');

/**
 * Module for the Data Streaming Configurations and Summary apges
 * @module yukon.tools.dataStreaming
 * @requires JQUERY
 * @requires yukon
 */
yukon.tools.dataStreaming = (function () {

    'use strict';
    
    var
    _initialized = false;

    mod = {
        
        /** Initialize this module. */
        init: function () {
                        
            if (_initialized) return;
            
            /** A checkbox was clicked, update the selected count and device collection. */
            $(document).on('click', '.js-select-all-item', function (ev) {

                var selected = $(this).prop('checked'),
                    count = parseInt($('.badge').text()),
                    selectedIds = $('.js-selected-ids').text();

                //update the count
                $('.badge').text(selected ? count + 1 : count - 1);
                
                //update the device Id list
                var list = selectedIds.split(',');
                if (selected) {
                    list.push($(this).val());
                } else {
                    list.splice(list.indexOf($(this).val()), 1);
                }
                $('.js-selected-ids').text(list.join(','));
            });
            
            /** Select all was clicked, update the selected count and device collection. */
            $(document).on('click', '.js-select-all', function (ev) {

                var selected = $(this).prop('checked'),
                    resultsCount = $('.js-results-count').text(),
                    resultsIds = $('.js-results-ids').text();

                //update the count
                $('.badge').text(selected ? resultsCount : 0);
                //update the device Id list
                $('.js-selected-ids').text(selected ? resultsIds : "");
            });
            
            $(document).on('yukon:tools:dataStreaming:results:load', function (ev) {

            });
            
            /** Configure Data Streaming was selected */
            $(document).on('click', '.js-selected-configure', function (ev) {
                //send to Configure Data Streaming page with collection parameters
                var selectedIds = $('.js-selected-ids').text();
                window.location.href = yukon.url('/bulk/dataStreaming/configure?collectionType=idList&idList.ids=' + selectedIds);
            });
            
            /** Remove Data Streaming was selected */
            $(document).on('click', '.js-selected-remove', function (ev) {
                //send to Remove Data Streaming page with collection parameters
                var selectedIds = $('.js-selected-ids').text();
                window.location.href = yukon.url('/bulk/dataStreaming/remove?collectionType=idList&idList.ids=' + selectedIds);
            });
            
            /** Collection Actions was selected */
            $(document).on('click', '.js-selected-actions', function (ev) {
                //send to Collection Actions page with collection parameters
                var selectedIds = $('.js-selected-ids').text();
                window.location.href = yukon.url('/bulk/collectionActions?collectionType=idList&idList.ids=' + selectedIds);
            });

            _initialized = true;
        }
    
    };
    
    return mod;
})();

$(function () { yukon.tools.dataStreaming.init(); });