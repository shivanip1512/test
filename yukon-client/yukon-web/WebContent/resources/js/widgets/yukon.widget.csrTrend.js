yukon.namespace('yukon.widget.csrTrend');

/**
 * Module for the Asset Availability Widget.
 * @module yukon.widget.csrTrend
 * @requires JQUERY
 * @requires yukon
 */
yukon.widget.csrTrend = (function () {
    
    'use strict';
    
    var
    _initialized = false,

    mod = {
        
        /** Initialize this module. */
        init : function () {
            
            if (_initialized) return;
            
            $(document).on('dialogopen', function (event, ui) {
                var id = $(event.target).attr('id');
                if (id.indexOf('box-container-info-popup-widget-titled-container-widget_') === 0) {
                    var tokens = id.split('_');
                    if ($('#widgetWrapper_widget_' + tokens[1]).exists() && $('#widgetWrapper_widget_' + tokens[1]).find('table.js-trend').exists()) {
                        $(this).find('.scroll-lg').html($('#widgetWrapper_widget_' + tokens[1]).find('.js-help-text').val())
                    }
                }
            });
            
            _initialized = true;
        }
    };
    
    return mod;
    
})();

$(function () { yukon.widget.csrTrend.init(); });