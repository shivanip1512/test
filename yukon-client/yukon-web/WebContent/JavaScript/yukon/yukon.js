/**
 * Singleton that manages all Yukon javascript functionality
 * 
 * @requires jQuery 1.8.3+
 * @requires jQuery UI 1.9.2+
 */

var yukon = (function (yukonMod) {
    return yukonMod;
})(yukon || {});

// namespace function, so we don't have to put all those checks to see if
// modules exist and either create empty ones or set a reference to one
// that was previously created
yukon.namespace = function (ns_string) {
    var parts = ns_string.split('.'),
        parent = yukon,
        i;
    // strip redundant leading global
    if (parts[0] === "yukon") {
        parts = parts.slice(1);
    }
    for (i = 0; i < parts.length; i += 1) {
        // create a property if it doesn't exist
        if (typeof parent[parts[i]] === "undefined") {
            parent[parts[i]] = {};
        }
        parent = parent[parts[i]];
    }
    return parent;
};

// polyfill for browsers that don't support Object.create - from developer.mozilla.org
if (!Object.create) {
    Object.create = (function(){
        function F(){};

        return function(o){
            if (arguments.length != 1) {
                throw new Error('Object.create implementation only accepts one parameter.');
            }
            F.prototype = o;
            return new F();
        };
    })();
}

//support for inheritance: inherit superType's prototype
//yukon.inheritPrototype = function (subType, superType) {
//    var prototype = Object.create(superType.prototype);
//    prototype.constructor = subType;
//    subType.prototype = prototype;
//};

(function (exports) {
    (function (exports) {
        var inheritPrototypeApi = {
            inheritPrototype: function (subType, superType) {
                var prototype = Object.create(superType.prototype);
                prototype.constructor = subType;
                subType.prototype = prototype;
            }
        };
        jQuery.extend(exports, inheritPrototypeApi);
    }((typeof exports === 'undefined') ? window : exports));
}(yukon));
