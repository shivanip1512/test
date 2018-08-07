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
            deviceGroupBtn = container.find('input[name^="js-select-by-device-group_"]'),
            selectedBtn = container.find('input[name^="js-select-individually_"]');
        
        container.find('.js-device-group-picker-container').toggleClass('dn', !deviceGroupBtn.is(':checked'));
        container.find('.js-picker-dialog').toggleClass('dn', !selectedBtn.is(':checked'));
    }
    
    mod = {
        
        /** Initialize this module. */
        init : function () {
            
            $(document).on('change', 'input[name^="js-select-by-device-group_"]', function (event) {
                var container = $(this).closest('.js-pao-selection-container');
                container.find('input[name^="js-select-individually_"]').prop('checked', false);
                _togglePickers($(this));
            });
            
            $(document).on('change', 'input[name^="js-select-individually_"]', function (event) {
                var container = $(this).closest('.js-pao-selection-container');
                container.find('input[name^="js-select-by-device-group_"]').prop('checked', false);
                _togglePickers($(this));
            });
            
            _initialized = true;
        }
    
    };
 
    return mod;
})();
 
$(function () { yukon.selectdevicesindividuallyorbygroup.init(); });