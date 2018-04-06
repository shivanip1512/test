yukon.namespace('yukon.ui.timeSlider');

/**
 * Module that handles the behavior of the Time Slider
 * @module yukon.ui.timeSlider
 * @requires JQUERY
 * @requires yukon
 */
yukon.ui.timeSlider= (function () {
    
    'use strict';
    
    var
    _initialized = false,
    
    _timeFormatter = yukon.timeFormatter,
    
    _initializeTimeSlider = function () {
        $('.js-time-slider-div').each(function(idx, elm) {
            var timeLabel = $(this).find('.js-time-label'),
                timeSlider = $(this).find('.js-time-slider'),
                startTime = $(this).find('.js-start-time'),
                endTime = $(this).find('.js-end-time'),
                stepValue = timeSlider.data("stepValue"),
                timeFormat = timeSlider.data("timeFormat"),
                minValue = timeSlider.data("minValue"),
                maxValue = timeSlider.data("maxValue"),
                showLabels = timeSlider.data("showLabels"),
                startTimeValue = startTime.val(),
                defaultStart = startTimeValue,
                range = endTime.length == 1 ? true : false,
                minValue = minValue ? minValue : 0,
                maxValue = maxValue ? maxValue : 1440;
            //format time
            defaultStart = _convertTimeToSliderValue(timeFormat, startTimeValue);
            defaultStart = defaultStart > -1 ? defaultStart : minValue;
            if (range) {
                var defaultEnd = endTime.val();
                defaultEnd = _convertTimeToSliderValue(timeFormat, defaultEnd);
                defaultEnd = defaultEnd > -1 ? defaultEnd : maxValue;
            }
            
            //initialize time slider
            timeSlider.slider({
                max: maxValue,
                min: minValue,
                step: stepValue ? stepValue : 15,
                range: range,
                slide: function (ev, ui) {
                    if (range) {
                        //do not allow a range greater than 24 hours
                        if ((((parseInt(ui.values[1]) - parseInt(ui.values[0])) / 60) > 24)
                                || (parseInt(ui.values[0]) >= 1440)
                                || ((parseInt(ui.values[1]) - parseInt(ui.values[0])) < 15)) {
                            return false;
                        }
                        startTime.val(_convertSliderValueToTime(timeFormat, ui.values[0]));
                        endTime.val(_convertSliderValueToTime(timeFormat, ui.values[1]));
                        var start = _timeFormatter.formatTime(ui.values[0], 0);
                        var end = _timeFormatter.formatTime(ui.values[1], 0);
                        timeLabel.text(start + ' - ' + end);
                    } else {
                        startTime.val(_convertSliderValueToTime(timeFormat, ui.value));
                        var start = _timeFormatter.formatTime(ui.value, 0);
                        timeLabel.text(start);
                    }
                },
            });
            var start = _timeFormatter.formatTime(defaultStart, 0);
            startTime.val(_convertSliderValueToTime(timeFormat, defaultStart));
            if (range) {
                timeSlider.slider("option", "values", [defaultStart, defaultEnd]);
                var end = _timeFormatter.formatTime(defaultEnd, 0);
                timeLabel.text(start + ' - ' + end);
                timeSlider.find('.ui-slider-range').css({"background" : "#38c", "height" : "12px", "padding" : "0"});
                endTime.val(_convertSliderValueToTime(timeFormat, defaultEnd));
            } else {
                timeSlider.slider("option", "value", defaultStart);
                timeLabel.text(start);
            }
            if (showLabels) {
                _showLabels(timeSlider);
            }
        });
    },
    
    
    _convertSliderValueToTime = function (timeFormat, sliderValue) {
        if (timeFormat == 'HHMM') {
            return _timeFormatter.format24HourTime(sliderValue, 0);
        } else if (timeFormat == 'SECONDS') {
            return sliderValue * 60;
        }
        return sliderValue;
    },
    
    _convertTimeToSliderValue = function (timeFormat, timeValue) {
        if (timeFormat == 'HHMM') {
            return _timeFormatter.parse24HourTime(timeValue);
        } else if (timeFormat == 'SECONDS') {
            return timeValue / 60;
        }
        return timeValue;
    },
    
    _showLabels = function (timeSlider) {
        //add labels if applicable
        var min = timeSlider.slider("option", "min"),
            max = timeSlider.slider("option", "max"),
            quarters = (max - min) / 4,
            minTime = _timeFormatter.formatTime(min, 0),
            maxTime = _timeFormatter.formatTime(max, 0),
            firstQuarterTime = _timeFormatter.formatTime(min + quarters, 0),
            secondQuarterTime = _timeFormatter.formatTime(min + (2 * quarters), 0),
            thirdQuarterTime = _timeFormatter.formatTime(min + (3 * quarters), 0);

        var table = $('<table style="width:100%;margin-top:10px;font-size:7.5px;">');
        var row = $('<tr>');
        
        var td = $('<td class="PL0" style="width:20%;">');
        $('<span>').text(minTime).appendTo(td);
        td.appendTo(row);
        var td2 = $('<td style="width:25%;">');
        $('<span>').text(firstQuarterTime).appendTo(td2);
        td2.appendTo(row);
        var td3 = $('<td style="width:25%;">');
        $('<span>').text(secondQuarterTime).appendTo(td3);
        td3.appendTo(row);
        var td4 = $('<td style="width:16%;">');
        $('<span>').text(thirdQuarterTime).appendTo(td4);
        td4.appendTo(row);
        var td5 = $('<td align="right" class="PL0" style="padding-right:0px;">');
        $('<span>').text(maxTime).appendTo(td5);
        td5.appendTo(row);
        row.appendTo(table);
        table.appendTo(timeSlider);
    },
    
    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            _initializeTimeSlider();
            
            if (_initialized) return;
                                                
            _initialized = true;
        }
        
    };
    
    return mod;
})();

$(function () { yukon.ui.timeSlider.init(); });