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
    
    var showConfigurationTable = function () {
        var toggle = $('.js-configuration-type'),
        configTypeRow = toggle.closest('tr'),
        newConfig = configTypeRow.find('.switch-btn-checkbox').prop('checked');
        if (!newConfig) {
            var id = $('.js-existing-configuration').find(":selected").val();
            if (id > 0) {
                $('.js-selected-config').show();
            }
           
            $('#configTable_' + id).show();
        }
    };
    
    var checkForValidConfig = function () {
        var validConfig = false;
        var toggle = $('.js-configuration-type'),
        configTypeRow = toggle.closest('tr'),
        newConfig = configTypeRow.find('.switch-btn-checkbox').prop('checked');
        if (newConfig) {
            $('.js-attribute').each(function () {
                var attOn = $(this).find('.switch-btn-checkbox').prop('checked');
                if (attOn) {
                    validConfig = true;
                }
            });
        } else {
            var selectedConfig = $('#selectedConfiguration').val();
            if (selectedConfig > 0) {
                validConfig = true;
            }
        }

        if (validConfig) {
            $('.js-next-button').prop('disabled', false);
        } else {
            $('.js-next-button').prop('disabled', true);
        }
    };

    'use strict';
    var initialized = false,
    _canceled,

    mod = {

            /** Initialize this module. */
            init: function () {

                if (initialized) return;
                
                $('#cancel-btn').click(function(ev) {
                    var btn = $(this),
                    params = {
                        key: btn.data('key')
                    };

                    yukon.ui.busy(btn);
                    
                    _canceled = true;
                    debug.log('canceling data streaming');
        
                    $.post(yukon.url('/bulk/dataStreaming/cancel'), params)
                    .done(function (result) {
                        debug.log('data streaming cancel result: ' + result);
                    });
                });

                $(document).ready(function() {enableDisable();});

//              show/hide information based on New or Existing Configuration selection
                $(document).on('click', '.js-configuration-type', function () {
                    enableDisable();
                    showConfigurationTable();
                    checkForValidConfig();
                });
              
                $(document).on('click', '.js-next-button', function () {
                    $('#configureForm').submit();
                });
                
                //  display table showing full data streaming configuration
                $(document).on('change', '.js-existing-configuration', function () {
                    showConfigurationTable();
                    checkForValidConfig();
                });
                
                $(document).on('click', '.js-attribute', function () {
                    checkForValidConfig();
                });
                
                checkForValidConfig();
                showConfigurationTable();
                initialized = true;
                _canceled = false;

            },
            
            /** Update the progress 
             *  @param {Object} data - Progress data.
             */
            progress: function(data) {
                var done = data.value === 'true';
                if (!_canceled) {
                    $('#cancel-btn').toggle(!done);
                }
                if (done) {
                    $('.js-result').each(function(index, elem) {
                        var result = $(elem),
                        count = result.find('.js-count'),
                        action = result.find('.js-action');
                        
                        if (parseInt(count.text(), 10) > 0) {
                            action.show();
                        }
                    });
                    
                    $('.js-progress .js-action').show();
                    $('.js-results-detail').show();
                    $('#cancel-btn').hide();
                }
            }
    };

    return mod;

})();

$(function () { yukon.bulk.dataStreaming.init(); });