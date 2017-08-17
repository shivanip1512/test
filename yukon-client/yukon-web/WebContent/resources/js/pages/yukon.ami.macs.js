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
        templateType =  $('.js-template-value').val(),
        isRetry = retryTypes.indexOf(templateType) !== -1,
        isIed300 = ied300Types.indexOf(templateType) !== -1,
        isIed400 = ied400Types.indexOf(templateType) !== -1;
        if (templateType == 'NO_TEMPLATE') {
            $('.js-meter-read-tab').addClass('dn');
            $('.js-options-tab').addClass('dn');
        } else {
            $('.js-meter-read-tab').removeClass('dn');
            $('.js-options-tab').removeClass('dn');
            if (isRetry) {
                $('.js-retry-section').addClass('dn');
            } else {
                $('.js-retry-section').removeClass('dn');
            }
            if (isIed300 || isIed400) {
                $('.js-ied-section').removeClass('dn');
            } else {
                $('.js-ied-section').addClass('dn');
            }
        }
    },
    
    _updateStartPolicyFields = function () {
        var policy = $('.js-start-policy').val();
        if (policy == 'DATETIME') {
            $('.js-start-dateTime').removeClass('dn');
        } else {
            $('.js-start-dateTime').addClass('dn');
        }
        if (policy == 'DAYOFMONTH') {
            $('.js-start-dayOfMonth').removeClass('dn');
        } else {
            $('.js-start-dayOfMonth').addClass('dn');
        }
        if (policy == 'WEEKDAY') {
            $('.js-start-weekDay').removeClass('dn');
        } else {
            $('.js-start-weekDay').addClass('dn');
        }
        if (policy == 'DAYOFMONTH' || policy == 'WEEKDAY') {
            $('.js-start-time').removeClass('dn');
        } else {
            $('.js-start-time').addClass('dn');
        }
        
    },
    
    _updateStopPolicyFields = function () {
        var policy = $('.js-stop-policy').val();
        if (policy == 'DURATION') {
            $('.js-stop-duration').removeClass('dn');
        } else {
            $('.js-stop-duration').addClass('dn');
        }
        if (policy == 'ABSOLUTETIME') {
            $('.js-stop-time').removeClass('dn');
        } else {
            $('.js-stop-time').addClass('dn');
        }
    },
    
    _initialized = false,

        mod = {
    
            init: function () {
                
                if (_initialized) {
                    return;
                }
                
                _updateStartPolicyFields();
                _updateStopPolicyFields();

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
                
                $(document).on('click', '.js-script-generate', function (ev) {
                    var form = $('#macs-schedule');                    
                    form.ajaxSubmit({
                        url: yukon.url('/macsscheduler/schedules/createScript'),
                        type: 'GET',
                        success: function (result, status, xhr, $form) {
                            $('#text-editor').html(xhr.responseText);
                            var popupTitle = $('#text-editor').data('title');
                            $('#text-editor').dialog({
                                title: popupTitle,
                                width: '800px',
                                modal: true,
                                buttons: yukon.ui.buttons({ okText: yg.text.save, event: 'yukon:schedule:saveScript', okClass: 'js-save-script' })
                            });
                            yukon.ui.unbusy('.js-script-generate');
                        },
                        error: function (xhr, status, error, $form) {
                            $('.yukon-page').html(xhr.responseText);
                            yukon.ui.initContent('#macs-schedule');
                            yukon.ui.highlightErrorTabs();
                        }
                    });

                });
                
                $(document).on('yukon:schedule:saveScript', function (ev) {
                    yukon.ui.busy('.js-save-script');
                    var updatedScriptText = $('#script').val();
                    $('#savedScriptText').val(updatedScriptText);
                    $('#macs-schedule').submit();
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
                            $('.js-script-tab').removeClass('js-get-template');
                            yukon.ui.initContent('#script-content');
                            _updateTemplateFields();
                        } else {
                            $('#command-content').html(data);
                            $('.js-command-tab').removeClass('js-get-template');
                        }
                        $('.js-save-button').prop('disabled', false);
                    });
                    
                });
                
                $(document).on('change', '.js-start-policy', function (ev) {
                    _updateStartPolicyFields();
                });
                
                $(document).on('change', '.js-stop-policy', function (ev) {
                    _updateStopPolicyFields();
                });
                
                /** User clicked OK on New Category dialog. */
                $(document).on('yukon:ami:macs:category:add', function (ev) {
                    var newCategory = $('#categoryNameText').val();
                    $('.js-category-select').append('<option selected=selected value=' + newCategory + '>' + newCategory + '</option>');
                    $('#category-popup').dialog('close');
                });
                
                _initialized = true;
                
                yukon.ui.highlightErrorTabs();

            }
    };
    return mod;
}());

$(function () {
    yukon.ami.macs.init();
});
