/**
 * Singleton that serves the DR home page ecobee pane and the ecobee details page
 * 
 * @requires jQuery 1.8.3+
 * @requires jQuery UI 1.9.2+
 */
yukon.namespace('yukon.dr.ecobee');
yukon.dr.ecobee = (function () {
    var _timeFormatter = yukon.timeFormatter,
        /** 
         * Setup a slider.
         * @param {String} containingDivSelector. div containing the .f-time-slider and .f-time-label
         * @param {String} inputSelector. - the actual hidden input that holds the current value of this slider
         */
        _setupSlider = function (containingDivSelector, inputSelector) {
            $(containingDivSelector +' .f-time-slider').slider({
                max: 24 * 60 - 15,
                min: 0,
                value: 120,
                step: 15,
                slide: function (event, ui) {
                    $(containingDivSelector + ' .f-time-label').text(_timeFormatter.formatTime(ui.value, 0));
                    $(inputSelector).val(ui.value);
                },
                change: function (event, ui) {
                    $(containingDivSelector + ' .f-time-label').text(_timeFormatter.formatTime(ui.value, 0));
                    $(inputSelector).val(ui.value);
                }
            });
        },
        mod;

    mod = {
        /** 
         * Initialize ecobee module: time slider and initialize value for time label.
         */
        init : function () {
            _setupSlider('#ecobee-download-schedule', '#ecobee-download-time');
            _setupSlider('#ecobee-data-collection-schedule', '#ecobee-data-collection-time');
            _setupSlider('#ecobee-error-check-schedule', '#ecobee-error-check-time');

            $('#ecobee-download-schedule .f-time-label').text(_timeFormatter.formatTime($('#ecobee-download-time').val(), 0));
            $('#ecobee-error-check-schedule .f-time-label').text(timeFormatter.formatTime($('#ecobee-error-check-time').val(), 0));
            $('#ecobee-data-collection-schedule .f-time-label').text(timeFormatter.formatTime($('#ecobee-data-collection-time').val(), 0));

            try {
                if ('undefined' !== typeof loadGroupPicker) {
                    loadGroupPicker.show.call(loadGroupPicker, true);
                }
            } catch (pickerException) {
                debug.log('pickerException: ' + pickerException);
            };

            $(document).on('click', '#ecobee-error-checking-toggle .toggle-on-off .button', function () {
                $('#ecobee-error-checking-toggle .toggle-on-off .button').toggleClass('on');
                var checkErrorsOn = $('#ecobee-error-checking-toggle .toggle-on-off .button.yes').hasClass('on');
                $('#ecobee-check-errors').val(checkErrorsOn ? 'true' : 'false');
                $('#ecobee-error-check-schedule').toggle('fade');
            });

            $(document).on('click', '#ecobee-data-collection-toggle .toggle-on-off .button', function() {
                $('#ecobee-data-collection-toggle .toggle-on-off .button').toggleClass('on');
                var dataCollectionOn = $('#ecobee-data-collection-toggle .toggle-on-off .button.yes').hasClass('on');
                $('#ecobee-data-collection').val(dataCollectionOn ? 'true' : 'false');
                $('#ecobee-data-collection-schedule').toggle('fade');
            });

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
})();
$(function () {
    yukon.dr.ecobee.init();
});
