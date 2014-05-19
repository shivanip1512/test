/**
 * Singleton that manages the javascript for DR Dashboard
 * 
 * @requires jQuery 1.8.3+
 * @requires jQuery UI 1.9.2+
 */

yukon.namespace('yukon.dr.dashboard');

yukon.dr.dashboard = (function() {
    
    var timeFormatter = yukon.timeFormatter,
        mod;

    mod = {
            
        init: function() {
            
            /** 
             * Initializes a jquery slider to represent an amount of time in minutes.
             * 
             * @param opts - Object that should contain the following:
             *               htmlSelector {string} - The css selector of the element containing the human readable time.
             *               timeSelector {string} - The css selector of the hidden input storing the time value.
             *               sliderSelector {string} - The css selector of the element to use as the jquery slider.
             *               max {number} - The maximum value of the slider.
             *               min {number} - The minimum value of the slider.
             *               value {number} - The initial value of the slider.
             *               step {number} - The amount between valid values.
             */
            var _sliderInit = function (opts) {
                    var slideOrChange = function (event, ui) {
                        var curTime = timeFormatter.formatTime(ui.value, 0);
                        $(opts.htmlSelector).html(curTime);
                        $(opts.timeSelector).val(ui.value);
                    };
                    $(opts.sliderSelector).slider({
                        max: opts.max,
                        min: opts.min,
                        value: opts.value,
                        step: opts.step,
                        slide: function (event, ui) {
                            slideOrChange(event, ui);
                        },
                        change: function (event, ui) {
                            slideOrChange(event, ui);
                        }
                    });
                },
                sliderInitOptions = [
                    /** Setup the command time slider */
                    ['#broadcast-config .f-time-slider', 24 * 60 - 15, 0, $('#rf-performance-command-time').val(), 15, '#broadcast-config .f-time-label', '#rf-performance-command-time'],
                    /** Setup the email time slider */
                    ['#broadcast-config .f-email-time-slider', 24 * 60 - 15, 0, $('#rf-performance-email-time').val(), 15, '#broadcast-config .f-email-time-label', '#rf-performance-email-time'],
                    /** Setup the error check time slider */
                    ['#ecobee-config .f-time-slider', 24 * 60 - 15, 0, $('#ecobee-error-check-time').val(), 15, '#ecobee-config .f-time-label', '#ecobee-error-check-time']
                ],
                _io,
                _initOpt;

            for (_io = 0; _io < sliderInitOptions.length; _io += 1) {
                _initOpt = sliderInitOptions[_io];
                _sliderInit({
                    sliderSelector: _initOpt[0],
                    max: _initOpt[1],
                    min: _initOpt[2],
                    value: _initOpt[3],
                    step: _initOpt[4],
                    htmlSelector: _initOpt[5],
                    timeSelector: _initOpt[6]
                });
            }
            /** Setup the time label */
            $('#broadcast-config .f-time-label').html(timeFormatter.formatTime($('#rf-performance-command-time').val(), 0));
            $('#broadcast-config .f-email-time-label').html(timeFormatter.formatTime($('#rf-performance-email-time').val(), 0));
            $('#ecobee-config .f-time-label').html(timeFormatter.formatTime($('#ecobee-error-check-time').val(), 0));
            
            /** Handle email on/off toggle button.  TODO make on-off toggle button resuable */
            $(document).on('click', '#broadcast-config .toggle-on-off .button', function() {
                $('#broadcast-config .toggle-on-off .button').toggleClass('on');
                $('.f-notif-group').toggle('fade');
                $('.f-email-schedule').toggle('fade');
                if ($('#broadcast-config .toggle-on-off .yes').hasClass('on')) {
                    $('#rf-performance-email').val('true');
                } else {
                    $('#rf-performance-email').val('false');
                }
                
            });
            
            $(document).on('click', '#ecobee-error-checking-toggle .toggle-on-off .button', function () {
                $('#ecobee-error-checking-toggle .toggle-on-off .button').toggleClass('on');
                if ($('#ecobee-error-checking-toggle .toggle-on-off .button.yes').hasClass('on')) {
                    $('#ecobee-check-errors').val('true');
                    $('#ecobee-error-check-schedule').toggle('fade');
                } else {
                    $('#ecobee-check-errors').val('false');
                    // hide daily error check row
                    $('#ecobee-error-check-schedule').toggle('fade');
                }
            });
            
            $(document).on('click', '#ecobee-data-collection-toggle .toggle-on-off .button', function() {
                $('#ecobee-data-collection-toggle .toggle-on-off .button').toggleClass('on');
                if ($('#ecobee-data-collection-toggle .toggle-on-off .button.yes').hasClass('on')) {
                    $('#ecobee-data-collection').val('true');
                } else {
                    $('#ecobee-data-collection').val('false');
                }
            });

            if ($('#rf-performance-email').val() === 'true') {
                $('#broadcast-config .toggle-on-off .yes').addClass('on');
                $('#broadcast-config .toggle-on-off .no').removeClass('on');
                $('.f-notif-group').show();
                $('.f-email-schedule').show();
            } else {
                $('#broadcast-config .toggle-on-off .no').addClass('on');
                $('#broadcast-config .toggle-on-off .yes').removeClass('on');
                $('.f-notif-group').hide();
                $('.f-email-schedule').hide();
            }
            
            if ('true' === $('#ecobee-check-errors').val()) {
                $('#ecobee-config #ecobee-check-errors .toggle-on-off .yes').addClass('on');
                $('#ecobee-config #ecobee-check-errors .toggle-on-off .no').removeClass('on');
            } else {
                $('#ecobee-config #ecobee-check-errors .toggle-on-off .no').addClass('on');
                $('#ecobee-config #ecobee-check-errors .toggle-on-off .yes').removeClass('on');
            }
            
            if ('true' === $('#ecobee-data-collection').val()) {
                $('#ecobee-config #ecobee-data-collection .toggle-on-off .yes').addClass('on');
                $('#ecobee-config #ecobee-data-collection .toggle-on-off .no').removeClass('on');
            } else {
                $('#ecobee-config #ecobee-data-collection .toggle-on-off .no').addClass('on');
                $('#ecobee-config #ecobee-data-collection .toggle-on-off .yes').removeClass('on');
            }
        }
    };
    
    return mod;
}());

$(function() { yukon.dr.dashboard.init(); });