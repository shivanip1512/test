yukon.namespace('yukon.ui.numeric');

/**
 * Module that handles the JS validation for the numeric tag
 * @module yukon.ui.numeric
 * @requires JQUERY
 * @requires yukon
 */
yukon.ui.numeric= (function () {
    
    'use strict';
    
    var
    _initialized = false,
    
    mod = {
        
        /** Initialize this module. */
        init: function () {

            if (_initialized) return;
            
            $('.js-numeric').each(function(idx, elm) {
                var minValue = $(this).data('minValue'),
                    maxValue = $(this).data('maxValue'),
                    stepValue = $(this).data('stepValue'),
                    numberFormat = $(this).data('isDecimalNumber') ? "n" : null;
            
                    $(this).spinner({
                        min: minValue,
                        max: maxValue,
                        step: stepValue ? stepValue : 1,
                        numberFormat: numberFormat
                    });
            });
            
            _initialized = true;
        }
        
    };
    
    return mod;
})();

$(function () { yukon.ui.numeric.init(); });