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
                var paoId = $(this).attr('data-paoId'),
                    pointsDiv = $('.js-points-' + paoId),
                    childPointsDiv = pointsDiv.find('.js-child-points'),
                    borderedDiv = pointsDiv.find('.bordered-div'),
                    icon = $(this).find('i');
                //check if points are already visible
                if (borderedDiv.is(":visible")) {
                    childPointsDiv.addClass('dn');
                    icon.removeClass('icon-collapse');
                    icon.addClass('icon-expand');
                } else {
                    $.ajax({ url: yukon.url('/stars/rtu/child/' + paoId + '/points') })
                    .done(function (details) {
                        pointsDiv.html(details);
                        icon.removeClass('icon-expand');
                        icon.addClass('icon-collapse');
                    });
                }                
            });
            
            _initialized = true;
        }
        
    };
    
    return mod;
})();

$(function () { yukon.assets.rtu.init(); });