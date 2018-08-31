yukon.namespace('yukon.selectdevicesindividuallyorbygroup');
 
/**
 * Module to handle device selection functionality when using selectDevicesIndividuallyOrByGroup.tag file.
 * 
 * @module yukon.selectdevicesindividuallyorbygroup
 * @requires yukon
 * @requires JQUERY
 */
yukon.selectdevicesindividuallyorbygroup = (function () {
    
    'use strict';
 
    var
    _initialized = false,
    
    _togglePickers = function (element) {
        var container = element.closest('.js-pao-selection-container'),
            deviceGroupBtn = container.find('.js-select-by-device-group'),
            selectedBtn = container.find('.js-select-individually'),
            allBtn = container.find('.js-all-devices');

        container.find('.js-device-group-picker-container').toggleClass('dn', !deviceGroupBtn.hasClass('on'));
        container.find('.js-picker-dialog').toggleClass('dn', !selectedBtn.hasClass('on'));
    },

    mod = {
        
        /** Initialize this module. */
        init : function () {

            $(document).on('click', '.js-device-selection', function (event) {
                _togglePickers($(this));
            });

            $('.js-pao-selection-container').each(function (idx, item) {
                var btn = $(item).find('button.on'),
                    devicePicker = $(item).find('.js-picker-dialog'),
                    deviceGroupPicker = $(item).find('.js-device-group-picker-container');

                _togglePickers(btn);
                devicePicker.find('span.b-label').css("maxWidth", "232px");
                deviceGroupPicker.find('span.fl').css("maxWidth", "228px");
            });
            
            _initialized = true;
        }
    
    };
 
    return mod;
})();
 
$(function () { yukon.selectdevicesindividuallyorbygroup.init(); });