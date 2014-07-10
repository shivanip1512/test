/**
 * Singleton that manages the javascript for dr rf broadcast performance
 * 
 * @requires jQuery 1.8.3+
 * @requires jQuery UI 1.9.2+
 */

yukon.namespace('yukon.dr');
yukon.namespace('yukon.dr.rf');
yukon.namespace('yukon.dr.rf.performance');

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
                url += $(this).closest('[data-test]').data('test');
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