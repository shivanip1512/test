TimeFormatter = Class.create();

TimeFormatter.prototype = { 
    initialize: function() {
    },

    // Generates a time in the format HHhmm (12h30) for the input which is 
    // the number of minutes from midnight
    formatTime: function(minutes, roundTo) {
        
        if(isNaN(minutes) || minutes == null){
            return '00h00';
        }
        
        // Make sure time is not negative and less than the number of minutes in a day
        if (minutes < 0) {
            minutes = 0;
        }
        if (minutes >= 1440) {
            minutes = 24 * 60 - 1;
        }
        
        if(typeof(roundTo) != 'undefined' && roundTo != 0){
            // Make sure the value is a multiple of 10
            minutes = Math.floor(minutes / roundTo) * roundTo;
        }
        
        var hour = Math.floor(minutes / 60);
        var minute = minutes % 60;
        
        // Add a leading zero to hours and minutes if needed
        var hourStr = "0" + hour;
        hourStr = hourStr.substr(hourStr.length-2, 2);
        var minuteStr = "0" + minute;
        minuteStr = minuteStr.substr(minuteStr.length-2, 2);
        
        return hourStr + "h" + minuteStr;
        
    },
    
    // Parses time in format - hh:mm AM into the number of minutes from midnight
    parseTime: function(time) {

        // Make sure the time string is a valid time (ex: 12h30)      
        var exp = /^([0-1][0-9]|[2][0-3])[h][0-5][0-9]$/;
        
        if(!exp.test(time)) {
            return -1;
        }
        
        // Get the hours and minutes
        var timeExp = /\d{2}/g;
        
        var timeArray = time.match(timeExp);
        
        var hours = parseInt(timeArray[0], 10);
        var minutes = parseInt(timeArray[1], 10);
        
        // Calculate the minutes from midnight
        var returnValue = minutes;
        returnValue = returnValue + (60 * hours);
        
        return returnValue;
    }
    
}

var timeFormatter = new TimeFormatter();
