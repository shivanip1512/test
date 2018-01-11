yukon.namespace('yukon.assets.rtu');

/**
 * Module that handles the behavior on the RTU Details page
 * @module yukon.assets.rtu
 * @requires JQUERY
 * @requires yukon
 */
yukon.assets.rtu= (function () {
    
    'use strict';
    
    var
    _initialized = false,
    
    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
            /** User clicked on one of the show hide buttons on the Child Hierarchy tab */
            $(document).on('click', '.js-show-hide', function () {
                var paoId = $(this).attr('data-paoId');
                $.ajax({ url: yukon.url('/stars/rtu/child/' + paoId + '/points') })
                .done(function (details) {
                    $('.js-points-' + paoId).html(details);
                });
            });
            
            _initialized = true;
        }
        
    };
    
    return mod;
})();

$(function () { yukon.assets.rtu.init(); });