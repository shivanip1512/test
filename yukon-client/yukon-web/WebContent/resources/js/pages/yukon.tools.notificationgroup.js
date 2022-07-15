yukon.namespace('yukon.tools.notificationgroup');

/**
 * Module that handles the behavior on Notification Group page.
 * @module yukon.tools.notificationgroup
 * @requires JQUERY
 * @requires yukon
 */
yukon.tools.notificationgroup = (function() {
    'use strict';

    var
    _initialized = false,

    mod = {

        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            $(document).on("yukon:notificationGroup:delete", function () {
                yukon.ui.blockPage();
                $('#delete-notificationGroup-form').submit();
            });

            _initialized = true;
        }
    };

    return mod;
})();

$(function () { yukon.tools.notificationgroup.init(); });