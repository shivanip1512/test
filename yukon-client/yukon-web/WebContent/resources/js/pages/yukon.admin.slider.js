yukon.namespace('yukon.admin.slider');

/**
 * Module that manages the slider in the Global Settings
 * @module  yukon.admin.slider
 * @requires JQUERY
 * @requires JQUERY UI
 */
yukon.admin.slider = (function () {
    
    _mod = {

        init : function () {
            $(document).ready(function() {
                $('div.slider-range').slider({
                	min: 0,
                    max: 1439,
                    values: [540, 1020],
                    slide: function(event, ui) {
                        var inputs= $(this).prevAll('.sliderValue');
                        var i=1;
                        inputs.each(function(index) {
                            $(this).val(ui.values[i--]);
                        });
                        var startTime = getTime(parseInt(ui.values[0] / 60 % 24, 10), parseInt(ui.values[0] % 60, 10));
                            endTime = getTime(parseInt(ui.values[1] / 60 % 24, 10), parseInt(ui.values[1] % 60, 10));
                        $(this).prevAll('.js-time-label').text(startTime + ' - ' + endTime);
                    }
                });

                function getTime(hours, minutes) {
                    var time = null;
                    minutes = minutes + "";
                    time = (hours < 12) ? "AM" : "PM";
                    if (hours == 0) {
                        hours = 12;
                    }
                    if (hours > 12) {
                        hours = hours - 12;
                    }
                    if (minutes.length == 1) {
                        minutes = "0" + minutes;
                    }
                    return hours + ":" + minutes + " " + time;
                }
                setTimeout(function() { 
                    $(".slider-range").each(function(index) {
                        var val1, val2;
                        $(this).prevAll('.sliderValue').each(function(i) {
                            if( i == 0) {
                                val1=$(this).val();
                            }
                            if(i == 1) {
                                val2=$(this).val();
                            }
                        });
                        $(this).slider('values',0,val2);
                        $(this).slider('values',1,val1);
                        var startTime = getTime(parseInt(val2 / 60 % 24, 10), parseInt(val2 % 60, 10));
                            endTime = getTime(parseInt(val1 / 60 % 24, 10), parseInt(val1 % 60, 10));
                       $(this).prevAll('.js-time-label').text(startTime + ' - ' + endTime);
                    });
                }, 1);
            });
        }
    };
    return _mod;
}());

$(function () { yukon.admin.slider.init(); });