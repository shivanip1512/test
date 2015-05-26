yukon.namespace('yukon.dr.psd');

/**
 * Module for the Power Supplier Displays pages used by Allegheny
 * @module yukon.dr.psd
 * @requires JQUERY
 * @requires yukon
 */
yukon.dr.psd = (function () {
    
    'use strict';
    
    return {
        
        /** Initialize this module. */
        transmitted: function () {
            $('#last-transmitted').html(moment(new Date()).tz(yg.timezone).format(yg.formats.date.both))
            .flash(3.5);
        }
        
    };
    
})();