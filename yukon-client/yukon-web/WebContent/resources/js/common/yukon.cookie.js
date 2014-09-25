/** jquery.cookie plugin config */
$.cookie.json = true;
$.extend($.cookie.defaults, { expires: 365, path: '/' });

yukon.namespace('yukon.cookie');

/**
 * Manages the 'yukon' cookie. The 'yukon' cookie is a single cookie
 * that holds a JSON object on which properties are set and retrieved.
 * @module yukon.cookie
 * @requires JQUERY
 * @requires jquery.cookie
 * @requires yukon
 */
yukon.cookie = (function () {

    'use strict';
    
    return {
            
        /** 
         * Retrieve value from JSON object store in the 'yukon' cookie. 
         * scope and persistId are concatinated to form the property name of
         * the value to retrieve. 
         * @param {string} scope - Scope of the value.
         * @param {string} persistId - Id of the value.
         * @param {string} defaultValue - Returned value if the requested property is not found.
         */
        get: function (scope, persistId, defaultValue) {
            var c = $.cookie('yukon'), value;
            if (!c) return defaultValue;
            value = c[scope + persistId];
            if (value) {
                return value;
            } else {
                return defaultValue;
            }
        },
        
        /** 
         * Sets a property on the JSON object stored in the 'yukon' cookie.
         * scope and persistId are concatinated to form the property name to
         * set the value on in the JSON object. 
         * e.g.
         * scope = 'foo', persistId = 'bar', value = 'cat'
         * results in: { foobar: 'cat' }
         *  
         * @param {string} scope - Scope for this value.
         * @param {string} persistId - Id for this value.
         * @param {string} value - Value to store.
         */
        set: function (scope, persistId, value) {
            var c = $.cookie('yukon');
            if (!c) c = {};
            c[scope + persistId] = value;
            $.cookie('yukon', c);
        }
        
    };
    
})();