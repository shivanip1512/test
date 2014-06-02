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
            var value = $(inputSelector).val();
            $(containingDivSelector +' .f-time-slider').slider({
                max: 24 * 60 - 15,
                min: 0,
                value: value,
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
            $(containingDivSelector + ' .f-time-label').text(_timeFormatter.formatTime(value, 0));
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
                $('#ecobee-error-checking-toggle .toggle-on-off .yes').addClass('on');
                $('#ecobee-error-checking-toggle .toggle-on-off .no').removeClass('on');
                $('#ecobee-error-check-schedule').show();
            } else {
                $('#ecobee-error-checking-toggle .toggle-on-off .no').addClass('on');
                $('#ecobee-error-checking-toggle .toggle-on-off .yes').removeClass('on');
                $('#ecobee-error-check-schedule').hide();
            }

            if ('true' === $('#ecobee-data-collection').val()) {
                $('#ecobee-data-collection-toggle .toggle-on-off .yes').addClass('on');
                $('#ecobee-data-collection-toggle .toggle-on-off .no').removeClass('on');
                $('#ecobee-data-collection-schedule').show();
            } else {
                $('#ecobee-data-collection-toggle .toggle-on-off .no').addClass('on');
                $('#ecobee-data-collection-toggle .toggle-on-off .yes').removeClass('on');
                $('#ecobee-data-collection-schedule').hide();
            }
        }
    };
    return mod;
})();
$(function () {
    yukon.dr.ecobee.init();
});
