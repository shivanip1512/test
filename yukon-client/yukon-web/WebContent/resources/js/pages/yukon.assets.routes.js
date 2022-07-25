yukon.namespace('yukon.assets.routes');
 
/** 
 * Module that handles the behavior on the routes channel
 * @module yukon.assets.routes.js
 * @requires JQUERY 
 * @requires yukon 
 */
yukon.assets.routes = (function () {
 
    'use strict';

    var
    _initialized = false,
 
    mod = {
 
        /** Initialize this module. */
        init: function () {
 
            if (_initialized) return;

            $(document).on("yukon:routes:delete", function () {
                yukon.ui.blockPage();
                $('#js-route-delete-form').submit();
            });

            _initialized = true;
        }
 
    };
 
    return mod;
})();
 
$(function () { yukon.assets.routes.init(); });