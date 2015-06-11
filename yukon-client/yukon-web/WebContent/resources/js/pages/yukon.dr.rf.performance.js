yukon.namespace('yukon.dr.rf.performance');

/**
 * Module for the dr rf broadcast performance section of the dr dashboard page
 * @module   yukon.dr.rf.performance
 * @requires JQUERY
 * @requires JQUERYUI
 * @requires yukon
 */
yukon.dr.rf.performance = (function () {
    
    var mod;

    mod = {
            
        init: function() {
            
            /** 
             * Handle click for success, failed, and unknown popups.
             * The unknown popup is slightly different and has a pie chart */
            $(document).on('click', '.js-success, .js-failed, .js-unknown', function(ev) {
                
                var popup = $('#devices-popup'),
                    url = yukon.url('/dr/rf/details'),
                    hasChart = false;
                
                /* Build url */
                if ($(this).hasClass('js-success')) {
                    url += '/success/';
                } else if ($(this).hasClass('js-failed')){
                    url += '/failed/';
                } else {
                    url += '/unknown/';
                    hasChart = true;
                }
                url += $(this).data('test');
                url += "?itemsPerPage=" + 10;
                /* Load popup */
                popup.load(url, function (resp, status, xhr) {
                    popup.dialog({width: 700, title: popup.find('.js-title').val()});
                    // generate pie chart
                    if (hasChart) {
                        $.plot('.js-pie-chart', JSON.parse(xhr.getResponseHeader('X-JSON')), {
                            series: {
                                pie: {
                                    show: true
                                }
                            }
                        });
                    }
                });
                
                // just to hide the menu
                popup.trigger('click');
            });
        }
    };
    return mod;
}());

$(function () {yukon.dr.rf.performance.init();});