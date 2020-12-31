yukon.namespace('yukon.widget.csrTrend');

/**
 * Module for the CSR Trend Widget.
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
            
            $(document).on('click', '.js-help-icon', function () {
                var popupId = $(this).data('popup'),
                    popup = $(popupId),
                    titleContainer = $(this).closest('.titled-container'),
                    helpText = titleContainer.find('.js-trend-help-text');
                if (helpText.exists()) {
                    popup.find('.scroll-lg').html(helpText.val());
                }
            });

            $('.js-temperature-checkbox').click(function() {
                var widgetId = $(this).closest('.widgetWrapper').attr('id'),
                    widgetId = widgetId.substring(widgetId.indexOf("_") + 1);
                    widget = yukon.widgets[widgetId];
                widget.doDirectActionRefresh('render?isTemperatureChecked=' + this.checked);
            });
            
            _initialized = true;
        }
    };
    
    return mod;
    
})();

$(function () { yukon.widget.csrTrend.init(); });