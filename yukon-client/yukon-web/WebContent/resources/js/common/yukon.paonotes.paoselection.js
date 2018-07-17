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
    
    _clearIndividualDeviceSelection = function () {
        yukon.pickers['paoPicker'].clearEntireSelection();
        var pickerBtn = $("#js-picker-dialog").find("#picker-paoPicker-btn");
        pickerBtn.find('span.b-label').text(pickerBtn.attr('aria-label'));
    },
    
    _clearDeviceGroupSelection = function () {
        $('input[name="deviceGroupName"]').val('');
        var deviceGroupPicker = $("#js-device-group-picker");
        deviceGroupPicker.find("input[name='deviceGroupNames']").val('');
        deviceGroupPicker.find("span.fl").text(deviceGroupPicker.find("a.js-device-group-picker").data("select-text"));
    },
    
    _togglePickerDisplay = function () {
        var selectDevices = $('#js-select-devices').val();
        if (selectDevices === 'allDevices') {
            $('#js-picker-dialog').addClass('dn');
            $('#js-device-group-picker').addClass('dn');
            _clearIndividualDeviceSelection();
            _clearDeviceGroupSelection();
        } else if (selectDevices === 'selectIndividually') {
            $('#js-picker-dialog').removeClass('dn');
            $('#js-device-group-picker').addClass('dn');
            _clearDeviceGroupSelection();
        } else if (selectDevices === 'byDeviceGroups') {
            _clearIndividualDeviceSelection();
            $('#js-picker-dialog').addClass('dn');
            $('#js-device-group-picker').removeClass('dn');
        }
    },
    
    mod = {
        
        /** Initialize this module. */
        init : function () {
            
            if (_initialized) return;
            
            $(document).on('change', '#js-select-devices', function () {
                _togglePickerDisplay();
            });
            
            _togglePickerDisplay();
            
            _initialized = true;
        }
    
    };
 
    return mod;
})();
 
$(function () { yukon.paonotes.paoselection.init(); });