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
    
    _enableHolidayUsage = function(onPageLoad){
        var _holidaySchedule = $('#js-holiday-schedule').val()
        if(_holidaySchedule === "0") {
            $("input[name='holidayUsage']").prop('disabled', true);
        }else{
            if(onPageLoad == true){
                $("input[name='holidayUsage']").filter('[value="EXCLUDE"]').attr('checked', true);
            }
            $("input[name='holidayUsage']").prop('disabled', false);
        }
    },
    
    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
            $(document).ready(_enableHolidayUsage(false));
            
            $(document).on("yukon:constraint:delete", function () {
                yukon.ui.blockPage();
                $('#delete-constraint-form').submit();
            });
            
            $(document).on('change', '#js-holiday-schedule', function () {
                _enableHolidayUsage(true);
            });
            
            _initialized = true;
        }
    };
    
    return mod;
})();

$(function () { yukon.dr.setup.constraint.init(); });