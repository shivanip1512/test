/**
 * Singleton that manages the javascript for DR Dashboard
 * 
 * @requires jQuery 1.8.3+
 * @requires jQuery UI 1.9.2+
 */

yukon.namespace('yukon.dr');
yukon.namespace('yukon.dr.dashboard');

yukon.dr.dashboard = (function () {
    
    var _commandTimeSelector = '.f-time-label',
        _emailTimeSelector = '.f-email-time-label',
        mod;

    mod = {
            
        init: function() {
            
            var _initialCommandTime = jQuery('#rf-performance-command-time').val(),
                _initialEmailTime = jQuery('#rf-performance-email-time').val(),
                _initialEmail = jQuery('#rf-performance-email').val();
            
            /** Setup the command time slider */
            jQuery(".f-broadcast-config .f-time-slider").slider({
                max: 24 * 60 - 15,
                min: 0,
                value: _initialCommandTime,
                step: 15,
                slide: function(event, ui) {
                    var currentTime = timeFormatter.formatTime(ui.value, 0);
                    jQuery(_commandTimeSelector).html(currentTime);
                    jQuery('#rf-performance-command-time').val(ui.value);
                },
                change: function(event, ui) {
                    var currentTime = timeFormatter.formatTime(ui.value, 0);
                    jQuery(_commandTimeSelector).html(currentTime);
                    jQuery('#rf-performance-command-time').val(ui.value);
                }
            });
            
            /** Setup the email time slider */
            jQuery(".f-broadcast-config .f-email-time-slider").slider({
                max: 24 * 60 - 15,
                min: 0,
                value: _initialEmailTime,
                step: 15,
                slide: function(event, ui) {
                    var currentTime = timeFormatter.formatTime(ui.value, 0);
                    jQuery(_emailTimeSelector).html(currentTime);
                    jQuery('#rf-performance-email-time').val(ui.value);
                },
                change: function(event, ui) {
                    var currentTime = timeFormatter.formatTime(ui.value, 0);
                    jQuery(_emailTimeSelector).html(currentTime);
                    jQuery('#rf-performance-email-time').val(ui.value);
                }
            });
            
            /** Setup the time label */
            jQuery(_commandTimeSelector).html(timeFormatter.formatTime(_initialCommandTime, 0));
            jQuery(_emailTimeSelector).html(timeFormatter.formatTime(_initialEmailTime, 0));
            
            /** Handle email on/off toggle button.  TODO make on-off toggle button resuable */
            jQuery(document).on('click', '.f-broadcast-config .toggle-on-off .button', function() {
                jQuery('.f-broadcast-config .toggle-on-off .button').toggleClass('on');
                jQuery('.f-notif-group').toggle('fade');
                jQuery('.f-email-schedule').toggle('fade');
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
                jQuery('.f-email-schedule').show();
            } else {
                jQuery('.f-broadcast-config .toggle-on-off .no').addClass('on');
                jQuery('.f-broadcast-config .toggle-on-off .yes').removeClass('on');
                jQuery('.f-notif-group').hide();
                jQuery('.f-email-schedule').hide();
            }
            
        }
        
    };
    return mod;
}());

jQuery(function () {yukon.dr.dashboard.init();});