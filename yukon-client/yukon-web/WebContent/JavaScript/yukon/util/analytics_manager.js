if(typeof(Yukon) === 'undefined'){
    Yukon = {};
}

if(typeof(Yukon.Util) === 'undefined'){
    Yukon.Util = {};
}

if(typeof(Yukon.Util.AnalyticsManager) === 'undefined'){
    /**
     * Singleton that manages [Google] analytics for Yukon. It is initialized 
     * when the public method "setTrackingIds" is first called. This is also when event listeners 
     * are registered (currently this only includes listening for all non-dataupdater ajax calls).
     * 
     * @class Manages [Google] analytics for Yukon
     * @author <a href="mailto:alex.delegard@cooperindustries.com">Alex Delegard</a>
     * @requires jQuery 1.6+
     */
	Yukon.Util.AnalyticsManager = {
        _initialized: false,
        _cooper_tracking_id: null,
        _additional_tracking_ids: [],
        _updater_url: "/spring/updater/update",
        
        /*---------------------*/
        /* 'PUBLIC' functions */
        /*---------------------*/

        /**
         * Sets the google analytics tracking Id's
         * @param {Object} args TrackingId arguments
         * @param {String} args.cooper_tracking_id The Cooper Google Analytics Tracking Id
         * @param {String} args.additional_tracking_ids The additional Google Analytics Tracking Ids (comma separated string)
         */
        setTrackingIds: function(args){
            this._init();
            if (typeof args.cooper_tracking_id === "string" && args.cooper_tracking_id !== "") {
                this._cooper_tracking_id = args.cooper_tracking_id;
                _gaq=[["_setAccount", this._cooper_tracking_id], ["_trackPageview"]];
            }
            if (typeof args.additional_tracking_ids === "string" && args.additional_tracking_ids !== "") {
                this._additional_tracking_ids = args.additional_tracking_ids.replace(/\s/g,'').split(","); //remove all whitespace then split it into our array
                this._setAdditionalTrackingIds();
            }

            var _self = this;
            // Log all jQuery AJAX requests to Google Analytics
            jQuery(document).ajaxSend(function(event, xhr, settings) {
                if (settings.url !== _self._updater_url && 
                        typeof _gaq !== "undefined" && _gaq !== null) {
                    _gaq.push(["_setAccount", this._cooper_tracking_id], ['_trackPageview', settings.url]);
                    if (_self._additional_tracking_ids.length > 0) {
                        _self._setAdditionalTrackingIds({url: settings.url});
                    }
                }
            });
        },
        
        /*---------------------*/
        /* 'PRIVATE' functions */
        /*---------------------*/
        _init: function(){
            if(this._initialized) return;
            if (typeof _gaq === "undefined" || _gaq === null) {
                _gaq = [];
            }

            (function(d){
                var g=d.createElement("script");
                var s=d.getElementsByTagName("body")[0];
                g.src=("https:"==location.protocol?"//ssl":"//www")+".google-analytics.com/ga.js";
                s.appendChild(g); // append to bottom of <body> to prevent blocking (as opposed to <head>)
            }(document));

            this._initialized = true;
        },

        _setAdditionalTrackingIds: function(args){
            var trackPageview = ['a._trackPageview'];
            if (typeof args !== 'undefined' && typeof args.url === "string" && args.url !== "") {
                trackPageview.push(args.url);
            }
            for (var i=0 ; i < this._additional_tracking_ids.length ; i++) {
                if (typeof this._additional_tracking_ids[i] !== 'undefined' && this._additional_tracking_ids[i] !== '') {
                    _gaq.push(['a._setAccount', this._additional_tracking_ids[i]], trackPageview);
                }
            }
        }
	};
}