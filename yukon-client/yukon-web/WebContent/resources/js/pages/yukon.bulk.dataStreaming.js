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
            id = (id === undefined ? $('#configId').val() : id);
            if (id > 0) {
                $('.js-selected-config').show();
            }
            $('.js-config-table').hide();
            
            $('#configTable_' + id).show();
        }
    };
    
    var checkForValidConfig = function () {
        var validConfig = false;
        var toggle = $('.js-configuration-type'),
        configTypeRow = toggle.closest('tr'),
        newConfig = configTypeRow.find('.switch-btn-checkbox').prop('checked');
        if (newConfig) {
            var numAttributesOn = $('.js-attribute .switch-btn-checkbox:checked').length,
                offAttributes = $('.js-attribute .switch-btn-checkbox:not(":checked")');
                maxNumAttributes = 8;
            if (numAttributesOn >= maxNumAttributes) {
                $('.js-too-many-attributes').show();
                offAttributes.prop("disabled", true);
            } else {
                $('.js-too-many-attributes').hide();
                offAttributes.prop("disabled", false);
            }
            if (numAttributesOn > 0 && numAttributesOn <= maxNumAttributes) {
                validConfig = true;
            }
        } else {
            var selectedConfig = $('#selectedConfiguration').val();
            if (selectedConfig > 0) {
                validConfig = true;
            }
        }
        
        $('.js-send-button').prop('disabled', !validConfig);
    };

    'use strict';
    var initialized = false,

    mod = {

            /** Initialize this module. */
            init: function () {

                $(document).ready(function() {enableDisable();});

//              show/hide information based on New or Existing Configuration selection
                $(document).on('click', '.js-configuration-type', function () {
                    enableDisable();
                    showConfigurationTable();
                    checkForValidConfig();
                });
              
                $(document).on('click', '.js-send-button', function () {
                    var btn = $(this),
                        popup = $('#verificationDialog');
                    $('#configureForm').ajaxSubmit({
                        success: function(data, status, xhr, $form) {
                            popup.html(data);
                            yukon.ui.unbusy(btn);
                            var isSendDisabled = $('#verificationPassed').val() == 'false';
                            popup.dialog({
                                title: popup.data('title'),
                                modal: true,
                                width: 900,
                                buttons: yukon.ui.buttons({ okText: yg.text.send, event: 'yukon.bulk.dataStreaming.send', okDisabled : isSendDisabled }),
                            });
                        }
                    });
                });
                
                $(document).on('yukon.bulk.dataStreaming.send', function (ev) {
                    var dialog = $(ev.target),
                        btns = dialog.closest('.ui-dialog').find('.ui-dialog-buttonset'),
                        primary = btns.find('.js-primary-action');
                    yukon.ui.busy(primary);
                    $('#verificationForm').ajaxSubmit({
                        success: function(data, status, xhr, $form) {
                            dialog.dialog('destroy');
                            $('#progressReportDiv').html(data);
                            $('#collectionActionsAccordion').accordion("option", "active", 3);
                            yukon.collection.actions.progress.report.init();
                        },
                        error: function (xhr, status, error, $form) {
                            dialog.html(xhr.responseText);
                            yukon.ui.unbusy(primary);
                        },
                    
                    });
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

            }

    };

    return mod;

})();

$(function () { yukon.bulk.dataStreaming.init(); });