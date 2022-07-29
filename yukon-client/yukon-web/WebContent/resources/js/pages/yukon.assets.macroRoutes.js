yukon.namespace('yukon.assets.macroRoutes');
 

/** 
 * Module that handles the behavior on the macro routes page.
 * @module yukon.assets.macroRoutes
 * @requires JQUERY 
 * @requires yukon 
 */
yukon.assets.macroRoutes = (function () {
 
    'use strict';

    var
    _initialized = false,
 
    mod = {
 
        /** Initialize this module. */
        init: function () {
 
            if (_initialized) return;

            $(document).on("yukon:macroRoute:delete", function () {
                yukon.ui.blockPage();
                $('#delete-macroRoute-form').submit();
            });

            _initialized = true;
        }
 
    };
 
    return mod;
})();
 
$(function () { yukon.assets.macroRoutes.init(); });