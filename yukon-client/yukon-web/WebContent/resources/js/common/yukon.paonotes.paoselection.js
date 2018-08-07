yukon.namespace('yukon.paonotes.paoselection');
 
/**
 * Module to handle pao selection functionality for notes search.
 * 
 * @module yukon.paonotes.paoselection
 * @requires yukon
 * @requires JQUERY
 */
yukon.paonotes.paoselection = (function () {
    
    'use strict';
 
    var
    _initialized = false,
    
    _clearIndividualDeviceSelection = function (pickerDialog, identifier) {
        yukon.pickers['paoPicker_' + identifier].clearEntireSelection();
        pickerDialog.find('input[type="hidden"][name="paoIds"]').remove();
        var pickerBtn = pickerDialog.find("#picker-paoPicker_" + identifier +"-btn");
        pickerBtn.find('span.b-label').text(pickerBtn.attr('aria-label'));
        $("#picker-paoPicker_" + identifier +"-show-selected-icon").addClass('dn');
    },
    
    _clearDeviceGroupSelection = function (deviceGroupPicker) {
        deviceGroupPicker.find("input[name='deviceGroupNames']").val('');
        deviceGroupPicker.find("span.fl").text(deviceGroupPicker.find("a.js-device-group-picker").data("select-text"));
    },
    
    mod = {
        
        /** Initialize this module. */
        init : function () {
            
            if (_initialized) return;
            
            $(document).on('yukon:paonotessearch:paosselected', function (ev, items, picker) {
                var tokens = picker.id.split('_');
                $("#picker-paoPicker_" + tokens[1] +"-show-selected-icon").toggleClass('dn', tokens.length === 0);
            });
            
            $(document).on('dialogopen', '.js-device-group-picker-dialog', function (event) {
                var id = event.target.id,
                    tokens = id.split('_');
                if (id.contains('js-paonotes-device-group-picker') && 
                        $("#js-clear-device-group-selection_" + tokens[1]).val() === 'CLEAR_SELECTION') {
                    var dialog = $(this),
                        selected = dialog.data('selected'),
                        tree = dialog.find('.tree-canvas').dynatree('getTree');
                    
                    tree.visit(function (node) {
                        node.select(false);
                    });
                    $("#js-clear-device-group-selection_" + tokens[1]).val('');
                }
            });
            
            $(document).on('change', 'input[name^="js-select-by-device-group_"], input[name^="js-select-individually_"]', function (event) {
                var container = $(this).closest('.js-pao-selection-container'),
                    deviceGroupBtn = container.find('input[name^="js-select-by-device-group_"]'),
                    selectedBtn = container.find('input[name^="js-select-individually_"]'),
                    allDeviceLbl= container.find('.js-all-devices-lbl'),
                    pickerDialog = container.find('.js-picker-dialog'),
                    uniqueIdentifier = container.find('.js-unique-identifier').val(),
                    deviceGroupPicker = container.find('.js-device-group-picker-container');
                allDeviceLbl.toggleClass('dn', deviceGroupBtn.is(':checked') || selectedBtn.is(':checked'));
                
                if (deviceGroupBtn.is(':checked')) {
                    container.find("input[name='paoSelectionMethod']").val($(".js-byDeviceGroup-enum-val").val());
                    _clearIndividualDeviceSelection(pickerDialog, uniqueIdentifier);
                } else if (selectedBtn.is(':checked')) {
                    container.find("input[name='paoSelectionMethod']").val($(".js-selectIndividually-enum-val").val());
                    _clearDeviceGroupSelection(deviceGroupPicker);
                    $("#js-clear-device-group-selection_" + uniqueIdentifier).val('CLEAR_SELECTION');
                } else {
                    container.find("input[name='paoSelectionMethod']").val($(".js-allDevices-enum-val").val());
                    _clearIndividualDeviceSelection(pickerDialog, uniqueIdentifier);
                    _clearDeviceGroupSelection(deviceGroupPicker);
                    $("#js-clear-device-group-selection_" + uniqueIdentifier).val('CLEAR_SELECTION');
                }
            });
            
            $('.js-pao-selection-container').each(function (idx, item) {
                var paoSelectionMethod = $(item).find("input[name='paoSelectionMethod']").val(),
                    byDeviceGroupVal = $(item).find(".js-byDeviceGroup-enum-val").val(),
                    selectIndividually = $(item).find(".js-selectIndividually-enum-val").val(),
                    allDeviceVal = $(item).find(".js-allDevices-enum-val").val();
                if (paoSelectionMethod === byDeviceGroupVal) {
                    $(item).find('input[name^="js-select-by-device-group_"]').prop('checked', true);
                    $(item).find('.js-device-group-picker-container').removeClass('dn');
                    $(item).find('.js-all-devices-lbl').addClass('dn');
                } else if (paoSelectionMethod === selectIndividually) {
                    $(item).find('input[name^="js-select-individually_"]').prop('checked', true);
                    $(item).find('.js-picker-dialog').removeClass('dn');
                    $(item).find('.js-all-devices-lbl').addClass('dn');
                } 
            });
            
            if($("#deviceGroups\\.errors").exists()) {
                $("#deviceGroups\\.errors").addClass('db');
            }
            _initialized = true;
        }
    
    };
 
    return mod;
})();
 
$(function () { yukon.paonotes.paoselection.init(); });