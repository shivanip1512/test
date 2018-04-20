yukon.namespace('yukon.bulk.route.locate');

/**
 * Module for the Route Locate Collection Action
 * @module yukon.bulk.route.locate
 * @requires JQUERY
 * @requires yukon
 */
yukon.bulk.route.locate = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    
    mod = {
        
        /** Initialize this module. */
        init : function () {
            
            if (_initialized) return;
            
            $(document).on('click', '.js-set-route', function () {
                var deviceId = $(this).data('deviceId'),
                    routeId = $(this).data('routeId'),
                    rowId = $(this).data('rowId'),
                    params = {
                        deviceId: deviceId,
                        routeId: routeId
                    };
                $.post(yukon.url('/bulk/routeLocate/setRoute'), params)
                .done(function (result) {
                    $('#' + rowId).html(result);
                });
                
            });
            
            $(document).on('click', '.js-locate-route', function () {
                //set dropdown value
                $('#commandFromDropdown').val($('#commandSelectId option:selected').text());
                $('#executeLocateForm').submit();
            });

            _initialized = true;
        }
        
    };
    
    return mod;
})();

$(function () { yukon.bulk.route.locate.init(); });