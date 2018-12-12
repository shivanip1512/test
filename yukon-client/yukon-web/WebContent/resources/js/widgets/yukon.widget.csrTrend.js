yukon.namespace('yukon.widget.csrTrend');

/**
 * Module for the Trend Widget.
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
                // get the id of the dialog opened.
                var id = $(event.target).attr('id');
                
                // check if the id has a substring mentioned below
                if (id.indexOf('box-container-info-popup-widget-titled-container-widget_') === 0) {
                    // derive the widget id from this string. The widget id is appended after the _.
                    var tokens = id.split('_');
                    
                    // check if dialog opened is help text dialog for the Trend widget. 
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