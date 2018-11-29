yukon.namespace('yukon.widget.monitors');
 
/** 
 * Module for Monitor widget.
 * @module yukon.widget.monitors
 * @requires JQUERY
 * @requires yukon
 */
yukon.widget.monitors = (function () {
 
    'use strict';
 
    var
    _initialized = false,

    toggleMonitor = function (menuClicked) {
        var trigger = menuClicked.closest('.dropdown-menu').data('trigger'),
            id = trigger.data('id'),
            type = trigger.data('name');

        $.post(yukon.url('/amr/' + type + '/' + id + '/toggle'), function (data) {
            if (data.errorMsg) {
                var errors = $('#js-error-msg');
                errors.html(data.errorMsg);
                errors.removeClass('dn');
            } else {
                $(".js-enable-" + type + "-" + id).toggleClass("dn", data.isEnabled);
                $(".js-disable-" + type + "-" + id).toggleClass("dn", !data.isEnabled);

                var monitorRow = $(".js-monitor-"+ type + "-" + id);
                _greyOutColumn(monitorRow, ".js-violations-count", data.isEnabled);
                _greyOutColumn(monitorRow, ".js-monitoring-count", data.isEnabled);
                _greyOutColumn(monitorRow, ".js-threshold", data.isEnabled);
            }
        });
    },
    
    _greyOutColumn = function (monitorRow, columnClass, isEnabled) {
        var column = monitorRow.find(columnClass);
        if (column.exists()) {
            column.toggleClass("very-disabled-look", !isEnabled);
        }
    },

    mod = {

        /** Initialize this module. */
        init: function () {

            if (_initialized) return;

            $(".js-toggle-status").click(function(e) {
                toggleMonitor($(this));
            });

            _initialized = true;
        }
 
    };
 
    return mod;
})();
 
$(function () { yukon.widget.monitors.init(); });