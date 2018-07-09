yukon.namespace('yukon.tools.paonotessearch.js');
 
/** 
 * Module to handle pao notes search functionality.
 * 
 * @module yukon.tools.paonotessearch 
 * @requires JQUERY 
 * @requires yukon 
 */
yukon.tools.paonotessearch = (function () {
 
    'use strict';
 
    var
    _initialized = false,
    
    _togglePickerDisplay = function () {
        var selectDevices = $('#js-select-devices').val();
        
        if (selectDevices === 'allDevices') {
            $('#js-picker-dialog').addClass('dn');
            $('#js-device-group-picker').addClass('dn');
            $('input[name="deviceGroupName"]').val('');
            $("#js-note-create-dates").removeAttr("style");
            $("#js-note-create-by").css({"margin-left":"6%", "margin-top":"1%"});
            $("#js-picker-dialog #picker-paoPicker-input-area").find('input[type="hidden"][name="paoIds"]').remove();
            $("#js-device-group-picker").find("input[name='deviceGroupNames']").val('');
        } else if (selectDevices === 'selectIndividually') {
            $('#js-picker-dialog').removeClass('dn');
            $('#js-device-group-picker').addClass('dn');
            $('input[name="deviceGroupName"]').val('');
            $("#js-note-create-dates").css({"margin-left":"6%", "margin-top":"1%"});
            $("#js-note-create-by").removeAttr("style");
            $("#js-device-group-picker").find("input[name='deviceGroupNames']").val('');
        } else if (selectDevices === 'byDeviceGroups') {
            $('#js-picker-dialog').addClass('dn');
            $('#js-device-group-picker').removeClass('dn');
            $("#js-note-create-dates").css({"margin-left":"6%", "margin-top":"1%"});
            $("#js-note-create-by").removeAttr("style");
            $("#js-picker-dialog #picker-paoPicker-input-area").find('input[type="hidden"][name="paoIds"]').remove();
        }
    },
 
    mod = {
 
        /** Initialize this module. */
        init: function () {
 
            if (_initialized) return;
            
            $(document).on('change', '#js-select-devices', function () {
                _togglePickerDisplay();
            });
            
            $(document).on('click', '.js-download', function () {
                var form = $('#filter-pao-notes-form');
                var data = form.serialize();
                window.location = yukon.url('/tools/paoNotes/download?' + data);
            });

            _togglePickerDisplay();
            
            _initialized = true;
        }
 
    };
 
    return mod;
})();
 
$(function () { yukon.tools.paonotessearch.init(); });