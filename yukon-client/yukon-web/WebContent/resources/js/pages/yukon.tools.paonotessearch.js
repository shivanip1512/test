yukon.namespace('yukon.tools.paonotessearch.js');
 
/** 
 * Module to handle pao notes search functionality.
 * 
 * @module yukon.tools.paonotessearch 
 * @requires JQUERY 
 * @requires yukon 
 */
yukon.tools.paonotessearch = (function () {
 
    'use strict';
 
    var
    _initialized = false,
 
    mod = {
 
        /** Initialize this module. */
        init: function () {
 
            if (_initialized) return;
            
            $(document).on('click', '.js-download', function () {
                var form = $('#filter-pao-notes-form');
                var data = form.serialize();
                window.location = yukon.url('/tools/paoNotes/download?' + data);
            });
            
            if ($("#deviceGroups\\.errors").length === 1) {
                $("#deviceGroups\\.errors").prev().remove();
            }
            if ($("#paoIds\\.errors").length === 1) {
                $("#paoIds\\.errors").prev().remove();
            }

            _initialized = true;
        }
 
    };
 
    return mod;
})();
 
$(function () { yukon.tools.paonotessearch.init(); });