/**
 * Singleton that serves the DR home page ecobee pane and the ecobee details page
 * 
 * @requires jQuery 1.8.3+
 * @requires jQuery UI 1.9.2+
 */
yukon.namespace('yukon.dr.ecobee');
yukon.dr.ecobee = (function () {
    
    var 
    _timeFormatter = yukon.timeFormatter,
    
    /** 
     * Setup a slider.
     * @param {String} containingDivSelector. div containing the .js-time-slider and .js-time-label
     * @param {String} inputSelector. - the actual hidden input that holds the current value of this slider
     */
    _setupSlider = function (containingDivSelector, inputSelector) {
        
        var value = $(inputSelector).val();
        
        $(containingDivSelector +' .js-time-slider').slider({
            max: 24 * 60 - 15,
            min: 0,
            value: value,
            step: 15,
            slide: function (ev, ui) {
                $(containingDivSelector + ' .js-time-label').text(_timeFormatter.formatTime(ui.value, 0));
                $(inputSelector).val(ui.value);
            },
            change: function (ev, ui) {
                $(containingDivSelector + ' .js-time-label').text(_timeFormatter.formatTime(ui.value, 0));
                $(inputSelector).val(ui.value);
            }
        });
        
        $(containingDivSelector + ' .js-time-label').text(_timeFormatter.formatTime(value, 0));
    },
    mod = null;

    mod = {
        /** 
         * Initialize ecobee module: time slider and initialize value for time label.
         */
        init: function () {

            var _originalErrorCheckTime = $('#ecobee-error-check-time').val(),
                _originalCollectionTime = $('#ecobee-data-collection-time').val(),
                appendEmptyList = function (listCont) {
                    var emptyMessage = $('#sync-issues').data('emptyKey');
                    listCont.append('<span class="empty-list" id="no-sync-issues"></span>');
                    $('#no-sync-issues').text(emptyMessage);
                },
                insertErrorMsg = function (errorOpts) {
                    var issueTd = errorOpts.row.find('td:first-child'),
                        previousErrorMsg = issueTd.find('.error'),
                        issueHtml;
                    if (0 !== previousErrorMsg.length) {
                        previousErrorMsg.remove();
                    }
                    issueHtml = issueTd.html();
                    issueTd.empty();
                    issueTd.text(issueHtml);
                    issueTd.append('<div class="error"></div>');
                    issueTd.find('.error').text(errorOpts.errorMsg);
                };

            _setupSlider('#ecobee-data-collection-schedule', '#ecobee-data-collection-time');
            _setupSlider('#ecobee-error-check-schedule', '#ecobee-error-check-time');
            
            if (0 === $('.js-fixable-issue').length) {
                $('#fix-all-btn').closest('.action-area').hide();
            }

            $(document).on('yukon_dr_ecobee_download_settings_load', function (ev) {
                // initialize the date/time pickers
                yukon.ui.initDateTimePickers().ancestorInit('#ecobee-download-popup');
                
                // initialize the load group picker
                loadGroupPicker.show.call(loadGroupPicker, true);
            });
            
            $(document).on('yukon_dr_ecobee_download_start', function (ev) {
                
                loadGroupPicker.endAction.call(loadGroupPicker, loadGroupPicker.selectedItems);
                
                $('#ecobee-download-popup form').ajaxSubmit({
                    url: 'ecobee/download/start', 
                    type: 'post',
                    success: function(data, status, xhr, $form) {
                        var table = $('#downloads-table');
                        data = $(data);
                        
                        $('#ecobee-download-popup').dialog('close');
                        if (table.find('tbody tr').length === 0) {
                            table.show();
                            $('#data-downloads .empty-list').hide();
                        }
                        table.find('tbody').prepend(data);
                        setTimeout(function() { // do this in a timeout, otherwise it doesn't work :(
                            $('#downloads-table').find('tbody tr:first-child').css({'opacity': '1'});
                        }, 1);
                        if (table.find('tbody tr').length > 5) {
                            table.find('tbody tr:last-child').remove();
                        }
                    },
                    error: function(xhr, status, error, $form) {
                        $('#ecobee-download-popup').html(xhr.responseText);
                        // initialize the date/time pickers
                        yukon.ui.initDateTimePickers().ancestorInit('#ecobee-download-popup');
                        
                        // initialize the load group picker
                        loadGroupPicker.show.call(loadGroupPicker, true);
                    }
                });
            });

            // OK pressed for fix issue
            $(document).on('yukon_dr_ecobee_fix_start', function (ev) {
                var row = $(ev.target),
                    errorId = row.find('#ecobee-error-id').val(),
                    reportId = row.find('#ecobee-report-id').val(),
                    clickedButton = $('[data-error-id="' + errorId + '"]').find('.button');

                yukon.ui.busy(clickedButton);
                $('#ecobee-fixable').ajaxSubmit({
                    url: 'ecobee/fixIssue',
                    type: 'post',
                    data: { reportId: reportId, errorId: errorId },
                    success: function (data, status, xhr, $form) {
                        var row = $('[data-error-id="' + errorId + '"]'),
                            syncIssuesTitle = $('#sync-issues .title-bar .title'),
                            syncIssuesStr = syncIssuesTitle.text(),
                            numberSyncIssues = parseInt(syncIssuesStr.match(/\d+/)[0], 10),
                            syncIssuesCont = $('#sync-issues .content'),
                            numberFixableIssues;

                        $('#ecobee-fixable').dialog('close');
                        numberFixableIssues = $('.js-fixable-issue').length;
                        $(clickedButton).fadeTo('slow', 0, function () {
                            $(row).remove();
                        });
                        if (0 < numberSyncIssues) {
                            numberSyncIssues -= 1;
                        }
                        syncIssuesStr = syncIssuesStr.replace(/\d+/, numberSyncIssues);
                        syncIssuesTitle.text(syncIssuesStr);
                        if (0 === numberSyncIssues || (1 >= numberFixableIssues)) {
                            $('#fix-all-btn').fadeTo('slow', 0, function () {
                                if (0 === $('.js-unfixable-issue').length) {
                                    syncIssuesCont.empty();
                                    appendEmptyList(syncIssuesCont);
                                }
                            });
                        }
                    },
                    error: function (xhr, status, error, $form) {
                        var row = $('[data-error-id="' + errorId + '"]'),
                            errorMsg = xhr.responseJSON.message;
                        $('#ecobee-fixable').dialog('close');
                        insertErrorMsg({ row: row, errorMsg: errorMsg });
                        yukon.ui.unbusy(clickedButton);
                    }
                });
            });

            $(document).on('click', '.js-fixable-issue, .js-unfixable-issue', function (ev) {
                var btn = $(this),
                    isFixable = btn.is('.js-fixable-issue'),
                    reportId = $('#sync-issues').data('reportId'),
                    errorId = btn.closest('tr').data('errorId'),
                    issueExplanation = btn.closest('tr').data('explanation'),
                    popup = isFixable ? $('#ecobee-fixable') : $('#ecobee-unfixable');
                
                if (isFixable) {
                    $('#ecobee-report-id').val(reportId);
                    $('#ecobee-error-id').val(errorId);
                    $('#ecobee-issue-explanation').html(issueExplanation);
                    
                    popup.dialog({width: 400, buttons: yukon.ui.buttons({event: 'yukon_dr_ecobee_fix_start'})});
                    
                } else {
                    $('#ecobee-unfixable-explanation').html(issueExplanation);
                    popup.dialog({width: 400});
                }
            });

            $(document).on('click', '#ecobee-error-checking-toggle .toggle-btns .button', function () {
                var checkErrorsOn = $('#ecobee-error-checking-toggle .toggle-btns .button.yes').hasClass('on');
                if (checkErrorsOn) {
                    $('#ecobee-check-errors').val('true');
                    $('#ecobee-error-check-schedule').show('fade');
                } else {
                    $('#ecobee-check-errors').val('false');
                    $('#ecobee-error-check-schedule').hide('fade');
                }
            });

            $(document).on('click', '#ecobee-data-collection-toggle .toggle-btns .button', function() {
                var dataCollectionOn = $('#ecobee-data-collection-toggle .toggle-btns .button.yes').hasClass('on');
                if (dataCollectionOn) {
                    $('#ecobee-data-collection').val('true');
                    $('#ecobee-data-collection-schedule').show('fade');
                } else {
                    $('#ecobee-data-collection').val('false');
                    $('#ecobee-data-collection-schedule').hide('fade');
                    
                }
            });

            if ('true' === $('#ecobee-check-errors').val()) {
                $('#ecobee-error-checking-toggle .toggle-btns .yes').addClass('on');
                $('#ecobee-error-checking-toggle .toggle-btns .no').removeClass('on');
                $('#ecobee-error-check-schedule').show();
            } else {
                $('#ecobee-error-checking-toggle .toggle-btns .no').addClass('on');
                $('#ecobee-error-checking-toggle .toggle-btns .yes').removeClass('on');
                $('#ecobee-error-check-schedule').hide();
            }

            if ('true' === $('#ecobee-data-collection').val()) {
                $('#ecobee-data-collection-toggle .toggle-btns .yes').addClass('on');
                $('#ecobee-data-collection-toggle .toggle-btns .no').removeClass('on');
                $('#ecobee-data-collection-schedule').show();
            } else {
                $('#ecobee-data-collection-toggle .toggle-btns .no').addClass('on');
                $('#ecobee-data-collection-toggle .toggle-btns .yes').removeClass('on');
                $('#ecobee-data-collection-schedule').hide();
            }
            
            // fix all button pressed
            $(document).on('click', '#fix-all-btn', function (ev) {
                var ecobeeReportId = $('#sync-issues').data('reportId'),
                    numFixables = $('#sync-issues table tr .js-fixable-issue').length,
                    numUnfixables = $('#sync-issues table tr .js-unfixable-issue').length,
                    syncIssuesTitle,
                    fixAllButton = $(ev.target).closest('button');
                
                // busy all fixable buttons
                $('#sync-issues table tr .js-fixable-issue').each(function (index, elem) {
                    yukon.ui.busy(elem);
                });

                // busy fix all button
                yukon.ui.busy(fixAllButton);

                $.get(yukon.url('/dr/ecobee/fix-all'), {reportId: ecobeeReportId})
                .done(function (data, textStatus, jqXHR) {
                    var errorIds = [],
                        fixFailures = [],
                        i;
                    for (i = 0; i < data.length; i += 1) {
                        if (true === data[i].success) {
                            errorIds.push(data[i]);
                            numFixables -= 1;
                        } else {
                            fixFailures.push(data[i]);
                        }
                    }

                    // check the error id of each issue in table against the ids of the successfullly fixed issues reported
                    // by the server and process accordingly
                    $('#sync-issues table tr').each(function (index, row) {
                        var errorId,
                            // TODO: I know, not the most efficient algorithm
                            isInList = function (list, val, foundIndex) {
                                var ind,
                                    found = false;
                                foundIndex.ind = -1;
                                for (ind = 0; ind < list.length; ind += 1) {
                                    if (val === list[ind].originalErrorId) {
                                        found = true;
                                        foundIndex.ind = ind;
                                        break;
                                    }
                                }
                                return found;
                            },
                            foundAtIndex = {ind: -1},
                            fixFailure = '',
                            FADE_TIMEOUT = 1000,
                            totalIssues;
                        errorId = + $(row).find('button').closest('tr').data('errorId');
                        if (isInList(errorIds, errorId, foundAtIndex)) {
                            yukon.ui.busy($(row).find('button'));
                            $(row).css('opacity', 0);
                            setTimeout (function () {
                                $(row).remove();
                            }, FADE_TIMEOUT);
                        } else if (isInList(fixFailures, errorId, foundAtIndex)) {
                            if (-1 !== foundAtIndex.ind) {
                                fixFailure = fixFailures[foundAtIndex.ind].fixErrorString;
                                insertErrorMsg({row: $(row), errorMsg: fixFailure});
                            }
                            yukon.ui.unbusy($(row).find('button'));
                        }
                        // fix up possibly changed number of sync issues
                        totalIssues = (numFixables + numUnfixables).toString(10);
                        syncIssuesTitle = $('#sync-issues .title-bar .title');
                        syncIssuesTitle.text(syncIssuesTitle.html().replace(/\d+/, totalIssues));
                    });
                    // if no more fixable issues exist hide fix all button
                    if (0 >= numFixables) {
                        $('#fix-all-btn').fadeTo('slow', 0);
                        if (0 === fixFailures.length && 0 === numUnfixables) {
                            $('[data-error-id]').fadeTo('slow', 0, function () {
                                $('#sync-issues .content').empty();
                                appendEmptyList($('#sync-issues .content'));
                            });
                        }
                    }
                    yukon.ui.unbusy(fixAllButton);
                })
                .fail(function(jqXHR, textStatus, errorThrown) {
                    yukon.ui.unbusy(fixAllButton);
                });
            });
            
            $(document).on('yukon_dr_ecobee_config_load', function () {
                // each time the configure button popup is loaded, reset the field values of the times
                // and reinit the sliders so they accurately reflect the current settings in the database
                $('#ecobee-error-check-time').val(_originalErrorCheckTime);
                $('#ecobee-data-collection-time').val(_originalCollectionTime);
                _setupSlider('#ecobee-data-collection-schedule', '#ecobee-data-collection-time');
                _setupSlider('#ecobee-error-check-schedule', '#ecobee-error-check-time');
            });
        },
        /**
         * Callback fired by load group id picker as its endAction (download.jsp).
         * Adds chosen loadgroup ids from the picker as hidden inputs to the download form
         * @callback
         * @param {Object} loadGroups - load groups selected in picker
         */
        assignInputs: function (loadGroups) {
            var pickerThis = loadGroupPicker,
                loadGroupDiv = document.getElementById('loadGroup'),
                ssInputId = 'picker_' + pickerThis.pickerId + '_ss',
                ssInputElem = document.getElementById(ssInputId);
            $.each(loadGroups, function (key, selectedItem) {
                var inputElement = document.createElement('input');
                inputElement.type = 'hidden';
                inputElement.value = selectedItem[pickerThis.idFieldName];
                inputElement.name = pickerThis.destinationFieldName;
                loadGroupDiv.appendChild(inputElement);
            });
            // Inline pickers create a hidden input named 'ss'.
            // We don't want to send that up to the server here.
            if ('undefined' !== typeof ssInputElem && ssInputElem) {
                ssInputElem.parentNode.removeChild(ssInputElem);
            }
            return true;
        },
        
        /**
         * Updater callback fired for each download.
         * @callback
         * @param {string} status - JSON object string with the following properties:
         * @param {boolean} complete - true if data retrieval from ecobee is finished, false otherwise.
         * @param {string}  percentDone - percentage in the form '###.#%'.
         * @param {boolean} successful - true if the data retrieval is complete without errors, false otherwise.
         */
        downloadStatus: function (status) {
            
            status = JSON.parse(status.value);
            
            var row = $('[data-key="' + status.key + '"]'),
                bar = row.find('.progress-bar'),
                btn = row.find('.js-ec-download'),
                wasDisabled = btn.prop('disabled');
            
            bar.css({width: status.percentDone});
            if (status.complete) {
                
                bar.removeClass('progress-bar-info').parent().removeClass('active progress-striped');
                
                if (status.successful) {
                    
                    row.find('.js-download-ready').show();
                    bar.addClass('progress-bar-success');
                    btn.enable();
                    
                    if (wasDisabled) {
                        btn.addClass('animated flash');
                        setTimeout(function () { btn.removeClass('flash animated'); }, 2000);
                    }
                } else {
                    row.find('.js-download-failed').show();
                    bar.addClass('progress-bar-danger');
                    btn.disable();
                }
            }
            
            row.find('.js-percent-done').html(status.percentDone);
        } 
    };
    return mod;
})();

$(function () { yukon.dr.ecobee.init(); });
