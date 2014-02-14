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
                    popup.dialog({width: 600});
                });
            });
        }
        
    };
    return mod;
}());

jQuery(function () {yukon.dr.rf.performance.init();});