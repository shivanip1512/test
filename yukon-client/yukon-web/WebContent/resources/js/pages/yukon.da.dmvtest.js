yukon.namespace('yukon.da.dmvtest');

/**
 * This module is used for the Demand Management and Verification Test feature.
 * @requires JQUERY
 * @requires yukon
 */

yukon.da.dmvtest = (function () {

    'use strict';
    
    var mod = {

        init : function () {
            $(document).on('yukon:da:dmvtest:delete', function () {
                $('#delete-dmvtest').submit();
            });
        }
    };

    return mod;
}());

$(function () { yukon.da.dmvtest.init(); });
