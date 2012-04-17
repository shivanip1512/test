/*  analytics_manager.js
 * 
 * requirements:
 *      jQuery 1.6+
 *      
 * abstract:
 *      Manages [Google] analytics for Yukon
 *      
 * usage:
 *      This singleton is initialized when the public method "setTrackingIds" is first called.
 *      This is also when event listeners are registered (currently this only includes listening for all non-dataupdater ajax calls).
 *      'Private' methods begin with '_' and should generally not be called.
 *      Public methods are limited to 
 *          * setting the tracking ids (a single cooper_tracking_id and a single additional_tracking_id 
 *              (which will generally be for the customer))
 */

if(typeof(Yukon) === 'undefined'){
    Yukon = {};
}

if(typeof(Yukon.Util) === 'undefined'){
    Yukon.Util = {};
}

if(typeof(Yukon.Util.AnalyticsManager) === 'undefined'){
	Yukon.Util.AnalyticsManager = {
        _initialized: false,
        _cooper_tracking_id: null,
        _additional_tracking_id: null,
        _updater_url: "/spring/updater/update",
        
        /*---------------------*/
        /* 'PUBLIC' functions */
        /*---------------------*/
        
        /*
         * Set the google analytics tracking Id's
         * 
         * args = object {}
         *      cooper_tracking_id:string               - the google analytics cooper tracking id
         *      additional_tracking_id:string           - the google analytics additional tracking id
         */
        setTrackingIds: function(args){
            this._init();
            if (typeof args.cooper_tracking_id !== "undefined" && args.cooper_tracking_id !== null &&
                    args.cooper_tracking_id !== "") {
                this._cooper_tracking_id = args.cooper_tracking_id;
                _gaq=[["_setAccount",this._cooper_tracking_id],["_trackPageview"]];
            }
            if (typeof args.additional_tracking_id !== "undefined" && args.additional_tracking_id !== null &&
                    args.additional_tracking_id !== "") {
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
            if (typeof this._additional_tracking_id !== "undefined" && this._additional_tracking_id !== null &&
                    this._additional_tracking_id !== "") {
                var trackPageview = ['a._trackPageview'];
                if (typeof args.url !== "undefined" && 
                        args.url !== null && args.url !== "") {
                    trackPageview.push(args.url);
                }
                _gaq.push(['a._setAccount',this._additional_tracking_id],trackPageview);
            }
        },
	};
}