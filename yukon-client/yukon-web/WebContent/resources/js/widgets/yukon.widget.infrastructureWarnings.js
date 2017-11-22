yukon.namespace('yukon.widget.infrastructureWarnings');

/**
 * Module for the Infrastructure Warnings Widget
 * @module yukon.widget.infrastructureWarnings
 * @requires JQUERY
 * @requires yukon
 */
yukon.widget.infrastructureWarnings = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    _updateInterval = 6000,
    
    _update = function () {
        $.ajax({
            url: yukon.url('/stars/infrastructureWarnings/updateWidget'),
        }).done(function (data) {
            $('#widgetView').html(data);
        });  
        setTimeout(_update, _updateInterval)
    },
    
    mod = {
        
        /** Initialize this module. */
        init : function () {
            
            if (_initialized) return;
            
            _update();
            
            $(document).on('click', '.js-update-infrastructure-warnings', function () {
                $(this).attr('disabled', true);
                $.ajax(yukon.url('/stars/infrastructureWarnings/forceUpdate'));
            });

            _initialized = true;
        },
        
    };
    
    return mod;
})();

$(function () { yukon.widget.infrastructureWarnings.init(); });