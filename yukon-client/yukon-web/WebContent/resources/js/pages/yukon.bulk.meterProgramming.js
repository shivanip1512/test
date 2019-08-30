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
            programTypeRow = toggle.closest('tr'),
            newProgram = programTypeRow.find('.switch-btn-checkbox').prop('checked');
        if (newProgram) {
            $('.js-new-program').toggleClass('dn', false);
        } else {
            $('.js-new-program').toggleClass('dn', true);
        }
    };


    mod = {

            /** Initialize this module. */
            init: function () {
                
                enableDisableProgramming();
                
                if (_initialized) return;

//              show/hide information based on New or Existing Program selection
                $(document).on('click', '.js-programming-type', function () {
                    enableDisableProgramming();
                });

                _initialized = true;

            }

    };

    return mod;

})();

$(function () { yukon.bulk.meterProgramming.init(); });