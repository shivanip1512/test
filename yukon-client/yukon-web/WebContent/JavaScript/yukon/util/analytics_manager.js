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
        _additional_tracking_id: null,
        _updater_url: "/spring/updater/update",
        
        /*---------------------*/
        /* 'PUBLIC' functions */
        /*---------------------*/

        /**
         * Sets the google analytics tracking Id's
         * @param {Object} args TrackingId arguments
         * @param {String} args.cooper_tracking_id The Cooper Google Analytics Tracking Id
         * @param {String} args.additional_tracking_id The additional Google Analytics Tracking Id
         */
        setTrackingIds: function(args){
            this._init();
            if (typeof args.cooper_tracking_id === "string" && args.cooper_tracking_id !== "") {
                this._cooper_tracking_id = args.cooper_tracking_id;
                _gaq=[["_setAccount",this._cooper_tracking_id],["_trackPageview"]];
            }
            if (typeof args.additional_tracking_id === "string" && args.additional_tracking_id !== "") {
                this._additional_tracking_id = args.additional_tracking_id;
                this._setAdditionalTrackingId;
            }

            var _self = this;
            // Log all jQuery AJAX requests to Google Analytics
            jQuery(document).ajaxSend(function(event, xhr, settings) {
                if (settings.url !== _self._updater_url && 
                        typeof _gaq !== "undefined" && _gaq !== null) {
                    _gaq.push(['_trackPageview',settings.url]);
                    _self._setAdditionalTrackingId({url: settings.url});
                }
            });
        },
        
        /*---------------------*/
        /* 'PRIVATE' functions */
        /*---------------------*/
        _init: function(){
            if(!this._initialized){
                if (typeof _gaq === "undefined" || _gaq === null) {
                    _gaq = [];
                }

                /* Asynchronous Google Analytics snippet
                 * mathiasbynens.be/notes/async-analytics-snippet */
                (function(d,t){
                    var g=d.createElement(t),s=d.getElementsByTagName(t)[0];
                    g.async=1;
                    g.src=("https:"==location.protocol?"//ssl":"//www")+".google-analytics.com/ga.js";
                    s.parentNode.insertBefore(g,s);
                }(document,"script"));

                this._initialized = true;
            }
        },

        _setAdditionalTrackingId: function(args){
            if (typeof this._additional_tracking_id === "string" && this._additional_tracking_id !== "") {
                var trackPageview = ['a._trackPageview'];
                if (typeof args.url === "string" && args.url !== "") {
                    trackPageview.push(args.url);
                }
                _gaq.push(['a._setAccount',this._additional_tracking_id],trackPageview);
            }
        },
	};
}