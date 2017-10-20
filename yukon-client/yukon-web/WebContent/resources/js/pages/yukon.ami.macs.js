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
            reloadUrl = tableContainer.data('url'),
            paramCharacter = reloadUrl.indexOf('?') > 0 ? '&' : '?',
            params = {};
            yukon.ui.getSortingPagingParameters(params);
            reloadUrl = reloadUrl + paramCharacter + $.param(params);
        tableContainer.load(reloadUrl, function () {
            setTimeout(_autoUpdatePageContent, 5000);
        });

    },
    
    _updateTemplateFields = function () {
        var retryTypes = yukon.fromJson('#retry-types'),
        ied300Types = yukon.fromJson('#ied-300-types'),
        ied400Types = yukon.fromJson('#ied-400-types'),
        templateType =  $('.js-template-value').val(),
        hasTemplate = templateType != 'NO_TEMPLATE',
        isRetry = retryTypes.indexOf(templateType) !== -1,
        isIed300 = ied300Types.indexOf(templateType) !== -1,
        isIed400 = ied400Types.indexOf(templateType) !== -1;
        $('#script-tabs').toggleClass('dn', !hasTemplate);
        $('.js-script-description').toggleClass('dn', !hasTemplate);
        $('.js-script-save-generate').toggleClass('dn', !hasTemplate);
        $('.js-script-generate').toggleClass('dn', !hasTemplate);
        $('.js-save-button').toggleClass('dn', hasTemplate);
        if (hasTemplate) {
            $('.js-retry-section').toggleClass('dn', isRetry);
            $('.js-ied-section').toggleClass('dn', !isIed300 && !isIed400);
        } else {
            $('#savedScriptText').val("");
        }
        $('#savedScriptText').toggleClass('dn', hasTemplate);
        yukon.ui.initContent('#script-content');
    },
    
    _updateStartPolicyFields = function () {
        var policy = $('.js-start-policy').val();
        $('.js-start-dateTime').toggleClass('dn', policy != 'DATETIME');
        $('.js-start-dayOfMonth').toggleClass('dn', policy != 'DAYOFMONTH');
        $('.js-start-weekDay').toggleClass('dn', policy != 'WEEKDAY');
        $('.js-start-time').toggleClass('dn', policy != 'DAYOFMONTH' && policy != 'WEEKDAY');
    },
    
    _updateStopPolicyFields = function () {
        var policy = $('.js-stop-policy').val();
        $('.js-stop-duration').toggleClass('dn', policy != 'DURATION');
        $('.js-stop-time').toggleClass('dn', policy != 'ABSOLUTETIME');
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
                            var errors = $('#js-error-msg');
                            errors.html(data.errorMsg);
                            errors.removeClass('dn');
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
                    var form = $('#macs-schedule'),
                        buttons = yukon.ui.buttons({ okText: yg.text.save, event: 'yukon:schedule:saveScript', okClass: 'js-save-script' });
                    
                    form.ajaxSubmit({
                        url: yukon.url('/macsscheduler/schedules/createScript'),
                        success: function (result, status, xhr, $form) {
                            var dialog = $('#text-editor'),
                                popupTitle = dialog.data('title');

                            dialog.html(xhr.responseText);
                            dialog.dialog({
                                title: popupTitle,
                                width: '800px',
                                modal: true,
                                buttons: buttons
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
                
                $(document).on('click', '.js-script-save-generate', function (ev) {
                    var form = $('#macs-schedule');
                    $('#generateScript').val("true");
                    form.submit();
                });
                
                $(document).on('click', '.js-view-script', function (ev) {
                    var dialog = $('#text-editor'),
                        scriptText = $('#savedScriptText').val(),
                        scriptArea = $('#script'),
                        popupTitle = dialog.data('title');

                        scriptArea.text(scriptText);
                        scriptArea.get(0).setSelectionRange(0,0);
                        dialog.dialog({
                            title: popupTitle,
                            width: '800px',
                            modal: true,
                            buttons: yukon.ui.buttons({ })
                        });
                        yukon.ui.unbusy('.js-view-script');
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
                
                $(document).on('change', '.js-type', function (ev) {
                    var type = $(this).val(),
                        templateType =  $('.js-template-value').val(),
                        isScript = type == 'SCRIPT',
                        hasTemplate = templateType != 'NO_TEMPLATE';
                    $('.js-template').toggleClass('dn', !isScript);
                    $('.js-script-tab').toggleClass('dn', !isScript);
                    $('.js-commands-tab').toggleClass('dn', isScript);
                    $('.js-script-description').toggleClass('dn', !isScript || (isScript && !hasTemplate));
                    $('.js-script-save-generate').toggleClass('dn', !isScript || (isScript && !hasTemplate));
                    $('.js-script-generate').toggleClass('dn', !isScript || (isScript && !hasTemplate));
                    $('.js-save-button').toggleClass('dn', isScript && hasTemplate);
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
                        $('.js-script-save-generate').prop('disabled', false);
                        $('.js-script-generate').prop('disabled', false);
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
                    if (newCategory == null || newCategory.trim() == ''){
                        $('.js-category-blank').removeClass('dn');
                    } else {
                        $('.js-category-blank').addClass('dn');
                        $('.js-category-select').append('<option selected=selected value=' + newCategory + '>' + newCategory + '</option>');
                        $('#category-popup').dialog('close');
                    }
                });
                
                $(document).on('click', '.js-filter', function (ev) {
                    var tableContainer = $('#scripts-container'),
                        form = $('#filter-form');
                    form.ajaxSubmit({
                        success: function(data, status, xhr, $form) {
                            tableContainer.html(data);
                            tableContainer.data('url', yukon.url('/macsscheduler/schedules/innerView?' + form.serialize()));
                        }
                    });
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
