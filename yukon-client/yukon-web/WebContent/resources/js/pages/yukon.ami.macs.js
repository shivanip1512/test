/**
 * Updates the MAC Schedules list. 
 * 
 * @module yukon.ami.macs
 * @requires JQUERY
 * @requires yukon
 */

yukon.namespace('yukon.ami.macs');

yukon.ami.macs = (function () {
	
	/** Refreshes the list of scheduled scripts after every 5 seconds. */
    var _autoUpdatePageContent = function () {
        var tableContainer = $('#scripts-container'),
            reloadUrl = tableContainer.attr('data-url'),
            params = {};
            yukon.ui.getSortingPagingParameters(params);
            reloadUrl = reloadUrl + "?" + $.param(params);
        tableContainer.load(reloadUrl, function () {
            setTimeout(_autoUpdatePageContent, 5000);
        });

    },
    
    _initialized = false,

        mod = {
    
            init: function () {
                
                if (_initialized) {
                    return;
                }
                
                var tableContainer = $('#scripts-container');
                if (tableContainer.length === 1) {
                    _autoUpdatePageContent();
                }

                $(document).on('click', '.js-script-toggle', function (ev) {
                    var selection = $(this);
                    var scheduleId = selection.data('scheduleId');
                    $.ajax({
                        url: yukon.url('/macsscheduler/schedules/' + scheduleId + '/toggleState'),
                        method: 'post',
                        dataType: 'json'
                    }).done(function (data) {
                        if (data.errorMsg) {
                            var errors = $('#errorMsg');
                            errors.html(data.errorMsg);
                        } else {
                            var params = {};
                            yukon.ui.getSortingPagingParameters(params);
                            window.location.href = yukon.url('/macsscheduler/schedules/view?' + $.param(params));
                        }
                    });
                });
                
                $(document).on('yukon:schedule:start', function (ev) {
                    var dialog = $(ev.target),
                        form = dialog.find('#startform');
                        scheduleId = form.find('#id').val(),
                        data = form.serialize();
                    $.ajax({
                        url: yukon.url('/macsscheduler/schedules/' + scheduleId + '/start'),
                        dataType: 'json',
                        method: 'post',
                        data: data
                    }).done(function (data) {
                        if (data.errorMsg) {
                            var errors = $('#errorMsg');
                            errors.html(data.errorMsg);
                        } else {
                            var params = {};
                            yukon.ui.getSortingPagingParameters(params);
                            window.location.href = yukon.url('/macsscheduler/schedules/view?' + $.param(params));
                        }
                    });
                });
                
                $(document).on('yukon:schedule:cancel', function (ev) {
                    var dialog = $(ev.target),
                        form = dialog.find('#stopform');
                        scheduleId = form.find('#id').val(),
                        data = form.serialize();
                    $.ajax({
                        url: yukon.url('/macsscheduler/schedules/' + scheduleId + '/stop'),
                        dataType: 'json',
                        method: 'post',
                        data: data
                    }).done(function (data) {
                        if (data.errorMsg) {
                            var errors = $('#errorMsg');
                            errors.html(data.errorMsg);
                        } else {
                            var params = {};
                            yukon.ui.getSortingPagingParameters(params);
                            window.location.href = yukon.url('/macsscheduler/schedules/view?' + $.param(params));
                        }
                    });
                });
                
                $(document).on('click', '.js-script-text', function (ev) {
                    var form = $('#macs-schedule');
                    
                    var data = $.ajax({
                        url: yukon.url('/macsscheduler/schedules/createScript'),
                        data: form.serialize()
                    }).done(function (data, textStatus, jqXHR) {
                        if (data.script) {
                            var script = $('#script');
                            script.html(data.script);
                        }
                        else if (data.errorMsg) {
                            var errors = $('#script-error');
                            errors.html(data.errorMsg);
                        }
                    });
                });
                
                _initialized = true;

            }
    };
    return mod;
}());

$(function () {
    yukon.ami.macs.init();
});
