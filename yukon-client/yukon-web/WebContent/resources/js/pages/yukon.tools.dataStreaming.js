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

        showHideConfigurations: function() {
            var selectedAtt = ($('#attributesSelect').val()? $('#attributesSelect').val(): -1),
                selectedInt = ($('#intervalSelect').val()? $('#intervalSelect').val(): -1),
                selectedConfig = ($('.js-selected-configuration').val()? $('.js-selected-configuration').val(): -1);
            
            if (selectedConfig == -1 && !(selectedAtt == -1 && selectedInt == -1)) {
                $('.js-selected-attInterval').prop('disabled', false);
                $('.js-selected-configuration').prop('disabled', true);
            } else if (selectedAtt == -1 && selectedInt == -1 && !(selectedConfig == -1)) {
                $('.js-selected-attInterval').prop('disabled', true);
                $('.js-selected-configuration').prop('disabled', false);
            } else {
                $('.js-selected-attInterval').prop('disabled', false);
                $('.js-selected-configuration').prop('disabled', false);
            }
        },
        
        updateCheckboxes: function() {
            var selectedIds = $('.js-selected-ids').val();
            var list = new Array();
            list = selectedIds.split(',');
            $('.js-select-all-item').each(function (index, item) {
                if (list.indexOf($(item).val()) !== -1) {
                    $(item).prop('checked', true);
                } else {
                    $(item).prop('checked', false);
                    $('.js-select-all').prop('checked', false);
                }
            });
        },

        checkAll: function() {
            $('.js-select-all-item').prop('checked', true);
            $('.js-select-all').prop('checked', true);
            $('.js-selected-ids').val($('.js-results-ids').text());
        },
        
        /** Initialize this module. */
        init: function () {
                        
            if (_initialized) return;
            
            mod.showHideConfigurations();
            mod.checkAll();

            /** A checkbox was clicked, update the selected count and device collection. */
            $(document).on('click', '.js-select-all-item', function (ev) {
                
                var selected = $(this).prop('checked'),
                    count = parseInt($('.badge').text()),
                    selectedIds = $('.js-selected-ids').val();

                //update the count
                var newCount = selected ? count + 1 : count - 1
                $('.badge').text(newCount);
                $('.js-cog-menu').toggleClass('dn', newCount == 0);
                
                //update the device Id list
                var list = selectedIds.length > 0 ? selectedIds.split(',') : new Array();
                if (selected) {
                    list.push($(this).val());
                } else {
                    list.splice(list.indexOf($(this).val()), 1);
                }
                $('.js-selected-ids').val(list.join(','));
            });
            
            /** Select all was clicked, update the selected count and device collection. */
            $(document).on('click', '.js-select-all', function (ev) {

                var selected = $(this).prop('checked'),
                    resultsCount = $('.js-results-count').text(),
                    resultsIds = $('.js-results-ids').text();

                //update the count
                $('.badge').text(selected ? resultsCount : 0);
                $('.js-cog-menu').toggleClass('dn', !selected);
                //update the device Id list
                $('.js-selected-ids').val(selected ? resultsIds : "");

            });
            
            $(document).on('yukon:tools:dataStreaming:results:load', function (ev) {
                mod.updateCheckboxes();
            });
            
            /** Configure Data Streaming was selected */
            $(document).on('click', '.js-selected-configure', function (ev) {
                $('#redirectUrl').val("/bulk/dataStreaming/configure");
                $('#createTempGroupForm').submit();
            });
            
            /** Remove Data Streaming was selected */
            $(document).on('click', '.js-selected-remove', function (ev) {
                $('#redirectUrl').val("/bulk/dataStreaming/remove");
                $('#createTempGroupForm').submit();
            });
            
            /** Collection Actions was selected */
            $(document).on('click', '.js-selected-actions', function (ev) {
                $('#redirectUrl').val("/bulk/collectionActions");
                $('#createTempGroupForm').submit();
            });
            
            $('#searchSection :input').change(function (ev) {
                $('#searchForm').attr('action', '/yukon/tools/dataStreaming/summary');
                $('#searchForm').submit(); //gut feeling I need to change the action back after submit
            });
            
            /** Configuration Drop down was changed */
            $(document).on('change', '.js-selected-configuration', function (ev) {
                mod.showHideConfigurations();
            });
            
            /** Attribute or Interval Drop down was changed */
            $(document).on('click', '.js-selected-attInterval', function (ev) {
                mod.showHideConfigurations();
            });
            
            /** Show All was clicked */
            $(document).on('click', '.js-show-all', function (ev) {
                window.location.href = yukon.url('/tools/dataStreaming/summary');
            });
            
            $(document).on('yukon:tools:dataStreaming:resend', function (ev) {
                var container = $(ev.target),
                deviceId = container.data('deviceId'),
                sorting = $('.sortable.desc, .sortable.asc'),
                paging = $('.paging-area'),
                params = {};
                
                if (sorting.length > 0) {
                    params.sort = sorting.data('sort'),
                    params.dir = sorting.is('.desc') ? 'desc' : 'asc';
                }
                params.itemsPerPage = paging.length > 0 ? paging.data('pageSize') : 10;
                params.page = paging.length > 0 ? paging.data('currentPage') : 1;
                window.location.href = yukon.url('/tools/dataStreaming/discrepancies/' + deviceId + '/resend?' + $.param(params));
            });
            
            $(document).on('yukon:tools:dataStreaming:accept', function (ev) {
                var container = $(ev.target),
                deviceId = container.data('deviceId');
                window.location.href = yukon.url('/tools/dataStreaming/discrepancies/' + deviceId + '/accept');
            });
            
            $(document).on('yukon:tools:dataStreaming:remove', function (ev) {
                var container = $(ev.target),
                deviceId = container.data('deviceId');
                window.location.href = yukon.url('/tools/dataStreaming/discrepancies/' + deviceId + '/remove');
            });
            
            $(document).on('yukon:tools:dataStreaming:resendAll', function (ev) {
                var form = $('#resendAll'),
                url = form.attr('action'),
                sorting = $('.sortable.desc, .sortable.asc'),
                paging = $('.paging-area'),
                params = {};
                
                if (sorting.length > 0) {
                    params.sort = sorting.data('sort'),
                    params.dir = sorting.is('.desc') ? 'desc' : 'asc';
                }
                params.itemsPerPage = paging.length > 0 ? paging.data('pageSize') : 10;
                params.page = paging.length > 0 ? paging.data('currentPage') : 1;
                form.attr('action', url + "?" + $.param(params));
                form.submit();
            });
            
            $(document).on('yukon:tools:dataStreaming:acceptAll', function (ev) {
                var container = $(ev.target);
                container.closest("form").submit();
            });

            _initialized = true;
        }
    
    };
    
    return mod;
})();

$(function () { yukon.tools.dataStreaming.init(); });