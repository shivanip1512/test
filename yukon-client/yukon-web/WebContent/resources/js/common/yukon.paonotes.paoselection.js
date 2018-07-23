yukon.namespace('yukon.paonotes.paoselection.js');
 
/**
 * TODO Change this.
 * Module to handle pao notes search widget functionality.
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
    
    _togglePickerDisplay = function (container) {
        var selectDevices = container.find('.js-select-devices').val(),
            pickerDialog = container.find('.js-picker-dialog'),
            deviceGroupPicker = container.find('.js-device-group-picker-for-notes'),
            identifier = container.find('.js-unique-identifier').val();
        if (selectDevices === 'allDevices') {
            pickerDialog.addClass('dn');
            deviceGroupPicker.addClass('dn');
            _clearIndividualDeviceSelection(pickerDialog, identifier);
            _clearDeviceGroupSelection(deviceGroupPicker);
            $("#js-clear-device-group-selection_" + identifier).val('CLEAR_SELECTION');
        } else if (selectDevices === 'selectIndividually') {
            pickerDialog.removeClass('dn');
            deviceGroupPicker.addClass('dn');
            _clearDeviceGroupSelection(deviceGroupPicker);
            $("#js-clear-device-group-selection_" + identifier).val('CLEAR_SELECTION');
        } else if (selectDevices === 'byDeviceGroups') {
            _clearIndividualDeviceSelection(pickerDialog, identifier);
            pickerDialog.addClass('dn');
            deviceGroupPicker.removeClass('dn');
        }
    },
    
    mod = {
        
        /** Initialize this module. */
        init : function () {
            
            if (_initialized) return;
            
            $(document).on('change', '.js-select-devices', function () {
                var container = $(this).closest('.js-pao-selection-container');
                _togglePickerDisplay(container);
            });
            
            
            $(document).find('.js-pao-selection-container').each(function () {
                _togglePickerDisplay($(this));
            });
            
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
            
            _initialized = true;
        }
    
    };
 
    return mod;
})();
 
$(function () { yukon.paonotes.paoselection.init(); });