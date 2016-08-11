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
            var selectedAtt = $('#attributesSelect').val(),
                selectedInt = $('#intervalSelect').val(),
                selectedConfig = $('.js-selected-configuration').val();
            if (selectedConfig == -1) {
                $('.js-selected-attInterval').prop('disabled', false);
            } else {
                $('.js-selected-attInterval').prop('disabled', true);
            }
            if (selectedAtt == -1 && selectedInt == -1) {
                $('.js-selected-configuration').prop('disabled', false);

            } else {
                $('.js-selected-configuration').prop('disabled', true);

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

            _initialized = true;
        }
    
    };
    
    return mod;
})();

$(function () { yukon.tools.dataStreaming.init(); });