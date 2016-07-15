yukon.namespace('yukon.bulk.dataStreaming');
/**
 * Module for the Collection Actions Data Streaming pages
 * @module yukon.bulk.dataStreaming
 * @requires JQUERY
 * @requires yukon
 */
yukon.bulk.dataStreaming = (function () {

    var enableDisable = function () {

        var toggle = $('.js-configuration-type'),
        configTypeRow = toggle.closest('tr'),
        newConfig = configTypeRow.find('.switch-btn-checkbox').prop('checked');

        if (newConfig) {
            $('.js-new-configuration').toggleClass('dn', false);
            $('.js-selected-config').hide();
        } else {
            $('.js-new-configuration').toggleClass('dn', true);
        }
    };

    'use strict';
    var initialized = false,

    mod = {

            /** Initialize this module. */
            init: function () {

                if (initialized) return;

                $(document).ready(function() {enableDisable();});

//              show/hide information based on New or Existing Configuration selection
                $(document).on('click', '.js-configuration-type', function () {
                    enableDisable();
                });

//              Validate user has either selected an existing config or turned at least one attribute on
                $(document).on('click', '.js-next-button', function () {
                    var validConfig = false;

                    var selectedConfig = $('#selectedConfiguration').val();
                    if (selectedConfig == 0) {
                        $('.js-attribute').each(function () {
                            var attOn = $(this).find('.switch-btn-checkbox').prop('checked');
                            if (attOn) {
                                validConfig = true;
                            }
                        });
                    } else {
                        validConfig = true;
                    }

                    if (validConfig) {
                        $('#configureForm').submit();
                    } else {
                        $('.js-none-selected').show();
                    }

                });
                
                //  display table showing full data streaming configuration
                $(document).on('change', '.js-existing-configuration', function () {
                    var id = $(this).find(":selected").val();
                    $('.js-selected-config').hide();
                    if (id > 0) {
                        $('.js-selected-config').show();
                    }
                    $('.js-config-table').hide();
                    $('#configTable_' + id).show();
                });
                
                
                initialized = true;

            },
    };

    return mod;

})();

$(function () { yukon.bulk.dataStreaming.init(); });