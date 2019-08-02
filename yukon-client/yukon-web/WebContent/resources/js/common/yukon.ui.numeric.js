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
                    stepValue = $(this).data('stepValue');            
                $(this).spinner({
                    min: minValue,
                    max: maxValue,
                    step: stepValue
                });
            });
            
            $(document).on('blur', '.js-numeric', function (event) {
                var value = $(this).val(),
                    id = $(this).attr('id'),
                    minValue = $(this).data('minValue'),
                    maxValue = $(this).data('maxValue'),
                    stepValue = $(this).data('stepValue');
                $(this).removeClass('error');
                $('.js-' + id + '-min-value-error').addClass('dn');
                $('.js-' + id + '-max-value-error').addClass('dn');
                //verify the number is in between range
                if (minValue != null) {
                    if (value < minValue) {
                        $(this).addClass('error');
                        $('.js-' + id + '-min-value-error').removeClass('dn');
                    }
                }
                if (maxValue != null) {
                    if (value > maxValue) {
                        $(this).addClass('error');
                        $('.js-' + id + '-max-value-error').removeClass('dn');
                    }
                }
            });
                                                
            _initialized = true;
        }
        
    };
    
    return mod;
})();

$(function () { yukon.ui.numeric.init(); });