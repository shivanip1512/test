yukon.namespace('yukon.dr.dashboard');

/**
 * Module for the dr dashboard page
 * @module   yukon.dr.dashboard
 * @requires JQUERY
 * @requires JQUERY UI
 */
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
                    // max, min, value, step, htmlSelector, timeSelector
                    /** Setup the command time slider */
                    ['#broadcast-config .js-time-slider', 24 * 60 - 15, 0, $('#rf-performance-command-time').val(), 15, '#broadcast-config .js-time-label', '#rf-performance-command-time'],
                    /** Setup the email time slider */
                    ['#broadcast-config .js-email-time-slider', 24 * 60 - 15, 0, $('#rf-performance-email-time').val(), 15, '#broadcast-config .js-email-time-label', '#rf-performance-email-time']
                ],
                _io,
                _initOpt,
                _initSliders = function () {
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
                },
                _originalRfCommandTime = $('#rf-performance-command-time').val(),
                _originalRfEmailTime = $('#rf-performance-email-time').val();

            _initSliders();

            /** Setup the time label */
            $('#broadcast-config .js-time-label').html(timeFormatter.formatTime($('#rf-performance-command-time').val(), 0));
            $('#broadcast-config .js-email-time-label').html(timeFormatter.formatTime($('#rf-performance-email-time').val(), 0));

            $(document).on('click', '#broadcast-config .button-group-toggle .button', function() {
                
                if ($('#broadcast-config .button-group-toggle .yes').hasClass('on')) {
                    $('#rf-performance-email').val('true');
                    $('.js-notif-group').show('fade');
                    $('.js-email-schedule').show('fade');
                } else {
                    $('#rf-performance-email').val('false');
                    $('.js-notif-group').hide('fade');
                    $('.js-email-schedule').hide('fade');
                }
                
            });

            if ($('#rf-performance-email').val() === 'true') {
                $('#broadcast-config .button-group-toggle .yes').addClass('on');
                $('#broadcast-config .button-group-toggle .no').removeClass('on');
                $('.js-notif-group').show();
                $('.js-email-schedule').show();
            } else {
                $('#broadcast-config .button-group-toggle .no').addClass('on');
                $('#broadcast-config .button-group-toggle .yes').removeClass('on');
                $('.js-notif-group').hide();
                $('.js-email-schedule').hide();
            }

            $(document).on('yukon.dr.rf.config.load', function (ev) {
                // each time the configure button popup is loaded, reset the field values of the times
                // and reinit the sliders so they accurately reflect the current settings in the database
                $('#rf-performance-command-time').val(_originalRfCommandTime);
                $('#rf-performance-email-time').val(_originalRfEmailTime);
                _initSliders();
            });
        }
    };
    
    return mod;
}());

$(function() { yukon.dr.dashboard.init(); });