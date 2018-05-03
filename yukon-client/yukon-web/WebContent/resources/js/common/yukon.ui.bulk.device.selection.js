yukon.namespace('yukon.ui.bulk.device.selection');

/**
 * Module that handles the Device Selection tag
 * @module yukon.ui.bulk.device.selection
 * @requires JQUERY
 * @requires yukon
 */
yukon.ui.bulk.device.selection = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    _blockOnSubmit = false,
    _noGroupSelectedText,
    _eventAfterSubmit,
    
    mod = {

        submitSelectDevicesByGroupForm : function () {
            if ($('#group-name').val() == '') {
                alert(_noGroupSelectedText);
                return false;
            }
            if (_blockOnSubmit) {
                yukon.ui.blockPage();
            }
            var form = $('#selectDevicesByGroupForm');
            if (_eventAfterSubmit) {
                form.ajaxSubmit({
                    success: function(data, status, xhr, $form) {
                        mod.updateCollectionActions(data);
                    }
                });
            } else {
                form.submit();
            }
        },
        
        updateCollectionActions : function (data) {
            $('#collectionActionsDiv').html(data);
            $('.js-device-separator').removeClass('dn');
            $('.js-count').html($('#deviceCollectionCount').val());
            $('.js-device-description').html($('#deviceCollectionDescription').val());
            $('#collectionActionsAccordion').accordion("option", "active", 1);
            //clear out any old progress reports
            var progressReportMsg = $('#progressReportMessage').val();
            $('#progressReportDiv').html(progressReportMsg);
            yukon.collection.actions.progress.report.cancelUpdating();
            var refreshUrl = $('#refreshLink').val();
            window.history.pushState({path:refreshUrl}, '', refreshUrl);
            var redirectUrl = $('#redirectUrl').val();
            if (redirectUrl) {
                var action = $('#actionString').val();
                $.ajax({ 
                    url: redirectUrl
                }).done(function (data) {
                    $('#actionInputsDiv').html(data);
                    yukon.ui.initContent(actionInputsDiv);
                    if (action == 'Device Configs') {
                        yukon.bulk.device.configs.init();
                    } else if (action == 'Mass Change') {
                        yukon.bulk.masschange.init();
                    }
                    $('.js-action-separator').removeClass('dn');
                    $('.js-action').html(action);
                    $('#collectionActionsAccordion').accordion("option", "active", 2);
                });
            }
        },

        updateFileNote : function (id) {
            var selection = $('#' + id + '_uploadType').val();
            $('.js-note-address').toggleClass('dn', selection != 'ADDRESS');
            $('.js-note-pao').toggleClass('dn', selection != 'PAONAME');
            $('.js-note-device').toggleClass('dn', selection != 'DEVICEID');
            $('.js-note-meter').toggleClass('dn', selection != 'METERNUMBER');
            $('.js-note-bulk').toggleClass('dn', selection != 'BULK');
        },

        toggleByAddrPopup : function (id) {
            mod.clearErrors();
            mod.clearAddrFields();
            mod.togglePopup(id);
            if ($('#' + id).is(':visible')) { 
                $('#' + id + '_startRange').focus();
            }
        },

        clearAddrFields : function (id) {
            mod.clearErrors();
            $('#' + id + '_startRange').val('');
            $('#' + id + '_endRange').val('');
        },

        toggleByFileUploadPopup : function (id) {
            mod.togglePopup(id);
            if ($('#' + id).is(':visible')) {
                $('#' + id + '_uploadType')[0].options[0].selected = true;
                mod.updateFileNote(id);
                $('#' + id + '_uploadType').focus();
            }
        },

        togglePopup : function (id) {
            var popupElem = $('#' + id);
            if (popupElem.is(':visible')) {
                popupElem.dialog('close');
            } else {
                popupElem.dialog('open');
            }
        },

        clearErrors : function () {
            $('.undefinedStartAddress, .undefinedEndAddress, .lessThanZero, .outOfRange, .startTooHigh, .endTooHigh').removeClass('error');
            $('.rangeMsg').hide();
        },

        validateAddressRange : function () {
            mod.clearErrors();
            var start = parseInt($('#byAddrPopup_startRange').val()),
                end = parseInt($('#byAddrPopup_endRange').val()),
                errors = [],
                MAX_INT = 2147483648; //Maximum 32-bit integer, which is how the range values are eventually interpreted
            
            //separate errors
            if (isNaN(start)) {
                errors.push('.undefinedStartAddress');
            }
            
            if (isNaN(end)) {
                errors.push('.undefinedEndAddress');
            }
            
            if (start < 0) {
                errors.push('.lessThanZero');
            }
            
            if (start > MAX_INT) {
                errors.push('.startTooHigh');
            }
            
            if (end > MAX_INT) {
                errors.push('.endTooHigh');
            }
                  
            if (start > end) {
                errors.push('.outOfRange');
            }
            
            return errors;
        },
        
        /** Initialize this module. */
        init: function () {
                        
            if (_initialized) return;
            
            _blockOnSubmit = $('#blockOnSubmit').val();
            _noGroupSelectedText = $('#noGroupSelectedText').val();
            _eventAfterSubmit = $('#eventAfterSubmit').val();
            
            /** By Device Functionality */
            $(document).on('click', '.js-device', function () {
                selectDevicesPicker.clearEntireSelection.call(selectDevicesPicker);
                selectDevicesPicker.show.call(selectDevicesPicker);            
            });
            
            $(document).on('yukon:ui:bulk:device:selection:selectDevices', function (ev, devices, picker) {
                var ids = [];
                
                // Get the id out of each of the selected devices
                for (var i = 0; i < devices.length; i++) {
                    ids[i] = devices[i].paoId;
                }
                
                $('#deviceIds').value = ids;
                if (_blockOnSubmit) {
                    yukon.ui.blockPage();
                }
                var form = $('#selectDevicesForm');
                if (_eventAfterSubmit) {
                    form.ajaxSubmit({
                        success: function(data, status, xhr, $form) {
                            mod.updateCollectionActions(data);
                        }
                    });
                } else {
                    form.submit();
                }
                return true;
            });
            
            /** By Address Range Functionality */
            $(document).on('click', '.js-address-range', function () {
                mod.toggleByAddrPopup('byAddrPopup');
            });
            
            $('#addByAddressForm input:text').keyup(function(e) {
                $(e.currentTarget).val($(e.currentTarget).val().replace(/\D/g, ''));
            });
            
            $(document).on('click', '#addByAddressForm button', function (e) {                
                var errors = mod.validateAddressRange();
                if (errors.length === 0) {
                    //submit the form
                    var form = $('#addByAddressForm');
                    if (_eventAfterSubmit) {
                        form.ajaxSubmit({
                            success: function(data, status, xhr, $form) {
                                mod.updateCollectionActions(data);
                            }
                        });
                    } else {
                        form.submit();
                    }
                } else {
                    //show the error
                    $(errors.join(',')).show().addClass('error');
                    yukon.ui.unbusy(e.currentTarget);
                }
            });
            
            $('div#byAddrPopup').on('dialogclose', function(event) {
                mod.clearAddrFields('byAddrPopup');
            });
            
            /** By File Upload Functionality */
            $(document).on('click', '.js-file-upload', function () {
                mod.toggleByFileUploadPopup('byFileUpload');
            });
            
            $(document).on('change', '#byFileUpload_uploadType', function () {
                mod.updateFileNote('byFileUpload');
            });
            
            $(document).on('click', '.js-file-upload-submit', function () {
                //submit the form
                var form = $('#addByFileUploadForm');
                if (_eventAfterSubmit) {
                    form.ajaxSubmit({
                        success: function(data, status, xhr, $form) {
                            yukon.ui.unbusy('.js-file-upload-submit');
                            $('#byFileUpload').dialog('close');
                            if (data.errorMsg) {
                                yukon.ui.alertError(data.errorMsg);
                            } else {
                                mod.updateCollectionActions(data);
                            }
                        }
                    });
                } else {
                    form.submit();
                }            
            });
                                                
            _initialized = true;
        }
        
    };
    
    return mod;
})();

$(function () { yukon.ui.bulk.device.selection.init(); });