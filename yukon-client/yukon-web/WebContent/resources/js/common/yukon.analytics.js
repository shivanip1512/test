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
    
    /** @type {string} - Cooper GA4 google analytics tracking ID. */
    _cooper_ga4_tracking_id = null,
    
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
        '/picker/search': 1,
        '/amr/dataCollection/updateChart': 1,
        '/stars/infrastructureWarnings/updateWidget': 1,
        '/widget/configWidget/getStatus': 1,
    },
    
    _gtag = function() {
        dataLayer.push(arguments);
    },
    
    _init = function() {
        if(_initialized) return;
        
        window.dataLayer = window.dataLayer || [];
        _gtag('js', new Date());

        _initialized = true;
    },

    /**
     * Add additional id tracking.
     * @param {Object} [args] - Contains options for additional tracking.
     * @param {string} [args.url] - url of page to be tracked.
     */
    _setAdditionalTrackingIds = function(args) {
        for (var i=0 ; i < _additional_tracking_ids.length ; i++) {
            if (typeof _additional_tracking_ids[i] !== 'undefined' && _additional_tracking_ids[i] !== '') {
                if (typeof args !== 'undefined' && typeof args.url === "string" && args.url !== "") {
                    _gtag('event', 'page_view', {
                        page_title: args.url,
                        page_location: args.url,
                        page_page: args.url,
                        send_to: _additional_tracking_ids[i]
                    });
                } else {
                    _gtag('config', _additional_tracking_ids[i]);
                }
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
                _gtag('config', _cooper_tracking_id);
            }
            if (typeof args.cooper_ga4_tracking_id === "string" && args.cooper_ga4_tracking_id !== "") {
                _cooper_ga4_tracking_id = args.cooper_ga4_tracking_id;
                _gtag('config', _cooper_ga4_tracking_id);
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
                if (yg.app_context_path != null) {
                    urlPath = urlPath.replace(yg.app_context_path, '');
                }
                if (!_skipped_urls[urlPath]) {
                    _gtag('event', 'page_view', {
                        page_title: settings.url,
                        page_location: settings.url,
                        page_page: settings.url,
                        send_to: args.cooper_tracking_id
                    });
                    _gtag('event', 'page_view', {
                        page_title: settings.url,
                        page_location: settings.url,
                        page_page: settings.url,
                        send_to: args.cooper_ga4_tracking_id
                    });
                    if (_additional_tracking_ids.length > 0) {
                        _setAdditionalTrackingIds({url: settings.url});
                    }
                }
            });
        }
    };
    
    return _mod;
})();