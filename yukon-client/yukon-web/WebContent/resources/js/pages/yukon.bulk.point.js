yukon.namespace('yukon.bulk.point');
/**
 * Module for the Collection Actions Add/Update/Remove Points
 * @module yukon.bulk.point
 * @requires JQUERY
 * @requires yukon
 */
yukon.bulk.point = (function () {

    'use strict';
    var initialized = false,

    mod = {

            /** Initialize this module. */
            init: function () {

                if (initialized) return;
                
                //check any preselectedPointIdentifiers (used when coming from Device Data Monitors)
                $("[name='preselectedPointIdentifiers']").each(function () {
                    var pointTypeSelector = $(this).val(),
                        checkboxes = $("[name$='" + pointTypeSelector + "']");
                    checkboxes.attr("checked", "checked");
                    checkboxes.closest('td').flash();
                });
                
                $(document).on('change', '#sharedPoints', function () {
                    var sharedPoints = $('#sharedPoints').val();
                    $('#sharedPointsDiv').toggleClass('dn', !sharedPoints);
                    $('#allPointsDiv').toggleClass('dn', sharedPoints);
                });
                
                initialized = true;
                
            }

    };

    return mod;

})();

$(function () { yukon.bulk.point.init(); });