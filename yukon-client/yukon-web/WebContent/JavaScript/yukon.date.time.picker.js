/**
 * Manager for date time and dateTime pickers. We use:
 *  - https://github.com/trentrichardson/jQuery-Timepicker-Addon
 *  - http://keith-wood.name/datetimeEntry.html
 */

yukon.modules.dateTimePickers = function (box) {
    
    var _initialized = false,
        cachedcfgDtArgs,
        cachedcfgTpArgs,

        _insertTimezone = function(self) {
            //there are some implementations of LocalDate out there, ugh...
            if(typeof(self.attr('data-time-zone-short')) != "undefined"){
                self.after('<div class="timezone_container" title="' + self.attr('data-time-zone-full') + '">' + self.attr('data-time-zone-short') + '</div>');
            }
        },

        _onBeforeShow = function(input){
            $('#ui-datepicker-div').addClass($(input).attr('data-class'));
            
            //only show the picker if the input is enabled
            return !$(input).attr("disabled");
        },

        _getPickerArgs = function(self){
            var args = {
                beforeShow: _onBeforeShow,
                stepHour: _getStepHour(self),
                stepMinute: _getStepMinute(self)
            };
            var max_date = self.attr('data-max-date');
            if (typeof(max_date) !== 'undefined' && max_date !== "") {
                args.maxDate = max_date;
            }
            var min_date = self.attr('data-min-date');
            if (typeof(min_date) !== 'undefined' && min_date !== "") {
                args.minDate = min_date;
            }
            return args;
        },

        /**
         * Gets the step hour for the datetimepicker and timepicker plugins. Defaults to 1.
         * @param {Object} self The $ object of the input field
         * @param {String} self.attr('data-step-hour') The step hour value
         */
        _getStepHour = function(self){
            return _parseIntSafe(self.attr('data-step-hour'));
        },

        /**
         * Gets the step minute for the datetimepicker and timepicker plugins. Defaults to 1.
         * @param {Object} self The $ object of the input field
         * @param {String} self.attr('data-step-minute') The step minute value
         */
        _getStepMinute = function(self){
            return _parseIntSafe(self.attr('data-step-minute'));
        },

        /**
         * Gets the timeSteps for the dateTimeEntry (keyboard & mouse manipulation) plugin. Both hour and minute default to 1.
         * @param {Object} self The $ object of the input field
         * @param {String} self.attr('data-step-hour') The step hour value
         * @param {String} self.attr('data-step-minute') The step minute value
         */
        _getTimeSteps = function(self){
            var step_hour = _parseIntSafe(self.attr('data-step-hour'));
            var step_minute = _parseIntSafe(self.attr('data-step-minute'));
            return [step_hour, step_minute, 1];
        },

        /**
         * Returns an integer, parsing num if posible. Otherwise 1 is returned as default.
         * @param {String} numString - The integer to parse
         */
        _parseIntSafe = function(numString) {
            return (typeof(numString) !== 'undefined' && numString !== "") ? parseInt(numString, 10) : 1;
        };

    // public interface
    box.ancestorInit = function (elemId) {
        box.init(cachedcfgDtArgs, cachedcfgTpArgs, elemId);
    };
    /**
     * 
     */
    box.init = function (cfgDtArgs, cfgTpArgs) {
        //default arguments
        var datetimepickerArgs = {
                buttonImage: yukon.url('/WebConfig/yukon/Icons/StartCalendar.png'),
                buttonImageOnly: true,
                hideIfNoPrevNext:true,
                showButtonPanel: true,
                showOn: "button",
                showOtherMonths: true,
                selectOtherMonths: true
            },
            timepickerArgs = cfgTpArgs,
            nargs = arguments.length,
            ancestor = 'body ',
            outer_self;

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
        if (_initialized && 2 === nargs) {
            return;
        }
        // Date
        outer_self = this;
        $(ancestor + "input.f-datePicker").each(function(){
            var self = $(this);
            self.datetimeEntry({
                datetimeFormat: self.attr('data-date-time-format'),
                spinnerImage: '',
                maxDatetime: self.attr('data-max-date'),
                minDatetime: self.attr('data-min-date')
            });
            
            if(self.hasClass('f-dateStart')){
                self.change(function(){
                    $(this).closest('.f-dateRange').find('.f-dateEnd').datetimeEntry('change', 'minDatetime', $(this).datetimeEntry('getDatetime'));
                });
            }
            if(self.hasClass('f-dateEnd')){
                self.change(function(){
                    $(this).closest('.f-dateRange').find('.f-dateStart').datetimeEntry('change', 'maxDatetime', $(this).datetimeEntry('getDatetime'));
                });
            }
            
        }).removeClass('f-datePicker').closest('.datetimeEntry_wrap').addClass('date'); //this class is used to set a fixed width based on the type of input we are creating
        $(ancestor + "input.f-datePickerUI").each(function(){
            var self = $(this);
            var args = _getPickerArgs(self);
            
            if(self.hasClass('f-dateStart')){
                args.onSelect = function(selectedDate) {
                    $(this).closest('.f-dateRange').find('.f-dateEnd').datepicker( "option", "minDate", selectedDate );
                };
            }
            if(self.hasClass('f-dateEnd')){
                args.onSelect = function(selectedDate) {
                    $(this).closest('.f-dateRange').find('.f-dateStart').datepicker( "option", "maxDate", selectedDate );
                };
            }
            
            //copy the defaults
            var defaultArgs = {};
            $.extend(defaultArgs, datetimepickerArgs);
            self.datepicker($.extend(defaultArgs, args));
            _insertTimezone(self);
        }).removeClass('f-datePickerUI');

        // Date + Time
        $(ancestor + "input.f-dateTimePicker").each(function(){
            var self = $(this);
            self.datetimeEntry({
                datetimeFormat: self.attr('data-date-time-format'),
                maxDatetime: self.attr('data-max-date'),
                minDatetime: self.attr('data-min-date'),
                timeSteps: _getTimeSteps(self),
                spinnerImage: ''
            });
        }).removeClass('f-dateTimePicker').closest('.datetimeEntry_wrap').addClass('dateTime'); //this class is used to set a fixed width based on the type of input we are creating
        $(ancestor + "input.f-dateTimePickerUI").each(function(){
            var self = $(this);
            //copy the defaults
            var defaultArgs = {};
            $.extend(defaultArgs, datetimepickerArgs);
            self.datetimepicker($.extend(defaultArgs, _getPickerArgs(self)));
            _insertTimezone(self);
        }).removeClass('f-dateTimePickerUI');

        // Time
        $(ancestor + "input.f-timePicker").each(function(){
            var self = $(this);
            self.datetimeEntry({
                datetimeFormat: self.attr('data-date-time-format'),
                timeSteps: _getTimeSteps(self),
                spinnerImage: ''
            });
        }).removeClass('f-timePicker').closest('.datetimeEntry_wrap').addClass('time'); //this class is used to set a fixed width based on the type of input we are creating
        $(ancestor + "input.f-timePickerUI").each(function(){
            var self = $(this);
            //copy the defaults
            var defaultArgs = {};
            $.extend(defaultArgs, datetimepickerArgs);
            $.extend(defaultArgs, timepickerArgs);
            self.timepicker($.extend(defaultArgs, _getPickerArgs(self)));
            _insertTimezone(self);
        }).removeClass('f-timePickerUI');
        _initialized = true;
    };
    box.initialNowVal = null;
};
yukon.ui.initDateTimePickers = function (cfgLocalization, cfgTimepickerArgs) {
    yukon.namespace('yukon.modules.dateTimePickers');
    try {
        Sandbox ('dateTimePickers', function (mod) {
            yukon.ui.dateTimePickers = mod;
        });
    } catch (sandboxex) {
        alert('dateTimePickers init: sandbox exception: ' + sandboxex);
    }
    try {
        yukon.ui.dateTimePickers.init(cfgLocalization, cfgTimepickerArgs);
    } catch (datetimepickerex) {
        alert('dateTimePickers: exception in yukon.ui.dateTimePickers.init: ' + datetimepickerex);
    }
};