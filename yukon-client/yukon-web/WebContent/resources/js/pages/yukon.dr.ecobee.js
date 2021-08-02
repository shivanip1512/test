yukon.namespace('yukon.dr.ecobee');

/**
 * Module that serves the DR home page ecobee pane and the ecobee details page
 * @module   yukon.dr.ecobee
 * @requires JQUERY
 * @requires JQUERY UI
 * @requires yukon
 * @requires yukon.date.time.picker
 */
yukon.dr.ecobee = (function () {

    mod = null;

    mod = {
        /** 
         * Initialize ecobee module: time slider and initialize value for time label.
         */
        init: function () {

            var _originalErrorCheckTime = $('#checkErrorsTime').val(),
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
            
            if (0 === $('.js-fixable-issue').length) {
                $('#fix-all-btn').closest('.action-area').hide();
            }

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

            $(document).on('click', '#ecobee-error-checking-toggle .button-group-toggle .button', function () {
                var checkErrorsOn = $('#ecobee-error-checking-toggle .button-group-toggle .button.yes').hasClass('on');
                if (checkErrorsOn) {
                    $('#ecobee-check-errors').val('true');
                    $('#ecobee-error-check-schedule').show('fade');
                } else {
                    $('#ecobee-check-errors').val('false');
                    $('#ecobee-error-check-schedule').hide('fade');
                }
            });

            if ('true' === $('#ecobee-check-errors').val()) {
                $('#ecobee-error-checking-toggle .button-group-toggle .yes').addClass('on');
                $('#ecobee-error-checking-toggle .button-group-toggle .no').removeClass('on');
                $('#ecobee-error-check-schedule').show();
            } else {
                $('#ecobee-error-checking-toggle .button-group-toggle .no').addClass('on');
                $('#ecobee-error-checking-toggle .button-group-toggle .yes').removeClass('on');
                $('#ecobee-error-check-schedule').hide();
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
                $('#checkErrorsTime').val(_originalErrorCheckTime);
                yukon.ui.timeSlider.init();
            });
            
        },

    };
    return mod;
})();

$(function () { yukon.dr.ecobee.init(); });
