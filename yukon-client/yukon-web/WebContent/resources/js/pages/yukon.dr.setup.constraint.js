yukon.namespace('yukon.dr.setup.constraint');

/**
 * Module that handles the behavior on Program Constraint page.
 * @module yukon.dr.setup.constraint
 * @requires JQUERY
 * @requires yukon
 */
yukon.dr.setup.constraint = (function() {
    
    'use strict';
    
    var
    _initialized = false,
    
    _enableHolidayUsage = function(){
        var _holidaySchedule = $('#js-holiday-schedule').val()
        if(_holidaySchedule === "0") {
            $("input[name='holidayUsage']").prop('disabled', true);
        }else{
            $("input[name='holidayUsage']").filter('[value="EXCLUDE"]').attr('checked', true);
            $("input[name='holidayUsage']").prop('disabled', false);
        }
    },
    _appendSeconds = function(){
        $("#js-maxActivate-seconds").after('<span> &nbsp;'+ yg.text.seconds +'</span>')
        $("#js-maxDailyOps-seconds").after('<span> &nbsp;'+ yg.text.seconds +'</span>')
        $("#js-minActivate-seconds").after('<span> &nbsp;'+ yg.text.seconds +'</span>')
        $("#js-minRestart-seconds").after('<span> &nbsp;'+ yg.text.seconds +'</span>')
    },
    
    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
            $(document).ready(_enableHolidayUsage);
            $(document).ready(_appendSeconds);
            
            $(document).on('click', '#js-cancel-btn', function (event) {
                window.history.back();
            });
            
            $(document).on("yukon:constraint:delete", function () {
                yukon.ui.blockPage();
                $('#delete-constraint-form').submit();
            });
            
            $(document).on('change', '#js-holiday-schedule', function () {
                _enableHolidayUsage();
            });
            
            _initialized = true;
        }
    };
    
    return mod;
})();

$(function () { yukon.dr.setup.constraint.init(); });