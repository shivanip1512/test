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
    
    enableDisable = function () {
        var selectMassChangeField = $('input[name=field]:checked').val();
        $('#massChangeBulkFieldName').val(selectMassChangeField);
        $('#massChangeSelectForm').ajaxSubmit({
            success : function(data, status, xhr, $form) {
                $('#actionInputsDiv2').html(data);
            }
        });
    };

    mod = {

        /** Initialize this module. */
        init: function () {

            enableDisable();
            
            $(document).on('click', '.js-mass-change', function () {
                enableDisable();
            });
            
            initialized = true;
            
        }

    };

    return mod;

})();

$(function () { yukon.bulk.masschange.init(); });