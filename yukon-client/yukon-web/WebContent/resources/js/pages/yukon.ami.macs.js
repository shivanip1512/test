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
    
    _updateTemplateFields = function () {
        var retryTypes = yukon.fromJson('#retry-types'),
        ied300Types = yukon.fromJson('#ied-300-types'),
        ied400Types = yukon.fromJson('#ied-400-types'),
        templateType =  $('.js-template').val(),
        isRetry = retryTypes.indexOf(templateType) !== -1,
        isIed300 = ied300Types.indexOf(templateType) !== -1,
        isIed400 = ied400Types.indexOf(templateType) !== -1,
        demandResetToggle = $('.js-demand-reset')
        demandResetRow = demandResetToggle.closest('tr'),
        demandResetSelected = demandResetRow.find('.switch-btn-checkbox').prop('checked');
        if (isRetry) {
            $('.js-retry-section').addClass('dn');
        } else {
            $('.js-retry-section').removeClass('dn');
        }
        if (isIed300 || isIed400) {
            $('.js-ied-section').removeClass('dn');
            if (demandResetSelected) {
                if (isIed400) {
                    $('.js-ied-400').removeClass('dn');
                    $('.js-ied-300').addClass('dn');
                } else {
                    $('.js-ied-400').addClass('dn');
                    $('.js-ied-300').removeClass('dn');
                }
            } else {
                $('.js-ied-300').addClass('dn');
                $('.js-ied-400').addClass('dn');
            }
        } else {
            $('.js-ied-section').addClass('dn');
        }
    };
    
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
                
                $(document).on('change', '.js-template', function (ev) {
                    _updateTemplateFields();
                });
                
                $(document).on('change', '.js-demand-reset', function (ev) {
                    _updateTemplateFields();
                });
                
                $(document).on('change', '.js-type', function (ev) {
                    var type = $(this).val();
                    if (type == 'SCRIPT') {
                        $('.js-template').removeClass('dn');
                        $('.js-script-tab').removeClass('dn');
                        $('.js-commands-tab').addClass('dn');
                    } else {
                        $('.js-template').addClass('dn');
                        $('.js-script-tab').addClass('dn');
                        $('.js-commands-tab').removeClass('dn');
                    }
                });
                
                $(document).on('click', '.js-get-template', function (ev) {
                    var form = $('#macs-schedule'),
                        type = $('.js-type').val();
                    
                    $.ajax({
                        url: yukon.url('/macsscheduler/schedules/getTemplate'),
                        data: form.serialize()
                    }).done(function (data) {
                        if (type == 'SCRIPT') {
                            $('#script-content').html(data);
                            yukon.ui.initContent('#script-content');
                            $('.js-script-tab').removeClass('js-get-template');
                        } else {
                            $('#command-content').html(data);
                            $('.js-command-tab').removeClass('js-get-template');
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
