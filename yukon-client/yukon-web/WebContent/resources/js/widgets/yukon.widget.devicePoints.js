yukon.namespace('yukon.widget.devicePoints');

/**
 * Module that handles the behavior on the Device Points widget
 * @module yukon.widget.devicePoints
 * @requires JQUERY
 * @requires yukon
 */
yukon.widget.devicePoints = (function() {
    
    'use strict';
    
    var
    _initialized = false,

    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
            $("#pointTypeSelector").chosen({width: "250px"});
            
            /** User filtered the Points */
            $(document).on('click', '.js-filter', function () {
                var tableContainer = $('.js-points-table'),
                    form = $('#devicePointsForm');
                form.ajaxSubmit({
                    success: function(data) {
                        tableContainer.html(data);
                        tableContainer.data('url', yukon.url('/widget/devicePointsWidget/pointsTable?' + form.serialize()));
                    }
                });
            });
            
            $(document).on('click', '.js-download', function () {
                var form = $('#devicePointsForm'),
                    data = form.serialize();
                window.location = yukon.url('/widget/devicePointsWidget/download?' + data);
            });
            
            _initialized = true;
        }
        
    };
    
    return mod;
})();

$(function () { yukon.widget.devicePoints.init(); });