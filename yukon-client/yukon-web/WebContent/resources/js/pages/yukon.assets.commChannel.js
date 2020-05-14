yukon.namespace('yukon.assets.commChannel.js');
 
/** 
 * Module that handles the behavior on the comm channel
 * @module yukon.assets.commChannel.js
 * @requires JQUERY 
 * @requires yukon 
 */
yukon.assets.commChannel = (function () {
 
    'use strict';
 
    var
    _initialized = false,
 
    mod = {
 
        /** Initialize this module. */
        init: function () {
 
            if (_initialized) return;
            $(document).on("yukon:commChannel:delete", function () {
                yukon.ui.blockPage();
                $('#delete-commChannel-form').submit();
            });
            _initialized = true;
        }
 
    };
 
    return mod;
})();
 
$(function () { yukon.assets.commChannel.init(); });