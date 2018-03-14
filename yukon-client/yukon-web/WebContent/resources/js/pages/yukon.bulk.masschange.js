yukon.namespace('yukon.bulk.masschange');
/**
 * Module for the Collection Actions Mass Change
 * @module yukon.bulk.masschange
 * @requires JQUERY
 * @requires yukon
 */
yukon.bulk.masschange = (function () {

    'use strict';
    var initialized = false,

    mod = {

            /** Initialize this module. */
            init: function () {

                if (initialized) return;
                
                $(document).on('click', '.js-mass-change', function () {
                    var selectMassChangeField = $(this).data('field');
                    $('#massChangeBulkFieldName').val(selectMassChangeField);
                    $('#massChangeSelectForm').submit();
                });
                
                initialized = true;
                
            }

    };

    return mod;

})();

$(function () { yukon.bulk.masschange.init(); });