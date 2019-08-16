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
    
    _initTypes = function () {
        var selectedFilterByType = $("#js-filter-by-type option:selected").val();
        $(".js-load-group-types-container").toggleClass("dn", selectedFilterByType !== 'LOAD_GROUP');
        $(".js-load-program-types-container").toggleClass("dn", selectedFilterByType !== 'LOAD_PROGRAM');
    },
    
    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
            if ($("#js-load-group-types").exists()) {
                $("#js-load-group-types").chosen({width: "450px"});
            }
            
            if ($("#js-load-program-types").exists()) {
                $("#js-load-program-types").chosen({width: "305px"});
            }
            
            _initTypes();
            
            $('#setupFilter :input').change(function (ev) {
                $('#setupFilter').submit();
            });
            
            _initialized = true;
        }
    };
    
    return mod;
})();

$(function () { yukon.dr.setup.list.init(); });