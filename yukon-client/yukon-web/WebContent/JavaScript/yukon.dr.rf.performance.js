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
            jQuery(document).on('click', '.f-success, .f-failed, .f-unknown', function(event) {
                
                var popup = jQuery('#devices-popup'),
                    url = '/dr/rf/details',
                    hasChart = false;
                
                /* Build url */
                if (jQuery(this).hasClass('f-success')) {
                    url += '/success/';
                } else if (jQuery(this).hasClass('f-failed')){
                    url += '/failed/';
                } else {
                    url += '/unknown/';
                    hasChart = true;
                }
                url += jQuery(this).closest('[data-test]').data('test');
                url += "?itemsPerPage=" + 10;
                /* Load popup */
                popup.load(url, function (resp, status, xhr) {
                    popup.dialog({width: 700, title: popup.find('.f-title').val()});
                    // generate pie chart
                    if (hasChart) {
                        jQuery.plot('.f-pie-chart', JSON.parse(xhr.getResponseHeader('X-JSON')), {
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

jQuery(function () {yukon.dr.rf.performance.init();});