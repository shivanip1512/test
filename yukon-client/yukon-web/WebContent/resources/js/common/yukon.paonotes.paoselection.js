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
    
    mod = {
        
        /** Initialize this module. */
        init : function () {
            
            if (_initialized) return;
            
            $(document).on('click', '.js-device-selection', function (event) {
                var container = $(this).closest('.js-pao-selection-container'),
                    deviceGroupBtn = container.find('.js-select-by-device-group'),
                    selectedBtn = container.find('.js-select-individually'),
                    allBtn = container.find('.js-all-devices'),
                    pickerDialog = container.find('.js-picker-dialog'),
                    uniqueIdentifier = container.find('.js-unique-identifier').val(),
                    deviceGroupPicker = container.find('.js-device-group-picker-container');
                
                if (deviceGroupBtn.hasClass('on')) {
                    container.find("input[name='paoSelectionMethod']").val($(".js-byDeviceGroup-enum-val").val());
                } else if (selectedBtn.hasClass('on')) {
                    container.find("input[name='paoSelectionMethod']").val($(".js-selectIndividually-enum-val").val());
                } else {
                    container.find("input[name='paoSelectionMethod']").val($(".js-allDevices-enum-val").val());
                }
            });
            
            $('.js-pao-notes-search-widget').each(function (index, item) {
                $(item).find('.js-pao-selection-container').css('margin-bottom', '5px');
                $(item).find('.js-pao-selection-container .js-picker-dialog').removeClass('dib');
                $(item).find('.js-pao-selection-container .js-picker-dialog').addClass('db');
                $(item).find('.js-pao-selection-container .js-device-group-picker-container span.fl').css('margin-left', '6px');
            });
            
            if($("#deviceGroups\\.errors").exists()) {
                $("#deviceGroups\\.errors").addClass('db');
            }
            if($("#paoIds\\.errors").exists()) {
                $( "<br>" ).insertBefore( "#paoIds\\.errors");
            }
            _initialized = true;
        }
    
    };
 
    return mod;
})();
 
$(function () { yukon.paonotes.paoselection.init(); });