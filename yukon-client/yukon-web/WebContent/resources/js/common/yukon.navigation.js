yukon.namespace('yukon.navigation');

/** 
 * Module that handles the rendering of the react navigation controls
 * @module yukon.navigation.js
 * @requires JQUERY 
 * @requires yukon 
 */
yukon.navigation = (function () {
    
    'use strict';
    
    var
    _initialized = false,

    mod = {
        
        /** Initialize this module. */
        init : function () {
            
            if (_initialized) return;
            
            //call react navigation component
/*            $.ajax({
                url: 'http://localhost:3000',
                success: function(data){
                    $('.js-navigation').html(data);
                }
            });*/
/*            $('.js-navigation').load('http://localhost:3000', function() {

            });*/
            
            _initialized = true;
        },
        
    };
        
    return mod;
})();
 
$(function () { yukon.navigation.init(); });