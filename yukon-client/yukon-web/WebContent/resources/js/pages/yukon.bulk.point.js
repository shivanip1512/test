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
    
    showHideAllPoints = function () {
        var sharedPoints = $('#sharedPoints').val();
        if (sharedPoints == 'true') {
            $('#sharedPointsDiv').removeClass('dn');
            $('#allPointsDiv').addClass('dn');
        } else {
            $('#sharedPointsDiv').addClass('dn');
            $('#allPointsDiv').removeClass('dn');
        }
   },

    mod = {

            /** Initialize this module. */
            init: function () {
                
                showHideAllPoints();
                
                //check any preselectedPointIdentifiers (used when coming from Device Data Monitors)
                $("[name='preselectedPointIdentifiers']").each(function () {
                    var pointTypeSelector = $(this).val(),
                        checkboxes = $("[name$='" + pointTypeSelector + "']");
                    checkboxes.attr("checked", "checked");
                    checkboxes.closest('td').flash();
                });
                
                $(document).on('change', '#sharedPoints', function () {
                    showHideAllPoints();
                });
                
                initialized = true;
                
            }

    };

    return mod;

})();

$(function () { yukon.bulk.point.init(); });