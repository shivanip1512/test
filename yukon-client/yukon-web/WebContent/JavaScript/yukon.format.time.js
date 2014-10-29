yukon.namespace('yukon.timeFormatter');

/**
 * Module handles the time formatting. Generates a time in the format hh:mm AM (12:30 PM) for the input which is 
 * the number of minutes from midnight.
 * @module yukon.timeFormatter
 */
yukon.timeFormatter = (function () {
    var mod;

    mod = {
        // Generates a time in the format hh:mm AM (12:30 PM) for the input which is 
        // the number of minutes from midnight
        formatTime: function (minutes, roundTo) {
            var hour,
                minute,
                ampmStr,
                hourStr,
                minuteStr;

            minutes = parseInt(minutes, 10);

            // this gets a little complicated: if a non-numerical string or null is passed to parseInt,
            // parseInt returns NaN, so check for that here
            // NaN is the only value in JavaScript that is not equal to itself
            if (minutes !== minutes) {
                return '12:00AM';
            }
            // Make sure time is not negative and less than the number of minutes in a day
            if (minutes < 0) {
                minutes = 0;
            }
            if (minutes >= 1440) {
                minutes = 24 * 60 - 1;
            }
            
            if (typeof roundTo !== 'undefined' && roundTo !== 0) {
                // Make sure the value is a multiple of 10
                minutes = Math.floor(minutes / roundTo) * roundTo;
            }
            
            hour = Math.floor(minutes / 60);
            minute = minutes % 60;
            
            ampmStr = "AM";
            if (hour >= 12) {
                ampmStr = "PM";
                if (hour > 12) {
                    hour = hour - 12;
                }
            } else if (hour === 0) {
                hour = 12;
            }
            
            // Add a leading zero to hours and minutes if needed
            hourStr = "" + hour;
            hourStr = hourStr.substr(hourStr.length-2, 2);
            minuteStr = "0" + minute;
            minuteStr = minuteStr.substr(minuteStr.length-2, 2);
            
            return hourStr + ":" + minuteStr + " " + ampmStr;
            
        },
    
        // Parses time in format - hh:mm AM into the number of minutes from midnight
        parseTime: function (time) {
    
            // Make sure the time string is a valid time (ex: 12:30 PM)     
            var exp = /^([0]*[1-9]|[1][0-2])([:][0-5][0-9]){0,1}\s{0,1}([AaPp][Mm])$/,
                timeExp,
                pmExp,
                timeArray,
                pm,
                hours,
                minutes,
                returnValue;
            
            if (!exp.test(time)) {
                return -1;
            }
            
            // Get the hours, minutes and determine if the time is pm
            timeExp = /\d{1,2}/g;
            pmExp = /[Pp][Mm]$/;
            
            timeArray = time.match(timeExp);
            pm = time.match(pmExp) !== null;
            
            hours = parseInt(timeArray[0], 10);
            minutes = parseInt(timeArray[1], 10);
            
            // If it's pm and not 12, add 12 to hours
            if (pm && hours != 12) {
                hours = hours + 12;
            } else if (!pm && hours === 12) {
                hours = 0;
            }
            
            // Calculate the minutes from midnight
            returnValue = minutes || 0;
            returnValue = returnValue + (60 * hours);
            
            return returnValue;
        }
    };
    return mod;
})();
