yukon.namespace('yukon.adminSetup.yukonLoggers');

/** .
 * Handles behavior on the Admin Setup yukon Loggers page
 * @module yukon.adminSetup.yukonLoggerz
 * @requires JQUERY
 * @requires yukon
 */
yukon.adminSetup.yukonLoggers = (function () {

    'use strict';
    
    var _initialized = false,
    
    _toggleEditViewLogger = function (loggerId) {
        var editSpan = $('.js-edit-logger-' + loggerId),
            viewSpan = $('.js-view-logger-' + loggerId),
            nameField = editSpan.find('[name="loggerName"]');
        //start out with all in view mode again
        $('[class*="js-edit-logger-"]').addClass('dn');
        $('[class*="js-view-logger-"]').removeClass('dn');
        //toggle the logger that was selected
        editSpan.toggleClass('dn', !editMode);
        viewSpan.toggleClass('dn', editMode);
               //set name to saved name
        nameField.val(editSpan.find('[name="savedName"]').val());
        //remove any errors
        nameField.removeClass('error');
    },
    
    _refreshLoggersTable = function (successMessage, errorMessage) {
        var tableContainer = $('#logger-container'),
            form = $('#filter-form');
        form.ajaxSubmit({
            success: function(data) {
                tableContainer.html(data);
                tableContainer.data('url', yukon.url('/admin/config/loggers/filter?' + form.serialize()));
                if (successMessage) {
                    $('.js-success-msg').append(yukon.escapeXml(successMessage)).removeClass('dn');
                }
                if (errorMessage) {
                    $('.js-error-msg').append(yukon.escapeXml(errorMessage)).removeClass('dn');
                }
            },
            error: function (xhr, status, error, $form) {
                tableContainer.html(xhr.responseText);
            },
        });  
    },
    
    mod = {
        /** Initialize this module. */
        init: function () {

            if (_initialized) return;
            
            $('.js-loggers-table').scrollTableBody();
            $('.js-selected-levels').chosen({'width': '350px'});

            $(document).on('click', '.js-filter-loggers', function() {
                _refreshLoggersTable();
            });

            $(document).on('yukon:logger:load', function (ev) {
                var popup = $(ev.target);
                yukon.ui.initDateTimePickers();
                popup.find('.js-levels');
                if (popup.find('.user-message').is(':visible')) {
                    $('.ui-dialog-buttonset').find('.js-primary-action').prop('disabled', true);
                }
            });

            $(document).on('click', '.js-edit-logger', function () {
                var loggerId = $(this).data('loggerId'),
                    url = yukon.url('/admin/config/loggers?id=' + loggerId),
                    popup = $('.js-edit-logger-popup'),
                    popupTitle = popup.data('title'),
                    dialogDivJson = {
                        "data-url" : url,
                        "data-dialog": '',
                        "data-load-event" : "yukon:logger:load",
                        "data-event" : "yukon:logger:save",
                        "data-title" : popupTitle,
                        "data-ok-text" : yg.text.save,
                        "data-destroy-dialog-on-close" : "",
                    };
                yukon.ui.dialog($("<div/>").attr(dialogDivJson));
            });

            $(document).on("yukon:logger:save", function (event) {
                var popup = $(event.target),
                    loggerName = popup.find('#loggerId option:selected').text();
                popup.find('#loggerName').val(yukon.escapeXml(loggerName));
                popup.find('#logger-form').ajaxSubmit({
                    success: function (data) {
                        popup.dialog('close');
                        //refresh logger table
                        _refreshLoggersTable(data.successMessage, data.errorMessage);
                    },
                    error: function (xhr) {
                        popup.html(xhr.responseText);
                        popup.find('.js-levels').chosen({'width': '350px'});
                    }
                });
            });

            _initialized = true;
        }

    };
    
    return mod;
})();
 
$(function () {yukon.adminSetup.yukonLoggers.init(); });