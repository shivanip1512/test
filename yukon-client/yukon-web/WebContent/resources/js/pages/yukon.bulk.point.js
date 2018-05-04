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
   
   enableDisableActionButton = function () {
       var inputsDiv = $('#actionInputsDiv'),
           pointsVisible = $('.js-point-checkbox').length,
           numPointsChecked = $('.js-point-checkbox:checked').length;
       if (pointsVisible > 0) {
           inputsDiv.find('.js-action-submit').prop("disabled", numPointsChecked == 0);
       }
   }

    mod = {

            /** Initialize this module. */
            init: function () {
                
                showHideAllPoints();
                enableDisableActionButton();
                
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
                
                $(document).on('click', '.js-point-checkbox', function () {
                    enableDisableActionButton();
                });
                
                initialized = true;
                
            }

    };

    return mod;

})();

$(function () { yukon.bulk.point.init(); });