yukon.namespace('yukon.admin.weekdays');

/**
 * Module that manages the weekdays in Global Settings.
 * @module yukon.admin.weekdays
 * @requires JQUERY
 * @requires JQUERY UI
 * @requires yukon
 * @requires yukon.ui
 */
String.prototype.replaceAt = function (index, replacement) {
    return this.substr(0, index) + replacement+ this.substr(index + replacement.length);
},

yukon.admin.weekdays = (function () {
    'use strict';
     var
        _initialized = false,
        mod = {
                   updateDays : function (id, name) {
                       var index=0;
                       switch (id) {
                           case 'SUNDAY':
                               index = 0;
                               break;
                           case 'MONDAY':
                               index = 1;
                               break;
                           case 'TUESDAY':
                               index = 2;
                               break;
                           case 'WEDNESDAY':
                               index = 3;
                               break;
                           case 'THURSDAY':
                               index = 4;
                               break;
                           case 'FRIDAY':
                               index = 5;
                               break;
                           case 'SATURDAY':
                               index = 6;
                               break;
                           default:
                                  index=0;
                    }
                    var currentDays = $('#'+name).val();
                    var toggle = currentDays.charAt(index);
                        toggle = (toggle == "Y") ? "N" : "Y";
                        currentDays= currentDays.replaceAt(index, toggle);
                    $('#'+name).val(currentDays);
                },

                 /**
                 * Initializes the module, hooking up event handlers to components.
                 * Depends on localized text in the jsp, so only run after DOM is ready.
                 */
                init: function () {
                    if (_initialized) return;
                    _initialized = true;
                }
            };
return mod;
}());

$(function () { yukon.admin.weekdays.init(); });