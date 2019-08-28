yukon.namespace('yukon.bulk.meterProgramming');
/**
 * Module for the Collection Actions Meter Programming page
 * @module yukon.bulk.meterProgramming
 * @requires JQUERY
 * @requires yukon
 */
yukon.bulk.meterProgramming = (function () {
    

    'use strict';
    var _initialized = false,

    enableDisableProgramming = function () {

        var toggle = $('.js-programming-type'),
            configTypeRow = toggle.closest('tr'),
            newConfig = configTypeRow.find('.switch-btn-checkbox').prop('checked');
        if (newConfig) {
            $('.js-new-configuration').toggleClass('dn', false);
        } else {
            $('.js-new-configuration').toggleClass('dn', true);
        }
    };


    mod = {

            /** Initialize this module. */
            init: function () {
                
                enableDisableProgramming();
                
                if (_initialized) return;

//              show/hide information based on New or Existing Configuration selection
                $(document).on('click', '.js-programming-type', function () {
                    enableDisableProgramming();
                });

                _initialized = true;

            }

    };

    return mod;

})();

$(function () { yukon.bulk.meterProgramming.init(); });