/**
 * Manager for date time and dateTime pickers. We use:
 *  - https://github.com/trentrichardson/jQuery-Timepicker-Addon
 *  - http://keith-wood.name/datetimeEntry.html
 */
yukon.namespace('yukon.ui.dateTimePickers');
yukon.ui.dateTimePickers = function () {
    
    var _initialized = false,
        cachedcfgDtArgs,
        cachedcfgTpArgs,

        _insertTimezone = function (self) {
            var tzShort = self.data('timeZoneShort');
            //there are some implementations of LocalDate out there, ugh...
            if (typeof tzShort !== 'undefined') {
                self.after('<div class="timezone_container" title="' + self.data('timeZoneFull') + '">' + tzShort + '</div>');
            }
        },

        _onBeforeShow = function (input) {
            $('#ui-datepicker-div').addClass($(input).data('class'));
            
            //only show the picker if the input is enabled
            return !$(input).prop('disabled');
        },

        _getPickerArgs = function (self) {
            var args = {
                beforeShow: _onBeforeShow,
                stepHour: _getStepHour(self),
                stepMinute: _getStepMinute(self)
            },
            max_date = self.data('maxDate'),
            min_date;
            if (typeof max_date !== 'undefined' && max_date !== '') {
                args.maxDate = max_date;
            }
            min_date = self.data('minDate');
            if (typeof min_date !== 'undefined' && min_date !== '') {
                args.minDate = min_date;
            }
            return args;
        },

        /**
         * Gets the step hour for the datetimepicker and timepicker plugins. Defaults to 1.
         * @param {Object} self The $ object of the input field
         * @param {String} self.data('stepHour') The step hour value
         */
        _getStepHour = function (self) {
            return _parseIntSafe(self.data('stepHour'));
        },

        /**
         * Gets the step minute for the datetimepicker and timepicker plugins. Defaults to 1.
         * @param {Object} self The $ object of the input field
         * @param {String} self.data('stepMinute') The step minute value
         */
        _getStepMinute = function (self) {
            return _parseIntSafe(self.data('stepMinute'));
        },

        /**
         * Gets the timeSteps for the dateTimeEntry (keyboard & mouse manipulation) plugin. Both hour and minute default to 1.
         * @param {Object} self The $ object of the input field
         * @param {String} self.data('stepHour') The step hour value
         * @param {String} self.data('stepMinute') The step minute value
         */
        _getTimeSteps = function (self) {
            var step_hour = _parseIntSafe(self.data('stepHour')),
                step_minute = _parseIntSafe(self.data('stepMinute'));
            return [step_hour, step_minute, 1];
        },

        /**
         * Returns an integer, parsing num if posible. Otherwise 1 is returned as default.
         * @param {String} numString - The integer to parse
         */
        _parseIntSafe = function (numString) {
            return (typeof numString !== 'undefined' && numString !== '') ? parseInt(numString, 10) : 1;
        },
        mod = {};

    // public interface
    mod = {
        ancestorInit : function (elemId) {
            //mod.init(cachedcfgDtArgs, cachedcfgTpArgs, elemId);
            yukon.ui.initDateTimePickers(cachedcfgDtArgs, cachedcfgTpArgs, elemId);
        },
        /**
         * 
         */
        init : function (cfgDtArgs, cfgTpArgs) {
            //default arguments
            var datetimepickerArgs = {
                    buttonImage: yukon.url('/WebConfig/yukon/Icons/StartCalendar.png'),
                    buttonImageOnly: true,
                    hideIfNoPrevNext:true,
                    showButtonPanel: true,
                    showOn: 'button',
                    showOtherMonths: true,
                    selectOtherMonths: true
                },
                timepickerArgs = cfgTpArgs,
                nargs = arguments.length,
                ancestor = 'body ';
    
            // called from widgetActionRefreshImage.tag with no args
            if (0 === nargs) {
                $.extend(datetimepickerArgs, cachedcfgDtArgs);
                timepickerArgs = cachedcfgTpArgs;
            } else {
                // called either from yukon.date.time.picker.js.jsp with 2 args
                // or from render.jsp via ancestorInit with 3 args
                if (3 === nargs) {
                    ancestor = arguments[2] + ' '; // parent and any child
                } else {
                    cachedcfgDtArgs = cfgDtArgs;
                    cachedcfgTpArgs = cfgTpArgs;
                }
                $.extend(datetimepickerArgs, cfgDtArgs);
            }
            if (true === _initialized && 2 === nargs) {
                return;
            }
            // Date
            $(ancestor + 'input.js-datePicker').each(function () {
                var self = $(this);
                self.datetimeEntry({
                    datetimeFormat: self.data('dateTimeFormat'),
                    spinnerImage: '',
                    maxDatetime: self.data('maxDate'),
                    minDatetime: self.data('minDate')
                });
                
                if (self.hasClass('js-dateStart')) {
                    self.change(function () {
                        $(this).closest('.js-dateRange').find('.js-dateEnd').datetimeEntry('option', 'minDatetime', $(this).datetimeEntry('getDatetime'));
                    });
                }
                if (self.hasClass('js-dateEnd')) {
                    self.change(function () {
                        $(this).closest('.js-dateRange').find('.js-dateStart').datetimeEntry('option', 'maxDatetime', $(this).datetimeEntry('getDatetime'));
                    });
                }
                
            }).removeClass('js-datePicker').closest('.datetimeEntry_wrap').addClass('date'); //this class is used to set a fixed width based on the type of input we are creating
            $(ancestor + 'input.js-datePickerUI').each(function () {
                var self = $(this),
                    args = _getPickerArgs(self),
                    defaultArgs;
                
                if (self.hasClass('js-dateStart')) {
                    args.onSelect = function (selectedDate) {
                        $(this).closest('.js-dateRange').find('.js-dateEnd').datepicker( 'option', 'minDate', selectedDate );
                    };
                }
                if (self.hasClass('js-dateEnd')) {
                    args.onSelect = function (selectedDate) {
                        $(this).closest('.js-dateRange').find('.js-dateStart').datepicker( 'option', 'maxDate', selectedDate );
                    };
                }
                
                //copy the defaults
                defaultArgs = {};
                $.extend(defaultArgs, datetimepickerArgs);
                self.datepicker($.extend(defaultArgs, args));
                _insertTimezone(self);
            }).removeClass('js-datePickerUI');
    
            // Date + Time
            $(ancestor + 'input.js-dateTimePicker').each(function () {
                var self = $(this);
                self.datetimeEntry({
                    datetimeFormat: self.data('dateTimeFormat'),
                    maxDatetime: self.data('maxDate'),
                    minDatetime: self.data('minDate'),
                    timeSteps: _getTimeSteps(self),
                    spinnerImage: ''
                });
            }).removeClass('js-dateTimePicker').closest('.datetimeEntry_wrap').addClass('dateTime'); //this class is used to set a fixed width based on the type of input we are creating
            $(ancestor + 'input.js-dateTimePickerUI').each(function () {
                var self = $(this);
                //copy the defaults
                var defaultArgs = {};
                $.extend(defaultArgs, datetimepickerArgs);
                self.datetimepicker($.extend(defaultArgs, _getPickerArgs(self)));
                _insertTimezone(self);
            }).removeClass('js-dateTimePickerUI');
    
            // Time
            $(ancestor + 'input.js-timePicker').each(function () {
                var self = $(this);
                self.datetimeEntry({
                    datetimeFormat: self.data('dateTimeFormat'),
                    timeSteps: _getTimeSteps(self),
                    spinnerImage: ''
                });
            }).removeClass('js-timePicker').closest('.datetimeEntry_wrap').addClass('time'); //this class is used to set a fixed width based on the type of input we are creating
            $(ancestor + 'input.js-timePickerUI').each(function () {
                var self = $(this);
                //copy the defaults
                var defaultArgs = {};
                $.extend(defaultArgs, datetimepickerArgs);
                $.extend(defaultArgs, timepickerArgs);
                self.timepicker($.extend(defaultArgs, _getPickerArgs(self)));
                _insertTimezone(self);
            }).removeClass('js-timePickerUI');
            _initialized = true;
        }
    };
    this.initialNowVal = null;
    return mod;
};

// this should be the only interface through which date and date time pickers are created
yukon.ui.initDateTimePickers = function (cfgLocalization, cfgTimepickerArgs) {
    var dateTimePicker = new yukon.ui.dateTimePickers();
    try {
        dateTimePicker.init(cfgLocalization, cfgTimepickerArgs);
    } catch (datetimepickerex) {
        alert('dateTimePickers: exception in yukon.ui.dateTimePickers.init: ' + datetimepickerex);
    }
    return dateTimePicker;
};