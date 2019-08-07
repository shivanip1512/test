yukon.namespace('yukon.dr.setup.programGear');

/**
 * Module that handles the behavior on the setup program Gear page.
 * @module yukon.dr.setup.programGear
 * @requires JQUERY
 * @requires yukon
 */
yukon.dr.setup.programGear = (function() {
    
    'use strict';
    
    var
    _initialized = false,
    _whenToChange = function() {
        var whenToChange = $("#whenToChange").val();
        $('#js-changePriority-row').toggleClass('dn', whenToChange != 'Priority');
        $('#js-changeDurationInMinutes-row').toggleClass('dn', whenToChange != 'Duration');
        $('#js-triggerNumber-row').toggleClass('dn', whenToChange != 'TriggerOffset');
        $('#js-triggerOffset-row').toggleClass('dn', whenToChange != 'TriggerOffset');
    }
    
    mod = {
        /** Initialize this module. */
        init: function () {
            if (_initialized) return;
             
            
            $(document).on('click', '#js-cancel-btn', function (event) {
                window.history.back();
            });

            $(document).on('change', '#whenToChange', function (event) {
                _whenToChange();
            });

            _initialized = true;
        }
    };
    
    return mod;
})();

$(function () { yukon.dr.setup.programGear.init(); });