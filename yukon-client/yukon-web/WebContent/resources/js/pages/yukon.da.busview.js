yukon.namespace('yukon.da.busview');

/**
 * Module for the busView page/
 * @module yukon.da.busview
 * @requires JQUERY
 * @requires yukon
 */
yukon.da.busview = (function () {

    'use strict';
    
   
    var _initialized = false;

    var _updateRecentEvents = function () {

        // Range Defaulted to 24 hours
        var range = 'DAY_1';
        $(".js-events-timeline").each(function () {
            
            var _id = this.getAttribute('data-zone-id');
            $.ajax({
                url : yukon.url('/capcontrol/zones/' + _id + '/events'),
                data : {
                    'range' : range
                }
            //data: { 'range': 'DAY_1' }
            }).done(function (events) {

                var timeline = $('.js-events-timeline[data-zone-id="' + _id + '"]');
                var toAdd = [];
                var options = {};
                options.end = new Date().getTime();
                var now = new Date();
                //added 24 to get last day events.
                var hoursAgo = 24;
                var begin = new Date(now.getTime() - (1000 * 60 * 60 * hoursAgo));
                options.begin = begin.getTime();

                // Reverse order to add oldest first.
                events.reverse().forEach(function (event) {
                    toAdd.push(event);
                });
                if (toAdd.length) {
                    timeline.timeline(options);
                    timeline.timeline('addEvents', toAdd);
                }
                timeline.timeline(options);
                timeline.timeline('draw');

            });

        });
        setTimeout(_updateRecentEvents, yg.rp.updater_delay);

    };

    mod = {

        /** Initialize this module. */
        init : function () {

            if (_initialized)
                return;

            _updateRecentEvents();

            _initialized = true;
        }

    };

    return mod;
})();

$(function () {
    yukon.da.busview.init();
});