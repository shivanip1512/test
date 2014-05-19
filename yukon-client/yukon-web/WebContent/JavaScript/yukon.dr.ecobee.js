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
         * Update time label and and value.
         * @param {jQuery} event.
         * @param {Object} ui.
         */
        _updateTime = function (event, ui) {
            var curTime = _timeFormatter.formatTime(ui.value, 0);
            $('#ecobee-download-schedule .f-time-label').html(curTime);
            $('#ecobee-download-time').val(ui.value);
        },
        mod;

    mod = {
        /** 
         * Initialize ecobee module: time slider and initialize value for time label.
         */
        init : function () {
            $('#ecobee-download-schedule .f-time-slider').slider({
                max: 24 * 60 - 15,
                min: 0,
                value: 120,
                step: 15,
                slide: function (event, ui) {
                    _updateTime(event, ui);
                },
                change: function (event, ui) {
                    _updateTime(event, ui);
                }
            });

            $('#ecobee-download-schedule .f-time-label').html(_timeFormatter.formatTime($('#ecobee-download-time').val(), 0));
            try {
                if ('undefined' !== typeof loadGroupPicker) {
                    loadGroupPicker.show.call(loadGroupPicker, true);
                }
            } catch (pickerException) {
                debug.log('pickerException: ' + pickerException);
            };

        }
    };
    return mod;
})();
$(function () {
    yukon.dr.ecobee.init();
});
