yukon.namespace('yukon.dr.setup.list');

/**
 * Module that handles the behavior on the setup page.
 * @module yukon.dr.setup.list
 * @requires JQUERY
 * @requires yukon
 */
yukon.dr.setup.list = (function() {
    
    'use strict';
    
    var
    _initialized = false,
    
    _initFilterOptions = function () {
        if ($("#js-switch-types").exists()) {
            $("form.js-filter-load-groups-form").find('.chosen-container-single').css({
               "margin-top" : "-3px",
               "margin-right" : "10px"});
            $("#js-switch-types").chosen({width: "450px"});
        }
    },
    
    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
            _initFilterOptions();
            
            $(document).on('change', '.js-filter-by', function () {
                $.get(yukon.url('/dr/setup/loadFilterByOptions/' + $('.js-filter-by').find(":selected").val()), function (data) {
                    $('.js-filter-options').html(data);
                    _initFilterOptions();
                });
            });
            
            _initialized = true;
        }
    };
    
    return mod;
})();

$(function () { yukon.dr.setup.list.init(); });