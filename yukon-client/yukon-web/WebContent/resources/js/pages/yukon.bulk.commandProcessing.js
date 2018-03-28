yukon.namespace('yukon.bulk.commandProcessing');
/**
 * Module for the Collection Actions Send Command
 * @module yukon.bulk.commandProcessing
 * @requires JQUERY
 * @requires yukon
 */
yukon.bulk.commandProcessing = (function () {

    'use strict';
    var initialized = false;

    mod = {

            /** Initialize this module. */
            init: function () {

                if (initialized) return;
                
                $(document).on('click', '.js-execute-command', function () {
                    //set dropdown value
                    $('#commandFromDropdown').val($('#commandSelectId option:selected').text());
                    $('#collectionProcessingForm').submit();
                });
                
                initialized = true;
                
            }

    };

    return mod;

})();

$(function () { yukon.bulk.commandProcessing.init(); });