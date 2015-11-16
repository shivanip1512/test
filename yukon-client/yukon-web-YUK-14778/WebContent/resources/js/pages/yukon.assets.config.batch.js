yukon.namespace('yukon.assets.config.batch');

/**
 * Module for the 'Save to File' inventory collection action
 * @module yukon.assets.config.batch
 * @requires JQUERY
 * @requires yukon
 */
yukon.assets.config.batch = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    
    mod = {
        
        /** Initialize this module. */
        init : function () {
            
            if (_initialized) return;
            
            var singleConfigType = $('[data-single-config]').data('singleConfig');
            
            $(document).on('change', '.js-routes input:radio', function (ev) {
                
                var container = $(this).closest('div');
                
                container.siblings().addClass('disabled')
                .find('input[type!=radio], select, textarea, button').attr('disabled', 'disabled');
                
                container.removeClass('disabled')
                .find('input, select, textarea, button').removeAttr('disabled');
            });
            
            $('.js-routes input:radio:checked').trigger('change');
            
            if (!singleConfigType) {
                $('.js-groups').addClass('disabled')
                .find(':input').attr('disabled', 'disabled');
            } else {
                $(document).on('change', '.js-groups input:radio', function (ev) {
                    
                    var container = $(this).closest('div');
                    
                    container.siblings().addClass('disabled')
                    .find('input[type!=radio], select, textarea, button').attr('disabled', 'disabled');
                    
                    container.removeClass('disabled')
                    .find('input, select, textarea, button').removeAttr('disabled');
                });
                
                $('.js-groups input:radio:checked').trigger('change');
            }
            
            _initialized = true;
        }
        
    };
    
    return mod;
})();

$(function () { yukon.assets.config.batch.init(); });