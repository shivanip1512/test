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
     * @param {String} containingDivSelector. div containing the .f-time-slider and .f-time-label
     * @param {String} inputSelector. - the actual hidden input that holds the current value of this slider
     */
    _setupSlider = function (containingDivSelector, inputSelector) {
        
        var value = $(inputSelector).val();
        
        $(containingDivSelector +' .f-time-slider').slider({
            max: 24 * 60 - 15,
            min: 0,
            value: value,
            step: 15,
            slide: function (ev, ui) {
                $(containingDivSelector + ' .f-time-label').text(_timeFormatter.formatTime(ui.value, 0));
                $(inputSelector).val(ui.value);
            },
            change: function (ev, ui) {
                $(containingDivSelector + ' .f-time-label').text(_timeFormatter.formatTime(ui.value, 0));
                $(inputSelector).val(ui.value);
            }
        });
        
        $(containingDivSelector + ' .f-time-label').text(_timeFormatter.formatTime(value, 0));
    },
    
    mod = null;

    mod = {
        /** 
         * Initialize ecobee module: time slider and initialize value for time label.
         */
        init: function () {
            _setupSlider('#ecobee-download-schedule', '#ecobee-download-time');
            _setupSlider('#ecobee-data-collection-schedule', '#ecobee-data-collection-time');
            _setupSlider('#ecobee-error-check-schedule', '#ecobee-error-check-time');

            $('#ecobee-download-schedule .f-time-label').html(_timeFormatter.formatTime($('#ecobee-download-time').val(), 0));
            
            $(document).on('yukon.dr.ecobee.download.settings.load', function (ev) {
                // initialize the date/time pickers
                yukon.ui.initDateTimePickers().ancestorInit('#ecobee-download-popup');
                
                // initialize the load group picker
                loadGroupPicker.show.call(loadGroupPicker, true);
            });
            
            $(document).on('yukon.dr.ecobee.download.start', function (ev) {
                
                loadGroupPicker.endAction.call(loadGroupPicker, loadGroupPicker.selectedItems);
                
                $('#ecobee-download-popup form').ajaxSubmit({
                    url: 'ecobee/download/start', 
                    type: 'post',
                    success: function(data, status, xhr, $form) {
                        var table = $('#downloads-table');
                        data = $(data);
                        
                        $('#ecobee-download-popup').dialog('close');
                        if (table.find('tbody tr').length == 0) {
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

            $(document).on('yukon.dr.ecobee.fix.init', function (ev, originalTarget) {
                mod.initFixIssue(ev, originalTarget);
            });

            $(document).on('click', '#ecobee-error-checking-toggle .toggle-on-off .button', function () {
                var checkErrorsOn = $('#ecobee-error-checking-toggle .toggle-on-off .button.yes').hasClass('on');
                if (checkErrorsOn) {
                    $('#ecobee-check-errors').val('true');
                    $('#ecobee-error-check-schedule').show('fade');
                } else {
                    $('#ecobee-check-errors').val('false');
                    $('#ecobee-error-check-schedule').hide('fade');
                }
            });

            $(document).on('click', '#ecobee-data-collection-toggle .toggle-on-off .button', function() {
                var dataCollectionOn = $('#ecobee-data-collection-toggle .toggle-on-off .button.yes').hasClass('on');
                if (dataCollectionOn) {
                    $('#ecobee-data-collection').val('true');
                    $('#ecobee-data-collection-schedule').show('fade');
                } else {
                    $('#ecobee-data-collection').val('false');
                    $('#ecobee-data-collection-schedule').hide('fade');
                    
                }
            });

            if ('true' === $('#ecobee-check-errors').val()) {
                $('#ecobee-error-checking-toggle .toggle-on-off .yes').addClass('on');
                $('#ecobee-error-checking-toggle .toggle-on-off .no').removeClass('on');
                $('#ecobee-error-check-schedule').show();
            } else {
                $('#ecobee-error-checking-toggle .toggle-on-off .no').addClass('on');
                $('#ecobee-error-checking-toggle .toggle-on-off .yes').removeClass('on');
                $('#ecobee-error-check-schedule').hide();
            }

            if ('true' === $('#ecobee-data-collection').val()) {
                $('#ecobee-data-collection-toggle .toggle-on-off .yes').addClass('on');
                $('#ecobee-data-collection-toggle .toggle-on-off .no').removeClass('on');
                $('#ecobee-data-collection-schedule').show();
            } else {
                $('#ecobee-data-collection-toggle .toggle-on-off .no').addClass('on');
                $('#ecobee-data-collection-toggle .toggle-on-off .yes').removeClass('on');
                $('#ecobee-data-collection-schedule').hide();
            }
            
            $(document).on('click', '#fix-all-btn', function (ev) {
                var reportId = $('#sync-issues').data('reportId');
                
                // disable all buttons
                $('#sync-issues table tr').each(function (index,elem) {
                    if ('#ecobee-unfixable' !== $(elem).find('button').attr('popup')) {
                        yukon.ui.busy($(elem).find('button'));
                        $(elem).find('button').prop('disabled', true);
                    }
                });

                $.ajax({
                    url: yukon.url('dr/ecobee/fix-all'),
                    type: 'get',
                    data: {reportId: reportId}
                })
                .done(function (data, textStatus, jqXHR) {
                    var errorIds = [],
                        i;
                    // make note of issues successfully fixed by server
                    for (i = 0; i < data.length; i += 1) {
                        if (true === data[i].success || 'true' === data[i].success) {
                            errorIds.push(data[i].originalErrorId);
                        }
                    }
                    
                    // check the error id of each issue in table against the ids
                    // of the successfullly fixed issues reported by the server
                    $('#sync-issues table tr').each(function (index,elem) {
                        var errorId,
                            // TODO: I know, not the most efficient algorithm
                            isInList = function (val) {
                                var ind,
                                    found = false;
                                for (ind = 0; ind < errorIds.length; ind += 1) {
                                    if (val === errorIds[ind]) {
                                        found = true;
                                        errorIds.slice(ind, ind + 1);
                                        break;
                                    }
                                }
                                return found;
                            };
                        errorId = $(elem).find('button').data('errorId');
                        if (isInList(errorId)) {
                            $(elem).remove();
                        }
                    });
                })
                .fail(function(jqXHR, textStatus, errorThrown) {
                    debug.log('fixAllIssues: fail: textStatus: ' + textStatus + ' errorThrown:' + errorThrown);
                });
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
        },
        /**
         * Initialize contents in popups for fixing ecobee issues before displaying them.
         * This callback is fired when a data-load-event attribute is specified in popup but
         * no data-url specified.
         * @callback
         * @param {module:yukon.dr.ecobee#event:yukon.dr.ecobee.fix.init} ev - A yukon.dr.ecobee.fix.init event.
         * @param {Object} originalTarget - in this case, the button that was clicked that has data attributes on it.
         */
        initFixIssue: function (ev, originalTarget) {
            var popupId = $(ev.target).attr('id'), 
                reportId = $('#sync-issues').data('reportId'),
                errorId = $(originalTarget).closest('button').data('errorId'),
                issueExplanation = $(originalTarget).closest('button').data('explanation');
            switch (popupId) {
            case 'ecobee-fix':
                $('#ecobee-report-id').val(reportId);
                $('#ecobee-error-id').val(errorId);
                $('#ecobee-issue-explanation').html(issueExplanation);
                break;
            case 'ecobee-unfixable':
                $('#ecobee-unfixable-explanation').html(issueExplanation);
                break;
            case 'ecobee-fixall':
                $('#ecobee-fixall-report-id').val(reportId);
                $('#ecobee-fixall-explanation').html(issueExplanation);
                break;
            default:
                break;
            }
        } 
    };
    return mod;
})();

$(function () { yukon.dr.ecobee.init(); });
