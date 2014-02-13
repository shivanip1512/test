/**
 * Singleton that manages the javascript for DR Dashboard
 * 
 * @requires jQuery 1.8.3+
 * @requires jQuery UI 1.9.2+
 */

yukon.namespace('yukon.dr');
yukon.namespace('yukon.dr.dashboard');

yukon.dr.dashboard = (function () {
    
    var _timeSelector = '.f-time-label',
        mod;

    mod = {
            
        init: function() {
            
            var _initialTime = jQuery('#rf-performance-command-time').val(),
                _initialEmail = jQuery('#rf-performance-email').val();
            
            /** Setup the time slider */
            jQuery(".f-broadcast-config .f-time-slider").slider({
                max: 24 * 60 - 15,
                min: 0,
                value: _initialTime,
                step: 15,
                slide: function(event, ui) {
                    var currentTime = timeFormatter.formatTime(ui.value, 0);
                    jQuery(_timeSelector).html(currentTime);
                    jQuery('#rf-performance-command-time').val(ui.value);
                },
                change: function(event, ui) {
                    var currentTime = timeFormatter.formatTime(ui.value, 0);
                    jQuery(_timeSelector).html(currentTime);
                    jQuery('#rf-performance-command-time').val(ui.value);
                }
            });
            /** Setup the time label */
            jQuery(_timeSelector).html(timeFormatter.formatTime(_initialTime, 0));
            
            /** Handle email on/off toggle button.  TODO make on-off toggle button resuable */
            jQuery(document).on('click', '.f-broadcast-config .toggle-on-off .button', function() {
                jQuery('.f-broadcast-config .toggle-on-off .button').toggleClass('on');
                jQuery('.f-notif-group').toggle('fade');
                if (jQuery('.f-broadcast-config .toggle-on-off .yes').hasClass('on')) {
                    jQuery('#rf-performance-email').val('true');
                } else {
                    jQuery('#rf-performance-email').val('false');
                }
                
            });
            
            if (_initialEmail === 'true') {
                jQuery('.f-broadcast-config .toggle-on-off .yes').addClass('on');
                jQuery('.f-broadcast-config .toggle-on-off .no').removeClass('on');
                jQuery('.f-notif-group').show();
            } else {
                jQuery('.f-broadcast-config .toggle-on-off .no').addClass('on');
                jQuery('.f-broadcast-config .toggle-on-off .yes').removeClass('on');
                jQuery('.f-notif-group').hide();
            }
            
        }
        
    };
    return mod;
}());

jQuery(function () {yukon.dr.dashboard.init();});