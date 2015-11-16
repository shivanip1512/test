yukon.namespace('yukon.analytics');

/**
 * Singleton that manages [Google] analytics for yukon. It is initialized 
 * when the public method "setTrackingIds" is first called. This is also when event listeners 
 * are registered (currently this only includes listening for all non-dataupdater ajax calls).
 * 
 * @module yukon.analytics
 * @class Manages [Google] analytics for Yukon
 * @requires JQUERY
 * @requires yukon
 */
yukon.analytics = (function() {
    
    var 
    _initialized = false,
    
    /** @type {string} - Cooper google analytics tracking ID. */
    _cooper_tracking_id = null,
    
    /** @type Array.<string> - array containing tracking ID's. */
    _additional_tracking_ids = [],
    
    /** @type {Object.<string,{integer}>} - URLS that need to be ignored  */
    _skipped_urls = {
        '/updater/update': 1, 
        '/addToHistory': 1, 
        '/isFavorite': 1, 
        '/isSubscribed': 1,
        '/search/autocomplete.json': 1, 
        '/picker/build': 1, 
        '/picker/idSearch': 1, 
        '/picker/search': 1
    },

    _init = function() {
        if(_initialized) return;
        if (typeof _gaq === "undefined" || _gaq === null) {
            _gaq = [];
        }

        (function(d) {
            var g=d.createElement("script");
            var s=d.getElementsByTagName("body")[0];
            g.src=("https:"==location.protocol?"//ssl":"//www")+".google-analytics.com/ga.js";
            s.appendChild(g); // append to bottom of <body> to prevent blocking (as opposed to <head>)
        }(document));

        _initialized = true;
    },

    /**
     * Add additional id tracking.
     * @param {Object} [args] - Contains options for additional tracking.
     * @param {string} [args.url] - url of page to be tracked.
     */
    _setAdditionalTrackingIds = function(args) {
        var trackPageview = ['a._trackPageview'];
        if (typeof args !== 'undefined' && typeof args.url === "string" && args.url !== "") {
            trackPageview.push(args.url);
        }
        for (var i=0 ; i < _additional_tracking_ids.length ; i++) {
            if (typeof _additional_tracking_ids[i] !== 'undefined' && _additional_tracking_ids[i] !== '') {
                _gaq.push(['a._setAccount', _additional_tracking_ids[i]], trackPageview);
            }
        }
    },

    _mod = {
        
        /**
         * Sets the google analytics tracking Id's
         * @param {Object} args - TrackingId arguments
         * @param {String} args.cooper_tracking_id - The Cooper Google Analytics Tracking Id
         * @param {String} args.additional_tracking_ids - The additional Google Analytics Tracking Ids (comma separated string)
         */
        setTrackingIds: function(args) {
            _init();
            if (typeof args.cooper_tracking_id === "string" && args.cooper_tracking_id !== "") {
                _cooper_tracking_id = args.cooper_tracking_id;
                _gaq=[["_setAccount", _cooper_tracking_id], ["_trackPageview"]];
            }
            if (typeof args.additional_tracking_ids === "string" && args.additional_tracking_ids !== "") {
                _additional_tracking_ids = args.additional_tracking_ids.replace(/\s/g,'').split(","); //remove all whitespace then split it into our array
                _setAdditionalTrackingIds();
            }

            // Log all jQuery AJAX requests to Google Analytics
            $(document).ajaxSend(function(event, xhr, settings) {
                var urlPath = settings.url;
                var indexOfQueryParams = urlPath.indexOf('?');
                if (indexOfQueryParams != -1) {
                    urlPath = urlPath.substring(0, indexOfQueryParams);
                }
                if (!_skipped_urls[urlPath] && typeof _gaq !== "undefined" && _gaq !== null) {
                    _gaq.push(["_setAccount", _cooper_tracking_id], ['_trackPageview', settings.url]);
                    if (_additional_tracking_ids.length > 0) {
                        _setAdditionalTrackingIds({url: settings.url});
                    }
                }
            });
        }
    };
    
    return _mod;
})();