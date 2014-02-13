/**
 * Singleton that manages google analytics
 * 
 * @requires jQuery 1.8.3+
 * @requires jQuery UI 1.9.2+
 */

var yukon = (function (yukonMod) {
    return yukonMod;
})(yukon || {});

yukon.namespace('yukon.AnalyticsManager');

yukon.AnalyticsManager = ( function () {
    /**
     * Singleton that manages [Google] analytics for yukon. It is initialized 
     * when the public method "setTrackingIds" is first called. This is also when event listeners 
     * are registered (currently this only includes listening for all non-dataupdater ajax calls).
     * 
     * @class Manages [Google] analytics for Yukon
     * @requires jQuery 1.6+
     */
    var _initialized = false,
        _cooper_tracking_id = null,
        _additional_tracking_ids = [],
        _skipped_urls = {'/updater/update':1, '/addToHistory':1, '/isFavorite':1, '/isSubscribed':1,
    		'/search/autocomplete.json':1, '/picker/v2/build':1, '/picker/v2/idSearch':1, '/picker/v2/search':1},

        /*---------------------*/
        /* 'PRIVATE' functions */
        /*---------------------*/
        _init = function () {
            if(_initialized) return;
            if (typeof _gaq === "undefined" || _gaq === null) {
                _gaq = [];
            }

            (function (d) {
                var g=d.createElement("script");
                var s=d.getElementsByTagName("body")[0];
                g.src=("https:"==location.protocol?"//ssl":"//www")+".google-analytics.com/ga.js";
                s.appendChild(g); // append to bottom of <body> to prevent blocking (as opposed to <head>)
            }(document));

            _initialized = true;
        },

        _setAdditionalTrackingIds = function (args) {
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
        analyticsMod;

    analyticsMod = {
        
        /*---------------------*/
        /* 'PUBLIC' functions */
        /*---------------------*/

        /**
         * Sets the google analytics tracking Id's
         * @param {Object} args TrackingId arguments
         * @param {String} args.cooper_tracking_id The Cooper Google Analytics Tracking Id
         * @param {String} args.additional_tracking_ids The additional Google Analytics Tracking Ids (comma separated string)
         */
        setTrackingIds: function (args) {
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
            jQuery(document).ajaxSend(function (event, xhr, settings) {
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
    return analyticsMod;
})();