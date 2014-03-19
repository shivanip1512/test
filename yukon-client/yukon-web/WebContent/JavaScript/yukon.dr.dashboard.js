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
        timeFormatter = yukon.timeFormatter,
        mod;

    mod = {
            
        init: function() {
            
            var _initialCommandTime = $('#rf-performance-command-time').val(),
                _initialEmailTime = $('#rf-performance-email-time').val(),
                _initialEmail = $('#rf-performance-email').val();
            
            /** Setup the command time slider */
            $(".f-broadcast-config .f-time-slider").slider({
                max: 24 * 60 - 15,
                min: 0,
                value: _initialCommandTime,
                step: 15,
                slide: function(event, ui) {
                    var currentTime = timeFormatter.formatTime(ui.value, 0);
                    $(_commandTimeSelector).html(currentTime);
                    $('#rf-performance-command-time').val(ui.value);
                },
                change: function(event, ui) {
                    var currentTime = timeFormatter.formatTime(ui.value, 0);
                    $(_commandTimeSelector).html(currentTime);
                    $('#rf-performance-command-time').val(ui.value);
                }
            });
            
            /** Setup the email time slider */
            $(".f-broadcast-config .f-email-time-slider").slider({
                max: 24 * 60 - 15,
                min: 0,
                value: _initialEmailTime,
                step: 15,
                slide: function(event, ui) {
                    var currentTime = timeFormatter.formatTime(ui.value, 0);
                    $(_emailTimeSelector).html(currentTime);
                    $('#rf-performance-email-time').val(ui.value);
                },
                change: function(event, ui) {
                    var currentTime = timeFormatter.formatTime(ui.value, 0);
                    $(_emailTimeSelector).html(currentTime);
                    $('#rf-performance-email-time').val(ui.value);
                }
            });
            
            /** Setup the time label */
            $(_commandTimeSelector).html(timeFormatter.formatTime(_initialCommandTime, 0));
            $(_emailTimeSelector).html(timeFormatter.formatTime(_initialEmailTime, 0));
            
            /** Handle email on/off toggle button.  TODO make on-off toggle button resuable */
            $(document).on('click', '.f-broadcast-config .toggle-on-off .button', function() {
                $('.f-broadcast-config .toggle-on-off .button').toggleClass('on');
                $('.f-notif-group').toggle('fade');
                $('.f-email-schedule').toggle('fade');
                if ($('.f-broadcast-config .toggle-on-off .yes').hasClass('on')) {
                    $('#rf-performance-email').val('true');
                } else {
                    $('#rf-performance-email').val('false');
                }
                
            });
            
            if (_initialEmail === 'true') {
                $('.f-broadcast-config .toggle-on-off .yes').addClass('on');
                $('.f-broadcast-config .toggle-on-off .no').removeClass('on');
                $('.f-notif-group').show();
                $('.f-email-schedule').show();
            } else {
                $('.f-broadcast-config .toggle-on-off .no').addClass('on');
                $('.f-broadcast-config .toggle-on-off .yes').removeClass('on');
                $('.f-notif-group').hide();
                $('.f-email-schedule').hide();
            }
            
        }
        
    };
    return mod;
}());

$(function () {yukon.dr.dashboard.init();});