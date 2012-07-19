<%@ page contentType="text/javascript" %>

<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

if(typeof(Yukon) == 'undefined') {
    Yukon = {};
}

if(typeof(Yukon.ui) == 'undefined') {
    Yukon.ui = {};
}

if(typeof(Yukon.ui.dateTimePickers) == 'undefined') {
	/**
	 * Manager for date time and dateTime pickers. We use:
	 * 	- https://github.com/trentrichardson/jQuery-Timepicker-Addon
	 *  - http://keith-wood.name/datetimeEntry.html
	 */
    Yukon.ui.dateTimePickers = {
    	_initialized: false,
    	
    	/**
    	 * 
    	 */
    	init: function(){
    		//if(!this._initialized){
    		//	this._initialized = true;

    			//default aruguments
    			var datetimepickerArgs = {
   					buttonImage: "/WebConfig/yukon/Icons/StartCalendar.gif",
   					buttonImageOnly: true,
   					hideIfNoPrevNext:true,
   					showButtonPanel: true,
   					showOn: "button",
   					showOtherMonths: true,
					selectOtherMonths: true,
   					
   					//localization
					clearText: "<cti:msg key="yukon.common.js.dateTimePicker.clearText"/>",
					clearStatus: "<cti:msg key="yukon.common.js.dateTimePicker.clearStatus"/>",
					closeText: "<cti:msg key="yukon.common.js.dateTimePicker.closeText"/>",
					closeStatus: "<cti:msg key="yukon.common.js.dateTimePicker.closeStatus"/>",
					prevText: "<cti:msg key="yukon.common.js.dateTimePicker.prevText"/>",
					prevBigText: "<cti:msg key="yukon.common.js.dateTimePicker.prevBigText"/>",
					prevStatus: "<cti:msg key="yukon.common.js.dateTimePicker.prevStatus"/>",
					prevBigStatus: "<cti:msg key="yukon.common.js.dateTimePicker.prevBigStatus"/>",
					nextText: "<cti:msg key="yukon.common.js.dateTimePicker.nextText"/>",
					nextBigText: "<cti:msg key="yukon.common.js.dateTimePicker.nextBigText"/>",
					nextStatus: "<cti:msg key="yukon.common.js.dateTimePicker.nextStatus"/>",
					nextBigStatus: "<cti:msg key="yukon.common.js.dateTimePicker.nextBigStatus"/>",
					currentText: "<cti:msg key="yukon.common.js.dateTimePicker.currentText"/>",
					currentStatus: "<cti:msg key="yukon.common.js.dateTimePicker.currentStatus"/>",
					monthNames: <cti:msg key="yukon.common.js.dateTimePicker.monthNames"/>,
					monthNamesShort: <cti:msg key="yukon.common.js.dateTimePicker.monthNamesShort"/>,
					monthStatus: "<cti:msg key="yukon.common.js.dateTimePicker.monthStatus"/>",
					yearStatus: "<cti:msg key="yukon.common.js.dateTimePicker.yearStatus"/>",
					weekHeader: "<cti:msg key="yukon.common.js.dateTimePicker.weekHeader"/>",
					weekStatus: "<cti:msg key="yukon.common.js.dateTimePicker.weekStatus"/>",
					dayNames: <cti:msg key="yukon.common.js.dateTimePicker.dayNames"/>,
					dayNamesShort: <cti:msg key="yukon.common.js.dateTimePicker.dayNamesShort"/>,
					dayNamesMin: <cti:msg key="yukon.common.js.dateTimePicker.dayNamesMin"/>,
					dayStatus: "<cti:msg key="yukon.common.js.dateTimePicker.dayStatus"/>",
					dateStatus: "<cti:msg key="yukon.common.js.dateTimePicker.dateStatus"/>",
					dateFormat: "<cti:msg key="yukon.common.js.dateTimePicker.dateFormat"/>",
					firstDay: "<cti:msg key="yukon.common.js.dateTimePicker.firstDay"/>",
					initStatus: "<cti:msg key="yukon.common.js.dateTimePicker.initStatus"/>",					isRTL: <cti:msg key="yukon.common.js.dateTimePicker.isRTL"/>
    			};
    			
    			var timepickerArgs = {
   					timeOnlyTitle: "<cti:msg key="yukon.common.js.timePicker.timeOnlyTitle"/>",
   					timeText: "<cti:msg key="yukon.common.js.timePicker.timeText"/>",
   					hourText: "<cti:msg key="yukon.common.js.timePicker.hourText"/>",
   					minuteText: "<cti:msg key="yukon.common.js.timePicker.minuteText"/>",
   					secondText: "<cti:msg key="yukon.common.js.timePicker.secondText"/>",
   					millisecText: "<cti:msg key="yukon.common.js.timePicker.millisecText"/>",
   					currentText: "<cti:msg key="yukon.common.js.timePicker.currentText"/>",
   					closeText: "<cti:msg key="yukon.common.js.timePicker.closeText"/>",
   					ampm: <cti:msg key="yukon.common.js.timePicker.ampm"/>
    			};
    			
    			// Date
    			var outer_self = this;
    			jQuery("input.f_datePicker").each(function(){
    			    var self = jQuery(this);
    				self.datetimeEntry({
    					datetimeFormat: self.attr('data-date-format'),
    					spinnerImage: '',
    					maxDatetime: self.attr('data-max-date'),
						minDatetime: self.attr('data-min-date')
    				});
    				
					if(self.hasClass('f_dateStart')){
						self.change(function(){
							jQuery(this).closest('.f_dateRange').find('.f_dateEnd').datetimeEntry('change', 'minDatetime', jQuery(this).datetimeEntry('getDatetime'));
						});
					}
					if(self.hasClass('f_dateEnd')){
						self.change(function(){
							jQuery(this).closest('.f_dateRange').find('.f_dateStart').datetimeEntry('change', 'maxDatetime', jQuery(this).datetimeEntry('getDatetime'));
						});
					}
					
    			}).removeClass('f_datePicker');
				jQuery("input.f_datePickerUI").each(function(){
					var self = jQuery(this);
					var args = {
				        beforeShow: outer_self._onBeforeShow,
						maxDate: self.attr('data-max-date'),
						minDate: self.attr('data-min-date')
					};
					
					if(self.hasClass('f_dateStart')){
						args.onSelect = function(selectedDate) {
							jQuery(this).closest('.f_dateRange').find('.f_dateEnd').datepicker( "option", "minDate", selectedDate );
						};
					}
					if(self.hasClass('f_dateEnd')){
						args.onSelect = function(selectedDate) {
							jQuery(this).closest('.f_dateRange').find('.f_dateStart').datepicker( "option", "maxDate", selectedDate );
						};
					}
					
					self.datepicker(jQuery.extend(datetimepickerArgs, args));
					outer_self._insertTimezone(self);
				}).removeClass('f_datePickerUI');
				
    			
    			// Date + Time
				jQuery("input.f_dateTimePicker").each(function(){
				    var self = jQuery(this);
					self.datetimeEntry({
						datetimeFormat: self.attr('data-date-time-format'),
    					maxDatetime: self.attr('data-max-date'),
    					minDatetime: self.attr('data-min-date'),
    					timeSteps: outer_self._getTimeSteps(self),
						spinnerImage: ''
					});
				}).removeClass('f_dateTimePicker');
				jQuery("input.f_dateTimePickerUI").each(function(){
					var self = jQuery(this);
					var args = {
				        beforeShow: outer_self._onBeforeShow,
						maxDate: self.attr('data-max-date'),
						minDate: self.attr('data-min-date'),
                        stepHour: outer_self._getStepHour(self),
                        stepMinute: outer_self._getStepMinute(self)
					};
					self.datetimepicker(jQuery.extend(datetimepickerArgs, args));
					outer_self._insertTimezone(self);
				}).removeClass('f_dateTimePickerUI');
				
				// Time
				jQuery("input.f_timePicker").each(function(){
				    var self = jQuery(this);
					self.datetimeEntry({
						datetimeFormat: self.attr('data-time-format'),
						timeSteps: outer_self._getTimeSteps(self),
						spinnerImage: ''
					});
				}).removeClass('f_timePicker');
				jQuery("input.f_timePickerUI").each(function(){
					var self = jQuery(this);
					var args = {
				        beforeShow: outer_self._onBeforeShow,
						maxDate: self.attr('data-max-date'),
						minDate: self.attr('data-min-date'),
                        stepHour: outer_self._getStepHour(self),
                        stepMinute: outer_self._getStepMinute(self)
					};
					self.timepicker(jQuery.extend(jQuery.extend(datetimepickerArgs, timepickerArgs), args));
					outer_self._insertTimezone(self);
				}).removeClass('f_timePickerUI');
    		//}
    	},
    	_insertTimezone: function(self){
    	    self.after('<div class="timezone_container" title="' + self.attr('data-time-zone-full') + '">' + self.attr('data-time-zone-short') + '</div>');
    	},
    	_onBeforeShow: function(input){
    	    jQuery('#ui-datepicker-div').addClass(jQuery(input).attr('data-class'));
    	},
        /**
         * Gets the step hour for the datetimepicker and timepicker plugins. Defaults to .05.
         * @param {Object} self The jQuery object of the input field
         * @param {String} self.attr('data-step-hour') The step hour value
         */
    	_getStepHour: function(self){
    	    var step_hour = self.attr('data-step-hour');
    	    return typeof(step_hour) !== 'undefined' ? parseFloat(step_hour) : .05;
    	},
        /**
         * Gets the step minute for the datetimepicker and timepicker plugins. Defaults to .05.
         * @param {Object} self The jQuery object of the input field
         * @param {String} self.attr('data-step-minute') The step minute value
         */
    	_getStepMinute: function(self){
            var step_minute = self.attr('data-step-minute');
            return typeof(step_minute) !== 'undefined' ? parseFloat(step_minute) : .05;
    	},
        /**
         * Gets the timeSteps for the dateTimeEntry (keyboard & mouse manipulation) plugin. Both hour and minute default to 1.
         * @param {Object} self The jQuery object of the input field
         * @param {String} self.attr('data-step-hour') The step hour value
         * @param {String} self.attr('data-step-minute') The step minute value
         */
    	_getTimeSteps: function(self){
            var step_hour = self.attr('data-step-hour');
            step_hour = typeof(step_hour) !== 'undefined' ? parseFloat(step_hour) : 1;
            var step_minute = self.attr('data-step-minute');
            step_minute = typeof(step_minute) !== 'undefined' ? parseFloat(step_minute) : 1;
    	    return [step_hour, step_minute, 1];
    	}
    };
}

jQuery(function(){
	Yukon.ui.dateTimePickers.init();
});
