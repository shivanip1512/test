yukon.namespace('yukon.assets.relay.details');
 
/**
 * Module to handle the relay details page.
 * 
 * @module yukon.assets.relay.details
 * @requires yukon
 * @requires JQUERY
 */
yukon.assets.relay.details = (function () {
 
    var
    _initialized = false,
    
    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
            $(document).on("yukon:relay:delete", function () {
                yukon.ui.blockPage();
                $('#delete-relay-form').submit();
            });
        }
    };
 
    return mod;
})();
 
$(function () { yukon.assets.relay.details.init(); });