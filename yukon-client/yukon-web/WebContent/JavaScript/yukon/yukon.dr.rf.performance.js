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
            
            /** Handle click for unknown popup */
            jQuery(document).on('click', '.f-unknown', function(event) {
                
                var popup = jQuery('#unknown-popup');
                popup.load('/dr/rf/details/unknown/' + jQuery(this).data('test'), function (resp, status, xhr) {
                    var data = xhr.getResponseHeader('X-JSON');
                    popup.dialog({width: 700});
                    jQuery.plot('.f-unknown-pie', JSON.parse(data), {
                        series: {
                            pie: {
                                show: true
                            }
                        }
                    });
                });
            });
        }
    };
    return mod;
}());

jQuery(function () {yukon.dr.rf.performance.init();});